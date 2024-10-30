import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderListener implements ChangeListener {
    private JLabel[][] labels;
    private GUI app;

    public SliderListener(JLabel[][] labels, GUI app) {
        this.labels = labels;
        this.app = app;
    }

    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        int value = slider.getValue();
        app.updateLabels(value);
    }
}