package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import mx.x10.afffsdd.vanillaanticheat.VACUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommand;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConThreadMain;
import net.minecraft.network.rcon.RConThreadQuery;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CryptManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer extends MinecraftServer implements IServer
{
    private static final Logger field_155771_h = LogManager.getLogger();
    private final List pendingCommandList = Collections.synchronizedList(new ArrayList());
    private RConThreadQuery theRConThreadQuery;
    private RConThreadMain theRConThreadMain;
    private PropertyManager settings;
    private boolean canSpawnStructures;
    private WorldSettings.GameType gameType;
    private boolean guiIsEnabled;
    private static final String __OBFID = "CL_00001784";

    public DedicatedServer(File par1File)
    {
        super(par1File, Proxy.NO_PROXY);
        Thread var10001 = new Thread("Server Infinisleeper")
        {
            private static final String __OBFID = "CL_00001787";
            {
                setDaemon(true);
                start();
            }

            public void run()
            {
                while (true)
                {
                    try
                    {
                        while (true)
                        {
                            Thread.sleep(2147483647L);
                        }
                    }
                    catch (InterruptedException var2)
                    {
                        ;
                    }
                }
            }
        };
    }

    private void loadExtraSettings()
    {
        shouldLogIps();
        shouldTellIp();
        getFastbreakLeeway();
        getFastbreakRatioThreshold();
        getBuildhackThreshold();
        getFloatingTicksThreshold();
        getFlyResetLogThreshold();
        getFlyResetKickThreshold();
        useDiamondNotifications();
        getHealthRegenTickCount();
        getSpeedhackLeeway();
        getSpeedhackRatioKickThreshold();
        getSpeedLimit(false, false, false); // I'm sorry.
        getSpeedLimit(false, false, true);
        getSpeedLimit(false, true, false);
        getSpeedLimit(false, true, true);
        getSpeedLimit(true, false, false);
        getSpeedLimit(true, false, true);
        getSpeedLimit(true, true, false);
        getSpeedLimit(true, true, true);
        getSneakSpeedLimit();
        getProxyCheckMode();
        shouldCheckProxies();
        shouldKicksBeBans();
        getKickTempbanLength();
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer() throws IOException
    {
        Thread var1 = new Thread("Server console handler")
        {
            private static final String __OBFID = "CL_00001786";

            public void run()
            {
                BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));
                String var2;

                try
                {
                    while (!DedicatedServer.this.isServerStopped() && DedicatedServer.this.isServerRunning() && (var2 = var1.readLine()) != null)
                    {
                        DedicatedServer.this.addPendingCommand(var2, DedicatedServer.this);
                    }
                }
                catch (IOException var4)
                {
                    DedicatedServer.field_155771_h.error("Exception handling console input", var4);
                }
            }
        };
        var1.setDaemon(true);
        var1.start();
        field_155771_h.info("Starting minecraft server version 1.7.2");
        field_155771_h.info("This server is running VanillaAntiCheat " + VACUtils.VACVersion);

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            field_155771_h.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        field_155771_h.info("Loading properties");
        settings = new PropertyManager(new File("server.properties"));

        if (isSinglePlayer())
        {
            setHostname("127.0.0.1");
        }
        else
        {
            setOnlineMode(settings.getBooleanProperty("online-mode", true));
            setHostname(settings.getStringProperty("server-ip", ""));
        }

        setCanSpawnAnimals(settings.getBooleanProperty("spawn-animals", true));
        setCanSpawnNPCs(settings.getBooleanProperty("spawn-npcs", true));
        setAllowPvp(settings.getBooleanProperty("pvp", true));
        setAllowFlight(settings.getBooleanProperty("allow-flight", false));
        func_155759_m(settings.getStringProperty("resource-pack", ""));
        setMOTD(settings.getStringProperty("motd", "A Minecraft Server"));
        setForceGamemode(settings.getBooleanProperty("force-gamemode", false));
        func_143006_e(settings.getIntProperty("player-idle-timeout", 0));

        if (settings.getIntProperty("difficulty", 1) < 0)
        {
            settings.setProperty("difficulty", Integer.valueOf(0));
        }
        else if (settings.getIntProperty("difficulty", 1) > 3)
        {
            settings.setProperty("difficulty", Integer.valueOf(3));
        }

        canSpawnStructures = settings.getBooleanProperty("generate-structures", true);
        int var2 = settings.getIntProperty("gamemode", WorldSettings.GameType.SURVIVAL.getID());
        gameType = WorldSettings.getGameTypeById(var2);
        field_155771_h.info("Default game type: " + gameType);
        InetAddress var3 = null;

        if (getServerHostname().length() > 0)
        {
            var3 = InetAddress.getByName(getServerHostname());
        }

        if (getServerPort() < 0)
        {
            setServerPort(settings.getIntProperty("server-port", 25565));
        }

        // Force preload of all extra settings to set defaults.
        loadExtraSettings();

        field_155771_h.info("Generating keypair");
        setKeyPair(CryptManager.generateKeyPair());
        field_155771_h.info("Starting Minecraft server on " + (getServerHostname().length() == 0 ? "*" : getServerHostname()) + ":" + getServerPort());

        try
        {
            func_147137_ag().addLanEndpoint(var3, getServerPort());
        }
        catch (IOException var16)
        {
            field_155771_h.warn("**** FAILED TO BIND TO PORT!");
            field_155771_h.warn("The exception was: {}", new Object[] {var16.toString()});
            field_155771_h.warn("Perhaps a server is already running on that port?");
            return false;
        }

        if (!isServerInOnlineMode())
        {
            field_155771_h.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            field_155771_h.warn("The server will make no attempt to authenticate usernames. Beware.");
            field_155771_h.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            field_155771_h.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        setConfigurationManager(new DedicatedPlayerList(this));
        long var4 = System.nanoTime();

        if (getFolderName() == null)
        {
            setFolderName(settings.getStringProperty("level-name", "world"));
        }

        String var6 = settings.getStringProperty("level-seed", "");
        String var7 = settings.getStringProperty("level-type", "DEFAULT");
        String var8 = settings.getStringProperty("generator-settings", "");
        long var9 = (new Random()).nextLong();

        if (var6.length() > 0)
        {
            try
            {
                long var11 = Long.parseLong(var6);

                if (var11 != 0L)
                {
                    var9 = var11;
                }
            }
            catch (NumberFormatException var15)
            {
                var9 = var6.hashCode();
            }
        }

        WorldType var17 = WorldType.parseWorldType(var7);

        if (var17 == null)
        {
            var17 = WorldType.DEFAULT;
        }

        func_155757_ar();
        isCommandBlockEnabled();
        getOpPermissionLevel();
        isSnooperEnabled();
        setBuildLimit(settings.getIntProperty("max-build-height", 256));
        setBuildLimit((getBuildLimit() + 8) / 16 * 16);
        setBuildLimit(MathHelper.clamp_int(getBuildLimit(), 64, 256));
        settings.setProperty("max-build-height", Integer.valueOf(getBuildLimit()));
        field_155771_h.info("Preparing level \"" + getFolderName() + "\"");
        loadAllWorlds(getFolderName(), getFolderName(), var9, var17, var8);
        long var12 = System.nanoTime() - var4;
        String var14 = String.format("%.3fs", new Object[] {Double.valueOf(var12 / 1.0E9D)});
        field_155771_h.info("Done (" + var14 + ")! For help, type \"help\" or \"?\"");

        if (settings.getBooleanProperty("enable-query", false))
        {
            field_155771_h.info("Starting GS4 status listener");
            theRConThreadQuery = new RConThreadQuery(this);
            theRConThreadQuery.startThread();
        }

        if (settings.getBooleanProperty("enable-rcon", false))
        {
            field_155771_h.info("Starting remote control listener");
            theRConThreadMain = new RConThreadMain(this);
            theRConThreadMain.startThread();
        }

        return true;
    }

    public boolean canStructuresSpawn()
    {
        return canSpawnStructures;
    }

    public WorldSettings.GameType getGameType()
    {
        return gameType;
    }

    public EnumDifficulty func_147135_j()
    {
        return EnumDifficulty.func_151523_a(settings.getIntProperty("difficulty", 1));
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return settings.getBooleanProperty("hardcore", false);
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport par1CrashReport)
    {
        while (isServerRunning())
        {
            executePendingCommands();

            try
            {
                Thread.sleep(10L);
            }
            catch (InterruptedException var3)
            {
                ;
            }
        }
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport)
    {
        par1CrashReport = super.addServerInfoToCrashReport(par1CrashReport);
        par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new Callable()
        {
            private static final String __OBFID = "CL_00001785";

            public String call()
            {
                String var1 = DedicatedServer.this.getServerModName();
                return !var1.equals("vanilla") ? "Definitely; Server brand changed to \'" + var1 + "\'" : "Unknown (can\'t tell)";
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Type", new Callable()
        {
            private static final String __OBFID = "CL_00001788";

            public String call()
            {
                return "Dedicated Server (map_server.txt)";
            }
        });
        return par1CrashReport;
    }

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    protected void systemExitNow()
    {
        System.exit(0);
    }

    public void updateTimeLightAndEntities()
    {
        super.updateTimeLightAndEntities();
        executePendingCommands();
    }

    public boolean getAllowNether()
    {
        return settings.getBooleanProperty("allow-nether", true);
    }

    public boolean allowSpawnMonsters()
    {
        return settings.getBooleanProperty("spawn-monsters", true);
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        par1PlayerUsageSnooper.addData("whitelist_enabled", Boolean.valueOf(getConfigurationManager().isWhiteListEnabled()));
        par1PlayerUsageSnooper.addData("whitelist_count", Integer.valueOf(getConfigurationManager().getWhiteListedPlayers().size()));
        super.addServerStatsToSnooper(par1PlayerUsageSnooper);
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return settings.getBooleanProperty("snooper-enabled", true);
    }

    public void addPendingCommand(String par1Str, ICommandSender par2ICommandSender)
    {
        pendingCommandList.add(new ServerCommand(par1Str, par2ICommandSender));
    }

    public void executePendingCommands()
    {
        while (!pendingCommandList.isEmpty())
        {
            ServerCommand var1 = (ServerCommand)pendingCommandList.remove(0);
            getCommandManager().executeCommand(var1.sender, var1.command);
        }
    }

    public boolean isDedicatedServer()
    {
        return true;
    }

    public DedicatedPlayerList getConfigurationManager()
    {
        return (DedicatedPlayerList)super.getConfigurationManager();
    }

    /**
     * Gets an integer property. If it does not exist, set it to the specified
     * value.
     */
    public int getIntProperty(String par1Str, int par2)
    {
        return settings.getIntProperty(par1Str, par2);
    }

    /**
     * Gets a string property. If it does not exist, set it to the specified
     * value.
     */
    public String getStringProperty(String par1Str, String par2Str)
    {
        return settings.getStringProperty(par1Str, par2Str);
    }

    /**
     * Gets a boolean property. If it does not exist, set it to the specified
     * value.
     */
    public boolean getBooleanProperty(String par1Str, boolean par2)
    {
        return settings.getBooleanProperty(par1Str, par2);
    }

    /**
     * Saves an Object with the given property name.
     */
    public void setProperty(String par1Str, Object par2Obj)
    {
        settings.setProperty(par1Str, par2Obj);
    }

    /**
     * Saves all of the server properties to the properties file.
     */
    public void saveProperties()
    {
        settings.saveProperties();
    }

    /**
     * Returns the filename where server properties are stored
     */
    public String getSettingsFilename()
    {
        File var1 = settings.getPropertiesFile();
        return var1 != null ? var1.getAbsolutePath() : "No settings file";
    }

    public void setGuiEnabled()
    {
        MinecraftServerGui.createServerGui(this);
        guiIsEnabled = true;
    }

    public boolean getGuiEnabled()
    {
        return guiIsEnabled;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll,
     * gameType and allows external connections.
     */
    public String shareToLAN(WorldSettings.GameType par1EnumGameType, boolean par2)
    {
        return "";
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled()
    {
        return settings.getBooleanProperty("enable-command-block", false);
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtectionSize()
    {
        return settings.getIntProperty("spawn-protection", super.getSpawnProtectionSize());
    }

    /**
     * Return if we should hide player IPs or not
     */
    public boolean shouldLogIps()
    {
        return settings.getBooleanProperty("log-ips", true);
    }

    /**
     * Return if we should tell the player their IP or not
     */
    public boolean shouldTellIp()
    {
        return settings.getBooleanProperty("tell-ip", false);
    }

    /**
     * Return the leeway for a fastbreak detection.
     */
    public double getFastbreakLeeway()
    {
        return settings.getDoubleProperty("vac-fastbreak-leeway", 0.3);
    }

    /**
     * Returns the threshold for the ratio of fastbreak detections for a setback
     * and a message. 0.0 = perfect timing required, 1.0 = no protection
     */
    public double getFastbreakRatioThreshold()
    {
        return settings.getDoubleProperty("vac-fastbreak-ratio-threshold", 0.5);
    }

    /**
     * Return the threshold for kicking someone due to buildhack. A good default
     * is 6.
     */
    public int getBuildhackThreshold()
    {
        return settings.getIntProperty("vac-buildhack-threshold", 6);
    }

    /**
     * Return the number of ticks you can be floating and not be set back
     */
    public int getFloatingTicksThreshold()
    {
        return settings.getIntProperty("vac-floating-ticks-threshold", 40);
    }

    /**
     * Return the number of times you can be reset and not have it be logged
     */
    public int getFlyResetLogThreshold()
    {
        return settings.getIntProperty("vac-fly-reset-log-threshold", 3);
    }

    /**
     * Return the number of times you can be reset and not be kicked
     */
    public int getFlyResetKickThreshold()
    {
        return settings.getIntProperty("vac-fly-reset-kick-threshold", 5);
    }

    /**
     * Whether diamond notifications are enabled or not
     */
    public boolean useDiamondNotifications()
    {
        return settings.getBooleanProperty("vac-diamond-notifications", true);
    }

    /**
     * Return the number of ticks a player should take to regenerate health
     */
    public int getHealthRegenTickCount()
    {
        return settings.getIntProperty("vac-health-regen-tickcount", 70);
    }

    /**
     * Return the number of ticks a player should take to regenerate health
     */
    public double getSpeedhackLeeway()
    {
        return settings.getDoubleProperty("vac-speedhack-leeway", 0.01);
    }

    /**
     * Return the threshold for the ratio for number of times a playe moved too
     * quickly will be set back for
     */
    public double getSpeedhackRatioKickThreshold()
    {
        return settings.getDoubleProperty("vac-speedhack-ratio-threshold", 0.1);
    }

    /**
     * Get the speed limit for a player with specific conditions.
     * 
     * @param sprinting
     *            whether the player is sprinting or not
     * @param jumping
     *            whether the player is jumping or not
     * @param potion
     *            whether the player has a speed potion or not
     * @return the speed limit for the player
     */
    public double getSpeedLimit(boolean sprinting, boolean jumping, boolean potion)
    {
        String property = "vac-speed-limit-";
        double defaultValue = 0.25;
        if (sprinting)
        {
            defaultValue = 0.8;
            property += "sprinting-";
        }
        if (jumping)
        {
            property += "jumping-";
            defaultValue += 0.1;
        }
        if (potion)
        {
            property += "potion-";
            defaultValue += 0.1;
        }
        if (property.substring(property.length() - 1).equals("-"))
        {
            System.out.println(property.substring(property.length() - 1));
            property = property.substring(0, property.length() - 1);
            System.out.println(property);
        }
        return settings.getDoubleProperty(property, defaultValue);
    }

    /**
     * Return the speed limit for a sneaking player.
     */
    public double getSneakSpeedLimit()
    {
        return settings.getDoubleProperty("vac-speed-limit-sneak", 0.16);
    }

    /**
     * Return the mode to check proxies with.
     */
    public int getProxyCheckMode()
    {
        return settings.getIntProperty("vac-check-proxies-mode", 0);
    }

    /**
     * Return if we will check proxies or not.
     */
    public boolean shouldCheckProxies()
    {
        return settings.getBooleanProperty("vac-check-proxies", true);
    }

    /**
     * Return if we will make kicks into tempbans.
     */
    public boolean shouldKicksBeBans()
    {
        return settings.getBooleanProperty("tempban-on-kicks", false);
    }

    /**
     * Return how long a tempban from a kick will last in minutes.
     */
    public int getKickTempbanLength()
    {
        return settings.getIntProperty("kick-tempban-length", 1);
    }

    /**
     * Returns true if a player does not have permission to edit the block at
     * the given coordinates.
     */
    public boolean isBlockProtected(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        if (par1World.provider.dimensionId != 0)
        {
            return false;
        }
        else if (getConfigurationManager().getOps().isEmpty())
        {
            return false;
        }
        else if (getConfigurationManager().isPlayerOpped(par5EntityPlayer.getUsername()))
        {
            return false;
        }
        else if (getSpawnProtectionSize() <= 0)
        {
            return false;
        }
        else
        {
            ChunkCoordinates var6 = par1World.getSpawnPoint();
            int var7 = MathHelper.abs_int(par2 - var6.posX);
            int var8 = MathHelper.abs_int(par4 - var6.posZ);
            int var9 = Math.max(var7, var8);
            return var9 <= getSpawnProtectionSize();
        }
    }

    public int getOpPermissionLevel()
    {
        return settings.getIntProperty("op-permission-level", 4);
    }

    public void func_143006_e(int par1)
    {
        super.func_143006_e(par1);
        settings.setProperty("player-idle-timeout", Integer.valueOf(par1));
        saveProperties();
    }

    public boolean func_155757_ar()
    {
        return settings.getBooleanProperty("announce-player-achievements", true);
    }
}
