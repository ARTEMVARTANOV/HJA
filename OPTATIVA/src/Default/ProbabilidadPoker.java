package Default;

import java.util.*;

public class ProbabilidadPoker {
	private static final int NUM_SIMULACIONES = 500;

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
    
    public static String decidirBot(double equity, double apuestaActual, double bote, double saldoBot) {
        double umbralRetiro = 0.3; // Se retira si la equity es menor al 30%
        double umbralSubir = 0.7;  // Sube si la equity es mayor al 70%

        if (equity < umbralRetiro) {
            return "retirarse";
        } else if (equity > umbralSubir && saldoBot > apuestaActual * 2) {
            return "subir";
        }
        return "igualar";
    }

    private static List<Integer> determinarGanador(Map<Integer, String> mejoresManos, Map<Integer, ManoPoker.HandRank> mejoresRanks) {
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
