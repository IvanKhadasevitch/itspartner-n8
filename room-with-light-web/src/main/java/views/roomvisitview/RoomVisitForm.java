package views.roomvisitview;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import entities.Room;
import lombok.Getter;
import org.apache.log4j.Logger;
import services.RoomService;
import utils.SingletonBuilder;

public class RoomVisitForm extends FormLayout {
    private static Logger log = Logger.getLogger(RoomVisitForm.class);

    private RoomVisitView roomVisitView;

    private final RoomService roomService = SingletonBuilder.getInstanceImpl(RoomService.class);
    @Getter
    private Room room;
    private final Binder<Room> roomBinder = new Binder<>(Room.class);

    private final Label roomLight = new Label();

    private final TextField name = new TextField("Room name:");

    private final Button switchLightBtn = new Button("Switch on/off light");
    private final Button closeFormBtn = new Button("Leave the room");


    public RoomVisitForm(RoomVisitView roomVisitView) {
        super();
        this.roomVisitView = roomVisitView;
        configureComponents();
        buildLayout();

    }

    private void configureComponents() {
        // connect entity fields with form fields
        name.setReadOnly(true);
        roomBinder.forField(name)
                  .bind(Room::getName, Room::setName);

        //show light state in room
        refreshLightInRoom();

        // add ToolTip to the buttons
        switchLightBtn.setDescription("Click the button to tern 'On' or 'Off' light in the room");

        // buttons
        switchLightBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        switchLightBtn.addClickListener(event -> saveRoomLight());
        closeFormBtn.addClickListener(event -> closeRoomVisitForm());
    }

    private void buildLayout() {
        this.setMargin(true);       // Enable layout margins. Affects all four sides of the layout
        this.setVisible(false);

        // form tools - buttons
        HorizontalLayout buttons = new HorizontalLayout(switchLightBtn, closeFormBtn);
        buttons.setSpacing(true);

        // collect form components - form fields & buttons
        this.addComponents(name, roomLight, buttons);
    }

    private void refreshLightInRoom() {
        if (room != null && room.getLightState()) {
            // light ON in room
            roomLight.setValue("Light in room is On");
            roomLight.setIcon(VaadinIcons.LIGHTBULB);
            roomLight.setStyleName(ValoTheme.LABEL_SUCCESS);
        } else {
            // light OFF in room
            roomLight.setValue("Light in room is Off");
            roomLight.setIcon(VaadinIcons.CLOSE);

            roomLight.setStyleName(ValoTheme.LABEL_FAILURE);
        }
    }

    private void saveRoomLight() {
        if (room == null || room.getId() == null) {
            return;
        }

        boolean currentLightState = room.getLightState();
        try {
            room.setLightState(! currentLightState);
            // save updated room in DB
            room = roomService.updateLightState(room);
            // refresh view, take fresh changes from DB
            setRoom(room);
            roomVisitView.updateRoomList();
        } catch (Exception exp) {
            log.error("Can't update room: " + room, exp);
            Notification.show("Can't switch on/of light. Some problems with electric. Try again");
            if (room != null) {
                room.setLightState(currentLightState);
            }

        }
        refreshLightInRoom();
    }

    public void setRoom(Room room) {
        if (room == null) {
            Notification.show("Sorry, Some body destroyed the room already", Notification.Type.ERROR_MESSAGE);
            return;
        }

        try {
            Room roomFromDB = roomService.get(room.getId());
            if (roomFromDB != null) {
                this.room = roomFromDB;
                this.setVisible(true);

                // connect entity fields with form fields
                roomBinder.readBean(roomFromDB);
                refreshLightInRoom();

            } else {
                Notification.show("Sorry, Some body destroyed the room already", Notification.Type.ERROR_MESSAGE);
            }
        } catch (Exception exp) {
            log.error("Can't get from DB room: " + room, exp);
            Notification.show("Some problems. Try again or choose another room", Notification.Type.ERROR_MESSAGE);
        }
    }

    public void closeRoomVisitForm() {
        this.setVisible(false);
        // refresh view, take fresh changes from DB
        roomVisitView.updateRoomList();
    }
}
