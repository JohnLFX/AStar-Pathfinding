package edu.usf.myweb.jcameron2;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ResourceUtils {

    private static final String EOF_OPCODE = "END";

    private ResourceUtils() {
    }

    public static Map<String, Point> loadPositions(InputStream locationsStream) {

        Map<String, Point> positions = new HashMap<>();

        try (Scanner scanner = new Scanner(locationsStream)) {

            String sourceID;

            while (!EOF_OPCODE.equals(sourceID = scanner.next())) {

                positions.put(sourceID, new Point(scanner.nextInt(), scanner.nextInt()));

            }

        }

        return positions;

    }

    public static Graph<String, DefaultWeightedEdge> loadGraph(Map<String, Point> positions, InputStream connectionsStream) {

        Graph<String, DefaultWeightedEdge> graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        positions.keySet().forEach(graph::addVertex);

        try (Scanner scanner = new Scanner(connectionsStream)) {

            String sourceID;

            while (!EOF_OPCODE.equals(sourceID = scanner.next())) {

                int size = scanner.nextInt();

                for (int i = 0; i < size; i++) {

                    String targetID = scanner.next();

                    graph.setEdgeWeight(
                            graph.addEdge(sourceID, targetID),
                            positions.get(sourceID).distance(positions.get(targetID))
                    );

                }

            }

        }

        return graph;

    }

}
