package Default;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BoardPanel extends JPanel {
    private CartaPanel[] cardPanels;

    public BoardPanel() {
        // Cambiar el color de fondo a un verde más oscuro
        setBackground(new Color(34, 139, 34)); // Verde oscuro agradable

        // Usar GridBagLayout para centrar las cartas horizontal y verticalmente
        setLayout(new GridBagLayout());

        // Configurar el tamaño preferido del panel
        setPreferredSize(new Dimension(400, 150)); // Tamaño más grande para acomodar el centrado

        // Inicializar cinco paneles de carta para las cartas del board
        cardPanels = new CartaPanel[5];
        JPanel cartasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Panel para las cartas

        // Configurar fondo del sub-panel para asegurarse de que las cartas queden en el centro
        cartasPanel.setOpaque(false);

        for (int i = 0; i < 5; i++) {
            cardPanels[i] = new CartaPanel();
            cardPanels[i].setPreferredSize(new Dimension(50, 70)); // Tamaño fijo de cada carta
            cartasPanel.add(cardPanels[i]);
        }

        // Usar restricciones para centrar el sub-panel en el GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Columna
        gbc.gridy = 0; // Fila
        gbc.weightx = 1; // Expandirse horizontalmente
        gbc.weighty = 1; // Expandirse verticalmente
        gbc.anchor = GridBagConstraints.CENTER; // Centrar horizontal y verticalmente
        gbc.fill = GridBagConstraints.NONE; // No expandir los componentes más allá de su tamaño preferido
        add(cartasPanel, gbc);
    }

    // Limpiar las cartas del board
    public void limpiarCartas() {
        for (CartaPanel panel : cardPanels) {
            panel.setImage(null); // Limpia la imagen estableciendo null
        }
    }

    // Mostrar cartas en el board con las rutas proporcionadas
    public void mostrarCartas(List<String> cartas, Map<String, String> cartaImagenMap) {
        limpiarCartas(); // Asegura que las cartas previas se borren
        for (int i = 0; i < cartas.size(); i++) {
            String rutaImagen = cartaImagenMap.get(cartas.get(i));
            if (rutaImagen != null) {
                cardPanels[i].setImage(rutaImagen); // Usa setImage para actualizar la imagen
            }
        }
    }
}
