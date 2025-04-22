package dwarf.item.custom;

import dwarf.DwarfMod;
import dwarf.entity.ModEntities;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    private static final Identifier id = Identifier.of("dwarf_mod", "dwarf_spawn_egg");
    private static final RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);

    public static final Item DWARF_SPAWN_EGG = registerItem("dwarf_spawn_egg",
            new SpawnEggItem(ModEntities.DWARF, new Item.Settings().useBlockPrefixedTranslationKey().registryKey(key)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(DwarfMod.MOD_ID, name), item);
    }

    public static void addItemsToItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(DWARF_SPAWN_EGG);
        });
    }

    public static void registerModItems() {
        addItemsToItemGroups();
    }
}