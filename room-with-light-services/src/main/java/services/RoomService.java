package services;

import entities.Room;

import java.io.Serializable;
import java.util.List;

public interface RoomService {
    /**
     * returns an Room record with an id = roomId from the database
     *
     * @param roomId determine id of Room record in database
     * @return Room record from the database with id = roomId, or
     *         null if such an entity was not found
     */
    Room get(Serializable roomId);

    /**
     * Saves the Room in the database if @param room != null
     *
     * @param room determine entity with type <Room>
     * @return saved entity with not null id or
     *         null if room = null or room.name is already in DB
     */
    Room add(Room room);

    /**
     * update an Room entity with an id = room.id in the database
     * if @param room != null
     *
     * @param room determine a new entity to be updated
     *             in the database with id = room.id
     * @return updated entity with not null id or
     *         null if room = null or room.name is already in DB
     */
    Room update(Room room);

    /**
     * update an Room entity with an id = room.id in the database
     * if @param room != null. Set in DB only entity.lightState as room.lightState
     *
     * @param room room determine a new entity to be updated
     *             in the database with id = room.id
     * @return updated entity with not null id or
     *         null if room = null  or room was deleted already
     */
    Room updateLightState(Room room);

    /**
     * removes from the database an Room entity with an id = roomId
     *
     * @param roomId determine id of entity in database
     * @return returns the number of deleted rows from the database
     */
    int delete(Serializable roomId);

    /**
     * get all records of Room from DB
     *
     * @return a list of all records of Room from the database
     *         or empty list if there are no entries
     */
    List<Room> getAll();

    /**
     * get all records of Room from DB if  @param nameFilter = null or empty string ("").
     * get all records of Room from DB where Room.name pass filter = nameFilter
     *
     * @param nameFilter determines string that mast contain Room.name
     * @return  get all records of Room from DB if  @param nameFilter = null or empty string ("").
     *          get all records of Room from DB where Room.name pass filter = nameFilter
     *          or empty List<Room> if no one record was found
     */
    List<Room> getAll(String nameFilter);

    /**
     * detect is or not any Room record in DB with Room.name.equalsIgnoreCase(roomName)
     *
     * @param roomName determinate name of Room for search in DB
     * @return true if is any Room record in DB with Room.name.equalsIgnoreCase(roomName) and
     *         false if no one such Room were found
     */
    boolean isNameAlreadyInUse(String roomName);

}
