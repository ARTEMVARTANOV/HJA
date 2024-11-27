package Default;

import java.util.*;

public class ProbabilidadPoker {
	private static final int NUM_SIMULACIONES = 10000;

    public static Map<Integer, Double> calcularProbabilidad(Map<Integer, String[]> manosJugadores, List<String> cartasComunitarias, List<String> mazoRestante) {
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
                List<String> cartasCompletas = new ArrayList<>();
                cartasCompletas.addAll(Arrays.asList(jugador.getValue())); 
                cartasCompletas.addAll(boardCompleto);
                LogicaManoPoker logica = new LogicaManoPoker(cartasCompletas);
                
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
    
    public static Map<Integer, Double> calcularProbabilidadOmaha(
            Map<Integer, List<Carta>> manosJugadores,
            List<Carta> cartasComunitarias,
            List<Carta> barajaRestante) {

        Map<Integer, Double> equity = new HashMap<>();
        int totalSimulaciones = 0;

        // Generar todas las combinaciones de cartas restantes para completar el board
        /*int cartasFaltantes = 5 - cartasComunitarias.size();
        List<Carta[]> combinacionesBoard = generarCombinaciones(barajaRestante, cartasFaltantes);

        // Contadores de victorias por jugador
        Map<Integer, Integer> victorias = new HashMap<>();
        for (Integer jugador : manosJugadores.keySet()) {
            victorias.put(jugador, 0);
        }

        // Simular cada combinación de cartas del board
        for (Carta[] boardFaltante : combinacionesBoard) {
            totalSimulaciones++;
            List<Carta> boardCompleto = new ArrayList<>(cartasComunitarias);
            boardCompleto.addAll(List.of(boardFaltante));

            // Evaluar las mejores manos de todos los jugadores
            int mejorValor = 0;
            List<Integer> ganadores = new ArrayList<>();

            for (Map.Entry<Integer, List<Carta>> entrada : manosJugadores.entrySet()) {
                int jugadorId = entrada.getKey();
                List<Carta> cartasJugador = entrada.getValue();

                // Crear una ManoOmaha para evaluar la mejor mano de este jugador
                ManoOmaha mano = new ManoOmaha(cartasJugador, boardCompleto);
                String mejorManoDescripcion = mano.formatearSalida(); // Usa formatearSalida para obtener la mejor mano
                ManoPoker mejorMano = new ManoPoker(cartasJugador);  // Evaluamos con ManoPoker
                int valorMano = mejorMano.obtenerValor();

                // Comparar con la mejor mano actual
                if (valorMano > mejorValor) {
                    mejorValor = valorMano;
                    ganadores.clear();
                    ganadores.add(jugadorId);
                } else if (valorMano == mejorValor) {
                    ganadores.add(jugadorId);
                }
            }

            // Registrar victorias para los ganadores
            for (int ganador : ganadores) {
                victorias.put(ganador, victorias.get(ganador) + 1);
            }
        }

        // Calcular el equity basado en las victorias
        for (Map.Entry<Integer, Integer> entrada : victorias.entrySet()) {
            equity.put(entrada.getKey(), (entrada.getValue() * 100.0) / totalSimulaciones);
        }*/

        return equity;
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