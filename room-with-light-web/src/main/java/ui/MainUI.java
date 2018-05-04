package ui;

import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import views.roomeditview.RoomEditForm;
import views.roomeditview.RoomEditView;
import views.DefaultView;
import views.roomvisitview.RoomVisitView;

import javax.servlet.annotation.WebServlet;

@Title("Room with light")
@PushStateNavigation                    // allow separate URL with "/"
public class MainUI extends UI {
    private static Logger log = Logger.getLogger(RoomEditForm.class);

    public static String contextPath = null;
    public static Page startPage = null;
    private final MainViewDisplay mainViewDisplay = new MainViewDisplay();

    private final MainMenu mainMenu = new MainMenu();

    @Override
    protected void init(VaadinRequest request) {
        if (contextPath == null) {
            contextPath = request.getContextPath();
            startPage = Page.getCurrent();
        }
        // set page URI in browser history
        startPage.pushState(contextPath + "/" + DefaultView.VIEW_NAME);

        // scan event when URI changed by navigation in browser history
        getPage().addPopStateListener( e -> enter() );

        // Read the initial URI fragment
        enter();

    }

    private void enter() {
//        ... initialize the UI ...
        configureComponents();
        buildLayout();

    }

    private void configureComponents() {

        // set Navigator
        Navigator navigator = this.getNavigator();
        if (navigator == null) {
            navigator = new Navigator(this, (ViewDisplay) mainViewDisplay);
            this.setNavigator(navigator);
        }
        // set navigation views
        navigator.addView(DefaultView.VIEW_NAME, new DefaultView()); // at start navigate to DefaultView
        navigator.addView(RoomEditView.VIEW_NAME, RoomEditView.class);
        navigator.addView(RoomVisitView.VIEW_NAME, RoomVisitView.class);

    }

    private void buildLayout() {
        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        this.setContent(root);

        // sen navigation bar - mainMenu
        root.addComponent(mainMenu);

        // mainViewDisplay this is Panel
        mainViewDisplay.setSizeFull();
        root.addComponent(mainViewDisplay);
        root.setExpandRatio(mainViewDisplay, 1.0f);

    }


    /*
     * Deployed as a Servlet or Portlet.
     *
     * You can specify additional servlet parameters like the URI and UI class
     * name and turn on production mode when you have finished developing the
     * application.
     */
    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
