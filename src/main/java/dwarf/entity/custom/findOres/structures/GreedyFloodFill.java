package dwarf.entity.custom.findOres.structures;

import dwarf.entity.custom.findOres.EnvironmentScan;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfPriorityQueue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class GreedyFloodFill {
    public static List<BlockPos> findPath(World world, BlockPos centerPos, int scanRadius) {
        EnvironmentScan env = new EnvironmentScan(world, centerPos, scanRadius);
        DwarfPriorityQueue queue = new DwarfPriorityQueue();

        List<DwarfNode> dwarfNodes = env.getSpecificOreData(EnvironmentScan.DWARF);
        List<DwarfNode> diamondNodes = env.getSpecificOreData(EnvironmentScan.DIAMOND);

        if (dwarfNodes == null || dwarfNodes.isEmpty() || diamondNodes == null || diamondNodes.isEmpty()) {
            return null;
        }

        DwarfNode startNode = dwarfNodes.get(0);
        DwarfNode goalNode = diamondNodes.get(0);

        System.out.println("Greedy Flood Fill Start node = ");
        startNode.printNode();
        System.out.println("Greedy Flood Fill Goal node = ");
        goalNode.printNode();

        // initialize startNode
        startNode.fScore = DwarfNode.manhattanDist(startNode, goalNode);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            DwarfNode current = queue.poll();

            if (DwarfNode.isEqual(current, goalNode)) {
                ArrayList<BlockPos> path = new ArrayList<>();
                current = current.parent;
                while (current != startNode) {
                    path.add(env.getBlockPosFromArrayNode(current));
                    current = current.parent;
                }
                path.add(env.getBlockPosFromArrayNode(startNode));
                Collections.reverse(path);
                return path;
            }

            current.visited = true;

            for (DwarfNode neighbor : current.neighbors) {
                if (!neighbor.visited) {
                    int heuristic = DwarfNode.manhattanDist(neighbor, goalNode);
                    if (heuristic < neighbor.fScore) {
                        neighbor.parent = current;
                        neighbor.fScore = heuristic;
                    }

                    if (queue.contains(neighbor)) {
                        queue.remove(neighbor);
                    }
                    queue.add(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }
}
