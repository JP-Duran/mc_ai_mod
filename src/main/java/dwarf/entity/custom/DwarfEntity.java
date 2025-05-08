package dwarf.entity.custom;

import dwarf.entity.ModEntities;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.inventory.SimpleInventory;

public class DwarfEntity extends MerchantEntity {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private final SimpleInventory inventory = new SimpleInventory(27); // 27 is the size of a chest

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Nullable
    private TradeOfferList offers;

    public DwarfEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new DwarfNavigation(this, world);
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
    protected void initGoals() {
        this.goalSelector.add(0, new WanderAroundFarGoal(this, 1.0D)); // UNCOMMENT TO ADD BACK WANDERING
        this.goalSelector.add(1, new FindDiamond(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            // Force update animation states every tick
            updateAnimationStates();
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