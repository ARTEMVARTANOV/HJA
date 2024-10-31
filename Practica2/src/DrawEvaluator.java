import java.util.*;

public class DrawEvaluator {
    private Draws draws;

    public DrawEvaluator() {
        this.draws = new Draws(); // Inicializa el objeto aquí.
    }

    public void calculateDrawProbabilities(String rangeString, List<String> board,
            Map<Draws.typeDraws, Double> probabilities,
            Map<Draws.typeDraws, DrawData> drawsCount) {

        countCombosByDrawType(rangeString, board, drawsCount); // Cambiado a List<String>

        // Calcular el total de combinaciones posibles
        int totalCombos = drawsCount.values().stream()
                .mapToInt(drawData -> drawData.getCount()) // Sumar todos los contadores
                .sum();

        // Calcular la probabilidad de cada tipo de draw
        for (Map.Entry<Draws.typeDraws, DrawData> entry : drawsCount.entrySet()) {
            DrawData drawData = entry.getValue(); // Obtener el objeto DrawData
            int drawCount = drawData.getCount(); // Obtener el conteo total para el tipo de draw
            double probability = totalCombos > 0 ? (drawCount / (double) totalCombos) * 100 : 0; // Calcular probabilidad
            probabilities.put(entry.getKey(), probability); // Asegúrate de usar el tipo correcto
        }
    }

    public void countCombosByDrawType(String rangeString, List<String> board,
            Map<Draws.typeDraws, DrawData> drawsCount) {

        // Inicializamos el mapa con todos los draws en 0
        for (Draws.typeDraws drawType : Draws.typeDraws.values()) {
            drawsCount.put(drawType, new DrawData()); // Crear nuevo DrawData con listas vacías
        }

        // Para cada combinación posible en el rango, evaluamos el draw y aumentamos el conteo
        List<String> combos = Arrays.asList(rangeString.split(",")); // Dividir la cadena en una lista de combos
        for (String combo : combos) {
            // Evaluamos el draw usando el método que debes definir
            draws.prepareEvaluateDraws(combo, board, drawsCount); // Ahora board es de tipo List<String>
        }
    }
}
