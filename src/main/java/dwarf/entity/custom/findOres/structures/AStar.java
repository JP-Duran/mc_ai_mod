package dwarf.entity.custom.findOres.structures;

import dwarf.entity.custom.findOres.EnvironmentScan;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AStar {
    AStar(World world, BlockPos centerPos, int scanRadius) {
        // scan the environment
        EnvironmentScan env = new EnvironmentScan(world, centerPos, scanRadius);
        // create a priority queue
        DwarfPriorityQueue queue = new DwarfPriorityQueue();
        // create a visited array
        int size = (2 * scanRadius) + 1;
        boolean visited[][][] = new boolean[size][size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    visited[i][j][k] = false;
                }
            }
        }

        // initialize search
        DwarfNode startNode = env.getSpecificOreData(EnvironmentScan.DWARF).get(0);
        DwarfNode goalNode = env.getSpecificOreData(EnvironmentScan.DIAMOND).get(0);
        // initialize startNode
        startNode.gScore = 0;
        startNode.fScore = DwarfNode.manhattanDist(startNode, goalNode);

        while (!queue.isEmpty()) {
            DwarfNode current = queue.poll();
            // if the current node is the goal node, path has been found
            if (DwarfNode.isEqual(current, goalNode)) {
                // reconstruct path
            }
            // mark current node as visited
            visited[current.X][current.Y][current.Z] = true;

        }

    }
}
