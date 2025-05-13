package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class FindDiamond extends FindOre {

    public FindDiamond(DwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    protected boolean isTargetOre(Block block) {
        return block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE;
    }

    @Override
    protected String getOreName() {
        return "diamond";
    }
}