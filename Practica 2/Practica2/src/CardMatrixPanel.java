
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CardMatrixPanel {
    private JLabel[][] labels;
    private JPanel panel;

    // Lista de combinaciones de cartas por fila, según el mapeo que proporcionaste
    private final String[][] cardCombinations = {
            {"AA", "AKs", "AQs", "AJs", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s"},
            {"AKo", "KK", "KQs", "KJs", "KTs", "K9s", "K8s", "K7s", "K6s", "K5s", "K4s", "K3s", "K2s"},
            {"AQo", "KQo", "QQ", "QJs", "QTs", "Q9s", "Q8s", "Q7s", "Q6s", "Q5s", "Q4s", "Q3s", "Q2s"},
            {"AJo", "KJo", "QJo", "JJ", "JTs", "J9s", "J8s", "J7s", "J6s", "J5s", "J4s", "J3s", "J2s"},
            {"ATo", "KTo", "QTo", "JTo", "TT", "T9s", "T8s", "T7s", "T6s", "T5s", "T4s", "T3s", "T2s"},
            {"A9o", "K9o", "Q9o", "J9o", "T9o", "99", "98s", "97s", "96s", "95s", "94s", "93s", "92s"},
            {"A8o", "K8o", "Q8o", "J8o", "T8o", "98o", "88", "87s", "86s", "85s", "84s", "83s", "82s"},
            {"A7o", "K7o", "Q7o", "J7o", "T7o", "97o", "87o", "77", "76s", "75s", "74s", "73s", "72s"},
            {"A6o", "K6o", "Q6o", "J6o", "T6o", "96o", "86o", "76o", "66", "65s", "64s", "63s", "62s"},
            {"A5o", "K5o", "Q5o", "J5o", "T5o", "95o", "85o", "75o", "65o", "55", "54s", "53s", "52s"},
            {"A4o", "K4o", "Q4o", "J4o", "T4o", "94o", "84o", "74o", "64o", "54o", "44", "43s", "42s"},
            {"A3o", "K3o", "Q3o", "J3o", "T3o", "93o", "83o", "73o", "63o", "53o", "43o", "33", "32s"},
            {"A2o", "K2o", "Q2o", "J2o", "T2o", "92o", "82o", "72o", "62o", "52o", "42o", "32o", "22"}
    };

    public CardMatrixPanel() {
        panel = new JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(1300, 1000)); // Ajuste del tamaño para adaptarse a 13x13

        labels = new JLabel[13][13];
        createMatrix(); // Crea la cuadrícula de cartas
    }

    private void createMatrix() {
        int labelWidth = 100;
        int labelHeight = 80;

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                int x = j * labelWidth;
                int y = i * labelHeight;

                JLabel label = new JLabel(cardCombinations[i][j], SwingConstants.CENTER); // Asignar la combinación de cartas
                label.setBorder(new LineBorder(Color.BLACK));
                label.setOpaque(true);
                label.setBackground(Color.WHITE);

                label.setBounds(x, y, labelWidth, labelHeight);
                label.addMouseListener(new LabelListener(label)); // Listener para cambiar color
                panel.add(label);
                labels[i][j] = label;
            }
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public JLabel[][] getLabels() {
        return labels;
    }

    // Método para limpiar la matriz
    public void clearMatrix() {
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                labels[i][j].setBackground(Color.WHITE);
            }
        }
    }
    public void updateFromSlider(int value) {
        // Ejemplo: Cambiar el color de todas las celdas a un color basado en el valor del deslizador
        Color newColor = (value % 2 == 0) ? Color.LIGHT_GRAY : Color.WHITE;

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                labels[i][j].setBackground(newColor);
            }
        } 
    }
}
