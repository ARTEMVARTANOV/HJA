import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Clase Draws para evaluar proyectos de color y escaleras
public class Draws {
    private Logic logica = new Logic();
    
    public enum typeDraws{
        FLUSH_DRAW, GUTSHOT_DRAW, OPEN_ENDED_DRAW
    }
    
    public void prepareEvaluateDraws(String combo, List<String> board, Map<Draws.typeDraws, DrawData> drawsCount) {
        List<String> combinaciones = logica.generarCombinaciones(combo, board);
        for (String caso : combinaciones) {
            // Dividir 'caso' en dos cartas (asumiendo que siempre son 4 caracteres)
            String carta1 = caso.substring(0, 2); // Primera carta
            String carta2 = caso.substring(2, 4); // Segunda carta
            
            String currentHandString = carta1 + carta2;
            List<String> currentHand = Arrays.asList(carta1, carta2); // Convertir combo a lista de carta
            List<String> combinedCardsArray = new ArrayList<>(currentHand);
            combinedCardsArray.addAll(board);
            
            String boardCards = String.join("", board);
            String combinedCards = String.join("", combinedCardsArray);
            evaluateHand(combinedCards, drawsCount, currentHandString, boardCards); // Modificado para usar el arreglo combinado
        }
    }

	private void evaluateHand(String combinedCards, Map<Draws.typeDraws, DrawData> drawsCount, String currentHandString,
			String boardCards){
        // Contar los draws de tipo Flush
		if (!logica.isFlush(combinedCards) && FlushDrawCombos(combinedCards)) {
			DrawData drawData = drawsCount.get(Draws.typeDraws.FLUSH_DRAW);
            if (drawData != null) { // Verifica que drawData no sea null
            	drawData.incrementCount(); // Incrementa el contador de flush draws
                drawData.getStringList().add(currentHandString); // Agregar la mano actual
            }
		}
        // Contar los draws de tipo Open-Ended Straight
		if(!logica.isStraight(combinedCards) && OpenEndedStraightDrawCombos(combinedCards)) {
			DrawData drawData = drawsCount.get(Draws.typeDraws.OPEN_ENDED_DRAW);
			if (drawData != null) { // Verifica que drawData no sea null
            	drawData.incrementCount(); // Incrementa el contador de flush draws
                drawData.getStringList().add(currentHandString); // Agregar la mano actual
            }
		}
		
		if(!logica.isStraight(combinedCards) && StraightGutShotDrawCombos(combinedCards)) {
			DrawData drawData = drawsCount.get(Draws.typeDraws.GUTSHOT_DRAW);
			if (drawData != null) { // Verifica que drawData no sea null
            	drawData.incrementCount(); // Incrementa el contador de flush draws
                drawData.getStringList().add(currentHandString); // Agregar la mano actual
            }
		}
	}


	private boolean StraightGutShotDrawCombos(String combinedCards) {
		List<Integer> valores = obtenerValoresOrdenados(combinedCards);

        // Verificamos por bloques de 5 cartas posibles con un hueco en el medio
        for (int i = 0; i < valores.size() - 3; i++) {
            // Caso 1: Un hueco entre la primera y la tercera carta
            if (valores.get(i) + 2 == valores.get(i + 1) && 
                valores.get(i + 1) + 1 == valores.get(i + 2)) {
                return true;
            }
            // Caso 2: Un hueco entre la segunda y cuarta carta
            if (valores.get(i) + 1 == valores.get(i + 1) &&
                valores.get(i + 2) == valores.get(i + 1) + 2) {
                return true;
            }
        }

        //caso especial escalera baja
        if ((valores.contains(13) && valores.size() == 5 &&
            valores.contains(1) && valores.contains(2) && 
            !valores.contains(3) && valores.contains(4)) ||
        	((valores.contains(13) && valores.size() == 5 &&
            valores.contains(1) && !valores.contains(2) && 
            valores.contains(3) && valores.contains(4))||
        	((valores.contains(13) && valores.size() == 5 &&
            !valores.contains(1) && valores.contains(2) && 
            valores.contains(3) && valores.contains(4))||
        	((valores.contains(13) && valores.size() == 5 &&
            valores.contains(1) && valores.contains(2) && 
            valores.contains(3) && !valores.contains(4)))))){
            return true;
        }

     // Caso especial escalera alta
        if ((valores.contains(9) && valores.size() == 5 &&
             valores.contains(10) && valores.contains(11) && 
             !valores.contains(12) && valores.contains(13)) ||
            (valores.contains(9) && valores.size() == 5 &&
             valores.contains(10) && !valores.contains(11) && 
             valores.contains(12) && valores.contains(13)) ||
            (valores.contains(9) && valores.size() == 5 &&
             !valores.contains(10) && valores.contains(11) && 
             valores.contains(12) && valores.contains(13)) ||
            (!valores.contains(9) && valores.size() == 5 &&
             valores.contains(10) && valores.contains(11) && 
             valores.contains(12) && valores.contains(13))) {
            return true; // Es un gutshot especial de As-K-Q-J-T
        }

        return false;
	}

