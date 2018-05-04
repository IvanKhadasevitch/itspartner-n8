package views.roomvisitview;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.renderers.ComponentRenderer;
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

public class RoomVisitView extends VerticalLayout implements View {
    private static Logger log = Logger.getLogger(RoomVisitView.class);

    public static final String VIEW_NAME = "room/visit";

    private final RoomService roomService = SingletonBuilder.getInstanceImpl(RoomService.class);

    private FilterWithClearBtn filterByName;
    private final Label information = new Label("Click any room to come in.");

    @Getter
    private Grid<Room> roomList;
    private final RoomVisitForm roomVisitForm = new RoomVisitForm(this);

    public RoomVisitView() {
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

        // Room list (Grid)
        roomList = new Grid<>();
        roomList.addColumn(Room::getName).setCaption("Room name");
//        roomList.addColumn( room -> room.getLightState() ? "On" : "Off").setCaption("Light in the room is...");
        roomList.addColumn(room -> {
            Button button = new Button("");
            button.addStyleName(ValoTheme.BUTTON_SMALL);
            if (room.getLightState()) {
                button.setIcon(VaadinIcons.LIGHTBULB);
                button.setEnabled(true);
                button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
            } else {
                button.setIcon(VaadinIcons.CLOSE);
                button.setEnabled(true);
                button.addStyleName(ValoTheme.BUTTON_DANGER);
            }
            return button;
        }, new ComponentRenderer()).setCaption("Light in the room is...");

        roomList.setSelectionMode(Grid.SelectionMode.SINGLE);
        ((SingleSelectionModel<Room>) roomList.getSelectionModel())
                .setDeselectAllowed(false);             // disallow empty selection
        // visit selected Room
        roomList.addSelectionListener(event -> {
            // when Room is chosen - can visit
            if (event.getFirstSelectedItem().orElse(null) != null) {
                roomVisitForm.setRoom(event.getFirstSelectedItem().orElse(null));
            }
        });

        // refresh Grid state
        this.updateRoomList();
    }

    private void buildLayout() {
        // tools bar
        Component[] controlComponents = {filterByName,
                information};
        Component control = new TopCenterComposite(controlComponents);
        this.addComponent(control);
        this.setComponentAlignment(control, Alignment.TOP_CENTER);

        // content - roomList & roomEditForm & roomComeInForm
        Component[] categoryContentComponents = {roomList, roomVisitForm};
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
            String ERROR_NOTIFICATION = "Can't connect to data base. Try again or refer to administrator";
            log.error(ERROR_NOTIFICATION, exp);
            Notification.show(ERROR_NOTIFICATION, Notification.Type.ERROR_MESSAGE);
        }

    }
}
