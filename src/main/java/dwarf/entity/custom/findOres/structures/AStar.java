package dwarf.entity.custom.findOres.structures;

import dwarf.entity.custom.findOres.EnvironmentScan;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfPriorityQueue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class AStar {
    public static List<BlockPos> findPath(EnvironmentScan env, DwarfNode start, DwarfNode end) {
        // create a priority queue
        DwarfPriorityQueue queue = new DwarfPriorityQueue();

        // start and end nodes
        DwarfNode startNode = start;
        DwarfNode goalNode = end;

        System.out.println("Start node = ");
        startNode.printNode();
        System.out.println("Goal node = ");
        goalNode.printNode();
        // initialize startNode
        startNode.gScore = 0;
        startNode.fScore = DwarfNode.manhattanDist(startNode, goalNode);
        // add start node to queue
        queue.add(startNode);

        while (!queue.isEmpty()) {
            DwarfNode current = queue.poll();
            if(DwarfNode.isEqual(current, goalNode)){
                // Build and return the path
                ArrayList<BlockPos> path = new ArrayList<>();
                do {
                    path.add(env.getBlockPosFromArrayNode(current));
                    current = current.parent;
                } while (current != startNode);
                path.add(env.getBlockPosFromArrayNode(current));
                Collections.reverse(path);
                return path;
            }
            // mark current node as visited
            current.visited = true;
            // for all neighbors of current block
            for (DwarfNode neighbor: current.neighbors) {
                // if neighbor unvisited
                if (!neighbor.visited) {
                    int tempGScore = current.gScore + neighbor.type + neighbor.extraCost;
                    // if cheaper path to neighbor, update
                    if (tempGScore < neighbor.gScore) {
                        // set parent
                        neighbor.parent = current;
                        neighbor.gScore = tempGScore;
                        neighbor.fScore = neighbor.gScore + DwarfNode.manhattanDist(neighbor, goalNode);
                    }
                    // add neighbor to priority queue if not already in it
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
