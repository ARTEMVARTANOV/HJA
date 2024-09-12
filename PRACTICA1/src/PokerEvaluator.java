import java.io.*;
import java.util.*;

public class PokerEvaluator {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso: java -jar nombreProyecto.jar <apartado> <entrada.txt> <salida.txt>");
            return;
        }

        String apartado = args[0];
        String archivoEntrada = args[1];
        String archivoSalida = args[2];

        try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                if (apartado.equals("1")) {
                    procesarManoCincoCartas(linea, bw);
                } else if (apartado.equals("2")) {
                    procesarManoJugadorMesa(linea, bw);
                } else if (apartado.equals("3")) {
                    procesarManoMultipleJugadores(linea, bw);
                }
                bw.write("\n"); // Línea en blanco para separar cada mano
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void procesarManoCincoCartas(String linea, BufferedWriter bw) throws IOException {
        // Implementación del Apartado 1
    }

    private static void procesarManoJugadorMesa(String linea, BufferedWriter bw) throws IOException {
        // Implementación del Apartado 2
    }

    private static void procesarManoMultipleJugadores(String linea, BufferedWriter bw) throws IOException {
        String[] partes = linea.split(";");
        int nJugadores = Integer.parseInt(partes[0]);

        if (nJugadores < 2 || nJugadores > 9 || partes.length != (nJugadores + 2)) {
            System.out.println("Formato de entrada inválido: " + linea);
            return;
        }

        // Cartas de los jugadores
        Map<String, List<Carta>> cartasJugadores = new HashMap<>();
        for (int i = 1; i <= nJugadores; i++) {
            String jugadorYCartas = partes[i];
            String jugadorId = jugadorYCartas.substring(0, 2); // "J1", "J2", etc.
            List<Carta> cartas = new ArrayList<>();
            cartas.add(new Carta(jugadorYCartas.substring(2, 4))); // Primera carta
            cartas.add(new Carta(jugadorYCartas.substring(4, 6))); // Segunda carta
            cartasJugadores.put(jugadorId, cartas);
        }

        // Cartas comunes
        List<Carta> cartasComunes = new ArrayList<>();
        String cartasComunesString = partes[partes.length - 1];
        for (int i = 0; i < 10; i += 2) {
            cartasComunes.add(new Carta(cartasComunesString.substring(i, i + 2)));
        }

        // Evaluar la mejor mano de cada jugador
        Map<String, ManoPoker> mejoresManos = new HashMap<>();
        for (String jugadorId : cartasJugadores.keySet()) {
            List<Carta> cartasJugador = cartasJugadores.get(jugadorId);
            ManoPoker mejorMano = calcularMejorMano(cartasJugador, cartasComunes);
            mejoresManos.put(jugadorId, mejorMano);
        }

        // Ordenar jugadores por la mejor mano
        List<Map.Entry<String, ManoPoker>> listaJugadoresOrdenados = new ArrayList<>(mejoresManos.entrySet());
        listaJugadoresOrdenados.sort((entry1, entry2) -> entry2.getValue().obtenerValor() - entry1.getValue().obtenerValor());

        // Escribir resultados en el archivo de salida
        bw.write(linea + "\n");
        for (Map.Entry<String, ManoPoker> entry : listaJugadoresOrdenados) {
            bw.write(entry.getKey() + ": " + entry.getValue().getDescripcion() + "\n");
        }
    }

    private static ManoPoker calcularMejorMano(List<Carta> cartasJugador, List<Carta> cartasComunes) {
        List<Carta> todasCartas = new ArrayList<>();
        todasCartas.addAll(cartasJugador);
        todasCartas.addAll(cartasComunes);

        ManoPoker mejorMano = null;
        int mejorValor = -1;

        // Generar todas las combinaciones posibles de 5 cartas de las cartas disponibles
        List<List<Carta>> combinaciones = generarCombinaciones(todasCartas, 5);
        for (List<Carta> combinacion : combinaciones) {
            ManoPoker mano = new ManoPoker(combinacion);
            int valor = mano.obtenerValor();

            if (valor > mejorValor) {
                mejorValor = valor;
                mejorMano = mano;
            }
        }

        return mejorMano;
    }

    private static List<List<Carta>> generarCombinaciones(List<Carta> cartas, int k) {
        List<List<Carta>> combinaciones = new ArrayList<>();
        generarCombinacionesRecursivamente(cartas, k, 0, new ArrayList<>(), combinaciones);
        return combinaciones;
    }

    private static void generarCombinacionesRecursivamente(List<Carta> cartas, int k, int start, List<Carta> actual, List<List<Carta>> combinaciones) {
        if (actual.size() == k) {
            combinaciones.add(new ArrayList<>(actual));
            return;
        }
        for (int i = start; i < cartas.size(); i++) {
            actual.add(cartas.get(i));
            generarCombinacionesRecursivamente(cartas, k, i + 1, actual, combinaciones);
            actual.remove(actual.size() - 1);
        }
    }
}
