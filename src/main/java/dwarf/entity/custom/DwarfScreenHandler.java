package dwarf.entity.custom;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class DwarfScreenHandler extends GenericContainerScreenHandler {

    // Constructor for the screen handler
    // Gets called when the dwarf inventory is opened
    public DwarfScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, inventory, 3);
    }
}

