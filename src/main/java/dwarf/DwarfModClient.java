package dwarf;

import dwarf.entity.ModEntities;
import dwarf.entity.client.DwarfModel;
import dwarf.entity.client.DwarfRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DwarfModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(DwarfModel.DWARF, DwarfModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.DWARF, DwarfRenderer::new);
    }
}
