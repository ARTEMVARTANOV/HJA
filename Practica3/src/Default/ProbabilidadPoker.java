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