package Default;

import javax.swing.*;

public class PokerGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MesaPoker mesa = new MesaPoker();
            mesa.setVisible(true);
        });
    }
}
