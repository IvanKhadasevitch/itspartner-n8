package services.impl;

import dao.RoomDao;
import entities.Room;
import org.apache.log4j.Logger;
import services.RoomService;
import services.ServiceException;
import utils.SingletonBuilder;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

public class RoomServiceImpl extends AbstractService implements RoomService {
    private static Logger log = Logger.getLogger(RoomServiceImpl.class);

    private RoomDao roomDao = SingletonBuilder.getInstanceImpl(RoomDao.class);

    private RoomServiceImpl() {}

    /**
     * returns an Room record with an id = roomId from the database
     *
     * @param roomId determine id of Room record in database
     * @return Room record from the database with id = roomId, or
     * null if such an entity was not found
     */
    @Override
    public Room get(Serializable roomId) {
        try {
            return roomDao.get(roomId);
        } catch (SQLException e) {
            String errorMessage = "Error getting Room by id: " + roomId;
            log.error(errorMessage + e.getMessage());
            throw new ServiceException(errorMessage);
        }
    }

    /**
     * Saves the Room in the database if @param room != null
     *
     * @param room determine entity with type <Room>
     * @return saved entity with not null id or
     *         null if room = null or room.name is already in DB
     */
    @Override
    public Room add(Room room) {
        if (room == null) {
            return null;
        }
        try {
            this.startTransaction();
            if (isNameAlreadyInUse(room.getName())) {
                this.stopTransaction();

                return null;
            }

            Room roomSave = roomDao.save(room);
            this.commit();
            this.stopTransaction();

            return roomSave;
        } catch (SQLException e) {
            rollback();
            String errorMessage = "Error saving Room: " + room;
            log.error(errorMessage + e.getMessage());
            throw new ServiceException(errorMessage);
        }
    }

    /**
     * update an Room entity with an id = room.id in the database
     * if @param room != null
     *
     * @param room determine a new entity to be updated
     *             in the database with id = room.id
     * @return updated entity with not null id or
     *         null if room = null or room.name is already in DB
     */
    @Override
    public Room update(Room room) {
        if (room == null) {
            return null;
        }
        try {
            this.startTransaction();
            List<Room> roomsWithNotUniqueName = roomDao.getAllByName(room.getName());
            boolean isUniqueRoomName = (roomsWithNotUniqueName.size() == 1 &&
                    room.getId().equals(roomsWithNotUniqueName.get(0).getId())) ||
                        roomsWithNotUniqueName.isEmpty();
            if (isUniqueRoomName) {
                // can update room record
                roomDao.update(room);
                this.commit();
                this.stopTransaction();

                return room;
            } else {
                // room name already in use - can't update room record
                this.stopTransaction();

                return null;
            }
        } catch (SQLException e) {
            rollback();
            String errorMessage = "Error updating Room: " + room;
            log.error(errorMessage + e.getMessage());
            throw new ServiceException(errorMessage);
        }
    }

    /**
     * update an Room entity with an id = room.id in the database
     * if @param room != null. Set in DB only entity.lightState as room.lightState
     *
     * @param room room determine a new entity to be updated
     *             in the database with id = room.id
     * @return updated entity with not null id or
     *         null if room = null or room was deleted already
     */
    @Override
    public Room updateLightState(Room room) {
        if (room == null) {
            return null;
        }
        try {
            this.startTransaction();
            Room roomFromDB = roomDao.get(room.getId());


            if (roomFromDB != null) {
                // can update room record
                roomFromDB.setLightState(room.getLightState());     // set required lightState
                roomDao.update(roomFromDB);
                this.commit();
                this.stopTransaction();

                return roomFromDB;
            } else {
                // room already deleted from DB - can't update room record
                this.stopTransaction();

                return null;
            }
        } catch (SQLException e) {
            rollback();
            String errorMessage = "Error updating Room: " + room;
            log.error(errorMessage + e.getMessage());
            throw new ServiceException(errorMessage);
        }
    }

    /**
     * removes from the database an Room entity with an id = roomId
     *
     * @param roomId determine id of entity in database
     * @return returns the number of deleted rows from the database
     */
    @Override
    public int delete(Serializable roomId) {
        try {
            this.startTransaction();
            int deletedRecords = roomDao.delete(roomId);
            this.commit();
            this.stopTransaction();

            return deletedRecords;
        } catch (SQLException e) {
            rollback();
            String errorMessage = "Error deleting from DB Room with id: " + roomId;
            log.error(errorMessage + e.getMessage());
            throw new ServiceException(errorMessage);
        }
    }

    /**
     * get all records of Room from DB
     *
     * @return a list of all records of Room from the database
     * or empty list if there are no entries
     */
    @Override
    public List<Room> getAll() {
        try {
            return roomDao.getAll();
        } catch (SQLException e) {
            String errorMessage = "Error getting all Rooms from database.";
            log.error(errorMessage + e.getMessage());
            throw new ServiceException(errorMessage);
        }
    }

    /**
     * get all records of Room from DB if  @param nameFilter = null or empty string ("").
     * get all records of Room from DB where Room.name pass filter = nameFilter
     *
     * @param nameFilter determines string that mast contain Room.name
     * @return get all records of Room from DB if  @param nameFilter = null or empty string ("").
     * get all records of Room from DB where Room.name pass filter = nameFilter
     * or empty List<Room> if no one record was found
     */
    @Override
    public List<Room> getAll(String nameFilter) {
        if (nameFilter == null || nameFilter.isEmpty()) {
            return getAll();
        }

        try {
            return roomDao.getAllForName(nameFilter);
        } catch (SQLException e) {
            String errorMessage = "Error getting all Rooms where Room.name contains: " + nameFilter;
            log.error(errorMessage + e.getMessage());
            throw new ServiceException(errorMessage);
        }
    }

    /**
     * detect is or not any Room record in DB with Room.name.equalsIgnoreCase(roomName)
     *
     * @param roomName determinate name of Room for search in DB
     * @return true if is any Room record in DB with Room.name.equalsIgnoreCase(roomName) and
     *         false if no one such Room were found
     */
    @Override
    public boolean isNameAlreadyInUse(String roomName) {
        if (roomName == null || roomName.isEmpty()) {
            return true;
        }

        try {
            List<Room> allRoomsWithRoomName = roomDao.getAllByName(roomName);

            return ! allRoomsWithRoomName.isEmpty();
        } catch (SQLException e) {
            String errorMessage = "Error getting all Rooms where Room.name equals: " + roomName;
            log.error(errorMessage + e.getMessage());
            throw new ServiceException(errorMessage);
        }
    }
}
