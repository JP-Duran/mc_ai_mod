package dwarf.entity.custom.findOres;

import dwarf.entity.custom.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class FindEmerald extends FindOre {

    public FindEmerald(DwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    protected boolean isTargetOre(Block block) {
        return block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE;
    }

    @Override
    protected String getOreName() {
        return "emerald";
    }
}