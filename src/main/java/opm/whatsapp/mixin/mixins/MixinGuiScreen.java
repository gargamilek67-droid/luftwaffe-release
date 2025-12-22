package opm.whatsapp.mixin.mixins;

import opm.whatsapp.WhatsApp;
import opm.whatsapp.features.modules.render.Background;
import opm.whatsapp.api.util.ColorUtil;
import opm.whatsapp.api.util.RenderUtil;
import opm.whatsapp.features.modules.misc.ToolTips;
import opm.whatsapp.api.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiScreen.class})
public class MixinGuiScreen
        extends Gui {
    @Inject(method = {"renderToolTip"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        if (ToolTips.getInstance().isOn() && stack.getItem() instanceof ItemShulkerBox) {
            ToolTips.getInstance().renderShulkerToolTip(stack, x, y, null);
            info.cancel();
        }
    }

    @Inject(method = {"drawDefaultBackground"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void drawDefaultBackgroundHook(CallbackInfo info) {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (Background.getINSTANCE().isOn() && mc != null && mc.world != null) {
                if (Background.getINSTANCE().gradient.getValue()) {
                    // Use safe fallbacks if textManager is not initialized
                    int width = (WhatsApp.textManager != null && WhatsApp.textManager.scaledWidth > 0) ? WhatsApp.textManager.scaledWidth : mc.displayWidth;
                    int height = (WhatsApp.textManager != null && WhatsApp.textManager.scaledHeight > 0) ? WhatsApp.textManager.scaledHeight : mc.displayHeight;
                    RenderUtil.drawGradientRect(0, 0, width, height + 1, ColorUtil.toRGBA(Background.getINSTANCE().red.getValue(), Background.getINSTANCE().green.getValue(), Background.getINSTANCE().blue.getValue(), Background.getINSTANCE().alpha.getValue()), ColorUtil.toRGBA(Background.getINSTANCE().red2.getValue(), Background.getINSTANCE().green2.getValue(), Background.getINSTANCE().blue2.getValue(), Background.getINSTANCE().alpha2.getValue()), true);
                }
                if (!Background.getINSTANCE().vanilla.getValue()) info.cancel();
            }
        } catch (Exception e) {
            // Silently fail if there's any issue to prevent crashes
        }
    }
}
