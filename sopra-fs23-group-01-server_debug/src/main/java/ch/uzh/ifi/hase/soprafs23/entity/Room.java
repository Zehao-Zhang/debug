package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "ROOM")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_sequence")
    @SequenceGenerator(name = "room_sequence", sequenceName = "room_sequence", allocationSize = 1, initialValue = 1)
    private long roomId;

    @Column(nullable = true)
    private Theme theme;

    @Column(nullable = false)
    private long roomOwnerId;

    @Column(nullable = true)
    private int maxPlayersNum;

//    @Column(nullable = false, unique = true)
//    private String token;

    @Column(nullable = false)
    private RoomProperty roomProperty;

    @OneToMany(mappedBy = "room")
    private List<User> roomPlayers = new ArrayList<>();

    @Column
    @ElementCollection
    private List<Long> roomPlayersList= new ArrayList<>();

    @Column
    @ElementCollection
    private List<Long> alivePlayersList = new ArrayList<>();


    private int currentPlayerIndex = 0; // index inside

    private GameStage gameStage;
    private Long winnerId = null;
    private Long playToOuted = null;

    @ElementCollection
    private Map<Long, Long> votingResult = new HashMap<>();

    public Map<Long, Long> getVotingResult() {
        return votingResult;
    }

    public void setVotingResult(Map<Long, Long> votingResult) {
        this.votingResult = votingResult;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }


//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }

    public RoomProperty getRoomProperty() {
        return roomProperty;
    }

    public void setRoomProperty(RoomProperty roomProperty) {
        this.roomProperty = roomProperty;
    }

    public int getMaxPlayersNum() {
        return maxPlayersNum;
    }

    public List<User> getRoomPlayers() {
        return roomPlayers;
    }

    public void addRoomPlayer(Optional<User> user) {
        if (user.isPresent()) {
            User owner = user.get();
            this.roomPlayers.add(owner);
            //this.roomPlayersList.add(owner.getId());
        }
    }

    public void addRoomPlayerList(Long id) {
        if (id!=null) {
            this.roomPlayersList.add(id);
        }
    }

    public List<Long> getRoomPlayersList() {
        return roomPlayersList;
    }

    public void setRoomPlayersList(List<Long> roomPlayersList) {
        this.roomPlayersList = roomPlayersList;
    }

    public long getRoomOwnerId() {
        return roomOwnerId;
    }

    public void setRoomOwnerId(long roomOwnerId) {
        this.roomOwnerId = roomOwnerId;
    }
    public void setMaxPlayersNum(int maxPlayersNum) {
        this.maxPlayersNum = maxPlayersNum;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public GameStage getGameStage() {
        return gameStage;
    }

    public void setGameStage(GameStage gameStage) {
        this.gameStage = gameStage;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public Long getPlayToOuted() {
        return playToOuted;
    }

    public void setPlayToOuted(Long playToOuted) {
        this.playToOuted = playToOuted;
    }

    public List<Long> getAlivePlayersList() {
        return alivePlayersList;
    }

    public void setAlivePlayersList(List<Long> alivePlayersList) {
        this.alivePlayersList = alivePlayersList;
    }
}
