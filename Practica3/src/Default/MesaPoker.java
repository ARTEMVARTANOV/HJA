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
    private Map<Integer, Jugador> panelesJugadores;
    private JTextField cartaInput;
    private JButton agregarCartaButton;
    private Map<Integer, Boolean> jugadoresActivos;
    private String modalidad = "Texas Hold'em";

    public MesaPoker() {
        setTitle("Mesa de Poker");
        setSize(1300, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Usaremos posicionamiento absoluto para los componentes

        // Inicialización de los datos
        inicializarMapaCartas();
        inicializarCartasDisponibles();
        cartasBoardActuales = new ArrayList<>();
        manosJugadores = new HashMap<>();
        panelesJugadores = new HashMap<>();
        jugadoresActivos = new HashMap<>();
        for (int i = 1; i <= 6; i++) {
            jugadoresActivos.put(i, true);
        }

        // ===== Selector de modalidad centrado =====
        JLabel modalidadLabel = new JLabel("Modalidad:");
        modalidadLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JComboBox<String> modalidadSelector = new JComboBox<>(new String[]{"Texas Hold'em", "Omaha"});
        modalidadSelector.addActionListener(e -> {
            modalidad = (String) modalidadSelector.getSelectedItem();
            reiniciarMesa();
        });

        // Panel para el selector de modalidad (centrado encima del board)
        JPanel modalidadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        modalidadPanel.setBounds(500, 50, 300, 40); // Centrado horizontalmente
        modalidadPanel.add(modalidadLabel);
        modalidadPanel.add(modalidadSelector);
        add(modalidadPanel);

        // ===== Board panel =====
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(600, 100));
        boardPanel.setBounds(350, 120, 600, 150); // Centrado horizontalmente
        add(boardPanel);

	     // ===== Controles para agregar carta =====
	     // Campo de texto más grande y de una sola línea de alto
	     cartaInput = new JTextField(15); // Ajustar ancho del campo
	     cartaInput.setMaximumSize(new Dimension(200, 30)); // Restringir el alto del campo
	
	     // Botón para agregar carta
	     agregarCartaButton = new JButton("Agregar Carta");
	     agregarCartaButton.addActionListener(e -> agregarCartaManualmente());
	
	     // Botón para avanzar fase
	     JButton nextButton = new JButton("Next");
	     nextButton.addActionListener(e -> avanzarFaseBoard());
	
	     // Panel para el campo de texto (centrado)
	     JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	     textPanel.setBounds(500, 300, 300, 40); // Centrado horizontalmente
	     textPanel.add(cartaInput);
	     add(textPanel);
	
	     // Panel para los botones (alineados horizontalmente y centrados)
	     JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	     buttonsPanel.setBounds(500, 350, 300, 40); // Centrado horizontalmente y debajo del campo de texto
	     buttonsPanel.add(agregarCartaButton);
	     buttonsPanel.add(nextButton);
	     add(buttonsPanel);
	     
	  // Botón global para actualizar todos los jugadores
	     JButton actualizarTodosButton = new JButton("Actualizar Todos");
	     actualizarTodosButton.addActionListener(e -> actualizarCartasTodos());

	     // Panel para el botón de actualizar todos
	     JPanel actualizarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	     actualizarPanel.setBounds(500, 400, 300, 40); // Ajustar la posición debajo de los otros controles
	     actualizarPanel.add(actualizarTodosButton);
	     add(actualizarPanel);



        // ===== Posicionar jugadores =====
        int panelWidth = 150;
        int panelHeight = 180;
        int[][] playerPositions = {
            {75, 100}, {975, 100}, {50, 350}, {1000, 350}, {75, 600}, {975, 600}
        };

        int cartasPorJugador = (modalidad.equals("Omaha")) ? 4 : 2;
        for (int i = 0; i < 6; i++) {
            String[] cartasJugador = seleccionarCartasAleatorias(cartasPorJugador);
            manosJugadores.put(i + 1, cartasJugador);

            final int jugadorId = i + 1;
            Jugador jugador = new Jugador(jugadorId, cartaImagenMap, cartasJugador, () -> {
                jugadoresActivos.put(jugadorId, false);
                actualizarProbabilidades(cartasBoardActuales, manosJugadores, generarBarajaDisponible());
            }, this);

            jugador.setBounds(playerPositions[i][0], playerPositions[i][1], panelWidth, panelHeight);
            panelesJugadores.put(jugadorId, jugador);
            add(jugador);
        }

        // Actualizar probabilidades iniciales
        actualizarProbabilidades(new ArrayList<>(), manosJugadores, generarBarajaDisponible());
    }


    private void inicializarCartasDisponibles() {
        cartasDisponibles = new ArrayList<>(cartaImagenMap.keySet());
    }

    private String[] seleccionarCartasAleatorias(int numCartas) {
        if (cartasDisponibles.size() < numCartas) {
            throw new IllegalStateException("No hay suficientes cartas disponibles para seleccionar.");
        }
        String[] cartasSeleccionadas = new String[numCartas];
        for (int i = 0; i < numCartas; i++) {
            cartasSeleccionadas[i] = cartasDisponibles.remove(0);
        }
        return cartasSeleccionadas;
    }

    
    private void reiniciarMesa() {
        cartasDisponibles.clear();
        inicializarCartasDisponibles();
        cartasBoardActuales.clear();
        manosJugadores.clear();

        for (int i = 1; i <= 6; i++) {
            String[] cartasJugador;
            if (modalidad.equals("Texas Hold'em")) {
                cartasJugador = seleccionarCartasAleatorias(2); // 2 cartas para Texas Hold'em
            } else if (modalidad.equals("Omaha")) {
                cartasJugador = seleccionarCartasAleatorias(4); // 4 cartas para Omaha
            } else {
                throw new IllegalStateException("Modalidad desconocida: " + modalidad);
            }

            manosJugadores.put(i, cartasJugador);

            // Actualizar las cartas gráficamente para este jugador
            if (cartasJugador.length >= 2) { // Asegúrate de que existan al menos 2 cartas
                Jugador jugadorPanel = panelesJugadores.get(i);
                if (jugadorPanel != null) {
                    jugadorPanel.actualizarCartas(cartasJugador, cartaImagenMap); // Método de Jugador
                }
            }
        }

        actualizarProbabilidades(cartasBoardActuales, manosJugadores, generarBarajaDisponible());
        repaint();
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

    public void actualizarCartasBoard(int numCartas) {
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
        return baraja;
    }

    public void actualizarProbabilidades(List<String> cartasComunitarias, Map<Integer, String[]> manosJugadores, List<String> baraja) {
        if (modalidad.equals("Texas Hold'em")) {
            Map<Integer, String[]> manosActivas = new HashMap<>();
            for (Map.Entry<Integer, String[]> entrada : manosJugadores.entrySet()) {
                if (jugadoresActivos.getOrDefault(entrada.getKey(), false)) {
                    manosActivas.put(entrada.getKey(), entrada.getValue());
                }
            }

            Map<Integer, Double> probabilidades = ProbabilidadPoker.calcularProbabilidad(manosActivas, cartasComunitarias, baraja);

            for (Map.Entry<Integer, String[]> jugador : manosJugadores.entrySet()) {
                int jugadorId = jugador.getKey();
                double probabilidad = probabilidades.getOrDefault(jugadorId, 0.0);
                Jugador jugadorPanel = panelesJugadores.get(jugadorId);
                if (jugadorPanel != null) {
                    jugadorPanel.actualizarProbabilidad(probabilidad);
                }
            }
        } else if (modalidad.equals("Omaha")) {
            // Convertir las manos y el board al formato Omaha
            List<Carta> cartasComunitariasOmaha = convertirCartas(cartasComunitarias);
            Map<Integer, List<Carta>> manosJugadoresOmaha = convertirManos(manosJugadores);

            // Calcular probabilidades Omaha
            List<Carta> barajaRestante = convertirCartas(baraja);
            actualizarProbabilidadesOmaha(cartasComunitariasOmaha, manosJugadoresOmaha, barajaRestante);
        }
    }
    
    private List<Carta> convertirCartas(List<String> cartas) {
        List<Carta> resultado = new ArrayList<>();
        for (String carta : cartas) {
            resultado.add(new Carta(carta)); // Asume que el constructor de Carta acepta un String
        }
        return resultado;
    }

    private Map<Integer, List<Carta>> convertirManos(Map<Integer, String[]> manos) {
        Map<Integer, List<Carta>> resultado = new HashMap<>();
        for (Map.Entry<Integer, String[]> entrada : manos.entrySet()) {
            List<Carta> mano = new ArrayList<>();
            for (String carta : entrada.getValue()) {
                mano.add(new Carta(carta)); // Constructor de Carta a partir de String
            }
            resultado.put(entrada.getKey(), mano);
        }
        return resultado;
    }

    
    private void actualizarProbabilidadesOmaha(List<Carta> cartasComunitarias, Map<Integer, List<Carta>> manosJugadores, List<Carta> barajaRestante) {
    	// Calcular el equity para Omaha
    	Map<Integer, Double> probabilidades = ProbabilidadPoker.calcularProbabilidadOmaha(manosJugadores,cartasComunitarias,barajaRestante);

    	// Actualizar la interfaz gráfica con las probabilidades calculadas
    	for (Map.Entry<Integer, Double> entrada : probabilidades.entrySet()) {
    		int jugadorId = entrada.getKey();
    		double probabilidad = entrada.getValue();
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

    private void actualizarCartasTodos() {
        for (Map.Entry<Integer, Jugador> entrada : panelesJugadores.entrySet()) {
            Jugador jugador = entrada.getValue();

            // Supongamos que cada jugador tiene un campo de entrada para cartas
            String entradaTexto = jugador.getEntradaCartas(); // Método para obtener el texto del campo
            if (entradaTexto != null && !entradaTexto.isEmpty()) {
                jugador.actualizarCartas(); // Reutilizamos el método existente en Jugador
            }
        }
        // Después de actualizar, recalculamos probabilidades
        List<String> barajaActualizada = generarBarajaDisponible();
        actualizarProbabilidades(cartasBoardActuales, manosJugadores, barajaActualizada);
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

    public void imprimirCartasDisponibles() {
        // Usar String.join para concatenar las cartas con un espacio entre ellas
        String cartas = String.join(" ", cartasDisponibles);
        System.out.println(cartas);  // Imprimir el resultado en la consola
    }
    
    public void actualizarManoJugador(int jugadorId, String[] nuevaMano) {
        // Primero, obtén la mano actual del jugador
        String[] manoActual = manosJugadores.get(jugadorId);

        // Asegúrate de que la mano actual no sea nula antes de intentar agregar las cartas a cartasDisponibles
        if (manoActual != null) {
            // Volver a agregar las cartas antiguas del jugador a cartasDisponibles
            for (String carta : manoActual) {
                if (!cartasDisponibles.contains(carta)) {
                    cartasDisponibles.add(carta);
                }
            }
        }

        // Ahora, actualiza la mano del jugador con la nueva mano
        manosJugadores.put(jugadorId, nuevaMano);
        // Eliminar las nuevas cartas de cartasDisponibles
        for (String carta : nuevaMano) {
            cartasDisponibles.remove(carta);
        }
        // Volver a calcular las cartas disponibles y las probabilidades
        List<String> barajaActualizada = generarBarajaDisponible();
        actualizarProbabilidades(cartasBoardActuales, manosJugadores, barajaActualizada);

        // Redibujar el tablero si es necesario
        repaint();
    }
    
    public boolean cartaEnDisponibles(String carta1, String carta2) {
        // Verifica si ambas cartas están en la lista de cartas disponibles
        return cartasDisponibles.contains(carta1) && cartasDisponibles.contains(carta2);
    }

}


