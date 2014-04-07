package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

public abstract class MinecraftServer implements ICommandSender, Runnable, IPlayerUsage
{
    private static final Logger logger = LogManager.getLogger();
    private static final Marker MARKER_VAC = MarkerManager.getMarker("VAC");

    /** Instance of Minecraft Server. */
    private static MinecraftServer mcServer;
    private final ISaveFormat anvilConverterForAnvilFile;

    /** The PlayerUsageSnooper instance. */
    private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this, getCurrentTimeMillis());
    private final File anvilFile;

    /** List of names of players who are online. */
    private final List playersOnline = new ArrayList();
    private final ICommandManager commandManager;
    public final Profiler theProfiler = new Profiler();
    private final NetworkSystem field_147144_o;
    private final ServerStatusResponse field_147147_p = new ServerStatusResponse();
    private final Random field_147146_q = new Random();

    /** The server's hostname. */
    private String hostname;

    /** The server's port. */
    private int serverPort = -1;

    /** The server world instances. */
    public WorldServer[] worldServers;

    /** The ServerConfigurationManager instance. */
    private ServerConfigurationManager serverConfigManager;

    /**
     * Indicates whether the server is running or not. Set to false to initiate
     * a shutdown.
     */
    private boolean serverRunning = true;

    /** Indicates to other classes that the server is safely stopped. */
    private boolean serverStopped;

    /** Incremented every tick. */
    private int tickCounter;
    protected final Proxy serverProxy;

    /**
     * The task the server is currently working on(and will output on
     * outputPercentRemaining).
     */
    public String currentTask;

    /** The percentage of the current task finished so far. */
    public int percentDone;

    /** True if the server is in online mode. */
    private boolean onlineMode;

    /** True if the server has animals turned on. */
    private boolean canSpawnAnimals;
    private boolean canSpawnNPCs;

    /** Indicates whether PvP is active on the server or not. */
    private boolean pvpEnabled;

    /** Determines if flight is allowed or not. */
    private boolean allowFlight;

    /** The server MOTD string. */
    private String motd;

    /** Maximum build height. */
    private int buildLimit;
    private int field_143008_E = 0;
    public final long[] tickTimeArray = new long[100];

    /** Stats are [dimension][tick%100] system.nanoTime is stored. */
    public long[][] timeOfLastDimensionTick;
    private KeyPair serverKeyPair;

    /** Username of the server owner (for integrated servers) */
    private String serverOwner;
    private String folderName;
    private boolean isDemo;
    private boolean enableBonusChest;

    /**
     * If true, there is no need to save chunks or stop the server, because that
     * is already being done.
     */
    private boolean worldIsBeingDeleted;
    private String field_147141_M = "";
    private boolean serverIsRunning;

    /**
     * Set when warned for "Can't keep up", which triggers again after 15
     * seconds.
     */
    private long timeOfLastWarning;
    private String userMessage;
    private boolean startProfiling;
    private boolean isGamemodeForced;
    private final MinecraftSessionService field_147143_S;
    private long field_147142_T = 0L;
    private static final String __OBFID = "CL_00001462";

    public MinecraftServer(File p_i45281_1_, Proxy p_i45281_2_)
    {
        mcServer = this;
        serverProxy = p_i45281_2_;
        anvilFile = p_i45281_1_;
        field_147144_o = new NetworkSystem(this);
        commandManager = new ServerCommandManager();
        anvilConverterForAnvilFile = new AnvilSaveConverter(p_i45281_1_);
        field_147143_S = (new YggdrasilAuthenticationService(p_i45281_2_, UUID.randomUUID().toString())).createMinecraftSessionService();
    }

    /**
     * Initialises the server and starts it.
     */
    protected abstract boolean startServer() throws IOException;

    protected void convertMapIfNeeded(String par1Str)
    {
        if (getActiveAnvilConverter().isOldMapFormat(par1Str))
        {
            logger.info("Converting map!");
            setUserMessage("menu.convertingLevel");
            getActiveAnvilConverter().convertMapFormat(par1Str, new IProgressUpdate()
            {
                private long field_96245_b = System.currentTimeMillis();
                private static final String __OBFID = "CL_00001417";

                public void displaySavingString(String par1Str)
                {
                }

                public void setLoadingProgress(int par1)
                {
                    if (System.currentTimeMillis() - field_96245_b >= 1000L)
                    {
                        field_96245_b = System.currentTimeMillis();
                        MinecraftServer.logger.info("Converting... " + par1 + "%");
                    }
                }

                public void displayLoadingString(String par1Str)
                {
                }
            });
        }
    }

    /**
     * Typically "menu.convertingLevel", "menu.loadingLevel" or others.
     */
    protected synchronized void setUserMessage(String par1Str)
    {
        userMessage = par1Str;
    }

    protected void loadAllWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str)
    {
        convertMapIfNeeded(par1Str);
        setUserMessage("menu.loadingLevel");
        worldServers = new WorldServer[3];
        timeOfLastDimensionTick = new long[worldServers.length][100];
        ISaveHandler var7 = anvilConverterForAnvilFile.getSaveLoader(par1Str, true);
        WorldInfo var9 = var7.loadWorldInfo();
        WorldSettings var8;

        if (var9 == null)
        {
            var8 = new WorldSettings(par3, getGameType(), canStructuresSpawn(), isHardcore(), par5WorldType);
            var8.func_82750_a(par6Str);
        }
        else
        {
            var8 = new WorldSettings(var9);
        }

        if (enableBonusChest)
        {
            var8.enableBonusChest();
        }

        for (int var10 = 0; var10 < worldServers.length; ++var10)
        {
            byte var11 = 0;

            if (var10 == 1)
            {
                var11 = -1;
            }

            if (var10 == 2)
            {
                var11 = 1;
            }

            if (var10 == 0)
            {
                if (isDemo())
                {
                    worldServers[var10] = new DemoWorldServer(this, var7, par2Str, var11, theProfiler);
                }
                else
                {
                    worldServers[var10] = new WorldServer(this, var7, par2Str, var11, var8, theProfiler);
                }
            }
            else
            {
                worldServers[var10] = new WorldServerMulti(this, var7, par2Str, var11, var8, worldServers[0], theProfiler);
            }

            worldServers[var10].addWorldAccess(new WorldManager(this, worldServers[var10]));

            if (!isSinglePlayer())
            {
                worldServers[var10].getWorldInfo().setGameType(getGameType());
            }

            serverConfigManager.setPlayerManager(worldServers);
        }

        func_147139_a(func_147135_j());
        initialWorldChunkLoad();
    }

    protected void initialWorldChunkLoad()
    {
        boolean var1 = true;
        boolean var2 = true;
        boolean var3 = true;
        boolean var4 = true;
        int var5 = 0;
        setUserMessage("menu.generatingTerrain");
        byte var6 = 0;
        logger.info("Preparing start region for level " + var6);
        WorldServer var7 = worldServers[var6];
        ChunkCoordinates var8 = var7.getSpawnPoint();
        long var9 = getCurrentTimeMillis();

        for (int var11 = -192; var11 <= 192 && isServerRunning(); var11 += 16)
        {
            for (int var12 = -192; var12 <= 192 && isServerRunning(); var12 += 16)
            {
                long var13 = getCurrentTimeMillis();

                if (var13 - var9 > 1000L)
                {
                    outputPercentRemaining("Preparing spawn area", var5 * 100 / 625);
                    var9 = var13;
                }

                ++var5;
                var7.theChunkProviderServer.loadChunk(var8.posX + var11 >> 4, var8.posZ + var12 >> 4);
            }
        }

        clearCurrentTask();
    }

    public abstract boolean canStructuresSpawn();

    public abstract WorldSettings.GameType getGameType();

    public abstract EnumDifficulty func_147135_j();

    /**
     * Defaults to false.
     */
    public abstract boolean isHardcore();

    public abstract int getOpPermissionLevel();

    /**
     * Used to display a percent remaining given text and the percentage.
     */
    protected void outputPercentRemaining(String par1Str, int par2)
    {
        currentTask = par1Str;
        percentDone = par2;
        logger.info(par1Str + ": " + par2 + "%");
    }

    /**
     * Set current task to null and set its percentage to 0.
     */
    protected void clearCurrentTask()
    {
        currentTask = null;
        percentDone = 0;
    }

    /**
     * par1 indicates if a log message should be output.
     */
    protected void saveAllWorlds(boolean par1)
    {
        if (!worldIsBeingDeleted)
        {
            WorldServer[] var2 = worldServers;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4)
            {
                WorldServer var5 = var2[var4];

                if (var5 != null)
                {
                    if (!par1)
                    {
                        logger.info("Saving chunks for level \'" + var5.getWorldInfo().getWorldName() + "\'/" + var5.provider.getDimensionName());
                    }

                    try
                    {
                        var5.saveAllChunks(true, (IProgressUpdate)null);
                    }
                    catch (MinecraftException var7)
                    {
                        logger.warn(var7.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stopServer()
    {
        if (!worldIsBeingDeleted)
        {
            logger.info("Stopping server");

            if (func_147137_ag() != null)
            {
                func_147137_ag().terminateEndpoints();
            }

            if (serverConfigManager != null)
            {
                logger.info("Saving players");
                serverConfigManager.saveAllPlayerData();
                serverConfigManager.removeAllPlayers();
            }

            logger.info("Saving worlds");
            saveAllWorlds(false);

            for (int var1 = 0; var1 < worldServers.length; ++var1)
            {
                WorldServer var2 = worldServers[var1];
                var2.flush();
            }

            if (usageSnooper.isSnooperRunning())
            {
                usageSnooper.stopSnooper();
            }
        }
    }

    /**
     * "getHostname" is already taken, but both return the hostname.
     */
    public String getServerHostname()
    {
        return hostname;
    }

    public void setHostname(String par1Str)
    {
        hostname = par1Str;
    }

    public boolean isServerRunning()
    {
        return serverRunning;
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to
     * shut down.
     */
    public void initiateShutdown()
    {
        serverRunning = false;
    }

    public void run()
    {
        try
        {
            if (startServer())
            {
                long var1 = getCurrentTimeMillis();
                long var50 = 0L;
                field_147147_p.func_151315_a(new ChatComponentText(motd));
                field_147147_p.func_151321_a(new ServerStatusResponse.MinecraftProtocolVersionIdentifier("1.7.2", 4));
                func_147138_a(field_147147_p);

                while (serverRunning)
                {
                    long var5 = getCurrentTimeMillis();
                    long var7 = var5 - var1;

                    if (var7 > 2000L && var1 - timeOfLastWarning >= 15000L)
                    {
                        logger.warn("Can\'t keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", new Object[] {Long.valueOf(var7), Long.valueOf(var7 / 50L)});
                        var7 = 2000L;
                        timeOfLastWarning = var1;
                    }

                    if (var7 < 0L)
                    {
                        logger.warn("Time ran backwards! Did the system time change?");
                        var7 = 0L;
                    }

                    var50 += var7;
                    var1 = var5;

                    if (worldServers[0].areAllPlayersAsleep())
                    {
                        tick();
                        var50 = 0L;
                    }
                    else
                    {
                        while (var50 > 50L)
                        {
                            var50 -= 50L;
                            tick();
                        }
                    }

                    Thread.sleep(1L);
                    serverIsRunning = true;
                }
            }
            else
            {
                finalTick((CrashReport)null);
            }
        }
        catch (Throwable var48)
        {
            logger.error("Encountered an unexpected exception", var48);
            CrashReport var2 = null;

            if (var48 instanceof ReportedException)
            {
                var2 = addServerInfoToCrashReport(((ReportedException)var48).getCrashReport());
            }
            else
            {
                var2 = addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", var48));
            }

            File var3 = new File(new File(getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (var2.saveToFile(var3))
            {
                logger.error("This crash report has been saved to: " + var3.getAbsolutePath());
            }
            else
            {
                logger.error("We were unable to save this crash report to disk.");
            }

            finalTick(var2);
        }
        finally
        {
            try
            {
                stopServer();
                serverStopped = true;
            }
            catch (Throwable var46)
            {
                logger.error("Exception stopping the server", var46);
            }
            finally
            {
                systemExitNow();
            }
        }
    }

    private void func_147138_a(ServerStatusResponse p_147138_1_)
    {
        File var2 = getFile("server-icon.png");

        if (var2.isFile())
        {
            ByteBuf var3 = Unpooled.buffer();

            try
            {
                BufferedImage var4 = ImageIO.read(var2);
                Validate.validState(var4.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(var4.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write(var4, "PNG", new ByteBufOutputStream(var3));
                ByteBuf var5 = Base64.encode(var3);
                p_147138_1_.func_151320_a("data:image/png;base64," + var5.toString(Charsets.UTF_8));
            }
            catch (Exception var6)
            {
                logger.error("Couldn\'t load server icon", var6);
            }
        }
    }

    protected File getDataDirectory()
    {
        return new File(".");
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport par1CrashReport)
    {
    }

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    protected void systemExitNow()
    {
    }

    /**
     * Main function called by run() every loop.
     */
    protected void tick()
    {
        long var1 = System.nanoTime();
        AxisAlignedBB.getAABBPool().cleanPool();
        ++tickCounter;

        if (startProfiling)
        {
            startProfiling = false;
            theProfiler.profilingEnabled = true;
            theProfiler.clearProfiling();
        }

        theProfiler.startSection("root");
        updateTimeLightAndEntities();

        if (var1 - field_147142_T >= 5000000000L)
        {
            field_147142_T = var1;
            field_147147_p.func_151319_a(new ServerStatusResponse.PlayerCountData(getMaxPlayers(), getCurrentPlayerCount()));
            GameProfile[] var3 = new GameProfile[Math.min(getCurrentPlayerCount(), 12)];
            int var4 = MathHelper.getRandomIntegerInRange(field_147146_q, 0, getCurrentPlayerCount() - var3.length);

            for (int var5 = 0; var5 < var3.length; ++var5)
            {
                var3[var5] = ((EntityPlayerMP)serverConfigManager.playerEntityList.get(var4 + var5)).getGameProfile();
            }

            Collections.shuffle(Arrays.asList(var3));
            field_147147_p.func_151318_b().func_151330_a(var3);
        }

        if (tickCounter % 900 == 0)
        {
            theProfiler.startSection("save");
            serverConfigManager.saveAllPlayerData();
            saveAllWorlds(true);
            theProfiler.endSection();
        }

        theProfiler.startSection("tallying");
        tickTimeArray[tickCounter % 100] = System.nanoTime() - var1;
        theProfiler.endSection();
        theProfiler.startSection("snooper");

        if (!usageSnooper.isSnooperRunning() && tickCounter > 100)
        {
            usageSnooper.startSnooper();
        }

        if (tickCounter % 6000 == 0)
        {
            usageSnooper.addMemoryStatsToSnooper();
        }

        theProfiler.endSection();
        theProfiler.endSection();
    }

    public void updateTimeLightAndEntities()
    {
        theProfiler.startSection("levels");
        int var1;

        for (var1 = 0; var1 < worldServers.length; ++var1)
        {
            long var2 = System.nanoTime();

            if (var1 == 0 || getAllowNether())
            {
                WorldServer var4 = worldServers[var1];
                theProfiler.startSection(var4.getWorldInfo().getWorldName());
                theProfiler.startSection("pools");
                var4.getWorldVec3Pool().clear();
                theProfiler.endSection();

                if (tickCounter % 20 == 0)
                {
                    theProfiler.startSection("timeSync");
                    serverConfigManager.sendPacketToAllPlayersInDimension(new S03PacketTimeUpdate(var4.getTotalWorldTime(), var4.getWorldTime(), var4.getGameRules().getGameRuleBooleanValue("doDaylightCycle")), var4.provider.dimensionId);
                    theProfiler.endSection();
                }

                theProfiler.startSection("tick");
                CrashReport var6;

                try
                {
                    var4.tick();
                }
                catch (Throwable var8)
                {
                    var6 = CrashReport.makeCrashReport(var8, "Exception ticking world");
                    var4.addWorldInfoToCrashReport(var6);
                    throw new ReportedException(var6);
                }

                try
                {
                    var4.updateEntities();
                }
                catch (Throwable var7)
                {
                    var6 = CrashReport.makeCrashReport(var7, "Exception ticking world entities");
                    var4.addWorldInfoToCrashReport(var6);
                    throw new ReportedException(var6);
                }

                theProfiler.endSection();
                theProfiler.startSection("tracker");
                var4.getEntityTracker().updateTrackedEntities();
                theProfiler.endSection();
                theProfiler.endSection();
            }

            timeOfLastDimensionTick[var1][tickCounter % 100] = System.nanoTime() - var2;
        }

        theProfiler.endStartSection("connection");
        func_147137_ag().networkTick();
        theProfiler.endStartSection("players");
        serverConfigManager.onTick();
        theProfiler.endStartSection("tickables");

        for (var1 = 0; var1 < playersOnline.size(); ++var1)
        {
            ((IUpdatePlayerListBox)playersOnline.get(var1)).update();
        }

        theProfiler.endSection();
    }

    public boolean getAllowNether()
    {
        return true;
    }

    public void func_82010_a(IUpdatePlayerListBox par1IUpdatePlayerListBox)
    {
        playersOnline.add(par1IUpdatePlayerListBox);
    }

    public static void main(String[] par0ArrayOfStr)
    {
        Bootstrap.func_151354_b();

        try
        {
            boolean var1 = !GraphicsEnvironment.isHeadless();
            String var2 = null;
            String var3 = ".";
            String var4 = null;
            boolean var5 = false;
            boolean var6 = false;
            int var7 = -1;

            for (int var8 = 0; var8 < par0ArrayOfStr.length; ++var8)
            {
                String var9 = par0ArrayOfStr[var8];
                String var10 = var8 == par0ArrayOfStr.length - 1 ? null : par0ArrayOfStr[var8 + 1];
                boolean var11 = false;

                if (!var9.equals("nogui") && !var9.equals("--nogui"))
                {
                    if (var9.equals("--port") && var10 != null)
                    {
                        var11 = true;

                        try
                        {
                            var7 = Integer.parseInt(var10);
                        }
                        catch (NumberFormatException var13)
                        {
                            ;
                        }
                    }
                    else if (var9.equals("--singleplayer") && var10 != null)
                    {
                        var11 = true;
                        var2 = var10;
                    }
                    else if (var9.equals("--universe") && var10 != null)
                    {
                        var11 = true;
                        var3 = var10;
                    }
                    else if (var9.equals("--world") && var10 != null)
                    {
                        var11 = true;
                        var4 = var10;
                    }
                    else if (var9.equals("--demo"))
                    {
                        var5 = true;
                    }
                    else if (var9.equals("--bonusChest"))
                    {
                        var6 = true;
                    }
                }
                else
                {
                    var1 = false;
                }

                if (var11)
                {
                    ++var8;
                }
            }

            final DedicatedServer var15 = new DedicatedServer(new File(var3));

            if (var2 != null)
            {
                var15.setServerOwner(var2);
            }

            if (var4 != null)
            {
                var15.setFolderName(var4);
            }

            if (var7 >= 0)
            {
                var15.setServerPort(var7);
            }

            if (var5)
            {
                var15.setDemo(true);
            }

            if (var6)
            {
                var15.canCreateBonusChest(true);
            }

            if (var1)
            {
                var15.setGuiEnabled();
            }

            var15.startServerThread();
            Runtime.getRuntime().addShutdownHook(new Thread("Server Shutdown Thread")
            {
                private static final String __OBFID = "CL_00001806";

                public void run()
                {
                    var15.stopServer();
                }
            });
        }
        catch (Exception var14)
        {
            logger.fatal("Failed to start the minecraft server", var14);
        }
    }

    public void startServerThread()
    {
        (new Thread("Server thread")
        {
            private static final String __OBFID = "CL_00001418";

            public void run()
            {
                MinecraftServer.this.run();
            }
        }).start();
    }

    /**
     * Returns a File object from the specified string.
     */
    public File getFile(String par1Str)
    {
        return new File(getDataDirectory(), par1Str);
    }

    /**
     * Logs the message with a level of INFO.
     */
    public void logInfo(String par1Str)
    {
        logger.info(par1Str);
    }

    /**
     * Logs the message with a level of WARN.
     */
    public void logWarning(String par1Str)
    {
        logger.warn(par1Str);
    }

    /**
     * Gets the worldServer by the given dimension.
     */
    public WorldServer worldServerForDimension(int par1)
    {
        return par1 == -1 ? worldServers[1] : (par1 == 1 ? worldServers[2] : worldServers[0]);
    }

    /**
     * Returns the server's hostname.
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Never used, but "getServerPort" is already taken.
     */
    public int getPort()
    {
        return serverPort;
    }

    /**
     * Returns the server message of the day
     */
    public String getMotd()
    {
        return motd;
    }

    /**
     * Returns the server's Minecraft version as string.
     */
    public String getMinecraftVersion()
    {
        return "1.7.2";
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount()
    {
        return serverConfigManager.getCurrentPlayerCount();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return serverConfigManager.getMaxPlayers();
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getAllUsernames()
    {
        return serverConfigManager.getAllUsernames();
    }

    /**
     * Used by RCon's Query in the form of
     * "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
     */
    public String getPlugins()
    {
        return "";
    }

    /**
     * Handle a command received by an RCon instance
     */
    public String handleRConCommand(String par1Str)
    {
        RConConsoleSource.instance.resetLog();
        commandManager.executeCommand(RConConsoleSource.instance, par1Str);
        return RConConsoleSource.instance.getLogContents();
    }

    /**
     * Returns true if debugging is enabled, false otherwise.
     */
    public boolean isDebuggingEnabled()
    {
        return false;
    }

    /**
     * Logs the error message with a level of SEVERE.
     */
    public void logSevere(String par1Str)
    {
        logger.error(par1Str);
    }

    /**
     * If isDebuggingEnabled(), logs the message with a level of INFO.
     */
    public void logDebug(String par1Str)
    {
        if (isDebuggingEnabled())
        {
            logger.info(par1Str);
        }
    }

    public void logVAC(String message)
    {
        logger.warn(MARKER_VAC, "[VAC]: " + message);
    }

    public String getServerModName()
    {
        return "vanilla";
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport)
    {
        par1CrashReport.getCategory().addCrashSectionCallable("Profiler Position", new Callable()
        {
            private static final String __OBFID = "CL_00001419";

            public String call()
            {
                return theProfiler.profilingEnabled ? theProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });

        if (worldServers != null && worldServers.length > 0 && worldServers[0] != null)
        {
            par1CrashReport.getCategory().addCrashSectionCallable("Vec3 Pool Size", new Callable()
            {
                private static final String __OBFID = "CL_00001420";

                public String call()
                {
                    int var1 = worldServers[0].getWorldVec3Pool().getPoolSize();
                    int var2 = 56 * var1;
                    int var3 = var2 / 1024 / 1024;
                    int var4 = worldServers[0].getWorldVec3Pool().getNextFreeSpace();
                    int var5 = 56 * var4;
                    int var6 = var5 / 1024 / 1024;
                    return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; " + var6 + " MB) used";
                }
            });
        }

        if (serverConfigManager != null)
        {
            par1CrashReport.getCategory().addCrashSectionCallable("Player Count", new Callable()
            {
                private static final String __OBFID = "CL_00001780";

                public String call()
                {
                    return serverConfigManager.getCurrentPlayerCount() + " / " + serverConfigManager.getMaxPlayers() + "; " + serverConfigManager.playerEntityList;
                }
            });
        }

        return par1CrashReport;
    }

    /**
     * If par2Str begins with /, then it searches for commands, otherwise it
     * returns players.
     */
    public List getPossibleCompletions(ICommandSender par1ICommandSender, String par2Str)
    {
        ArrayList var3 = new ArrayList();

        if (par2Str.startsWith("/"))
        {
            par2Str = par2Str.substring(1);
            boolean var10 = !par2Str.contains(" ");
            List var11 = commandManager.getPossibleCommands(par1ICommandSender, par2Str);

            if (var11 != null)
            {
                Iterator var12 = var11.iterator();

                while (var12.hasNext())
                {
                    String var13 = (String)var12.next();

                    if (var10)
                    {
                        var3.add("/" + var13);
                    }
                    else
                    {
                        var3.add(var13);
                    }
                }
            }

            return var3;
        }
        else
        {
            String[] var4 = par2Str.split(" ", -1);
            String var5 = var4[var4.length - 1];
            String[] var6 = serverConfigManager.getAllUsernames();
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; ++var8)
            {
                String var9 = var6[var8];

                if (CommandBase.doesStringStartWith(var5, var9))
                {
                    var3.add(var9);
                }
            }

            return var3;
        }
    }

    /**
     * Gets mcServer.
     */
    public static MinecraftServer getServer()
    {
        return mcServer;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly
     * "Rcon")
     */
    public String getUsername()
    {
        return "Server";
    }

    /**
     * Executes a command as Server.
     */
    public static void executeCommand(String command)
    {
        getServer().getCommandManager().executeCommand(getServer(), command);
    }

    /**
     * Whispers to a player a message without logging it.
     */
    public static void anonymousTell(EntityPlayerMP player, String message)
    {
        // these stupid conflicting names are some real BULLSHIT
        // Get ahold of the "core" logger
        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger)logger;
        Level previousLevel = coreLogger.getLevel(); // Store the previous level
        coreLogger.setLevel(Level.OFF); // Disable logging
        tell(player, message); // Perform tell
        coreLogger.setLevel(previousLevel); // Re-enable logging
    }

    /**
     * Whispers to a player a message.
     */
    public static void tell(EntityPlayerMP player, String message)
    {
        executeCommand("/tell " + player.getUsername() + " " + message);
    }

    public static boolean isPlayerOpped(String playerName)
    {
        return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(playerName);
    }

    public static boolean isPlayerOpped(ICommandSender player)
    {
        return isPlayerOpped(player.getUsername()) || player == getServer();
    }

    public static boolean isPlayerOwner(String playerName)
    {
        return MinecraftServer.getServer().getConfigurationManager().isPlayerOwner(playerName);
    }

    public static boolean isPlayerOwner(ICommandSender player)
    {
        return isPlayerOwner(player.getUsername()) || player == getServer();
    }

    public static boolean isPlayerOppedOrCreative(EntityPlayerMP player)
    {
        return isPlayerOpped(player) || player.theItemInWorldManager.isCreative();
    }

    /**
     * Notifies this sender of some sort of information. This is for messages
     * intended to display to the user. Used for typical output (like
     * "you asked for whether or not this game rule is set, so here's your answer"
     * ), warnings (like "I fetched this block for you by ID, but I'd like you
     * to know that every time you do this, I die a little
     * inside"), and errors (like "it's not called iron_pixacke, silly").
     */
    public void addChatMessage(IChatComponent p_145747_1_)
    {
        logger.info(p_145747_1_.getUnformattedText());
    }

    public void addChatMessage(String message)
    {
        addChatMessage(new ChatComponentText(message));
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean canCommandSenderUseCommand(int par1, String par2Str)
    {
        return true;
    }

    public ICommandManager getCommandManager()
    {
        return commandManager;
    }

    /**
     * Gets KeyPair instanced in MinecraftServer.
     */
    public KeyPair getKeyPair()
    {
        return serverKeyPair;
    }

    /**
     * Gets serverPort.
     */
    public int getServerPort()
    {
        return serverPort;
    }

    public void setServerPort(int par1)
    {
        serverPort = par1;
    }

    /**
     * Returns the username of the server owner (for integrated servers)
     */
    public String getServerOwner()
    {
        return serverOwner;
    }

    /**
     * Sets the username of the owner of this server (in the case of an
     * integrated server)
     */
    public void setServerOwner(String par1Str)
    {
        serverOwner = par1Str;
    }

    public boolean isSinglePlayer()
    {
        return serverOwner != null;
    }

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(String par1Str)
    {
        folderName = par1Str;
    }

    public void setKeyPair(KeyPair par1KeyPair)
    {
        serverKeyPair = par1KeyPair;
    }

    public void func_147139_a(EnumDifficulty p_147139_1_)
    {
        for (int var2 = 0; var2 < worldServers.length; ++var2)
        {
            WorldServer var3 = worldServers[var2];

            if (var3 != null)
            {
                if (var3.getWorldInfo().isHardcoreModeEnabled())
                {
                    var3.difficultySetting = EnumDifficulty.HARD;
                    var3.setAllowedSpawnTypes(true, true);
                }
                else if (isSinglePlayer())
                {
                    var3.difficultySetting = p_147139_1_;
                    var3.setAllowedSpawnTypes(var3.difficultySetting != EnumDifficulty.PEACEFUL, true);
                }
                else
                {
                    var3.difficultySetting = p_147139_1_;
                    var3.setAllowedSpawnTypes(allowSpawnMonsters(), canSpawnAnimals);
                }
            }
        }
    }

    protected boolean allowSpawnMonsters()
    {
        return true;
    }

    /**
     * Gets whether this is a demo or not.
     */
    public boolean isDemo()
    {
        return isDemo;
    }

    /**
     * Sets whether this is a demo or not.
     */
    public void setDemo(boolean par1)
    {
        isDemo = par1;
    }

    public void canCreateBonusChest(boolean par1)
    {
        enableBonusChest = par1;
    }

    public ISaveFormat getActiveAnvilConverter()
    {
        return anvilConverterForAnvilFile;
    }

    /**
     * WARNING : directly calls
     * getActiveAnvilConverter().deleteWorldDirectory(theWorldServer
     * [0].getSaveHandler().getWorldDirectoryName());
     */
    public void deleteWorldAndStopServer()
    {
        worldIsBeingDeleted = true;
        getActiveAnvilConverter().flushCache();

        for (int var1 = 0; var1 < worldServers.length; ++var1)
        {
            WorldServer var2 = worldServers[var1];

            if (var2 != null)
            {
                var2.flush();
            }
        }

        getActiveAnvilConverter().deleteWorldDirectory(worldServers[0].getSaveHandler().getWorldDirectoryName());
        initiateShutdown();
    }

    public String func_147133_T()
    {
        return field_147141_M;
    }

    public void func_155759_m(String p_155759_1_)
    {
        field_147141_M = p_155759_1_;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        par1PlayerUsageSnooper.addData("whitelist_enabled", Boolean.valueOf(false));
        par1PlayerUsageSnooper.addData("whitelist_count", Integer.valueOf(0));
        par1PlayerUsageSnooper.addData("players_current", Integer.valueOf(getCurrentPlayerCount()));
        par1PlayerUsageSnooper.addData("players_max", Integer.valueOf(getMaxPlayers()));
        par1PlayerUsageSnooper.addData("players_seen", Integer.valueOf(serverConfigManager.getAvailablePlayerDat().length));
        par1PlayerUsageSnooper.addData("uses_auth", Boolean.valueOf(onlineMode));
        par1PlayerUsageSnooper.addData("gui_state", getGuiEnabled() ? "enabled" : "disabled");
        par1PlayerUsageSnooper.addData("run_time", Long.valueOf((getCurrentTimeMillis() - par1PlayerUsageSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L));
        par1PlayerUsageSnooper.addData("avg_tick_ms", Integer.valueOf((int)(MathHelper.average(tickTimeArray) * 1.0E-6D)));
        int var2 = 0;

        for (int var3 = 0; var3 < worldServers.length; ++var3)
        {
            if (worldServers[var3] != null)
            {
                WorldServer var4 = worldServers[var3];
                WorldInfo var5 = var4.getWorldInfo();
                par1PlayerUsageSnooper.addData("world[" + var2 + "][dimension]", Integer.valueOf(var4.provider.dimensionId));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][mode]", var5.getGameType());
                par1PlayerUsageSnooper.addData("world[" + var2 + "][difficulty]", var4.difficultySetting);
                par1PlayerUsageSnooper.addData("world[" + var2 + "][hardcore]", Boolean.valueOf(var5.isHardcoreModeEnabled()));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][generator_name]", var5.getTerrainType().getWorldTypeName());
                par1PlayerUsageSnooper.addData("world[" + var2 + "][generator_version]", Integer.valueOf(var5.getTerrainType().getGeneratorVersion()));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][height]", Integer.valueOf(buildLimit));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][chunks_loaded]", Integer.valueOf(var4.getChunkProvider().getLoadedChunkCount()));
                ++var2;
            }
        }

        par1PlayerUsageSnooper.addData("worlds", Integer.valueOf(var2));
    }

    public void addServerTypeToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        par1PlayerUsageSnooper.addData("singleplayer", Boolean.valueOf(isSinglePlayer()));
        par1PlayerUsageSnooper.addData("server_brand", getServerModName());
        par1PlayerUsageSnooper.addData("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
        par1PlayerUsageSnooper.addData("dedicated", Boolean.valueOf(isDedicatedServer()));
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return true;
    }

    public abstract boolean isDedicatedServer();

    public boolean isServerInOnlineMode()
    {
        return onlineMode;
    }

    public void setOnlineMode(boolean par1)
    {
        onlineMode = par1;
    }

    public boolean getCanSpawnAnimals()
    {
        return canSpawnAnimals;
    }

    public void setCanSpawnAnimals(boolean par1)
    {
        canSpawnAnimals = par1;
    }

    public boolean getCanSpawnNPCs()
    {
        return canSpawnNPCs;
    }

    public void setCanSpawnNPCs(boolean par1)
    {
        canSpawnNPCs = par1;
    }

    public boolean isPVPEnabled()
    {
        return pvpEnabled;
    }

    public void setAllowPvp(boolean par1)
    {
        pvpEnabled = par1;
    }

    public boolean isFlightAllowed()
    {
        return allowFlight;
    }

    public void setAllowFlight(boolean par1)
    {
        allowFlight = par1;
    }

    /**
     * Return whether command blocks are enabled.
     */
    public abstract boolean isCommandBlockEnabled();

    /**
     * Return if we should hide player IPs or not
     */
    public abstract boolean shouldLogIps();

    /**
     * Return if we should tell the player their IP or not
     */
    public abstract boolean shouldTellIp();

    /**
     * Return the leeway for a fastbreak detection.
     */
    public abstract double getFastbreakLeeway();

    /**
     * Returns the threshold for the ratio of fastbreak detections for a setback
     * and a message. 0.0 = perfect timing required, 1.0 = no protection
     */
    public abstract double getFastbreakRatioThreshold();

    /**
     * Return the threshold for kicking someone due to buildhack. A good default
     * is 6.
     */
    public abstract int getBuildhackThreshold();

    /**
     * Return the number of ticks you can be floating and not be set back
     */
    public abstract int getFloatingTicksThreshold();

    /**
     * Return the number of times you can be reset and not have it be logged
     */
    public abstract int getFlyResetLogThreshold();

    /**
     * Return the number of times you can be reset and not be kicked
     */
    public abstract int getFlyResetKickThreshold();

    /**
     * Whether diamond notifications are enabled or not
     */
    public abstract boolean useDiamondNotifications();

    /**
     * Return the number of ticks a player should take to regenerate health
     */
    public abstract int getHealthRegenTickCount();

    /**
     * Return the number of ticks a player should take to regenerate health
     */
    public abstract double getSpeedhackLeeway();

    /**
     * Return the threshold for the ratio for number of times a player moved too
     * quickly will be set back for
     */
    public abstract double getSpeedhackRatioKickThreshold();

    /**
     * Get the speed limit for a player with specific conditions.
     * @param sprinting whether the player is sprinting or not
     * @param jumping whether the player is jumping or not
     * @param potion whether the player has a speed potion or not
     * @return the speed limit for the player
     */
    public abstract double getSpeedLimit(boolean sprinting, boolean jumping, boolean potion);

    /**
     * Return the speed limit for a sneaking player.
     */
    public abstract double getSneakSpeedLimit();

    /**
     * Return the mode to check proxies with.
     */
    public abstract int getProxyCheckMode();

    /**
     * Return if we will check proxies or not.
     */
    public abstract boolean shouldCheckProxies();

    /**
     * Return if we will make kicks into tempbans.
     */
    public abstract boolean shouldKicksBeBans();

    /**
     * Return how long a tempban from a kick will last.
     */
    // Int is good enough because it's in minutes not ms
    public abstract int getKickTempbanLength();

    public String getMOTD()
    {
        return motd;
    }

    public void setMOTD(String par1Str)
    {
        motd = par1Str;
    }

    public int getBuildLimit()
    {
        return buildLimit;
    }

    public void setBuildLimit(int par1)
    {
        buildLimit = par1;
    }

    public boolean isServerStopped()
    {
        return serverStopped;
    }

    public ServerConfigurationManager getConfigurationManager()
    {
        return serverConfigManager;
    }

    public void setConfigurationManager(ServerConfigurationManager par1ServerConfigurationManager)
    {
        serverConfigManager = par1ServerConfigurationManager;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        for (int var2 = 0; var2 < worldServers.length; ++var2)
        {
            getServer().worldServers[var2].getWorldInfo().setGameType(par1EnumGameType);
        }
    }

    public NetworkSystem func_147137_ag()
    {
        return field_147144_o;
    }

    public boolean getGuiEnabled()
    {
        return false;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll,
     * gameType and allows external connections.
     */
    public abstract String shareToLAN(WorldSettings.GameType var1, boolean var2);

    public int getTickCounter()
    {
        return tickCounter;
    }

    public void enableProfiling()
    {
        startProfiling = true;
    }

    /**
     * Return the position for this command sender.
     */
    public ChunkCoordinates getCommandSenderPosition()
    {
        return new ChunkCoordinates(0, 0, 0);
    }

    public World getEntityWorld()
    {
        return worldServers[0];
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtectionSize()
    {
        return 16;
    }

    /**
     * Returns true if a player does not have permission to edit the block at
     * the given coordinates.
     */
    public boolean isBlockProtected(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        return false;
    }

    public void setForceGamemode(boolean par1)
    {
        isGamemodeForced = par1;
    }

    public boolean getForceGamemode()
    {
        return isGamemodeForced;
    }

    public Proxy getServerProxy()
    {
        return serverProxy;
    }

    public static long getCurrentTimeMillis()
    {
        return System.currentTimeMillis();
    }

    public int func_143007_ar()
    {
        return field_143008_E;
    }

    public void func_143006_e(int par1)
    {
        field_143008_E = par1;
    }

    public IChatComponent getUsernameAsIChatComponent()
    {
        return new ChatComponentText(getUsername());
    }

    public boolean func_147136_ar()
    {
        return true;
    }

    public MinecraftSessionService func_147130_as()
    {
        return field_147143_S;
    }

    public ServerStatusResponse func_147134_at()
    {
        return field_147147_p;
    }

    public void func_147132_au()
    {
        field_147142_T = 0L;
    }
}
