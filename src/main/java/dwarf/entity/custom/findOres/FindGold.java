package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class FindGold extends FindOre {

    public FindGold(DwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    protected boolean isTargetOre(Block block) {
        return block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE;
    }

    @Override
    protected String getOreName() {
        return "gold";
    }
}