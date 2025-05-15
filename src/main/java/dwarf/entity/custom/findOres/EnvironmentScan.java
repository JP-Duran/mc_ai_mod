package dwarf.entity.custom.findOres;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnvironmentScan {

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
        if (block == Blocks.DIAMOND_ORE) return DIAMOND;
        if (block == Blocks.GOLD_ORE) return GOLD;
        if (block == Blocks.EMERALD_ORE) return EMERALD;
        if (block == Blocks.LAPIS_ORE) return LAPIS;
        if (block == Blocks.REDSTONE_ORE) return REDSTONE;
        if (block == Blocks.IRON_ORE) return IRON;
        if (block == Blocks.COPPER_ORE) return COPPER;
        if (block == Blocks.COAL_ORE) return COAL;
        // for clear roof application (alg visibility setups)
        if (block == Blocks.GLASS) return Integer.MAX_VALUE;
        // default return value (if block not specified)
        return DEFAULT;
    }

    /*
     * creates a 3d array and fills it with block => integer map values
     * this essentially creates a novel representation of the environment around the center
     */
    public static int[][][] scanEnvironmentToArray(World world, BlockPos centerPos, int scanRadius) {
        // array width/height/depth
        int size = (2 * scanRadius) + 1;

        int[][][] blockData = new int[size][size][size];

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
                blockData[arrayX][arrayY][arrayZ] = blockId;
            }
        }
        // fill the dwarfs position in the arr
        blockData[centerPos.getX() - worldOriginX][centerPos.getY() - worldOriginY][centerPos.getZ() - worldOriginZ] = DWARF_POS;
        return blockData;
    }
}