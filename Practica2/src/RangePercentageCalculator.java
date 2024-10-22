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
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (labels[i][j].getBackground().equals(Color.YELLOW)) {
                    if (i == j) {
                        handCount += 6;
                    } else {
                        handCount += 12;
                    }
                }
            }
        }
        return (double) handCount / 1326 * 100;
    }
}