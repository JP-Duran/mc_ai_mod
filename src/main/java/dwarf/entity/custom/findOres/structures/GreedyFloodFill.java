package dwarf.entity.custom.findOres.structures;

import dwarf.entity.custom.findOres.EnvironmentScan;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class GreedyFloodFill {
    public static List<BlockPos> findPath(World world, BlockPos centerPos, int scanRadius) {
        // Scan environment
        EnvironmentScan env = new EnvironmentScan(world, centerPos, scanRadius);
        List<DwarfNode> dwarfNodes = env.getSpecificOreData(EnvironmentScan.DWARF);
        List<DwarfNode> diamondNodes = env.getSpecificOreData(EnvironmentScan.DIAMOND);

        if (dwarfNodes == null || dwarfNodes.isEmpty() || diamondNodes == null || diamondNodes.isEmpty()) {
            return null; // Handle better in production
        }

        DwarfNode startNode = dwarfNodes.get(0);
        DwarfNode goalNode = diamondNodes.get(0);

        System.out.println("Start node:");
        startNode.printNode();
        System.out.println("Goal node:");
        goalNode.printNode();

        // Use a queue for flood fill (can switch to stack for DFS)
        Queue<DwarfNode> queue = new LinkedList<>();
        startNode.visited = true;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            DwarfNode current = queue.poll();

            if (DwarfNode.isEqual(current, goalNode)) {
                // Reconstruct path
                ArrayList<BlockPos> path = new ArrayList<>();
                while (current != null && current != startNode) {
                    path.add(env.getBlockPosFromArrayNode(current));
                    current = current.parent;
                }
                path.add(env.getBlockPosFromArrayNode(startNode));
                Collections.reverse(path);
                return path;
            }

            for (DwarfNode neighbor : current.neighbors) {
                if (!neighbor.visited) {
                    neighbor.visited = true;
                    neighbor.parent = current;
                    queue.add(neighbor);
                }
            }
        }

        // No path found
        return new ArrayList<>();
    }
}