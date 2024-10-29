import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class Main {
    private CardMatrixPanel cardMatrixPanel;
    private RangeParser rangeParser;
    private RangePercentageCalculator rangeCalculator; // Añadir una instancia de RangePercentageCalculator
    private BoardCardSelector boardCardSelector; // Añadir una instancia de BoardCardSelector
    private HandEvaluator handEvaluator; // Añadir una instancia de HandEvaluator

    public static void main(String[] args) {
        Main app = new Main();
        app.createAndShowGUI();
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Rangos en NLHE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900); // Aumentar el tamaño del frame para acomodar la nueva tabla

        cardMatrixPanel = new CardMatrixPanel();
        rangeParser = new RangeParser(cardMatrixPanel);
        rangeCalculator = new RangePercentageCalculator(cardMatrixPanel); // Inicializar RangePercentageCalculator
        boardCardSelector = new BoardCardSelector(); // Inicializar BoardCardSelector
        handEvaluator = new HandEvaluator(); // Inicializar HandEvaluator

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());

        containerPanel.add(cardMatrixPanel.getPanel(), BorderLayout.CENTER);
        containerPanel.add(boardCardSelector.getPanel(), BorderLayout.EAST); // Añadir la tabla de selección de cartas del board a la derecha

        JTextField rangeInput = new JTextField();
        JButton convertToGraphButton = new JButton("Convertir a gráfico");
        JButton convertToTextButton = new JButton("Convertir a texto");
        JButton calculatePercentageButton = new JButton("Calcular porcentaje"); // Añadir botón para calcular el porcentaje
        JButton showCombosButton = new JButton("Mostrar combos"); // Añadir botón para mostrar combos

        convertToGraphButton.addActionListener(e -> {
            String rangeText = rangeInput.getText();
            cardMatrixPanel.clearMatrix();
            rangeParser.setRangeFromText(rangeText);
        });

        convertToTextButton.addActionListener(e -> {
            String rangeText = rangeParser.getRangeAsText();
            rangeInput.setText(rangeText);
        });

        calculatePercentageButton.addActionListener(e -> {
            String inputText = rangeInput.getText();
            if (inputText.endsWith("%")) {
                inputText = inputText.replace("%", "");
                int percentage = Integer.parseInt(inputText);

                rangeCalculator.calculateHandPercentageColour(percentage);
            } else {
                double percentage = rangeCalculator.calculateHandPercentage();
                JOptionPane.showMessageDialog(frame, "Porcentaje de manos: " + String.format("%.2f", percentage) + "%");
            }
        });

        showCombosButton.addActionListener(e -> {
            // Obtener el rango seleccionado como texto
            String rangeText = rangeParser.getRangeAsText();
            // Convertir el rango de texto en una lista de cartas
            List<String> range = Arrays.asList(rangeText.split(","));
            // Obtener las cartas seleccionadas del board usando BoardCardSelector
            List<String> board = boardCardSelector.getSelectedCards();
            
            // Evaluar las probabilidades con HandEvaluator
            if (board.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, selecciona al menos una carta del board.");
                return;
            }
            
            Map<HandEvaluator.HandRank, Double> probabilities = handEvaluator.calculateProbabilities(range, board);

            // Mostrar las probabilidades en un cuadro de diálogo
            StringBuilder result = new StringBuilder("Probabilidades:\n");
            for (Map.Entry<HandEvaluator.HandRank, Double> entry : probabilities.entrySet()) {
                result.append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("%\n");
            }
            JOptionPane.showMessageDialog(frame, result.toString());
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 4)); // Ajustar la disposición a 2 filas y 4 columnas
        inputPanel.add(new JLabel("Rango:"));
        inputPanel.add(rangeInput);
        inputPanel.add(convertToGraphButton);
        inputPanel.add(convertToTextButton);
        inputPanel.add(calculatePercentageButton); // Añadir el botón de porcentaje al panel
        inputPanel.add(showCombosButton); // Añadir el botón de mostrar combos al panel

        containerPanel.add(inputPanel, BorderLayout.SOUTH);
        frame.add(containerPanel);
        cardMatrixPanel.clearMatrix();
        frame.setVisible(true);
    }

    public void updateLabels(int value) {
        // Supongamos que 'value' representa un nivel de selección, por ejemplo, entre 0 y 100.
        // Vamos a cambiar el color de las etiquetas en función de este valor.

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
                labels[i][j].setBackground(newColor);
            }
        }

        // Imprime el valor del deslizador para fines de depuración
        System.out.println("Slider value: " + value);
    }
}