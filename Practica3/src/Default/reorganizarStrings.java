package Default;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class reorganizarStrings {
	// Método que reorganiza las cartas para el Four of a Kind
		public String reorganizarFourOfAKind(String cartas) {
		    // Crear 5 variables para cada carta
		    String carta1 = cartas.substring(0, 2);
		    String carta2 = cartas.substring(2, 4); 
		    String carta3 = cartas.substring(4, 6);
		    String carta4 = cartas.substring(6, 8); 
		    String carta5 = cartas.substring(8, 10);

		    // Usar un array para almacenar las cartas
		    String[] cartasArray = {carta1, carta2, carta3, carta4, carta5};
		    
		    // Contar las repeticiones de las cartas por su valor (sin tener en cuenta el palo)
		    Map<Character, Integer> cartaCount = new HashMap<>();
		    for (String carta : cartasArray) {
		        char valor = carta.charAt(0);  // Tomar solo el valor (primer carácter)
		        cartaCount.put(valor, cartaCount.getOrDefault(valor, 0) + 1);
		    }
		    
		    // Variables para almacenar las cartas repetidas y la carta restante
		    String fourOfAKind = "";
		    String kicker = "";

		    // Determinar qué carta es la que se repite 4 veces y cuál es la carta restante
		    for (Map.Entry<Character, Integer> entry : cartaCount.entrySet()) {
		        if (entry.getValue() == 4) {
		            fourOfAKind = String.valueOf(entry.getKey());  // Guardar el valor de la carta repetida
		        } else if (entry.getValue() == 1) {
		            kicker = String.valueOf(entry.getKey());  // Guardar el valor de la carta restante
		        }
		    }

		    // Reorganizar las cartas: primero las 4 iguales (con los palos originales), luego la carta restante
		    StringBuilder resultado = new StringBuilder();
		    for (String carta : cartasArray) {
		        if (carta.charAt(0) == fourOfAKind.charAt(0)) {
		            resultado.append(carta);  // Añadir las 4 cartas iguales
		        }
		    }

		    // Añadir la carta restante (el "kicker")
		    for (String carta : cartasArray) {
		        if (carta.charAt(0) == kicker.charAt(0)) {
		            resultado.append(carta);  // Añadir la carta restante
		        }
		    }

		    return resultado.toString();
		}
		
		// Método que reorganiza las cartas para el Full House
	    public String reorganizarFullHouse(String cartas) {
	        // Crear 5 variables para cada carta
	        String carta1 = cartas.substring(0, 2);
	        String carta2 = cartas.substring(2, 4); 
	        String carta3 = cartas.substring(4, 6);
	        String carta4 = cartas.substring(6, 8); 
	        String carta5 = cartas.substring(8, 10);

	        // Usar un array para almacenar las cartas
	        String[] cartasArray = {carta1, carta2, carta3, carta4, carta5};
	        
	        // Contar las repeticiones de las cartas por su valor (sin tener en cuenta el palo)
	        Map<Character, Integer> cartaCount = new HashMap<>();
	        for (String carta : cartasArray) {
	            char valor = carta.charAt(0);  // Tomar solo el valor (primer carácter)
	            cartaCount.put(valor, cartaCount.getOrDefault(valor, 0) + 1);
	        }
	        
	        // Variables para almacenar las cartas del trío y la pareja
	        String trio = "";
	        String pareja = "";

	        // Determinar qué carta es la que se repite 3 veces (trío) y cuál es la que se repite 2 veces (pareja)
	        for (Map.Entry<Character, Integer> entry : cartaCount.entrySet()) {
	            if (entry.getValue() == 3) {
	                trio = String.valueOf(entry.getKey());  // Guardar el valor del trío
	            } else if (entry.getValue() == 2) {
	                pareja = String.valueOf(entry.getKey());  // Guardar el valor de la pareja
	            }
	        }

	        // Reorganizar las cartas: primero las 3 del trío, luego las 2 de la pareja
	        StringBuilder resultado = new StringBuilder();
	        // Añadir las 3 cartas del trío
	        for (String carta : cartasArray) {
	            if (carta.charAt(0) == trio.charAt(0)) {
	                resultado.append(carta);
	            }
	        }

	        // Añadir las 2 cartas de la pareja
	        for (String carta : cartasArray) {
	            if (carta.charAt(0) == pareja.charAt(0)) {
	                resultado.append(carta);
	            }
	        }

	        return resultado.toString();
	    }
	    
	    // Método que reorganiza las cartas para el Three of a Kind y ordena el resto de mayor a menor
	    public String reorganizarThreeOfAKind(String cartas) {
	        // Crear 5 variables para cada carta
	        String carta1 = cartas.substring(0, 2);
	        String carta2 = cartas.substring(2, 4); 
	        String carta3 = cartas.substring(4, 6);
	        String carta4 = cartas.substring(6, 8); 
	        String carta5 = cartas.substring(8, 10);

	        // Usar un array para almacenar las cartas
	        String[] cartasArray = {carta1, carta2, carta3, carta4, carta5};
	        
	        // Contar las repeticiones de las cartas por su valor (sin tener en cuenta el palo)
	        Map<Character, Integer> cartaCount = new HashMap<>();
	        for (String carta : cartasArray) {
	            char valor = carta.charAt(0);  // Tomar solo el valor (primer carácter)
	            cartaCount.put(valor, cartaCount.getOrDefault(valor, 0) + 1);
	        }
	        
	        // Variables para almacenar el trío y las cartas restantes
	        String threeOfAKind = "";
	        List<String> resto = new ArrayList<>();

	        // Determinar qué carta se repite 3 veces (el trío) y las cartas restantes
	        for (Map.Entry<Character, Integer> entry : cartaCount.entrySet()) {
	            if (entry.getValue() == 3) {
	                threeOfAKind = String.valueOf(entry.getKey());  // Guardar el valor del trío
	            } else if (entry.getValue() == 1 || entry.getValue() == 2) {
	                // Guardar las cartas restantes
	                for (String carta : cartasArray) {
	                    if (carta.charAt(0) == entry.getKey()) {
	                        resto.add(carta);
	                    }
	                }
	            }
	        }

	        // Ordenar las cartas restantes de mayor a menor por su valor
	        Collections.sort(resto, new Comparator<String>() {
	            @Override
	            public int compare(String carta1, String carta2) {
	                int valor1 = getValorNumerico(carta1.charAt(0));
	                int valor2 = getValorNumerico(carta2.charAt(0));
	                return Integer.compare(valor2, valor1);  // Ordenar de mayor a menor
	            }
	        });

	        // Reorganizar las cartas: primero el trío, luego el resto ordenado
	        StringBuilder resultado = new StringBuilder();
	        
	        // Añadir las 3 cartas del trío
	        for (String carta : cartasArray) {
	            if (carta.charAt(0) == threeOfAKind.charAt(0)) {
	                resultado.append(carta);
	            }
	        }

	        // Añadir las cartas restantes ordenadas
	        for (String carta : resto) {
	            resultado.append(carta);
	        }

	        return resultado.toString();
	    }

	 // Método que reorganiza las cartas para el Two Pair y coloca la pareja más alta primero
	    public String reorganizarTwoPair(String cartas) {
	        // Crear 5 variables para cada carta
	        String carta1 = cartas.substring(0, 2);
	        String carta2 = cartas.substring(2, 4); 
	        String carta3 = cartas.substring(4, 6);
	        String carta4 = cartas.substring(6, 8); 
	        String carta5 = cartas.substring(8, 10);

	        // Usar un array para almacenar las cartas
	        String[] cartasArray = {carta1, carta2, carta3, carta4, carta5};
	        
	        // Contar las repeticiones de las cartas por su valor (sin tener en cuenta el palo)
	        Map<Character, Integer> cartaCount = new HashMap<>();
	        for (String carta : cartasArray) {
	            char valor = carta.charAt(0);  // Tomar solo el valor (primer carácter)
	            cartaCount.put(valor, cartaCount.getOrDefault(valor, 0) + 1);
	        }

	        // Variables para almacenar las dos parejas y la carta restante
	        String pair1 = "";
	        String pair2 = "";
	        String kicker = "";

	        // Determinar qué valores se repiten dos veces (las parejas) y cuál es la carta restante
	        for (Map.Entry<Character, Integer> entry : cartaCount.entrySet()) {
	            if (entry.getValue() == 2) {
	                if (pair1.isEmpty()) {
	                    pair1 = String.valueOf(entry.getKey());  // Guardar el primer par
	                } else {
	                    pair2 = String.valueOf(entry.getKey());  // Guardar el segundo par
	                }
	            } else if (entry.getValue() == 1) {
	                kicker = String.valueOf(entry.getKey());  // Guardar la carta restante
	            }
	        }

	        // Separar las cartas en dos grupos: las cartas de cada pareja y la carta restante
	        List<String> pair1Cards = new ArrayList<>();
	        List<String> pair2Cards = new ArrayList<>();
	        List<String> kickerCard = new ArrayList<>();
	        
	        for (String carta : cartasArray) {
	            if (carta.charAt(0) == pair1.charAt(0)) {
	                pair1Cards.add(carta);
	            } else if (carta.charAt(0) == pair2.charAt(0)) {
	                pair2Cards.add(carta);
	            } else {
	                kickerCard.add(carta);
	            }
	        }

	        // Ordenar las parejas de mayor a menor por su valor
	        if (getValorNumerico(pair1.charAt(0)) < getValorNumerico(pair2.charAt(0))) {
	            // Intercambiar las parejas si es necesario para que la pareja más alta esté primero
	            List<String> temp = pair1Cards;
	            pair1Cards = pair2Cards;
	            pair2Cards = temp;
	        }

	        // Reorganizar las cartas: primero la pareja más alta, luego la segunda pareja, y la carta restante al final
	        StringBuilder resultado = new StringBuilder();

	        // Añadir la pareja más alta
	        for (String carta : pair1Cards) {
	            resultado.append(carta);
	        }

	        // Añadir la segunda pareja
	        for (String carta : pair2Cards) {
	            resultado.append(carta);
	        }

	        // Añadir la carta restante
	        for (String carta : kickerCard) {
	            resultado.append(carta);
	        }

	        return resultado.toString();
	    }
	    
	    public String reorganizarPair(String cartas) {
	        // Crear 5 variables para cada carta
	        String carta1 = cartas.substring(0, 2);
	        String carta2 = cartas.substring(2, 4); 
	        String carta3 = cartas.substring(4, 6);
	        String carta4 = cartas.substring(6, 8); 
	        String carta5 = cartas.substring(8, 10);

	        // Usar un array para almacenar las cartas
	        String[] cartasArray = {carta1, carta2, carta3, carta4, carta5};
	        
	        // Contar las repeticiones de las cartas por su valor (sin tener en cuenta el palo)
	        Map<Character, Integer> cartaCount = new HashMap<>();
	        for (String carta : cartasArray) {
	            char valor = carta.charAt(0);  // Tomar solo el valor (primer carácter)
	            cartaCount.put(valor, cartaCount.getOrDefault(valor, 0) + 1);
	        }

	        // Variables para almacenar la pareja y la carta restante
	        String pair = "";
	        List<String> kickerCards = new ArrayList<>();

	        // Determinar qué valor se repite dos veces (el par) y cuáles son las cartas restantes
	        for (Map.Entry<Character, Integer> entry : cartaCount.entrySet()) {
	            if (entry.getValue() == 2) {
	                pair = String.valueOf(entry.getKey());  // Guardar el valor de la pareja
	            } else if (entry.getValue() == 1) {
	                kickerCards.add(entry.getKey() + "");  // Añadir la carta restante
	            }
	        }

	        // Separar las cartas en dos grupos: las cartas del par y las cartas restantes
	        List<String> pairCards = new ArrayList<>();
	        List<String> otherCards = new ArrayList<>();
	        
	        for (String carta : cartasArray) {
	            if (carta.charAt(0) == pair.charAt(0)) {
	                pairCards.add(carta);  // Añadir las cartas del par
	            } else {
	                otherCards.add(carta);  // Añadir las cartas restantes
	            }
	        }

	        // Ordenar las cartas restantes de mayor a menor según el valor
	        otherCards.sort((c1, c2) -> Integer.compare(getValorNumerico(c2.charAt(0)), getValorNumerico(c1.charAt(0))));

	        // Reorganizar las cartas: primero la pareja, luego las cartas restantes ordenadas
	        StringBuilder resultado = new StringBuilder();

	        // Añadir la pareja
	        for (String carta : pairCards) {
	            resultado.append(carta);
	        }

	        // Añadir las cartas restantes ordenadas
	        for (String carta : otherCards) {
	            resultado.append(carta);
	        }

	        return resultado.toString();
	    }
	    
	    // Método para obtener el valor numérico de una carta (solo el valor, sin el palo)
	    public int getValorNumerico(char valor) {
	        switch (valor) {
	            case 'A': return 14;
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
	            default: return 0;  // En caso de un valor desconocido
	        }
	    }

}
