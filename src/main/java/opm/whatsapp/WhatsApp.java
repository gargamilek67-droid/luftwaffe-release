package opm.whatsapp;

import me.whatsapp.BuildConstants;
import opm.whatsapp.api.manager.*;
import opm.whatsapp.api.util.discord.DiscordManager;
import opm.whatsapp.api.util.IconUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.InputStream;
import java.nio.ByteBuffer;

@Mod(modid = "whatsapp.cz", name = "whatsapp.cz", version = "4.0.7")
public class WhatsApp {
    public static final String MODID = "whatsapp.cz";
    public static final String MODNAME = "whatsapp.cz";
    public static final String MODVER = "0.0.4";
    public static final String MODNAME1 = "whatsapp";
    public static final String MODNAME2 = ".cz";
    public static final String GITHASH = BuildConstants.GIT_HASH;
    public static final String GITREVISION = BuildConstants.GIT_REVISION;
    public static final Logger LOGGER = LogManager.getLogger("whatsapp.cz");
    public static TimerManager timerManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    public static ChatManager chatManager;
    public static DiscordManager discordManager;
    @Mod.Instance
    public static WhatsApp INSTANCE;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public static void load() {
        LOGGER.info("\n\nLoading whatsapp.cz");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        
        // Initialize basic managers first
        LOGGER.info("Initializing basic managers...");
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        rotationManager = new RotationManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        holeManager = new HoleManager();
        timerManager = new TimerManager();
        chatManager = new ChatManager();
        LOGGER.info("Basic managers loaded.");
        
        // Initialize file manager and config manager
        LOGGER.info("Initializing file system...");
        try {
            fileManager = new FileManager();
            configManager = new ConfigManager();
            LOGGER.info("File system initialized.");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize file system: " + e.getMessage());
            // Continue without file system if it fails
        }
        
        // Initialize modules
        LOGGER.info("Initializing modules...");
        try {
            moduleManager = new ModuleManager();
            moduleManager.init();
            LOGGER.info("Modules loaded.");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize modules: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Initialize config and events
        LOGGER.info("Initializing configuration...");
        try {
            if (configManager != null) {
                configManager.init();
            }
            eventManager.init();
            LOGGER.info("Configuration loaded.");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize configuration: " + e.getMessage());
        }
        
        // Initialize text manager
        LOGGER.info("Initializing text manager...");
        try {
            textManager.init(true);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize text manager: " + e.getMessage());
        }
        
        // Skip Discord RPC initialization (already disabled)
        LOGGER.info("Discord RPC initialization skipped to prevent hanging");
        
        // Final module setup
        LOGGER.info("Finalizing module setup...");
        try {
            if (moduleManager != null) {
                moduleManager.onLoad();
            }
            WhatsApp.setWindowIcon();
        } catch (Exception e) {
            LOGGER.error("Failed to finalize module setup: " + e.getMessage());
        }
        
        LOGGER.info("whatsapp.cz successfully loaded!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading whatsapp.cz");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        WhatsApp.onUnload();
        eventManager = null;
        friendManager = null;
        speedManager = null;
        holeManager = null;
        positionManager = null;
        rotationManager = null;
        configManager = null;
        commandManager = null;
        colorManager = null;
        serverManager = null;
        fileManager = null;
        potionManager = null;
        inventoryManager = null;
        moduleManager = null;
        textManager = null;
        chatManager = null;
        LOGGER.info("whatsapp.cz unloaded!\n");
    }

    public static void reload() {
        WhatsApp.unload(false);
        WhatsApp.load();
    }

    public static void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (InputStream inputStream16x = Minecraft.class.getResourceAsStream("/whatsapp/assets/whatsapp-16x.png");
                 InputStream inputStream32x = Minecraft.class.getResourceAsStream("/whatsapp/assets/whatsapp-32x.png")) {
                ByteBuffer[] icons = new ByteBuffer[]{IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x)};
                Display.setIcon(icons);
            } catch (Exception e) {
                WhatsApp.LOGGER.error("Couldn't set Windows Icon", e);
            }
        }
    }

    public static void onUnload() {
        if (!unloaded) {
            if (eventManager != null) {
                eventManager.onUnload();
            }
            if (moduleManager != null) {
                moduleManager.onUnload();
            }
            if (configManager != null && configManager.config != null) {
                configManager.saveConfig(WhatsApp.configManager.config.replaceFirst("whatsapp/", ""));
            }
            if (moduleManager != null) {
                moduleManager.onUnloadPost();
            }
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("blyat");
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Initializing whatsapp...");
        Display.setTitle("whatsapp.cz");
        LOGGER.info("Starting full initialization...");
        // Full initialization to enable GUI and commands
        try {
            load();
            LOGGER.info("Full initialization complete");
        } catch (Exception e) {
            LOGGER.error("Full initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.info("whatsapp initialized successfully!");
    }

    private static void initDRPC() {
        // Temporarily disabled Discord RPC to prevent hanging
        // TODO: Re-enable when Discord RPC connection issues are resolved
        /*
        discordManager = DiscordManager.getInstance();
        discordManager.start(true);
        discordManager.setDetails("favorite dallas opp");
        discordManager.setState(MODVER + "+" + GITHASH + "+" + GITREVISION);
        discordManager.setLargeImage("whatsapp", "nazi ahhh client");
        discordManager.setSmallImage("whatsapp", "whatsapp");
        discordManager.setStartTimestampToNow();
        */
        LOGGER.info("Discord RPC initialization skipped to prevent hanging");
    }
}

