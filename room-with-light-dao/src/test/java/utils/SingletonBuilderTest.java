package utils;

import dao.RoomDao;
import entities.Room;
import org.junit.Assert;
import org.junit.Test;

public class SingletonBuilderTest extends Assert {

    @Test
    public void getInstanceImpl() {
        RoomDao roomDao1 =  SingletonBuilder.getInstanceImpl(RoomDao.class);
        RoomDao roomDao2 =  SingletonBuilder.getInstanceImpl(RoomDao.class);
        assertEquals(roomDao1, roomDao2);
        assertNotNull(roomDao1);
        assertNotNull(roomDao2);
    }

    @Test (expected = SingletonException.class)
    public void getInstanceImplNullException() {

        SingletonBuilder.getInstanceImpl(null);
    }

    @Test (expected = SingletonException.class)
    public void getInstanceImplNoImplementation() {

        SingletonBuilder.getInstanceImpl(Room.class);
    }
}

