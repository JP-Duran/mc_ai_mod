package dwarf.entity.custom;

import dwarf.entity.ModEntities;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Random;

// An evaluation task is spawning a dwarf in at a random location,
// waiting for it to compute the paths to the diamonds
// killing it so the next evaluation task can start

public class EvaluationTask {

    // Where the dwarf will spawn
    private final BlockPos pos;
    private final ServerPlayerEntity player;
    private final ServerWorld world;
    private DwarfEntity dwarf;
    // Flag indicating whether the dwarf has been spawned and initialization done
    private boolean started = false;

    // Ranges dwarf cna spawn in
    static int xRange = 500;
    static int zRange = 500;
    static int yMin = -40;
    static int yMax = 0;

    public EvaluationTask(BlockPos pos, ServerPlayerEntity player, ServerWorld world) {
        this.pos = pos;
        this.player = player;
        this.world = world;
    }

    public void tick() {

        if (!started) {
            // Wait until the chunk is loaded before spawning dwarf
            if (!world.isChunkLoaded(pos)) {
                System.out.println("Waiting on chunk to load");
                return;
            }

            // Clear blocks where dwarf is spawning so it doesn't suffocate
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            world.setBlockState(pos.down(), Blocks.COBBLESTONE.getDefaultState());

            // Spawn dwarf
            dwarf = new DwarfEntity(ModEntities.DWARF, world);
            dwarf.refreshPositionAndAngles(pos, 0.0F, 0.0F);
            boolean success = world.spawnEntity(dwarf);
            System.out.println("Spawn success? " + success);

            started = true;
        }
    }

    public void start() {
        System.out.println("Going to from start: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());

        // Teleport player to evaluation location so chunk can load in
        player.networkHandler.requestTeleport(
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                0.0F,
                0.0F
        );

    }

    // Checks if the evaluation task is complete
    // Teh task is complete if the dwarf has computed the path to all the diamonds
    public boolean isComplete() {
        if (dwarf != null && dwarf.hasComputedPath) {
            System.out.println("Dwarf is done computing, killing it");

            // Kill the dwarf
            dwarf.discard();

            return true;
        }

        return false;
    }

    // Function to generate a random location between set boundaries the dwarf can spawn in
    public static BlockPos getRandCoordinates(){
        Random rand = new Random();

        int x = rand.nextInt(xRange * 2) - xRange;
        int z = rand.nextInt(zRange * 2) - zRange;
        int y = rand.nextInt(yMin, yMax);

        System.out.println("Going to: " + x + " " + y + " " + z);
        return new BlockPos(x, y, z);
    }
}

