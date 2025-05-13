package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class FindLapis extends FindOre {

    public FindLapis(DwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    protected boolean isTargetOre(Block block) {
        return block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE;
    }

    @Override
    protected String getOreName() {
        return "lapis";
    }
}