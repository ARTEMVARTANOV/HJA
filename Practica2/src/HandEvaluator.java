import java.util.*;

public class HandEvaluator {
	private Logic logica;
	
	public HandEvaluator() {
        this.logica = new Logic(); // Inicializa el objeto aquí.
    }
	
    public void calculateProbabilities(List<String> range, List<String> board,
    		Map<Logic.HandRank, Double> probabilities, Map<Logic.HandRank, Map<String, Integer>> RangeValues,
    		Map<Logic.HandRank, Integer> handRankCounts) {
        countCombosByHandRank(range, board, handRankCounts, RangeValues);

        // Calcular el total de combinaciones posibles
        int totalCombos = handRankCounts.values().stream().mapToInt(Integer::intValue).sum();

        // Calcular la probabilidad de cada jugada
        for (Map.Entry<Logic.HandRank, Integer> entry : handRankCounts.entrySet()) {
            double probability = (entry.getValue() / (double) totalCombos) * 100;
            probabilities.put(entry.getKey(), probability);
        }
    }
    
    public void countCombosByHandRank(List<String> range, List<String> board, 
    	Map<Logic.HandRank, Integer> handRankCounts, Map<Logic.HandRank, Map<String, Integer>> RangeValues) {
        // Inicializamos el mapa con todas las jugadas en 0
        for (Logic.HandRank rank : Logic.HandRank.values()) {
            Map<String, Integer> innerMap = new HashMap<>();
            RangeValues.put(rank, innerMap);
            handRankCounts.put(rank, 0);
        }
        // Para cada combinación posible en el rango, evaluamos la mano y aumentamos el conteo
        for (String combo : range) {
        	// Evaluamos la mano usando evaluateHand
        	logica.prepareEvaluateHand(combo, board, handRankCounts, RangeValues);
        }
    } 
}