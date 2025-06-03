package dwarf.entity.custom.findOres.structures;

import dwarf.entity.custom.findOres.EnvironmentScan;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import dwarf.entity.custom.findOres.structures.OreGraph.GraphNode;
import dwarf.entity.custom.findOres.structures.OreGraph.TSPGraph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static dwarf.entity.custom.findOres.EnvironmentScan.DIAMOND;

public class DwarfTSP {
    public List<DwarfNode> runDwarfTSP(EnvironmentScan env) {
        // initialize TSPGraph object
        TSPGraph graph = new TSPGraph();
        // get the list DwarfNodes representing all diamond ores in the scanned environment
        List<DwarfNode> diamondLocs = env.getSpecificOreData(DIAMOND);
        // insert all diamond ores into graph
        for (DwarfNode diamond : diamondLocs) {
            // create a GraphNode corresponding to the diamond ore
            GraphNode node = new GraphNode(diamond);
            // insert the GraphNode into the TSPGraph
            graph.addNode(node);
        }
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

    }
}
