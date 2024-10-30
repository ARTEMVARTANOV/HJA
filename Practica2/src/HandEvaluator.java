import java.util.*;

public class HandEvaluator {
    // Enumeración para las jugadas posibles
    public enum HandRank {
        STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH, 
        STRAIGHT, THREE_OF_A_KIND, TWO_PAIR, OVERPAIR, TOP_PAIR,
        POCKET_PAIR_BELOW_TOP_PAIR, MIDDLE_PAIR, WEAK_PAIR, ACE_HIGH,
        NO_MADE_HAND
    }

 // Evaluar una mano dada (rango + board) y devolver la mejor jugada
    public void evaluateHand(String combo, List<String> board, 
            Map<HandRank, Integer> handRankCounts, Map<HandRank, Map<String, Integer>> RangeValues) {
        List<String> combinaciones = generarCombinaciones(combo, board);
        for (String caso : combinaciones) {
            // Dividir 'caso' en dos cartas (asumiendo que siempre son 4 caracteres)
            String carta1 = caso.substring(0, 2); // Primera carta
            String carta2 = caso.substring(2, 4); // Segunda carta
            
            String currentHandString = carta1 + carta2;
            List<String> currentHand = Arrays.asList(carta1, carta2); // Convertir combo a lista de carta
            List<String> combinedCards = new ArrayList<>(currentHand);
            combinedCards.addAll(board);
            
            // Evaluar las manos
            if (isStraightFlush(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.STRAIGHT_FLUSH, 0) + 1;
                handRankCounts.put(HandRank.STRAIGHT_FLUSH, count);
                actualizarRangeValues(RangeValues, HandRank.STRAIGHT_FLUSH, currentHandString);
            } else if (isFourOfAKind(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.FOUR_OF_A_KIND, 0) + 1;
                handRankCounts.put(HandRank.FOUR_OF_A_KIND, count);
                actualizarRangeValues(RangeValues, HandRank.FOUR_OF_A_KIND, currentHandString);
            } else if (isFullHouse(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.FULL_HOUSE, 0) + 1;
                handRankCounts.put(HandRank.FULL_HOUSE, count);
                actualizarRangeValues(RangeValues, HandRank.FULL_HOUSE, currentHandString);
            } else if (isFlush(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.FLUSH, 0) + 1;
                handRankCounts.put(HandRank.FLUSH, count);
                actualizarRangeValues(RangeValues, HandRank.FLUSH, currentHandString);
            } else if (isStraight(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.STRAIGHT, 0) + 1;
                handRankCounts.put(HandRank.STRAIGHT, count);
                actualizarRangeValues(RangeValues, HandRank.STRAIGHT, currentHandString);
            } else if (isThreeOfAKind(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.THREE_OF_A_KIND, 0) + 1;
                handRankCounts.put(HandRank.THREE_OF_A_KIND, count);
                actualizarRangeValues(RangeValues, HandRank.THREE_OF_A_KIND, currentHandString);
            } else if (isTwoPair(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.TWO_PAIR, 0) + 1;
                handRankCounts.put(HandRank.TWO_PAIR, count);
                actualizarRangeValues(RangeValues, HandRank.TWO_PAIR, currentHandString);
            } else if (isTopPair(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.TOP_PAIR, 0) + 1;
                handRankCounts.put(HandRank.TOP_PAIR, count);
                actualizarRangeValues(RangeValues, HandRank.TOP_PAIR, currentHandString);
            } else if (isMiddlePair(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.MIDDLE_PAIR, 0) + 1;
                handRankCounts.put(HandRank.MIDDLE_PAIR, count);
                actualizarRangeValues(RangeValues, HandRank.MIDDLE_PAIR, currentHandString);
            } else if (isWeakPair(combinedCards)) {
                int count = handRankCounts.getOrDefault(HandRank.WEAK_PAIR, 0) + 1;
                handRankCounts.put(HandRank.WEAK_PAIR, count);
                actualizarRangeValues(RangeValues, HandRank.WEAK_PAIR, currentHandString);
            } else if (isAceHigh(combinaciones)) {
                int count = handRankCounts.getOrDefault(HandRank.ACE_HIGH, 0) + 1;
                handRankCounts.put(HandRank.ACE_HIGH, count);
                actualizarRangeValues(RangeValues, HandRank.ACE_HIGH, currentHandString);
            }
            else {
            	int count = handRankCounts.getOrDefault(HandRank.NO_MADE_HAND, 0) + 1;
                handRankCounts.put(HandRank.NO_MADE_HAND, count);
                actualizarRangeValues(RangeValues, HandRank.NO_MADE_HAND, currentHandString);
            }
        }
    }


