public class Carta {
	
    private char valor;
    private char palo;

    public Carta(String representacion) {
        if (representacion.length() != 2) {
            throw new IllegalArgumentException("Formato de carta inválido: " + representacion);
        }
        this.valor = representacion.charAt(0);
        this.palo = representacion.charAt(1);
    }

    public char getValor() {
        return valor;
    }

    public char getPalo() {
        return palo;
    }

    @Override
    public String toString() {
        return "" + valor + palo;
    }
}