	private boolean OpenEndedStraightDrawCombos(String combinedCards) {
		List<Integer> valores = obtenerValoresOrdenados(combinedCards);
		
		if(!valores.contains(9) && valores.size() == 5 &&
	             valores.contains(10) && valores.contains(11) && 
	             valores.contains(12) && valores.contains(13)) {
			return false;
			
		}
		if(valores.contains(13) && valores.size() == 5 &&
	            valores.contains(1) && valores.contains(2) && 
	            valores.contains(3) && !valores.contains(4)) {
			return false;
		}
		
		// Verificamos por bloques de 4 cartas consecutivas
        for (int i = 0; i < valores.size() - 3; i++) {
            if (valores.get(i) + 1 == valores.get(i + 1) &&
                valores.get(i + 1) + 1 == valores.get(i + 2) &&
                valores.get(i + 2) + 1 == valores.get(i + 3)) {
                return true;
            }
        }
      //caso especial escalera baja
        if ((!valores.contains(13) && valores.size() == 5 &&
            valores.contains(1) && valores.contains(2) && 
            valores.contains(3) && valores.contains(4))) {
            return true;
        }

      //caso especial escalera alta
        if (((valores.contains(9) && valores.size() == 5 &&
            valores.contains(10) && valores.contains(11) && 
            valores.contains(12) && !valores.contains(13)))) {
            return true; // Es un gutshot especial de As-K-Q-J-T
        }


        return false;
		
	}
	
	private List<Integer> obtenerValoresOrdenados(String combinedCards) {
        List<Integer> valores = new ArrayList<>();
        // Iterar sobre los caracteres en las posiciones pares
        for (int i = 0; i < combinedCards.length(); i += 2) {
            char cartaChar = combinedCards.charAt(i);
            int valorNumerico = logica.charAnum(cartaChar);
            valores.add(valorNumerico);
        }
        Collections.sort(valores);
        return valores;
    }

	private boolean FlushDrawCombos(String combinedCards) {
		 // Contar cuántas cartas hay de cada palo
		 Map<Character, Integer> conteoPalos = new HashMap<>();

		 // Iterar sobre las cartas del combinedCards, saltando cada 2 caracteres (carta y palo)
		 for (int i = 0; i < combinedCards.length(); i += 2) {
		    char palo = combinedCards.charAt(i + 1); // Obtener el palo de la carta
		    conteoPalos.put(palo, conteoPalos.getOrDefault(palo, 0) + 1); // Contar el palo
		 }

		    // Verificar si hay al menos 4 cartas del mismo palo
		 for (int count : conteoPalos.values()) {
		    if (count >= 4) {
		    	return true; // Devuelve 1 si hay al menos un draw de flush
		    }
		 }

		 return false; // No hay draw de flush
	}
	
    private int countInsideStraightDrawCombos(List<String> range, List<String> board) {
        List<Integer> ranks = getRanks(range, board);
        ranks.sort(Integer::compareTo);

        // Verificar si hay una secuencia de tres cartas con un hueco
        for (int i = 0; i < ranks.size() - 3; i++) {
            if (ranks.get(i + 3) - ranks.get(i) == 4 &&
                (ranks.get(i + 1) != ranks.get(i) + 1 || ranks.get(i + 2) != ranks.get(i) + 2)) {
                return 4; // 4 outs para una escalera con hueco
            }
        }
        return 0;
    }

    private List<Integer> getRanks(List<String> range, List<String> board) {
        List<Integer> ranks = new ArrayList<>();
        for (String card : range) {
            ranks.add(logica.charAnum(card.charAt(0)));
        }
        for (String card : board) {
            ranks.add(logica.charAnum(card.charAt(0)));
        }
        return ranks;
    }

    private List<String> splitCards(String cards) {
        List<String> cardList = new ArrayList<>();
        for (int i = 0; i < cards.length(); i += 2) {
            cardList.add(cards.substring(i, Math.min(i + 2, cards.length())));
        }
        return cardList;
    }
}