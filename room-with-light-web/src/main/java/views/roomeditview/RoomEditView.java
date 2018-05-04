package views.roomeditview;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import entities.Room;
import lombok.Getter;
import org.apache.log4j.Logger;
import services.RoomService;
import ui.MainUI;
import ui.customcompanents.FilterWithClearBtn;
import ui.customcompanents.TopCenterComposite;
import utils.SingletonBuilder;

import java.util.List;
import java.util.Set;

public class RoomEditView extends VerticalLayout implements View {
    private static Logger log = Logger.getLogger(RoomEditView.class);

    public static final String VIEW_NAME = "room/edit";

    private final String ERROR_NOTIFICATION = "Can't connect to data base. Try again or refer to administrator";

    private final RoomService roomService = SingletonBuilder.getInstanceImpl(RoomService.class);

    private FilterWithClearBtn filterByName;
    @Getter
    private final Button addRoomBtn = new Button("Add room");
    private final Button deleteRoomBtn = new Button("Delete room");
    private final Button editRoomBtn = new Button("Edit room");
    @Getter
    private Grid<Room> roomList;

    private final RoomEditForm roomEditForm = new RoomEditForm(this);

    public RoomEditView() {
        super();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        MainUI.startPage.pushState(MainUI.contextPath + "/" + VIEW_NAME);

        // Read the initial URI fragment
        enter();

        MainUI myUI = (MainUI) UI.getCurrent();
        myUI.getNavigator().getDisplay().showView(this);
    }

    private void enter() {
//        ... initialize the UI ...
        configureComponents();
        buildLayout();

    }

    private void configureComponents() {
        // filterByName field with clear button
        filterByName = new FilterWithClearBtn("Filter by name...", e -> updateRoomList());

        // add Room Button
        addRoomBtn.addClickListener(e -> {
            addRoomBtn.setEnabled(false);
            Room room = new Room("", false);
            room.setId(-1L);
            roomEditForm.setRoom(room);
        } );

        // delete Room Button
        deleteRoomBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteRoomBtn.setEnabled(false);
        deleteRoomBtn.addClickListener(event -> {
            // try delete selected items
            int deletedRoomsCount = deleteRooms(roomList.getSelectedItems());
            deleteRoomBtn.setEnabled(false);
            addRoomBtn.setEnabled(true);    // switch on addNewRoom possibility
            updateRoomList();
            Notification.show(String.format("Were deleted [%d] rooms.", deletedRoomsCount),
                    Notification.Type.WARNING_MESSAGE);
        });

        // edit Room Button (can edit only if one room was chosen)
        editRoomBtn.setEnabled(false);
        editRoomBtn.addClickListener(event -> {
            addRoomBtn.setEnabled(true);       // switch on addNewRoom possibility
            Room editCandidate = roomList.getSelectedItems().iterator().next();
            roomEditForm.setRoom(editCandidate);
        });

        // Room list (Grid)
        roomList = new Grid<>();
        roomList.addColumn(Room::getName).setCaption("Room name");
        roomList.addColumn( room -> room.getLightState() ? "On" : "Off").setCaption("Light in the room is...");
        roomList.setSelectionMode(Grid.SelectionMode.MULTI);
        // delete and edit selected Room
        roomList.addSelectionListener(event -> {
            // when Room is chosen - can delete or edit
            Set<Room> selectedRooms = event.getAllSelectedItems();
            if (selectedRooms != null && selectedRooms.size() == 1) {
                // chosen only one room - can add & delete & edit
                addRoomBtn.setEnabled(true);
                deleteRoomBtn.setEnabled(true);
                editRoomBtn.setEnabled(true);
            } else if (selectedRooms != null && selectedRooms.size() > 1) {
                // chosen more then one room - can delete & add
                roomEditForm.setVisible(false);
                addRoomBtn.setEnabled(true);
                deleteRoomBtn.setEnabled(true);
                editRoomBtn.setEnabled(false);
            } else {
                // no any room chosen - can't delete & edit
                deleteRoomBtn.setEnabled(false);
                editRoomBtn.setEnabled(false);
                roomEditForm.setVisible(false);
            }
        });
        // refresh Grid state
        this.updateRoomList();

    }

    private void buildLayout() {
        // tools bar
        Component[] controlComponents = {filterByName,
                addRoomBtn, deleteRoomBtn, editRoomBtn};
        Component control = new TopCenterComposite(controlComponents);
        this.addComponent(control);
        this.setComponentAlignment(control, Alignment.TOP_CENTER);

        // content - roomList & roomEditForm & roomComeInForm
        Component[] categoryContentComponents = {roomList, roomEditForm};
        Component categoryContent = new TopCenterComposite(categoryContentComponents);
        this.addComponent(categoryContent);
        this.setComponentAlignment(categoryContent, Alignment.TOP_CENTER);


        // Compound view part and allow resizing
        this.setSpacing(true);
        this.setMargin(false);
        this.setWidth("100%");
    }

    void updateRoomList() {
        try {
            List<Room> roomList = roomService.getAll(filterByName.getValue());
            this.roomList.setItems(roomList);
        } catch (Exception exp) {
            log.error(ERROR_NOTIFICATION, exp);
            Notification.show(ERROR_NOTIFICATION, Notification.Type.ERROR_MESSAGE);
        }

    }
    private int deleteRooms(Set<Room> roomSet) {
        int deleteCount = 0;
        try {
            for (Room room : roomSet) {
                roomService.delete(room.getId());
                deleteCount++;
            }
        } catch(Exception exp) {
            log.error("Can't delete room", exp);
            Notification.show(ERROR_NOTIFICATION, Notification.Type.ERROR_MESSAGE);
        }
        return deleteCount;

    }
}
