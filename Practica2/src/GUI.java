import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI {
	private CardMatrixPanel cardMatrixPanel;
    private RangeParser rangeParser;
    private RangePercentageCalculator rangeCalculator; // Añadir una instancia de RangePercentageCalculator
    private BoardCardSelector boardCardSelector; // Añadir una instancia de BoardCardSelector
    private HandEvaluator handEvaluator; // Añadir una instancia de HandEvaluator
    private DrawEvaluator drawEvaluator;
    
    public GUI() {
    	this.cardMatrixPanel = new CardMatrixPanel();
    	this.rangeParser = new RangeParser(cardMatrixPanel);
    	this.rangeCalculator = new RangePercentageCalculator(cardMatrixPanel); // Inicializar RangePercentageCalculator
    	this.boardCardSelector = new BoardCardSelector(); // Inicializar BoardCardSelector
    	this.handEvaluator = new HandEvaluator(); // Inicializar HandEvaluator
        this.drawEvaluator = new DrawEvaluator();
        createAndShowGUI();
    }
    
	
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Rangos en NLHE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900); // Aumentar el tamaño del frame para acomodar la nueva tabla

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());

        containerPanel.add(cardMatrixPanel.getPanel(), BorderLayout.CENTER);
        containerPanel.add(boardCardSelector.getPanel(), BorderLayout.EAST); // Añadir la tabla de selección de cartas del board a la derecha

        JTextField rangeInput = new JTextField();
        JButton convertToGraphButton = new JButton("Convertir a gráfico");
        JButton convertToTextButton = new JButton("Convertir a texto");
        JButton calculatePercentageButton = new JButton("Calcular porcentaje"); // Añadir botón para calcular el porcentaje
        JButton showCombosButton = new JButton("Mostrar combos"); // Añadir botón para mostrar combos
        JButton clearButton = new JButton("Limpiar");
        JButton drawsButton = new JButton("Draws");
        
        convertToGraphButton.addActionListener(e -> {
            String rangeText = rangeInput.getText();
            cardMatrixPanel.clearMatrix();
            rangeParser.setRangeFromText(rangeText);
        });

        convertToTextButton.addActionListener(e -> {
            String rangeText = rangeParser.getRangeAsText();
            rangeInput.setText(rangeText);
        });

        clearButton.addActionListener(e -> {
        	cardMatrixPanel.clearMatrix();
        	boardCardSelector.clearBoard();
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

     // Inicializar el formato como detallado o compacto
        boolean[] detailedFormat = {true};

        JButton toggleFormatButton = new JButton("Cambiar formato de salida");
        toggleFormatButton.addActionListener(e -> {
            detailedFormat[0] = !detailedFormat[0]; // Alternar el formato
            toggleFormatButton.setText(detailedFormat[0] ? "Formato: Detallado" : "Formato: Compacto");
        });

        // Añadir el botón a tu panel o frame
        frame.add(toggleFormatButton);

        // Mostrar el cuadro de diálogo con el formato seleccionado en showCombosButton
        showCombosButton.addActionListener(e -> {
            // Obtener el rango seleccionado y cartas del board
            String rangeText = rangeParser.getRangeAsText();
            List<String> range = Arrays.asList(rangeText.split(","));
            List<String> board = boardCardSelector.getSelectedCards();
            
            if (board.size() < 3) {
                JOptionPane.showMessageDialog(frame, "Por favor, selecciona al menos tres cartas del board.");
                return;
            }
            
            Map<Logic.HandRank, Double> probabilities = new HashMap<>();
            Map<Logic.HandRank, Map<String, Integer>> RangeValues = new HashMap<>();
            Map<Logic.HandRank, Integer> handRankCounts = new HashMap<>();
            
            handEvaluator.calculateProbabilities(range, board, probabilities, RangeValues, handRankCounts);
            int totalCombos = handRankCounts.values().stream().mapToInt(Integer::intValue).sum();
            
            // Construir el resultado según el formato seleccionado
            StringBuilder result = new StringBuilder("Probabilidades:\n");
            result.append("Total de combos: ").append(totalCombos).append("\n\n");
            
            for (Logic.HandRank handRank : Logic.HandRank.values()) {
                if (probabilities.containsKey(handRank)) {
                    double probability = probabilities.get(handRank);
                    int comboCount = handRankCounts.get(handRank);
                    
                    result.append(handRank).append(": ").append(String.format("%.2f", probability))
                          .append("% (").append(comboCount).append(")\n");
                    
                    if (detailedFormat[0] && RangeValues.containsKey(handRank)) { // Solo mostrar combinaciones en modo detallado
                        Map<String, Integer> combos = RangeValues.get(handRank);
                        
                        List<Map.Entry<String, Integer>> sortedCombos = new ArrayList<>(combos.entrySet());
                        sortedCombos.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
                        if (!sortedCombos.isEmpty()) {
                        	StringBuilder combosList = new StringBuilder("  Combinaciones: ");
	                        for (Map.Entry<String, Integer> comboEntry : sortedCombos) {
	                            combosList.append(comboEntry.getKey()).append("(").append(comboEntry.getValue()).append("), ");
	                        }
                        
	                        if (combosList.length() > 0) {
	                            combosList.setLength(combosList.length() - 2);
	                        }
	                        result.append(combosList.toString()).append("\n");
                        }
                        
                    }
                }
            }

            JOptionPane.showMessageDialog(frame, result.toString());
        });

        drawsButton.addActionListener(e -> {
            // Obtener el rango seleccionado como texto
            String rangeText = rangeParser.getRangeAsText();
            List<String> range = Arrays.asList(rangeText.split(","));

            // Obtener las cartas seleccionadas del board usando BoardCardSelector
            List<String> board = boardCardSelector.getSelectedCards();

            // Validar que se han seleccionado al menos tres cartas del board
            if (board.size() < 3) {
                JOptionPane.showMessageDialog(frame, "Por favor, selecciona al menos tres cartas del board.");
                return;
            }

            try {
                String rangeString = String.join(",", range);

                // Asegúrate de que evaluateDraws retorne el tipo correcto
                Map<Draws.typeDraws, Double> probabilities = new HashMap<>();
                Map<Draws.typeDraws, DrawData> drawsCount = new HashMap<>();

                drawEvaluator.calculateDrawProbabilities(rangeString, board, probabilities, drawsCount);

                // Construir el resultado para mostrar en un cuadro de diálogo
                StringBuilder result = new StringBuilder("Proyectos:\n");
                if (drawsCount.isEmpty()) {
                    result.append("No hay proyectos válidos para la combinación seleccionada.\n");
                } else {
                    // Ordenar los draws según el orden del enum
                    for (Draws.typeDraws drawType : Draws.typeDraws.values()) {
                        DrawData drawData = drawsCount.get(drawType);
                        if (drawData != null) { // Solo proceder si hay datos para este tipo de draw
                            int count = drawData.getCount(); // Acceder al contador correspondiente

                            // Obtener la lista de manos provocadoras
                            List<String> hands = drawData.getStringList();
                            String handsString = String.join(", ", hands); // Convertir la lista de manos a String

                            // Agregar al resultado
                            result.append(drawType.name())
                                  .append(": ")
                                  .append(count);

                            // Solo agregar "Manos:" si hay manos en la lista
                            if (!hands.isEmpty()) {
                                result.append(" combos (Manos: ").append(handsString).append(")");
                            } else {
                                result.append(" combos");
                            }

                            result.append("\n"); // Nueva línea para el siguiente draw
                        }
                    }
                }
                JOptionPane.showMessageDialog(frame, result.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al evaluar los proyectos: " + ex.getMessage());
            }
        });
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 4)); // Ajustar la disposición a 2 filas y 4 columnas
        inputPanel.add(new JLabel("Rango:"));
        inputPanel.add(rangeInput);
        inputPanel.add(convertToGraphButton);
        inputPanel.add(convertToTextButton);
        inputPanel.add(calculatePercentageButton); // Añadir el botón de porcentaje al panel
        inputPanel.add(showCombosButton); // Añadir el botón de mostrar combos al panel
        inputPanel.add(clearButton);
        inputPanel.add(drawsButton);
        
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
