import java.util.ArrayList;
import java.util.List;

public class DrawData {
    private int count; // Contador para los draws
    private List<String> stringList; // Lista de manos

    public DrawData() {
        this.count = 0; // Inicializar el contador
        this.stringList = new ArrayList<>(); // Inicializar la lista
    }

    public void incrementCount() {
        this.count++; // Incrementa el contador
    }

    public int getCount() {
        return count; // Devuelve el contador
    }

    public List<String> getStringList() {
        return stringList; // Devuelve la lista de manos
    }
}
