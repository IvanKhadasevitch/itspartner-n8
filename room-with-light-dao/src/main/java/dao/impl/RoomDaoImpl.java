package dao.impl;

import dao.RoomDao;
import entities.Room;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoomDaoImpl extends AbstractDao implements RoomDao {

    private static Logger log = Logger.getLogger(RoomDaoImpl.class);

    private static final String SAVE_ROOM_SQL =
            "INSERT INTO Room (`name`, light_state) VALUES (?,?)";
    private static final String GET_ROOM_BY_ID_SQL = "SELECT * FROM Room WHERE id=?";
    private static final String GET_ROOM_BY_NAME_SQL = "SELECT * FROM Room WHERE `name`=?";
    private static final String GET_ALL_ROOMS_SQL = "SELECT * FROM Room";
    private static final String GET_ALL_ROOMS_WHERE_NAME_PASS_FILTER_SQL =
            "SELECT * FROM Room WHERE `name` LIKE CONCAT('%',?,'%')";
    private static final String UPDATE_ROOM_BY_ID_SQL =
            "UPDATE Room SET `name`=?, light_state=? WHERE id=?";
    private static final String DELETE_ROOM_BY_ID_SQL = "DELETE FROM Room WHERE id=?";

    private PreparedStatement psSave = null;
    private PreparedStatement psGet = null;
    private PreparedStatement psGetByName = null;
    private PreparedStatement psGetAll = null;
    private PreparedStatement psGetAllWhereNamePassFilter = null;
    private PreparedStatement psUpdate = null;
    private PreparedStatement psDelete = null;

    private RoomDaoImpl() {}

    /**
     * Saves the entity type <Room> in the database if @param t != null
     *
     * @param room determine entity with type <Room>
     * @return saved entity with not null id or
     * null if t = null
     * @throws SQLException if can't save entity
     */
    @Override
    public Room save(Room room) throws SQLException {
        if (room == null) {
            return null;
        }

        if (psSave == null) {
            psSave = psSave = prepareStatement(SAVE_ROOM_SQL, Statement.RETURN_GENERATED_KEYS);
        }
        psSave.setString(1, room.getName());
        psSave.setBoolean(2, room.getLightState());
        psSave.executeUpdate();

        try (ResultSet rs = psSave.getGeneratedKeys()) {
            if (rs.next()) {
                room.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            String errorMessage = "Can't execute SQL: " + psSave + e.getMessage();
            log.error(errorMessage);
            throw new SQLException(errorMessage);
        }
        return room;
    }

    /**
     * returns an entity with an id from the database
     *
     * @param id determine id of entity in database
     * @return entity with type <Room> from the database, or null if such an entity was not found
     * @throws SQLException if there is an error connecting to the database
     */
    @Override
    public Room get(Serializable id) throws SQLException {
        if (psGet == null) {
            psGet = prepareStatement(GET_ROOM_BY_ID_SQL);
        }
        psGet.setLong(1, (Long) id);
        psGet.executeQuery();

        try (ResultSet rs = psGet.getResultSet()) {
            if (rs.next()) {
                return populateEntity(rs);
            }
        } catch (SQLException e) {
            String errorMessage = "Can't execute SQL: " + psGet + e.getMessage();
            log.error(errorMessage);
            throw new SQLException(errorMessage);
        }
        return null;
    }

    /**
     * update an entity with an id = room.id in the database if @param room != null
     *
     * @param room determine a new entity to be updated in the database with id = room.id
     * @throws SQLException if there is an error updating entity in the database
     */
    @Override
    public void update(Room room) throws SQLException {
        if (room == null) {
            return;
        }
        try {
            if (psUpdate == null) {
                psUpdate = prepareStatement(UPDATE_ROOM_BY_ID_SQL);
            }
            psUpdate.setLong(3, room.getId());
            psUpdate.setString(1, room.getName());
            psUpdate.setBoolean(2, room.getLightState());
            psUpdate.executeUpdate();
        } catch (SQLException e) {
            String errorMessage = "Can't execute SQL: " + psUpdate + e.getMessage();
            log.error(errorMessage);
            throw new SQLException(errorMessage);
        }
    }

    /**
     * removes from the database an entity with type <Room> and id
     *
     * @param id determine id of entity in database
     * @return returns the number of deleted rows from the database
     * @throws SQLException if there is an error deleting entity from the database
     */
    @Override
    public int delete(Serializable id) throws SQLException {
        try {
            if (psDelete == null) {
                psDelete = prepareStatement(DELETE_ROOM_BY_ID_SQL);
            }
            psDelete.setLong(1, (Long) id);
            return psDelete.executeUpdate();
        } catch (SQLException e) {
            String errorMessage = "Can't execute SQL: " + psDelete + e.getMessage();
            log.error(errorMessage);
            throw new SQLException(errorMessage);
        }
    }

    /**
     * get all Rooms from DB
     *
     * @return a list of all records of Room from the database
     * or empty list if there are no entries
     * @throws SQLException if there is an error connecting to the database
     */
    @Override
    public List<Room> getAll() throws SQLException {
        List<Room> list = new ArrayList<>();
        try {
            if (psGetAll == null) {
                psGetAll = prepareStatement(GET_ALL_ROOMS_SQL);
            }
            psGetAll.execute();
            try (ResultSet rs = psGetAll.getResultSet()) {
                while (rs.next()) {
                    list.add(populateEntity(rs));
                }
            } catch (SQLException e) {
                String errorMessage = "Can't execute SQL: " + psGetAll + e.getMessage();
                log.error(errorMessage);
                throw new SQLException(errorMessage);
            }
        } catch (SQLException e) {
            String errorMessage = "Can't execute SQL: " + psGetAll + e.getMessage();
            log.error(errorMessage);
            throw new SQLException(errorMessage);
        }

        return list;
    }

    /**
     * get all records of Room from DB where Room.Room pass nameFilter
     *
     * @param nameFilter
     * @throws SQLException if there is an error connecting to the database
     * @returna list of all records of Room from the database
     * where Room.Room pass nameFilter or
     * empty list if there are no entries
     */
    @Override
    public List<Room> getAllForName(String nameFilter) throws SQLException {
        List<Room> list = new ArrayList<>();
        try {
            if (psGetAllWhereNamePassFilter == null) {
                psGetAllWhereNamePassFilter = prepareStatement(GET_ALL_ROOMS_WHERE_NAME_PASS_FILTER_SQL);
            }
            psGetAllWhereNamePassFilter.setString(1, nameFilter);
            psGetAllWhereNamePassFilter.execute();
            try (ResultSet rs = psGetAllWhereNamePassFilter.getResultSet()) {
                while (rs.next()) {
                    list.add(populateEntity(rs));
                }
            } catch (SQLException e) {
                String errorMessage = "Can't execute SQL: " + psGetAllWhereNamePassFilter +
                        e.getMessage();
                log.error(errorMessage);
                throw new SQLException(errorMessage);
            }
        } catch (SQLException e) {
            String errorMessage = "Can't execute SQL: " + psGetAllWhereNamePassFilter +
                    e.getMessage();
            log.error(errorMessage);
            throw new SQLException(errorMessage);
        }

        return list;
    }

    /**
     * get all records of Room from DB where Room.name = roomName
     *
     * @param roomName
     * @return list of all records of Room from the database
     * where Room.Room = roomName or
     * empty list if there are no entries
     * @throws SQLException if there is an error connecting to the database
     */
    @Override
    public List<Room> getAllByName(String roomName) throws SQLException {
        List<Room> list = new ArrayList<>();
        try {
            if (psGetByName == null) {
                psGetByName = prepareStatement(GET_ROOM_BY_NAME_SQL);
            }
            psGetByName.setString(1, roomName);
            psGetByName.execute();
            try (ResultSet rs = psGetByName.getResultSet()) {
                while (rs.next()) {
                    list.add(populateEntity(rs));
                }
            } catch (SQLException e) {
                String errorMessage = "Can't execute SQL: " + psGetByName + e.getMessage();
                log.error(errorMessage);
                throw new SQLException(errorMessage);
            }
        } catch (SQLException e) {
            String errorMessage = "Can't execute SQL: " + psGetByName + e.getMessage();
            log.error(errorMessage);
            throw new SQLException(errorMessage);
        }

        return list;
    }

    private Room populateEntity(ResultSet rs) throws SQLException {
        Room entity = new Room();
        entity.setId(rs.getLong(1));
        entity.setName(rs.getString(2));
        entity.setLightState(rs.getBoolean(3));

        return entity;
    }
}
