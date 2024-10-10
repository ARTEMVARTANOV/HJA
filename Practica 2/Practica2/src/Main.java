import javax.swing.*;
import java.awt.*;

public class Main {
    private CardMatrixPanel cardMatrixPanel;
    private RangeParser rangeParser;
    private RangePercentageCalculator rangeCalculator;

    public static void main(String[] args) {
        Main app = new Main();
        app.createAndShowGUI();
    }

    public void createAndShowGUI() {
        // Crear el marco de la ventana principal
        JFrame frame = new JFrame("Rangos en NLHE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 900);

        // Instanciar las clases responsables de las diferentes funciones
        cardMatrixPanel = new CardMatrixPanel();
        rangeParser = new RangeParser(cardMatrixPanel);
        rangeCalculator = new RangePercentageCalculator(cardMatrixPanel);

        // Crear el panel para contener el gráfico de cartas
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());

        // Añadir el panel de la matriz de cartas
        containerPanel.add(cardMatrixPanel.getPanel(), BorderLayout.CENTER);

        // Crear el TextField y los botones para interacción textual y porcentaje
        JTextField rangeInput = new JTextField();
        JButton convertToGraphButton = new JButton("Convertir a gráfico");
        JButton convertToTextButton = new JButton("Convertir a texto");
        JButton calculatePercentageButton = new JButton("Calcular porcentaje");

        // Añadir funcionalidad a los botones
        convertToGraphButton.addActionListener(e -> {
            String rangeText = rangeInput.getText();
            rangeParser.setRangeFromText(rangeText);
        });

        convertToTextButton.addActionListener(e -> {
            String rangeText = rangeParser.getRangeAsText();
            rangeInput.setText(rangeText);
        });

        calculatePercentageButton.addActionListener(e -> {
            double percentage = rangeCalculator.calculateHandPercentage();
            JOptionPane.showMessageDialog(frame, "Porcentaje de manos: " + percentage + "%");
        });

        // Añadir los componentes a la parte inferior
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Rango:"));
        inputPanel.add(rangeInput);
        inputPanel.add(convertToGraphButton);
        inputPanel.add(convertToTextButton);
        inputPanel.add(calculatePercentageButton);

        // Añadir panel de interacción a la ventana
        containerPanel.add(inputPanel, BorderLayout.SOUTH);
        frame.add(containerPanel);
        frame.setVisible(true);
    }
    public void updateLabels(int value) {
        // Aquí puedes agregar lógica para modificar la matriz en función del valor del deslizador
        // Por ejemplo, cambiar el color de las etiquetas o realizar otra acción con el valor.

        // Si no necesitas hacer nada específico, pero necesitas capturar este valor, puedes dejar el método vacío.
        System.out.println("Slider value: " + value);

        // Si deseas realizar alguna acción sobre el CardMatrixPanel con base en este valor
        // Por ejemplo, modificar algo visual en el CardMatrixPanel
        cardMatrixPanel.updateFromSlider(value);
    }
}