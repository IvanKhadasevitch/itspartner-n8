package views.roomeditview;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import entities.Room;
import lombok.Getter;
import org.apache.log4j.Logger;
import services.RoomService;
import ui.customcompanents.BooleanField;
import utils.SingletonBuilder;

public class RoomEditForm extends FormLayout {
    private static Logger log = Logger.getLogger(RoomEditForm.class);

    private RoomEditView roomEditView;

    private final RoomService roomService = SingletonBuilder.getInstanceImpl(RoomService.class);
    @Getter
    private Room room;
    private final Binder<Room> roomBinder = new Binder<>(Room.class);

    private final TextField name = new TextField("Room name:");
    private final BooleanField lightState = new BooleanField();

    private Button saveRoomBtn = new Button("Save");
    private Button closeFormBtn = new Button("Close");

    public RoomEditForm(RoomEditView roomEditView) {
        super();
        this.roomEditView = roomEditView;
        configureComponents();
        buildLayout();

    }

    private void configureComponents() {
        // lightState
        lightState.setCaption("Light in the room is:");

        // add ToolTip to the forms fields
        name.setDescription("Room name");
        lightState.setDescription("Click the button to tern 'On' or 'Off' light in the room");

        // connect entity fields with form fields
        name.setRequiredIndicatorVisible(true);         // Required field
        roomBinder.forField(name)
                  .asRequired("Every room must have name")
                  .bind(Room::getName, Room::setName);

        lightState.setRequiredIndicatorVisible(true);         // Required field
        roomBinder.forField(lightState)
                  .asRequired("Every room has light 'On' or 'Off'")
                  .bind(Room::getLightState, Room::setLightState);

        // buttons
        saveRoomBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        saveRoomBtn.addClickListener(event -> saveRoom());
        closeFormBtn.addClickListener(event -> closeRoomEditForm());
    }

    private void buildLayout() {
        this.setMargin(true);       // Enable layout margins. Affects all four sides of the layout
        this.setVisible(false);

        // form tools - buttons
        HorizontalLayout buttons = new HorizontalLayout(saveRoomBtn, closeFormBtn);
        buttons.setSpacing(true);

        // collect form components - form fields & buttons
        this.addComponents(name, lightState, buttons);
    }
    public void saveRoom() {
        // This will make all current validation errors visible
        BinderValidationStatus<Room> status = roomBinder.validate();
        if (status.hasErrors()) {
            Notification.show("Validation error count: "
                    + status.getValidationErrors().size(), Notification.Type.WARNING_MESSAGE);
        } else {
            // save validated Room with not empty fields
            if ( !status.hasErrors() ) {
                try {
                    boolean isSaved = false;
                    Room savedRoom = null;
                    if (this.getRoom().getId() == null) {
                        // try save new room
                        savedRoom = roomService.add(this.getRoom());
                    } else {
                        // update existed room
                        savedRoom = roomService.update(this.getRoom());
                    }
                    isSaved = savedRoom != null;
                    if (isSaved) {
                        this.setRoom(savedRoom);
                        this.setVisible(false);
                        roomEditView.updateRoomList();
                        Notification.show("Saved room with name: " + this.getRoom().getName(),
                                Notification.Type.HUMANIZED_MESSAGE);
                    } else {
                        Notification.show("Can't save room with name: " + this.getRoom().getName(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                } catch (Exception exp) {
                    log.error("Can't save room with name: " + this.getRoom().getName(), exp);
                    Notification.show("Can't save room with name: " + this.getRoom().getName() +
                    ". Try again or refer to administrator.");
                }

            }
        }

        this.roomEditView.getAddRoomBtn().setEnabled(true);
    }

    public void setRoom(Room room) {
        try {
            Room roomFromDB = Long.valueOf(-1L).equals(room.getId()) ? room : roomService.get(room.getId());
            if (roomFromDB != null) {
                roomFromDB.setId(Long.valueOf(-1L).equals(roomFromDB.getId()) ? null : roomFromDB.getId());
                this.room = roomFromDB;
                this.setVisible(true);

                // connect entity fields with form fields
                roomBinder.setBean(roomFromDB);

            } else {
                Notification.show("Sorry, Some body destroyed with room already", Notification.Type.ERROR_MESSAGE);
            }
        } catch (Exception exp) {
            log.error("Can't get from DB room: " + room, exp);
            Notification.show("Some problems. Try again or choose another room", Notification.Type.ERROR_MESSAGE);
        }
    }

    public void closeRoomEditForm() {
        this.setVisible(false);
        roomEditView.getAddRoomBtn().setEnabled(true);
        roomEditView.getRoomList().deselectAll();
        // refresh view, take fresh changes from DB
        roomEditView.updateRoomList();
    }
}
