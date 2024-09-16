import java.io.*;
import java.util.*;

public class PokerEvaluator {
	public static void main(String[] args) {
        String archivoEntrada = "entrada2.txt";
        String archivoSalida = "salida2.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                procesarManoJugadorMesa(linea, bw);
                bw.write("\n"); // Línea en blanco para separar cada mano
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void procesarManoJugadorMesa(String linea, BufferedWriter bw) throws IOException {
        String[] partes = linea.split(";");
        if (partes.length != 3) {
            System.out.println("Formato de entrada inválido: " + linea);
            return;
        }

        // Cartas del jugador
        List<Carta> cartasJugador = new ArrayList<>();
        cartasJugador.add(new Carta(partes[0].substring(0, 2)));
        cartasJugador.add(new Carta(partes[0].substring(2, 4)));

        // Número de cartas comunes
        int nCartasComunes = Integer.parseInt(partes[1]);

        // Cartas comunes
        List<Carta> cartasComunes = new ArrayList<>();
        for (int i = 0; i < nCartasComunes * 2; i += 2) {
            cartasComunes.add(new Carta(partes[2].substring(i, i + 2)));
        }

        ManoPoker mejorMano = calcularMejorMano(cartasJugador, cartasComunes);

        bw.write(linea + "\n");
        bw.write("- Best hand: " + mejorMano.getDescripcion() + "\n");

        if (nCartasComunes < 5) { // Solo calcular draws si hay menos de 5 cartas comunes
            List<String> draws = mejorMano.detectarDraws();
            for (String draw : draws) {
                bw.write("- Draw: " + draw + "\n");
            }
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
