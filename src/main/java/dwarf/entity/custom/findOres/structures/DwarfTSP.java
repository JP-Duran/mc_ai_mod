package dwarf.entity.custom.findOres.structures;

import com.google.common.graph.Graph;
import dwarf.entity.custom.findOres.EnvironmentScan;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import dwarf.entity.custom.findOres.structures.OreGraph.GraphNode;
import dwarf.entity.custom.findOres.structures.OreGraph.TSPGraph;
import net.minecraft.util.math.BlockPos;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dwarf.entity.custom.findOres.EnvironmentScan.DIAMOND;
import static dwarf.entity.custom.findOres.EnvironmentScan.DWARF;

public class DwarfTSP {

    public static List<BlockPos> nearestNeighborTSP(EnvironmentScan env) {
        // generate TSP graph
        TSPGraph graph = generateTSPGraph(env);
        // run nearest neighbor algorithm on graph
        ArrayList<GraphNode> nearestNeighborPath = nearestNeighbor(graph);
        // run A* between all nodes and generate final path
        ArrayList<BlockPos> path = new ArrayList<>();
        DwarfNode current = graph.startNode.ore;
        for (int i = 1; i < nearestNeighborPath.size(); i++) {
            DwarfNode next = nearestNeighborPath.get(i).ore;
            path.addAll(AStar.findPath(env, current, next));
            env.resetEnvironmentNodes();
            current = next;
        }
        for (BlockPos pos : path) {
            System.out.println("( " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " )");
        }
        return path;
    }

    public static List<BlockPos> twoOptTSP(EnvironmentScan env) {
        // generate TSP graph
        TSPGraph graph = generateTSPGraph(env);
        // run two-opt algorithm on graph
        ArrayList<GraphNode> twoOptPath = twoOpt(graph);
        // run A* between all nodes and generate final path
        ArrayList<BlockPos> path = new ArrayList<>();
        DwarfNode current = graph.startNode.ore;
        for (int i = 1; i < twoOptPath.size(); i++) {
            DwarfNode next = twoOptPath.get(i).ore;
            path.addAll(AStar.findPath(env, current, next));
            env.resetEnvironmentNodes();
            current = next;
        }
        for (BlockPos pos : path) {
            System.out.println("( " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " )");
        }
        return path;
    }

    public static TSPGraph generateTSPGraph(EnvironmentScan env) {
        // initialize TSPGraph object
        TSPGraph graph = new TSPGraph();
        // get the list DwarfNodes representing all diamond ores in the scanned environment
        List<DwarfNode> diamondLocs = env.getSpecificOreData(DIAMOND);
        // get the DwarfNode representing the dwarf
        DwarfNode dwarf = env.getSpecificOreData(DWARF).get(0);
        // insert all diamond ores into graph
        for (DwarfNode diamond : diamondLocs) {
            // create a GraphNode corresponding to the diamond ore
            GraphNode node = new GraphNode(diamond);
            // insert the GraphNode into the TSPGraph
            graph.addNode(node);
        }
        // insert dwarf into the graph
        GraphNode dwarfNode = new GraphNode(dwarf);
        graph.addNode(dwarfNode);
        graph.startNode = dwarfNode;
        // get a list of all nodes in the graph
        ArrayList<GraphNode> nodeList = graph.getNodes();
        // calculate edge weights (manhattan dist) between all graph nodes and
        // populate the graph with edges-- graph will be complete
        for (int i = 0; i < nodeList.size(); i++) {
            for (int j = i + 1; j < nodeList.size(); j++) {
                int edgeWeight = DwarfNode.manhattanDist(nodeList.get(i).ore, nodeList.get(j).ore);
                graph.addEdge(nodeList.get(i), nodeList.get(j), edgeWeight);
            }
        }
        return graph;
    }

    public static ArrayList<GraphNode> nearestNeighbor(TSPGraph graph) {
        // initialize empty ArrayList
        ArrayList<GraphNode> path = new ArrayList<>();
        // choose dwarf node as starting point
        GraphNode startNode = graph.startNode;
        GraphNode currentNode = startNode;
        // pick nearest neighbor (according to edge weight) until all nodes are visited
        while (true) {
            Map<GraphNode, Integer> neighbors = graph.getNeighbors(currentNode);
            GraphNode minNeighbor = null;
            int minVal = Integer.MAX_VALUE;
            for (Map.Entry<GraphNode, Integer> entry : neighbors.entrySet()) {
                if (entry.getValue() < minVal && !entry.getKey().visited) {
                    minVal = entry.getValue();
                    minNeighbor = entry.getKey();
                }
            }
            // if no unvisited neighbors (tour is complete) return path
            if (minNeighbor == null) {
                path.add(currentNode);
                resetGraphVisited(graph);
                return path;
            }
            path.add(currentNode);
            currentNode.visited = true;
            currentNode = minNeighbor;
        }
    }

    public static ArrayList<GraphNode> twoOpt(TSPGraph graph) {
        // run nearestNeighbor on graph for initial tour path
        ArrayList<GraphNode> path = nearestNeighbor(graph);
        int pathCost = tourCost(graph, path);
        // if path length is < 4 two-opt cannot help
        // in this case nearestNeighbor is optimal path
        if (path.size() < 4) {
            return path;
        }
        int numNodes = path.size();
        // search for edges to swap until local minimum is reached
        while (true) {
            int currentCost = tourCost(graph, path);
            int minChange = 0;
            int iOpt = 0;
            int jOpt = 0;
            for (int i = 0; i < numNodes - 3; i++) {
                for (int j = i + 2; j < numNodes - 1; j++) {
                    GraphNode i1 = path.get(i);
                    GraphNode i2 = path.get(i + 1);
                    GraphNode j1 = path.get(j);
                    GraphNode j2 = path.get(j + 1);
                    System.out.println("Considering (" + i + ", " + (i+1) + "), (" + j + ", " + (j+1) + ")");
                    int diff = graph.getEdgeWeight(i1, j1) + graph.getEdgeWeight(i2, j2) -
                            graph.getEdgeWeight(i1, i2) - graph.getEdgeWeight(j1, j2);
                    System.out.println("Diff = " + diff);
                    if (diff < minChange) {
                        minChange = diff;
                        iOpt = i;
                        jOpt = j;
                    }
                }
            }
            if (minChange < 0) {
                ArrayList<GraphNode> newPath = (ArrayList<GraphNode>) path.clone();
                GraphNode tempJ = newPath.get(jOpt);
                newPath.set(jOpt, newPath.get(iOpt + 1));
                newPath.set(iOpt + 1, tempJ);
                System.out.println("Swapping (" + iOpt + ", " + (iOpt+1) + "), (" + jOpt + ", " + (jOpt+1)
                + ") with (" + iOpt + ", " + jOpt + "), (" + (iOpt+1) + ", " + (jOpt+1) + ")");
                path = newPath;
            } else {
                return path;
            }
        }
    }

    // calculates the total tour cost given a TSPGraph and a list of graphNodes
    public static int tourCost(TSPGraph graph, ArrayList<GraphNode> tour) {
        int tourCost = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            tourCost += graph.getEdgeWeight(tour.get(i), tour.get(i + 1));
        }
        return tourCost;
    }

    // resets all nodes in a TSP graph to unvisited
    public static void resetGraphVisited(TSPGraph graph) {
        ArrayList<GraphNode> nodes = graph.getNodes();
        for (GraphNode node : nodes) {
            node.visited = false;
        }
    }
}
