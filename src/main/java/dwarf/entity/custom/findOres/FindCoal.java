package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class FindCoal extends FindOre {

    public FindCoal(DwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    protected boolean isTargetOre(Block block) {
        return block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE;
    }

    @Override
    protected String getOreName() {
        return "coal";
    }
}