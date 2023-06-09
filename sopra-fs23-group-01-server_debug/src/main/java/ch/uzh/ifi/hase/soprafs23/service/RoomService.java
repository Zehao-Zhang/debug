package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.constant.ReadyStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class RoomService {

    private final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final UserService userService;

    public RoomService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("roomRepository") RoomRepository roomRepository, ChatService chatService, UserService userService) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.userService = userService;
    }

    public List<Room> getRooms() {return this.roomRepository.findAll();}

    //Here we create a new room and we need to set the room property and theme according to the input from client
    public Room createRoom(Room newRoom) {
        //newRoom.setToken(UUID.randomUUID().toString());
        newRoom.setRoomOwnerId(newRoom.getRoomOwnerId());
        newRoom.setRoomProperty(newRoom.getRoomProperty());
        newRoom.setTheme(newRoom.getTheme());
        newRoom.addRoomPlayerList(newRoom.getRoomOwnerId());
        //newRoom.addRoomPlayer(userRepository.findById(newRoom.getRoomOwnerId()));
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newRoom = roomRepository.save(newRoom);
        roomRepository.flush();
        log.debug("Created Information for Room: {}", newRoom);
        return newRoom;
    }

    public Room findRoomById(Long id) {
        Optional<Room> roomById = roomRepository.findById(id);
        if (roomById.isPresent()) {
            return roomById.get();
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room with this ID:"+id+" not found!");
        }
    }

    public void enterRoom(Room room, User user){
        room.addRoomPlayerList(user.getId());
    }

    public void collectVote(Room room, long voterId, long voteeId) {
        Map<Long, Long> votingResult = room.getVotingResult();
        votingResult.put(voterId, voteeId);
        room.setVotingResult(votingResult);
    }
    public boolean checkIfAllVoted(Room room) {
        return room.getVotingResult().size() == room.getAlivePlayersList().size();
    }

    public boolean checkIfAllReady(Room room) {
        int numOfReady = 0;
         for (long id:room.getRoomPlayersList()){
             if (userRepository.findById(id).get().getReadyStatus()== ReadyStatus.READY){
                 numOfReady++;
             }
        }
         if (numOfReady == room.getRoomPlayersList().size()) {
             return true;}
         else {return false;}
    }

    public void startGame(Room room){
        room.setCurrentPlayerIndex(0);
        room.setGameStage(GameStage.DESCRIPTION);
        assignCardsAndRoles(room);
    }

    public void assignCardsAndRoles(Room room) {
        int num = room.getRoomPlayersList().size();
        Random random = new Random();
        int randomNumber = random.nextInt(num); // 生成的随机数为0-5

        // assign role and card to each player
        for (int i = 0; i < num; i++) {
            User player = userRepository.getOne(room.getRoomPlayersList().get(i));
            if (i == randomNumber) {
                player.setRole(false);
                player.setCard("pear");
            } else {
                player.setRole(true);
                player.setCard("apple");
            }
        }

    }


    public void checkIfSomeoneOut(Room room){
        Map<Long, Long> votingResult = room.getVotingResult();
        if (votingResult != null) {
            Map<Long, Integer> voteCounts = new HashMap<>();
            for (Map.Entry<Long, Long> entry : votingResult.entrySet()) {
                Long voterId = entry.getKey();
                Long voteeId = entry.getValue();

                Integer voteCount = voteCounts.get(voteeId);
                if (voteCount == null) {
                    voteCount = 1;
                }
                else {
                    voteCount += 1;
                }
                voteCounts.put(voteeId, voteCount);
            }

            Long mostVotedPlayer = null;
            int maxVotes = -1;

            for (Map.Entry<Long, Integer> entry : voteCounts.entrySet()) {
                Long playerId = entry.getKey();
                int voteCount = entry.getValue();
                if (voteCount > maxVotes) {
                    maxVotes = voteCount;
                    mostVotedPlayer = playerId;
                }
            }

            if (mostVotedPlayer != null) {
                User userToBeOuted = userService.getUserById(mostVotedPlayer);
                userToBeOuted.setAliveStatus(false);
                room.getAlivePlayersList().remove(mostVotedPlayer);
            }else {
                //systemReminder
                chatService.systemReminder("No players out!");
            }
        }
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */

//    void checkIfUserExists(User userToBeCreated) {
//        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
//
//        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
//        if (userByUsername != null && userToBeCreated.getId()!= userByUsername.getId()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT,
//                    String.format(baseErrorMessage, "username", "is"));
//        }
//    }
//
//    public User loginUser(User user) {
//        user = checkIfPasswordWrong(user);
//        user.setStatus(UserStatus.ONLINE);
//        user.setToken(UUID.randomUUID().toString());
//
//        return user;
//    }
//
//    User checkIfPasswordWrong(User userToBeLoggedIn) {
//
//        User userByUsername = userRepository.findByUsername(userToBeLoggedIn.getUsername());
//
//        if (userByUsername == null) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username not exist!");
//        }
//        else if (!userByUsername.getPassword().equals(userToBeLoggedIn.getPassword())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password incorrect!");
//        }
//        else {
//            return userByUsername;
//        }
//    }
//
//    //Define the logout function to set the status to OFFLINE when log out
//    public User logoutUser(User userToBeLoggedOut) {
//        User userByUsername = userRepository.getOne(userToBeLoggedOut.getId());
//        userByUsername.setStatus(UserStatus.OFFLINE);
//        return userByUsername;
//    }
//
//    public User userProfileById(Long id) {
//        Optional<User> userByUserid = userRepository.findById(id);
//        if (userByUserid.isPresent()) {
//            return userByUserid.get();
//        }
//        else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this ID:"+id+" not found!");
//        }
//    }
//
//    public void userEditProfile(User user) {
//        if(!userRepository.existsById(user.getId())) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user ID was not found");
//        }
//        User userByUserid = userRepository.getOne(user.getId());
//
//        if(user.getUsername()!=null){
//            checkIfUserExists(user);
//            userByUserid.setUsername(user.getUsername());
//        };
//        // set the birthday
//        if(user.getBirthday()!=null){
//            userByUserid.setBirthday(user.getBirthday());
//        };
//        // set the gender
//        if(user.getGender()!=null){
//            userByUserid.setGender(user.getGender());
//        };
//        // set the email
//        if(user.getEmail()!=null){
//            userByUserid.setEmail(user.getEmail());
//        };
//        // set the intro
//        if(user.getIntro()!=null){
//            userByUserid.setIntro(user.getIntro());
//        };
//        if(user.getAvatarUrl()!=null){
//            userByUserid.setAvatarUrl(user.getAvatarUrl());
//        };
//
//
//        // saves the given entity but data is only persisted in the database once
//        // flush() is called
//        userRepository.flush();
//        // return userByUserid;
//    }
}
