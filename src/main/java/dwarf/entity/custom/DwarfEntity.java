package dwarf.entity.custom;

import dwarf.entity.custom.findOres.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.ai.goal.Goal;


import java.util.List;
import java.util.Set;

public class DwarfEntity extends MerchantEntity {

    private final SimpleInventory inventory = new SimpleInventory(27); // 27 is the size of a chest

    private List<BlockPos> currentPath = null;
    private int pathIndex = 0;
    private int jumpTimer = -1;
    private int stuckTimer = 5;
    double prevX = this.getX();
    double prevY = this.getY();
    double prevZ = this.getZ();

    boolean hasComputedPath = false;

    //List of items it will try to pickup, add new items here if you want to add/remove one
    private static final Set<Item> pickUpItemsList = Set.of(
            Items.DIAMOND,
            Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE,
            Items.RAW_IRON, Items.RAW_COPPER, Items.RAW_GOLD,
            Items.IRON_ORE, Items.GOLD_ORE, Items.COPPER_ORE,
            Items.COAL, Items.REDSTONE, Items.LAPIS_LAZULI,
            Items.EMERALD, Items.TORCH
    );

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    protected void fillRecipes() {

    }

    public DwarfEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void afterUsing(TradeOffer offer) {

    }

    @Override
    protected void initGoals() {
        //wander lowest priority so when it cant find anything it does it, then diamonds, etc
        //this.goalSelector.add(0, new WanderAroundFarGoal(this, 1.0D)); // UNCOMMENT TO ADD BACK WANDERING
        this.goalSelector.add(5, new FindDiamond(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            for (ItemEntity itemEntity : this.getWorld().getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(2.0), i -> true)) {
                ItemStack stack = itemEntity.getStack();
                if (pickUpItemsList.contains(stack.getItem())) {
                    if (this.inventory.canInsert(stack)) {
                        this.inventory.addStack(stack.copy());
                        itemEntity.discard(); // Remove from world
                        break; // Only pick up one item per tick
                    }
                }
            }

            // Path Following Logic
            if (currentPath != null && pathIndex < currentPath.size()) {

                if (!this.hasComputedPath) {
                    this.hasComputedPath = true;
                    System.out.println("Dwarf finished computing path!");
                }

                BlockPos target = currentPath.get(pathIndex);
                Vec3d targetCenter = new Vec3d(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
                double dx = targetCenter.x - this.getX();
                double dy = targetCenter.y - this.getY();
                double dz = targetCenter.z - this.getZ();
                double distanceSq = this.getPos().squaredDistanceTo(targetCenter);

                // Bridging logic for walking over gaps
                BlockPos supportPos = target.down();
                boolean needsBridge = this.getWorld().isAir(supportPos)
                        && this.getWorld().isAir(supportPos.down())
                        && !currentPath.contains(supportPos);
                if (needsBridge) {
                    this.getWorld().setBlockState(supportPos, Blocks.COBBLESTONE.getDefaultState());
                }

                // Stuck detection, wait 5 ticks, if position hasn't changed, clear path and find new one
                if (this.getX() == prevX && this.getY() == prevY && this.getZ() == prevZ) {
                    stuckTimer--;
                    if (stuckTimer <= 0) {
                        System.out.println("Dwarf is stuck!");
                        FindOre.resetPath(this);
                        stuckTimer = 5;
                    }
                } else {
                    prevX = this.getX();
                    prevY = this.getY();
                    prevZ = this.getZ();
                    stuckTimer = 5;
                }

                //Reached current path node
                if (distanceSq < 0.5) {
                    System.out.println("Reached: " + currentPath.get(pathIndex));

                    // Break the next block in the path if it's not air
                    if (pathIndex + 1 < currentPath.size()) {
                        BlockPos nextTarget = currentPath.get(pathIndex + 1);
                        System.out.println("Breaking next path block: " + nextTarget);
                        this.getWorld().breakBlock(nextTarget, true, this);
                    }

                    pathIndex++;
                }else {
                    double speed = 0.2;

                    // Set horizontal velocity
                    this.setVelocity(dx * speed, this.getVelocity().y, dz * speed);
                    this.velocityDirty = true;

                    // Determine front block
                    int stepX = (int) Math.signum(dx);
                    int stepZ = (int) Math.signum(dz);
                    BlockPos front = this.getBlockPos().add(stepX, 0, stepZ);
                    BlockPos aboveFront = front.up();

                    boolean blockInFront = !this.getWorld().isAir(front);
                    boolean airAboveFront = this.getWorld().isAir(aboveFront);

                    // Jump if there's a block in front
                    if (this.isOnGround() && blockInFront && airAboveFront) {
                        this.jump();
                    }

                    // Tower up if needed
                    if (dy > 0.5 && Math.abs(dx) < 1 && Math.abs(dz) < 1 && this.isOnGround()) {
                        BlockPos above = this.getBlockPos().up();

                        if (!this.getWorld().isAir(above)) {
                            this.getWorld().breakBlock(above, true, this);
                        }
                        this.jump();
                        this.jumpTimer = 6;
                    }

                    if (jumpTimer > 0) jumpTimer--;

                    if (this.jumpTimer == 0 && !this.isOnGround()) {
                        this.getWorld().setBlockState(this.getBlockPos().down(), Blocks.COBBLESTONE.getDefaultState());
                        this.jumpTimer = -1;
                    }
                }

                FindOre.placeTorch(this);
            }
            // Reached end of path, reset the path
            if (currentPath != null && pathIndex >= currentPath.size()) {
                System.out.println("Completed path, resetting");
                FindOre.resetPath(this);
            }
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 500)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.ATTACK_DAMAGE, 1);
    }

    public void setPath(List<BlockPos> path) {
        System.out.println("Setting path inside DwarfEntity");
        this.currentPath = path;
        this.pathIndex = 0;
        this.hasComputedPath = false;
    }

    // Called when the player right-clicks the dwarf
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient()) {
            player.openHandledScreen(new DwarfScreenHandlerFactory(this));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

}