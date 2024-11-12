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
        
        // Configurar el FlowLayout para una fila centrada con espaciado horizontal
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); 
        
        // Ajustar el tamaño preferido para que quepan las cinco cartas en una fila
        setPreferredSize(new Dimension(300, 80)); 
        
        // Inicializar cinco paneles de carta para las cartas del board
        cardPanels = new CartaPanel[5];
        for (int i = 0; i < 5; i++) {
            cardPanels[i] = new CartaPanel();
            cardPanels[i].setPreferredSize(new Dimension(40, 60)); // Tamaño fijo de cada carta
            add(cardPanels[i]);
        }
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






