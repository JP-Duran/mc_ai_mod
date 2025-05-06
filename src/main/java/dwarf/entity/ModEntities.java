package dwarf.entity;

import dwarf.DwarfMod;
import dwarf.entity.custom.DwarfEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final Identifier DWARF_ID = Identifier.of(DwarfMod.MOD_ID, "dwarf");

    public static final EntityType<DwarfEntity> DWARF = Registry.register(
            Registries.ENTITY_TYPE,
            DWARF_ID,
            EntityType.Builder.create(DwarfEntity::new, SpawnGroup.CREATURE)
                    .dimensions(.6f, .8f) // This changes the hitbox
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, DWARF_ID)));

    public static void registerModEntities() {
        DwarfMod.LOGGER.info("Registering Dwarf Entity for" + DwarfMod.MOD_ID);
    }
}
