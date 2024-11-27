package Default;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Jugador extends JPanel {
	private JTextField entradaCartas;
	private int numJugador = 0;
	private JButton foldButton;
    private boolean enJuego;
	private CartaPanel carta1Panel;
	private CartaPanel carta2Panel;
	private Runnable onFoldCallback;
	private JLabel labelProbabilidad; // Etiqueta para la probabilidad
    private Map<String, String> cartaImagenMap;
    private MesaPoker mesaPoker;

    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap, String[] cartasIniciales, 
    		Runnable onFoldCallback, MesaPoker mesaPoker) {
        this(numeroJugador, cartaImagenMap); // Llama al constructor original
        this.onFoldCallback = onFoldCallback;
        this.mesaPoker = mesaPoker;
        this.numJugador = numeroJugador;
        setCartasIniciales(cartasIniciales);
    }
    
    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap) {
        this.cartaImagenMap = cartaImagenMap;
        this.enJuego = true; // Por defecto, todos los jugadores están en juego

        // Aumentar el tamaño del cuadrado gris
        setPreferredSize(new Dimension(200, 300));
        setMinimumSize(new Dimension(200, 300));
        setMaximumSize(new Dimension(250, 350));

        // Cambiar a un diseño vertical
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(200, 200, 200));

        // Etiqueta para el número del jugador
        JLabel jugadorLabel = new JLabel("Jugador " + numeroJugador + ":");
        jugadorLabel.setAlignmentX(CENTER_ALIGNMENT); // Centrar el texto
        add(jugadorLabel);

        // Etiqueta para mostrar la probabilidad
        labelProbabilidad = new JLabel("Probabilidad: 0%", SwingConstants.CENTER);
        labelProbabilidad.setAlignmentX(CENTER_ALIGNMENT);
        add(labelProbabilidad);

        // Botón de Fold
        foldButton = new JButton("Fold");
        foldButton.setAlignmentX(CENTER_ALIGNMENT);
        foldButton.addActionListener(e -> hacerFold());
        add(foldButton);

        // Campo de texto para ingresar las cartas
        entradaCartas = new JTextField(10);
        entradaCartas.setMaximumSize(new Dimension(150, 30)); // Ajusta el ancho del campo
        add(entradaCartas);

        // Paneles de cartas
        carta1Panel = new CartaPanel();
        carta2Panel = new CartaPanel();
        JPanel cartasPanel = new JPanel();
        cartasPanel.setLayout(new FlowLayout());
        cartasPanel.add(carta1Panel);
        cartasPanel.add(carta2Panel);
        add(cartasPanel);

        // Botón para actualizar cartas
        JButton actualizarBtn = new JButton("Actualizar");
        actualizarBtn.setAlignmentX(CENTER_ALIGNMENT);
        actualizarBtn.addActionListener(e -> actualizarCartas());
        add(actualizarBtn);
    }

    
    private void setCartasIniciales(String[] cartasIniciales) {
        // Configura las imágenes de las cartas iniciales usando el mapa de imágenes
        String rutaCarta1 = cartaImagenMap.get(cartasIniciales[0]);
        String rutaCarta2 = cartaImagenMap.get(cartasIniciales[1]);
        carta1Panel.setImage(rutaCarta1);
        carta2Panel.setImage(rutaCarta2);
    }
    
    public boolean estaEnJuego() {
        return enJuego;
    }

    private void hacerFold() {
        enJuego = false; // Marca que este jugador no está en juego
        labelProbabilidad.setText("Folded");
        foldButton.setEnabled(false); // Desactiva el botón
        if (onFoldCallback != null) {
            onFoldCallback.run();
        }
    }
    
    // Método para actualizar la probabilidad mostrada gráficamente
    public void actualizarProbabilidad(double probabilidad) {
    	if (enJuego) {
    		labelProbabilidad.setText(String.format("Probabilidad: %.2f%%", probabilidad));
    	}
    }

    private void actualizarCartas() {
        String entrada = entradaCartas.getText().trim();
        String[] cartas = entrada.split(",");

        if (cartas.length == 2) {
            // Quitar espacios en blanco y obtener las rutas desde el mapa
            String carta1 = cartas[0].trim();
            String carta2 = cartas[1].trim();

            // Obtener las rutas de las imágenes usando el mapa
            String rutaCarta1 = cartaImagenMap.get(carta1);
            String rutaCarta2 = cartaImagenMap.get(carta2);

            if(mesaPoker.cartaEnDisponibles(carta1, carta2)) {
	            if (rutaCarta1 != null && rutaCarta2 != null) {
	                // Actualizar las imágenes de los paneles de cartas
	                carta1Panel.setImage(rutaCarta1);
	                carta2Panel.setImage(rutaCarta2);
	
	                // Notificar a la mesa que se han cambiado las cartas de este jugador
	                String[] nuevaMano = {carta1, carta2};
	                mesaPoker.actualizarManoJugador(numJugador, nuevaMano);
	
	            } else {
	                JOptionPane.showMessageDialog(this, "Una o ambas cartas no existen. Verifica los nombres de las cartas.");
	            }
            } else {
                JOptionPane.showMessageDialog(this, "Una o ambas cartas ya se estan utilizando.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese 2 cartas separadas por una coma.");
        }

        // Vaciar el campo de texto después de actualizar las cartas
        entradaCartas.setText("");
    }

    
    public void actualizarCartas(String[] cartas, Map<String, String> cartaImagenMap) {
        if (cartas.length >= 2) {
            String rutaCarta1 = cartaImagenMap.get(cartas[0].trim());
            String rutaCarta2 = cartaImagenMap.get(cartas[1].trim());
            if (rutaCarta1 != null && rutaCarta2 != null) {
                carta1Panel.setImage(rutaCarta1);
                carta2Panel.setImage(rutaCarta2);
            }
        }
    }



}



