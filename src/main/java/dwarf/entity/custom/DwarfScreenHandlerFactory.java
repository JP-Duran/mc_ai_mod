package dwarf.entity.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class DwarfScreenHandlerFactory implements NamedScreenHandlerFactory {
    private final DwarfEntity dwarf;

    public DwarfScreenHandlerFactory(DwarfEntity dwarf) {
        this.dwarf = dwarf;
    }

    // When you open the dwarf's inventory this is called
    // It creates the screen for the inventory and make sure it's tied to the dwarf's actual stored items
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DwarfScreenHandler(syncId, inv, dwarf.getInventory());
    }

    // To rename the screen handler
    @Override
    public Text getDisplayName() {
        return Text.of("Dwarf Inventory");
    }
}
