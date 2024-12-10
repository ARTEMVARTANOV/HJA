package Default;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Map<Integer, Boolean> jugadoresActivos;
    private String modalidad = "Texas Hold'em";
    private double boteTotal = 0.0;
    private JLabel boteLabel;
    private static final double CIEGA_INICIAL = 100;
    private int jugadorCiegaPequena = 1; // El jugador que paga la ciega pequeña
    private int jugadorCiegaGrande = 2; // El jugador que paga la ciega grande
    private int turnoActual; // Índice del jugador actual
    private double apuestaActual = CIEGA_INICIAL;
    private boolean modoAllIn = false;
    private double apuestaTotalJug1 = 100;
    private double apuestaTotalJug2= 200;
    private int contadorTurno1 = 0;
	private  int cont = 0;
	private  double ciegaActual = CIEGA_INICIAL;
    private double valorManoBot = 0;
    
    //Bot 1 = normal; Bot 2 = agresivo; Bot 3 = conservador
    private  int tipoDeBot = 1;
    private JPanel panelCartasJugador1, panelCartasJugador2;
	
    public MesaPoker() {
        setTitle("Mesa de Poker");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configurar el contenido del JFrame
        MesaPanel mesaPanel = new MesaPanel();
        mesaPanel.setLayout(null);
        setContentPane(mesaPanel);

        // Inicializar lógica
        inicializarMapaCartas();
        inicializarCartasDisponibles();
        cartasBoardActuales = new ArrayList<>();
        manosJugadores = new HashMap<>();
        panelesJugadores = new HashMap<>();
        jugadoresActivos = new HashMap<>();
        Collections.shuffle(cartasDisponibles);

        for (int i = 1; i <= 2; i++) {
            jugadoresActivos.put(i, true);
        }

        // ===== Panel del Board =====
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(600, 100));
        boardPanel.setBounds(350, 190, 600, 150); // Centrado horizontalmente
        add(boardPanel);
        
        mostrarSeleccionNivelBot();
        
        // ===== Panel para cartas del jugador 1 con bordes redondeados =====
        panelCartasJugador1 = new RoundedPanel(new Color(0x65, 0x43, 0x21), 20); // Color marrón y esquinas redondeadas
        panelCartasJugador1.setBounds(500, 60, 300, 100); // Posición superior centrada
        mesaPanel.add(panelCartasJugador1);

        // ===== Panel para cartas del jugador 2 con bordes redondeados =====
        panelCartasJugador2 = new RoundedPanel(new Color(0x65, 0x43, 0x21), 20); // Color marrón y esquinas redondeadas
        panelCartasJugador2.setBounds(500, 540, 300, 100); // Posición inferior centrada
        mesaPanel.add(panelCartasJugador2);
        

        // ===== Etiqueta del Bote =====
        boteLabel = new JLabel("Bote: $0.00");
        boteLabel.setFont(new Font("Arial", Font.BOLD, 20));
        boteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boteLabel.setForeground(Color.WHITE);
        boteLabel.setBounds(900, 250, 300, 50); // Encima del board
        mesaPanel.add(boteLabel);
        
        

        // ===== Paneles de los Jugadores =====
        int panelWidth = 200;
        int panelHeight = 141;

        int[][] playerPositions = {
            {551, 493},   // Jugador 1 (arriba-izquierda)
            {551, 10},  // Jugador 2 (abajo-derecha)
        };

        int cartasPorJugador = modalidad.equals("Omaha") ? 4 : 2;
        double saldoInicial = 1000.0;
        int idBot = 2;

        for (int i = 0; i < 2; i++) {
            String[] cartasJugador = seleccionarCartasAleatorias(cartasPorJugador);
            manosJugadores.put(i + 1, cartasJugador);

            final int jugadorId = i + 1;
            boolean esBot = (jugadorId == idBot);

            Jugador jugador = new Jugador(
                jugadorId,
                cartaImagenMap,
                cartasJugador,
                saldoInicial,
                () -> jugadoresActivos.put(jugadorId, false),
                this,
                esBot
            );

            jugador.setBounds(playerPositions[i][0], playerPositions[i][1], panelWidth, panelHeight);
            jugador.setBackground(new Color(0x65, 0x43, 0x21));
            panelesJugadores.put(jugadorId, jugador);
            //mesaPanel.add(jugador);
        }

        cont = 1;

        // Configuración del turno inicial
        Jugador bot = panelesJugadores.get(2);
        turnoActual = jugadorCiegaPequena;
        valorManoBot = ProbabilidadPoker.calcularValorManoBot(
            new ArrayList<>(),
            manosJugadores.get(bot.getId()),
            generarBarajaDisponible()
        );

        System.out.println("Valor de la mano del bot: " + valorManoBot);
        
        //Comentar si se desea ver las cartas del bot
        bot.ocultarCartas();
        
        pagarCiegas(ciegaActual);
        SwingUtilities.invokeLater(() -> ejecutarTurnoJugador(panelesJugadores.get(turnoActual)));
        
        for (Jugador jugador : panelesJugadores.values()) {
            mesaPanel.add(jugador);
            mesaPanel.setComponentZOrder(jugador, 0); // Traer al frente
        }
    }
    
 // Clase para paneles con bordes redondeados
    private static class RoundedPanel extends JPanel {
        private Color backgroundColor;
        private int cornerRadius;

        public RoundedPanel(Color backgroundColor, int cornerRadius) {
            this.backgroundColor = backgroundColor;
            this.cornerRadius = cornerRadius;
            setOpaque(false); // Hacer el fondo transparente para que solo se dibuje el rectángulo redondeado
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dibujar fondo con bordes redondeados
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            // Dibujar borde si es necesario
            g2d.setColor(Color.BLACK); // Cambiar color del borde si es necesario
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }

 // Clase para dibujar la mesa elíptica con los nuevos colores
    private static class MesaPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Fondo azul oscuro
            g2d.setColor(new Color(0x00, 0x33, 0x66));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Elipse de la mesa (color verde oscuro)
            g2d.setColor(new Color(0x3B, 0x7A, 0x57)); // Color hexadecimal 3B7A57
            g2d.fillOval(100, 50, getWidth() - 200, getHeight() - 100);

            // Borde de la elipse (color amarillo brillante)
            g2d.setColor(new Color(0xFF, 0xC3, 0x00)); // Color hexadecimal FFC300
            g2d.setStroke(new BasicStroke(5)); // Ancho del borde
            g2d.drawOval(100, 50, getWidth() - 200, getHeight() - 100);
        }
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
        
        Jugador bot = panelesJugadores.get(2);
        
        //Comentar si se desea ver las cartas del bot
        bot.ocultarCartas();
        
        valorManoBot = ProbabilidadPoker.calcularValorManoBot(new ArrayList<>(), manosJugadores.get(bot.getId()), generarBarajaDisponible());
        System.out.println("Valor de la mano del bot: " + valorManoBot);
        repaint();
    }

    private void inicializarCartasDisponibles() {
        cartasDisponibles = new ArrayList<>(cartaImagenMap.keySet());
    }
    
    private void ejecutarTurnoJugador(Jugador jugador) {
        String[] opciones = {"Check", "Call", "Raise", "Fold", "Cerrar Programa"};

        // Crear un diálogo personalizado
        JDialog dialog = new JDialog(this, "Turno de Apuesta", true);
        dialog.setUndecorated(true); // Eliminar barra de título
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Desactivar cierre con la cruz

     // Cambiar el fondo del contenido del diálogo
        dialog.getContentPane().setBackground(new Color(0x3B, 0x7A, 0x57)); // Mismo color que el tablero
        
        // Crear y configurar el JLabel
        JLabel label = new JLabel("Jugador " + jugador.getId() + ": ¿Qué deseas hacer?");
        label.setHorizontalAlignment(SwingConstants.CENTER); // Centrar horizontalmente
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar en el contenedor
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Añadir margen alrededor del texto
        label.setFont(new Font("Arial", Font.BOLD, 15)); // Fuente 'Arial', negrita, tamaño 15
        label.setForeground(Color.WHITE); // Cambiar el texto a blanco para mejor visibilidad

        // Crear un panel para los botones
        JPanel botonesPanel = new JPanel();
        botonesPanel.setBackground(new Color(0x3B, 0x7A, 0x57)); // Camuflar el fondo con el tablero
        botonesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Centrar botones y añadir espacio
        
        // Crear botones para opciones
        JButton[] botones = new JButton[opciones.length];
        for (int i = 0; i < opciones.length; i++) {
            botones[i] = new JButton(opciones[i]);
            botones[i].setFocusPainted(false); // Quitar el efecto de foco
            botones[i].setBackground(Color.DARK_GRAY); // Fondo oscuro para los botones
            botones[i].setForeground(Color.WHITE); // Texto blanco para contraste
            int eleccion = i; // Capturar índice para el switch
            botones[i].addActionListener(e -> {
                procesarEleccion(jugador, eleccion); // Llamar a la función de elección
                dialog.dispose(); // Cerrar el diálogo
            });
            botonesPanel.add(botones[i]);
        }


        // Agregar componentes al diálogo
        dialog.add(label);   // Añadir el JLabel
        dialog.add(botonesPanel); // Añadir los botones al panel

        dialog.pack();
        dialog.setLocationRelativeTo(this);  // Centrar el diálogo en la ventana principal
        dialog.setLocation(dialog.getX(), dialog.getY() + 100); // Desplazar el diálogo 75 píxeles hacia abajo
        dialog.setVisible(true);
    }


    // Método para procesar la elección
    private void procesarEleccion(Jugador jugador, int eleccion) {
        switch (eleccion) {
            case 0: // Check
                check(jugador);
                break;
            case 1: // Call
                ver(jugador);
                break;
            case 2: // Raise
                subir(jugador);
                break;
            case 3: // Fold
                foldear(jugador);
                break;
            case 4: // Cerrar Programa
                cerrarPrograma();
                break;
            default:
                break;
        }
    }
	
	 // Método para cerrar el programa mostrando un mensaje de agradecimiento
	 private void cerrarPrograma() {
	     // Mostrar un cuadro de diálogo con el mensaje de despedida
	     JOptionPane.showMessageDialog(
	         this, 
	         "¡Gracias por jugar!", 
	         "Despedida", 
	         JOptionPane.INFORMATION_MESSAGE
	     );
	
	     // Cerrar el programa
	     System.exit(0);
	 }
 
    private void check(Jugador jugador) {
    	double cantidadPorVer = calcularCantidadPorVer(jugador);
    	if(modoAllIn) {
    		mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
            ejecutarTurnoJugador(jugador);
        }
    	
        if (cantidadPorVer != 0) {
            mostrarMensaje("No puedes hacer 'check'. Hay apuestas activas.");
            ejecutarTurnoJugador(jugador);
        } else {
            if (jugador.estaEnJuego() && apuestaTotalJug1 == apuestaTotalJug2 && contadorTurno1 != 0) {
                avanzarFaseBoard();
            } 
            if(contadorTurno1 == 0) 
        		contadorTurno1 = 1;
            pasarAlSiguienteJugador();
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
                mostrarMensaje("Jugador " + jugador.getId() + " iguala con $" + cantidadPorVer);
                jugador.resetApuesta(); // Resetea la apuesta actual del jugador para reflejar la igualdad.
                if (jugador.estaEnJuego() && apuestaTotalJug1 == apuestaTotalJug2 && contadorTurno1 != 0 && !modoAllIn) {
                    avanzarFaseBoard();
                }
                	
            } else {
                double saldoRestante = jugador.getSaldo();
                jugador.reducirSaldo(saldoRestante);
                jugador.aumentarApuesta(saldoRestante);
                actualizarBote(saldoRestante);
                mostrarMensaje("Jugador " + jugador.getId() + " hace all-in con $" + saldoRestante);
            }
            apuestaActual = calcularApuestaMaxima(); // Ajustar la apuesta máxima al estado actual.
        } else {
            mostrarMensaje("Apuesta igualada, puedes hacer 'Check' o 'Raise'.");
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
        	if(contadorTurno1 == 0) 
        		contadorTurno1 = 1;
            pasarAlSiguienteJugador();
        }
    }

    private double calcularCantidadPorVer(Jugador jugador) {
        return Math.abs(apuestaTotalJug2 - apuestaTotalJug1);
    }
    
    private void subir(Jugador jugador) {
    	if(modoAllIn) {
    		mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
            ejecutarTurnoJugador(jugador);
        }
    	boolean aux = false;
        String cantidadStr = JOptionPane.showInputDialog("Introduce la cantidad para subir:");
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            mostrarMensaje("No se ingresó cantidad.");
            ejecutarTurnoJugador(jugador);
            return;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr.trim());

            if(jugador.getId() == 1)
            	if (apuestaTotalJug2 >= apuestaTotalJug1 + cantidad) {
                    mostrarMensaje("La cantidad debe ser mayor a la apuesta actual.");
                    ejecutarTurnoJugador(jugador);
                    return;
                }
        	else
        		if (apuestaTotalJug1 >= apuestaTotalJug2 + cantidad) {
                    mostrarMensaje("La cantidad debe ser mayor a la apuesta actual.");
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
                mostrarMensaje("Jugador " + jugador.getId() + " no puede apostar más dinero del que tiene.");
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
            	if(contadorTurno1 == 0) 
            		contadorTurno1 = 1; 
                pasarAlSiguienteJugador();
                
            }
            
        } catch (NumberFormatException e) {
            mostrarMensaje("Entrada no válida.");
            ejecutarTurnoJugador(jugador);
        }
    }

    
    private void foldear(Jugador jugador) {
        jugadoresActivos.put(jugador.getId(), false);
        mostrarMensaje("Jugador " + jugador.getId() + " se retira.");
        this.contadorTurno1 = 0;
        verificarGanador(); // Verificar si queda un solo jugador activo
        pasarAlSiguienteJugador();
    }
    
    
    private void registrarSubida(double cantidad) {
        apuestaActual = Math.max(apuestaActual, cantidad); // Mantiene la apuesta máxima actualizada.
        mostrarMensaje("Nueva subida registrada: $" + String.format("%.2f", cantidad));
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
        mostrarMensaje("Modo all-in activado. Las rondas avanzarán automáticamente.");
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
        valorManoBot = ProbabilidadPoker.calcularValorManoBot(cartasBoardActuales, manosJugadores.get(bot.getId()), generarBarajaDisponible());
        String accion = determinarAccionBot(bot);

        switch (accion) {
            case "Check":
                mostrarMensaje("El jugador 2 ha hecho check.");
                check(bot);
                break;
            case "Call":
                ver(bot);
                break;
            case "Raise":
            	double cantidadBot = calcularCantidad(bot);
                subirBot(bot, cantidadBot);
                break;
            case "Fold":
                foldear(bot);
                break;
        }
    }
    
    private double calcularCantidad(Jugador bot) {
    	double cantidadBot = 0;
    	//posiciones: 1(AllIn), 2(Subida baja), 3(Subida media), 4(Subida alta)
    	List<Integer> listaAux = obtenerListaAuxCantidad();
    	
    	if(valorManoBot > listaAux.get(0))
    		cantidadBot = bot.getSaldo();
    	else
    		if(valorManoBot < listaAux.get(1))
    			cantidadBot = Math.abs(apuestaTotalJug2 - apuestaTotalJug1) + 50;
    		else if(valorManoBot > listaAux.get(2) && valorManoBot <= listaAux.get(3))
    			cantidadBot = Math.abs(apuestaTotalJug2 - apuestaTotalJug1) + 100;
    		else if(valorManoBot > listaAux.get(3) && valorManoBot <= listaAux.get(0))
    			cantidadBot = Math.abs(apuestaTotalJug2 - apuestaTotalJug1) + 150;
    		else
    			cantidadBot = bot.getSaldo();
    	return cantidadBot;
	}

	private List<Integer> obtenerListaAuxCantidad() {
		//375, 325, 400 
		if (tipoDeBot == 1) {
            return Arrays.asList(600, 430, 485, 540);
        } else if (tipoDeBot == 2) {
            return Arrays.asList(500, 370, 415, 460);
        } else {
            return Arrays.asList(800, 500, 600, 700);
        }
	}

	private String determinarAccionBot(Jugador bot) {
		//posiciones: 1(AllIn), 2(Diferncia de Apuesta), 3(Raise), 4(Fold)
		List<Integer> listaAux = obtenerListaAuxAccion();
        
        if (modoAllIn) {
            return (valorManoBot > listaAux.get(0)) ? "Call" : "Fold";
        }

        double diferenciaApuesta = apuestaTotalJug1 - apuestaTotalJug2;

        if (diferenciaApuesta >= listaAux.get(1)) {
            return evaluarAccionConDiferenciaAlta(listaAux);
        }

        return evaluarAccionSinDiferenciaAlta(listaAux);
    }

    private List<Integer> obtenerListaAuxAccion() {
        if (tipoDeBot == 1) {
            return Arrays.asList(450, 400, 375, 280);
        } else if (tipoDeBot == 2) {
            return Arrays.asList(310, 500, 325, 270);
        } else {
            return Arrays.asList(500, 350, 400, 290);
        }
    }

    private String evaluarAccionConDiferenciaAlta(List<Integer> listaAux) {
        if (valorManoBot > listaAux.get(2) + 50) {
            return "Raise";
        } else if (valorManoBot < listaAux.get(3) + 20) {
            return "Fold";
        } else {
            return "Call";
        }
    }

    private String evaluarAccionSinDiferenciaAlta(List<Integer> listaAux) {
        if (valorManoBot > listaAux.get(2)) {
            return "Raise";
        } else if (valorManoBot < listaAux.get(3)) {
            return "Fold";
        } else {
            return (apuestaTotalJug1 != apuestaTotalJug2) ? "Call" : "Check";
        }
    }

    private void subirBot(Jugador bot, double cantidadSubida) {
    	if(modoAllIn) {
    		mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
            ejecutarTurnoJugador(bot);
        }
    	boolean aux = false;
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
                mostrarMensaje("El bot ha hecho raise de $ " + cantidadSubida);
            } else {
                // Caso de all-in si el bot no tiene suficiente saldo para cubrir la subida
                double saldoRestante = bot.getSaldo();
                bot.reducirSaldo(saldoRestante);
                bot.aumentarApuesta(saldoRestante);
                registrarSubida(saldoRestante); // Registrar el all-in como la nueva apuesta
                actualizarBote(saldoRestante);
                apuestaActual = saldoRestante; // Actualizar apuesta actual al all-in
                mostrarMensaje("Bot hace all-in con $ " + saldoRestante);
            }
        } else {
            mostrarMensaje("El bot no puede subir por debajo de la apuesta actual.");
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
        	if(contadorTurno1 == 0) 
        		contadorTurno1 = 1;
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
                    mostrarMensaje("El jugador " + jugador.getId() + " ha sido eliminado.");
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
            mostrarMensaje("¡El jugador " + ganadorId + " ha ganado $" + premioPorJugador + " en un empate!");
        }
    }

    private void mostrarGanador(int jugadorId) {
        Jugador ganador = panelesJugadores.get(jugadorId);
        double premio = boteTotal;
        ganador.ganarApuesta(premio);
        Jugador bot = panelesJugadores.get(2);
        bot.descubrirCartas(manosJugadores.get(2));
        mostrarMensaje("¡El jugador " + jugadorId + " ha ganado $" + premio + " con la mejor mano!");
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
        
        ciegaActual += 25; 
        
        if(jugadorCiegaPequena == 1) {
        	apuestaTotalJug1 = ciegaActual;
    		apuestaTotalJug2 = ciegaActual * 2;
        }else {
        	apuestaTotalJug1 = ciegaActual * 2;
    		apuestaTotalJug2 = ciegaActual;
        }
        
    	this.contadorTurno1 = 0;
    	
        pagarCiegas(ciegaActual);
        apuestaActual = ciegaActual;

        // Verificar si un jugador se quedó sin saldo
        List<Integer> jugadoresConSaldo = new ArrayList<>();
        for (Jugador jugador : panelesJugadores.values()) {
            if (jugador.getSaldo() > 0) {
                jugador.resetApuesta(); // Método en Jugador para reiniciar la apuesta
                jugadoresConSaldo.add(jugador.getId());
            } else {
                jugadoresActivos.put(jugador.getId(), false);
                mostrarMensaje("El jugador " + jugador.getId() + " ha sido eliminado.");
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
            System.out.println("Valor de la mano del bot: " + valorManoBot);
            pasarAlJugadorBB();
        } else if (faseBoard == 3) {
            actualizarCartasBoard(4);
            faseBoard = 4;
            System.out.println("Valor de la mano del bot: " + valorManoBot);
            pasarAlJugadorBB();
        } else if (faseBoard == 4) {
            actualizarCartasBoard(5);
            faseBoard = 5;
            System.out.println("Valor de la mano del bot: " + valorManoBot);
            pasarAlJugadorBB();
        } else {
        	faseBoard = 0;
        	verificarGanador();
        }
    }
    
    private void pasarAlJugadorBB() {
    	turnoActual = (turnoActual == 2) ? 1 : 2;
    	contadorTurno1 = 0;
    	
    	Jugador jugadorActual = panelesJugadores.get(turnoActual);
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
    private void pagarCiegas(double ciegaActual) {
        Jugador jugadorCiegaPequenaObj = panelesJugadores.get(jugadorCiegaPequena);
        Jugador jugadorCiegaGrandeObj = panelesJugadores.get(jugadorCiegaGrande);

        // Ciega pequeña
        if (jugadorCiegaPequenaObj.getSaldo() >= ciegaActual) {
            jugadorCiegaPequenaObj.reducirSaldo(ciegaActual);
            actualizarBote(ciegaActual);
        } else {
            double saldoRestante = jugadorCiegaPequenaObj.getSaldo();
            jugadorCiegaPequenaObj.reducirSaldo(saldoRestante);
            actualizarBote(saldoRestante);
            mostrarMensaje("Jugador " + jugadorCiegaPequena + " hace all-in con $" + saldoRestante);
        }

        // Ciega grande
        if (jugadorCiegaGrandeObj.getSaldo() >= ciegaActual * 2) {
            jugadorCiegaGrandeObj.reducirSaldo(ciegaActual * 2);
            actualizarBote(ciegaActual * 2);
        } else {
            double saldoRestante = jugadorCiegaGrandeObj.getSaldo();
            jugadorCiegaGrandeObj.reducirSaldo(saldoRestante);
            actualizarBote(saldoRestante);
            mostrarMensaje("Jugador " + jugadorCiegaGrande + " hace all-in con $" + saldoRestante);
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
        Jugador bot = panelesJugadores.get(2);
        valorManoBot = ProbabilidadPoker.calcularValorManoBot(new ArrayList<>(), manosJugadores.get(bot.getId()), generarBarajaDisponible());
    }

    private List<String> generarBarajaDisponible() {
        List<String> baraja = new ArrayList<>(cartasDisponibles);
        baraja.removeAll(cartasBoardActuales);
        return baraja;
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
    
    private void mostrarSeleccionNivelBot() {
        // Crear el diálogo
        JDialog dialog = new JDialog(this, "Seleccionar Nivel del Bot", true);
        dialog.setSize(700, 150);
        dialog.setLayout(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Etiqueta descriptiva
        JLabel label = new JLabel("Selecciona el nivel del Bot:");
        label.setBounds(50, 10, 200, 30);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label);

        // Botones para seleccionar el nivel del bot
        JButton botonBot1 = new JButton("Bot 1: Bot Normal");
        JButton botonBot2 = new JButton("Bot 2: Bot Agresivo");
        JButton botonBot3 = new JButton("Bot 3: Bot Conservador");

        // Posicionar los botones en fila
        botonBot1.setBounds(50, 50, 150, 30);
        botonBot2.setBounds(250, 50, 150, 30);
        botonBot3.setBounds(450, 50, 175, 30);

        // Acciones para cada botón
        botonBot1.addActionListener(e -> {
            seleccionarNivelBot(1);
            dialog.dispose();
        });
        botonBot2.addActionListener(e -> {
            seleccionarNivelBot(2);
            dialog.dispose();
        });
        botonBot3.addActionListener(e -> {
            seleccionarNivelBot(3);
            dialog.dispose();
        });

        // Añadir los botones al diálogo
        dialog.add(botonBot1);
        dialog.add(botonBot2);
        dialog.add(botonBot3);

        // Centrar el diálogo en la ventana principal
        dialog.setLocationRelativeTo(this);

        // Mostrar el diálogo
        dialog.setVisible(true);
    }

    private void seleccionarNivelBot(int nivel) {
        // Ajusta las configuraciones del bot en función del nivel seleccionado
        // Por ejemplo:
        switch (nivel) {
            case 1:
                this.tipoDeBot = 1;
                break;
            case 2:
                this.tipoDeBot = 2;
                break;
            case 3:
                this.tipoDeBot = 3;
                break;
            default:
                throw new IllegalArgumentException("Nivel no válido: " + nivel);
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
        Jugador bot = panelesJugadores.get(2);
        valorManoBot = ProbabilidadPoker.calcularValorManoBot(new ArrayList<>(), manosJugadores.get(bot.getId()), generarBarajaDisponible());
        repaint(); // Actualizar la interfaz gráfica
    }
    
    public void mostrarMensaje(String mensaje) {
        // Crear un diálogo personalizado para mostrar el mensaje
        JDialog dialog = new JDialog(this, "Mensaje", true);
        dialog.setSize(400, 100); // Tamaño del diálogo
        dialog.setLayout(new BorderLayout());

        // Etiqueta con el mensaje
        JLabel label = new JLabel(mensaje, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        dialog.add(label, BorderLayout.CENTER);

        // Botón para cerrar el diálogo
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Centrar y desplazar el diálogo hacia abajo
        dialog.setLocationRelativeTo(this);
        int desplazamientoY = 50;
        dialog.setLocation(dialog.getX(), dialog.getY() + desplazamientoY);

        // Mostrar el diálogo
        dialog.setVisible(true);
    }

    
}

