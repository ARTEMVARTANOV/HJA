package Default;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LogicaRonda {
	private MesaPoker mesaPoker; 
	public LogicaRonda(MesaPoker mesaPoker) {
		this.mesaPoker = mesaPoker;
	}
	
	public void ejecutarTurnoJugador(Jugador jugador) {
        String[] opciones = {"Check", "Call", "Raise", "Fold", "Cerrar Programa"};

        // Crear un diálogo personalizado
        JDialog dialog = new JDialog(mesaPoker, "Turno de Apuesta", true);
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
        dialog.setLocationRelativeTo(mesaPoker);  // Centrar el diálogo en la ventana principal
        dialog.setLocation(dialog.getX(), dialog.getY() + 100); // Desplazar el diálogo 75 píxeles hacia abajo
        dialog.setVisible(true);
    }
	
	// Método para procesar la elección
    private void procesarEleccion(Jugador jugador, int eleccion) {
        switch (eleccion) {
            case 0: // Check
            	jugador.check(jugador);
                break;
            case 1: // Call
            	jugador.ver(jugador);
                break;
            case 2: // Raise
            	subirPanel(jugador);
                break;
            case 3: // Fold
            	jugador.foldear(jugador);
                break;
            case 4: // Cerrar Programa
            	mesaPoker.cerrarPrograma();
                break;
            default:
                break;
        }
    }
    
    private void subirPanel(Jugador jugador) {
        if (mesaPoker.getAllIn()) {
        	mesaPoker.mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
        	ejecutarTurnoJugador(jugador);
            return;
        }

        // Crear un diálogo para ingresar la cantidad
        JDialog dialog = new JDialog(mesaPoker, "Subir Apuesta", true);
        dialog.setSize(300, 120);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Etiqueta descriptiva
        JLabel label = new JLabel("Introduce la cantidad para subir:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label, BorderLayout.NORTH);

        // Campo de texto para ingresar la cantidad
        JTextField textField = new JTextField();
        dialog.add(textField, BorderLayout.CENTER);

        // Botón para confirmar
        JButton confirmButton = new JButton("Confirmar");
        confirmButton.addActionListener(e -> {
            String cantidadStr = textField.getText();
            if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            	mesaPoker.mostrarMensaje("No se ingresó cantidad.");
                dialog.dispose();
                ejecutarTurnoJugador(jugador);
                return;
            }
            else {
            	dialog.dispose();
            	jugador.subirLogica(cantidadStr, jugador);
            }
        });

        // Botón para cancelar
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> {
            dialog.dispose();
            ejecutarTurnoJugador(jugador);
        });

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Centrar el diálogo en la ventana principal
        dialog.setLocationRelativeTo(mesaPoker);
        dialog.setVisible(true);
    }
    
    public void pasarAlSiguienteJugador() {
    	//System.out.println("Jugador: " + mesaPoker.getApuestaTotalJug1() + "Bot: " + mesaPoker.getApuestaTotalJug2());
    	List<Jugador> jugadores = new ArrayList<>();
    	Map<Integer, Jugador> panelesJugadores = mesaPoker.getPanelesJugadores();
    	for (Jugador jugador : panelesJugadores.values()) {
    		jugadores.add(jugador);
        }
    	
    	double saldoJug1 = jugadores.get(0).getSaldo();
    	double saldoJug2 = jugadores.get(1).getSaldo();
    	if(saldoJug1 == 0.0) 
    		mesaPoker.mostrarMensaje("El jugador 1 hizo All-In");
    	else if(saldoJug2 == 0.0) 
    		mesaPoker.mostrarMensaje("El bot hizo All-In");
    	
    	if(mesaPoker.getContadorTurno1() != 0) 
    		mesaPoker.setTurnoActual((mesaPoker.getTurnoActual() == 2) ? 1 : 2);
    	else
    		mesaPoker.setContadorTurno1(1);
    	Jugador jugadorActual = panelesJugadores.get(mesaPoker.getTurnoActual());
        if (jugadorActual.esBot()) {
            ejecutarTurnoBot(jugadorActual);
        } else {
        	ejecutarTurnoJugador(jugadorActual);
        }
    }

    public void ejecutarTurnoBot(Jugador bot) {
    	// Lógica del bot
    	Map<Integer, String[]> manosJugadores = mesaPoker.getManosJugadores();
    	mesaPoker.setValorManoBot(ProbabilidadPoker.calcularValorManoBot(mesaPoker.getCartasBoardActuales(), 
    			manosJugadores.get(bot.getId()), mesaPoker.generarBarajaDisponible()));
    	String accion = determinarAccionBot(bot);

        switch (accion) {
            case "Check":
            	mesaPoker.mostrarMensaje("El jugador 2 ha hecho check.");
                bot.check(bot);
                break;
            case "Call":
            	bot.ver(bot);
                break;
            case "Raise":
            	double cantidadBot = calcularCantidad(bot);
                bot.subirBot(bot, cantidadBot);
                break;
            case "Fold":
            	bot.foldear(bot);
                break;
        }
    }
    
    private double calcularCantidad(Jugador bot) {
    	int multiplicador = 1;
    
    	if(mesaPoker.getTipoBot() == 1)  
    		multiplicador += 2;
    	else if(mesaPoker.getTipoBot() == 2)  
    		multiplicador += 3;
    	else if(mesaPoker.getTipoBot() == 3)
    		multiplicador += 1;
    	
    	double cantidadBot = 0;
    	if(mesaPoker.getApuestaTotalJug1() - mesaPoker.getApuestaTotalJug2() > 0 )
    		cantidadBot = mesaPoker.getApuestaTotalJug1() - mesaPoker.getApuestaTotalJug2() + 200 * multiplicador;
    	else
    		cantidadBot = 200 * multiplicador;
    	
    	//posiciones: 1(AllIn), 2(Subida baja), 3(Subida media), 4(Subida alta), 5(Random All-In)
    	List<Integer> listaAux = obtenerListaAuxCantidad();
    	int random = (int) (Math.random() * 101);
    	
    	if(mesaPoker.getValorManoBot() > listaAux.get(0) || random <= listaAux.get(4))
    		cantidadBot = bot.getSaldo();
    	else if(mesaPoker.getValorManoBot() > listaAux.get(1) && mesaPoker.getValorManoBot() <= listaAux.get(2))
    		cantidadBot = Math.abs(mesaPoker.getApuestaTotalJug2() - mesaPoker.getApuestaTotalJug1()) + 300 * multiplicador;
    	else if(mesaPoker.getValorManoBot() > listaAux.get(2) && mesaPoker.getValorManoBot() <= listaAux.get(3))
    		cantidadBot = Math.abs(mesaPoker.getApuestaTotalJug2() - mesaPoker.getApuestaTotalJug1()) + 400 * multiplicador;
    	else if(mesaPoker.getValorManoBot() > listaAux.get(3))
    		cantidadBot = Math.abs(mesaPoker.getApuestaTotalJug2() - mesaPoker.getApuestaTotalJug1()) + 500 * multiplicador;
    	
    	return cantidadBot;
	}

	private List<Integer> obtenerListaAuxCantidad() {
		if (mesaPoker.getTipoBot() == 1) {
            return Arrays.asList(600, 430, 485, 540, 3);
        } else if (mesaPoker.getTipoBot() == 2) {
            return Arrays.asList(450, 370, 415, 460, 5);
        } else {
            return Arrays.asList(800, 500, 600, 700, 1);
        }
	}

	private String determinarAccionBot(Jugador bot) {
		//posiciones: 1(AllIn), 2(Diferncia de Apuesta), 3(Raise), 4(Fold), 5(Random All-In)
		List<Integer> listaAux = obtenerListaAuxAccion();
		int random = (int) (Math.random() * 101);
		
        if (mesaPoker.getAllIn() || random <= listaAux.get(4)) {
            return (mesaPoker.getValorManoBot() > listaAux.get(0)) ? "Call" : "Fold";
        }

        double diferenciaApuesta = mesaPoker.getApuestaTotalJug1() - mesaPoker.getApuestaTotalJug2();

        if (diferenciaApuesta >= listaAux.get(1)) {
            return evaluarAccionConDiferenciaAlta(listaAux);
        }

        return evaluarAccionSinDiferenciaAlta(listaAux);
    }

    private List<Integer> obtenerListaAuxAccion() {
        if (mesaPoker.getTipoBot() == 1) {
            return Arrays.asList(420, 800, 360, 320, 5);
        } else if (mesaPoker.getTipoBot() == 2) {
            return Arrays.asList(380, 1000, 350, 310, 10);
        } else {
            return Arrays.asList(480, 600, 370, 335, 2);
        }
    }

    private String evaluarAccionConDiferenciaAlta(List<Integer> listaAux) {
        if (mesaPoker.getValorManoBot() > listaAux.get(2) + 50) {
            return "Raise";
        } else if (mesaPoker.getValorManoBot() < listaAux.get(3) + 20) {
            return "Fold";
        } else {
        	return (mesaPoker.getApuestaTotalJug1() != mesaPoker.getApuestaTotalJug2()) ? "Call" : "Check";
        }
    }

    private String evaluarAccionSinDiferenciaAlta(List<Integer> listaAux) {
        if (mesaPoker.getValorManoBot() > listaAux.get(2)) {
            return "Raise";
        } else if (mesaPoker.getValorManoBot() < listaAux.get(3)) {
            return "Fold";
        } else {
            return (mesaPoker.getApuestaTotalJug1() != mesaPoker.getApuestaTotalJug2()) ? "Call" : "Check";
        }
    }
    
    public double calcularApuestaMaxima() {
        double max = 0;
        Map<Integer, Jugador> panelesJugadores = mesaPoker.getPanelesJugadores();
        for (Jugador jugador : panelesJugadores.values()) {
            if (jugador.estaEnJuego() && jugador.getApuestaActual() > max) {
                max = jugador.getApuestaActual();
            }
        }
        return max;
    }
    
    private void avanzarHastaRiver() {
        while (!mesaPoker.boardEstaEnRiver()) {
        	avanzarFaseBoard(true);
        }
        verificarGanador();
        mesaPoker.setAllIn(false);
    }
    
    public void iniciarModoAllIn() {
    	mesaPoker.mostrarMensaje("Modo all-in activado. Las rondas avanzarán automáticamente.");
    	avanzarHastaRiver();
    }
    
    public boolean algunoAllIn() {
    	int cont = 0;
    	Map<Integer, Jugador> panelesJugadores = mesaPoker.getPanelesJugadores();
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

    public void verificarGanador() {
        List<Integer> jugadoresRestantes = new ArrayList<>();
        Map<Integer, Boolean> jugadoresActivos = mesaPoker.getJugadoresActivos();
        for (Map.Entry<Integer, Boolean> entry : jugadoresActivos.entrySet()) {
            if (entry.getValue()) {
                jugadoresRestantes.add(entry.getKey());
            }
        }

        if (jugadoresRestantes.size() == 1) {
            int ganador = jugadoresRestantes.get(0);
            mesaPoker.mostrarGanador(ganador);
            mesaPoker.reiniciarRonda();
        } else if (jugadoresRestantes.size() == 2) {
            int jugador1 = jugadoresRestantes.get(0);
            int jugador2 = jugadoresRestantes.get(1);

            // Validar cartasBoardActuales
            if (mesaPoker.getCartasBoardActuales() == null || mesaPoker.getCartasBoardActuales().isEmpty()) {
                throw new IllegalStateException("El board está vacío.");
            }
            
            // Validar manos de jugadores
            Map<Integer, String[]> manosJugadores = mesaPoker.getManosJugadores();
            String[] cartasJugador1 = manosJugadores.get(jugador1);
            String[] cartasJugador2 = manosJugadores.get(jugador2);

            if (cartasJugador1 == null || cartasJugador1.length == 0 ||
                cartasJugador2 == null || cartasJugador2.length == 0) {
                throw new IllegalStateException("Las cartas de los jugadores están vacías.");
            }
            
            LogicaManoPoker logicaJugador = new LogicaManoPoker(cartasJugador1, mesaPoker.getCartasBoardActuales());
            LogicaManoPoker logicaBot = new LogicaManoPoker(cartasJugador2, mesaPoker.getCartasBoardActuales());

            Map<Integer, ManoPoker.HandRank> mejoresRanks = new HashMap<>();
            Map<Integer, String> mejoresManos = new HashMap<>();

            mejoresRanks.put(1, logicaJugador.getMejorRank());
            mejoresManos.put(1, logicaJugador.getMejorMano());
            mejoresRanks.put(2, logicaBot.getMejorRank());
            mejoresManos.put(2, logicaBot.getMejorMano());

            List<Integer> ganadores = ProbabilidadPoker.determinarGanador(mejoresManos, mejoresRanks);

            if (ganadores.size() == 1) {
            	mesaPoker.mostrarGanador(ganadores.get(0));
            } else {
            	mesaPoker.repartirBote(ganadores);
            }
            // Verificar si un jugador se quedó sin saldo
            List<Integer> jugadoresConSaldo = new ArrayList<>();
            int contConSaldo = 0;
            Map<Integer, Jugador> panelesJugadores = mesaPoker.getPanelesJugadores();
            for (Jugador jugador : panelesJugadores.values()) {
                if (jugador.getSaldo() > 0) {
                	contConSaldo += 1;
                	jugadoresConSaldo.add(jugador.getId());
                } else {
                	mesaPoker.mostrarMensaje("El jugador " + jugador.getId() + " ha sido eliminado.");
                }
            }
            if (contConSaldo == 2)
            	mesaPoker.reiniciarRonda();
            else {
            	// Si solo queda un jugador con saldo, declarar ganador final y salir del método
                if (contConSaldo == 1) {
                    if (mesaPoker.mostrarGanadorFinal(jugadoresConSaldo.get(0))) {
                        System.exit(0); // Terminar la aplicación si el método retorna true
                    }
                }
            	
            }
        }
    }
    
    private void pasarAlJugadorBB() {
    	mesaPoker.setTurnoActual((mesaPoker.getTurnoActual() == 2) ? 1 : 2);
    	mesaPoker.setContadorTurno1(0);
    	
    	Map<Integer, Jugador> panelesJugadores = mesaPoker.getPanelesJugadores();
    	Jugador jugadorActual = panelesJugadores.get(mesaPoker.getTurnoActual());
        if (jugadorActual.esBot()) {
        	ejecutarTurnoBot(jugadorActual);
        } else {
        	ejecutarTurnoJugador(jugadorActual);
        }     
	}
  
    public void avanzarFaseBoard(boolean irAlRiver) {
        if (mesaPoker.getFaseBoard() == 0) {
        	mesaPoker.actualizarCartasBoard(3);
        	mesaPoker.setFaseBoard(3);
            if(!irAlRiver)
            	pasarAlJugadorBB();
        } else if (mesaPoker.getFaseBoard() == 3) {
        	mesaPoker.actualizarCartasBoard(4);
        	mesaPoker.setFaseBoard(4);
            if(!irAlRiver)
            	pasarAlJugadorBB();
        } else if (mesaPoker.getFaseBoard() == 4) {
        	mesaPoker.actualizarCartasBoard(5);
            mesaPoker.setFaseBoard(5);
            if(!irAlRiver)
            	pasarAlJugadorBB();
        } else {
        	mesaPoker.setFaseBoard(0);
        	verificarGanador();
        }
    }

}
