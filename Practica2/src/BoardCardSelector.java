import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList; // Para ArrayList
import java.util.List; // Para List

public class BoardCardSelector {
    private JPanel panel;
    private JLabel[][] labels;
    private String[][] cardValues = {
            {"Ah", "Ac", "Ad", "As"},
            {"Kh", "Kc", "Kd", "Ks"},
            {"Qh", "Qc", "Qd", "Qs"},
            {"Jh", "Jc", "Jd", "Js"},
            {"Th", "Tc", "Td", "Ts"},
            {"9h", "9c", "9d", "9s"},
            {"8h", "8c", "8d", "8s"},
            {"7h", "7c", "7d", "7s"},
            {"6h", "6c", "6d", "6s"},
            {"5h", "5c", "5d", "5s"},
            {"4h", "4c", "4d", "4s"},
            {"3h", "3c", "3d", "3s"},
            {"2h", "2c", "2d", "2s"}
    };

    public BoardCardSelector() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(13, 4)); // 13 filas (de As a 2), 4 columnas (h, c, d, s)
        labels = new JLabel[13][4];

        createBoard();
    }

    private void createBoard() {
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 4; j++) {
                JLabel label = new JLabel(cardValues[i][j], SwingConstants.CENTER);
                label.setBorder(new LineBorder(Color.BLACK));
                label.setOpaque(true);
                
                // Establecer el color de fondo según la columna
                switch (j) {
                    case 0: // Columna h
                        label.setBackground(new Color(255, 182, 193)); // Rojo pastel
                        break;
                    case 1: // Columna c
                        label.setBackground(new Color(144, 238, 144)); // Verde pastel
                        break;
                    case 2: // Columna d
                        label.setBackground(new Color(173, 216, 230)); // Azul clarito
                        break;
                    case 3: // Columna s
                        label.setBackground(new Color(211, 211, 211)); // Gris clarito
                        break;
                }
                
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JLabel source = (JLabel) e.getSource();
                        if (source.getBackground().equals(Color.YELLOW)) {
                            source.setBackground(source.getBackground().darker()); // Cambiar color a un tono más oscuro
                        } else {
                            source.setBackground(Color.YELLOW); // Cambiar a amarillo para representar selección
                        }
                    }
                });
                panel.add(label);
                labels[i][j] = label;
            }
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public String[][] getCardValues() {
        return cardValues;
    }
    
    public ArrayList<String> getSelectedCards() {
        ArrayList<String> selectedCards = new ArrayList<>();
        JLabel[][] labels = this.labels; // Matriz de etiquetas de la nueva tabla de 4 columnas

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 4; j++) {
                if (labels[i][j].getBackground().equals(Color.YELLOW)) {
                    // Añadir la carta seleccionada a la lista
                    selectedCards.add(cardValues[i][j]);
                }
            }
        }

        return selectedCards;
    }
}