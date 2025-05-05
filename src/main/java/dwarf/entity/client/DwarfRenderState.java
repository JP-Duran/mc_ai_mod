package dwarf.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)

public class DwarfRenderState extends LivingEntityRenderState {
    public final AnimationState idle;
    public final AnimationState walk;
    public final AnimationState run;

    public DwarfRenderState() {
        this.idle = new AnimationState();
        this.walk = new AnimationState();
        this.run = new AnimationState();
    }
}
