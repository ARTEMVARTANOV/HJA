package Default;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CartaPanel extends JPanel {
    private BufferedImage image;

    public CartaPanel() {
        // Iniciar sin imagen
        image = null;
        setPreferredSize(new Dimension(50, 70)); // Tamaño estándar para la carta
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void setImage(String rutaImagen) {
        try {
            if (rutaImagen == null) {
                image = null; // Si la ruta es null, establece la imagen como null
            } else {
                image = ImageIO.read(new File(rutaImagen)); // Carga la imagen solo si la ruta no es null
            }
            repaint(); // Redibujar el panel con la nueva imagen o sin imagen
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo cargar la nueva imagen: " + rutaImagen);
        }
    }

}

