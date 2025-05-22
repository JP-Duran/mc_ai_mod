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

    // TODO add hash map for ore positions Hashmap of ArrayLists or something like that

    // general variables for env scan
    private final World world;
    private final BlockPos centerPos;
    private final int scanRadius;

    // helper vars
    private int centerIndexX;
    private int centerIndexY;
    private int centerIndexZ;

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
    public int[][][] blockData;

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
        this.blockData = scanEnvironmentToArray(this.world, this.centerPos, this.scanRadius);
    }

    /*
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

    /*
     * creates a 3d array and fills it with block => integer map values
     * this essentially creates a novel representation of the environment around the mobs position where
     * each block is represented by an integer in a 3d array
     */
    public int[][][] scanEnvironmentToArray(World world, BlockPos centerPos, int scanRadius) {
        // array width/height/depth
        int size = (2 * scanRadius) + 1;

        int[][][] blockData = new int[size][size][size];

        // initialize array with OOB values
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    blockData[x][y][z] = OOB;
                }
            }
        }

        // calculate world coords for array[0][0][0]
        int worldOriginX = centerPos.getX() - scanRadius;
        int worldOriginY = centerPos.getY() - scanRadius;
        int worldOriginZ = centerPos.getZ() - scanRadius;

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
                DwarfNode blockCoord = new DwarfNode(arrayX, arrayY, arrayZ);
                // increment the counter and add position to hashmap if an ore is encountered
                switch (blockId) {
                    case DIAMOND:
                        // hash(diamond) = coordinate
                        DIAMOND_COUNT++;
                        oreData.computeIfAbsent(DIAMOND, k -> new ArrayList<>()).add(blockCoord);
                        break;
                    case GOLD:
                        GOLD_COUNT++;
                        oreData.computeIfAbsent(GOLD, k -> new ArrayList<>()).add(blockCoord);
                        break;
                    case EMERALD:
                        EMERALD_COUNT++;
                        oreData.computeIfAbsent(EMERALD, k -> new ArrayList<>()).add(blockCoord);
                        break;
                    case LAPIS:
                        LAPIS_COUNT++;
                        oreData.computeIfAbsent(LAPIS, k -> new ArrayList<>()).add(blockCoord);
                        break;
                    case REDSTONE:
                        REDSTONE_COUNT++;
                        oreData.computeIfAbsent(REDSTONE, k -> new ArrayList<>()).add(blockCoord);
                        break;
                    case IRON:
                        IRON_COUNT++;
                        oreData.computeIfAbsent(IRON, k -> new ArrayList<>()).add(blockCoord);
                        break;
                    case COPPER:
                        COPPER_COUNT++;
                        oreData.computeIfAbsent(COPPER, k -> new ArrayList<>()).add(blockCoord);
                        break;
                    case COAL:
                        COAL_COUNT++;
                        oreData.computeIfAbsent(COAL, k -> new ArrayList<>()).add(blockCoord);
                        break;
                    default:
                        break;
                }
                // fill the block position in the array
                blockData[arrayX][arrayY][arrayZ] = blockId;
            }
        }
        // fill the dwarfs position in the arr
        this.centerIndexX = centerPos.getX() - worldOriginX;
        this.centerIndexY = centerPos.getY() - worldOriginY;
        this.centerIndexZ = centerPos.getZ() - worldOriginZ;
        blockData[centerIndexX][centerIndexY][centerIndexZ] = DWARF_POS;
        return blockData;
    }

    // CONSTANTS
    // dwarf identifier value
    final public static int DWARF_POS = 100;
    // block identifier values
    final public static int DEFAULT = 5;
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