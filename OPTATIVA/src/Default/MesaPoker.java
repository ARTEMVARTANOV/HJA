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
    private Map<Integer, String[]> manosJugadores;
    private Map<Integer, Jugador> panelesJugadores;
    private Map<Integer, Boolean> jugadoresActivos;
    private BoardPanel boardPanel;
    private JLabel boteLabel;
    
    //Variables privadas para la logica del juego
    private LogicaRonda logicaRonda = new LogicaRonda(this);
    private boolean modoAllIn = false;
    private int faseBoard = 0;
    private int jugadorCiegaPequena = 1; // El jugador que paga la ciega pequeña
    private int turnoActual; // Índice del jugador actual
    private int contadorTurno1 = 0;
	private int cont = 0;
	private double apuestaActual = 100;
    private double boteTotal = 0.0;
    private double apuestaTotalJug1 = 100;
    private double apuestaTotalJug2= 200;
	private double ciegaActual = 100;
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

        int cartasPorJugador = 2;
        double saldoInicial = 20000.0;
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
        
        //Comentar si se desea ver las cartas del bot
        bot.ocultarCartas();
        
        pagarCiegas(ciegaActual);
        SwingUtilities.invokeLater(() -> logicaRonda.ejecutarTurnoJugador(panelesJugadores.get(turnoActual)));
        
        for (Jugador jugador : panelesJugadores.values()) {
            mesaPanel.add(jugador);
            mesaPanel.setComponentZOrder(jugador, 0); // Traer al frente
        }
    }
    
    //Definimos geters y seters
	public boolean getAllIn() {
		return modoAllIn;
	}

	public double getApuestaTotalJug1() {
		return apuestaTotalJug1;
	}

	public double getApuestaTotalJug2() {
		return apuestaTotalJug2;
	}
	
	public double getValorManoBot() {
		return valorManoBot;
	}
	
	public double getApuestaActual() {
		return apuestaActual;
	}
	
	public int getContadorTurno1() {
		return contadorTurno1;
	}
	
	public int getFaseBoard() {
		return faseBoard;
	}
	
	public int getTipoBot() {
		return tipoDeBot;
	}
	
	public int getTurnoActual() {
		return turnoActual;
	}
	
	public Map<Integer, String[]> getManosJugadores() {
		return manosJugadores;
	}
	
	public Map<Integer, Boolean> getJugadoresActivos() {
		return jugadoresActivos;
	}
	
	public List<String> getCartasBoardActuales() {
		return cartasBoardActuales;
	}
	
	public Map<Integer, Jugador> getPanelesJugadores() {
		return panelesJugadores;
	}
	
	public void setTurnoActual(int i) {
		turnoActual = i;
	}
	
	public void setContadorTurno1(int i) {
		contadorTurno1 = i;
	}
	
	public void setApuestaActual(double d) {
		apuestaActual = d;
	}
	
	public void setValorManoBot(double d) {
		valorManoBot = d;
	}

	public void setApuestaTotalJug1(double d) {
		apuestaTotalJug1 = d;
	}

	public void setApuestaTotalJug2(double d) {
		apuestaTotalJug2 = d;
	}

	public void setAllIn(boolean b) {
		modoAllIn = b; 
	}
	
	public void setJugadoresActivos(Jugador jugador, boolean b) {
		jugadoresActivos.put(jugador.getId(), b);
	}
    
	public void setFaseBoard(int i) {
		faseBoard = i;
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

        int cartasPorJugador = 2;
        
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
        repaint();
    }

    private void inicializarCartasDisponibles() {
        cartasDisponibles = new ArrayList<>(cartaImagenMap.keySet());
    }
	
	 // Método para cerrar el programa mostrando un mensaje de agradecimiento
	 public void cerrarPrograma() {
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
	 
    public boolean boardEstaEnRiver() {
        // Suponiendo que tienes un contador de fases del board
        return faseBoard == 5;
    }

    public void repartirBote(List<Integer> ganadores) {
        double premioPorJugador = boteTotal / ganadores.size();
        for (int ganadorId : ganadores) {
            Jugador ganador = panelesJugadores.get(ganadorId);
            ganador.ganarApuesta(premioPorJugador);
            mostrarMensaje("¡El jugador " + ganadorId + " ha ganado $" + premioPorJugador + " en un empate!");
        }
    }

    public void mostrarGanador(int jugadorId) {
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
    

    public void reiniciarRonda() { 	
        // Reiniciar el bote
        boteTotal = 0.0;
        boteLabel.setText("Bote: $0.00");
        
        this.ciegaActual += 50;
        if(jugadorCiegaPequena == 1) {
        	apuestaTotalJug1 = this.ciegaActual;
    		apuestaTotalJug2 = this.ciegaActual * 2;
    		jugadorCiegaPequena = 2;
        }else {
        	apuestaTotalJug1 = this.ciegaActual * 2;
    		apuestaTotalJug2 = this.ciegaActual;
    		jugadorCiegaPequena = 1;
        }

    	this.contadorTurno1 = 0;
    	this.faseBoard = 0;
    	this.modoAllIn = false;
    	
        pagarCiegas(ciegaActual);

        this.turnoActual = jugadorCiegaPequena;
        
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

    public boolean mostrarGanadorFinal(int jugadorId) {
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
    
    // Método para pagar las ciegas
    private void pagarCiegas(double ciegaActual) {
        Jugador jugador = panelesJugadores.get(1);
        Jugador bot = panelesJugadores.get(2);

        if (jugador.getSaldo() >= apuestaTotalJug1) {
        	jugador.reducirSaldo(apuestaTotalJug1);
            actualizarBote(apuestaTotalJug1);
        } else {
            double saldoRestante = jugador.getSaldo();
            jugador.reducirSaldo(saldoRestante);
            actualizarBote(saldoRestante);
            mostrarMensaje("Jugador " + 1 + " hace all-in con $" + saldoRestante);
        }

        if (bot.getSaldo() >= apuestaTotalJug2) {
        	bot.reducirSaldo(apuestaTotalJug2);
            actualizarBote(apuestaTotalJug2);
        } else {
            double saldoRestante = bot.getSaldo();
            bot.reducirSaldo(saldoRestante);
            actualizarBote(saldoRestante);
            mostrarMensaje("Jugador " + 2 + " hace all-in con $" + saldoRestante);
        }
    }


    public void actualizarCartasBoard(int numCartas) {
        while (cartasBoardActuales.size() < numCartas && !cartasDisponibles.isEmpty()) {
            String cartaNueva = cartasDisponibles.remove(0);
            cartasBoardActuales.add(cartaNueva);
        }

        boardPanel.mostrarCartas(cartasBoardActuales, cartaImagenMap);
    }

    public List<String> generarBarajaDisponible() {
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
            int cartasPorJugador = 2;
            String[] cartasIniciales = seleccionarCartasAleatorias(cartasPorJugador);

            // Actualizar las cartas del jugador y reiniciar su estado
            manosJugadores.put(jugadorId, cartasIniciales);
            jugador.reiniciarJugador(cartasIniciales);
        }
        
        for (int i = 1; i <= 2; i++) {
            jugadoresActivos.put(i, true);
        }

        repaint(); // Actualizar la interfaz gráfica
    }
    
    public void mostrarMensaje(String mensaje) {
        // Crear un diálogo personalizado para mostrar el mensaje
        JDialog dialog = new JDialog(this, "Mensaje", true);
        dialog.setSize(430, 100); // Tamaño del diálogo
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

