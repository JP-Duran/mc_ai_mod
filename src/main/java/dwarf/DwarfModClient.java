package dwarf;

import dwarf.entity.ModEntities;
import dwarf.entity.client.DwarfRenderer;
import dwarf.entity.client.DwarfModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DwarfModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.DWARF, DwarfRenderer::new);

        /* Top line is for mantis, bottom is for the testing cube */
        //EntityModelLayerRegistry.registerModelLayer(DwarfModel.DWARF, DwarfModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DwarfModel.CUBE_DWARF_LAYER, DwarfModel::getTexturedModelData);
    }
}