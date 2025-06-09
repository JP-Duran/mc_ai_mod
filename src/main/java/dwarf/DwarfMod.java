package dwarf;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dwarf.entity.ModEntities;
import dwarf.entity.custom.DwarfEntity;
import dwarf.entity.custom.DwarfEvaluationManager;
import dwarf.entity.custom.EvaluationTask;
import dwarf.item.custom.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
// Import everything in the CommandManager


public class DwarfMod implements ModInitializer {

	public static final String MOD_ID = "dwarf_mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// global variable for choosing algorithm
	public static int a_flag = 1;

	public static void nearest_neighbor() {
		a_flag = 1;
	}
	public static void  two_opt() {
		a_flag = 2;
	}
	public static void  greedy_flood_fill() {
		a_flag = 3;
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

		// Register a tick handler for the dwarf evaluation tasks
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world instanceof ServerWorld serverWorld) {
				DwarfEvaluationManager.tick();
			}
		});

		// UI code starting here this is registering the dwarf command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("dwarf")
						.then(argument("path", StringArgumentType.string())
								.executes(context -> executeDwarf(StringArgumentType.getString(context, "path"), context)))

						// /dwarf evaluate <iterations>
						.then(literal("evaluate")
								.then(argument("iterations", IntegerArgumentType.integer(1)) // Ensure there is at least one iteration
										.executes(context -> {
											// Get number of iterations from input
											int iterations = IntegerArgumentType.getInteger(context, "iterations");

											// Get the world and player
											ServerWorld world = context.getSource().getWorld();
											ServerPlayerEntity player = context.getSource().getPlayer();

											// Queue evaluation tasks with random positions
											for (int i = 0; i < iterations; i++) {
												BlockPos pos = EvaluationTask.getRandCoordinates();
												DwarfEvaluationManager.addTask(new EvaluationTask(pos, player, world));
											}
											context.getSource().sendFeedback(() ->
													Text.literal("Evaluating Dwarf " + iterations + " times"), false);
											return 1;
										})
								)
						)
				)
		);

	}
	// This code actually performs the command actions
	public static int executeDwarf(String path, CommandContext<ServerCommandSource> context) {
		// Algorithms will be called here
		if (path.equals("nearestneighbor")) {
			nearest_neighbor();
			context.getSource().sendFeedback(() -> Text.literal("Using Nearest Neighbor TSP Algorithm. Flag Value is %s".formatted(a_flag)), false);
		}
		if (path.equals("2opt")) {
            two_opt();
			context.getSource().sendFeedback(() -> Text.literal("Using 2-opt TSP Optimization. Flag Value is %s".formatted(a_flag)), false);
		}
		if (path.equals("greedyfloodfill")) {
			greedy_flood_fill();
			context.getSource().sendFeedback(() -> Text.literal("Using Greedy Flood Fill. Flag Value is %s".formatted(a_flag)), false);
		}

		return 1;
	}

}

