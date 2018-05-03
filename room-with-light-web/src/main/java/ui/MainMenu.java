package ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;
import views.roomeditview.RoomEditView;
import views.roomvisitview.RoomVisitView;

public class MainMenu extends CustomComponent {
    private MenuBar.MenuItem previous = null;
    private final Label selection = new Label("-");

    public MainMenu() {
        HorizontalLayout layout = new HorizontalLayout();
        MenuBar menuBar = new MenuBar();
        menuBar.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
        layout.addComponent(menuBar);
        // A feedback component
        layout.addComponent(selection);

        // put menu items
        menuBar.addItem("Edit room", VaadinIcons.EDIT , roomEditBtn());
        menuBar.addItem("Visit room", VaadinIcons.LIGHTBULB  , roomVisitBtn());

        layout.setSizeUndefined();
        setCompositionRoot(layout);
    }

    private MenuBar.Command roomEditBtn() {

        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                highLightSelection(selection, selectedItem);
                getUI().getNavigator().navigateTo(RoomEditView.VIEW_NAME);
            }
        };
    }

    private MenuBar.Command roomVisitBtn() {

        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                highLightSelection(selection, selectedItem);
                getUI().getNavigator().navigateTo(RoomVisitView.VIEW_NAME);
            }
        };
    }

    private void highLightSelection(Label selection, MenuBar.MenuItem selectedItem) {
        selection.setValue("Ordered a " +
                selectedItem.getText() +
                " from menu.");

        if (previous != null)
            previous.setStyleName("unchecked" );

        selectedItem.setStyleName("checked");
        previous = selectedItem;
    }
}

