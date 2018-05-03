package ui.customcompanents;

import com.vaadin.ui.*;

public class BooleanField extends CustomField<Boolean> {
    private final Button button = new Button("Off");
    private boolean value;

    @Override
    protected Component initContent() {
        button.addClickListener(event -> {
            setValue(!getValue());
            button.setCaption(getValue() ? "On" : "Off");
        });

        return button;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    protected void doSetValue(Boolean value) {
        this.value = value;
        button.setCaption(value ? "On" : "Off");
    }
}
