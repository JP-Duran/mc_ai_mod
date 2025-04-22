package dwarf.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)

public class DwarfRenderState extends LivingEntityRenderState {
    public final AnimationState idlingAnimationState;
    public final AnimationState walkingAnimationState;

    public DwarfRenderState() {
        this.idlingAnimationState = new AnimationState();
        this.walkingAnimationState = new AnimationState();
    }
}
