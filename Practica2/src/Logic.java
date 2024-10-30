import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Logic {
    // Enumeración para las jugadas posibles
    public enum HandRank {
        ROYAL_FLUSH, STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH, 
        STRAIGHT, THREE_OF_A_KIND, TWO_PAIR, OVERPAIR, TOP_PAIR,
        POCKET_PAIR_BELOW_TOP_PAIR, MIDDLE_PAIR, WEAK_PAIR, ACE_HIGH,
        NO_MADE_HAND
    }

    // Evaluar una mano dada (rango + board) y devolver la mejor jugada
    public void prepareEvaluateHand(String combo, List<String> board, 
            Map<HandRank, Integer> handRankCounts, Map<HandRank, Map<String, Integer>> RangeValues) {
        List<String> combinaciones = generarCombinaciones(combo, board);
        for (String caso : combinaciones) {
            // Dividir 'caso' en dos cartas (asumiendo que siempre son 4 caracteres)
            String carta1 = caso.substring(0, 2); // Primera carta
            String carta2 = caso.substring(2, 4); // Segunda carta
            
            String currentHandString = carta1 + carta2;
            List<String> currentHand = Arrays.asList(carta1, carta2); // Convertir combo a lista de carta
            List<String> combinedCardsArray = new ArrayList<>(currentHand);
            combinedCardsArray.addAll(board);
            
            String combinedCards = String.join("", combinedCardsArray);
            evaluateHand(combinedCards, handRankCounts, RangeValues, currentHandString);
        }
    }


    private void evaluateHand(String combinedCards, Map<HandRank, Integer> handRankCounts,
			Map<HandRank, Map<String, Integer>> RangeValues, String currentHandString) {
    	// Evaluar las manos
    	if (isRoyalFlush(combinedCards)) {
            int count = handRankCounts.getOrDefault(HandRank.ROYAL_FLUSH, 0) + 1;
            handRankCounts.put(HandRank.ROYAL_FLUSH, count);
            actualizarRangeValues(RangeValues, HandRank.ROYAL_FLUSH, currentHandString);
        } else if (isFlush(combinedCards) && isStraight(combinedCards)) {
            int count = handRankCounts.getOrDefault(HandRank.STRAIGHT_FLUSH, 0) + 1;
            handRankCounts.put(HandRank.STRAIGHT_FLUSH, count);
            actualizarRangeValues(RangeValues, HandRank.STRAIGHT_FLUSH, currentHandString);
        } else if (isFourOfAKind(combinedCards)) {
            int count = handRankCounts.getOrDefault(HandRank.FOUR_OF_A_KIND, 0) + 1;
            handRankCounts.put(HandRank.FOUR_OF_A_KIND, count);
            actualizarRangeValues(RangeValues, HandRank.FOUR_OF_A_KIND, currentHandString);
        } else if (isThreeOfAKind(combinedCards) && isPair(combinedCards)) {
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
        }else if (isOverPair(combinedCards)) {
            int count = handRankCounts.getOrDefault(HandRank.OVERPAIR, 0) + 1;
            handRankCounts.put(HandRank.OVERPAIR, count);
            actualizarRangeValues(RangeValues, HandRank.OVERPAIR, currentHandString);
        }else if (isTopPair(combinedCards)) {
            int count = handRankCounts.getOrDefault(HandRank.TOP_PAIR, 0) + 1;
            handRankCounts.put(HandRank.TOP_PAIR, count);
            actualizarRangeValues(RangeValues, HandRank.TOP_PAIR, currentHandString);
        }else if (isPocketPairBelowTopPair(combinedCards)) {
            int count = handRankCounts.getOrDefault(HandRank.POCKET_PAIR_BELOW_TOP_PAIR, 0) + 1;
            handRankCounts.put(HandRank.POCKET_PAIR_BELOW_TOP_PAIR, count);
            actualizarRangeValues(RangeValues, HandRank.POCKET_PAIR_BELOW_TOP_PAIR, currentHandString);
        }else if (isMiddlePair(combinedCards)) {
            int count = handRankCounts.getOrDefault(HandRank.MIDDLE_PAIR, 0) + 1;
            handRankCounts.put(HandRank.MIDDLE_PAIR, count);
            actualizarRangeValues(RangeValues, HandRank.MIDDLE_PAIR, currentHandString);
        }else if (isWeakPair(combinedCards)) {
            int count = handRankCounts.getOrDefault(HandRank.WEAK_PAIR, 0) + 1;
            handRankCounts.put(HandRank.WEAK_PAIR, count);
            actualizarRangeValues(RangeValues, HandRank.WEAK_PAIR, currentHandString);
        }else if (combinedCards.contains("A")) {
            int count = handRankCounts.getOrDefault(HandRank.ACE_HIGH, 0) + 1;
            handRankCounts.put(HandRank.ACE_HIGH, count);
            actualizarRangeValues(RangeValues, HandRank.ACE_HIGH, currentHandString);
        }else {
        	int count = handRankCounts.getOrDefault(HandRank.NO_MADE_HAND, 0) + 1;
            handRankCounts.put(HandRank.NO_MADE_HAND, count);
            actualizarRangeValues(RangeValues, HandRank.NO_MADE_HAND, currentHandString);
        }
		
	}


	private boolean isPocketPairBelowTopPair(List<String> combinedCards) {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean isOverPair(List<String> combinedCards) {
		// TODO Auto-generated method stub
		return false;
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

	private boolean isRoyalFlush(String cards) {
	    if (!isFlush(cards)) return false;

	    boolean hasTen = cards.contains("T");
	    boolean hasJack = cards.contains("J");
	    boolean hasQueen = cards.contains("Q");
	    boolean hasKing = cards.contains("K");
	    boolean hasAce = cards.contains("A");

	    return hasTen && hasJack && hasQueen && hasKing && hasAce;
	}
	
	private boolean isFlush(String cards) {
	    Set<Character> suits = new HashSet<>();

	    // Recorrer las cartas y agregar sus palos al conjunto
	    for (int i = 1; i < cards.length(); i += 2) {
	        char suit = cards.charAt(i); 
	        suits.add(suit); // Agregar el palo al conjunto
	    }

	    return suits.size() == 1;
	}



    private boolean isStraight(String cards) {
        // Usar un conjunto para almacenar los valores de las cartas
        Set<Integer> cardValues = new HashSet<>();
        boolean straight = false;
        
        for (int i = 0; i < cards.length(); i += 2) {
            char rank = cards.charAt(i); 
            int value = charAnum(rank); 
            if (value != -1) {
                cardValues.add(value); 
            }
        }

        // Verificar si hay cinco cartas consecutivas
        for (int i = 0; i <= 10; i++) { // Solo necesitamos verificar hasta el 10
            boolean found = true;
            for (int j = 0; j < 5; j++) {
                if (!cardValues.contains(i + j)) {
                    found = false; // Si no se encuentra una carta en la secuencia
                    break;
                }
            }
            if (found) {
            	straight =  true; // Si encontramos una secuencia de cinco cartas
            }
        }
        
        //caso especial As como carta mas baja
        boolean as = cards.contains("A");
        boolean dos = cards.contains("2");
        boolean tres = cards.contains("3");
        boolean cuatro = cards.contains("4");
        boolean cinco = cards.contains("5");

        if (as && dos && tres && cuatro && cinco) straight = true;
        
        return straight;
        
    }


	private int charAnum(char c) {
	    int num = -1;
	    switch (c) {
	        case 'A': {
	            num = 13; 
	            break;}
	        case 'K': {
	            num = 12; 
	            break;}
	        case 'Q':{
	            num = 11;
	            break;}
	        case 'J':{
	            num = 10;
	            break;}
	        case 'T':{
	            num = 9;
	            break;}
	        case '9':{
	            num = 8;
	            break;}
	        case '8':{
	            num = 7;
	            break;}
	        case '7':{
	            num = 6;
	            break;}
	        case '6':{
	            num = 5;
	            break;}
	        case '5':{
	            num = 4; 
	            break;}
	        case '4':{
	            num = 3; 
	            break;}
	        case '3':{
	            num = 2; 
	            break;}
	        case '2':{
	            num = 1;
	            break;}
	        default:{
	            System.out.println("Caracter no válido: " + c);
	            break;}
	    }
	    return num;
	}




	private boolean isFourOfAKind(String cards) {
	    Map<Character, Integer> cardCount = new HashMap<>();
	    
	    // Recorremos las cartas y contamos cuántas veces aparece cada valor (ignorando el palo)
	    for (int i = 0; i < cards.length(); i += 2) {
	        char rank = cards.charAt(i); // Obtenemos el rango de cada carta
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

    private boolean isThreeOfAKind(String cards) {
        Map<Character, Integer> cardCount = new HashMap<>();

        // Contar cuántas veces aparece cada carta
        for (int i = 0; i < cards.length(); i += 2) {
            char rank = cards.charAt(i); // Obtenemos el rango de cada carta
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }

        // Verificar si alguna carta aparece tres veces
        for (int count : cardCount.values()) {
            if (count == 3) {
                return true;
            }
        }

        return false;
    }

    private boolean isTwoPair(String cards) {
        Map<Character, Integer> cardCount = new HashMap<>();
        
        // Contar cuántas veces aparece cada carta
        for (int i = 0; i < cards.length(); i += 2) {
            char rank = cards.charAt(i); // Obtenemos el rango de cada carta
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }

        int pairCount = 0;

        // Contar cuántos pares hay
        for (int count : cardCount.values()) {
            if (count == 2) {
                pairCount++;
            }
        }

        return pairCount == 2; // Retorna true si hay dos pares
    }


    private boolean isPair(String cards) {
        Map<Character, Integer> cardCount = new HashMap<>();

        // Contar cuántas veces aparece cada carta
        for (int i = 0; i < cards.length(); i += 2) {
            char rank = cards.charAt(i); // Obtenemos el rango de cada carta
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

}
