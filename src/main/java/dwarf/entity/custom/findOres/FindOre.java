package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import static dwarf.entity.custom.findOres.EnvironmentScan.DEFAULT;
import static dwarf.entity.custom.findOres.EnvironmentScan.scanEnvironmentToArray;


public abstract class FindOre extends Goal {
    protected final DwarfEntity dwarf;
    protected static final int scanRadius = 5;
    protected BlockPos targetOre = null;
    protected static final int torchLightLevel = 5;

    private static FindOre activeGoal = null;

    public FindOre(DwarfEntity dwarf) {
        this.dwarf = dwarf;
    }

    @Override
    public boolean canStart() {
        if (activeGoal != null && activeGoal != this) {
            return false;
        }

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

        // If we're close enough to the target, mine it
        if (dwarf.getBlockPos().isWithinDistance(targetOre, 2)) {
            mineOre(targetOre);
            return;
        }

        // Check if we need to break blocks or recalculate path
        moveToTarget(dwarf.getBlockPos(), targetOre);
    }

    @Override
    public void start() {
        activeGoal = this;

        BlockPos pos = dwarf.getBlockPos();
        World world = dwarf.getWorld();

        BlockPos closestOre = null;
        double closestDistance = Double.MAX_VALUE;

        for (BlockPos blockPosition : BlockPos.iterateOutwards(pos, scanRadius, scanRadius, scanRadius)) {
            if (isTargetOre(world.getBlockState(blockPosition).getBlock()) &&
                    pos.getSquaredDistance(blockPosition) < closestDistance) {

                closestOre = blockPosition.toImmutable();
                closestDistance = pos.getSquaredDistance(blockPosition);
            }
        }

        if (closestOre != null) {
            // TODO remove
            int envScan[][][] = scanEnvironmentToArray(world, pos, scanRadius);
            print3DArraySlices(envScan);
            targetOre = closestOre;
        } else {
            activeGoal = null;
        }
    }


    @Override
    public boolean shouldContinue() {
        return targetOre != null && activeGoal == this;
    }


    protected void mineOre(BlockPos orePos) {
        World world = dwarf.getWorld();

        // Check if the block is still the target ore
        if (isTargetOre(world.getBlockState(orePos).getBlock())) {
            // Break the ore and drop items
            world.breakBlock(orePos, true, dwarf);

            // Reset target so dwarf looks for more ore
            targetOre = null;
        }
    }

    protected void moveToTarget(BlockPos start, BlockPos end) {
        Path path = dwarf.getNavigation().findPathTo(end.getX(), end.getY(), end.getZ(), 0);
        placeTorch();

        // Check if we're at the ore position
        if (start.isWithinDistance(end, 2)) {
            mineOre(end);
            return;
        }

        // First try to follow the path if we found one
        if (path != null) {
            dwarf.getNavigation().startMovingAlong(path, 1.0D);
        }

        // If we're not following a path or stuck then break blocks
        if (!dwarf.getNavigation().isFollowingPath()) {
            World world = dwarf.getWorld();
            Vec3i direction = end.subtract(start);
            int x = Integer.signum(direction.getX());
            int y = Integer.signum(direction.getY());
            int z = Integer.signum(direction.getZ());

            BlockPos faceLevelPos = start.add(x, y + 1, z);
            BlockPos footLevelPos = start.add(x, y, z);

            // If there are blocks in the way, break them
            if (!world.isAir(faceLevelPos) || !world.isAir(footLevelPos)) {
                if (!world.isAir(faceLevelPos)) {
                    world.breakBlock(faceLevelPos, true, dwarf);
                }
                if (!world.isAir(footLevelPos)) {
                    world.breakBlock(footLevelPos, true, dwarf);
                }

                // Try to move after breaking blocks
                dwarf.getNavigation().startMovingTo(end.getX(), end.getY(), end.getZ(), 1.0D);
            }
        }
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


    public static void print3DArraySlices(int[][][] array3D) {
        for (int i = 0; i < array3D.length; i++) {
            System.out.println("Slice " + i + ":");
            for (int j = array3D[i].length - 1; j >= 0; j--) {
                System.out.print("  ");
                for (int k = array3D[i][j].length - 1; k >= 0; k--) {
                    switch(array3D[i][j][k]) {
                        case Integer.MAX_VALUE:
                            System.out.printf("%-4s", "X");
                            break;
                        case DEFAULT:
                            System.out.printf("%-4s", "Z");
                            break;
                        default:
                            System.out.printf("%-4d", array3D[i][j][k]);
                            break;
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}