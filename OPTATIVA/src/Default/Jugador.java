package Default;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Jugador extends JPanel {
    private final JLabel labelSaldo;
    private final CartaPanel[] cartaPanels = new CartaPanel[4];
    private final int numJugador;
    private double saldo;
    private double apuestaActual;
    private boolean enJuego = true;
    private final Map<String, String> cartaImagenMap;
    private boolean esBot;
	private MesaPoker mesaPoker;
	private LogicaRonda logicaRonda;

    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap, String[] cartasIniciales, double saldoInicial, Runnable onFoldCallback, MesaPoker mesaPoker, boolean esBot) {
        this(numeroJugador, cartaImagenMap, onFoldCallback, mesaPoker, saldoInicial, esBot);
        reiniciarCartas(cartasIniciales);
    }

    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap, Runnable onFoldCallback, MesaPoker mesaPoker, double saldoInicial, boolean esBot) {
        this.numJugador = numeroJugador;
        this.mesaPoker = mesaPoker;
        this.cartaImagenMap = cartaImagenMap;
        this.saldo = saldoInicial;
        this.apuestaActual = 0;
        this.esBot = esBot;
        this.logicaRonda = new LogicaRonda(mesaPoker);

        setPreferredSize(new Dimension(200, 400));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0x65, 0x43, 0x21)); // Fondo color marrón
        setOpaque(true); // Asegúrate de que el fondo sea visible

        // Panel para la mitad superior con fondo gris
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(0x65, 0x43, 0x21));  // Fondo gris
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.add(crearEtiqueta("Jugador " + numeroJugador + ":"));
        labelSaldo = crearEtiqueta("Saldo: 1000$");
        panelSuperior.add(labelSaldo);

        panelSuperior.setPreferredSize(new Dimension(200, 90));
        
        // Panel para la mitad inferior (cartas y entrada)
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setPreferredSize(new Dimension(200, 250));  // 150 de altura para la mitad inferior
        panelInferior.setBackground(new Color(0x65, 0x43, 0x21));
        panelInferior.add(crearPanelCartas());

        // Agregar ambos paneles al panel principal
        add(panelSuperior);
        add(panelInferior);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setAlignmentX(CENTER_ALIGNMENT);
        etiqueta.setForeground(Color.WHITE);
        return etiqueta;
    }

    private JPanel crearPanelCartas() {
        JPanel panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelCartas.setBackground(new Color(0x65, 0x43, 0x21));
        
        for (int i = 0; i < cartaPanels.length; i++) {
            cartaPanels[i] = new CartaPanel();
            panelCartas.add(cartaPanels[i]);
        }
        
        if(this.esBot == true) {
        	ocultarCartas();
        }
        
        return panelCartas;
    }

    public boolean estaEnJuego() {
        return enJuego;
    }
    
    public double getSaldo() {
        return saldo;
    }

    public double getApuestaActual() {
        return apuestaActual;
    }
    
    public double setApuestaActual(double cantidad) {
        return apuestaActual = cantidad;
    }
    
    public void aumentarApuesta(double cantidad) {
        this.apuestaActual += cantidad;
    }
    
    public boolean esBot() {
        return esBot;
    }

    public void ganarApuesta(double cantidad) {
        saldo += cantidad;
        actualizarSaldo();
    }
    
    public void reducirSaldo(double cantidad) {
        saldo -= cantidad;
        actualizarSaldo();
    }

    public void resetApuesta() {
        apuestaActual = 0;
    }

    private void actualizarSaldo() {
        labelSaldo.setText(String.format("Saldo: $%.2f", saldo));
    }
    
    public int getId() {
        return numJugador;
    }
   
    public void reiniciarCartas(String[] cartas) {
    	eliminarTodasLasCartas();
        for (int i = 0; i < cartas.length && i < cartaPanels.length; i++) {
            String ruta = cartaImagenMap.get(cartas[i]);
            if (ruta != null) {
                cartaPanels[i].setImage(ruta);
                cartaPanels[i].setVisible(true);
            }
        }
        revalidate();
        repaint();
    }

    public void eliminarTodasLasCartas() {
        for (CartaPanel panel : cartaPanels) {
            panel.setVisible(false);
        }
    }

    
    public void descubrirCartas(String[] cartas) {
    	for (int i = 0; i < cartas.length && i < cartaPanels.length; i++) {
            String ruta = cartaImagenMap.get(cartas[i]);
            if (ruta != null) {
                cartaPanels[i].setImage(ruta);
                cartaPanels[i].setVisible(true);
            }
        }
        revalidate();
        repaint();	
	}

    public void ocultarCartas() {
    	for (int i = 0; i < 2 && i < cartaPanels.length; i++) {
            String ruta = "images/cartaAlReves.png";
            if (ruta != null) {
                cartaPanels[i].setImage(ruta);
                cartaPanels[i].setVisible(true);
            }
        }
        revalidate();
        repaint();
	}

	public void reiniciarJugador(String[] cartasIniciales) {
        // Restablecer el estado del jugador
        enJuego = true;
        resetApuesta();
        //labelProbabilidad.setText("Probabilidad: 0%"); // Reiniciar la etiqueta de probabilidad
        
        actualizarSaldo();
        // Revalidar y repintar para actualizar la interfaz
        revalidate();
        repaint();
    }
	
	public void check(Jugador jugador) {
    	double cantidadPorVer = calcularCantidadPorVer(jugador);
    	if(mesaPoker.getAllIn()) {
    		mesaPoker.mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
    		logicaRonda.ejecutarTurnoJugador(jugador);
        }
    	
        if (cantidadPorVer != 0) {
        	mesaPoker.mostrarMensaje("No puedes hacer 'check'. Hay apuestas activas.");
        	logicaRonda.ejecutarTurnoJugador(jugador);
        } else {
            if (jugador.estaEnJuego() && mesaPoker.getApuestaTotalJug1() == mesaPoker.getApuestaTotalJug2() 
            		&& mesaPoker.getContadorTurno1() != 0) {
            	logicaRonda.avanzarFaseBoard(false);
            } 
            if(mesaPoker.getContadorTurno1() == 0) 
            	mesaPoker.setContadorTurno1(1);
            logicaRonda.pasarAlSiguienteJugador();
        }
    }

    public void ver(Jugador jugador) {
    	boolean aux = false;
    	
        double cantidadPorVer = calcularCantidadPorVer(jugador);
        if (cantidadPorVer > 0) {
            if (jugador.getSaldo() >= cantidadPorVer) {
            	
            	if(jugador.getId() == 1)
            		mesaPoker.setApuestaTotalJug1(mesaPoker.getApuestaTotalJug1() + cantidadPorVer);
            	else
            		mesaPoker.setApuestaTotalJug2(mesaPoker.getApuestaTotalJug2() + cantidadPorVer);
            	
                jugador.reducirSaldo(cantidadPorVer);
                jugador.aumentarApuesta(cantidadPorVer);
                mesaPoker.actualizarBote(cantidadPorVer);
                mesaPoker.mostrarMensaje("Jugador " + jugador.getId() + " iguala con $" + cantidadPorVer);
                jugador.resetApuesta(); // Resetea la apuesta actual del jugador para reflejar la igualdad.
                if (jugador.estaEnJuego() && mesaPoker.getApuestaTotalJug1() == mesaPoker.getApuestaTotalJug2() 
                		&& mesaPoker.getContadorTurno1() != 0 && !mesaPoker.getAllIn()) {
                	logicaRonda.avanzarFaseBoard(false);
                }
                	
            } else {
                double saldoRestante = jugador.getSaldo();
                jugador.reducirSaldo(saldoRestante);
                jugador.aumentarApuesta(saldoRestante);
                mesaPoker.actualizarBote(saldoRestante);
                mesaPoker.mostrarMensaje("Jugador " + jugador.getId() + " hace all-in con $" + saldoRestante);
            }
            apuestaActual = logicaRonda.calcularApuestaMaxima(); // Ajustar la apuesta máxima al estado actual.
        } else {
        	mesaPoker.mostrarMensaje("Apuesta igualada, puedes hacer 'Check' o 'Raise'.");
        	logicaRonda.ejecutarTurnoJugador(jugador);
            aux = true;
        }
        if(mesaPoker.getAllIn()) {
        	logicaRonda.iniciarModoAllIn();
        	aux = true;
        }
        else if(aux == false) {
        	if (logicaRonda.algunoAllIn()) {
        		mesaPoker.setAllIn(true);
            }
        	if(mesaPoker.getContadorTurno1() == 0) 
        		mesaPoker.setContadorTurno1(1);
        	logicaRonda.pasarAlSiguienteJugador();
        }
    }

    public double calcularCantidadPorVer(Jugador jugador) {
    	System.out.println("Bot: " + mesaPoker.getApuestaTotalJug2() + " Jugador: " + mesaPoker.getApuestaTotalJug1());
    	if (jugador.getId() == 1)
    		return mesaPoker.getApuestaTotalJug2() - mesaPoker.getApuestaTotalJug1();
    	else
    		return mesaPoker.getApuestaTotalJug1() - mesaPoker.getApuestaTotalJug2();
    }
    
    public void subirLogica(String cantidadStr, Jugador jugador) {
        try {
            double cantidad = Double.parseDouble(cantidadStr.trim());

            if (jugador.getId() == 1) {
                if (mesaPoker.getApuestaTotalJug2() >= mesaPoker.getApuestaTotalJug1() + cantidad) {
                	mesaPoker.mostrarMensaje("La cantidad debe ser mayor a la apuesta actual.");
                	logicaRonda.ejecutarTurnoJugador(jugador);
                    return;
                }
            } else {
                if (mesaPoker.getApuestaTotalJug1() >= mesaPoker.getApuestaTotalJug2() + cantidad) {
                	mesaPoker.mostrarMensaje("La cantidad debe ser mayor a la apuesta actual.");
                	logicaRonda.ejecutarTurnoJugador(jugador);
                    return;
                }
            }

            if (jugador.getSaldo() >= cantidad) {
                if (jugador.getId() == 1)
                	mesaPoker.setApuestaTotalJug1(mesaPoker.getApuestaTotalJug1() + cantidad);
                else
                	mesaPoker.setApuestaTotalJug2(mesaPoker.getApuestaTotalJug2() + cantidad);

                jugador.reducirSaldo(cantidad);
                jugador.aumentarApuesta(cantidad);
                jugador.registrarSubida(cantidad, jugador);
                mesaPoker.actualizarBote(cantidad);
                mesaPoker.setApuestaActual(mesaPoker.getApuestaActual() + cantidad);

                if (mesaPoker.getAllIn()) {
                	logicaRonda.iniciarModoAllIn();
                } else {
                    if (logicaRonda.algunoAllIn()) {
                    	mesaPoker.setAllIn(true);
                    }
                    if (mesaPoker.getContadorTurno1() == 0)
                    	mesaPoker.setContadorTurno1(1);
                    logicaRonda.pasarAlSiguienteJugador();
                }
            } else {
            	mesaPoker.mostrarMensaje("Jugador " + jugador.getId() + " no puede apostar más dinero del que tiene.");
            	logicaRonda.ejecutarTurnoJugador(jugador);
            }

        } catch (NumberFormatException ex) {
        	mesaPoker.mostrarMensaje("Entrada no válida.");
        	logicaRonda.ejecutarTurnoJugador(jugador);
        }
		
	}
    
    public void subirBot(Jugador bot, double cantidadSubida) {
    	if(mesaPoker.getAllIn()) {
    		mesaPoker.mostrarMensaje("Con un All-In solo se puede Ver o Foldear.");
    		logicaRonda.ejecutarTurnoJugador(bot);
        }
    	boolean aux = false;
    	if (cantidadSubida > mesaPoker.getApuestaTotalJug1()) {
            if (bot.getSaldo() >= cantidadSubida) {
            	
            	mesaPoker.setApuestaTotalJug2(mesaPoker.getApuestaTotalJug2() + cantidadSubida);
            	
                bot.reducirSaldo(cantidadSubida);
                bot.aumentarApuesta(cantidadSubida);
                bot.registrarSubida(cantidadSubida, bot);
                mesaPoker.actualizarBote(cantidadSubida);
                mesaPoker.setApuestaActual(mesaPoker.getApuestaActual() + cantidadSubida); // Actualizar apuesta actual al all-in
                mesaPoker.mostrarMensaje("El bot ha hecho raise de $ " + cantidadSubida);
            } else {
                // Caso de all-in si el bot no tiene suficiente saldo para cubrir la subida
                double saldoRestante = bot.getSaldo();
                
            	mesaPoker.setApuestaTotalJug2(mesaPoker.getApuestaTotalJug2() + saldoRestante);
            	
                bot.reducirSaldo(saldoRestante);
                bot.aumentarApuesta(saldoRestante);
                bot.registrarSubida(saldoRestante, bot); // Registrar el all-in como la nueva apuesta
                mesaPoker.actualizarBote(saldoRestante);
                mesaPoker.setApuestaActual(mesaPoker.getApuestaActual() + saldoRestante); // Actualizar apuesta actual al all-in
                mesaPoker.mostrarMensaje("Bot hace all-in con $ " + saldoRestante);
            }
        } else {
        	mesaPoker.mostrarMensaje("El bot no puede subir por debajo de la apuesta actual.");
        }

        // Verificación para iniciar modo all-in si todos aceptan
        if(mesaPoker.getAllIn()) {
        	logicaRonda.iniciarModoAllIn();
        	aux = true;
        }
        if(aux == false) {
        	if (logicaRonda.algunoAllIn()) {
        		mesaPoker.setAllIn(true); 
            }
        	if(mesaPoker.getContadorTurno1() == 0) 
        		mesaPoker.setContadorTurno1(1);
        	logicaRonda.pasarAlSiguienteJugador();
        }
    }
    
    public void foldear(Jugador jugador) {
    	mesaPoker.setJugadoresActivos(jugador, false);
    	mesaPoker.mostrarMensaje("Jugador " + jugador.getId() + " se retira.");
    	mesaPoker.setContadorTurno1(0);
    	logicaRonda.verificarGanador(); // Verificar si queda un solo jugador activo
    	logicaRonda.pasarAlSiguienteJugador();
    }
    
    
    public void registrarSubida(double cantidad, Jugador jugudor) {
        apuestaActual = Math.max(apuestaActual, cantidad); // Mantiene la apuesta máxima actualizada.
        if(jugudor.getId() == 1)
        	mesaPoker.mostrarMensaje("Nueva subida registrada: $" + String.format("%.2f", cantidad));
    }

}