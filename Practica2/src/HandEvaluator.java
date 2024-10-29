import java.util.*;

public class HandEvaluator {
    // Enumeración para las jugadas posibles
    public enum HandRank {
        STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH, STRAIGHT,
        THREE_OF_A_KIND, TWO_PAIR, PAIR, HIGH_CARD,
        OVERPAIR, TOP_PAIR, POCKET_PAIR_BELOW_TOP_PAIR, MIDDLE_PAIR, WEAK_PAIR
    }

    // Evaluar una mano dada (rango + board) y devolver la mejor jugada
    public HandRank evaluateHand(List<String> range, List<String> board) {
        // Combinamos el rango y el board
        List<String> combinedCards = new ArrayList<>(range);
        combinedCards.addAll(board);

        // Comprobar jugadas de mayor a menor rango
        if (isStraightFlush(combinedCards)) return HandRank.STRAIGHT_FLUSH;
        if (isFourOfAKind(combinedCards)) return HandRank.FOUR_OF_A_KIND;
        if (isFullHouse(combinedCards)) return HandRank.FULL_HOUSE;
        if (isFlush(combinedCards)) return HandRank.FLUSH;
        if (isStraight(combinedCards)) return HandRank.STRAIGHT;
        if (isThreeOfAKind(combinedCards)) return HandRank.THREE_OF_A_KIND;
        if (isTwoPair(combinedCards)) return HandRank.TWO_PAIR;

        // Llamar a isPair para verificar si hay una pareja y obtener el tipo de pareja
        HandRank pairRank = isPair(range, board);
        if (pairRank != null) {
            return pairRank; // Devolver el tipo de pareja
        }

        // Si ninguna jugada se cumple, es una carta alta
        return HandRank.HIGH_CARD;
    }

    // Método para verificar si una combinación específica es una escalera de color, poker, etc.
    private boolean isStraightFlush(List<String> cards) {
        // Creamos un mapa para almacenar las cartas por palo
        Map<Character, List<String>> cardsBySuit = new HashMap<>();

        // Agrupamos las cartas por palo
        for (String card : cards) {
            if (card.length() >= 2) { // Asegurarse de que la carta tenga al menos dos caracteres
                char suit = card.charAt(1); // Obtenemos el palo de la carta
                cardsBySuit.putIfAbsent(suit, new ArrayList<>());
                cardsBySuit.get(suit).add(card);
            }
        }

        // Verificamos si hay una escalera dentro de cada grupo de cartas del mismo palo
        for (List<String> suitedCards : cardsBySuit.values()) {
            if (isStraight(suitedCards)) {
                return true;
            }
        }

        return false;
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

    private HandRank isPair(List<String> range, List<String> board) {
        // Creamos un mapa para contar cuántas veces aparece cada valor de carta
        Map<Character, Integer> cardCount = new HashMap<>();
        
        // Recorremos las cartas del rango y del board, contando cuántas veces aparece cada valor (ignoramos palo)
        for (String card : range) {
            char rank = card.charAt(0);
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }

        for (String card : board) {
            char rank = card.charAt(0);
            cardCount.put(rank, cardCount.getOrDefault(rank, 0) + 1);
        }

        // Buscar si hay una pareja en las cartas del rango
        for (String card : range) {
            char rank = card.charAt(0);
            if (cardCount.get(rank) == 2) {
                // Se ha detectado una pareja, ahora distinguimos entre las subcategorías
                return evaluatePairType(rank, board);
            }
        }

        // Si no se encuentra ninguna pareja, devolver null (no hay pareja)
        return null;
    }

    private boolean isHighCard(List<String> cards) {
        // Si ninguna de las jugadas anteriores es cierta, la jugada es una carta alta
        return true; // La lógica para la carta alta es implícita cuando las demás jugadas no se cumplen
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
    
    private HandRank evaluatePairType(char pairRank, List<String> board) {
        // Obtener la carta más alta y la segunda carta más alta del board
        int highestBoardCard = getHighestCardValue(board);
        int secondHighestBoardCard = getSecondHighestCardValue(board);

        // Obtener el valor de la pareja detectada
        int pairValue = getCardValue(pairRank);

        // Verificar si es una overpair
        if (pairValue > highestBoardCard) {
            return HandRank.OVERPAIR;
        }

        // Verificar si es una top pair
        if (pairValue == highestBoardCard) {
            return HandRank.TOP_PAIR;
        }

        // Verificar si es una pocket pair below top pair
        if (pairValue < highestBoardCard && pairValue > secondHighestBoardCard) {
            return HandRank.POCKET_PAIR_BELOW_TOP_PAIR;
        }

        // Verificar si es una middle pair
        if (pairValue == secondHighestBoardCard) {
            return HandRank.MIDDLE_PAIR;
        }

        // Si ninguna de las anteriores se cumple, es una weak pair
        return HandRank.WEAK_PAIR;
    }
    private int getHighestCardValue(List<String> board) {
        int highestValue = 0;
        for (String card : board) {
            int cardValue = getCardValue(card.charAt(0));
            if (cardValue > highestValue) {
                highestValue = cardValue;
            }
        }
        return highestValue;
    }

    private int getSecondHighestCardValue(List<String> board) {
        int highestValue = 0;
        int secondHighestValue = 0;
        for (String card : board) {
            int cardValue = getCardValue(card.charAt(0));
            if (cardValue > highestValue) {
                secondHighestValue = highestValue;
                highestValue = cardValue;
            } else if (cardValue > secondHighestValue) {
                secondHighestValue = cardValue;
            }
        }
        return secondHighestValue;
    }
    
    public Map<HandRank, Double> calculateProbabilities(List<String> range, List<String> board) {
        Map<HandRank, Integer> comboCounts = countCombosByHandRank(range, board);
        Map<HandRank, Double> probabilities = new HashMap<>();

        // Calcular el total de combinaciones posibles
        int totalCombos = comboCounts.values().stream().mapToInt(Integer::intValue).sum();

        // Calcular la probabilidad de cada jugada
        for (Map.Entry<HandRank, Integer> entry : comboCounts.entrySet()) {
            double probability = (entry.getValue() / (double) totalCombos) * 100;
            probabilities.put(entry.getKey(), probability);
        }

        return probabilities;
    }
    
    public Map<HandRank, Integer> countCombosByHandRank(List<String> range, List<String> board) {
        Map<HandRank, Integer> handRankCounts = new HashMap<>();
        
        // Inicializamos el mapa con todas las jugadas en 0
        for (HandRank rank : HandRank.values()) {
            handRankCounts.put(rank, 0);
        }

        // Para cada combinación posible en el rango, evaluamos la mano y aumentamos el conteo
        for (String combo : range) {
            List<String> currentHand = Arrays.asList(combo.split("")); // Convertir combo a lista de cartas

            // Evaluamos la mano usando evaluateHand
            HandRank rank = evaluateHand(currentHand, board);
            handRankCounts.put(rank, handRankCounts.get(rank) + 1);
        }

        return handRankCounts;
    } 
}
