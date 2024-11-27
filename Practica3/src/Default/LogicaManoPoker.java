package Default;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Default.ManoPoker.HandRank;


public class LogicaManoPoker {
    private List<String> combinaciones = new ArrayList<>();
    private ManoPoker.HandRank mejorRankPosible = ManoPoker.HandRank.HIGH_CARD;
    private String mejorManoPosible = "";
    private reorganizarStrings auxStrings = new reorganizarStrings();
    
    public LogicaManoPoker(List<String> boardCompleto) {
        generarCombinacionesBoards(boardCompleto);
        buscarMejorMano();
    }
    
    public LogicaManoPoker() {
	}

	public ManoPoker.HandRank getMejorRank() {
        return mejorRankPosible;
    }
    
    public String getMejorMano() {
        return mejorManoPosible;
    }
    
    private void generarCombinacionesBoards(List<String> boardCompleto) {
        // Se generan todas las combinaciones de 5 cartas de las 7 cartas del board
        int n = boardCompleto.size();
        
        // Generar combinaciones de 5 cartas sin repetición
        for (int i = 0; i < n - 4; i++) {
            for (int j = i + 1; j < n - 3; j++) {
                for (int k = j + 1; k < n - 2; k++) {
                    for (int l = k + 1; l < n - 1; l++) {
                        for (int m = l + 1; m < n; m++) {
                            // Crear una combinación de 5 cartas
                        	String combinacion = boardCompleto.get(i) + boardCompleto.get(j) + boardCompleto.get(k) + boardCompleto.get(l) + boardCompleto.get(m);
                            combinaciones.add(combinacion);
                        }
                    }
                }
            }
        }
    }


    private void buscarMejorMano() {
		for (String manoActual : combinaciones) {
			ordenarCartas(manoActual);
			ManoPoker manoCompleta = new ManoPoker(manoActual);
			Map<ManoPoker.HandRank, String> aux = new HashMap<>();
			aux = manoCompleta.evaluarMano();
			
			ManoPoker.HandRank rankRes = aux.keySet().iterator().next();;
			String empateAct = aux.values().iterator().next();
			
			if(mejorManoPosible == "") {
				mejorManoPosible = empateAct;
				mejorRankPosible = rankRes;
			}
			else if(rankRes.ordinal() < mejorRankPosible.ordinal()) {
					mejorManoPosible = empateAct;
					mejorRankPosible = rankRes;
			}
			else if(rankRes.ordinal() == mejorRankPosible.ordinal()) {
				if(compararManos(empateAct, mejorManoPosible)) {
					mejorManoPosible = empateAct;
					mejorRankPosible = rankRes;
				}
			}
		}
    }

    // Método para ordenar cartas de mayor a menor
    public String ordenarCartas(String manoBoard) {
        List<Carta> cartas = new ArrayList<>();

        // Convertir la cadena de manoBoard en una lista de objetos Carta
        for (int i = 0; i < manoBoard.length(); i += 2) {
            String stringAct = "" + manoBoard.charAt(i) + manoBoard.charAt(i + 1);
            Carta cartaAct = new Carta(stringAct);
            cartas.add(cartaAct);
        }

        // Ordenar las cartas por valor numérico de mayor a menor
        Collections.sort(cartas, new Comparator<Carta>() {
            @Override
            public int compare(Carta c1, Carta c2) {
                return Integer.compare(c2.getValorNumerico(), c1.getValorNumerico());  // Ordenar de mayor a menor
            }
        });

        // Convertir la lista de cartas de nuevo a un String
        StringBuilder sb = new StringBuilder();
        for (Carta carta : cartas) {
            sb.append(carta.toString());  // Añadir cada carta al resultado final
        }

        return sb.toString();  // Devolver las cartas ordenadas como String
    }

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
