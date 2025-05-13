package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class FindRedstone extends FindOre {

    public FindRedstone(DwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    protected boolean isTargetOre(Block block) {
        return block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE;
    }

    @Override
    protected String getOreName() {
        return "redstone";
    }
}