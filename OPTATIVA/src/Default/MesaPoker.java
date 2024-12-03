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
    private boolean modoAllIn = false;
    private int apuestaTotalJug1 = 100;
    private int apuestaTotalJug2= 200;
    private int contadorTurno1 = 0;
	private boolean huboFold = false;
    
    
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
        int idBot = 3;
        for (int i = 0; i < 2; i++) {
            String[] cartasJugador = seleccionarCartasAleatorias(cartasPorJugador);
            manosJugadores.put(i + 1, cartasJugador);

            final int jugadorId = i + 1;
            boolean esBot = (jugadorId == idBot); // Determina si este jugador es el bot
            Jugador jugador = new Jugador(jugadorId, cartaImagenMap, cartasJugador,saldoInicial, () -> {
                jugadoresActivos.put(jugadorId, false);
                actualizarProbabilidades(cartasBoardActuales, manosJugadores, generarBarajaDisponible());
            }, this, esBot);

            jugador.setBounds(playerPositions[i][0], playerPositions[i][1], panelWidth, panelHeight);
            panelesJugadores.put(jugadorId, jugador);
            add(jugador);
        }
        cont = 1;
        // Actualizar probabilidades iniciales
        turnoActual = jugadorCiegaPequena; // Comienza con la ciega pequeña
        actualizarProbabilidades(new ArrayList<>(), manosJugadores, generarBarajaDisponible());
        pagarCiegas();
        SwingUtilities.invokeLater(() -> ejecutarTurnoJugador(panelesJugadores.get(turnoActual)));

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
        String[] opciones = {"Check", "Call", "Raise", "Fold"};
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
        
        System.out.println(apuestaTotalJug1 + ", " + apuestaTotalJug2);
        System.out.println(apuestaActual);
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
    	if(modoAllIn) {
    		jugador.mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
            ejecutarTurnoJugador(jugador);
        }
    	
        if (apuestaActual != jugador.getApuestaActual()) {
            jugador.mostrarMensaje("No puedes hacer 'check'. Hay apuestas activas.");
            ejecutarTurnoJugador(jugador);
        } else {
            if (jugador.estaEnJuego() && apuestaTotalJug1 == apuestaTotalJug2 && contadorTurno1 != 0) {
                avanzarFaseBoard();
            } else {
                pasarAlSiguienteJugador();
            }
        }
    }



    private void ver(Jugador jugador) {
    	boolean aux = false;
        double cantidadPorVer = calcularCantidadPorVer(jugador);
        if (cantidadPorVer > 0) {
            if (jugador.getSaldo() >= cantidadPorVer) {
            	
            	if(jugador.getId() == 1)
            		apuestaTotalJug1 += cantidadPorVer;
            	else
            		apuestaTotalJug2 += cantidadPorVer;
            	
                jugador.reducirSaldo(cantidadPorVer);
                jugador.aumentarApuesta(cantidadPorVer);
                actualizarBote(cantidadPorVer);
                jugador.mostrarMensaje("Jugador " + jugador.getId() + " iguala con $" + cantidadPorVer);
                jugador.resetApuesta(); // Resetea la apuesta actual del jugador para reflejar la igualdad.
                if (jugador.estaEnJuego() && apuestaTotalJug1 == apuestaTotalJug2 && contadorTurno1 != 0) {
                    avanzarFaseBoard();
                }
                	
            } else {
                double saldoRestante = jugador.getSaldo();
                jugador.reducirSaldo(saldoRestante);
                jugador.aumentarApuesta(saldoRestante);
                actualizarBote(saldoRestante);
                jugador.mostrarMensaje("Jugador " + jugador.getId() + " hace all-in con $" + saldoRestante);
            }
            apuestaActual = calcularApuestaMaxima(); // Ajustar la apuesta máxima al estado actual.
        } else {
            jugador.mostrarMensaje("Apuesta igualada, puedes hacer 'Check' o 'Raise'.");
            ejecutarTurnoJugador(jugador);
            aux = true;
        }
        if(modoAllIn) {
        	iniciarModoAllIn();
        	aux = true;
        }
        else if(aux == false) {
        	if (algunoAllIn()) {
            	modoAllIn = true; 
            }
            pasarAlSiguienteJugador();
        }
    }



    private double calcularCantidadPorVer(Jugador jugador) {
        return Math.max(apuestaActual - jugador.getApuestaActual(), 0);
    }
    
    private void subir(Jugador jugador) {
    	if(modoAllIn) {
    		jugador.mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
            ejecutarTurnoJugador(jugador);
        }
    	boolean aux = false;
        String cantidadStr = JOptionPane.showInputDialog("Introduce la cantidad para subir:");
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            jugador.mostrarMensaje("No se ingresó cantidad.");
            ejecutarTurnoJugador(jugador);
            return;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr.trim());

            if (cantidad <= apuestaActual) {
                jugador.mostrarMensaje("La cantidad debe ser mayor a la apuesta actual.");
                ejecutarTurnoJugador(jugador);
                return;
            }
            
            if (jugador.getSaldo() >= cantidad) {
            	
            	if(jugador.getId() == 1)
            		apuestaTotalJug1 += cantidad;
            	else
            		apuestaTotalJug2 += cantidad;
            	
                jugador.reducirSaldo(cantidad);
                jugador.aumentarApuesta(cantidad);
                registrarSubida(cantidad);
                actualizarBote(cantidad);
                apuestaActual = cantidad;
            } else {
                jugador.mostrarMensaje("Jugador " + jugador.getId() + " no puede apostar más dinero del que tiene.");
            	ejecutarTurnoJugador(jugador);
            }

            if(modoAllIn) {
            	iniciarModoAllIn();
            	aux = true;
            }
            else if(aux == false) {
            	if (algunoAllIn()) {
                	modoAllIn = true; 
                }
            	
                pasarAlSiguienteJugador();
                
            }
            
        } catch (NumberFormatException e) {
            jugador.mostrarMensaje("Entrada no válida.");
            ejecutarTurnoJugador(jugador);
        }
    }

    
    private void foldear(Jugador jugador) {
        jugadoresActivos.put(jugador.getId(), false);
        jugador.mostrarMensaje("Jugador " + jugador.getId() + " se retira.");
        verificarGanador(); // Verificar si queda un solo jugador activo
    	huboFold = true; 
        pasarAlSiguienteJugador();
 
    }
    
    
    private void registrarSubida(double cantidad) {
        apuestaActual = Math.max(apuestaActual, cantidad); // Mantiene la apuesta máxima actualizada.
        JOptionPane.showMessageDialog(this, "Nueva subida registrada: $" + String.format("%.2f", cantidad));
    }
    
    private double calcularApuestaMaxima() {
        double max = 0;
        for (Jugador jugador : panelesJugadores.values()) {
            if (jugador.estaEnJuego() && jugador.getApuestaActual() > max) {
                max = jugador.getApuestaActual();
            }
        }
        return max;
    }
    
    private void iniciarModoAllIn() {
        JOptionPane.showMessageDialog(null,"Modo all-in activado. Las rondas avanzarán automáticamente.");
        avanzarHastaRiver();
    }
    
    private boolean boardEstaEnRiver() {
        // Suponiendo que tienes un contador de fases del board
        return faseBoard == 5;
    }
    
    private void avanzarHastaRiver() {
        while (!boardEstaEnRiver()) {
            avanzarBoard();
        }
        verificarGanador();
        modoAllIn = false;
    }
    
    private boolean algunoAllIn() {
    	int cont = 0;
        for (Jugador jugador : panelesJugadores.values()) {
            if (jugador.estaEnJuego() && jugador.getSaldo() == 0) {
                cont += 1;
            }
        }
        
        if (cont == 1 || cont == 2)
        	return true;
        else
        	return false;
    }

    
    private void pasarAlSiguienteJugador() {
    	if(!huboFold) 
    		contadorTurno1 = 1; 
    	turnoActual = (turnoActual == 2) ? 1 : 2;
    	Jugador jugadorActual = panelesJugadores.get(turnoActual);
        if (jugadorActual.esBot()) {
            ejecutarTurnoBot(jugadorActual);
        } else {
            ejecutarTurnoJugador(jugadorActual);
        }
    }

    private void ejecutarTurnoBot(Jugador bot) {

        // Lógica del bot
        Map<Integer, Double> probabilidades = ProbabilidadPoker.calcularProbabilidad(manosJugadores, cartasBoardActuales, generarBarajaDisponible(), modalidad.equals("Omaha"));
        double equityBot = probabilidades.getOrDefault(bot.getId(), 0.0);
        String accion = determinarAccionBot(equityBot, bot);

        switch (accion) {
            case "Check":
                bot.mostrarMensaje("El bot ha hecho check.");
                check(bot);
                break;
            case "Call":
                bot.mostrarMensaje("El bot ha hecho call.");
                ver(bot);
                break;
            case "Raise":
                subirBot(bot, equityBot);
                break;
            case "Fold":
                bot.mostrarMensaje("El bot ha hecho fold.");
                foldear(bot);
                break;
        }
    }
    
    private String determinarAccionBot(double equity, Jugador bot) {
        // Lógica simple basada en equity
        if(modoAllIn) {
        	if (equity > 60) {
                return "Call"; // Equity muy alta, miralo
            } else {
                return "Fold"; // Equity baja, retírate
            }
        }
        else{
        	if(apuestaActual != bot.getApuestaActual()) {
        		if (equity > 70) {
                    return "Raise"; // Equity muy alta, sube
                } else if (equity < 20) {
                    return "Fold"; // Equity baja, retírate
                } else
                	return "Call";
                			
        	}else if (equity > 70) {
                return "Raise"; // Equity muy alta, sube
            } else if (equity < 20) {
                return "Fold"; // Equity baja, retírate
            } else
            	return "Check";
        }
    }

    private void subirBot(Jugador bot, double equity) {
    	if(modoAllIn) {
    		bot.mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
            ejecutarTurnoJugador(bot);
        }
    	boolean aux = false;
        double cantidadSubida = equity / 100 * bot.getSaldo(); // Subir un porcentaje del saldo basado en equity

        if (cantidadSubida > apuestaActual) {
            if (bot.getSaldo() >= cantidadSubida) {
            	
            	if(bot.getId() == 1)
            		apuestaTotalJug1 += cantidadSubida;
            	else
            		apuestaTotalJug2 += cantidadSubida;
            	
                bot.reducirSaldo(cantidadSubida);
                bot.aumentarApuesta(cantidadSubida);
                registrarSubida(cantidadSubida);
                actualizarBote(cantidadSubida);
                apuestaActual = cantidadSubida; // Actualizar apuesta actual al all-in
                bot.mostrarMensaje("El bot ha hecho raise de $ " + cantidadSubida);
            } else {
                // Caso de all-in si el bot no tiene suficiente saldo para cubrir la subida
                double saldoRestante = bot.getSaldo();
                bot.reducirSaldo(saldoRestante);
                bot.aumentarApuesta(saldoRestante);
                registrarSubida(saldoRestante); // Registrar el all-in como la nueva apuesta
                actualizarBote(saldoRestante);
                apuestaActual = saldoRestante; // Actualizar apuesta actual al all-in
                bot.mostrarMensaje("Bot hace all-in con $ " + saldoRestante);
            }
        } else {
            bot.mostrarMensaje("El bot no puede subir por debajo de la apuesta actual.");
        }

        // Verificación para iniciar modo all-in si todos aceptan
        if(modoAllIn) {
        	iniciarModoAllIn();
        	aux = true;
        }
        if(aux == false) {
        	if (algunoAllIn()) {
            	modoAllIn = true; 
            }
        	pasarAlSiguienteJugador();
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
        } else if (jugadoresRestantes.size() == 2) {
            int jugador1 = jugadoresRestantes.get(0);
            int jugador2 = jugadoresRestantes.get(1);

            // Validar cartasBoardActuales
            if (cartasBoardActuales == null || cartasBoardActuales.isEmpty()) {
                throw new IllegalStateException("El board está vacío.");
            }
            
            // Validar manos de jugadores
            String[] cartasJugador1 = manosJugadores.get(jugador1);
            String[] cartasJugador2 = manosJugadores.get(jugador2);

            if (cartasJugador1 == null || cartasJugador1.length == 0 ||
                cartasJugador2 == null || cartasJugador2.length == 0) {
                throw new IllegalStateException("Las cartas de los jugadores están vacías.");
            }
            LogicaManoPoker logicaJugador = new LogicaManoPoker(cartasJugador1, cartasBoardActuales, false);
            LogicaManoPoker logicaBot = new LogicaManoPoker(cartasJugador2, cartasBoardActuales, false);

            Map<Integer, ManoPoker.HandRank> mejoresRanks = new HashMap<>();
            Map<Integer, String> mejoresManos = new HashMap<>();

            mejoresRanks.put(1, logicaJugador.getMejorRank());
            mejoresManos.put(1, logicaJugador.getMejorMano());
            mejoresRanks.put(2, logicaBot.getMejorRank());
            mejoresManos.put(2, logicaBot.getMejorMano());

            List<Integer> ganadores = ProbabilidadPoker.determinarGanador(mejoresManos, mejoresRanks);

            if (ganadores.size() == 1) {
                mostrarGanador(ganadores.get(0));
            } else {
                repartirBote(ganadores);
            }
            // Verificar si un jugador se quedó sin saldo
            List<Integer> jugadoresConSaldo = new ArrayList<>();
            int contConSaldo = 0;
            for (Jugador jugador : panelesJugadores.values()) {
                if (jugador.getSaldo() > 0) {
                	contConSaldo += 1;
                	jugadoresConSaldo.add(jugador.getId());
                } else {
                    jugador.mostrarMensaje("El jugador " + jugador.getId() + " ha sido eliminado.");
                }
            }
            if (contConSaldo == 2)
            	reiniciarRonda();
            else {
            	// Si solo queda un jugador con saldo, declarar ganador final y salir del método
                if (contConSaldo == 1) {
                    if (mostrarGanadorFinal(jugadoresConSaldo.get(0))) {
                        System.exit(0); // Terminar la aplicación si el método retorna true
                    }
                }
            	
            }
        }
    }



    private void repartirBote(List<Integer> ganadores) {
        double premioPorJugador = boteTotal / ganadores.size();
        for (int ganadorId : ganadores) {
            Jugador ganador = panelesJugadores.get(ganadorId);
            ganador.ganarApuesta(premioPorJugador);
            ganador.mostrarMensaje("¡El jugador " + ganadorId + " ha ganado $" + premioPorJugador + " en un empate!");
        }
    }

    private void mostrarGanador(int jugadorId) {
        Jugador ganador = panelesJugadores.get(jugadorId);
        double premio = boteTotal;
        ganador.ganarApuesta(premio);
        ganador.mostrarMensaje("¡El jugador " + jugadorId + " ha ganado $" + premio + " con la mejor mano!");
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
    	huboFold = true;
    	
        // Reiniciar el bote
        boteTotal = 0.0;
        boteLabel.setText("Bote: $0.00");
        
        if(jugadorCiegaPequena == 1) {
        	apuestaTotalJug1 = 100;
    		apuestaTotalJug2 = 200;
        }else {
        	apuestaTotalJug1 = 200;
    		apuestaTotalJug2 = 100;
        }
        
    	contadorTurno1 = 0;
        
        pagarCiegas();
        apuestaActual = CIEGA_PEQUENA;

        // Verificar si un jugador se quedó sin saldo
        List<Integer> jugadoresConSaldo = new ArrayList<>();
        for (Jugador jugador : panelesJugadores.values()) {
            if (jugador.getSaldo() > 0) {
                jugador.resetApuesta(); // Método en Jugador para reiniciar la apuesta
                jugadoresConSaldo.add(jugador.getId());
            } else {
                jugadoresActivos.put(jugador.getId(), false);
                jugador.mostrarMensaje("El jugador " + jugador.getId() + " ha sido eliminado.");
            }
        }

        // Si solo queda un jugador con saldo, declarar ganador final y salir del método
        if (jugadoresConSaldo.size() == 1) {
            if (mostrarGanadorFinal(jugadoresConSaldo.get(0))) {
                System.exit(0); // Terminar la aplicación si el método retorna true
            }
            return;
        }

        // Continuar con la siguiente mano
        reiniciarMesa();
    }

    private boolean mostrarGanadorFinal(int jugadorId) {
        Jugador ganador = panelesJugadores.get(jugadorId);
        JOptionPane.showMessageDialog(
            this,
            "¡El jugador " + jugadorId + " ha ganado el juego con $" + 
            String.format("%.2f", ganador.getSaldo()) + "!",
            "¡Ganador Final!",
            JOptionPane.INFORMATION_MESSAGE
        );
        return true; // Indicar que el juego ha terminado
    }

    private void avanzarFaseBoard() {
        if (faseBoard == 0) {
            actualizarCartasBoard(3);
            faseBoard = 3;
            pasarAlJugadorBB();
        } else if (faseBoard == 3) {
            actualizarCartasBoard(4);
            faseBoard = 4;
            pasarAlJugadorBB();
        } else if (faseBoard == 4) {
            actualizarCartasBoard(5);
            faseBoard = 5;
            pasarAlJugadorBB();
        } else {
        	faseBoard = 0;
        	verificarGanador();
        }
    }
    
    private void pasarAlJugadorBB() {
    	turnoActual = 2;
    	contadorTurno1 = 0;
    	Jugador jugadorActual = panelesJugadores.get(2);
        if (jugadorActual.esBot()) {
            ejecutarTurnoBot(jugadorActual);
        } else {
            ejecutarTurnoJugador(jugadorActual);
        }     
	}

	private void avanzarBoard() {
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
    
    // Método para pagar las ciegas
    private void pagarCiegas() {
        Jugador jugadorCiegaPequenaObj = panelesJugadores.get(jugadorCiegaPequena);
        Jugador jugadorCiegaGrandeObj = panelesJugadores.get(jugadorCiegaGrande);

        // Ciega pequeña
        if (jugadorCiegaPequenaObj.getSaldo() >= CIEGA_PEQUENA) {
            jugadorCiegaPequenaObj.reducirSaldo(CIEGA_PEQUENA);
            actualizarBote(CIEGA_PEQUENA);
        } else {
            double saldoRestante = jugadorCiegaPequenaObj.getSaldo();
            jugadorCiegaPequenaObj.reducirSaldo(saldoRestante);
            actualizarBote(saldoRestante);
            jugadorCiegaPequenaObj.mostrarMensaje("Jugador " + jugadorCiegaPequena + " hace all-in con $" + saldoRestante);
        }

        // Ciega grande
        if (jugadorCiegaGrandeObj.getSaldo() >= CIEGA_GRANDE) {
            jugadorCiegaGrandeObj.reducirSaldo(CIEGA_GRANDE);
            actualizarBote(CIEGA_GRANDE);
        } else {
            double saldoRestante = jugadorCiegaGrandeObj.getSaldo();
            jugadorCiegaGrandeObj.reducirSaldo(saldoRestante);
            actualizarBote(saldoRestante);
            jugadorCiegaGrandeObj.mostrarMensaje("Jugador " + jugadorCiegaGrande + " hace all-in con $" + saldoRestante);
        }

        // Desplazar las posiciones de las ciegas
        jugadorCiegaPequena = (jugadorCiegaPequena == 2) ? 1 : 2;
        jugadorCiegaGrande = (jugadorCiegaGrande == 2) ? 1 : 2;
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

