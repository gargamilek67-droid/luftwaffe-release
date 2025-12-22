package opm.whatsapp.api.manager;

import opm.whatsapp.WhatsApp;
import opm.whatsapp.features.Feature;
import opm.whatsapp.features.gui.font.CustomFont;
import opm.whatsapp.features.modules.client.FontMod;
import opm.whatsapp.api.util.Timer;
import opm.whatsapp.api.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class TextManager
        extends Feature {
    private final Timer idleTimer = new Timer();
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    private CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, false);
    private boolean idling;

    public TextManager() {
        // Don't call updateResolution() here to avoid accessing Minecraft during initialization
        // It will be called later when the TextManager is properly initialized
    }

    public void init(boolean startup) {
        // Initialize resolution now that Minecraft is available
        this.updateResolution();
        
        FontMod cFont = WhatsApp.moduleManager.getModuleByClass(FontMod.class);
        try {
            this.setFontRenderer(new Font(cFont.fontName.getValue(), cFont.fontStyle.getValue(), cFont.fontSize.getValue()), cFont.antiAlias.getValue(), cFont.fractionalMetrics.getValue());
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public void drawString(String text, float x, float y, int color, boolean shadow) {
        if (WhatsApp.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
            if (shadow) {
                this.customFont.drawStringWithShadow(text, x, y, color);
            } else {
                this.customFont.drawString(text, x, y, color);
            }
            return;
        }
        Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, shadow);
    }

    public float centerText(String govno) {
        float x = this.renderer.scaledWidth - (float) this.renderer.getStringWidth(govno) / 2;
        return x;
    }

    public int getStringWidth(String text) {
        if (WhatsApp.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
            return this.customFont.getStringWidth(text);
        }
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        if (WhatsApp.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
            String text = "A";
            return this.customFont.getStringHeight(text);
        }
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public Font getCurrentFont() {
        return this.customFont.getFont();
    }

    public void updateResolution() {
        try {
            // Use LWJGL Display class to get display dimensions in MC 1.12.2
            this.scaledWidth = org.lwjgl.opengl.Display.getWidth();
            this.scaledHeight = org.lwjgl.opengl.Display.getHeight();
            this.scaleFactor = 1;
            
            // Get Minecraft instance directly
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null || mc.gameSettings == null) {
                // Fallback values if Minecraft isn't ready
                return;
            }
            
            boolean flag = mc.isUnicode();
            int i = mc.gameSettings.guiScale;
            if (i == 0) {
                i = 1000;
            }
            while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
                ++this.scaleFactor;
            }
            if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
                --this.scaleFactor;
            }
            double scaledWidthD = this.scaledWidth / this.scaleFactor;
            double scaledHeightD = this.scaledHeight / this.scaleFactor;
            this.scaledWidth = MathHelper.ceil(scaledWidthD);
            this.scaledHeight = MathHelper.ceil(scaledHeightD);
        } catch (Exception e) {
            // Fallback to basic values if there's any issue
            this.scaledWidth = 854;
            this.scaledHeight = 480;
            this.scaleFactor = 1;
        }
    }

    public String getIdleSign() {
        if (this.idleTimer.passedMs(500L)) {
            this.idling = !this.idling;
            this.idleTimer.reset();
        }
        if (this.idling) {
            return "_";
        }
        return "";
    }
}

