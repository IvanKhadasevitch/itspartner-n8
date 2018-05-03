package ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;

public class MainViewDisplay extends Panel implements ViewDisplay {
    private static Logger log = Logger.getLogger(MainViewDisplay.class);

    public MainViewDisplay() {

        setStyleName(ValoTheme.PANEL_BORDERLESS);
    }

    @Override
    public void showView(View view) {
        // Assuming View's are components, which is often the case
        setContent((Component) view);
    }


}
