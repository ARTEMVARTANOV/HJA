package Default;

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


    public int getValorNumerico() {
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

        // Método estático para convertir el valor numérico a string
        public static String valorAString(int valorNumerico) {
            switch (valorNumerico) {
                case 14: return "A";
                case 13: return "K";
                case 12: return "Q";
                case 11: return "J";
                case 10: return "T";
                case 9: return "9";
                case 8: return "8";
                case 7: return "7";
                case 6: return "6";
                case 5: return "5";
                case 4: return "4";
                case 3: return "3";
                case 2: return "2";
                default: return "?"; // En caso de valor no reconocido
            }
        }
        
        // Método estático para convertir una representación String en una instancia de Carta
        public static Carta fromString(String representacion) {
            if (representacion == null || representacion.length() != 2) {
                throw new IllegalArgumentException("Representación inválida de carta: " + representacion);
            }
            return new Carta(representacion);
        }
        
        public int getIndicePalo() {
            switch (palo) {
                case 'h': return 0; // Corazones
                case 'd': return 1; // Diamantes
                case 'c': return 2; // Tréboles
                case 's': return 3; // Picas
                default: throw new IllegalArgumentException("Palo desconocido: " + palo);
            }
        }


        @Override
        public String toString() {
            return "" + valor + palo;
        }
}