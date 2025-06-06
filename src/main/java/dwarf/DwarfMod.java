package dwarf;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dwarf.entity.ModEntities;
import dwarf.entity.custom.DwarfEntity;
import dwarf.item.custom.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
// word()
import static com.mojang.brigadier.arguments.StringArgumentType.word;
// literal("foo")
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything in the CommandManager
import static net.minecraft.server.command.CommandManager.*;



public class DwarfMod implements ModInitializer {

	public static final String MOD_ID = "dwarf_mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// global variable for choosing algorithm
	public static int a_flag = 0;

	public static void a_star() {
		a_flag = 1;
	}
	public static void  tsp() {
		a_flag = 2;
	}
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello dwarf_mod!");

		ModEntities.registerModEntities();

		FabricDefaultAttributeRegistry.register(ModEntities.DWARF, DwarfEntity.createAttributes());
		ModItems.registerModItems();
		// UI code starting here this is registering the dwarf command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("dwarf")
				.then(argument("path", StringArgumentType.string())
						.executes(context -> executeDwarf(StringArgumentType.getString(context, "path"), context)))));
	}
	//this code actually performs the command actions
	public static int executeDwarf(String path, CommandContext<ServerCommandSource> context) {
		//algorithms will be called here
		if (path.equals("astar")) {
			a_star();
			context.getSource().sendFeedback(() -> Text.literal("Running AStar on dwarf. Flag Value is %s".formatted(a_flag)), false);

			//call astar algorithm here
		}
		if (path.equals("tsp")) {
            tsp();
			context.getSource().sendFeedback(() -> Text.literal("Running TSP on dwarf. Flag Value is %s".formatted(a_flag)), false);

			//call tsp algorithm here
		}


		return 1;
	}

}

