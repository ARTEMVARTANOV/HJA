package Default;

import java.util.*;

public class ProbabilidadPoker {
	public static Map<Integer, Double> calcularProbabilidad(
            Map<Integer, String[]> manosJugadores, 
            List<String> cartasComunitarias, 
            List<String> baraja) {
        
        Map<Integer, Double> probabilidades = new HashMap<>();

        // Si no hay cartas en el board, asignamos probabilidades iguales a todos
        if (cartasComunitarias.isEmpty()) {
            double probabilidadInicial = 100.0 / manosJugadores.size(); // Probabilidad igual para todos los jugadores
            for (Integer jugador : manosJugadores.keySet()) {
                probabilidades.put(jugador, probabilidadInicial);
            }
            return probabilidades;
        }

        // Convertir las cartas del board en objetos Carta
        List<Carta> cartasComunitariasObj = new ArrayList<>();
        for (String carta : cartasComunitarias) {
            cartasComunitariasObj.add(new Carta(carta));
        }

        // Crear baraja restante excluyendo las cartas ya usadas
        Set<String> cartasUsadas = new HashSet<>(cartasComunitarias);
        for (String[] mano : manosJugadores.values()) {
            cartasUsadas.addAll(Arrays.asList(mano));
        }

        List<String> barajaRestante = new ArrayList<>(baraja);
        barajaRestante.removeAll(cartasUsadas);

        // Simular múltiples escenarios
        int simulaciones = 1000;
        Map<Integer, Integer> victorias = new HashMap<>();

        for (int i = 0; i < simulaciones; i++) {
            // Completar el board con cartas simuladas
            List<Carta> cartasSimuladas = new ArrayList<>(cartasComunitariasObj);
            Collections.shuffle(barajaRestante);

            for (int j = cartasSimuladas.size(); j < 5; j++) {
                cartasSimuladas.add(new Carta(barajaRestante.get(j)));
            }

            // Evaluar las manos de los jugadores
            Map<Integer, ManoPoker> manosEvaluadas = new HashMap<>();
            for (Map.Entry<Integer, String[]> entrada : manosJugadores.entrySet()) {
                int jugador = entrada.getKey();
                String[] cartasJugador = entrada.getValue();

                List<Carta> todasCartas = new ArrayList<>(cartasSimuladas);
                todasCartas.add(new Carta(cartasJugador[0]));
                todasCartas.add(new Carta(cartasJugador[1]));

                manosEvaluadas.put(jugador, new ManoPoker(todasCartas));
            }

            // Determinar el ganador de la simulación
            int ganador = determinarGanador(manosEvaluadas);
            victorias.put(ganador, victorias.getOrDefault(ganador, 0) + 1);
        }

        // Calcular probabilidades
        for (int jugador : manosJugadores.keySet()) {
            int victoriasJugador = victorias.getOrDefault(jugador, 0);
            probabilidades.put(jugador, (double) victoriasJugador / simulaciones * 100);
        }

        return probabilidades;
    }


    private static int determinarGanador(Map<Integer, ManoPoker> manosEvaluadas) {
        int ganador = -1;
        int mejorValor = -1;
        ManoPoker mejorMano = null;

        for (Map.Entry<Integer, ManoPoker> entrada : manosEvaluadas.entrySet()) {
            int jugador = entrada.getKey();
            ManoPoker mano = entrada.getValue();

            int valorActual = mano.obtenerValor();
            if (valorActual > mejorValor || 
               (valorActual == mejorValor && manoSupera(mano, mejorMano))) {
                ganador = jugador;
                mejorValor = valorActual;
                mejorMano = mano;
            }
        }

        return ganador;
    }

    private static boolean manoSupera(ManoPoker mano1, ManoPoker mano2) {
        // En caso de empate en valor, evalúa según las cartas más altas
        List<Integer> valores1 = mano1.obtenerValoresOrdenados();
        List<Integer> valores2 = mano2.obtenerValoresOrdenados();

        for (int i = valores1.size() - 1; i >= 0; i--) {
            if (valores1.get(i) > valores2.get(i)) {
                return true;
            } else if (valores1.get(i) < valores2.get(i)) {
                return false;
            }
        }

        return false; // Si todas las cartas son iguales
    }
}
