package dwarf.entity.custom.findOres.structures;

import dwarf.entity.custom.findOres.EnvironmentScan;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfPriorityQueue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BadStar {
    public static List<BlockPos> findPath(World world, BlockPos centerPos, int scanRadius) {
        EnvironmentScan env = new EnvironmentScan(world, centerPos, scanRadius);
        DwarfPriorityQueue queue = new DwarfPriorityQueue();
        int size = (2 * scanRadius) + 1;

        List<DwarfNode> dwarfNodes = env.getSpecificOreData(EnvironmentScan.DWARF);
        List<DwarfNode> diamondNodes = env.getSpecificOreData(EnvironmentScan.DIAMOND);

        if (dwarfNodes == null || dwarfNodes.isEmpty() || diamondNodes == null || diamondNodes.isEmpty()) {
            return null;
        }

        DwarfNode startNode = dwarfNodes.get(0);
        DwarfNode goalNode = diamondNodes.get(0);

        startNode.gScore = 0;

        // BAD fScore: favor nodes further from goal and closer to start (opposite of good pathing)
        startNode.fScore = DwarfNode.manhattanDist(startNode, goalNode) * 2;

        queue.add(startNode);

        while (!queue.isEmpty()) {
            DwarfNode current = queue.poll();
            if (DwarfNode.isEqual(current, goalNode)) {
                ArrayList<BlockPos> path = new ArrayList<>();
                current = current.parent;
                while (current != null && current != startNode) {
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
                    // Make straight movement more expensive than turning (encourage zig-zag)
                    int tempGScore = current.gScore + 10 + (int)(Math.random() * 5); // random penalty

                    // Penalize nodes that align with the goal's x or z direction (avoid straight lines)
                    if (neighbor.X == goalNode.X || neighbor.Z == goalNode.Z) {
                        tempGScore += 20;
                    }

                    if (tempGScore < neighbor.gScore) {
                        neighbor.parent = current;
                        neighbor.gScore = tempGScore;

                        // BAD fScore: prioritize Manhattan from START instead of GOAL
                        neighbor.fScore = neighbor.gScore + DwarfNode.manhattanDist(neighbor, startNode);
                    }

                    if (queue.contains(neighbor)) {
                        queue.remove(neighbor);
                        queue.add(neighbor);
                    } else {
                        queue.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();
    }
}
