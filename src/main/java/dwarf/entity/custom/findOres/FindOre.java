package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import dwarf.entity.custom.findOres.structures.DwarfTSP;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class FindOre extends Goal {
    protected final DwarfEntity dwarf;
    protected static final int scanRadius = 10;
    protected static BlockPos targetOre = null;
    protected static final int torchLightLevel = 5;

    private static FindOre activeGoal = null;

    public FindOre(DwarfEntity dwarf) {
        this.dwarf = dwarf;
    }

    @Override
    public boolean canStart() {
        BlockPos pos = dwarf.getBlockPos();
        World world = dwarf.getWorld();

        for (BlockPos blockPosition : BlockPos.iterateOutwards(pos, scanRadius, scanRadius, scanRadius)) {
            if (isTargetOre(world.getBlockState(blockPosition).getBlock())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        activeGoal = this;

        BlockPos pos = dwarf.getBlockPos();
        World world = dwarf.getWorld();
        BlockPos closestOre = scanForOres();

        System.out.println("Inside start");

        if (closestOre != null) {
            targetOre = closestOre;
            EnvironmentScan env = new EnvironmentScan(world, pos, scanRadius);
            DwarfTSP TSP = new DwarfTSP();
            List<BlockPos> path = TSP.nearestNeighborTSP(env);
            if (path != null && !path.isEmpty()) {
                dwarf.setPath(path);
            } else{
                targetOre = null;
                activeGoal = null;
            }
        } else {
            activeGoal = null;
        }
    }


    protected BlockPos scanForOres() {
        BlockPos pos = dwarf.getBlockPos();
        World world = dwarf.getWorld();

        BlockPos closestOre = null;
        double closestDistance = Double.MAX_VALUE;

        for (BlockPos blockPosition : BlockPos.iterateOutwards(pos, scanRadius, scanRadius, scanRadius)) {
            if (isTargetOre(world.getBlockState(blockPosition).getBlock())) {
                double distance = pos.getSquaredDistance(blockPosition);
                if (distance < closestDistance) {
                    closestOre = blockPosition.toImmutable();
                    closestDistance = distance;
                }
            }
        }

        return closestOre;
    }


    @Override
    public boolean shouldContinue() {
        return targetOre != null && activeGoal == this;
    }

    public static void placeTorch(DwarfEntity dwarf) {
        World world = dwarf.getWorld();
        BlockPos pos = dwarf.getBlockPos();

        if (world.getLightLevel(pos) <= torchLightLevel) {
            if (world.isAir(pos)) {
                world.setBlockState(pos, Blocks.TORCH.getDefaultState());
            }
        }
    }

    @Override
    public void stop() {
        if (activeGoal == this) {
            activeGoal = null;
        }
        targetOre = null;
    }


    // Abstract methods that child classes must implement
    protected abstract boolean isTargetOre(Block block);

    public static void resetPath(DwarfEntity dwarf){
        targetOre = null;
        dwarf.setPath(null);
        activeGoal = null;
    }
}