    private boolean isTopPair(List<String> combinedCards) {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean isMiddlePair(List<String> combinedCards) {
	// TODO Auto-generated method stub
	return false;
}


	private boolean isWeakPair(List<String> combinedCards) {
	// TODO Auto-generated method stub
	return false;
    }


	private boolean isAceHigh(List<String> combinaciones) {
	boolean existeAce = false;
		for (String carta:combinaciones) {
			if(carta.charAt(0) == 'A') {
				existeAce = true;
			}
		}
	return existeAce;
    }


	private void actualizarRangeValues(Map<HandRank, Map<String, Integer>> RangeValues, HandRank handRank, String combo) {
        Map<String, Integer> comboCounts = RangeValues.getOrDefault(handRank, new HashMap<>());
        comboCounts.put(combo, comboCounts.getOrDefault(combo, 0) + 1);
        RangeValues.put(handRank, comboCounts); // Vuelve a poner el mapa actualizado
    }


    private List<String> generarCombinaciones(String combo, List<String> board) {
    	List<String> combinaciones = new ArrayList<>();
        String rank = combo.substring(0, 1);
        String[] palos = {"h", "d", "c", "s"};

        if (combo.length() == 2) { 
            for (int i = 0; i < palos.length; i++) {
                for (int j = i + 1; j < palos.length; j++) {
                	if(!comprobarExistencia(board, rank+palos[i], rank+palos[j]))
                    	combinaciones.add(rank + palos[i] + rank + palos[j]);
                }
            }
        }
		else if (combo.length() == 3) {
			if(combo.endsWith("s")) {
				for (int i = 0; i < palos.length; i++) {
	                for (int j = i + 1; j < palos.length; j++) {
	                	if(!comprobarExistencia(board, rank+palos[i], rank+palos[j])) {
	                		if(j == i)
	                    		combinaciones.add(rank + palos[i] + rank + palos[j]);
	                	}
	                }
	            }
			}
			else{
				for (int i = 0; i < palos.length; i++) {
	                for (int j = i + 1; j < palos.length; j++) {
	                	if(!comprobarExistencia(board, rank+palos[i], rank+palos[j])) {
	                		if(j != i)
	                    		combinaciones.add(rank + palos[i] + rank + palos[j]);
	                	}
	                }
	            }
			}
		}
		return combinaciones;
	}
    
	private boolean comprobarExistencia(List<String> board,String palos1, String palos2) {
		boolean existe = false;
		for(int i = 0; i < board.size(); i++) {
	        
			if ((board.get(i).equals(palos1)) || (board.get(i).equals(palos2))) {
				existe = true;
			}
		}
		return existe;
	}

	// Método para verificar si una combinación específica es una escalera de color
	private boolean isStraightFlush(List<String> cards) {
	    // Creamos un mapa para almacenar las cartas por palo
	    Map<Character, List<Integer>> cardsBySuit = new HashMap<>();

	    // Agrupamos las cartas por palo
	    for (String card : cards) {
	        if (card.length() >= 2) { // Asegurarse de que la carta tenga al menos dos caracteres
	            char suit = card.charAt(1); // Obtenemos el palo de la carta
	            int rank = getRankValue(card.charAt(0)); // Obtener el valor de la carta (A, 2, ..., K)
	            cardsBySuit.putIfAbsent(suit, new ArrayList<>());
	            cardsBySuit.get(suit).add(rank);
	        }
	    }

	    // Verificamos si hay una escalera dentro de cada grupo de cartas del mismo palo
	    for (List<Integer> suitedCards : cardsBySuit.values()) {
	        if (suitedCards.size() >= 5 && isStraight(cards)) {
	            return true;
	        }
	    }

	    return false;
	}

	// Método auxiliar para obtener el valor numérico de una carta
	private int getRankValue(char rank) {
	    switch (rank) {
	        case 'A': return 14; // As
	        case 'K': return 13; // Rey
	        case 'Q': return 12; // Reina
	        case 'J': return 11; // Jota
	        default: return Character.getNumericValue(rank); // Para 2-10
	    }
	}



    // Métodos adicionales para verificar otras jugadas (escalera, color, full house, etc.)
    private boolean isFourOfAKind(List<String> cards) {
        // Creamos un mapa para contar cuántas veces aparece cada valor de carta
        Map<Character, Integer> cardCount = new HashMap<>();
        
        // Recorremos las cartas y contamos cuántas veces aparece cada valor (ignorar palo)
        for (String card : cards) {
            char rank = card.charAt(0);
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }
        
        // Verificamos si alguna carta aparece cuatro veces
        for (int count : cardCount.values()) {
            if (count == 4) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isFullHouse(List<String> cards) {
        Map<Character, Integer> cardCount = new HashMap<>();
        
        // Contar cuántas veces aparece cada carta
        for (String card : cards) {
            char rank = card.charAt(0);
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }

        boolean hasThreeOfAKind = false;
        boolean hasPair = false;

        // Verificar si hay un trío y una pareja
        for (int count : cardCount.values()) {
            if (count == 3) hasThreeOfAKind = true;
            if (count == 2) hasPair = true;
        }

        return hasThreeOfAKind && hasPair;
    }

    private boolean isFlush(List<String> cards) {
        Map<Character, List<String>> cardsBySuit = new HashMap<>();

        // Agrupamos las cartas por palo
        for (String card : cards) {
            if (card.length() >= 2) { // Asegurarse de que la carta tenga al menos dos caracteres
                char suit = card.charAt(1);
                cardsBySuit.putIfAbsent(suit, new ArrayList<>());
                cardsBySuit.get(suit).add(card);
            }
        }

        // Verificamos si hay un grupo de cinco o más cartas del mismo palo
        for (List<String> suitedCards : cardsBySuit.values()) {
            if (suitedCards.size() >= 5) {
                return true;
            }
        }

        return false;
    }



    private boolean isStraight(List<String> cards) {
        // Obtenemos los valores de las cartas ordenadas
        Set<Integer> cardValues = new HashSet<>();
        for (String card : cards) {
            cardValues.add(getCardValue(card.charAt(0)));
        }
        
        // Creamos una lista de valores únicos y la ordenamos
        List<Integer> sortedValues = new ArrayList<>(cardValues);
        Collections.sort(sortedValues);
        
        // Verificamos si hay cinco valores consecutivos
        int consecutiveCount = 0;
        for (int i = 0; i < sortedValues.size() - 1; i++) {
            if (sortedValues.get(i) + 1 == sortedValues.get(i + 1)) {
                consecutiveCount++;
                if (consecutiveCount == 4) {
                    return true;
                }
            } else {
                consecutiveCount = 0;
            }
        }
        
        // Verificamos la "escalera baja" (A, 2, 3, 4, 5)
        return sortedValues.contains(14) && sortedValues.contains(2) && 
               sortedValues.contains(3) && sortedValues.contains(4) && 
               sortedValues.contains(5);
    }

    private boolean isThreeOfAKind(List<String> cards) {
        // Creamos un mapa para contar cuántas veces aparece cada valor de carta
        Map<Character, Integer> cardCount = new HashMap<>();
        
        // Recorremos las cartas y contamos cuántas veces aparece cada valor (ignorar palo)
        for (String card : cards) {
            char rank = card.charAt(0); // Obtenemos el valor de la carta (primer carácter)
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }
        
        // Verificamos si alguna carta aparece tres veces
        for (int count : cardCount.values()) {
            if (count == 3) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isTwoPair(List<String> cards) {
        Map<Character, Integer> cardCount = new HashMap<>();
        
        // Contar cuántas veces aparece cada carta
        for (String card : cards) {
            char rank = card.charAt(0);
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }

        int pairCount = 0;

        // Contar cuántos pares hay
        for (int count : cardCount.values()) {
            if (count == 2) {
                pairCount++;
            }
        }

        return pairCount >= 2;
    }

    private boolean isPair(List<String> cards) {
        Map<Character, Integer> cardCount = new HashMap<>();
        
        // Contar cuántas veces aparece cada carta
        for (String card : cards) {
            char rank = card.charAt(0);
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }

        // Verificar si hay al menos un par
        for (int count : cardCount.values()) {
            if (count == 2) {
                return true; // Si encontramos un par, retornamos true
            }
        }

        return false; // Si no hay pares, retornamos false
    }

    
    private int getCardValue(char card) {
        switch (card) {
            case 'A': return 14; // Valor de As (puede ser 1 también para escalera baja)
            case 'K': return 13;
            case 'Q': return 12;
            case 'J': return 11;
            case 'T': return 10;
            case '9': return 9;
            case '8': return 8;
            case '7': return 7;
            case '6': return 6;
            case '5': return 5;
            case '4': return 4;
            case '3': return 3;
            case '2': return 2;
            default: throw new IllegalArgumentException("Valor de carta no válido: " + card);
        }
    }
    
    public void calculateProbabilities(List<String> range, List<String> board,
    		Map<HandRank, Double> probabilities, Map<HandRank, Map<String, Integer>> RangeValues,
    		Map<HandRank, Integer> handRankCounts) {
        countCombosByHandRank(range, board, handRankCounts, RangeValues);

        // Calcular el total de combinaciones posibles
        int totalCombos = handRankCounts.values().stream().mapToInt(Integer::intValue).sum();

        // Calcular la probabilidad de cada jugada
        for (Map.Entry<HandRank, Integer> entry : handRankCounts.entrySet()) {
            double probability = (entry.getValue() / (double) totalCombos) * 100;
            probabilities.put(entry.getKey(), probability);
        }
    }
    
    public void countCombosByHandRank(List<String> range, List<String> board, 
    	Map<HandRank, Integer> handRankCounts, Map<HandRank, Map<String, Integer>> RangeValues) {
        // Inicializamos el mapa con todas las jugadas en 0
        for (HandRank rank : HandRank.values()) {
            Map<String, Integer> innerMap = new HashMap<>();
            RangeValues.put(rank, innerMap);
            handRankCounts.put(rank, 0);
        }
        // Para cada combinación posible en el rango, evaluamos la mano y aumentamos el conteo
        for (String combo : range) {
        	// Evaluamos la mano usando evaluateHand
        	evaluateHand(combo, board, handRankCounts, RangeValues);
        }
    } 
}

//cardMap.put(cardName, cardMap.getOrDefault(cardName, 0) + 1); // Esto funcionará