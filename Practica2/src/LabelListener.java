import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class LabelListener extends MouseAdapter {
    private JLabel label;
    private Color selectedColor;
    private Color defaultColor = Color.YELLOW;

    public LabelListener(JLabel label, Color selectedColor) {
        this.label = label;
        this.selectedColor = selectedColor;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (label.getBackground().equals(selectedColor)) {
            label.setBackground(defaultColor);
        } else {
            label.setBackground(selectedColor);
        }
    }
}
