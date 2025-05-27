package dwarf.entity.custom.findOres;

import dwarf.entity.custom.findOres.structures.DwarfNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnvironmentScan {

    // general variables for env scan
    private final World world;
    private final BlockPos centerPos;
    private final int scanRadius;

    // helper vars
    private int centerIndexX;
    private int centerIndexY;
    private int centerIndexZ;
    private int worldOriginX;
    private int worldOriginY;
    private int worldOriginZ;

    // ore counts (within scanned environment)
    private int DIAMOND_COUNT;
    private int GOLD_COUNT;
    private int EMERALD_COUNT;
    private int LAPIS_COUNT;
    private int REDSTONE_COUNT;
    private int IRON_COUNT;
    private int COPPER_COUNT;
    private int COAL_COUNT;

    // 3d array holding environment scan
    public DwarfNode[][][] blockData;

    public HashMap<Integer, ArrayList<DwarfNode>> oreData;

    public EnvironmentScan(World world, BlockPos centerPos, int scanRadius) {
        // initialize vars
        this.world = world;
        this.centerPos = centerPos;
        this.scanRadius = scanRadius;
        this.DIAMOND_COUNT = 0;
        this.GOLD_COUNT = 0;
        this.EMERALD_COUNT = 0;
        this.LAPIS_COUNT = 0;
        this.REDSTONE_COUNT = 0;
        this.IRON_COUNT = 0;
        this.COPPER_COUNT = 0;
        this.COAL_COUNT = 0;
        // initialize ore map
        this.oreData = new HashMap<>();
        // scan the environment
        int size = (2 * scanRadius) + 1;
        this.blockData = new DwarfNode[size][size][size];
        scanEnvironmentToArray(this.world, this.centerPos, this.scanRadius);
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
    public DwarfNode[][][] scanEnvironmentToArray(World world, BlockPos centerPos, int scanRadius) {
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
                        DIAMOND_COUNT++;
                        oreData.computeIfAbsent(DIAMOND, k -> new ArrayList<>()).add(block);
                        break;
                    case GOLD:
                        GOLD_COUNT++;
                        oreData.computeIfAbsent(GOLD, k -> new ArrayList<>()).add(block);
                        break;
                    case EMERALD:
                        EMERALD_COUNT++;
                        oreData.computeIfAbsent(EMERALD, k -> new ArrayList<>()).add(block);
                        break;
                    case LAPIS:
                        LAPIS_COUNT++;
                        oreData.computeIfAbsent(LAPIS, k -> new ArrayList<>()).add(block);
                        break;
                    case REDSTONE:
                        REDSTONE_COUNT++;
                        oreData.computeIfAbsent(REDSTONE, k -> new ArrayList<>()).add(block);
                        break;
                    case IRON:
                        IRON_COUNT++;
                        oreData.computeIfAbsent(IRON, k -> new ArrayList<>()).add(block);
                        break;
                    case COPPER:
                        COPPER_COUNT++;
                        oreData.computeIfAbsent(COPPER, k -> new ArrayList<>()).add(block);
                        break;
                    case COAL:
                        COAL_COUNT++;
                        oreData.computeIfAbsent(COAL, k -> new ArrayList<>()).add(block);
                        break;
                    default:
                        break;
                }
                // fill the block position in the array
                blockData[arrayX][arrayY][arrayZ] = block;

                // Check for air below the current block
                // We can also add checks for blocks that have lava above them or something
                if(blockId == AIR && arrayY > 0){
                    DwarfNode below = blockData[arrayX][arrayY - 1][arrayZ];
                    if (below.type == AIR){
                        block.extraCost += 1;
                    }
                }
            }
        }
        // calculate dwarfs position (always in center of arr)
        centerIndexX = centerPos.getX() - worldOriginX;
        centerIndexY = centerPos.getY() - worldOriginY;
        centerIndexZ = centerPos.getZ() - worldOriginZ;
        // add dwarfs position to array and hashmap
        DwarfNode dwarfPos = new DwarfNode(centerIndexX, centerIndexY, centerIndexZ, DWARF);
        blockData[centerIndexX][centerIndexY][centerIndexZ] = dwarfPos;
        oreData.computeIfAbsent(DWARF, k -> new ArrayList<>()).add(dwarfPos);

        printAllLayers(blockData); // PRINT THE LAYERS

        return blockData;
    }

    /**
     * calculates all neighbors for the DwarfNodes in blockData
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
     * converts local array coordinates (integer x, y, z) to real-world BlockPos coordinates
     */
    public BlockPos getBlockPosFromArrayCoordinates(int arrayX, int arrayY, int arrayZ) {
        int worldX = this.worldOriginX + arrayX;
        int worldY = this.worldOriginY + arrayY;
        int worldZ = this.worldOriginZ + arrayZ;
        return new BlockPos(worldX, worldY, worldZ);
    }

    public void printAllLayers(DwarfNode[][][] blockData) {
        int sizeY = blockData[0].length;

        for (int y = 0; y < sizeY; y++) {
            System.out.println("Layer Y: " + y );
            for (int z = 0; z < blockData[0][0].length; z++) {
                for (int x = 0; x < blockData.length; x++) {
                    DwarfNode node = blockData[x][y][z];
                    System.out.printf("%d/%d ", node.type, node.extraCost);  // prints it out in block type/extra cost format used to see if my fix was working
                }
                System.out.println();
            }
            System.out.println();
        }
    }


    // CONSTANTS
    // dwarf identifier value
    final public static int DWARF = 100;
    // block identifier values
    final public static int DEFAULT = 5;
    // TODO SHOULD THIS BE 1? TO MAKE HEURISTIC ADMISSIBLE
    final public static int AIR = 0;
    final public static int DIRT = 1;
    final public static int STONE = 2;
    final public static int COBBLESTONE = 2; // Same as STONE
    final public static int DIAMOND = 99;
    final public static int GOLD = 98;
    final public static int EMERALD = 97;
    final public static int LAPIS = 96;
    final public static int REDSTONE = 95;
    final public static int IRON = 94;
    final public static int COPPER = 93;
    final public static int COAL = 92;
    // utility values
    final public static int GLASS = 1000;
    final public static int OOB = 999;

    // GETTERS
    public World getWorld() {
        return world;
    }

    public BlockPos getCenterPos() {
        return centerPos;
    }

    public int getScanRadius() {
        return scanRadius;
    }

    public int getDiamondCount() {
        return DIAMOND_COUNT;
    }

    public int getGoldCount() {
        return GOLD_COUNT;
    }

    public int getEmeraldCount() {
        return EMERALD_COUNT;
    }

    public int getLapisCount() {
        return LAPIS_COUNT;
    }

    public int getRedstoneCount() {
        return REDSTONE_COUNT;
    }

    public int getIronCount() {
        return IRON_COUNT;
    }

    public int getCopperCount() {
        return COPPER_COUNT;
    }

    public int getCoalCount() {
        return COAL_COUNT;
    }

    // returns entire hashmap object
    public HashMap<Integer, ArrayList<DwarfNode>> getOreData() {
        return oreData;
    }

    // returns specified ore's coordinate list, or null if none
    public List<DwarfNode> getSpecificOreData(int oreType) {
        return oreData.getOrDefault(oreType, null);
    }
}