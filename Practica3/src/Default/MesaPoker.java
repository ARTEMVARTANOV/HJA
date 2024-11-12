package Default;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MesaPoker extends JFrame {
    private Map<String, String> cartaImagenMap; // Mapa para asociar cartas con sus imágenes
    private List<String> cartasDisponibles;
    private BoardPanel boardPanel;
    
    public MesaPoker() {
        setTitle("Mesa de Poker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        inicializarMapaCartas();       // Inicializa el mapa de cartas e imágenes
        inicializarCartasDisponibles(); // Inicializa el conjunto de cartas disponibles

        // Panel central para el board
        boardPanel = new BoardPanel();
        
        // Calcular el centro de la ventana para centrar el boardPanel
        int panelWidth = 300;
        int panelHeight = 80;
        int x = (getWidth() - panelWidth) / 2;
        int y = (getHeight() - panelHeight) / 2 - 50; // Ajuste para centrar verticalmente con espacio
        
        boardPanel.setBounds(x, y, panelWidth, panelHeight); // Posición centrada
        add(boardPanel);
        
        // Configurar el JComboBox para seleccionar el número de cartas en el board
        JComboBox<Integer> numCartasBoard = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4, 5});
        numCartasBoard.setBounds(x + 75, y + 100, 100, 30); // Coloca el combo debajo del boardPanel
        numCartasBoard.addActionListener(e -> {
            int numCartas = (int) numCartasBoard.getSelectedItem();
            actualizarCartasBoard(numCartas);
        });
        add(numCartasBoard);

        // Crear y añadir los paneles de los jugadores con campos de texto
        int[][] playerPositions = {
                {100, 100}, {600, 100}, {50, 250}, {650, 250}, {100, 400}, {600, 400}
        };

        for (int i = 0; i < 6; i++) {
            String[] cartasJugador = seleccionarCartasAleatorias(); // Selecciona dos cartas aleatorias
            Jugador jugador = new Jugador(i + 1, cartaImagenMap, cartasJugador);
            jugador.setBounds(playerPositions[i][0], playerPositions[i][1], 150, 150);
            add(jugador);
        }
    }
    
    private void inicializarCartasDisponibles() {
        cartasDisponibles = new ArrayList<>(cartaImagenMap.keySet()); // Llena la lista con todas las cartas
        Collections.shuffle(cartasDisponibles); // Mezcla las cartas para aleatoriedad
    }
    
    private String[] seleccionarCartasAleatorias() {
        // Selecciona dos cartas y las elimina de la lista para evitar duplicados
        String carta1 = cartasDisponibles.remove(0);
        String carta2 = cartasDisponibles.remove(0);
        return new String[]{carta1, carta2};
    }
    
    private void actualizarCartasBoard(int numCartas) {
        if (boardPanel == null) {
            System.out.println("Error: boardPanel es null en actualizarCartasBoard");
            return;
        }
        boardPanel.limpiarCartas();
        
        List<String> cartasBoard = new ArrayList<>();
        for (int i = 0; i < numCartas; i++) {
            if (!cartasDisponibles.isEmpty()) {
                String cartaSeleccionada = cartasDisponibles.remove(0);
                cartasBoard.add(cartaSeleccionada);
                System.out.println("Carta seleccionada para el board: " + cartaSeleccionada);
            } else {
                System.out.println("No hay suficientes cartas disponibles.");
            }
        }
        
        boardPanel.mostrarCartas(cartasBoard, cartaImagenMap);
    }

    private void inicializarMapaCartas() {
        cartaImagenMap = new HashMap<>();

        // Ases
        cartaImagenMap.put("As", "images/As.png");
        cartaImagenMap.put("Ah", "images/Ah.png");
        cartaImagenMap.put("Ad", "images/Ad.png");
        cartaImagenMap.put("Ac", "images/Ac.png");

        // Reyes
        cartaImagenMap.put("Ks", "images/Ks.png");
        cartaImagenMap.put("Kh", "images/Kh.png");
        cartaImagenMap.put("Kd", "images/Kd.png");
        cartaImagenMap.put("Kc", "images/Kc.png");

        // Reinas
        cartaImagenMap.put("Qs", "images/Qs.png");
        cartaImagenMap.put("Qh", "images/Qh.png");
        cartaImagenMap.put("Qd", "images/Qd.png");
        cartaImagenMap.put("Qc", "images/Qc.png");

        // Jotas
        cartaImagenMap.put("Js", "images/Js.png");
        cartaImagenMap.put("Jh", "images/Jh.png");
        cartaImagenMap.put("Jd", "images/Jd.png");
        cartaImagenMap.put("Jc", "images/Jc.png");

        // Dieces
        cartaImagenMap.put("Ts", "images/Ts.png");
        cartaImagenMap.put("Th", "images/Th.png");
        cartaImagenMap.put("Td", "images/Td.png");
        cartaImagenMap.put("Tc", "images/Tc.png");

        // Nueves
        cartaImagenMap.put("9s", "images/9s.png");
        cartaImagenMap.put("9h", "images/9h.png");
        cartaImagenMap.put("9d", "images/9d.png");
        cartaImagenMap.put("9c", "images/9c.png");

        // Ochos
        cartaImagenMap.put("8s", "images/8s.png");
        cartaImagenMap.put("8h", "images/8h.png");
        cartaImagenMap.put("8d", "images/8d.png");
        cartaImagenMap.put("8c", "images/8c.png");

        // Sietes
        cartaImagenMap.put("7s", "images/7s.png");
        cartaImagenMap.put("7h", "images/7h.png");
        cartaImagenMap.put("7d", "images/7d.png");
        cartaImagenMap.put("7c", "images/7c.png");

        // Seises
        cartaImagenMap.put("6s", "images/6s.png");
        cartaImagenMap.put("6h", "images/6h.png");
        cartaImagenMap.put("6d", "images/6d.png");
        cartaImagenMap.put("6c", "images/6c.png");

        // Cincos
        cartaImagenMap.put("5s", "images/5s.png");
        cartaImagenMap.put("5h", "images/5h.png");
        cartaImagenMap.put("5d", "images/5d.png");
        cartaImagenMap.put("5c", "images/5c.png");

        // Cuatros
        cartaImagenMap.put("4s", "images/4s.png");
        cartaImagenMap.put("4h", "images/4h.png");
        cartaImagenMap.put("4d", "images/4d.png");
        cartaImagenMap.put("4c", "images/4c.png");

        // Treses
        cartaImagenMap.put("3s", "images/3s.png");
        cartaImagenMap.put("3h", "images/3h.png");
        cartaImagenMap.put("3d", "images/3d.png");
        cartaImagenMap.put("3c", "images/3c.png");

        // Doses
        cartaImagenMap.put("2s", "images/2s.png");
        cartaImagenMap.put("2h", "images/2h.png");
        cartaImagenMap.put("2d", "images/2d.png");
        cartaImagenMap.put("2c", "images/2c.png");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MesaPoker mesa = new MesaPoker();
            mesa.setVisible(true);
        });
    }
}



