package dwarf.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.entity.ai.pathing.Path;

public class FindDiamond extends Goal{
    private final DwarfEntity dwarf;
    private static final int scanRadius = 16;
    private BlockPos targetDiamond = null;

    private static final int torchLightLevel = 5;


    public FindDiamond(DwarfEntity dwarf){
        this.dwarf = dwarf;
    }

    @Override
    public boolean canStart(){
        return true;
        //return !dwarf.getNavigation().isFollowingPath(); // Can only start if its not already walking somewhere
    }

    @Override
    public void tick(){
        if(targetDiamond == null){
            start();
            return;
        }

        // If we're close enough to the target, stop
        if(dwarf.getBlockPos().isWithinDistance(targetDiamond, 2)){
            targetDiamond = null;
            return;
        }

        // Check if we need to break blocks or recalculate path
        moveToTarget(dwarf.getBlockPos(), targetDiamond);
    }


    @Override
    public void start(){
        System.out.println("Starting new diamond search");
        BlockPos pos = dwarf.getBlockPos();
        World world = dwarf.getWorld();

        BlockPos closestDiamond = null;
        double closestDistance = Double.MAX_VALUE;

        for(BlockPos blockPosition : BlockPos.iterateOutwards(pos, scanRadius, scanRadius, scanRadius)) {
            if((world.getBlockState(blockPosition).isOf(Blocks.DIAMOND_ORE)) ||
                    (world.getBlockState(blockPosition).isOf(Blocks.DEEPSLATE_DIAMOND_ORE)) &&
                            pos.getSquaredDistance(blockPosition) < closestDistance){

                closestDiamond = blockPosition.toImmutable();
                closestDistance = pos.getSquaredDistance(blockPosition);
            }
        }

        if(closestDiamond != null){
            System.out.println("Found diamond at: " + closestDiamond);
            targetDiamond = closestDiamond;
        } else{
            System.out.println("No diamonds found within radius");
        }
    }

    private void moveToTarget(BlockPos start, BlockPos end){
        Path path = dwarf.getNavigation().findPathTo(end.getX(), end.getY(), end.getZ(), 0);
        PlaceTorch();

        // First try to follow the path if we found one
        if(path != null){
            dwarf.getNavigation().startMovingAlong(path, 1.0D);
        }
        
        // If we're not following a path or stuck then break blocks
        if(!dwarf.getNavigation().isFollowingPath()){
            World world = dwarf.getWorld();
            Vec3i direction = end.subtract(start);
            int x = Integer.signum(direction.getX());
            int y = Integer.signum(direction.getY());
            int z = Integer.signum(direction.getZ());

            BlockPos faceLevelPos = start.add(x, y+1, z);
            BlockPos footLevelPos = start.add(x, y, z);

            // If there are blocks in the way, break them
            if(!world.isAir(faceLevelPos) || !world.isAir(footLevelPos)){
                if(!world.isAir(faceLevelPos)){
                    world.breakBlock(faceLevelPos, true, dwarf);
                }
                if(!world.isAir(footLevelPos)){
                    world.breakBlock(footLevelPos, true, dwarf);
                }

                // Try to move after breaking blocks
                dwarf.getNavigation().startMovingTo(end.getX(), end.getY(), end.getZ(), 1.0D);
            }
        }
    }

    private void PlaceTorch(){
        World world = dwarf.getWorld();
        BlockPos pos = dwarf.getBlockPos();

        SimpleInventory inventory = dwarf.getInventory();
        int torchSlot = -1;
        // Loop through inventory to find torch
        for(int i = 0; i < inventory.size(); i++){
            if (inventory.getStack(i).isOf(Items.TORCH)){
                torchSlot = i;
                break;
            }
        }

        if(torchSlot == -1){
            return;
        }
        System.out.println("Light level: " + world.getLightLevel(pos));
        if(world.getLightLevel(pos) <= torchLightLevel){
            if(world.isAir(pos)){
                world.setBlockState(pos, Blocks.TORCH.getDefaultState());
                inventory.getStack(torchSlot).decrement(1);
            }
        }

    }
}