package dwarf.entity.custom;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dwarf.entity.custom.DwarfEntity;

public class DwarfNavigation extends MobNavigation {
    public DwarfNavigation(DwarfEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    public Path findPathTo(BlockPos target, int distance) {
        return null;
    }

}