package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-24T17:43:43+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 17.0.6 (Eclipse Adoptium)"
)
public class DTOMapperImpl implements DTOMapper {

    @Override
    public User convertUserPostDTOtoEntity(UserPostDTO userPostDTO) {
        if ( userPostDTO == null ) {
            return null;
        }

        User user = new User();

        user.setBirthday( userPostDTO.getBirthday() );
        user.setPassword( userPostDTO.getPassword() );
        user.setId( userPostDTO.getId() );
        user.setUsername( userPostDTO.getUsername() );
        user.setStatus( userPostDTO.getStatus() );
        user.setRegisterDate( userPostDTO.getRegisterDate() );
        user.setToken( userPostDTO.getToken() );
        user.setName( userPostDTO.getName() );
        user.setReadyStatus( userPostDTO.getReadyStatus() );

        return user;
    }

    @Override
    public UserGetDTO convertEntityToUserGetDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserGetDTO userGetDTO = new UserGetDTO();

        userGetDTO.setBirthday( user.getBirthday() );
        userGetDTO.setRole( user.getRole() );
        userGetDTO.setGender( user.getGender() );
        userGetDTO.setAvatarUrl( user.getAvatarUrl() );
        userGetDTO.setRateUn( user.getRateUn() );
        userGetDTO.setRateDe( user.getRateDe() );
        userGetDTO.setIntro( user.getIntro() );
        userGetDTO.setId( user.getId() );
        userGetDTO.setEmail( user.getEmail() );
        userGetDTO.setCard( user.getCard() );
        userGetDTO.setUsername( user.getUsername() );
        userGetDTO.setStatus( user.getStatus() );
        userGetDTO.setRegisterDate( user.getRegisterDate() );
        userGetDTO.setReadyStatus( user.getReadyStatus() );

        return userGetDTO;
    }

    @Override
    public User convertUserPutDTOtoEntity(UserPutDTO userPutDTO) {
        if ( userPutDTO == null ) {
            return null;
        }

        User user = new User();

        user.setBirthday( userPutDTO.getBirthday() );
        user.setGender( userPutDTO.getGender() );
        user.setAvatarUrl( userPutDTO.getAvatarUrl() );
        user.setRateUn( userPutDTO.getRateUn() );
        user.setRateDe( userPutDTO.getRateDe() );
        user.setIntro( userPutDTO.getIntro() );
        if ( userPutDTO.getId() != null ) {
            user.setId( userPutDTO.getId() );
        }
        user.setEmail( userPutDTO.getEmail() );
        user.setUsername( userPutDTO.getUsername() );
        user.setToken( userPutDTO.getToken() );
        user.setRegisterDate( userPutDTO.getRegisterDate() );
        user.setReadyStatus( userPutDTO.getReadyStatus() );

        return user;
    }

    @Override
    public Room convertRoomPostDTOtoEntity(RoomPostDTO roomPostDTO) {
        if ( roomPostDTO == null ) {
            return null;
        }

        Room room = new Room();

        room.setRoomProperty( roomPostDTO.getRoomProperty() );
        room.setTheme( roomPostDTO.getTheme() );
        room.setRoomOwnerId( roomPostDTO.getRoomOwnerId() );
        room.setRoomId( roomPostDTO.getRoomId() );
        List<Long> list = roomPostDTO.getRoomPlayersList();
        if ( list != null ) {
            room.setRoomPlayersList( new ArrayList<Long>( list ) );
        }
        room.setMaxPlayersNum( roomPostDTO.getMaxPlayersNum() );

        return room;
    }

    @Override
    public RoomGetDTO convertEntityToRoomGetDTO(Room room) {
        if ( room == null ) {
            return null;
        }

        RoomGetDTO roomGetDTO = new RoomGetDTO();

        roomGetDTO.setRoomOwnerId( room.getRoomOwnerId() );
        roomGetDTO.setRoomProperty( room.getRoomProperty() );
        roomGetDTO.setTheme( room.getTheme() );
        List<Long> list = room.getRoomPlayersList();
        if ( list != null ) {
            roomGetDTO.setRoomPlayersList( new ArrayList<Long>( list ) );
        }
        roomGetDTO.setRoomId( room.getRoomId() );
        List<User> list1 = room.getRoomPlayers();
        if ( list1 != null ) {
            roomGetDTO.setRoomPlayers( new ArrayList<User>( list1 ) );
        }
        roomGetDTO.setMaxPlayersNum( room.getMaxPlayersNum() );

        return roomGetDTO;
    }

    @Override
    public Room convertRoomPutDTOtoEntity(RoomPutDTO roomPutDTO) {
        if ( roomPutDTO == null ) {
            return null;
        }

        Room room = new Room();

        room.setRoomProperty( roomPutDTO.getRoomProperty() );
        room.setTheme( roomPutDTO.getTheme() );
        room.setRoomOwnerId( roomPutDTO.getRoomOwnerId() );
        room.setRoomId( roomPutDTO.getRoomId() );
        if ( room.getRoomPlayers() != null ) {
            ArrayList<User> arrayList = roomPutDTO.getRoomPlayers();
            if ( arrayList != null ) {
                room.getRoomPlayers().addAll( arrayList );
            }
        }
        List<Long> list = roomPutDTO.getRoomPlayersList();
        if ( list != null ) {
            room.setRoomPlayersList( new ArrayList<Long>( list ) );
        }
        room.setMaxPlayersNum( roomPutDTO.getMaxPlayersNum() );

        return room;
    }
}
