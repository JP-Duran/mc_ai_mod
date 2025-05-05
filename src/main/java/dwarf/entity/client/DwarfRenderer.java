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
    public void updateRenderState(DwarfEntity entity, DwarfRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);


        // Transfer animation states from entity to render state
        if (entity.idleAnimationState.isRunning()) {
            state.idle.start(entity.age);
        } else {
            state.idle.stop();
        }

        if (entity.walkAnimationState.isRunning()) {
            state.walk.start(entity.age);
        } else {
            state.walk.stop();
        }

        if (entity.runAnimationState.isRunning()) {
            state.run.start(entity.age);
        } else {
            state.run.stop();
        }
    }

    @Override
    public Identifier getTexture(DwarfRenderState state) {
        return Identifier.of(DwarfMod.MOD_ID, "textures/entity/dwarf/dwarftexture.png");
    }
}