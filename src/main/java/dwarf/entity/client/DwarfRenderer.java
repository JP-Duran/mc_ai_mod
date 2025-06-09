package dwarf.entity.client;

import dwarf.DwarfMod;
import dwarf.entity.custom.DwarfEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DwarfRenderer extends MobEntityRenderer<DwarfEntity, DwarfRenderState, DwarfModel> {

    public DwarfRenderer(EntityRendererFactory.Context context) {
        super(context, new DwarfModel(context.getPart(DwarfModel.DWARF_LAYER)), 0.5f);

    }

    @Override
    public DwarfRenderState createRenderState() {
        // Create a new instance of your render state
        return new DwarfRenderState();
    }

    @Override
    public Identifier getTexture(DwarfRenderState state) {
        return Identifier.of(DwarfMod.MOD_ID, "textures/entity/dwarf/dwarftexture.png");
    }
}