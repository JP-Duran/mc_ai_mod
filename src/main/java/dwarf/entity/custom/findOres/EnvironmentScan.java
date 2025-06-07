package dwarf.entity.custom.findOres;

import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnvironmentScan {

    private final int scanRadius;

    private int worldOriginX;
    private int worldOriginY;
    private int worldOriginZ;

    // 3d array holding environment scan
    public DwarfNode[][][] blockData;

    // hashmap holding ore locations in blockData
    public HashMap<Integer, ArrayList<DwarfNode>> oreData;

    /**
     * EnvironmentScan constructor
     */
    public EnvironmentScan(World world, BlockPos centerPos, int scanRadius) {
        // initialize vars
        // general variables for env scan
        this.scanRadius = scanRadius;
        // initialize ore map
        this.oreData = new HashMap<>();
        // scan the environment
        int size = (2 * scanRadius) + 1;
        this.blockData = new DwarfNode[size][size][size];
        scanEnvironmentToArray(world, centerPos, this.scanRadius);
        // calculate neighbors
        this.calculateBlockNeighbors();
    }

    /**
     * returns an integer for each block type
     * this integer represents how much 'time' it takes to traverse the block
     */
    public static int getBlockId(World world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        // block type => integer mapping
        if (block == Blocks.AIR) return AIR;
        if (block == Blocks.DIRT) return DIRT;
        if (block == Blocks.STONE) return STONE;
        if (block == Blocks.COBBLESTONE) return COBBLESTONE;
        if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) return DIAMOND;
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) return GOLD;
        if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) return EMERALD;
        if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) return LAPIS;
        if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) return REDSTONE;
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) return IRON;
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) return COPPER;
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) return COAL;
        if (block == Blocks.LAVA) return LAVA;
        // for clear roof application (alg visibility setups)
        if (block == Blocks.GLASS) return GLASS;
        // default return value (if block not specified)
        return DEFAULT;
    }

    /**
     * creates a 3d array and fills it with DwarfNode representing the blocks in the environment
     * this essentially creates a novel representation of the environment around the mobs position where
     * each block is represented by a DwarfNode in a 3d array
     */
    public void scanEnvironmentToArray(World world, BlockPos centerPos, int scanRadius) {
        // array width/height/depth
        int size = (2 * scanRadius) + 1;

        // initialize array with OOB values
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    blockData[x][y][z] = new DwarfNode(x, y, z, OOB);
                }
            }
        }

        // calculate world coords for array[0][0][0]
        worldOriginX = centerPos.getX() - scanRadius;
        worldOriginY = centerPos.getY() - scanRadius;
        worldOriginZ = centerPos.getZ() - scanRadius;

        // fill the 3d arr
        for (BlockPos currentWorldPos : BlockPos.iterateOutwards(centerPos, scanRadius, scanRadius, scanRadius)) {

            int[][] directions = {{0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};

            int arrayX = currentWorldPos.getX() - worldOriginX;
            int arrayY = currentWorldPos.getY() - worldOriginY;
            int arrayZ = currentWorldPos.getZ() - worldOriginZ;

            // check if indices are in bounds
            if (arrayX >= 0 && arrayX < size &&
                    arrayY >= 0 && arrayY < size &&
                    arrayZ >= 0 && arrayZ < size) {

                int blockId = getBlockId(world, currentWorldPos);
                DwarfNode block = new DwarfNode(arrayX, arrayY, arrayZ, blockId);
                // increment the counter and add position to hashmap if an ore is encountered
                switch (blockId) {
                    case DIAMOND:
                        oreData.computeIfAbsent(DIAMOND, k -> new ArrayList<>()).add(block);
                        break;
                    case GOLD:
                        oreData.computeIfAbsent(GOLD, k -> new ArrayList<>()).add(block);
                        break;
                    case EMERALD:
                        oreData.computeIfAbsent(EMERALD, k -> new ArrayList<>()).add(block);
                        break;
                    case LAPIS:
                        oreData.computeIfAbsent(LAPIS, k -> new ArrayList<>()).add(block);
                        break;
                    case REDSTONE:
                        oreData.computeIfAbsent(REDSTONE, k -> new ArrayList<>()).add(block);
                        break;
                    case IRON:
                        oreData.computeIfAbsent(IRON, k -> new ArrayList<>()).add(block);
                        break;
                    case COPPER:
                        oreData.computeIfAbsent(COPPER, k -> new ArrayList<>()).add(block);
                        break;
                    case COAL:
                        oreData.computeIfAbsent(COAL, k -> new ArrayList<>()).add(block);
                        break;
                    case LAVA:
                        oreData.computeIfAbsent(LAVA, k -> new ArrayList<>()).add(block);
                        break;
                    case OBSIDIAN:
                        oreData.computeIfAbsent(OBSIDIAN, k -> new ArrayList<>()).add(block);
                        break;
                    default:
                        break;
                }
                // fill the block position in the array
                blockData[arrayX][arrayY][arrayZ] = block;

                // Check for air below the current block
                // This logic helps ensure the dwarf displays 'desirable' behavior in pathfinding (such that it does
                // not overuse towering in its pathing)
                if (arrayY > 0) {
                    DwarfNode below = blockData[arrayX][arrayY - 1][arrayZ];
                    if (below.type == AIR) {
                        if (below.extraCost == 0) {
                            block.extraCost = 5;
                        } else {
                            block.extraCost += below.extraCost + 5;
                        }
                    }
                }

                // For some reason it only updates blocks to the side not above or below
                for (int[] direction : directions) {
                    int x = arrayX + direction[0];
                    int y = arrayY + direction[1];
                    int z = arrayZ + direction[2];

                    if (x >= 0 && x < size && y >= 0 && y > size && z >= 0 && z > size) {
                        blockData[x][y][z].extraCost = LAVA;
                    }
                }

            }
        }
        // calculate dwarfs position (always in center of arr)
        int centerIndexX = centerPos.getX() - worldOriginX;
        int centerIndexY = centerPos.getY() - worldOriginY;
        int centerIndexZ = centerPos.getZ() - worldOriginZ;
        // add dwarfs position to array and hashmap
        DwarfNode dwarfPos = new DwarfNode(centerIndexX, centerIndexY, centerIndexZ, DWARF);
        blockData[centerIndexX][centerIndexY][centerIndexZ] = dwarfPos;
        oreData.computeIfAbsent(DWARF, k -> new ArrayList<>()).add(dwarfPos);
    }

    /**
     * calculates all neighbors for the DwarfNodes in blockData -- this is essential for the A* algorithm
     */
    public void calculateBlockNeighbors() {
        // array dimensions
        int size = (2 * scanRadius) + 1;
        // possible directions for neighbors
        int[][] directions = {{0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
        // calculate neighbors for all blocks
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    DwarfNode current = blockData[i][j][k];
                    for (int[] dir : directions) {
                        int neighborX = current.X + dir[0];
                        int neighborY = current.Y + dir[1];
                        int neighborZ = current.Z + dir[2];
                        // check if neighbor in bounds
                        if (neighborX >= 0 && neighborX < size &&
                                neighborY >= 0 && neighborY < size &&
                                neighborZ >= 0 && neighborZ < size) {
                            // if neighbor not glass or OOB, add to neighbor list
                            DwarfNode neighbor = blockData[neighborX][neighborY][neighborZ];
                            if (neighbor.type != GLASS && neighbor.type != OOB) {
                                current.neighbors.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * converts local array coordinates (from a DwarfNode) to real-world BlockPos coordinates
     */
    public BlockPos getBlockPosFromArrayNode(DwarfNode localNode) {
        if (localNode == null) {
            return null;
        }
        int worldX = worldOriginX + localNode.X;
        int worldY = worldOriginY + localNode.Y;
        int worldZ = worldOriginZ + localNode.Z;
        return new BlockPos(worldX, worldY, worldZ);
    }

    /**
     * returns a list of DwarfNodes representing all the specified oreType within the environment scan
     */
    public List<DwarfNode> getSpecificOreData(int oreType) {
        return oreData.getOrDefault(oreType, null);
    }

    /**
     * resets the members in a DwarfNode that are modified within A* -- this must be called on the EnvironmentScan
     * between each function call of A*
     */
    public void resetEnvironmentNodes() {
        // array dimensions
        int size = (2 * scanRadius) + 1;
        // calculate neighbors for all blocks
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    DwarfNode node = blockData[i][j][k];
                    node.visited = false;
                    node.fScore = Integer.MAX_VALUE;
                    node.gScore = Integer.MAX_VALUE;
                    node.parent = null;
                }
            }
        }
    }

    // CONSTANTS
    // dwarf identifier value
    final public static int DWARF = 100;
    // block identifier values
    final public static int DEFAULT = 25;
    final public static int AIR = 1;
    final public static int DIRT = 5;
    final public static int STONE = 15;
    final public static int COBBLESTONE = 15; // Same as STONE
    final public static int DIAMOND = 99;
    final public static int GOLD = 98;
    final public static int EMERALD = 97;
    final public static int LAPIS = 96;
    final public static int REDSTONE = 95;
    final public static int IRON = 94;
    final public static int COPPER = 93;
    final public static int COAL = 92;
    final public static int LAVA = 9999;
    final public static int OBSIDIAN = 900;
    // utility values
    final public static int GLASS = 1000;
    final public static int OOB = 999;

}