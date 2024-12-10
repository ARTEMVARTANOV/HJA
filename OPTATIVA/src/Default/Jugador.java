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
    private final MesaPoker mesaPoker;
    private boolean esBot;

    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap, String[] cartasIniciales, double saldoInicial, Runnable onFoldCallback, MesaPoker mesaPoker, boolean esBot) {
        this(numeroJugador, cartaImagenMap, onFoldCallback, mesaPoker, saldoInicial, esBot);
        reiniciarCartas(cartasIniciales);
    }

    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap, Runnable onFoldCallback, MesaPoker mesaPoker, double saldoInicial, boolean esBot) {
        this.numJugador = numeroJugador;
        this.cartaImagenMap = cartaImagenMap;
        this.mesaPoker = mesaPoker;
        this.saldo = saldoInicial;
        this.apuestaActual = 0;
        this.esBot = esBot;

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

    private JTextField crearCampoEntrada() {
        JTextField campo = new JTextField(10);
        campo.setMaximumSize(new Dimension(150, 30));
        return campo;
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

    public void apostar(double cantidad) {
        if (cantidad <= saldo) {
            saldo -= cantidad;
            apuestaActual += cantidad;
            mesaPoker.actualizarBote(cantidad); // Actualizar el bote en la mesa
            actualizarSaldo();
        } else {
        	mesaPoker.mostrarMensaje("No tienes suficiente saldo para apostar $" + cantidad);
        }
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

    public boolean puedeSeguirJugando() {
        return saldo > 0;
    }

    public void actualizarProbabilidad(double probabilidad) {
        //if (enJuego) {
            //labelProbabilidad.setText(String.format("Probabilidad: %.2f%%", probabilidad));
        //}
    }
    
    public int getId() {
        return numJugador;
    }

    private void actualizarManoJugador(String[] cartas) {
        // Verificar las cartas ingresadas dependiendo de la longitud
        if (cartas.length == 2) {
            // Para el póker clásico (2 cartas)
            String carta1 = cartas[0].trim();
            String carta2 = cartas[1].trim();
            String rutaCarta1 = cartaImagenMap.get(carta1);
            String rutaCarta2 = cartaImagenMap.get(carta2);
            
            if (mesaPoker.cartaEnDisponibles(carta1) && mesaPoker.cartaEnDisponibles(carta2) && rutaCarta1 != null && rutaCarta2 != null) {
                reiniciarCartas(new String[]{carta1, carta2});
                mesaPoker.actualizarManoJugador(numJugador, new String[]{carta1, carta2});
            } else {
            	mesaPoker.mostrarMensaje("Una o ambas cartas no son válidas o ya están en uso.");
            }
        } else if (cartas.length == 4) {
            // Para Omaha (4 cartas)
            String carta1 = cartas[0].trim();
            String carta2 = cartas[1].trim();
            String carta3 = cartas[2].trim();
            String carta4 = cartas[3].trim();
            String rutaCarta1 = cartaImagenMap.get(carta1);
            String rutaCarta2 = cartaImagenMap.get(carta2);
            String rutaCarta3 = cartaImagenMap.get(carta3);
            String rutaCarta4 = cartaImagenMap.get(carta4);
            
            if (mesaPoker.cartaEnDisponibles(carta1) && mesaPoker.cartaEnDisponibles(carta2) &&
                mesaPoker.cartaEnDisponibles(carta3) && mesaPoker.cartaEnDisponibles(carta4) &&
                rutaCarta1 != null && rutaCarta2 != null && rutaCarta3 != null && rutaCarta4 != null) {
                reiniciarCartas(new String[]{carta1, carta2, carta3, carta4});
                mesaPoker.actualizarManoJugador(numJugador, new String[]{carta1, carta2, carta3, carta4});
            } else {
            	mesaPoker.mostrarMensaje("Una o más cartas no son válidas o ya están en uso.");
            }
        }
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

}