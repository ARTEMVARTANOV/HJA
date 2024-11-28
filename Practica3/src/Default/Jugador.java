package Default;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Jugador extends JPanel {
    private final JTextField entradaCartas;
    private final JLabel labelProbabilidad;
    private final JButton foldButton;
    private final CartaPanel[] cartaPanels = new CartaPanel[4];
    private final int numJugador;
    private boolean enJuego = true;
    private final Map<String, String> cartaImagenMap;
    private final Runnable onFoldCallback;
    private final MesaPoker mesaPoker;

    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap, String[] cartasIniciales, Runnable onFoldCallback, MesaPoker mesaPoker) {
        this(numeroJugador, cartaImagenMap, onFoldCallback, mesaPoker);
        reiniciarCartas(cartasIniciales);
    }

    public Jugador(int numeroJugador, Map<String, String> cartaImagenMap, Runnable onFoldCallback, MesaPoker mesaPoker) {
        this.numJugador = numeroJugador;
        this.cartaImagenMap = cartaImagenMap;
        this.onFoldCallback = onFoldCallback;
        this.mesaPoker = mesaPoker;

        setPreferredSize(new Dimension(200, 400));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Panel para la mitad superior con fondo gris
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(200, 200, 200));  // Fondo gris
        
        panelSuperior.add(crearEtiqueta("Jugador " + numeroJugador + ":"));
        labelProbabilidad = crearEtiqueta("Probabilidad: 0%");
        panelSuperior.add(labelProbabilidad);
        foldButton = crearBotonFold();
        panelSuperior.add(foldButton);

        panelSuperior.setPreferredSize(new Dimension(200, 100));
        
        // Panel para la mitad inferior (cartas y entrada)
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setPreferredSize(new Dimension(200, 250));  // 150 de altura para la mitad inferior
        entradaCartas = crearCampoEntrada();
        panelInferior.add(entradaCartas);
        panelInferior.add(crearPanelCartas());

        // Agregar ambos paneles al panel principal
        add(panelSuperior);
        add(panelInferior);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setAlignmentX(CENTER_ALIGNMENT);
        return etiqueta;
    }

    private JButton crearBotonFold() {
        JButton boton = new JButton("Fold");
        boton.setAlignmentX(CENTER_ALIGNMENT);
        boton.addActionListener(e -> hacerFold());
        return boton;
    }

    private JTextField crearCampoEntrada() {
        JTextField campo = new JTextField(10);
        campo.setMaximumSize(new Dimension(150, 30));
        return campo;
    }

    private JPanel crearPanelCartas() {
        JPanel panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        for (int i = 0; i < cartaPanels.length; i++) {
            cartaPanels[i] = new CartaPanel();
            panelCartas.add(cartaPanels[i]);
        }
        return panelCartas;
    }

    public boolean estaEnJuego() {
        return enJuego;
    }

    private void hacerFold() {
        enJuego = false;
        labelProbabilidad.setText("Folded");
        foldButton.setEnabled(false);
        if (onFoldCallback != null) {
            onFoldCallback.run();
        }
    }

    public void actualizarProbabilidad(double probabilidad) {
        if (enJuego) {
            labelProbabilidad.setText(String.format("Probabilidad: %.2f%%", probabilidad));
        }
    }

    public void actualizarCartas() {
        String[] cartas = entradaCartas.getText().trim().split(",");
        
        // Verificar que el número de cartas es 2 (para el póker clásico) o 4 (para Omaha)
        if (cartas.length == 2 || cartas.length == 4) {
            actualizarManoJugador(cartas);
        } else {
            mostrarMensaje("Por favor, ingrese 2 cartas si esta en modalidad Texas Hold'em "
            		+ "o 4 cartas si esta en modalidad Omaha separadas por una coma.");
        }
        entradaCartas.setText("");
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
                mostrarMensaje("Una o ambas cartas no son válidas o ya están en uso.");
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
                mostrarMensaje("Una o más cartas no son válidas o ya están en uso.");
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

    public String getEntradaCartas() {
        return entradaCartas.getText().trim(); // Devuelve el texto del campo de entrada
    }
    
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
