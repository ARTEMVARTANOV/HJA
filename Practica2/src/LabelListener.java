import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class LabelListener extends MouseAdapter {
    private JLabel label;

    public LabelListener(JLabel label) {
        this.label = label;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (label.getBackground().equals(Color.YELLOW)) {
            label.setBackground(Color.WHITE);
        } else {
            label.setBackground(Color.YELLOW);
        }
    }
}