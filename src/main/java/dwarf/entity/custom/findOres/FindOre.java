package dwarf.entity.custom.findOres;

import dwarf.DwarfMod;
import dwarf.entity.custom.DwarfEntity;
import dwarf.entity.custom.findOres.structures.DwarfTSP;
import dwarf.entity.custom.findOres.structures.GreedyFloodFill;
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
    protected static final int scanRadius = 25;
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

        if (closestOre != null) {
            targetOre = closestOre;
            EnvironmentScan env = new EnvironmentScan(world, pos, scanRadius);
            List<BlockPos> path = null;
            List <BlockPos> pathAlt = null;
            switch (DwarfMod.a_flag) {
                case 1:
                    for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                        player.sendMessage(Text.literal("Main algorithm = nearest neighbor"), false);
                    }
                    path = DwarfTSP.nearestNeighborTSP(env);
                    pathAlt = DwarfTSP.twoOptTSP(env);
                    break;
                case 2:
                    for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                        player.sendMessage(Text.literal("Main algorithm = 2-opt"), false);
                    }
                    path = DwarfTSP.twoOptTSP(env);
                    pathAlt = DwarfTSP.nearestNeighborTSP(env);
                    break;
                case 3:
                    for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                        player.sendMessage(Text.literal("Main algorithm = Greedy Flood Fill"), false);
                    }
                    path = GreedyFloodFill.findPath(world, pos, scanRadius);
                    pathAlt = GreedyFloodFill.findPath(world, pos, scanRadius);
                    break;
            }
            if (path != null && !path.isEmpty()) {
                for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                    player.sendMessage(Text.literal("Main   alg path length = " + path.size()), false);
                    player.sendMessage(Text.literal("Other alg path length = " + pathAlt.size()), false);
                }
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



