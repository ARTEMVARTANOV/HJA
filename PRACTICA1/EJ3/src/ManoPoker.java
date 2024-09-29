import java.util.*;

public class ManoPoker {
	private List<Carta> cartas;
    private String mejorMano; // Descripción de la mejor mano
    private List<Carta> cartasMejorMano; // Cartas que forman la mejor mano

    public ManoPoker(List<Carta> cartas) {
        this.cartas = cartas;
        this.cartasMejorMano = new ArrayList<>(cartas); // Inicializamos con todas las cartas
        this.mejorMano = evaluarMano(); // Evaluamos la mano en el constructor
    }

    public String evaluarMano() {
        // Lógica para evaluar la mejor mano
        if (esEscaleraColor()) {
            mejorMano = "Straight Flush";
        } else if (esPoker()) {
            mejorMano = "Four of a Kind";
        } else if (esFull()) {
            mejorMano = "Full House";
        } else if (esColor()) {
            mejorMano = "Flush";
        } else if (esEscalera()) {
            mejorMano = "Straight";
        } else if (esTrio()) {
            mejorMano = "Three of a Kind";
        } else if (esDoblePareja()) {
            mejorMano = "Two Pair";
        } else if (esPareja()) {
            mejorMano = "Pair";
        } else {
            mejorMano = "High Card " + cartaAlta();
        }

        return mejorMano;
    }


    public String getCartasFormateadas() {
        // Devuelve las cartas de la mejor mano en formato "6c7c8c9hTh"
        StringBuilder sb = new StringBuilder();
        for (Carta carta : cartasMejorMano) {
            sb.append(carta.toString());
        }
        return sb.toString();
    }
    
    public List<String> detectarDraws() {
        List<String> draws = new ArrayList<>();

        // Detectar posibles draws
        if (!esColor() && tieneColorDraw()) {
            draws.add("Flush Draw");
        }
        if (!esEscalera() && tieneEscaleraGutshot()) {
            draws.add("Straight Gutshot");
        }
        if (!esEscalera() && tieneEscaleraAbierta()) {
            draws.add("Straight Open-ended");
        }

        return draws;
    }

    public int obtenerValor() {
        // Asigna un valor numérico a cada tipo de mano para poder comparar
        switch (mejorMano) {
            case "Straight Flush": return 9;
            case "Four of a Kind": return 8;
            case "Full House": return 7;
            case "Flush": return 6;
            case "Straight": return 5;
            case "Three of a Kind": return 4;
            case "Two Pair": return 3;
            case "Pair": return 2;
            default: return 1; // High Card
        }
    }

    public String getDescripcion() {
        return mejorMano;
    }

    // Métodos auxiliares para la evaluación de la mano
    private boolean esEscaleraColor() {
        return esColor() && esEscalera();
    }

    private boolean esPoker() {
        Map<Character, Integer> conteoValores = contarValores();
        return conteoValores.containsValue(4);
    }

    private boolean esFull() {
        Map<Character, Integer> conteoValores = contarValores();
        return conteoValores.containsValue(3) && conteoValores.containsValue(2);
    }

    private boolean esColor() {
        char paloInicial = cartas.get(0).getPalo();
        for (Carta carta : cartas) {
            if (carta.getPalo() != paloInicial) {
                return false;
            }
        }
        return true;
    }

    private boolean esEscalera() {
        List<Integer> valores = obtenerValoresOrdenados();
        for (int i = 0; i < valores.size() - 1; i++) {
            if (valores.get(i) + 1 != valores.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    private boolean esTrio() {
        Map<Character, Integer> conteoValores = contarValores();
        return conteoValores.containsValue(3);
    }

    private boolean esDoblePareja() {
        Map<Character, Integer> conteoValores = contarValores();
        int count = 0;
        for (int value : conteoValores.values()) {
            if (value == 2) count++;
        }
        return count == 2;
    }

    private boolean esPareja() {
        Map<Character, Integer> conteoValores = contarValores();
        return conteoValores.containsValue(2);
    }
    
    private boolean tieneColorDraw() {
        // Contar cuántas cartas hay de cada palo
        Map<Character, Integer> conteoPalos = new HashMap<>();
        for (Carta carta : cartas) {
            char palo = carta.getPalo();
            conteoPalos.put(palo, conteoPalos.getOrDefault(palo, 0) + 1);
        }

        // Verificar si hay al menos 4 cartas del mismo palo
        for (int count : conteoPalos.values()) {
            if (count >= 4) {
                return true;
            }
        }

        return false;
    }
    

    private boolean tieneEscaleraAbierta() {
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

    private boolean tieneEscaleraGutshot() {
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
    private String cartaAlta() {
        // Devuelve la carta más alta de la mano
        List<Integer> valores = obtenerValoresOrdenados();
        return Carta.valorAString(valores.get(valores.size() - 1));
    }

    private Map<Character, Integer> contarValores() {
        Map<Character, Integer> conteo = new HashMap<>();
        for (Carta carta : cartas) {
            char valor = carta.getValor();
            conteo.put(valor, conteo.getOrDefault(valor, 0) + 1);
        }
        return conteo;
    }

    private List<Integer> obtenerValoresOrdenados() {
        List<Integer> valores = new ArrayList<>();
        for (Carta carta : cartas) {
            valores.add(carta.getValorNumerico());
        }
        Collections.sort(valores);
        return valores;
    }
}
