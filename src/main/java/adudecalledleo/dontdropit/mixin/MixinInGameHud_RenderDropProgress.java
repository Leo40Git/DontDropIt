package adudecalledleo.dontdropit.mixin;

import adudecalledleo.dontdropit.DropHandler;
import adudecalledleo.dontdropit.config.ModConfigHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud_RenderDropProgress extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderHotbarItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V",
            shift = At.Shift.AFTER))
    public void dontdropit$renderDropProgress(int i, int j, float f, PlayerEntity playerEntity, ItemStack itemStack, CallbackInfo ci) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0, 0, getBlitOffset());
        RenderSystem.enableBlend();
        DropHandler.renderSlotFavoriteIcon(itemStack, i, j, 16, 16);
        RenderSystem.disableBlend();
        if (!ModConfigHolder.getConfig().dropDelay.enabled) {
            RenderSystem.enableDepthTest();
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.popMatrix();
            return;
        }
        if (client.options.keyDrop.isPressed()) {
            if (itemStack == playerEntity.inventory.getMainHandStack())
                DropHandler.renderSlotProgressOverlay(itemStack, i, j, 16, 16);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.popMatrix();

    }
}