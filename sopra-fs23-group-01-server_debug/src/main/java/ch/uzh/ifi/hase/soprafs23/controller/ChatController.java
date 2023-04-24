package ch.uzh.ifi.hase.soprafs23.controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ch.uzh.ifi.hase.soprafs23.model.Message;
import ch.uzh.ifi.hase.soprafs23.model.Status;
import ch.uzh.ifi.hase.soprafs23.service.ChatService;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {
    private final RoomService roomService;
    private final UserService userService;


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatService chatService;

    private Map<String, String> userWordMap = new ConcurrentHashMap<>();

    public ChatController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message) {
        // 如果是加入房间的消息，那么发送给他们分配的单词
        if (message.getStatus() == Status.JOIN) {
            userJoin(message.getSenderName());
            String assignedWord = userWordMap.get(message.getSenderName());
            String assignedRole = assignUserRole();
            Message wordMessage = new Message();
            wordMessage.setSenderName("system");
            wordMessage.setMessage(assignedWord);
            wordMessage.setStatus(Status.ASSIGNED_WORD); 
            wordMessage.setRole(assignedRole); // 修改状态为 ASSIGNED_WORD
            simpMessagingTemplate.convertAndSendToUser(message.getSenderName(), "/private", wordMessage);
        }
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
        System.out.println(message.toString());
        return message;
    }

    @MessageMapping(value = "/gamestart/{roomId}")
    public void startGame(@PathVariable Long roomId) {
        Room room = roomService.findRoomById(roomId);
        if (roomService.checkIfAllReady(room)) {
            broadcastGameStart();
            initiateGame(room);
            conductTurn(room);
        }else {
            systemReminder("Not enough players or not all players are ready yet!");
        }
    }
    public void initiateGame(Room room){
        room.setCurrentPlayerIndex(0);
        room.setGameStage(GameStage.DESCRIPTION);
        roomService.assignCardsAndRoles(room);
        List<Long> newPlayersList = new ArrayList<>(room.getRoomPlayersList());
        room.setAlivePlayersList(newPlayersList);
    }

    public void broadcastGameStart() {
        Message gameStartMessage = new Message();
        gameStartMessage.setSenderName("system");
        gameStartMessage.setMessage("Game has started!");
        gameStartMessage.setStatus(Status.START); // 设置状态为 GAME_STARTED
        simpMessagingTemplate.convertAndSend("/chatroom/public", gameStartMessage);
    }

    public void broadcastVoteStart() {
        Message voteStartMessage = new Message();
        voteStartMessage.setSenderName("system");
        voteStartMessage.setMessage("Now it's time to vote!\n You can click avatar to vote");
        voteStartMessage.setStatus(Status.VOTE); // 设置状态为 GAME_STARTED
        simpMessagingTemplate.convertAndSend("/chatroom/public", voteStartMessage);
    }

    public void systemReminder(String reminderInfo) {
        Message gameStartMessage = new Message();
        gameStartMessage.setSenderName("system");
        gameStartMessage.setMessage(reminderInfo);
        gameStartMessage.setStatus(Status.REMINDER); // 设置状态为 GAME_STARTED
        simpMessagingTemplate.convertAndSend("/chatroom/public", gameStartMessage);
    }

    public void conductTurn(Room room){
        AtomicInteger currentPlayerIndex = new AtomicInteger(room.getCurrentPlayerIndex());
        AtomicReference<GameStage> currentGameStage = new AtomicReference<>(room.getGameStage());
        AtomicInteger currentAlivePlayersNum = new AtomicInteger(room.getAlivePlayersList().size());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        if ( currentGameStage.equals(GameStage.DESCRIPTION)){
            while (currentPlayerIndex.get() < currentAlivePlayersNum.get()) {
                if (currentGameStage.equals(GameStage.DESCRIPTION)) {
                    User currentUser = userService.getUserById(room.getAlivePlayersList().get(currentPlayerIndex.get()));
                    systemReminder("Now it's Player" + currentUser.getUsername() + "'s turn to describe");
                    executor.schedule(() -> {
                        // 这里是15秒后要执行的代码
                        if (currentPlayerIndex.get() < currentAlivePlayersNum.get() - 1) {
                            currentPlayerIndex.incrementAndGet();
                        }
                        else {
                            currentPlayerIndex.set(0);
                            currentGameStage.set(GameStage.VOTING);
                        }
                    }, 15, TimeUnit.SECONDS);
                }
            }
        }else if (currentGameStage.equals(GameStage.VOTING)) {
            broadcastVoteStart();
            executor.schedule(() -> {
                // 这里是15秒后要执行的代码
                // 展示投票结果
                // room.getVotingResult();
                roomService.checkIfSomeoneOut(room);
            }, 15, TimeUnit.SECONDS);

            //and go to the next stage of game
            //not all voted so do nothing
        }


    }


    public void userJoin(String username) {
        if (!userWordMap.containsKey(username)) {
            // 从词汇表中随机选择一个单词
            String word = chatService.getRandomWord();
            userWordMap.put(username, word);
        }
    }

    public String assignUserRole() {
        List<String> roles = Arrays.asList("detective", "spy");
        return roles.get(new Random().nextInt(roles.size()));
    }
}
