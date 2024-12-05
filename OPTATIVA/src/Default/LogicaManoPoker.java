package Default;

import java.util.*;

public class LogicaManoPoker {
    private List<String> combinaciones = new ArrayList<>();
    private ManoPoker.HandRank mejorRankPosible = ManoPoker.HandRank.HIGH_CARD;
    private String mejorManoPosible = "";
    private reorganizarStrings auxStrings = new reorganizarStrings();

    public LogicaManoPoker(String[] strings, List<String> boardCompleto, boolean omaha) {
        List<String> cartasCompletas = new ArrayList<>();
        cartasCompletas.addAll(Arrays.asList(strings));

        if (!omaha) {
            cartasCompletas.addAll(boardCompleto);
            generarCombinaciones(cartasCompletas, 5); // Genera combinaciones de 5 cartas.
        } else {
            generarCombinacionesOmaha(Arrays.asList(strings), boardCompleto);
        }

        buscarMejorMano();
    }

    public LogicaManoPoker() {}

    public LogicaManoPoker(String[] manoBot, List<String> boardCompleto) {
    	generarCombinacionesOmaha(Arrays.asList(manoBot), boardCompleto);
    	buscarMejorMano();
	}

	public ManoPoker.HandRank getMejorRank() {
        return mejorRankPosible;
    }

    public String getMejorMano() {
        return mejorManoPosible;
    }

    private void generarCombinaciones(List<String> cartas, int r) {
        generarCombinacionesRecursivas(cartas, r, 0, new ArrayList<>());
    }

    private void generarCombinacionesRecursivas(List<String> cartas, int r, int start, List<String> combinacionActual) {
        if (combinacionActual.size() == r) {
            combinaciones.add(String.join("", combinacionActual));
            return;
        }

        for (int i = start; i < cartas.size(); i++) {
            combinacionActual.add(cartas.get(i));
            generarCombinacionesRecursivas(cartas, r, i + 1, combinacionActual);
            combinacionActual.remove(combinacionActual.size() - 1);
        }
    }

    private void generarCombinacionesOmaha(List<String> mano, List<String> boardCompleto) {
        generarCombinaciones(mano, 2); // Combinaciones de 2 cartas de la mano.
        List<String> combinacionesMano = new ArrayList<>(combinaciones);

        combinaciones.clear();
        generarCombinaciones(boardCompleto, 3); // Combinaciones de 3 cartas del board.
        List<String> combinacionesBoard = new ArrayList<>(combinaciones);

        combinaciones.clear();
        for (String cartasMano : combinacionesMano) {
            for (String cartasBoard : combinacionesBoard) {
                combinaciones.add(cartasMano + cartasBoard);
            }
        }
    }

    private void buscarMejorMano() {
        for (String manoActual : combinaciones) {
            manoActual = ordenarCartas(manoActual);
            ManoPoker manoCompleta = new ManoPoker(manoActual);
            Map<ManoPoker.HandRank, String> resultado = manoCompleta.evaluarMano();

            actualizarMejorMano(resultado);
        }
    }

    private void actualizarMejorMano(Map<ManoPoker.HandRank, String> resultado) {
        ManoPoker.HandRank rankActual = resultado.keySet().iterator().next();
        String manoActual = resultado.values().iterator().next();

        if (mejorManoPosible.isEmpty() || rankActual.ordinal() < mejorRankPosible.ordinal() || 
            (rankActual.ordinal() == mejorRankPosible.ordinal() && compararManos(manoActual, mejorManoPosible))) {
            mejorRankPosible = rankActual;
            mejorManoPosible = manoActual;
        }
    }

    public String ordenarCartas(String manoBoard) {
        List<Carta> cartas = new ArrayList<>();
        for (int i = 0; i < manoBoard.length(); i += 2) {
            cartas.add(new Carta(manoBoard.substring(i, i + 2)));
        }
        cartas.sort(Comparator.comparingInt(Carta::getValorNumerico).reversed());
        return cartas.stream().map(Carta::toString).reduce("", String::concat);
    }


    //Devuelve True si empateAct es mejor/mayor que empate
    public boolean compararManos(String empateAct, String empate) {
	    for (int i = 0; i < empateAct.length(); i += 2) {
	        // Obtener el valor numérico de la carta en la posición actual (pares)
	        int valorEmpateAct = auxStrings.getValorNumerico(empateAct.charAt(i));
	        int valorEmpate = auxStrings.getValorNumerico(empate.charAt(i));

	        // Comparar los valores
	        if (valorEmpateAct > valorEmpate) {
	            return true;  // Si empateAct tiene un valor mayor, devuelve true
	        } else if (valorEmpateAct < valorEmpate) {
	            return false;  // Si empate tiene un valor mayor, devuelve false
	        }
	        // Si son iguales, continuar con la siguiente carta (i += 2)
	    }

	    return true;
	}

    //Devuelve true si y solo si las manos son iguales
	public boolean manosIguales(String empateAct, String empate) {
		for (int i = 0; i < empateAct.length(); i += 2) {
	        // Obtener el valor numérico de la carta en la posición actual (pares)
	        int valorEmpateAct = auxStrings.getValorNumerico(empateAct.charAt(i));
	        int valorEmpate = auxStrings.getValorNumerico(empate.charAt(i));

	        // Comparar los valores
	        if (valorEmpateAct > valorEmpate) {
	            return false;  // Si empateAct tiene un valor mayor, devuelve true
	        } else if (valorEmpateAct < valorEmpate) {
	            return false;  // Si empate tiene un valor mayor, devuelve false
	        }
	        // Si son iguales, continuar con la siguiente carta (i += 2)
	    }

	    return true;
	}


}
