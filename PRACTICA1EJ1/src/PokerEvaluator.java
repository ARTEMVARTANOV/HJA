import java.io.*;
import java.util.*;

public class PokerEvaluator {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso: java -jar nombreProyecto.jar 1 entrada.txt salida.txt");
            return;
        }

        String archivoEntrada = args[1];
        String archivoSalida = args[2];

        try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.length() != 10) {
                    System.out.println("Formato de entrada inválido: " + linea);
                    continue;
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
                bw.write("\n"); // Línea en blanco para separar cada mano
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
