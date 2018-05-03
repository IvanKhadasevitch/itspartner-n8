package views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class DefaultView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "room";


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed NOT in the init() method(), but constructor
        UI.getCurrent().getNavigator().getDisplay().showView(this);
        Notification.show("Choose any menu item, please.", Notification.Type.HUMANIZED_MESSAGE);
    }
}

