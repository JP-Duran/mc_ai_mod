package dwarf.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class FindDiamond extends Goal{
    private final DwarfEntity dwarf;
    private final int scanRadius = 16;
    private BlockPos targetDiamond = null;


    public FindDiamond(DwarfEntity dwarf){
        this.dwarf = dwarf;
    }

    @Override
    public boolean canStart() {
        return true;
        //return !dwarf.getNavigation().isFollowingPath(); // Can only start if its not already walking somewhere
    }

    // Added so the dwarf starts finding a diamond if it is just sitting idle
    @Override
    public void tick() {
        if (targetDiamond == null) {
            start();
            return;
        }

        if (dwarf.getBlockPos().isWithinDistance(targetDiamond, 2)) {
            // Reached the diamond
//            dwarf.getWorld().getPlayers().forEach(player -> {
//                player.sendMessage(Text.literal("Dwarf reached the diamond!"), false);
//            });
            targetDiamond = null;
            return;
        }

        if (isPathBlocked(dwarf.getBlockPos(), targetDiamond)) {
            System.out.println("Path blocked. Mining next block...");
//            dwarf.getWorld().getPlayers().forEach(player -> {
//                player.sendMessage(Text.literal("Block in the way... mining"), false);
//            });
            breakBlock(dwarf.getBlockPos(), targetDiamond);
        } else {
            // Move to the diamond
            System.out.println("Walking...");

            dwarf.getNavigation().startMovingTo(
                    targetDiamond.getX(), targetDiamond.getY(), targetDiamond.getZ(), 1.0D);
        }
    }


    @Override
    public void start() {
        BlockPos pos = dwarf.getBlockPos();
        World world = dwarf.getWorld();

        BlockPos closestDiamond = null;
        double closestDistance = Double.MAX_VALUE;

        for (BlockPos blockPosition : BlockPos.iterateOutwards(pos, scanRadius, scanRadius, scanRadius)) {
            if ((world.getBlockState(blockPosition).isOf(Blocks.DIAMOND_ORE)) ||
                    (world.getBlockState(blockPosition).isOf(Blocks.DEEPSLATE_DIAMOND_ORE)) &&
                            pos.getSquaredDistance(blockPosition) < closestDistance) {

                closestDiamond = blockPosition.toImmutable();
                closestDistance = pos.getSquaredDistance(blockPosition);
            }
        }

        if (closestDiamond != null) {
            targetDiamond = closestDiamond;
//            dwarf.getWorld().getPlayers().forEach(player -> {
//                player.sendMessage(Text.literal("Dwarf found diamond at: " +
//                        targetDiamond.getX() + ", " +
//                        targetDiamond.getY() + ", " +
//                        targetDiamond.getZ()), false);
//            });
        }
    }


    private boolean isPathBlocked(BlockPos start, BlockPos end){
        World world = dwarf.getWorld();

        Vec3i direction = end.subtract(start);
        int x = Integer.signum(direction.getX());
        int y = Integer.signum(direction.getY());
        int z = Integer.signum(direction.getZ());
        BlockPos next = start.add(x, y, z);

        boolean blocked = world.getBlockState(next).isSolidBlock(world, next);

        System.out.println("Checking path from " + start.toShortString() + " to " + end.toShortString() + ". Next block: " + next.toShortString() + ". Blocked: " + blocked);

        return blocked;
    }

    private void breakBlock(BlockPos start, BlockPos end){
        System.out.println("Inside break block");
        World world = dwarf.getWorld();
        Vec3i direction = end.subtract(start);
        int x = Integer.signum(direction.getX());
        int y = Integer.signum(direction.getY());
        int z = Integer.signum(direction.getZ());
        BlockPos next = start.add(x, y, z);
        if(!world.isAir(next)){
            world.breakBlock(next, true, dwarf);
        }
        next = next.add(0, 1, 0);
        // Check the block above it
        if(!world.isAir(next)){
            world.breakBlock(next, true, dwarf);
        }
    }
}


