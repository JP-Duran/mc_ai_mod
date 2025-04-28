package dwarf.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FindDiamond extends Goal{
    private final DwarfEntity dwarf;
    private final int scanRadius = 16;

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
        if (dwarf.getNavigation().isIdle()) {
            start();
        }
    }

    @Override
    public void start(){
        BlockPos pos = dwarf.getBlockPos();
        World world = dwarf.getWorld();

        BlockPos closestDiamond = null;
        double closestDistance = Double.MAX_VALUE;

        // Scans around the dwarf in a cube shaped radius of 16
        for(BlockPos blockPosition : BlockPos.iterateOutwards(pos, scanRadius, scanRadius, scanRadius)){
            // Checks if the block is a diamond ore and if it is closer than the closest found diamond
            if((world.getBlockState(blockPosition).isOf(Blocks.DIAMOND_ORE)) || (world.getBlockState(blockPosition).isOf(Blocks.DEEPSLATE_DIAMOND_ORE))
                    && pos.getSquaredDistance(blockPosition) < closestDistance){
                closestDiamond = blockPosition.toImmutable();
                closestDistance = pos.getSquaredDistance(blockPosition);
            }
        }

        // If a diamond is found, go to it
        if(closestDiamond != null){
            final BlockPos diamondPos = closestDiamond;
            // Print out the coordinates to teh chat
            dwarf.getWorld().getPlayers().forEach(player -> {
                player.sendMessage(Text.literal("Dwarf found diamond at: " + diamondPos.getX() + ", " + diamondPos.getY() + ", " + diamondPos.getZ()), false);
            });

            // Move to the diamond
            dwarf.getNavigation().startMovingTo(closestDiamond.getX(),
                    closestDiamond.getY(),
                    closestDiamond.getZ(),
                    1.0D);
        }
    }
}
