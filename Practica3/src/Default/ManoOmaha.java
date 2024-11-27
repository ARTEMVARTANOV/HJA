package Default;

import java.util.ArrayList;
import java.util.List;

public class ManoOmaha {
    private List<Carta> cartasJugador;  // 4 cartas del jugador
    private List<Carta> cartasComunes;  // Cartas del board

    public ManoOmaha(List<Carta> cartasJugador, List<Carta> cartasComunes) {
        this.cartasJugador = cartasJugador;
        this.cartasComunes = cartasComunes;
    }

    // Método para encontrar la mejor mano y formatear la salida
    public String formatearSalida() {
        String mejorManoDescripcion = "";
        /*ManoPoker mejorManoPoker = null;
        
        // Generar todas las combinaciones válidas de 2 cartas del jugador y 3 cartas del board
        List<Carta[]> combinacionesJugador = generarCombinaciones(cartasJugador, 2);
        List<Carta[]> combinacionesComunes = generarCombinaciones(cartasComunes, 3);

        // Variable para almacenar el valor de la mejor mano
        int mejorValorMano = 0;

        // Recorrer todas las combinaciones y encontrar la mejor mano
        for (Carta[] manoJugador : combinacionesJugador) {
            for (Carta[] manoComunitaria : combinacionesComunes) {
                // Crear una mano completa de 5 cartas (2 del jugador y 3 del board)
                List<Carta> manoCompleta = new ArrayList<>();
                for (Carta carta : manoJugador) {
                    manoCompleta.add(carta);
                }
                for (Carta carta : manoComunitaria) {
                    manoCompleta.add(carta);
                }

                // Evaluar la mano usando ManoPoker
                ManoPoker manoPoker = new ManoPoker(manoCompleta);

                // Comparar el valor de esta mano con la mejor mano encontrada hasta ahora
                int valorMano = manoPoker.obtenerValor();
                if (valorMano > mejorValorMano) {
                    mejorValorMano = valorMano;
                    mejorManoPoker = manoPoker;
                }
            }
        }

        // Si encontramos una mejor mano, formateamos la descripción
        if (mejorManoPoker != null) {
            mejorManoDescripcion = mejorManoPoker.getDescripcion();
        }*/

        return mejorManoDescripcion;
    }

    // Detectar posibles draws (Flush Draw, Straight Open-ended, Straight Gutshot)
    public List<String> detectarDraws() {
        List<String> draws = new ArrayList<>();

        // Verificar si hay Flush Draw
        if (tieneColorDraw()) {
            draws.add("Flush Draw");
        }

        // Verificar si hay Gutshot Straight Draw
        if (tieneEscaleraGutshot()) {
            draws.add("Gutshot Straight Draw");
        }

        // Verificar si hay Open-ended Straight Draw
        if (tieneEscaleraAbierta()) {
            draws.add("Open-ended Straight Draw");
        }

        return draws;
    }

    // Método auxiliar para generar combinaciones
    private List<Carta[]> generarCombinaciones(List<Carta> cartas, int k) {
        List<Carta[]> combinaciones = new ArrayList<>();
        combinar(cartas, new Carta[k], 0, 0, k, combinaciones);
        return combinaciones;
    }

    private void combinar(List<Carta> cartas, Carta[] temp, int start, int index, int k, List<Carta[]> result) {
        if (index == k) {
            result.add(temp.clone());
            return;
        }
        for (int i = start; i < cartas.size(); i++) {
            temp[index] = cartas.get(i);
            combinar(cartas, temp, i + 1, index + 1, k, result);
        }
    }

    // ---------------------------- Métodos auxiliares para detectar draws ----------------------------

    // Verificar si hay un posible Color (Flush Draw)
    private boolean tieneColorDraw() {
        // Contar cuántas cartas hay de cada palo entre las cartas del jugador y del board
        int[] conteoPalos = new int[4];  // Suponiendo que hay 4 palos (0: Corazones, 1: Diamantes, 2: Tréboles, 3: Picas)

        for (Carta carta : cartasJugador) {
            conteoPalos[carta.getIndicePalo()]++;
        }
        for (Carta carta : cartasComunes) {
            conteoPalos[carta.getIndicePalo()]++;
        }

        // Verificar si hay al menos 4 cartas del mismo palo
        for (int count : conteoPalos) {
            if (count >= 4) {
                return true;
            }
        }
        return false;
    }

    // Verificar si hay un posible Straight Draw con hueco (Gutshot)
    private boolean tieneEscaleraGutshot() {
        List<Integer> valores = obtenerValoresOrdenados();

        // Verificamos por bloques de 4 cartas consecutivas con un hueco en el medio
        for (int i = 0; i < valores.size() - 3; i++) {
            if (valores.get(i) + 1 != valores.get(i + 1) &&
                valores.get(i) + 2 == valores.get(i + 1)) {
                return true;
            }
        }

        return false;
    }

    // Verificar si hay un posible Straight Draw abierto (Open-ended)
    private boolean tieneEscaleraAbierta() {
        List<Integer> valores = obtenerValoresOrdenados();

        // Verificar secuencias de 4 cartas consecutivas
        for (int i = 0; i < valores.size() - 3; i++) {
            if (valores.get(i) + 1 == valores.get(i + 1) &&
                valores.get(i + 1) + 1 == valores.get(i + 2) &&
                valores.get(i + 2) + 1 == valores.get(i + 3)) {
                return true;
            }
        }

        return false;
    }

    // Obtener los valores numéricos de las cartas en orden
    private List<Integer> obtenerValoresOrdenados() {
        List<Integer> valores = new ArrayList<>();
        for (Carta carta : cartasJugador) {
            valores.add(carta.getValorNumerico());
        }
        for (Carta carta : cartasComunes) {
            valores.add(carta.getValorNumerico());
        }
        valores.sort(Integer::compareTo);
        return valores;
    }
}
