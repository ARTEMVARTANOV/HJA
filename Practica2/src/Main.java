import javax.swing.*;
import java.awt.*;

public class Main {
    private CardMatrixPanel cardMatrixPanel;
    private RangeParser rangeParser;

    public static void main(String[] args) {
        Main app = new Main();
        app.createAndShowGUI();
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Rangos en NLHE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 900);

        cardMatrixPanel = new CardMatrixPanel();
        rangeParser = new RangeParser(cardMatrixPanel);

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());

        containerPanel.add(cardMatrixPanel.getPanel(), BorderLayout.CENTER);

        JTextField rangeInput = new JTextField();
        JButton convertToGraphButton = new JButton("Convertir a gráfico");
        JButton convertToTextButton = new JButton("Convertir a texto");

        convertToGraphButton.addActionListener(e -> {
            String rangeText = rangeInput.getText();
            cardMatrixPanel.clearMatrix();
            rangeParser.setRangeFromText(rangeText);
        });

        convertToTextButton.addActionListener(e -> {
            String rangeText = rangeParser.getRangeAsText();
            rangeInput.setText(rangeText);
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Rango:"));
        inputPanel.add(rangeInput);
        inputPanel.add(convertToGraphButton);
        inputPanel.add(convertToTextButton);

        containerPanel.add(inputPanel, BorderLayout.SOUTH);
        frame.add(containerPanel);
        frame.setVisible(true);
    }
    
    public void updateLabels(int value) {
        // Supongamos que 'value' representa un nivel de selección, por ejemplo, entre 0 y 100.
        // Vamos a cambiar el color de las etiquetas en función de este valor.

        // Por ejemplo, si el valor es par, pintaremos las etiquetas de un color específico,
        // y si es impar, usaremos otro color. También podrías utilizar este valor para resaltar
        // combinaciones específicas, si lo deseas.

        Color newColor;
        if (value < 50) {
            newColor = Color.LIGHT_GRAY;
        } else {
            newColor = Color.CYAN; // Cambia el color según tu preferencia o lógica.
        }

        // Recorremos todas las etiquetas y las actualizamos con el nuevo color
        JLabel[][] labels = cardMatrixPanel.getLabels();
        for (int i = 0; i < labels.length; i++) {
            for (int j = 0; j < labels[i].length; j++) {
                // Actualizamos el color de fondo de cada etiqueta
                labels[i][j].setBackground(newColor);
            }
        }

        // Imprime el valor del deslizador para fines de depuración
        System.out.println("Slider value: " + value);
    }
}