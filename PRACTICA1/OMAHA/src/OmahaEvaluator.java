import java.io.*;
import java.util.*;

public class OmahaEvaluator {
    public static void main(String[] args) {
        String archivoEntrada = "entrada4.txt";
        String archivoSalida = "salida4.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                procesarManoJugadorMesa(linea, bw);
                bw.write("\n");  // Línea en blanco para separar cada mano
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

        // Cartas del jugador (4 cartas)
        List<Carta> cartasJugador = new ArrayList<>();
        cartasJugador.add(new Carta(partes[0].substring(0, 2)));
        cartasJugador.add(new Carta(partes[0].substring(2, 4)));
        cartasJugador.add(new Carta(partes[0].substring(4, 6)));
        cartasJugador.add(new Carta(partes[0].substring(6, 8)));

        // Número de cartas comunes
        int nCartasComunes = Integer.parseInt(partes[1]);

        // Cartas comunes
        List<Carta> cartasComunes = new ArrayList<>();
        for (int i = 0; i < nCartasComunes * 2; i += 2) {
            cartasComunes.add(new Carta(partes[2].substring(i, i + 2)));
        }

        // Crear la mano de Omaha
        ManoOmaha manoOmaha = new ManoOmaha(cartasJugador, cartasComunes);

        // Escribir la salida
        bw.write(linea + "\n");
        bw.write("- " + manoOmaha.formatearSalida() + "\n");

        if (nCartasComunes < 5) {  // Si hay menos de 5 cartas comunes, se detectan posibles draws
            List<String> draws = manoOmaha.detectarDraws();
            for (String draw : draws) {
                bw.write("- Draw: " + draw + "\n");
            }
        }
    }
}
