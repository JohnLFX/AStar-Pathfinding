package edu.usf.myweb.jcameron2;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;

import java.util.*;

public class AStarAlgorithm<V, E> {

    private final Graph<V, E> graph;
    private final AStarAdmissibleHeuristic<V> heuristic;

    public AStarAlgorithm(Graph<V, E> graph, AStarAdmissibleHeuristic<V> heuristic) {
        this.graph = graph;
        this.heuristic = heuristic;
    }

    public List<V> getPath(V start, V goal, Set<V> exclusions) {
        return getPath(start, goal, exclusions, false);
    }

    public List<V> getPath(V start, V goal, Set<V> exclusions, boolean debug) {

        Set<V> openNodes = new HashSet<>();
        Set<V> closedNodes = new HashSet<>();
        Map<V, Double> gScoreMap = new HashMap<>();
        Map<V, V> predecessorMap = new HashMap<>();
        Map<V, Double> fScoreMap = new HashMap<>();

        gScoreMap.put(start, 0.0);
        openNodes.add(start);

        while (!openNodes.isEmpty()) {

            V currentNode = openNodes.stream().sorted().min(Comparator.comparing(fScoreMap::get)).get();

            openNodes.remove(currentNode);
            closedNodes.add(currentNode);

            Map<V, Double> possibleNeighbors = new TreeMap<>();

            for (E edge : graph.outgoingEdgesOf(currentNode)) {

                V neighbor = Graphs.getOppositeVertex(graph, edge, currentNode);

                if (exclusions.contains(neighbor))
                    continue;

                possibleNeighbors.put(neighbor, graph.getEdgeWeight(edge));

            }

            if (debug) {
                System.out.println("City selected: " + currentNode);

                String possibleCitiesOutput = new TreeSet<>(possibleNeighbors.keySet()).toString();

                System.out.println("Possible cities to where to travel: " + possibleCitiesOutput.substring(1, possibleCitiesOutput.length() - 1));
            }

            if (!currentNode.equals(goal)) {

                // Nodes are ordered alphabetically
                for (Map.Entry<V, Double> en : possibleNeighbors.entrySet()) {

                    V neighbor = en.getKey();

                    if (neighbor.equals(currentNode) || closedNodes.contains(neighbor))
                        continue;

                    double tentativeGScore = gScoreMap.get(currentNode) + en.getValue();

                    if (!openNodes.contains(neighbor)) {

                        openNodes.add(neighbor);

                    } else if (tentativeGScore >= gScoreMap.get(neighbor)) {

                        continue;

                    }

                    predecessorMap.put(neighbor, currentNode);
                    gScoreMap.put(neighbor, tentativeGScore);
                    fScoreMap.put(neighbor, tentativeGScore + heuristic.getCostEstimate(neighbor, goal));

                }

            }

            if (debug) {

                System.out.print("Cities at the end of possible paths: ");

                fScoreMap.entrySet().stream().filter(en -> !closedNodes.contains(en.getKey())).sorted(Map.Entry.comparingByValue()).forEach(
                        en -> System.out.printf("%s(%.0f) ", en.getKey(), Math.floor(en.getValue()))
                );

                System.out.println();

                System.out.println();

            }

            if (currentNode.equals(goal)) {

                List<V> totalPath = new ArrayList<>();
                totalPath.add(currentNode);

                Set<V> keys = predecessorMap.keySet();

                while (keys.contains(currentNode)) {

                    currentNode = predecessorMap.get(currentNode);
                    totalPath.add(currentNode);

                }

                Collections.reverse(totalPath);
                return totalPath;

            }

        }

        // No path exists from sourceVertex to TargetVertex
        return null;

    }


}
