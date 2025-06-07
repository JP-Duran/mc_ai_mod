package dwarf.entity.custom.findOres.structures;

import dwarf.entity.custom.findOres.EnvironmentScan;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfPriorityQueue;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class AStar {
    /**
     * Uses 3-dimensional A* pathfinding to find a path within 'env' from 'start' to 'end'
     * This path will be returned as a List of BlockPos, which is in real-world coordinates and is
     * not dependent on any custom environment indexing
     */
    public static List<BlockPos> findPath(EnvironmentScan env, DwarfNode start, DwarfNode end) {
        // create a priority queue
        DwarfPriorityQueue queue = new DwarfPriorityQueue();

        // start and end nodes

        System.out.println("Start node = ");
        start.printNode();
        System.out.println("Goal node = ");
        end.printNode();
        // initialize startNode
        start.gScore = 0;
        start.fScore = DwarfNode.manhattanDist(start, end);
        // add start node to queue
        queue.add(start);

        while (!queue.isEmpty()) {
            DwarfNode current = queue.poll();
            if(DwarfNode.isEqual(current, end)){
                // Build and return the path
                ArrayList<BlockPos> path = new ArrayList<>();
                do {
                    //System.out.println("Path node cost = " + current.type + "/" + current.extraCost);
                    path.add(env.getBlockPosFromArrayNode(current));
                    current = current.parent;
                } while (current != start);
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
                        neighbor.fScore = neighbor.gScore + DwarfNode.manhattanDist(neighbor, end);
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
