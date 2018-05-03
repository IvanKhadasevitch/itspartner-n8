package dao;

import entities.Room;

import java.sql.SQLException;
import java.util.List;

public interface RoomDao extends DAO<Room> {
    /**
     *
     * get all records of Room from DB
     *
     * @return a list of all records of Room from the database
     *         or empty list if there are no entries
     * @throws SQLException if there is an error connecting to the database
     */
    List<Room> getAll() throws SQLException;

    /**
     * get all records of Room from DB where Room.name pass nameFilter
     *
     * @param nameFilter
     * @returna list of all records of Room from the database
     *         where Room.Room pass nameFilter or
     *         empty list if there are no entries
     * @throws SQLException if there is an error connecting to the database
     */
    List<Room> getAllForName(String nameFilter) throws SQLException;

    /**
     * get all records of Room from DB where Room.name = roomName
     *
     * @param roomName
     * @return list of all records of Room from the database
     *         where Room.Room = roomName or
     *         empty list if there are no entries
     * @throws SQLException if there is an error connecting to the database
     */
    List<Room> getAllByName(String roomName) throws SQLException;
}
