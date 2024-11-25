package Default;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MesaPoker extends JFrame {
	private Map<String, String> cartaImagenMap;
    private List<String> cartasDisponibles;
    private List<String> cartasBoardActuales;
    private BoardPanel boardPanel;
    private int faseBoard = 0;
    private Map<Integer, String[]> manosJugadores;
    private Map<Integer, Jugador> panelesJugadores; // Nuevo mapa
    private JTextField cartaInput;
    private JButton agregarCartaButton;

    public MesaPoker() {
        setTitle("Mesa de Poker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        inicializarMapaCartas();
        inicializarCartasDisponibles();
        cartasBoardActuales = new ArrayList<>();
        manosJugadores = new HashMap<>();
        panelesJugadores = new HashMap<>(); // Inicializar mapa de paneles de jugadores

        boardPanel = new BoardPanel();

        int panelWidth = 300;
        int panelHeight = 80;
        int x = (getWidth() - panelWidth) / 2;
        int y = (getHeight() - panelHeight) / 2 - 50;

        boardPanel.setBounds(x, y, panelWidth, panelHeight);
        add(boardPanel);

        JButton nextButton = new JButton("Next");
        nextButton.setBounds(x + 100, y + 100, 100, 30);
        nextButton.addActionListener(e -> avanzarFaseBoard());
        add(nextButton);

        cartaInput = new JTextField(5);
        agregarCartaButton = new JButton("Agregar Carta");
        cartaInput.setBounds(x + 90, y + 135, 120, 30);
        agregarCartaButton.setBounds(x + 90, y + 165, 120, 30);
        agregarCartaButton.addActionListener(e -> agregarCartaManualmente());
        add(cartaInput);
        add(agregarCartaButton);

        int[][] playerPositions = {
            {110, 10}, {540, 10}, {35, 185}, {620, 185}, {110, 365}, {540, 365}
        };

        for (int i = 0; i < 6; i++) {
            String[] cartasJugador = seleccionarCartasAleatorias();
            manosJugadores.put(i + 1, cartasJugador);
            Jugador jugador = new Jugador(i + 1, cartaImagenMap, cartasJugador);
            jugador.setBounds(playerPositions[i][0], playerPositions[i][1], 150, 180);
            panelesJugadores.put(i + 1, jugador); // Guardar referencia al panel del jugador
            add(jugador);
        }

        actualizarProbabilidades(new ArrayList<>(), manosJugadores, generarBarajaDisponible());
    }

    private void inicializarCartasDisponibles() {
        cartasDisponibles = new ArrayList<>(cartaImagenMap.keySet());
        Collections.shuffle(cartasDisponibles);
    }

    private String[] seleccionarCartasAleatorias() {
        String carta1 = cartasDisponibles.remove(0);
        String carta2 = cartasDisponibles.remove(0);
        return new String[]{carta1, carta2};
    }

    private void avanzarFaseBoard() {
        if (faseBoard == 0) {
            actualizarCartasBoard(3);
            faseBoard = 3;
        } else if (faseBoard == 3) {
            actualizarCartasBoard(4);
            faseBoard = 4;
        } else if (faseBoard == 4) {
            actualizarCartasBoard(5);
            faseBoard = 5;
        }
    }

    private void actualizarCartasBoard(int numCartas) {
        while (cartasBoardActuales.size() < numCartas && !cartasDisponibles.isEmpty()) {
            String cartaNueva = cartasDisponibles.remove(0);
            cartasBoardActuales.add(cartaNueva);
        }

        boardPanel.mostrarCartas(cartasBoardActuales, cartaImagenMap);
        actualizarProbabilidades(cartasBoardActuales, manosJugadores, generarBarajaDisponible());
    }

    private List<String> generarBarajaDisponible() {
        List<String> baraja = new ArrayList<>(cartasDisponibles);
        baraja.removeAll(cartasBoardActuales);
        for (String[] mano : manosJugadores.values()) {
            Collections.addAll(baraja, mano);
        }
        return baraja;
    }

    private void actualizarProbabilidades(List<String> cartasComunitarias, Map<Integer, String[]> manosJugadores, List<String> baraja) {
        Map<Integer, Double> probabilidades = ProbabilidadPoker.calcularProbabilidad(manosJugadores, cartasComunitarias, baraja);

        for (Map.Entry<Integer, String[]> jugador : manosJugadores.entrySet()) {
            int jugadorId = jugador.getKey();
            double probabilidad = probabilidades.getOrDefault(jugadorId, 0.0);
            Jugador jugadorPanel = panelesJugadores.get(jugadorId);
            if (jugadorPanel != null) {
                jugadorPanel.actualizarProbabilidad(probabilidad);
            }
        }
    }



    private void agregarCartaManualmente() {
        String carta = cartaInput.getText().trim();

        if (!cartaImagenMap.containsKey(carta)) {
            JOptionPane.showMessageDialog(this, "Carta inválida. Intenta nuevamente.");
            return;
        }

        if (cartasBoardActuales.contains(carta) || !cartasDisponibles.remove(carta)) {
            JOptionPane.showMessageDialog(this, "Carta ya en uso o no disponible.");
            return;
        }

        cartasBoardActuales.add(carta);
        boardPanel.mostrarCartas(cartasBoardActuales, cartaImagenMap);
        actualizarProbabilidades(cartasBoardActuales, manosJugadores, generarBarajaDisponible());
        cartaInput.setText("");
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



