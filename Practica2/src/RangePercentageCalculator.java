import java.awt.Color;
import javax.swing.JLabel;
public class RangePercentageCalculator {
    private CardMatrixPanel cardMatrixPanel;

    // Lista de manos ordenada según el ranking de Sklansky-Chubukov
    private static final String[] handRankings = {
        "AA", "KK", "AKs", "QQ", "AKo", "JJ", "AQs", "TT", "AQo", "99",
        "AJs", "88", "ATs", "AJo", "77", "66", "ATo", "A9s", "55", "A8s",
        "KQs", "44", "A9o", "A7s", "KJs", "A5s", "A8o", "A6s", "A4s", "33",
        "KTs", "A7o", "A3s", "KQo", "A2s", "A5o", "A6o","A4o", "KJo", "QJs","A3o",
        "22", "K9s", "A2o", "KTo", "QTs", "K8s", "K7s", "JTs", "K9o", "K6s", 
        "QJo", "Q9s", "K5s", "K8o", "K4s", "QTo", "KTo", "K7o", "K3s", "K2s", "Q8s",
        "K6o", "J9s", "K5o", "Q9o", "JTo", "K4o", "Q7s", "T9s", "Q6s", "K3o",
        "J8s", "Q5s", "K2o", "Q8o", "Q4s", "Q3s", "J9o", "T8s", "J7s", "Q7o",
        "Q2s", "Q6o", "98s", "Q5o", "T9o", "J8o", "J6s", "J5s", "T7s", "Q4o",
        "J4s", "J7o", "Q3o", "97s", "J3s", "T8o", "T6s", "Q2o", "J2s", "87s",
        "J6o", "98o", "T7o", "96s", "J5o", "T5s", "T4s", "86s", "J4o", "T3s", 
        "97o", "T6o", "95s", "76s", "J3o", "T2s", "87o", "85s", "96o", "J2o", 
        "T5o", "94s", "75s", "65s", "T4o", "93s", "86o", "84s", "95o", "76o",
        "T3o", "92s", "74s", "54s", "85o", "T2o", "64s", "83s", "75o", "94o",
        "82s", "73s", "93o", "53s", "65o", "63s", "84o", "92o", "43s", "72s",
        "54o", "74o", "62s", "52s", "64o", "83o", "42s", "82o", "53o", "63o",
        "73o", "32s", "43o", "72o", "62o", "52o", "42o", "32o" 
    };

    private static final String[] ranks = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
    
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
    
    public void calculateHandPercentageColour(int percentage) {
    	int numHands = (int) (handRankings.length * (percentage / 100.0));
    	int row;
    	int col;
	    // Limpiar la matriz antes de aplicar el nuevo coloreado
	    cardMatrixPanel.clearMatrix();

	    // Recorrer las primeras `numHands` manos y colorearlas
	    for (int i = 0; i < numHands; i++) {
	        String hand = handRankings[i];
	        if (hand.length() == 2) {
		        row = getRowIndex(hand);  
		        col = getColIndex(hand); 
	        }
	        else {
	        	if(hand.endsWith("o")) {
	        		col = getRowIndex(hand);  
	        		row = getColIndex(hand); 
	        	}
	        	else {
	        		row = getRowIndex(hand);  
			        col = getColIndex(hand); 
	        	}
	        }
	        // Colorear la celda correspondiente
	        cardMatrixPanel.getLabels()[row][col].setBackground(Color.MAGENTA);
	    }
	  
    }
    
    private int getRowIndex(String hand) {
        String firstChar = hand.substring(0, 1);
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(firstChar)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid hand format: " + hand);
    }

    private int getColIndex(String hand) {
        String secondChar = hand.substring(1, 2);
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(secondChar)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid hand format: " + hand);
    }
    
}