package Default;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Jugador extends JPanel {
	private JTextField entradaCartas;
	private JButton foldButton;
    private boolean enJuego;
	private CartaPanel carta1Panel;
	private CartaPanel carta2Panel;
	private Runnable onFoldCallback;
	private JLabel labelProbabilidad; // Etiqueta para la probabilidad
    private Map<String, String> cartaImagenMap;

    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap, String[] cartasIniciales, Runnable onFoldCallback) {
        this(numeroJugador, cartaImagenMap); // Llama al constructor original
        this.onFoldCallback = onFoldCallback;
        setCartasIniciales(cartasIniciales);
    }
    
    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap) {
        this.cartaImagenMap = cartaImagenMap;
        this.enJuego = true; // Por defecto, todos los jugadores están en juego
        setBackground(new Color(200, 200, 200));
        setLayout(new FlowLayout());

        // Etiqueta para el número del jugador
        JLabel jugadorLabel = new JLabel("Jugador " + numeroJugador + ":");
        add(jugadorLabel);
        
        // Etiqueta para mostrar la probabilidad
        labelProbabilidad = new JLabel("Probabilidad: 0%", SwingConstants.CENTER);
        add(labelProbabilidad);
        
        foldButton = new JButton("Fold");
        foldButton.addActionListener(e -> hacerFold()); // Evento del botón
        add(foldButton);
        

        // Campo de texto para ingresar las cartas
        entradaCartas = new JTextField(10);
        add(entradaCartas);

        // Paneles de las cartas, inicializados sin imagen
        carta1Panel = new CartaPanel();
        carta2Panel = new CartaPanel();
        add(carta1Panel);
        add(carta2Panel);

        // Botón para actualizar las cartas
        JButton actualizarBtn = new JButton("Actualizar");
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

            if (rutaCarta1 != null && rutaCarta2 != null) {
                // Actualizar las imágenes de los paneles de cartas
                carta1Panel.setImage(rutaCarta1);
                carta2Panel.setImage(rutaCarta2);
            } else {
                JOptionPane.showMessageDialog(this, "Una o ambas cartas no existen en el mapa. Verifica los nombres de las cartas.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese 2 cartas separadas por una coma.");
        }
    }


}



