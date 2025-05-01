package dwarf.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class DwarfModel extends EntityModel<EntityRenderState> {

    public static final EntityModelLayer CUBE_DWARF_LAYER = new EntityModelLayer(Identifier.of("dwarf", "cube_dwarf"), "main");
    private final ModelPart bb_main;

    public DwarfModel(ModelPart root) {
        super(root); // Call the superclass constructor
        this.bb_main = root.getChild("bb_main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("bb_main",
                ModelPartBuilder.create()
                        .cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }


}