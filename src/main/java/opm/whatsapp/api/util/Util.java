package opm.whatsapp.api.util;

import net.minecraft.client.Minecraft;

public interface Util {
    // Get Minecraft instance dynamically to avoid initialization issues
    Minecraft mc = Minecraft.getMinecraft();
}

