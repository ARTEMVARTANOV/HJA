import javax.swing.JLabel;   // Para JLabel
import java.awt.Color;
public class RangeParser {
    private CardMatrixPanel cardMatrixPanel;

    public RangeParser(CardMatrixPanel cardMatrixPanel) {
        this.cardMatrixPanel = cardMatrixPanel;
    }

    public void setRangeFromText(String rangeText) {
        cardMatrixPanel.clearMatrix();
        JLabel[][] labels = cardMatrixPanel.getLabels();

        String[] ranges = rangeText.split(",");
        for (String range : ranges) {
            range = range.trim();
            if (range.length() == 2) {
                int index = getRankIndex(range.charAt(0));
                labels[index][index].setBackground(Color.YELLOW);
            } else if (range.length() >= 3) {
                char highCard = range.charAt(0);
                char lowCard = range.charAt(1);
                char suited = range.charAt(2);
                int highIndex = getRankIndex(highCard);
                int lowIndex = getRankIndex(lowCard);
                if (suited == 's') {
                    labels[lowIndex][highIndex].setBackground(Color.YELLOW);
                } else if (suited == 'o') {
                    labels[highIndex][lowIndex].setBackground(Color.YELLOW);
                }
            }
        }
    }

    public String getRangeAsText() {
        StringBuilder rangeBuilder = new StringBuilder();
        JLabel[][] labels = cardMatrixPanel.getLabels();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (labels[i][j].getBackground().equals(Color.YELLOW)) {
                    if (i == j) {
                        rangeBuilder.append(getRankChar(i)).append(getRankChar(i)).append(",");
                    } else if (i > j) {
                        rangeBuilder.append(getRankChar(i)).append(getRankChar(j)).append("o,");
                    } else {
                        rangeBuilder.append(getRankChar(j)).append(getRankChar(i)).append("s,");
                    }
                }
            }
        }
        if (rangeBuilder.length() > 0) {
            rangeBuilder.setLength(rangeBuilder.length() - 1);
        }
        return rangeBuilder.toString();
    }

    private int getRankIndex(char rank) {
        // Implementar el mismo método getRankIndex de antes.
        return 0; // Simplificado aquí
    }

    private char getRankChar(int index) {
        // Implementar el mismo método getRankChar de antes.
        return 'A'; // Simplificado aquí
    }
}
