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
import net.minecraft.server.management.ServerConfigurationManager;
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
                this.setDaemon(true);
                this.start();
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

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            field_155771_h.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        field_155771_h.info("Loading properties");
        this.settings = new PropertyManager(new File("server.properties"));

        if (this.isSinglePlayer())
        {
            this.setHostname("127.0.0.1");
        }
        else
        {
            this.setOnlineMode(this.settings.getBooleanProperty("online-mode", true));
            this.setHostname(this.settings.getStringProperty("server-ip", ""));
        }

        this.setCanSpawnAnimals(this.settings.getBooleanProperty("spawn-animals", true));
        this.setCanSpawnNPCs(this.settings.getBooleanProperty("spawn-npcs", true));
        this.setAllowPvp(this.settings.getBooleanProperty("pvp", true));
        this.setAllowFlight(this.settings.getBooleanProperty("allow-flight", false));
        this.func_155759_m(this.settings.getStringProperty("resource-pack", ""));
        this.setMOTD(this.settings.getStringProperty("motd", "A Minecraft Server"));
        this.setForceGamemode(this.settings.getBooleanProperty("force-gamemode", false));
        this.func_143006_e(this.settings.getIntProperty("player-idle-timeout", 0));

        if (this.settings.getIntProperty("difficulty", 1) < 0)
        {
            this.settings.setProperty("difficulty", Integer.valueOf(0));
        }
        else if (this.settings.getIntProperty("difficulty", 1) > 3)
        {
            this.settings.setProperty("difficulty", Integer.valueOf(3));
        }

        this.canSpawnStructures = this.settings.getBooleanProperty("generate-structures", true);
        int var2 = this.settings.getIntProperty("gamemode", WorldSettings.GameType.SURVIVAL.getID());
        this.gameType = WorldSettings.getGameTypeById(var2);
        field_155771_h.info("Default game type: " + this.gameType);
        InetAddress var3 = null;

        if (this.getServerHostname().length() > 0)
        {
            var3 = InetAddress.getByName(this.getServerHostname());
        }

        if (this.getServerPort() < 0)
        {
            this.setServerPort(this.settings.getIntProperty("server-port", 25565));
        }

        field_155771_h.info("Generating keypair");
        this.setKeyPair(CryptManager.generateKeyPair());
        field_155771_h.info("Starting Minecraft server on " + (this.getServerHostname().length() == 0 ? "*" : this.getServerHostname()) + ":" + this.getServerPort());

        try
        {
            this.func_147137_ag().addLanEndpoint(var3, this.getServerPort());
        }
        catch (IOException var16)
        {
            field_155771_h.warn("**** FAILED TO BIND TO PORT!");
            field_155771_h.warn("The exception was: {}", new Object[] {var16.toString()});
            field_155771_h.warn("Perhaps a server is already running on that port?");
            return false;
        }

        if (!this.isServerInOnlineMode())
        {
            field_155771_h.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            field_155771_h.warn("The server will make no attempt to authenticate usernames. Beware.");
            field_155771_h.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            field_155771_h.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        this.setConfigurationManager(new DedicatedPlayerList(this));
        long var4 = System.nanoTime();

        if (this.getFolderName() == null)
        {
            this.setFolderName(this.settings.getStringProperty("level-name", "world"));
        }

        String var6 = this.settings.getStringProperty("level-seed", "");
        String var7 = this.settings.getStringProperty("level-type", "DEFAULT");
        String var8 = this.settings.getStringProperty("generator-settings", "");
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
                var9 = (long)var6.hashCode();
            }
        }

        WorldType var17 = WorldType.parseWorldType(var7);

        if (var17 == null)
        {
            var17 = WorldType.DEFAULT;
        }

        this.func_155757_ar();
        this.isCommandBlockEnabled();
        this.getOpPermissionLevel();
        this.isSnooperEnabled();
        this.setBuildLimit(this.settings.getIntProperty("max-build-height", 256));
        this.setBuildLimit((this.getBuildLimit() + 8) / 16 * 16);
        this.setBuildLimit(MathHelper.clamp_int(this.getBuildLimit(), 64, 256));
        this.settings.setProperty("max-build-height", Integer.valueOf(this.getBuildLimit()));
        field_155771_h.info("Preparing level \"" + this.getFolderName() + "\"");
        this.loadAllWorlds(this.getFolderName(), this.getFolderName(), var9, var17, var8);
        long var12 = System.nanoTime() - var4;
        String var14 = String.format("%.3fs", new Object[] {Double.valueOf((double)var12 / 1.0E9D)});
        field_155771_h.info("Done (" + var14 + ")! For help, type \"help\" or \"?\"");

        if (this.settings.getBooleanProperty("enable-query", false))
        {
            field_155771_h.info("Starting GS4 status listener");
            this.theRConThreadQuery = new RConThreadQuery(this);
            this.theRConThreadQuery.startThread();
        }

        if (this.settings.getBooleanProperty("enable-rcon", false))
        {
            field_155771_h.info("Starting remote control listener");
            this.theRConThreadMain = new RConThreadMain(this);
            this.theRConThreadMain.startThread();
        }

        return true;
    }

    public boolean canStructuresSpawn()
    {
        return this.canSpawnStructures;
    }

    public WorldSettings.GameType getGameType()
    {
        return this.gameType;
    }

    public EnumDifficulty func_147135_j()
    {
        return EnumDifficulty.func_151523_a(this.settings.getIntProperty("difficulty", 1));
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.settings.getBooleanProperty("hardcore", false);
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport par1CrashReport)
    {
        while (this.isServerRunning())
        {
            this.executePendingCommands();

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
        this.executePendingCommands();
    }

    public boolean getAllowNether()
    {
        return this.settings.getBooleanProperty("allow-nether", true);
    }

    public boolean allowSpawnMonsters()
    {
        return this.settings.getBooleanProperty("spawn-monsters", true);
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        par1PlayerUsageSnooper.addData("whitelist_enabled", Boolean.valueOf(this.getConfigurationManager().isWhiteListEnabled()));
        par1PlayerUsageSnooper.addData("whitelist_count", Integer.valueOf(this.getConfigurationManager().getWhiteListedPlayers().size()));
        super.addServerStatsToSnooper(par1PlayerUsageSnooper);
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return this.settings.getBooleanProperty("snooper-enabled", true);
    }

    public void addPendingCommand(String par1Str, ICommandSender par2ICommandSender)
    {
        this.pendingCommandList.add(new ServerCommand(par1Str, par2ICommandSender));
    }

    public void executePendingCommands()
    {
        while (!this.pendingCommandList.isEmpty())
        {
            ServerCommand var1 = (ServerCommand)this.pendingCommandList.remove(0);
            this.getCommandManager().executeCommand(var1.sender, var1.command);
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
     * Gets an integer property. If it does not exist, set it to the specified value.
     */
    public int getIntProperty(String par1Str, int par2)
    {
        return this.settings.getIntProperty(par1Str, par2);
    }

    /**
     * Gets a string property. If it does not exist, set it to the specified value.
     */
    public String getStringProperty(String par1Str, String par2Str)
    {
        return this.settings.getStringProperty(par1Str, par2Str);
    }

    /**
     * Gets a boolean property. If it does not exist, set it to the specified value.
     */
    public boolean getBooleanProperty(String par1Str, boolean par2)
    {
        return this.settings.getBooleanProperty(par1Str, par2);
    }

    /**
     * Saves an Object with the given property name.
     */
    public void setProperty(String par1Str, Object par2Obj)
    {
        this.settings.setProperty(par1Str, par2Obj);
    }

    /**
     * Saves all of the server properties to the properties file.
     */
    public void saveProperties()
    {
        this.settings.saveProperties();
    }

    /**
     * Returns the filename where server properties are stored
     */
    public String getSettingsFilename()
    {
        File var1 = this.settings.getPropertiesFile();
        return var1 != null ? var1.getAbsolutePath() : "No settings file";
    }

    public void setGuiEnabled()
    {
        MinecraftServerGui.createServerGui(this);
        this.guiIsEnabled = true;
    }

    public boolean getGuiEnabled()
    {
        return this.guiIsEnabled;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
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
        return this.settings.getBooleanProperty("enable-command-block", false);
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtectionSize()
    {
        return this.settings.getIntProperty("spawn-protection", super.getSpawnProtectionSize());
    }
    
    /**
     * Return if we should hide player IPs or not
     */
    public boolean shouldLogIps()
    {
    	return this.settings.getBooleanProperty("log-ips", true);
    }
    
    /**
     * Return if we should tell the player their IP or not
     */
    public boolean shouldTellIp()
    {
    	return this.settings.getBooleanProperty("tell-ip", false);
    }
    
    /**
     * Return the leeway for a fastbreak detection.
     */
    public double getFastbreakLeeway()
    {
    	return this.settings.getDoubleProperty("vac-fastbreak-leeway", 0.3);
    }
    
    /**
     * Returns the threshold for the ratio of fastbreak detections
     * for a setback and a message. 0.0 = perfect timing required,
     * 1.0 = no protection
     */
    public double getFastbreakRatioThreshold()
    {
    	return this.settings.getDoubleProperty("vac-fastbreak-ratio-threshold", 0.5);
    }
    
    /**
     * Return the threshold for kicking someone due to buildhack.
     * A good default is 6.
     */
    public int getBuildhackThreshold()
    {
    	return this.settings.getIntProperty("vac-buildhack-threshold", 6);
    }

    /**
     * Returns true if a player does not have permission to edit the block at the given coordinates.
     */
    public boolean isBlockProtected(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        if (par1World.provider.dimensionId != 0)
        {
            return false;
        }
        else if (this.getConfigurationManager().getOps().isEmpty())
        {
            return false;
        }
        else if (this.getConfigurationManager().isPlayerOpped(par5EntityPlayer.getCommandSenderName()))
        {
            return false;
        }
        else if (this.getSpawnProtectionSize() <= 0)
        {
            return false;
        }
        else
        {
            ChunkCoordinates var6 = par1World.getSpawnPoint();
            int var7 = MathHelper.abs_int(par2 - var6.posX);
            int var8 = MathHelper.abs_int(par4 - var6.posZ);
            int var9 = Math.max(var7, var8);
            return var9 <= this.getSpawnProtectionSize();
        }
    }

    public int getOpPermissionLevel()
    {
        return this.settings.getIntProperty("op-permission-level", 4);
    }

    public void func_143006_e(int par1)
    {
        super.func_143006_e(par1);
        this.settings.setProperty("player-idle-timeout", Integer.valueOf(par1));
        this.saveProperties();
    }

    public boolean func_155757_ar()
    {
        return this.settings.getBooleanProperty("announce-player-achievements", true);
    }
}
