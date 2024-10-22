import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderListener implements ChangeListener {
    private JLabel[][] labels;
    private Main app;

    public SliderListener(JLabel[][] labels, Main app) {
        this.labels = labels;
        this.app = app;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        int value = slider.getValue();
        app.updateLabels(value);
    }
}