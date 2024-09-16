import java.io.*;
import java.util.*;

public class PokerEvaluator {
    public static void main(String[] args) {
        String archivoEntrada = "entrada.txt";
        String archivoSalida = "salida.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                procesarManoCincoCartas(linea, bw);
                bw.write("\n"); // Línea en blanco para separar cada mano
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void procesarManoCincoCartas(String linea, BufferedWriter bw) throws IOException {
        if (linea.length() != 10) {
            System.out.println("Formato de entrada inválido: " + linea);
            return;
        }

        List<Carta> cartas = new ArrayList<>();
        for (int i = 0; i < linea.length(); i += 2) {
            cartas.add(new Carta(linea.substring(i, i + 2)));
        }

        ManoPoker mano = new ManoPoker(cartas);
        String mejorMano = mano.evaluarMano();
        List<String> draws = mano.detectarDraws();

        bw.write(linea + "\n");
        bw.write("- Best hand: " + mejorMano + "\n");
        for (String draw : draws) {
            bw.write("- Draw: " + draw + "\n");
        }
    }
}
