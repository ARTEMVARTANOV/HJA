package Default;

import java.util.*;

public class ManoPoker {
	private String cartas = "";

    // Enumeración para las jugadas posibles
 	public enum HandRank {
         ROYAL_FLUSH, STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH, 
         STRAIGHT, THREE_OF_A_KIND, TWO_PAIR, PAIR, HIGH_CARD
     }
    
    public ManoPoker(String mano) {
        this.cartas = mano;
    }

	public Map<HandRank, String> evaluarMano() {
		// TODO Auto-generated method stub
		Map<HandRank, String> resultado = new HashMap<>();
 		String aux = "";
 		reorganizarStrings pokerUtils = new reorganizarStrings();
		LogicaManoPoker logica = new LogicaManoPoker();
 		// Evaluar las manos
    	if (isRoyalFlush(cartas)) {
    		aux = logica.ordenarCartas(cartas);
    		resultado.put(HandRank.ROYAL_FLUSH, aux);
        } else if (isFlush(cartas) && isStraight(cartas)) {
        	aux = logica.ordenarCartas(cartas);
        	resultado.put(HandRank.STRAIGHT_FLUSH, aux);
        } else if (isFourOfAKind(cartas)) {
        	aux = pokerUtils.reorganizarFourOfAKind(cartas); 
        	resultado.put(HandRank.FOUR_OF_A_KIND, aux);
        } else if (isThreeOfAKind(cartas) && isPair(cartas)) {
        	aux = pokerUtils.reorganizarFullHouse(cartas);
        	resultado.put(HandRank.FULL_HOUSE, aux);
        } else if (isFlush(cartas)) {
        	aux = logica.ordenarCartas(cartas);
        	resultado.put(HandRank.FLUSH, aux);
        } else if (isStraight(cartas)) {
        	aux = logica.ordenarCartas(cartas);
        	resultado.put(HandRank.STRAIGHT, aux);
        } else if (isThreeOfAKind(cartas)) {
        	aux = pokerUtils.reorganizarThreeOfAKind(cartas);
        	resultado.put(HandRank.THREE_OF_A_KIND, aux);
        } else if (isTwoPair(cartas)) {
        	aux = pokerUtils.reorganizarTwoPair(cartas);
        	resultado.put(HandRank.TWO_PAIR, aux);
        }else if (isPair(cartas)) {
        	aux = pokerUtils.reorganizarPair(cartas);
        	resultado.put(HandRank.PAIR, aux);
        }else {
        	aux = logica.ordenarCartas(cartas);
        	resultado.put(HandRank.HIGH_CARD, aux);
        }
 		
 		return resultado;
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
	
	public static int charAnum(char c) {
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
}