import java.util.*;

public class ManoPoker {
    private List<Carta> cartas;
    private String mejorMano; // Descripción de la mejor mano

    public ManoPoker(List<Carta> cartas) {
        this.cartas = cartas;
        this.mejorMano = evaluarMano(); // Evaluamos la mano en el constructor
    }

    public String evaluarMano() {
        Map<Character, Integer> conteoValores = contarValores();  // Conteo de las cartas
        List<Character> valoresOrdenados = obtenerValoresOrdenadosCaracter();  // Valores ordenados

        if (esEscaleraColor()) {
            mejorMano = "Straight Flush " + describirCartas(valoresOrdenados);
        } else if (esPoker()) {
            char valorPoker = obtenerValorDeMultiplesCartas(conteoValores, 4);
            mejorMano = "Four of a Kind, " + valorPoker + "s";
        } else if (esFull()) {
            char valorTrio = obtenerValorDeMultiplesCartas(conteoValores, 3);
            char valorPareja = obtenerValorDeMultiplesCartas(conteoValores, 2);
            mejorMano = "Full House, " + valorTrio + "s over " + valorPareja + "s";
        } else if (esColor()) {
            mejorMano = "Flush " + describirCartas(valoresOrdenados);
        } else if (esEscalera()) {
            mejorMano = "Straight " + describirCartas(valoresOrdenados);
        } else if (esTrio()) {
            char valorTrio = obtenerValorDeMultiplesCartas(conteoValores, 3);
            mejorMano = "Three of a Kind, " + valorTrio + "s";
        } else if (esDoblePareja()) {
            List<Character> valoresParejas = obtenerValoresDeMultiplesCartas(conteoValores, 2);
            mejorMano = "Two Pair, " + valoresParejas.get(0) + "s and " + valoresParejas.get(1) + "s";
        } else if (esPareja()) {
            char valorPareja = obtenerValorDeMultiplesCartas(conteoValores, 2);
            mejorMano = "Pair of " + valorPareja + "s";
        } else {
            mejorMano = "High Card " + Carta.valorAString(valoresOrdenados.get(valoresOrdenados.size() - 1));
        }

        return mejorMano;
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
        
        // Verificar si es escalera normal (con As como 14)
        boolean escaleraNormal = true;
        for (int i = 0; i < valores.size() - 1; i++) {
            if (valores.get(i) + 1 != valores.get(i + 1)) {
                escaleraNormal = false;
                break;
            }
        }
        
        // Verificar si es escalera baja (A-2-3-4-5) con As como 1
        boolean escaleraBaja = false;
        if (valores.contains(14) && valores.get(0) == 2) { // Si hay un As y el menor valor es 2
            List<Integer> posiblesValores = new ArrayList<>(valores);
            posiblesValores.remove((Integer) 14); // Eliminar As (14)
            posiblesValores.add(0, 1); // Añadir As como 1 al principio
            escaleraBaja = true;
            for (int i = 0; i < posiblesValores.size() - 1; i++) {
                if (posiblesValores.get(i) + 1 != posiblesValores.get(i + 1)) {
                    escaleraBaja = false;
                    break;
                }
            }
        }

        // Si alguna de las dos formas de escalera es válida
        return escaleraNormal || escaleraBaja;
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
        
        // Verificamos por bloques de 4 cartas consecutivas
        for (int i = 0; i < valores.size() - 3; i++) {
            if (valores.get(i) + 1 == valores.get(i + 1) &&
                valores.get(i + 1) + 1 == valores.get(i + 2) &&
                valores.get(i + 2) + 1 == valores.get(i + 3)) {
                return true;
            }
        }

        // Verificar caso de escalera baja con As como 1
        if (valores.contains(14) && valores.get(0) == 2 &&
            valores.get(1) == 3 && valores.get(2) == 4 && valores.get(3) == 5) {
            return true;
        }

        return false;
    }


    private boolean tieneEscaleraGutshot() {
        List<Integer> valores = obtenerValoresOrdenados();

        // Verificamos por bloques de 5 cartas posibles con un hueco en el medio
        for (int i = 0; i < valores.size() - 3; i++) {
            // Caso 1: Un hueco entre la primera y la tercera carta
            if (valores.get(i) + 2 == valores.get(i + 1) && 
                valores.get(i + 1) + 1 == valores.get(i + 2)) {
                return true;
            }
            // Caso 2: Un hueco entre la segunda y cuarta carta
            if (valores.get(i) + 1 == valores.get(i + 1) &&
                valores.get(i + 2) == valores.get(i + 1) + 2) {
                return true;
            }
        }

        // Caso especial de gutshot con As-2-3-4-5 (escalera baja)
        if (valores.contains(14) && valores.get(0) == 2 &&
            valores.get(1) == 3 && valores.get(2) == 4 && valores.get(3) == 5) {
            return true;
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
    
 // Devuelve el valor de las cartas que aparecen un número específico de veces (como en el caso de pares, tríos, etc.)
    private char obtenerValorDeMultiplesCartas(Map<Character, Integer> conteoValores, int cantidad) {
        for (Map.Entry<Character, Integer> entry : conteoValores.entrySet()) {
            if (entry.getValue() == cantidad) {
                return entry.getKey();
            }
        }
        return ' ';
    }

    // Devuelve los valores de las cartas que forman pares o doble pares
    private List<Character> obtenerValoresDeMultiplesCartas(Map<Character, Integer> conteoValores, int cantidad) {
        List<Character> valores = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : conteoValores.entrySet()) {
            if (entry.getValue() == cantidad) {
                valores.add(entry.getKey());
            }
        }
        Collections.sort(valores, Collections.reverseOrder());  // Ordenamos de mayor a menor
        return valores;
    }

 // Devuelve los valores de las cartas en formato ordenado, usado para describir la mano
    private List<Character> obtenerValoresOrdenadosCaracter() {
        List<Carta> cartasOrdenadas = new ArrayList<>(cartas);
        cartasOrdenadas.sort(Comparator.comparingInt(Carta::getValorNumerico).reversed());
        
        List<Character> valores = new ArrayList<>();
        for (Carta carta : cartasOrdenadas) {
            valores.add(carta.getValor());
        }
        return valores;
    }


    private String describirCartas(List<?> valoresOrdenados) {
        StringBuilder sb = new StringBuilder();
        
        for (Object valor : valoresOrdenados) {
            int valorNumerico;
            
            if (valor instanceof Character) {
                // Si es un Character, convertirlo manualmente a su valor numérico
                valorNumerico = convertirCharAValorNumerico((Character) valor);
            } else if (valor instanceof Integer) {
                // Si es un Integer, usarlo directamente
                valorNumerico = (Integer) valor;
            } else {
                continue;  // En caso de valor no esperado, ignorarlo
            }
            
            // Convertir el valor numérico a su representación en string
            sb.append(Carta.valorAString(valorNumerico)).append(" ");
        }
        
        return sb.toString().trim();  // Elimina el espacio final
    }

 // Función auxiliar para convertir Character a valor numérico
    private int convertirCharAValorNumerico(char valor) {
        switch (valor) {
            case 'A': return 14;
            case 'K': return 13;
            case 'Q': return 12;
            case 'J': return 11;
            case 'T': return 10;
            case '9': return 9;
            case '8': return 8;
            case '7': return 7;
            case '6': return 6;
            case '5': return 5;
            case '4': return 4;
            case '3': return 3;
            case '2': return 2;
            default: return 0; // Valor no reconocido
        }
    }
    




}
