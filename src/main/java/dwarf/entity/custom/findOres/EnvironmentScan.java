package dwarf.entity.custom.findOres;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnvironmentScan {

    /*
     * returns an integer for each block type
     * this integer represents how much 'time' it takes to traverse the block
     */
    public static int getBlockId(World world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        // block type => integer mapping
        if (block == Blocks.AIR) return 0;
        if (block == Blocks.DIRT) return 1;
        if (block == Blocks.STONE) return 2;
        if (block == Blocks.DIAMOND_ORE) return 99;
        // default return value (if block not specified)
        return 5;
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
        return blockData;
    }
}