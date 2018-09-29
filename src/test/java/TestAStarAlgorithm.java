import edu.usf.myweb.jcameron2.AStarAlgorithm;
import edu.usf.myweb.jcameron2.ResourceUtils;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class TestAStarAlgorithm {

    @Test
    public void test() throws Exception {

        Graph<String, DefaultWeightedEdge> graph;
        Map<String, Point> positions;

        try (
                InputStream locationStream = TestAStarAlgorithm.class.getResourceAsStream("/locations.txt");
                InputStream connectionsStream = TestAStarAlgorithm.class.getResourceAsStream("/connections.txt")
        ) {

            positions = ResourceUtils.loadPositions(locationStream);
            graph = ResourceUtils.loadGraph(positions, connectionsStream);

        }

        AStarAdmissibleHeuristic<String> fewestCitiesHeuristic = (sourceVertex, targetVertex) -> 1;
        AStarAdmissibleHeuristic<String> sldHeuristic = (sourceVertex, targetVertex) -> positions.get(sourceVertex).distance(positions.get(targetVertex));

        AStarAlgorithm<String, DefaultWeightedEdge> fewestAlgorithm = new AStarAlgorithm<>(graph, fewestCitiesHeuristic);
        AStarAlgorithm<String, DefaultWeightedEdge> sldAlgorithm = new AStarAlgorithm<>(graph, sldHeuristic);

        AStarShortestPath<String, DefaultWeightedEdge> expectedAStar = new AStarShortestPath<>(graph, sldHeuristic);

        Assert.assertTrue(expectedAStar.isConsistentHeuristic(fewestCitiesHeuristic));
        Assert.assertTrue(expectedAStar.isConsistentHeuristic(sldHeuristic));

        Set<String> vertexSet = graph.vertexSet();

        //noinspection Duplicates
        for (int i = 0; i < 10; i++) { // Run 10 trials

            String start = randomNode(vertexSet);
            String end = randomNode(vertexSet);

            List<String> expectedPath = expectedAStar.getPath(start, end).getVertexList();
            List<String> actualPath = sldAlgorithm.getPath(start, end, Collections.emptySet());

            Assert.assertEquals(expectedPath, actualPath);

        }

        expectedAStar = new AStarShortestPath<>(graph, fewestCitiesHeuristic);

        //noinspection Duplicates
        for (int i = 0; i < 10; i++) { // Run 10 trials

            String start = randomNode(vertexSet);
            String end = randomNode(vertexSet);

            List<String> expectedPath = expectedAStar.getPath(start, end).getVertexList();
            List<String> actualPath = fewestAlgorithm.getPath(start, end, Collections.emptySet());

            Assert.assertEquals(expectedPath, actualPath);

        }

    }

    private String randomNode(Set<String> set) {

        int index = ThreadLocalRandom.current().nextInt(set.size());
        int i = 0;

        for (String s : set) {

            if (index == i++) {

                return s;

            }

        }

        return set.stream().findAny().orElse(null);

    }

}
