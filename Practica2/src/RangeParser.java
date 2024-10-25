import javax.swing.JLabel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class RangeParser {
    private CardMatrixPanel cardMatrixPanel;

    public RangeParser(CardMatrixPanel cardMatrixPanel) {
        this.cardMatrixPanel = cardMatrixPanel;
    }

    public void setRangeFromText(String rangeText) {
        //cardMatrixPanel.clearMatrix(); // Limpiar la matriz antes de empezar

        String[] subRanges = rangeText.split(",");
        for (String subRange : subRanges) {
            subRange = subRange.trim();

            if (subRange.contains("-")) {
                handleDashRange(subRange);
            } else if (subRange.endsWith("+")) {
                handlePlusRange(subRange);
            } else {
                paintSingleCombination(subRange);
            }
        }
    }
    private void handleDashRange(String range) {
        // Separar ambos lados del guion
        String[] rangeParts = range.split("-");
        if (rangeParts.length != 2) return;

        String startPart = rangeParts[0].trim();
        String endPart = rangeParts[1].trim();

        // Extraer la carta alta y la notación suited/offsuit (suponemos que ambos lados tienen el mismo formato)
        char highCard = startPart.charAt(0);  // La carta alta es la misma para ambos extremos
        char suited = startPart.charAt(2);    // Suponemos que ambos tienen 's' o 'o'

        // Obtener los índices de las cartas bajas en los dos extremos
        int startIndex = getRankIndex(startPart.charAt(1)); // Índice del primer extremo
        int endIndex = getRankIndex(endPart.charAt(1));     // Índice del segundo extremo

        // Construir el nuevo rango incluyendo todos los valores intermedios desde la carta alta hasta la carta baja
        StringBuilder expandedRange = new StringBuilder();
        for (int i = startIndex; i <= endIndex; i++) {
            // Construir la combinación usando la carta alta común, la carta baja y la notación
            String combination = "" + highCard + getRankChar(i) + suited;
            expandedRange.append(combination).append(", ");
        }

        // Eliminar la última coma y espacio
        if (expandedRange.length() > 0) {
            expandedRange.setLength(expandedRange.length() - 2);
        }

        // Enviar el nuevo rango expandido a setRangeFromText
        setRangeFromText(expandedRange.toString());
    }

    private void paintSingleCombination(String combination) {
        JLabel[][] labels = cardMatrixPanel.getLabels();

        if (combination.length() == 2) {
            int index = getRankIndex(combination.charAt(0));
            labels[index][index].setBackground(Color.YELLOW);
        } else if (combination.length() == 3) {
            char highCard = combination.charAt(0);
            char lowCard = combination.charAt(1);
            char suited = combination.charAt(2);

            int highIndex = getRankIndex(highCard);
            int lowIndex = getRankIndex(lowCard);

            if (suited == 's') {
                labels[highIndex][lowIndex].setBackground(Color.YELLOW);
            } else if (suited == 'o') {
                labels[lowIndex][highIndex].setBackground(Color.YELLOW);
            }
        }
    }

    public String getRangeAsText() { 
        StringBuilder rangeBuilder = new StringBuilder();
        JLabel[][] labels = cardMatrixPanel.getLabels();

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                if (labels[i][j].getBackground().equals(Color.YELLOW)) {
                    if (i == j) {
                        rangeBuilder.append(getRankChar(i)).append(getRankChar(j)).append(",");
                    } else if (i < j) {
                        rangeBuilder.append(getRankChar(j)).append(getRankChar(i)).append("s,");
                    } else {
                        rangeBuilder.append(getRankChar(i)).append(getRankChar(j)).append("o,");
                    }
                }
            }
        }

        if (rangeBuilder.length() > 0) {
            rangeBuilder.setLength(rangeBuilder.length() - 1);
        }

        return rangeBuilder.toString();
    }
    
    private void handlePlusRange(String range) {
        // Verificar si el rango termina en "+"
        if (!range.endsWith("+")) return;

        // Eliminar el "+" del rango para obtener la base
        String baseRange = range.substring(0, range.length() - 1);

        // Verificar si es una pareja
        if (baseRange.length() == 2 && baseRange.charAt(0) == baseRange.charAt(1)) {
            // Es una pareja (por ejemplo, "JJ+")
            String expandedPairs = generarParesSuperiores(baseRange);
            setRangeFromText(expandedPairs);
        } else if (baseRange.length() == 3) {
            // No es una pareja (por ejemplo, "T2s+")
            char highCard = baseRange.charAt(0);
            char lowCard = baseRange.charAt(1);
            char suited = baseRange.charAt(2);

            String expandedCombinations = generarCombinacionesInferiores(highCard, lowCard, suited);
            setRangeFromText(expandedCombinations);
        }
    }

    private String generarParesSuperiores(String startPair) {
        StringBuilder pairsBuilder = new StringBuilder();
        int startIndex = getRankIndex(startPair.charAt(0));

        // Generar todas las parejas iguales o superiores a la indicada
        for (int i = startIndex; i >= 0; i--) {
            String pair = "" + getRankChar(i) + getRankChar(i);
            pairsBuilder.append(pair).append(", ");
        }

        // Eliminar la última coma y espacio
        if (pairsBuilder.length() > 0) {
            pairsBuilder.setLength(pairsBuilder.length() - 2);
        }

        return pairsBuilder.toString();
    }

    private String generarCombinacionesInferiores(char highCard, char lowCard, char suited) {
        StringBuilder combinationsBuilder = new StringBuilder();
        int lowIndex = getRankIndex(lowCard);
        int highIndex = getRankIndex(highCard); // Excluir la pareja de la carta alta

        // Generar combinaciones desde la carta baja hasta la carta alta menos una pareja
        for (int i = lowIndex; i > highIndex; i--) {
            String combination = "" + highCard + getRankChar(i) + suited;
            combinationsBuilder.append(combination).append(", ");
        }

        // Eliminar la última coma y espacio
        if (combinationsBuilder.length() > 0) {
            combinationsBuilder.setLength(combinationsBuilder.length() - 2);
        }

        return combinationsBuilder.toString();
    }
    private int getRankIndex(char rank) {
        switch (rank) {
            case 'A': return 0;
            case 'K': return 1;
            case 'Q': return 2;
            case 'J': return 3;
            case 'T': return 4;
            case '9': return 5;
            case '8': return 6;
            case '7': return 7;
            case '6': return 8;
            case '5': return 9;
            case '4': return 10;
            case '3': return 11;
            case '2': return 12;
            default: throw new IllegalArgumentException("Rango desconocido: " + rank);
        }
    }

    private char getRankChar(int index) {
        switch (index) {
            case 0: return 'A';
            case 1: return 'K';
            case 2: return 'Q';
            case 3: return 'J';
            case 4: return 'T';
            case 5: return '9';
            case 6: return '8';
            case 7: return '7';
            case 8: return '6';
            case 9: return '5';
            case 10: return '4';
            case 11: return '3';
            case 12: return '2';
            default: throw new IllegalArgumentException("Índice desconocido: " + index);
        }
    }
}
