import java.awt.Color;
import javax.swing.JLabel;
public class RangePercentageCalculator {
    private CardMatrixPanel cardMatrixPanel;

    public RangePercentageCalculator(CardMatrixPanel cardMatrixPanel) {
        this.cardMatrixPanel = cardMatrixPanel;
    }

    public double calculateHandPercentage() {
        int handCount = 0;
        JLabel[][] labels = cardMatrixPanel.getLabels();

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                if (labels[i][j].getBackground().equals(Color.YELLOW)) {
                    if (i == j) {
                        handCount += 6; // Pares: 6 combinaciones
                    } else {
                        handCount += 12; // Combinaciones suited y offsuit: 12 combinaciones cada una
                    }
                }
            }
        }
        
        return (double) handCount / 1326 * 100; // Total de combinaciones en Texas Hold'em: 1326
    }
}