package edu.usf.myweb.jcameron2;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class Bootstrap {

    public static void main(String[] args) {

        Graph<String, DefaultWeightedEdge> graph;
        Map<String, Point> positions;

        try (
                InputStream locationStream = Files.newInputStream(Paths.get(args.length > 0 ? args[0] : "locations.txt"));
                InputStream connectionsStream = Files.newInputStream(Paths.get(args.length > 1 ? args[1] : "connections.txt"))
        ) {

            positions = ResourceUtils.loadPositions(locationStream);
            graph = ResourceUtils.loadGraph(positions, connectionsStream);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Loaded " + graph.vertexSet().size() + " cities and " + graph.edgeSet().size() + " segments");

        Scanner scanner = new Scanner(System.in);

        String startCity;

        do {

            System.out.print("Enter start city: ");

            startCity = scanner.nextLine();

            if (!graph.containsVertex(startCity)) {
                System.out.println("Start city \"" + startCity + "\" does not exist in the graph.");
                startCity = null;
            }

        } while (startCity == null);

        String endCity;

        do {

            System.out.print("Enter end city: ");

            endCity = scanner.nextLine();

            if (!graph.containsVertex(endCity)) {
                System.out.println("End city \"" + endCity + "\" does not exist in the graph.");
                endCity = null;
            }

        } while (endCity == null);

        if (Objects.equals(startCity, endCity))
            System.out.println("Note: Both the start city and end city are identical!");

        Set<String> excludedCities = new HashSet<>();

        System.out.print("Enter a list of excluded cities [e.g. B2 E4 A1]: ");

        for (String city : scanner.nextLine().split(Pattern.quote(" "))) {

            if (city.isEmpty())
                continue;

            if (graph.containsVertex(city)) {

                if (excludedCities.add(city))
                    System.out.println("Added new excluded city: \"" + city + "\"");

            } else {

                System.out.println("Note: Ignoring input city \"" + city + "\" because it does not exist in the graph.");

            }

        }

        AStar_Heuristic heuristicType = null;

        do {

            System.out.print("Enter A* heuristic to use (" + Arrays.toString(AStar_Heuristic.values()) + "): ");

            String input = scanner.nextLine().toUpperCase();

            try {

                heuristicType = AStar_Heuristic.valueOf(input);

            } catch (IllegalArgumentException e) {
                System.out.println("Unsupported heuristic: \"" + input + "\"");
            }

        } while (heuristicType == null);

        System.out.print("Should the results be viewed step-by-step? (Y/N): ");

        boolean stepByStep = "Y".equalsIgnoreCase(scanner.nextLine());

        scanner.close();

        System.out.println();

        AStarAdmissibleHeuristic<String> heuristic;

        switch (heuristicType) {
            case FEWEST_CITIES:
                heuristic = (sourceVertex, targetVertex) -> 1;
                graph.edgeSet().forEach(edge -> graph.setEdgeWeight(edge, 1));
                break;
            case STRAIGHT_LINE_DISTANCE:
                heuristic = (sourceVertex, targetVertex) -> positions.get(sourceVertex).distance(positions.get(targetVertex));
                break;
            default:
                heuristic = null;
                break;
        }

        AStarAlgorithm<String, DefaultWeightedEdge> aStarAlgorithm = new AStarAlgorithm<>(graph, heuristic);

        List<String> path = aStarAlgorithm.getPath(startCity, endCity, excludedCities, stepByStep);

        if (path != null) {

            System.out.println("The final solution path is: ");

            double totalLength = 0;

            for (int i = 1; i < path.size(); i++) {

                //From A1 to B1. Length: 177

                String source = path.get(i - 1);
                String destination = path.get(i);
                double length = graph.getEdgeWeight(graph.getEdge(source, destination));

                System.out.printf("From %s to %s. Length: %.0f", source, destination, Math.floor(length));
                System.out.println();

                totalLength += length;

            }

            System.out.printf("Total Length: %.0f", Math.floor(totalLength));
            System.out.println();

        } else {

            System.out.println("There is no path possible from " + startCity + " to " + endCity);

        }

    }

}
