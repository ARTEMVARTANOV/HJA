package Default;

import java.util.*;

public class ProbabilidadPoker {
	private static final int NUM_SIMULACIONES = 1000;

    public static Map<Integer, Double> calcularProbabilidad(Map<Integer, String[]> manosJugadores, 
    		List<String> cartasComunitarias, List<String> mazoRestante, boolean omaha) {
    	Map<Integer, Double> victorias = new HashMap<>();
        for (int jugadorId : manosJugadores.keySet()) {
            victorias.put(jugadorId, 0.0);
        }

        Random random = new Random();

        for (int i = 0; i < NUM_SIMULACIONES; i++) {
            // Copiar el mazo restante y barajar
            List<String> mazo = new ArrayList<>(mazoRestante);
            Collections.shuffle(mazo, random);

            // Completar el board
            List<String> boardCompleto = new ArrayList<>(cartasComunitarias);
            while (boardCompleto.size() < 5) {
                boardCompleto.add(mazo.remove(0));
            } 

            // Evaluar manos
            Map<Integer, ManoPoker.HandRank> mejoresRanks = new HashMap<>();
            Map<Integer, String> mejoresManos = new HashMap<>();
            for (Map.Entry<Integer, String[]> jugador : manosJugadores.entrySet()) {
            	// Combinar las cartas del jugador con el board completo
                LogicaManoPoker logica = new LogicaManoPoker(jugador.getValue(), boardCompleto, omaha);	

                mejoresRanks.put(jugador.getKey(), logica.getMejorRank());
                mejoresManos.put(jugador.getKey(), logica.getMejorMano());
            }
            // Determinar el ganador de esta simulación
            List<Integer> ganadores = determinarGanador(mejoresManos, mejoresRanks);

            
            double sumadorAux = 1.0 /ganadores.size();
	        // Incrementar las victorias para cada ganador
	        for (Integer ganador : ganadores) {
	            victorias.put(ganador, victorias.get(ganador) + sumadorAux);
	        }
        }
        
        // Calcular probabilidades
        Map<Integer, Double> probabilidades = new HashMap<>();
        for (Map.Entry<Integer, Double> entrada : victorias.entrySet()) {
            probabilidades.put(entrada.getKey(), (entrada.getValue() * 100.0) / NUM_SIMULACIONES);
        }
        return probabilidades;
    }
    
    public static double calcularValorManoBot(List<String> cartasComunitarias, String[] manoBot,
    	List<String> mazoRestante) {
    	double valor = 0.0;

        Random random = new Random();

        for (int i = 0; i < NUM_SIMULACIONES; i++) {
            // Copiar el mazo restante y barajar
            List<String> mazo = new ArrayList<>(mazoRestante);
            Collections.shuffle(mazo, random);

            // Completar el board
            List<String> boardCompleto = new ArrayList<>(cartasComunitarias);
            while (boardCompleto.size() < 5) {
                boardCompleto.add(mazo.remove(0));
            } 

            LogicaManoPoker logica = new LogicaManoPoker(manoBot, boardCompleto);
            valor += anadirValor(logica);
        }
        
        
        valor = valor/1000;
        return valor;
    }
    
    private static int anadirValor(LogicaManoPoker logica) {
        ManoPoker.HandRank rank = logica.getMejorRank(); // Obtén el rango de la mano
        String mejoresCartas = logica.getMejorMano(); // Obtén las mejores cartas

        // Valores base según el rango
        int valorBase;
        switch (rank) {
            case ROYAL_FLUSH:
                valorBase = 1300;
                break;
            case STRAIGHT_FLUSH:
                valorBase = 1200;
                break;
            case FOUR_OF_A_KIND:
                valorBase = 1300;
                break;
            case FULL_HOUSE:
                valorBase = 1100;
                break;
            case FLUSH:
                valorBase = 900;
                break;
            case STRAIGHT:
                valorBase = 800;
                break;
            case THREE_OF_A_KIND:
                valorBase = 400;
                break;
            case TWO_PAIR:
                valorBase = 300;
                break;
            case PAIR:
                valorBase = 200;
                break;
            case HIGH_CARD:
            default:
                valorBase = 50;
                break;
        }

        int valorCartas = anadirValorMano(mejoresCartas);

        return valorBase + valorCartas;
    }

	private static int anadirValorMano(String mejoresCartas) {
		// Valores base según el rango
        int valor;
        switch (ManoPoker.charAnum(mejoresCartas.charAt(0))) {
            case 13:
            	valor = 95;
                break;
            case 12:
            	valor = 90;
                break;
            case 11:
            	valor = 85;
                break;
            case 10:
            	valor = 80;
                break;
            case 9:
            	valor = 70;
                break;
            case 8:
            	valor = 60;
                break;
            case 7:
            	valor = 50;
                break;
            case 6:
            	valor = 40;
                break;
            case 5:
            	valor = 30;
                break;
            case 4:
            	valor = 20;
                break;
            case 3:
            	valor = 10;
                break;
            case 2:
            default:
            	valor = 5;
                break;
        }

		return valor;
	}

    static List<Integer> determinarGanador(Map<Integer, String> mejoresManos, Map<Integer, ManoPoker.HandRank> mejoresRanks) {
        String mejorMano = null;
        ManoPoker.HandRank mejorRank = null;
        LogicaManoPoker logica = new LogicaManoPoker();
        List<Integer> ganadores = new ArrayList<>();
        
        // Recorrer las claves del mapa mejoresManos
        for (Integer jugadorId : mejoresManos.keySet()) {
            // Obtener la mano y el rango de la mano del jugador actual
            String manoActual = mejoresManos.get(jugadorId);
            ManoPoker.HandRank rankActual = mejoresRanks.get(jugadorId);

            // Si es el primer jugador o si el rango de la mano es mejor que el mejor encontrado
            if (mejorRank == null || rankActual.ordinal() < mejorRank.ordinal()) {
                mejorMano = manoActual;
                mejorRank = rankActual;
            }
            // Si los rangos son iguales, comparar las manos de manera detallada
            else if (rankActual.ordinal() == mejorRank.ordinal()) {
            	if(logica.compararManos(manoActual, mejorMano)) {
            		mejorMano = manoActual;
                    mejorRank = rankActual;
            	}
            }
            
        }
        
        // Recorrer las claves del mapa mejoresManos
        for (Integer jugadorId : mejoresManos.keySet()) {
            // Obtener la mano y el rango de la mano del jugador actual
            String manoActual = mejoresManos.get(jugadorId);
            ManoPoker.HandRank rankActual = mejoresRanks.get(jugadorId);

            // Si es el primer jugador o si el rango de la mano es mejor que el mejor encontrado
            if (mejorRank == rankActual && logica.manosIguales(manoActual, mejorMano)) {
                ganadores.add(jugadorId);
            }
            
        }
        return ganadores;
    }

}