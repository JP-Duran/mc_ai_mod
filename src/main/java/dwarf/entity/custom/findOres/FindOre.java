package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import dwarf.entity.custom.findOres.structures.AStar;
import dwarf.entity.custom.findOres.structures.DwarfTSP;
import dwarf.entity.custom.findOres.structures.OreGraph.DwarfNode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Items;
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
//        if (activeGoal != null && activeGoal != this) {
//            return false;
//        }

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
    public void tick() {
//        System.out.println("Target ore at: " + targetOre.getX() + targetOre.getY() + targetOre.getZ());
//        if (targetOre != null && dwarf.getPos().squaredDistanceTo(Vec3d.ofCenter(targetOre)) < 3) {
//
//            // Mine the ore
//            if (!dwarf.getWorld().isAir(targetOre)) {
//                dwarf.getWorld().breakBlock(targetOre, true, dwarf);
//            }
//
//            targetOre = null; // Clear so we find a new one
//            dwarf.setPath(null);
//            activeGoal = null;
//            System.out.println("CLEARED TARGET IN FINDORE");
//            return;
//        }

//        // Find a new target if none
//        if (targetOre == null) {
//            System.out.println("Target is none, finding new ore inside findOre");
//            start();
//            return;
//        }
//
//        if ((dwarf.getCurrentPath() == null || dwarf.getCurrentPath().isEmpty()) && targetOre != null) {
//            System.out.println("Path empty, trying to recompute");
//            List<BlockPos> path = AStar.findPath(dwarf.getWorld(), dwarf.getBlockPos(), scanRadius);
//            if (path != null && !path.isEmpty()) {
//                dwarf.setPath(path);
//            } else {
//                System.out.println("Path still null, abandoning target");
//                targetOre = null;
//                activeGoal = null;
//            }
//        }
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
            if(path != null && !path.isEmpty()){

                for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                    player.sendMessage(Text.literal("Found Path!"), false);
                }

                dwarf.setPath(path);
            } else{
                for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                    player.sendMessage(Text.literal("No ore found"), false);
                }
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

    protected void placeTorch() {
        World world = dwarf.getWorld();
        BlockPos pos = dwarf.getBlockPos();

        SimpleInventory inventory = dwarf.getInventory();
        int torchSlot = -1;
        // Loop through inventory to find torch
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isOf(Items.TORCH)) {
                torchSlot = i;
                break;
            }
        }

        if (torchSlot == -1) {
            return;
        }
        if (world.getLightLevel(pos) <= torchLightLevel) {
            if (world.isAir(pos)) {
                world.setBlockState(pos, Blocks.TORCH.getDefaultState());
                inventory.getStack(torchSlot).decrement(1);
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
    protected abstract String getOreName();


    public static void print3DArraySlices(DwarfNode[][][] array3D) {
        for (int i = 0; i < array3D.length; i++) {
            System.out.println("Slice " + i + ":");
            for (int j = array3D[i].length - 1; j >= 0; j--) {
                System.out.print("  ");
                for (int k = array3D[i][j].length - 1; k >= 0; k--) {
                    switch(array3D[i][j][k].type) {
                        case EnvironmentScan.GLASS:
                            System.out.printf("%-4s", "X");
                            break;
                        case EnvironmentScan.DEFAULT:
                            System.out.printf("%-4s", "Z");
                            break;
                        default:
                            System.out.printf("%-4d", array3D[i][j][k].type);
                            break;
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void resetPath(DwarfEntity dwarf){
        targetOre = null;
        dwarf.setPath(null);
        activeGoal = null;
    }
}



