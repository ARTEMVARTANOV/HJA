package Default;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MesaPoker extends JFrame {
	private  int cont = 0;
	
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
    private double boteTotal = 0.0;
    private JLabel boteLabel;
    private static final double CIEGA_PEQUENA = 100;
    private static final double CIEGA_GRANDE = 200;
    private int jugadorCiegaPequena = 1; // El jugador que paga la ciega pequeña
    private int jugadorCiegaGrande = 2; // El jugador que paga la ciega grande
    private int turnoActual; // Índice del jugador actual
    private double apuestaActual = CIEGA_PEQUENA;


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
        for (int i = 1; i <= 2; i++) {
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
        modalidadPanel.setBounds(500, 180, 300, 40); // Centrado horizontalmente
        modalidadPanel.add(modalidadLabel);
        modalidadPanel.add(modalidadSelector);
        add(modalidadPanel);

        // ===== Board panel =====
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(600, 100));
        boardPanel.setBounds(350, 250, 600, 150); // Centrado horizontalmente
        add(boardPanel);
        
        // Inicializar y mostrar el bote
        boteLabel = new JLabel("Bote: $0.00");
        boteLabel.setFont(new Font("Arial", Font.BOLD, 16));
        boteLabel.setBounds(350, 400, 600, 150); // Ubicación en la interfaz
        add(boteLabel);


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
	     textPanel.setBounds(500, 430, 300, 40); // Mover más abajo
	     textPanel.add(cartaInput);
	     add(textPanel);
	
	     // Panel para los botones (alineados horizontalmente y centrados)
	     JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	     buttonsPanel.setBounds(500, 480, 300, 40); // Mover más abajo
	     buttonsPanel.add(agregarCartaButton);
	     buttonsPanel.add(nextButton);
	     add(buttonsPanel);
	
	     // Botón global para actualizar todos los jugadores
	     JButton actualizarTodosButton = new JButton("Actualizar Todos");
	     actualizarTodosButton.addActionListener(e -> actualizarCartasTodos());
	
	     // Panel para el botón de actualizar todos
	     JPanel actualizarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	     actualizarPanel.setBounds(500, 530, 300, 40); // Mover más abajo
	     actualizarPanel.add(actualizarTodosButton);
	     add(actualizarPanel);


        // ===== Posicionar jugadores =====
        int panelWidth = 150;
        int panelHeight = 250;
        int[][] playerPositions = {
        	    {75, 50}, {1075, 550}
        	};

        int cartasPorJugador = (modalidad.equals("Omaha")) ? 4 : 2;
        double saldoInicial = 1000.0;
        for (int i = 0; i < 2; i++) {
            String[] cartasJugador = seleccionarCartasAleatorias(cartasPorJugador);
            manosJugadores.put(i + 1, cartasJugador);

            final int jugadorId = i + 1;
            Jugador jugador = new Jugador(jugadorId, cartaImagenMap, cartasJugador,saldoInicial, () -> {
                jugadoresActivos.put(jugadorId, false);
                actualizarProbabilidades(cartasBoardActuales, manosJugadores, generarBarajaDisponible());
            }, this);

            jugador.setBounds(playerPositions[i][0], playerPositions[i][1], panelWidth, panelHeight);
            panelesJugadores.put(jugadorId, jugador);
            add(jugador);
        }
        cont = 1;
        // Actualizar probabilidades iniciales
        turnoActual = jugadorCiegaPequena; // Comienza con la ciega pequeña
        actualizarProbabilidades(new ArrayList<>(), manosJugadores, generarBarajaDisponible());
        pagarCiegas();

    }

    private void reiniciarMesa() {
    	limpiarCartas();
    	boardPanel.limpiarCartas();
    	
    	if(cont == 1)
    		reiniciarJugadores();
    	
    	cartasBoardActuales.clear();
        cartasDisponibles.clear();
        inicializarCartasDisponibles();
        manosJugadores.clear();

        int cartasPorJugador = modalidad.equals("Omaha") ? 4 : 2;
        
        for (int i = 1; i <= 2; i++) {
        	String[] cartasJugador = seleccionarCartasAleatorias(cartasPorJugador);
            manosJugadores.put(i, cartasJugador);

            // Actualizar gráficamente cada jugador
            Jugador jugadorPanel = panelesJugadores.get(i);
            if (jugadorPanel != null) {
                jugadorPanel.reiniciarCartas(cartasJugador); // Método en Jugador
            }
        }
        
        actualizarProbabilidades(cartasBoardActuales, manosJugadores, generarBarajaDisponible());
        repaint();
    }

    private void inicializarCartasDisponibles() {
        cartasDisponibles = new ArrayList<>(cartaImagenMap.keySet());
    }
    
    private void ejecutarTurnoJugador(Jugador jugador) {
        String[] opciones = {"Check", "Ver", "Subir", "Foldear"};
        int eleccion = JOptionPane.showOptionDialog(
            this,
            "Jugador " + jugador.getId() + ": ¿Qué deseas hacer?",
            "Turno de Apuesta",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        switch (eleccion) {
            case 0: // Check
                check(jugador);
                break;
            case 1: // Ver
                ver(jugador);
                break;
            case 2: // Subir
                subir(jugador);
                break;
            case 3: // Foldear
                foldear(jugador);
                break;
            default:
                break;
        }
    }

    private void check(Jugador jugador) {
        if (apuestaActual > 0) {
            jugador.mostrarMensaje("No puedes hacer 'check'. Debes igualar la apuesta actual.");
        } else {
            avanzarFaseBoard();
        }
    }

    private void ver(Jugador jugador) {
        double cantidadPorVer = calcularCantidadPorVer(jugador);
        if (jugador.getSaldo() >= cantidadPorVer && apuestaActual != 0) {
            jugador.reducirSaldo(cantidadPorVer);
            jugador.aumentarApuesta(cantidadPorVer); // Sincroniza su apuesta actual
            actualizarBote(cantidadPorVer);
            apuestaActual = 0;
            pasarAlSiguienteJugador();
        } else {
            jugador.mostrarMensaje("Saldo insuficiente para igualar la apuesta.");
            ejecutarTurnoJugador(jugador);
        }
    }

    private double calcularCantidadPorVer(Jugador jugador) {
        return Math.max(apuestaActual - jugador.getApuestaActual(), 0);
    }
    
    private void subir(Jugador jugador) {
        String cantidadStr = JOptionPane.showInputDialog("Introduce la cantidad para subir:");
        
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            jugador.mostrarMensaje("No se ha ingresado ninguna cantidad.");
            return; // Detenemos la ejecución si la entrada es nula o vacía
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr.trim()); // Uso de trim() para eliminar espacios adicionales
            if (cantidad > 0 && jugador.getSaldo() >= cantidad) {
                jugador.reducirSaldo(cantidad);
                jugador.aumentarApuesta(cantidad);
                apuestaActual = cantidad;
                registrarSubida(cantidad);
                actualizarBote(cantidad);
                pasarAlSiguienteJugador();
            } else {
                jugador.mostrarMensaje("Cantidad inválida o saldo insuficiente.");
            }
        } catch (NumberFormatException e) {
            jugador.mostrarMensaje("Entrada no válida. Por favor, introduce un número.");
        }
    }

    
    private void foldear(Jugador jugador) {
        jugadoresActivos.put(jugador.getId(), false);
        jugador.mostrarMensaje("Jugador " + jugador.getId() + " se retira.");
        verificarGanador(); // Verificar si queda un solo jugador activo
        pasarAlSiguienteJugador();
    }
    
    
    private void registrarSubida(double cantidad) {
        apuestaActual += cantidad;
        JOptionPane.showMessageDialog(this, "Nueva subida registrada: $" + String.format("%.2f", cantidad));
    }
    
    private void pasarAlSiguienteJugador() {
        do {
            turnoActual = (turnoActual % 2) + 1; // Pasar al siguiente jugador en la mesa
        } while (!jugadoresActivos.get(turnoActual)); // Saltar jugadores retirados

        if (jugadoresActivos.size() != 1) {
            ejecutarTurnoJugador(panelesJugadores.get(turnoActual)); // Ejecutar el turno del jugador activo
        } else {
            verificarGanador();
        }
    }
    
    private void verificarGanador() {
        List<Integer> jugadoresRestantes = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : jugadoresActivos.entrySet()) {
            if (entry.getValue()) {
                jugadoresRestantes.add(entry.getKey());
            }
        }
        
        if (jugadoresRestantes.size() == 1) {
            int ganador = jugadoresRestantes.get(0);
            mostrarGanador(ganador);
            reiniciarRonda();
        }
    }
    
    private void mostrarGanador(int jugadorId) {
        Jugador ganador = panelesJugadores.get(jugadorId);
        double premio = boteTotal;
        ganador.ganarApuesta(premio);
        ganador.mostrarMensaje("¡El jugador " + jugadorId + " ha ganado $" + premio + "!");
    }


    private String[] seleccionarCartasAleatorias(int numCartas) {
    	if(cont == 1) {
    		Collections.shuffle(cartasDisponibles);
    	}
  
        if (cartasDisponibles.size() < numCartas) {
            throw new IllegalStateException("No hay suficientes cartas disponibles para seleccionar.");
        }
        String[] cartasSeleccionadas = new String[numCartas];
        for (int i = 0; i < numCartas; i++) {
            cartasSeleccionadas[i] = cartasDisponibles.remove(0);
        }
        return cartasSeleccionadas;
    }
    
    public void actualizarBote(double cantidad) {
        boteTotal += cantidad; // Sumar la cantidad apostada al bote
        boteLabel.setText("Bote: $" + String.format("%.2f", boteTotal)); // Actualizar la etiqueta
    }
    

    private void reiniciarRonda() {
        // Reiniciar el bote
        boteTotal = 0.0;
        boteLabel.setText("Bote: $0.00");
        
        pagarCiegas();
        
        // Reiniciar las apuestas de los jugadores
        for (Jugador jugador : panelesJugadores.values()) {
            if (jugador.getSaldo() > 0) {
                jugador.resetApuesta(); // Método en Jugador para reiniciar la apuesta
            } else {
                jugadoresActivos.put(jugador.getId(), false);
                jugador.mostrarMensaje("El jugador " + jugador.getId() + " ha sido eliminado.");
            }
        }

        // Continuar con la sigdeuiente mano
        reiniciarMesa();
    }


    private void avanzarFaseBoard() {
        if (faseBoard == 0) {
            actualizarCartasBoard(3);
            faseBoard = 3;
            ejecutarTurnoJugador(panelesJugadores.get(turnoActual));
        } else if (faseBoard == 3) {
            actualizarCartasBoard(4);
            faseBoard = 4;
            ejecutarTurnoJugador(panelesJugadores.get(turnoActual));
        } else if (faseBoard == 4) {
            actualizarCartasBoard(5);
            faseBoard = 5;
            ejecutarTurnoJugador(panelesJugadores.get(turnoActual));
        } else {
        	faseBoard = 0;
        	verificarGanador();
        }
    }
    
 // Método para pagar las ciegas
    private void pagarCiegas() {
        Jugador jugadorCiegaPequenaObj = panelesJugadores.get(jugadorCiegaPequena);
        Jugador jugadorCiegaGrandeObj = panelesJugadores.get(jugadorCiegaGrande);
        
        // Asegúrate de que los jugadores tengan suficiente saldo
        if (jugadorCiegaPequenaObj.getSaldo() >= CIEGA_PEQUENA) {
            jugadorCiegaPequenaObj.reducirSaldo(CIEGA_PEQUENA);  // Método para reducir saldo en Jugador
            actualizarBote(CIEGA_PEQUENA);  // Actualizar el bote
        } else {
            // El jugador no tiene suficiente saldo para pagar la ciega pequeña
            jugadorCiegaPequenaObj.mostrarMensaje("Saldo insuficiente para pagar la ciega pequeña.");
        }

        if (jugadorCiegaGrandeObj.getSaldo() >= CIEGA_GRANDE) {
            jugadorCiegaGrandeObj.reducirSaldo(CIEGA_GRANDE);  // Método para reducir saldo en Jugador
            actualizarBote(CIEGA_GRANDE);  // Actualizar el bote
        } else {
            // El jugador no tiene suficiente saldo para pagar la ciega grande
            jugadorCiegaGrandeObj.mostrarMensaje("Saldo insuficiente para pagar la ciega grande.");
        }

        // Desplazar las posiciones de las ciegas
        jugadorCiegaPequena = (jugadorCiegaPequena % 2) + 1;  // Mover la ciega pequeña al siguiente jugador
        jugadorCiegaGrande = (jugadorCiegaGrande % 2) + 1;  // Mover la ciega grande al siguiente jugador
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
    	boolean Omaha = false;
    	if (modalidad.equals("Omaha")) 
    		Omaha = true;
    	Map<Integer, Double> probabilidades = new HashMap<>();
    	Map<Integer, String[]> manosActivas = new HashMap<>();
        for (Map.Entry<Integer, String[]> entrada : manosJugadores.entrySet()) {
            if (jugadoresActivos.getOrDefault(entrada.getKey(), false)) {
                manosActivas.put(entrada.getKey(), entrada.getValue());
            }
        }

        probabilidades = ProbabilidadPoker.calcularProbabilidad(manosActivas, cartasComunitarias, baraja, Omaha);
        actualizarProbabilidades(probabilidades);
    }
    
    
    private void actualizarProbabilidades(Map<Integer, Double> probabilidades) {
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
        String entrada = cartaInput.getText().trim();
        String[] cartas = entrada.split(",");
        
        for(String carta : cartas) {
	        if (!cartaImagenMap.containsKey(carta)) {
	            JOptionPane.showMessageDialog(this, "Carta inválida. Intenta nuevamente.");
	            return;
	        }
	
	        if (cartasBoardActuales.contains(carta) || !cartasDisponibles.remove(carta)) {
	            JOptionPane.showMessageDialog(this, "Carta ya en uso o no disponible.");
	            return;
	        }
	
	        cartasBoardActuales.add(carta);
        }
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
        String[] palos = {"s", "h", "d", "c"};
        String[] valores = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
        for (String valor : valores) {
            for (String palo : palos) {
            	cartaImagenMap.put(valor + palo, "images/" + valor + palo + ".png");
            }
        }
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
        
        // Redibujar el tablero si es necesario
        repaint();
    }
    
    public boolean cartaEnDisponibles(String carta) {
        // Verifica si ambas cartas están en la lista de cartas disponibles
        return cartasDisponibles.contains(carta);
    }

    
    public void limpiarCartas() {
        for (Jugador jugadorPanel : panelesJugadores.values()) {
            if (jugadorPanel != null) {
                jugadorPanel.eliminarTodasLasCartas(); // Método que limpia las cartas gráficas
            }
        }
    }
    
    private void reiniciarJugadores() {
        // Iterar sobre los paneles de los jugadores y reiniciar cada jugador
        for (Map.Entry<Integer, Jugador> entry : panelesJugadores.entrySet()) {
            Jugador jugador = entry.getValue();
            int jugadorId = entry.getKey();

            // Obtener las cartas iniciales según la modalidad
            int cartasPorJugador = modalidad.equals("Omaha") ? 4 : 2;
            String[] cartasIniciales = seleccionarCartasAleatorias(cartasPorJugador);

            // Actualizar las cartas del jugador y reiniciar su estado
            manosJugadores.put(jugadorId, cartasIniciales);
            jugador.reiniciarJugador(cartasIniciales);
        }
        
        for (int i = 1; i <= 2; i++) {
            jugadoresActivos.put(i, true);
        }
        
        // Recalcular las probabilidades tras el reinicio
        actualizarProbabilidades(cartasBoardActuales, manosJugadores, generarBarajaDisponible());
        repaint(); // Actualizar la interfaz gráfica
    }

}

