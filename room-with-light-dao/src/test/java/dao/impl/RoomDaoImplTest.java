package dao.impl;

import dao.RoomDao;
import db.ConnectionManager;
import entities.Room;
import org.junit.Assert;
import org.junit.Test;
import utils.SingletonBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoomDaoImplTest extends Assert {
    private RoomDao dao = SingletonBuilder.getInstanceImpl(RoomDao.class);

    @Test
    public void crud()  throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);
        Room saved;
        Room getIt;
        Room updated;

        Room newOneForSave = new Room();
        newOneForSave.setName("Room #1");
        newOneForSave.setLightState(true);

        Room newOneForUpdate = new Room();
        newOneForUpdate.setName("Room #1 update");
        newOneForUpdate.setLightState(false);

        try {
            // check null save & update
            Room nullSave = dao.save(null);
            assertNull(nullSave);
            dao.update(nullSave);
            assertNull(nullSave);

            // check save and get
            saved = dao.save(newOneForSave);
            getIt = dao.get(saved.getId());

            assertNotNull(saved);
            assertNotNull(getIt);
            assertEquals(saved, getIt);

            // check update
            newOneForUpdate.setId(saved.getId());
            dao.update(newOneForUpdate);
            updated = dao.get(newOneForUpdate.getId());

            assertNotEquals(saved, updated);
            assertNotNull(updated);
            assertEquals(newOneForUpdate,updated);
            assertEquals(saved.getId(),updated.getId());

            // check delete
            int delNumber = dao.delete(getIt.getId());
            assertEquals(delNumber, 1);
            getIt = dao.get(saved.getId());
            assertNull(getIt);

        } finally {
            con.rollback();
        }
    }

    @Test
    public void getAll() throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        Room newOneForSave = new Room();
        newOneForSave.setName("Room #1");
        newOneForSave.setLightState(true);

        try {
            List<Room> list1 = dao.getAll();

            dao.save(newOneForSave);
            List<Room> list2 = dao.getAll();

            assertEquals(list2.size() - list1.size() , 1);

            for (Room element : list2) {
                dao.delete(element.getId());
            }
            List<Room> listAfterDeleteAll = dao.getAll();
            assertEquals(listAfterDeleteAll.size(),0);
        } finally {
            con.rollback();
        }
    }

    @Test
    public void getAllForName() throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        Room newOneForSave = new Room();
        newOneForSave.setName("Room #1");
        newOneForSave.setLightState(true);

        final String nameFilter = "#1";

        try {
            List<Room> list1 = dao.getAllForName(nameFilter);

            // save 2 entities that pass name filter & one entity that don't pass filter
            dao.save(newOneForSave);            // pass filter
            newOneForSave.setName(" Room #1 ABC");
            newOneForSave.setLightState(false);
            dao.save(newOneForSave);            // pass filter
            newOneForSave.setName(" Room ABC");
            newOneForSave.setLightState(true);
            dao.save(newOneForSave);            // not pass filter
            List<Room> list2 = dao.getAllForName(nameFilter);

            assertEquals(list2.size() - list1.size() , 2);

            for (Room element : dao.getAll()) {
                dao.delete(element.getId());
            }
            List<Room> listAfterDeleteAll = dao.getAll();
            assertEquals(listAfterDeleteAll.size(),0);
        } finally {
            con.rollback();
        }
    }

    @Test
    public void getAllByName() throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        Room newOneForSave = new Room();
        newOneForSave.setName("Room #1");
        newOneForSave.setLightState(true);

        String nameForSearch = "Room #1";

        try {
            List<Room> list1 = dao.getAllByName(nameForSearch);

            dao.save(newOneForSave);
            List<Room> list2 = dao.getAllByName(nameForSearch);

            assertEquals(list2.size() - list1.size() , 1);

            list2 = dao.getAll();
            for (Room element : list2) {
                dao.delete(element.getId());
            }
            List<Room> listAfterDeleteAll = dao.getAll();
            assertEquals(listAfterDeleteAll.size(),0);
        } finally {
            con.rollback();
        }
    }

}
