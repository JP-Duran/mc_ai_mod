package dwarf.entity.custom;

import dwarf.entity.ModEntities;
import dwarf.entity.custom.findOres.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
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
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.inventory.SimpleInventory;

import java.util.List;
import java.util.Set;

public class DwarfEntity extends MerchantEntity {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private final SimpleInventory inventory = new SimpleInventory(27); // 27 is the size of a chest

    private List<BlockPos> currentPath = null;
    private int pathIndex = 0;

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

    @Nullable
    private TradeOfferList offers;

    public DwarfEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void afterUsing(TradeOffer offer) {
        // Empty implementation
    }

    @Override
    protected void fillRecipes() {
        // Empty implementation
    }

    @Override
    protected void initGoals() { //wander lowest priority so when it cant find anything it does it, then diamonds, etc
        //this.goalSelector.add(0, new WanderAroundFarGoal(this, 1.0D)); // UNCOMMENT TO ADD BACK WANDERING
        this.goalSelector.add(5, new FindDiamond(this));
        //this.goalSelector.add(4, new FindEmerald(this));
        //this.goalSelector.add(4, new FindGold(this));
        //this.goalSelector.add(3, new FindLapis(this));
        //this.goalSelector.add(2, new FindIron(this));
        //this.goalSelector.add(1, new FindCoal(this));
        //this.goalSelector.add(1, new FindCopper(this));
        //this.goalSelector.add(1, new FindRedstone(this));
    }



    @Override
    public void tick() {
        super.tick();

        //not client check, as item pickup is server side
        if (!this.getWorld().isClient()) { //the 2 in the expand below is the area in which it can "see" items to pickup
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
        }
//        if (this.getWorld().isClient()) {
//            // Force update animation states every tick
//            updateAnimationStates(); //animations may not be working, but I've left the remains of the attempts in case
//            //someone else wants to give it a shot
//        }
        if (!this.getWorld().isClient() && currentPath != null && pathIndex < currentPath.size()) {

            BlockPos target = currentPath.get(pathIndex);
            double dx = target.getX() + 0.5 - this.getX();
            double dy = target.getY() - this.getY();
            double dz = target.getZ() + 0.5 - this.getZ();

            double distanceSq = dx * dx + dy * dy + dz * dz;

            // Stop the dwarf if it is within two blocks of the ore
            // We can change this later
            if (distanceSq < 2) {
                // Arrived at target block, mine it this doesn't work for some reason
                if (!this.getWorld().isAir(target)) {
                    this.getWorld().breakBlock(target, true, this);
                }
                pathIndex++;
            } else {
                double speed = 0.3;

                // Set horizontal velocity, don't change vertical
                this.setVelocity(dx * speed, this.getVelocity().y, dz * speed);
                this.velocityDirty = true; // We changed the velocity so this tells teh game to update it

                // Check if block in front is solid and dwarf is on the ground
                int stepX = (int) Math.signum(dx);
                int stepZ = (int) Math.signum(dz);
                BlockPos front = this.getBlockPos().add(stepX, 0, stepZ);
                BlockPos aboveFront = front.up();

                boolean blockInFront = !this.getWorld().isAir(front);
                boolean airAboveFront = this.getWorld().isAir(aboveFront);

                // This is responsible for the dwarf towering up
                if (dy > 0.5 && Math.abs(dx) < 1 && Math.abs(dz) < 1 && this.isOnGround()) {
                    this.jump();
                    this.getWorld().setBlockState(this.getBlockPos(), Blocks.COBBLESTONE.getDefaultState());
                }

                if (this.isOnGround() && blockInFront && airAboveFront) {
                    this.jump(); // Use the built in minecraft jump physics
                }
            }
        }

    }

    private void updateAnimationStates() {
        // Check if the entity is moving
        boolean isMoving = this.getVelocity().horizontalLengthSquared() > 0.0025D;

        if (isMoving) {
            // Check speed to determine walk vs run
            float speed = (float) this.getVelocity().horizontalLength();

            if (speed > 0.15f) {
                // Running - start run animation, stop others
                this.walkAnimationState.stop();
                this.idleAnimationState.stop();
                this.runAnimationState.startIfNotRunning(this.age);
            } else {
                // Walking - start walk animation, stop others
                this.runAnimationState.stop();
                this.idleAnimationState.stop();
                this.walkAnimationState.startIfNotRunning(this.age);
            }
        } else {
            // Not moving - idle animation
            this.runAnimationState.stop();
            this.walkAnimationState.stop();

            // IMPORTANT CHANGE: Always start idle animation when not moving
            this.idleAnimationState.startIfNotRunning(this.age);
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 18)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.ATTACK_DAMAGE, 1);
    }

    public void setPath(List<BlockPos> path) {
        this.currentPath = path;
        this.pathIndex = 0;
    }

    public List<BlockPos> getCurrentPath() {
        return this.currentPath;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.DWARF.create(world, SpawnReason.BREEDING);
    }

    // Called when the player right clicks the dwarf
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient()) {
            player.openHandledScreen(new DwarfScreenHandlerFactory(this));
        }
        return ActionResult.SUCCESS;
    }
}