package Default;

import java.util.*;

public class ProbabilidadPoker {
    private static final int NUM_SIMULACIONES = 100000;

    public static Map<Integer, Double> calcularProbabilidad(Map<Integer, String[]> manosJugadores, List<String> cartasComunitarias, List<String> mazoRestante) {
        Map<Integer, Integer> victorias = new HashMap<>();
        for (int jugadorId : manosJugadores.keySet()) {
            victorias.put(jugadorId, 0);
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
            Map<Integer, ManoPoker> evaluaciones = new HashMap<>();
            for (Map.Entry<Integer, String[]> jugador : manosJugadores.entrySet()) {
                List<Carta> cartasJugador = new ArrayList<>();
                for (String carta : jugador.getValue()) {
                    cartasJugador.add(new Carta(carta));
                }
                for (String carta : boardCompleto) {
                    cartasJugador.add(new Carta(carta));
                }
                ManoPoker mano = new ManoPoker(cartasJugador);
                evaluaciones.put(jugador.getKey(), mano);
            }

            // Determinar el ganador de esta simulación
            int ganador = determinarGanador(evaluaciones);
            victorias.put(ganador, victorias.get(ganador) + 1);
        }

        // Calcular probabilidades
        Map<Integer, Double> probabilidades = new HashMap<>();
        for (Map.Entry<Integer, Integer> entrada : victorias.entrySet()) {
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
        int cartasFaltantes = 5 - cartasComunitarias.size();
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
        }

        return equity;
    }


    // Método auxiliar para generar combinaciones
    private static List<Carta[]> generarCombinaciones(List<Carta> cartas, int k) {
        List<Carta[]> combinaciones = new ArrayList<>();
        combinar(cartas, new Carta[k], 0, 0, k, combinaciones);
        return combinaciones;
    }

    private static void combinar(List<Carta> cartas, Carta[] temp, int start, int index, int k, List<Carta[]> result) {
        if (index == k) {
            result.add(temp.clone());
            return;
        }
        for (int i = start; i < cartas.size(); i++) {
            temp[index] = cartas.get(i);
            combinar(cartas, temp, i + 1, index + 1, k, result);
        }
    }


    private static int determinarGanador(Map<Integer, ManoPoker> evaluaciones) {
        int ganador = -1;
        ManoPoker mejorMano = null;

        for (Map.Entry<Integer, ManoPoker> entrada : evaluaciones.entrySet()) {
            if (mejorMano == null || entrada.getValue().obtenerValor() > mejorMano.obtenerValor()) {
                mejorMano = entrada.getValue();
                ganador = entrada.getKey();
            } else if (entrada.getValue().obtenerValor() == mejorMano.obtenerValor()) {
                // Resolver empates con cartas altas
                if (entrada.getValue().evaluarMano().compareTo(mejorMano.evaluarMano()) > 0) {
                    mejorMano = entrada.getValue();
                    ganador = entrada.getKey();
                }
            }
        }
        return ganador;
    }
}