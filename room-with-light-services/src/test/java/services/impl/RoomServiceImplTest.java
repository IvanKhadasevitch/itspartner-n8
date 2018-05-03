package services.impl;

import entities.Room;
import org.junit.Assert;
import org.junit.Test;
import services.RoomService;
import utils.SingletonBuilder;

import java.util.List;

public class RoomServiceImplTest extends Assert {
    private RoomService service = SingletonBuilder.getInstanceImpl(RoomService.class);

    @Test
    public void crudGetAll() {
        Room saved;
        Room getIt;
        Room updated;

        Room newOneForSave = new Room();
        newOneForSave.setName("Room #3");
        newOneForSave.setLightState(true);

        Room newOneForUpdate = new Room();
        newOneForUpdate.setName("Room #3 update");
        newOneForUpdate.setLightState(false);

        // check getAll before save Room in DB
        List<Room> list1 = service.getAll();

        // save Room in DB
        assertEquals(null, newOneForSave.getId());
        saved = service.add(newOneForSave);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        // getAll check after save Room in DB
        List<Room> list2 = service.getAll();
        assertEquals(1, list2.size() - list1.size());

        // get Room from DB
        getIt = service.get(saved.getId());
        assertNotNull(getIt);
        assertEquals(saved, getIt);

        // update Room in DB
        newOneForUpdate.setId(saved.getId());
        service.update(newOneForUpdate);
        getIt = service.get(newOneForUpdate.getId());
        assertNotNull(getIt);
        assertNotEquals(getIt, saved);
        assertEquals(newOneForUpdate, getIt);

        // delete Room from DB
        int deletedRecords = service.delete(saved.getId());
        assertEquals(1, deletedRecords);
        getIt = service.get(saved.getId());
        assertNull(getIt);
    }

    @Test
    public void getAllWithFilter() {
        Room newOneForSave = new Room();
        newOneForSave.setName("Room #1");       // pass filter
        newOneForSave.setLightState(false);

        Room anotherOneForSave = new Room();
        anotherOneForSave.setName("Room #1 abc");   // pass filter
        anotherOneForSave.setLightState(true);

        Room notPassFilterForSave = new Room();
        notPassFilterForSave.setName("Room abc");      //not pass filter
        notPassFilterForSave.setLightState(true);

        final String filter = "#1";

        // check getAll(filter) before save Rooms in DB
        List<Room> list1 = service.getAll(filter);
        List<Room> listAll1 = service.getAll();

        // save 2 Rooms in DB that pass filter & one Room that not pass filter
        Room savedThatPassFilter1 = service.add(newOneForSave);
        assertNotNull(savedThatPassFilter1);
        Room savedThatPassFilter2 = service.add(anotherOneForSave);
        assertNotNull(savedThatPassFilter2);

        Room savedThatNotPassFilter = service.add(notPassFilterForSave);
        assertNotNull(savedThatNotPassFilter);

        // getAll(filter) check after save Rooms in DB
        List<Room> list2 = service.getAll(filter);
        assertEquals(2, list2.size() - list1.size());
        List<Room> listAll2 = service.getAll();
        assertEquals(3, listAll2.size() - listAll1.size());

        // delete saved entities
        int deletedRecords = service.delete(savedThatPassFilter1.getId());
        assertEquals(1, deletedRecords);
        Room getIt = service.get(savedThatPassFilter1.getId());
        assertNull(getIt);

        deletedRecords = service.delete(savedThatPassFilter2.getId());
        assertEquals(1, deletedRecords);
        getIt = service.get(savedThatPassFilter2.getId());
        assertNull(getIt);

        deletedRecords = service.delete(savedThatNotPassFilter.getId());
        assertEquals(1, deletedRecords);
        getIt = service.get(savedThatNotPassFilter.getId());
        assertNull(getIt);
    }

    @Test
    public void isNameAlreadyInUse() {
        // check null & empty name
        assertTrue(service.isNameAlreadyInUse(null));
        assertTrue(service.isNameAlreadyInUse(""));

        Room newOneForSave = new Room();
        newOneForSave.setName("Room #1");
        newOneForSave.setLightState(false);

        // save room in Db
        Room saved = service.add(newOneForSave);

        assertTrue(service.isNameAlreadyInUse("room #1"));

        // delete Room from DB
        int deletedRecords = service.delete(saved.getId());
        assertEquals(1, deletedRecords);
        Room getIt = service.get(saved.getId());
        assertNull(getIt);

    }

}
