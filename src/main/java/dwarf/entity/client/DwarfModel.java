package dwarf.entity.client;

import dwarf.entity.custom.DwarfEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DwarfModel extends EntityModel<EntityRenderState> {
    public static final EntityModelLayer DWARF_LAYER =
            new EntityModelLayer(Identifier.of("dwarf_mod", "dwarf"), "main");

    private final ModelPart body;
    private final ModelPart hat;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart beard;
    private final ModelPart nose;

    public DwarfModel(ModelPart root) {
        super(root);
        this.body = root.getChild("Body");
        this.hat = root.getChild("Hat");
        this.head = this.body.getChild("Head");
        this.nose = this.head.getChild("Nose");
        this.beard = this.head.getChild("beard");

        ModelPart arms = this.body.getChild("Arms");
        this.rightArm = arms.getChild("rightArm");
        this.leftArm = arms.getChild("LeftArm");

        ModelPart legs = this.body.getChild("Legs");
        this.rightLeg = legs.getChild("rightLeg");
        this.leftLeg = legs.getChild("leftLeg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild("Body",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-3.0F, -10.0F, -2.0F, 6.0F, 6.0F, 5.0F),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData arms = body.addChild("Arms", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -10.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        arms.addChild("rightArm", ModelPartBuilder.create().uv(22, 5)
                        .cuboid(3.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        arms.addChild("LeftArm", ModelPartBuilder.create().uv(22, 13)
                        .cuboid(-8.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                ModelTransform.of(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData legs = body.addChild("Legs", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        legs.addChild("rightLeg", ModelPartBuilder.create().uv(0, 21)
                        .cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 3.0F),
                ModelTransform.of(1.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        legs.addChild("leftLeg", ModelPartBuilder.create().uv(12, 21)
                        .cuboid(-4.5F, 0.0F, -1.0F, 3.0F, 4.0F, 3.0F),
                ModelTransform.of(1.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData head = body.addChild("Head", ModelPartBuilder.create()
                        .uv(0, 11).cuboid(-3.0F, -5.0F, -2.0F, 6.0F, 5.0F, 5.0F)
                        .uv(24, 21).cuboid(-3.0F, -4.0F, -3.0F, 6.0F, 1.0F, 1.0F),
                ModelTransform.of(0.0F, -10.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        head.addChild("Nose", ModelPartBuilder.create().uv(24, 23)
                        .cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F),
                ModelTransform.of(0.0F, -3.0F, -3.0F, 0.0F, 0.0F, 0.0F));

        head.addChild("beard", ModelPartBuilder.create()
                        .uv(22, 0).cuboid(-3.0F, -2.0F, -1.0F, 6.0F, 4.0F, 1.0F)
                        .uv(16, 28).cuboid(-2.0F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F)
                        .uv(20, 28).cuboid(1.0F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F)
                        .uv(0, 28).cuboid(-2.0F, 2.0F, -1.0F, 4.0F, 1.0F, 1.0F)
                        .uv(10, 28).cuboid(-1.0F, 3.0F, -1.0F, 2.0F, 1.0F, 1.0F),
                ModelTransform.of(0.0F, 2.0F, -2.0F, 0.0F, 0.0F, 0.0F));

        root.addChild("Hat", ModelPartBuilder.create()
                        .uv(22, 32).cuboid(-4.0F, -16.0F, -4.0F, 8.0F, 2.0F, 8.0F)
                        .uv(26, 44).cuboid(-3.0F, -17.0F, -3.0F, 6.0F, 1.0F, 6.0F),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }


    public void animate(dwarf.entity.client.DwarfRenderState state, float animationProgress, float headYaw, float headPitch) {
        body.resetTransform();
        head.resetTransform();
        rightArm.resetTransform();
        leftArm.resetTransform();
        rightLeg.resetTransform();
        leftLeg.resetTransform();
        beard.resetTransform();
        nose.resetTransform();

        float swingSpeed = 1.0f;
        float swingAmount = 0.5f;

        if (state.run.isRunning()) {
            swingSpeed = 2.5f;
            swingAmount = 1.0f;
        } else if (state.walk.isRunning()) {
            swingSpeed = 1.5f;
            swingAmount = 0.7f;
        } else if (state.idle.isRunning()) {
            swingSpeed = 0.5f;
            swingAmount = 0.15f;
        }

        float swing = animationProgress * swingSpeed;

        rightArm.pitch = MathHelper.cos(swing) * swingAmount;
        leftArm.pitch = MathHelper.cos(swing + (float) Math.PI) * swingAmount;
        rightLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * swingAmount;
        leftLeg.pitch = MathHelper.cos(swing) * swingAmount;

        head.pitch = headPitch * 0.017453292F;
        head.yaw = headYaw * 0.017453292F;
    }


    public void setAngles(EntityRenderState entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof DwarfRenderState dwarfState) {
            animate(dwarfState, animationProgress, headYaw, headPitch);
        }
    }


    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        body.render(matrices, vertexConsumer, light, overlay);
        hat.render(matrices, vertexConsumer, light, overlay);
    }
}
