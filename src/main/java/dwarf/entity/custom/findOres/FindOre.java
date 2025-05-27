package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import dwarf.entity.custom.findOres.structures.AStar;
import dwarf.entity.custom.findOres.structures.DwarfNode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class FindOre extends Goal {
    protected final DwarfEntity dwarf;
    protected static final int scanRadius = 10;
    protected BlockPos targetOre = null;
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
        if (targetOre == null) {
            start();
            return;
        }

        if (dwarf.getCurrentPath() == null || dwarf.getCurrentPath().isEmpty()) {
            List<BlockPos> path = AStar.findPath(dwarf.getWorld(), dwarf.getBlockPos(), scanRadius);
            if (path != null  && !path.isEmpty()) {
                dwarf.setPath(path);
            } else {
                targetOre = null;  // No path found
                activeGoal = null;
            }
        }
    }

    @Override
    public void start() {
        activeGoal = this;

        BlockPos pos = dwarf.getBlockPos();
        World world = dwarf.getWorld();
        BlockPos closestOre = scanForOres();

        System.out.println("Inside start");

        if (closestOre != null) {
            // TODO remove
            List<BlockPos> path = AStar.findPath(world, pos, scanRadius);
            //EnvironmentScan scan = new EnvironmentScan(world, pos, scanRadius);
            //print3DArraySlices(scan.blockData);
            targetOre = closestOre;
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
}