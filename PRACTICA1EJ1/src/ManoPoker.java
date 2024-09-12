import java.util.*;

public class ManoPoker {
    private List<Carta> cartas;

    public ManoPoker(List<Carta> cartas) {
        if (cartas.size() != 5) {
            throw new IllegalArgumentException("Una mano de póker debe tener 5 cartas.");
        }
        this.cartas = cartas;
    }
    
 // Método para comprobar si la mano es una escalera de color
    private boolean esEscaleraDeColor() {
        return esColor() && esEscalera();
    }

    // Método para comprobar si la mano es un póker (cuatro cartas del mismo valor)
    private boolean esPoker() {
        Map<Character, Integer> frecuencia = contarValores();
        return frecuencia.containsValue(4);
    }

    // Método para comprobar si la mano es un full house (tres cartas de un valor y dos de otro)
    private boolean esFullHouse() {
        Map<Character, Integer> frecuencia = contarValores();
        return frecuencia.containsValue(3) && frecuencia.containsValue(2);
    }

    // Método para comprobar si la mano es un color (cinco cartas del mismo palo)
    private boolean esColor() {
        char paloInicial = cartas.get(0).getPalo();
        for (Carta carta : cartas) {
            if (carta.getPalo() != paloInicial) {
                return false;
            }
        }
        return true;
    }

    // Método para comprobar si la mano es una escalera (cinco cartas en secuencia)
    private boolean esEscalera() {
        List<Integer> valores = obtenerValoresOrdenados();
        for (int i = 0; i < valores.size() - 1; i++) {
            if (valores.get(i) + 1 != valores.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    // Método para comprobar si la mano es un trío (tres cartas del mismo valor)
    private boolean esTrio() {
        Map<Character, Integer> frecuencia = contarValores();
        return frecuencia.containsValue(3);
    }

    // Método para comprobar si la mano es un doble par
    private boolean esDoblePar() {
        Map<Character, Integer> frecuencia = contarValores();
        int pares = 0;
        for (int count : frecuencia.values()) {
            if (count == 2) {
                pares++;
            }
        }
        return pares == 2;
    }

    // Método para comprobar si la mano es un par
    private boolean esPar() {
        Map<Character, Integer> frecuencia = contarValores();
        return frecuencia.containsValue(2);
    }

    public String evaluarMano() {
        if (esEscaleraDeColor()) return "Escalera de Color";
        if (esPoker()) return "Póker";
        if (esFullHouse()) return "Full House";
        if (esColor()) return "Color";
        if (esEscalera()) return "Escalera";
        if (esTrio()) return "Trío";
        if (esDoblePar()) return "Doble Par";
        if (esPar()) return "Par";
        return "Carta Alta";
    }

    public List<String> detectarDraws() {
        List<String> draws = new ArrayList<>();
        if (esFlushDraw()) draws.add("Flush Draw");
        if (esOpenEndedStraightDraw()) draws.add("Straight Open-Ended");
        if (esGutshotDraw()) draws.add("Straight Gutshot");
        return draws;
    }

    private boolean esFlushDraw() {
        Map<Character, Integer> frecuenciaPalos = new HashMap<>();
        for (Carta carta : cartas) {
            frecuenciaPalos.put(carta.getPalo(), frecuenciaPalos.getOrDefault(carta.getPalo(), 0) + 1);
        }
        return frecuenciaPalos.containsValue(4);
    }

    private boolean esOpenEndedStraightDraw() {
        List<Integer> valores = obtenerValoresOrdenados();
        for (int i = 0; i < valores.size() - 3; i++) {
            if (valores.get(i) + 1 == valores.get(i + 1) && 
                valores.get(i + 1) + 1 == valores.get(i + 2) && 
                valores.get(i + 2) + 1 == valores.get(i + 3)) {
                return true;
            }
        }
        return false;
    }

    private boolean esGutshotDraw() {
        List<Integer> valores = obtenerValoresOrdenados();
        for (int i = 0; i < valores.size() - 3; i++) {
            if ((valores.get(i) + 2 == valores.get(i + 2) && 
                 valores.get(i + 2) + 1 == valores.get(i + 3)) || 
                (valores.get(i) + 1 == valores.get(i + 1) && 
                 valores.get(i + 2) == valores.get(i + 1) + 2)) {
                return true;
            }
        }
        return false;
    }

    // Métodos adicionales necesarios para evaluar la mejor mano y draws
    private Map<Character, Integer> contarValores() {
        Map<Character, Integer> frecuencia = new HashMap<>();
        for (Carta carta : cartas) {
            frecuencia.put(carta.getValor(), frecuencia.getOrDefault(carta.getValor(), 0) + 1);
        }
        return frecuencia;
    }

    private List<Integer> obtenerValoresOrdenados() {
        List<Integer> valores = new ArrayList<>();
        for (Carta carta : cartas) {
            valores.add(valorNumerico(carta.getValor()));
        }
        Collections.sort(valores);
        return valores;
    }

    private int valorNumerico(char valor) {
        switch (valor) {
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case 'T': return 10;
            case 'J': return 11;
            case 'Q': return 12;
            case 'K': return 13;
            case 'A': return 14;
            default: throw new IllegalArgumentException("Valor de carta desconocido: " + valor);
        }
    }
}
