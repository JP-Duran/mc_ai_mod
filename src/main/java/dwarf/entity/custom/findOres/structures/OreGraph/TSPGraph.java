package dwarf.entity.custom.findOres.structures.OreGraph;

import java.util.*;

public class TSPGraph {
    // adjacency nested hash maps for weighted graph representation
    private Map<GraphNode, Map<GraphNode, Integer>> adjacencyMap;
    // starting node for graph searches (dwarf location)
    public GraphNode startNode;

    // graph constructor
    public TSPGraph() {
        adjacencyMap = new HashMap<>();
    }

    // adds a new node to the graph
    public void addNode(GraphNode node) {
        adjacencyMap.putIfAbsent(node, new HashMap<>());
    }

    // adds a new edge from 'from' to 'to' with weight 'edgeWeight' to the graph
    // NOTE: adds parameter nodes to graph if not already present
    public void addEdge(GraphNode from, GraphNode to, int edgeWeight) {
        // add nodes to graph if not already present
        addNode(from);
        addNode(to);
        // add edge to adjacency map
        adjacencyMap.get(to).put(from, edgeWeight);
        adjacencyMap.get(from).put(to, edgeWeight);
    }

    // returns the edge weight from node 'from' to node 'to'
    public Integer getEdgeWeight(GraphNode from, GraphNode to) {
        return adjacencyMap.get(from).get(to);
    }

    // returns an ArrayList<GraphNode> of all keys in the adjacencyMap (all graph nodes)
    public ArrayList<GraphNode> getNodes() {
        return new ArrayList<>(adjacencyMap.keySet());
    }

    // returns a Map<GraphNode, Integer> containing all neighbors and edge weights for
    // node 'node'
    public Map<GraphNode, Integer> getNeighbors(GraphNode node) {
        return adjacencyMap.getOrDefault(node, new HashMap<>());
    }
}
