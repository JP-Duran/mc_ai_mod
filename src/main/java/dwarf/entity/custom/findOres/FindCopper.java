package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class FindCopper extends FindOre {

    public FindCopper(DwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    protected boolean isTargetOre(Block block) {
        return block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE;
    }

    @Override
    protected String getOreName() {
        return "copper";
    }
}