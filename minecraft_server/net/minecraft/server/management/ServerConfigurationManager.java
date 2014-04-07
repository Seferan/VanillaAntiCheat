package net.minecraft.server.management;

import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

public abstract class ServerConfigurationManager
{
    private static final Logger logger = LogManager.getLogger();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    /** Reference to the MinecraftServer object. */
    private final MinecraftServer mcServer;

    /** A list of player entities that exist on this server. */
    public final List playerEntityList = new ArrayList();
    private final BanList bannedPlayers = new BanList(new File("banned-players.txt"));
    private final BanList bannedIPs = new BanList(new File("banned-ips.txt"));

    /** A set containing the OPs. */
    private final Set ops = new HashSet();
    /** A set cointaining the owners. */
    private final Set owners = new HashSet();

    /** The Set of all whitelisted players. */
    private final Set whiteListedPlayers = new HashSet();
    private final Map field_148547_k = Maps.newHashMap();

    /** Reference to the PlayerNBTManager object. */
    private IPlayerFileData playerNBTManagerObj;

    /**
     * Server setting to only allow OPs and whitelisted players to join the
     * server.
     */
    private boolean whiteListEnforced;

    /** The maximum number of players that can be connected at a time. */
    protected int maxPlayers;
    protected int viewDistance;
    private WorldSettings.GameType gameType;

    /** True if all players are allowed to use commands (cheats). */
    private boolean commandsAllowedForAll;

    /**
     * index into playerEntities of player to ping, updated every tick;
     * currently hardcoded to max at 200 players
     */
    private int playerPingIndex;
    private static final String __OBFID = "CL_00001423";

    public ServerConfigurationManager(MinecraftServer par1MinecraftServer)
    {
        mcServer = par1MinecraftServer;
        bannedPlayers.setListActive(false);
        bannedIPs.setListActive(false);
        maxPlayers = 8;
    }

    public void initializeConnectionToPlayer(NetworkManager par1INetworkManager, EntityPlayerMP par2EntityPlayerMP)
    {
        NBTTagCompound var3 = readPlayerDataFromFile(par2EntityPlayerMP);
        par2EntityPlayerMP.setWorld(mcServer.worldServerForDimension(par2EntityPlayerMP.dimension));
        par2EntityPlayerMP.theItemInWorldManager.setWorld((WorldServer)par2EntityPlayerMP.worldObj);
        String var4 = "local";

        if (par1INetworkManager.getRemoteAddress() != null)
        {
            // Always uphold promise of not logging IPs regardless of config
            if (mcServer.shouldLogIps() && !mcServer.shouldTellIp())
                var4 = par1INetworkManager.getRemoteAddress().toString();
            else
                var4 = "redacted";
        }

        logger.info(par2EntityPlayerMP.getUsername() + "[" + var4 + "] logged in with entity id " + par2EntityPlayerMP.getEntityId() + " at (" + par2EntityPlayerMP.posX + ", " + par2EntityPlayerMP.posY + ", " + par2EntityPlayerMP.posZ + ")");
        WorldServer var5 = mcServer.worldServerForDimension(par2EntityPlayerMP.dimension);
        ChunkCoordinates var6 = var5.getSpawnPoint();
        func_72381_a(par2EntityPlayerMP, (EntityPlayerMP)null, var5);
        NetHandlerPlayServer var7 = new NetHandlerPlayServer(mcServer, par1INetworkManager, par2EntityPlayerMP);
        var7.sendPacket(new S01PacketJoinGame(par2EntityPlayerMP.getEntityId(), par2EntityPlayerMP.theItemInWorldManager.getGameType(), var5.getWorldInfo().isHardcoreModeEnabled(), var5.provider.dimensionId, var5.difficultySetting, getMaxPlayers(), var5.getWorldInfo().getTerrainType()));
        var7.sendPacket(new S3FPacketCustomPayload("MC|Brand", getServerInstance().getServerModName().getBytes(Charsets.UTF_8)));
        var7.sendPacket(new S05PacketSpawnPosition(var6.posX, var6.posY, var6.posZ));
        var7.sendPacket(new S39PacketPlayerAbilities(par2EntityPlayerMP.capabilities));
        var7.sendPacket(new S09PacketHeldItemChange(par2EntityPlayerMP.inventory.currentItem));
        par2EntityPlayerMP.func_147099_x().func_150877_d();
        par2EntityPlayerMP.func_147099_x().func_150884_b(par2EntityPlayerMP);
        func_96456_a((ServerScoreboard)var5.getScoreboard(), par2EntityPlayerMP);
        mcServer.func_147132_au();
        ChatComponentTranslation var8 = new ChatComponentTranslation("multiplayer.player.joined", new Object[] {par2EntityPlayerMP.getUsernameAsIChatComponent()});
        var8.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        func_148539_a(var8);
        playerLoggedIn(par2EntityPlayerMP);
        var7.setPlayerLocation(par2EntityPlayerMP.posX, par2EntityPlayerMP.posY, par2EntityPlayerMP.posZ, par2EntityPlayerMP.rotationYaw, par2EntityPlayerMP.rotationPitch);
        updateTimeAndWeatherForPlayer(par2EntityPlayerMP, var5);

        if (mcServer.func_147133_T().length() > 0)
        {
            par2EntityPlayerMP.func_147095_a(mcServer.func_147133_T());
        }

        Iterator var9 = par2EntityPlayerMP.getActivePotionEffects().iterator();

        while (var9.hasNext())
        {
            PotionEffect var10 = (PotionEffect)var9.next();
            var7.sendPacket(new S1DPacketEntityEffect(par2EntityPlayerMP.getEntityId(), var10));
        }

        par2EntityPlayerMP.addSelfToInternalCraftingInventory();

        if (var3 != null && var3.func_150297_b("Riding", 10))
        {
            Entity var11 = EntityList.createEntityFromNBT(var3.getCompoundTag("Riding"), var5);

            if (var11 != null)
            {
                var11.forceSpawn = true;
                var5.spawnEntityInWorld(var11);
                par2EntityPlayerMP.mountEntity(var11);
                var11.forceSpawn = false;
            }
        }
    }

    protected void func_96456_a(ServerScoreboard par1ServerScoreboard, EntityPlayerMP par2EntityPlayerMP)
    {
        HashSet var3 = new HashSet();
        Iterator var4 = par1ServerScoreboard.getTeams().iterator();

        while (var4.hasNext())
        {
            ScorePlayerTeam var5 = (ScorePlayerTeam)var4.next();
            par2EntityPlayerMP.playerNetServerHandler.sendPacket(new S3EPacketTeams(var5, 0));
        }

        for (int var9 = 0; var9 < 3; ++var9)
        {
            ScoreObjective var10 = par1ServerScoreboard.func_96539_a(var9);

            if (var10 != null && !var3.contains(var10))
            {
                List var6 = par1ServerScoreboard.func_96550_d(var10);
                Iterator var7 = var6.iterator();

                while (var7.hasNext())
                {
                    Packet var8 = (Packet)var7.next();
                    par2EntityPlayerMP.playerNetServerHandler.sendPacket(var8);
                }

                var3.add(var10);
            }
        }
    }

    /**
     * Sets the NBT manager to the one for the WorldServer given.
     */
    public void setPlayerManager(WorldServer[] par1ArrayOfWorldServer)
    {
        playerNBTManagerObj = par1ArrayOfWorldServer[0].getSaveHandler().getPlayerNBTManager();
    }

    public void func_72375_a(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        WorldServer var3 = par1EntityPlayerMP.getServerForPlayer();

        if (par2WorldServer != null)
        {
            par2WorldServer.getPlayerManager().removePlayer(par1EntityPlayerMP);
        }

        var3.getPlayerManager().addPlayer(par1EntityPlayerMP);
        var3.theChunkProviderServer.loadChunk((int)par1EntityPlayerMP.posX >> 4, (int)par1EntityPlayerMP.posZ >> 4);
    }

    public int getEntityViewDistance()
    {
        return PlayerManager.getFurthestViewableBlock(getViewDistance());
    }

    /**
     * called during player login. reads the player information from disk.
     */
    public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP par1EntityPlayerMP)
    {
        NBTTagCompound var2 = mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
        NBTTagCompound var3;

        if (par1EntityPlayerMP.getUsername().equals(mcServer.getServerOwner()) && var2 != null)
        {
            par1EntityPlayerMP.readFromNBT(var2);
            var3 = var2;
            logger.debug("loading single player");
        }
        else
        {
            var3 = playerNBTManagerObj.readPlayerData(par1EntityPlayerMP);
        }

        return var3;
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP par1EntityPlayerMP)
    {
        playerNBTManagerObj.writePlayerData(par1EntityPlayerMP);
        StatisticsFile var2 = (StatisticsFile)field_148547_k.get(par1EntityPlayerMP.getUsername());

        if (var2 != null)
        {
            var2.func_150883_b();
        }
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk
     * and inserts the player into the world.
     */
    public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP)
    {
        sendPacketToAllPlayers(new S38PacketPlayerListItem(par1EntityPlayerMP.getUsername(), true, 1000));
        playerEntityList.add(par1EntityPlayerMP);
        WorldServer var2 = mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        var2.spawnEntityInWorld(par1EntityPlayerMP);
        func_72375_a(par1EntityPlayerMP, (WorldServer)null);

        for (int var3 = 0; var3 < playerEntityList.size(); ++var3)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)playerEntityList.get(var3);
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(var4.getUsername(), true, var4.ping));
        }

        if (mcServer.shouldTellIp())
        {
            String ip = par1EntityPlayerMP.getPlayerIP();
            MinecraftServer.anonymousTell(par1EntityPlayerMP, "Welcome! Your external IP appears to be: " + ip);
            MinecraftServer.anonymousTell(par1EntityPlayerMP, "This has not been logged, nor will anyone else recieve this message.");
            MinecraftServer.anonymousTell(par1EntityPlayerMP, "Have a good day!");
        }
        if (mcServer.getConfigurationManager().getMotd().length > 0) par1EntityPlayerMP.addChatMessage("Welcome! Please read the motd using /motd.");
    }

    /**
     * using player's dimension, update their movement when in a vehicle (e.g.
     * cart, boat)
     */
    public void serverUpdateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(par1EntityPlayerMP);
    }

    /**
     * Called when a player disconnects from the game. Writes player data to
     * disk and removes them from the world.
     */
    public void playerLoggedOut(EntityPlayerMP par1EntityPlayerMP)
    {
        par1EntityPlayerMP.triggerAchievement(StatList.leaveGameStat);
        writePlayerData(par1EntityPlayerMP);
        WorldServer var2 = par1EntityPlayerMP.getServerForPlayer();

        if (par1EntityPlayerMP.ridingEntity != null)
        {
            var2.removePlayerEntityDangerously(par1EntityPlayerMP.ridingEntity);
            logger.debug("removing player mount");
        }

        var2.removeEntity(par1EntityPlayerMP);
        var2.getPlayerManager().removePlayer(par1EntityPlayerMP);
        playerEntityList.remove(par1EntityPlayerMP);
        field_148547_k.remove(par1EntityPlayerMP.getUsername());
        sendPacketToAllPlayers(new S38PacketPlayerListItem(par1EntityPlayerMP.getUsername(), false, 9999));
    }

    public String func_148542_a(SocketAddress p_148542_1_, GameProfile p_148542_2_)
    {
        if (bannedPlayers.isBanned(p_148542_2_.getName()))
        {
            BanEntry var6 = (BanEntry)bannedPlayers.getBannedList().get(p_148542_2_.getName());
            String var7 = "You are banned from this server!\nReason: " + var6.getBanReason();

            if (var6.getBanEndDate() != null)
            {
                var7 = var7 + "\nYour ban will be removed on " + dateFormat.format(var6.getBanEndDate());
            }

            return var7;
        }
        else if (!isAllowedToLogin(p_148542_2_.getName()))
        {
            return "You are not white-listed on this server!";
        }
        else
        {
            String var3 = p_148542_1_.toString();
            var3 = var3.substring(var3.indexOf("/") + 1);
            var3 = var3.substring(0, var3.indexOf(":"));

            if (bannedIPs.isBanned(var3))
            {
                BanEntry var4 = (BanEntry)bannedIPs.getBannedList().get(var3);
                String var5 = "Your IP address is banned from this server!\nReason: " + var4.getBanReason();

                if (var4.getBanEndDate() != null)
                {
                    var5 = var5 + "\nYour ban will be removed on " + dateFormat.format(var4.getBanEndDate());
                }

                return var5;
            }
            else
            {
                return playerEntityList.size() >= maxPlayers ? "The server is full!" : null;
            }
        }
    }

    public EntityPlayerMP func_148545_a(GameProfile p_148545_1_)
    {
        ArrayList var2 = new ArrayList();
        EntityPlayerMP var4;

        for (int var3 = 0; var3 < playerEntityList.size(); ++var3)
        {
            var4 = (EntityPlayerMP)playerEntityList.get(var3);

            if (var4.getUsername().equalsIgnoreCase(p_148545_1_.getName()))
            {
                var2.add(var4);
            }
        }

        Iterator var5 = var2.iterator();

        while (var5.hasNext())
        {
            var4 = (EntityPlayerMP)var5.next();
            var4.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
        }

        Object var6;

        if (mcServer.isDemo())
        {
            var6 = new DemoWorldManager(mcServer.worldServerForDimension(0));
        }
        else
        {
            var6 = new ItemInWorldManager(mcServer.worldServerForDimension(0));
        }

        return new EntityPlayerMP(mcServer, mcServer.worldServerForDimension(0), p_148545_1_, (ItemInWorldManager)var6);
    }

    /**
     * Called on respawn
     */
    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3)
    {
        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().untrackEntity(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().removePlayer(par1EntityPlayerMP);
        playerEntityList.remove(par1EntityPlayerMP);
        mcServer.worldServerForDimension(par1EntityPlayerMP.dimension).removePlayerEntityDangerously(par1EntityPlayerMP);
        ChunkCoordinates var4 = par1EntityPlayerMP.getBedLocation();
        boolean var5 = par1EntityPlayerMP.isSpawnForced();
        par1EntityPlayerMP.dimension = par2;
        Object var6;

        if (mcServer.isDemo())
        {
            var6 = new DemoWorldManager(mcServer.worldServerForDimension(par1EntityPlayerMP.dimension));
        }
        else
        {
            var6 = new ItemInWorldManager(mcServer.worldServerForDimension(par1EntityPlayerMP.dimension));
        }

        EntityPlayerMP var7 = new EntityPlayerMP(mcServer, mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), par1EntityPlayerMP.getGameProfile(), (ItemInWorldManager)var6);
        var7.playerNetServerHandler = par1EntityPlayerMP.playerNetServerHandler;
        var7.clonePlayer(par1EntityPlayerMP, par3);
        var7.setEntityId(par1EntityPlayerMP.getEntityId());
        WorldServer var8 = mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        func_72381_a(var7, par1EntityPlayerMP, var8);
        ChunkCoordinates var9;

        if (var4 != null)
        {
            var9 = EntityPlayer.verifyRespawnCoordinates(mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), var4, var5);

            if (var9 != null)
            {
                var7.setLocationAndAngles(var9.posX + 0.5F, var9.posY + 0.1F, var9.posZ + 0.5F, 0.0F, 0.0F);
                var7.setSpawnChunk(var4, var5);
            }
            else
            {
                var7.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
            }
        }

        var8.theChunkProviderServer.loadChunk((int)var7.posX >> 4, (int)var7.posZ >> 4);

        while (!var8.getCollidingBoundingBoxes(var7, var7.boundingBox).isEmpty())
        {
            var7.setPosition(var7.posX, var7.posY + 1.0D, var7.posZ);
        }

        var7.playerNetServerHandler.sendPacket(new S07PacketRespawn(var7.dimension, var7.worldObj.difficultySetting, var7.worldObj.getWorldInfo().getTerrainType(), var7.theItemInWorldManager.getGameType()));
        var9 = var8.getSpawnPoint();
        var7.playerNetServerHandler.setPlayerLocation(var7.posX, var7.posY, var7.posZ, var7.rotationYaw, var7.rotationPitch);
        var7.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(var9.posX, var9.posY, var9.posZ));
        var7.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(var7.experience, var7.experienceTotal, var7.experienceLevel));
        updateTimeAndWeatherForPlayer(var7, var8);
        var8.getPlayerManager().addPlayer(var7);
        var8.spawnEntityInWorld(var7);
        playerEntityList.add(var7);
        var7.addSelfToInternalCraftingInventory();
        var7.setHealth(var7.getHealth());
        return var7;
    }

    /**
     * moves provided player from overworld to nether or vice versa
     */
    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2)
    {
        int var3 = par1EntityPlayerMP.dimension;
        WorldServer var4 = mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.dimension = par2;
        WorldServer var5 = mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S07PacketRespawn(par1EntityPlayerMP.dimension, par1EntityPlayerMP.worldObj.difficultySetting, par1EntityPlayerMP.worldObj.getWorldInfo().getTerrainType(), par1EntityPlayerMP.theItemInWorldManager.getGameType()));
        var4.removePlayerEntityDangerously(par1EntityPlayerMP);
        par1EntityPlayerMP.isDead = false;
        transferEntityToWorld(par1EntityPlayerMP, var3, var4, var5);
        func_72375_a(par1EntityPlayerMP, var4);
        par1EntityPlayerMP.playerNetServerHandler.setPlayerLocation(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
        par1EntityPlayerMP.theItemInWorldManager.setWorld(var5);
        updateTimeAndWeatherForPlayer(par1EntityPlayerMP, var5);
        syncPlayerInventory(par1EntityPlayerMP);
        Iterator var6 = par1EntityPlayerMP.getActivePotionEffects().iterator();

        while (var6.hasNext())
        {
            PotionEffect var7 = (PotionEffect)var6.next();
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(par1EntityPlayerMP.getEntityId(), var7));
        }
    }

    /**
     * Transfers an entity from a world to another world.
     */
    public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer)
    {
        double var5 = par1Entity.posX;
        double var7 = par1Entity.posZ;
        double var9 = 8.0D;
        double var11 = par1Entity.posX;
        double var13 = par1Entity.posY;
        double var15 = par1Entity.posZ;
        float var17 = par1Entity.rotationYaw;
        par3WorldServer.theProfiler.startSection("moving");

        if (par1Entity.dimension == -1)
        {
            var5 /= var9;
            var7 /= var9;
            par1Entity.setLocationAndAngles(var5, par1Entity.posY, var7, par1Entity.rotationYaw, par1Entity.rotationPitch);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }
        else if (par1Entity.dimension == 0)
        {
            var5 *= var9;
            var7 *= var9;
            par1Entity.setLocationAndAngles(var5, par1Entity.posY, var7, par1Entity.rotationYaw, par1Entity.rotationPitch);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }
        else
        {
            ChunkCoordinates var18;

            if (par2 == 1)
            {
                var18 = par4WorldServer.getSpawnPoint();
            }
            else
            {
                var18 = par4WorldServer.getEntrancePortalLocation();
            }

            var5 = var18.posX;
            par1Entity.posY = var18.posY;
            var7 = var18.posZ;
            par1Entity.setLocationAndAngles(var5, par1Entity.posY, var7, 90.0F, 0.0F);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }

        par3WorldServer.theProfiler.endSection();

        if (par2 != 1)
        {
            par3WorldServer.theProfiler.startSection("placing");
            var5 = MathHelper.clamp_int((int)var5, -29999872, 29999872);
            var7 = MathHelper.clamp_int((int)var7, -29999872, 29999872);

            if (par1Entity.isEntityAlive())
            {
                par1Entity.setLocationAndAngles(var5, par1Entity.posY, var7, par1Entity.rotationYaw, par1Entity.rotationPitch);
                par4WorldServer.getDefaultTeleporter().placeInPortal(par1Entity, var11, var13, var15, var17);
                par4WorldServer.spawnEntityInWorld(par1Entity);
                par4WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }

            par3WorldServer.theProfiler.endSection();
        }

        par1Entity.setWorld(par4WorldServer);
    }

    /**
     * self explanitory
     */
    public void onTick()
    {
        if (++playerPingIndex > 600)
        {
            playerPingIndex = 0;
        }

        if (playerPingIndex < playerEntityList.size())
        {
            EntityPlayerMP var1 = (EntityPlayerMP)playerEntityList.get(playerPingIndex);
            sendPacketToAllPlayers(new S38PacketPlayerListItem(var1.getUsername(), true, var1.ping));
        }
    }

    public void sendPacketToAllPlayers(Packet p_148540_1_)
    {
        for (int var2 = 0; var2 < playerEntityList.size(); ++var2)
        {
            ((EntityPlayerMP)playerEntityList.get(var2)).playerNetServerHandler.sendPacket(p_148540_1_);
        }
    }

    public void sendPacketToAllPlayersInDimension(Packet p_148537_1_, int p_148537_2_)
    {
        for (int var3 = 0; var3 < playerEntityList.size(); ++var3)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)playerEntityList.get(var3);

            if (var4.dimension == p_148537_2_)
            {
                var4.playerNetServerHandler.sendPacket(p_148537_1_);
            }
        }
    }

    /**
     * returns a string containing a comma-seperated list of player names
     */
    public String getPlayerListAsString()
    {
        String var1 = "";

        for (int var2 = 0; var2 < playerEntityList.size(); ++var2)
        {
            if (var2 > 0)
            {
                var1 = var1 + ", ";
            }

            var1 = var1 + ((EntityPlayerMP)playerEntityList.get(var2)).getUsername();
        }

        return var1;
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getAllUsernames()
    {
        String[] var1 = new String[playerEntityList.size()];

        for (int var2 = 0; var2 < playerEntityList.size(); ++var2)
        {
            var1[var2] = ((EntityPlayerMP)playerEntityList.get(var2)).getUsername();
        }

        return var1;
    }

    public BanList getBannedPlayers()
    {
        return bannedPlayers;
    }

    public BanList getBannedIPs()
    {
        return bannedIPs;
    }

    /**
     * This adds a username to the ops list, then saves the op list
     */
    public void addOp(String par1Str)
    {
        ops.add(par1Str.toLowerCase());
    }

    /**
     * This removes a username from the ops list, then saves the op list
     */
    public void removeOp(String par1Str)
    {
        ops.remove(par1Str.toLowerCase());
    }

    /**
     * Determine if the player is allowed to connect based on current server
     * settings.
     */
    public boolean isAllowedToLogin(String par1Str)
    {
        par1Str = par1Str.trim().toLowerCase();
        return !whiteListEnforced || ops.contains(par1Str) || whiteListedPlayers.contains(par1Str);
    }

    /**
     * Returns true if the specified player is opped, even if they're currently
     * offline.
     */
    public boolean isPlayerOpped(String name)
    {
        return ops.contains(name.trim().toLowerCase()) || isPlayerPossibleOp(name);
    }

    public void addOwner(String par1Str)
    {
        owners.add(par1Str.toLowerCase());
    }

    public void removeOwner(String par1Str)
    {
        owners.remove(par1Str.toLowerCase());
    }

    /**
     * Returns true if the specified player is an owner.
     */
    public boolean isPlayerOwner(String name)
    {
        return owners.contains(name.trim().toLowerCase()) || isPlayerPossibleOp(name);
    }

    /**
     * Returns true if a player can be op (offline or not, are commands allowed,
     * etc)
     */
    public boolean isPlayerPossibleOp(String name)
    {
        return mcServer.isSinglePlayer() && mcServer.worldServers[0].getWorldInfo().areCommandsAllowed() && mcServer.getServerOwner().equalsIgnoreCase(name) || commandsAllowedForAll;
    }

    /**
     * gets the player entity for the player with the name specified
     */
    public EntityPlayerMP getPlayerEntity(String par1Str)
    {
        Iterator var2 = playerEntityList.iterator();
        EntityPlayerMP var3;

        do
        {
            if (!var2.hasNext()) { return null; }

            var3 = (EntityPlayerMP)var2.next();
        } while (!var3.getUsername().equalsIgnoreCase(par1Str));

        return var3;
    }

    /**
     * Find all players in a specified range and narrowing down by other
     * parameters
     */
    public List findPlayers(ChunkCoordinates par1ChunkCoordinates, int par2, int par3, int par4, int par5, int par6, int par7, Map par8Map, String par9Str, String par10Str, World par11World)
    {
        if (playerEntityList.isEmpty())
        {
            return null;
        }
        else
        {
            Object var12 = new ArrayList();
            boolean var13 = par4 < 0;
            boolean var14 = par9Str != null && par9Str.startsWith("!");
            boolean var15 = par10Str != null && par10Str.startsWith("!");
            int var16 = par2 * par2;
            int var17 = par3 * par3;
            par4 = MathHelper.abs_int(par4);

            if (var14)
            {
                par9Str = par9Str.substring(1);
            }

            if (var15)
            {
                par10Str = par10Str.substring(1);
            }

            for (int var18 = 0; var18 < playerEntityList.size(); ++var18)
            {
                EntityPlayerMP var19 = (EntityPlayerMP)playerEntityList.get(var18);

                if ((par11World == null || var19.worldObj == par11World) && (par9Str == null || var14 != par9Str.equalsIgnoreCase(var19.getUsername())))
                {
                    if (par10Str != null)
                    {
                        Team var20 = var19.getTeam();
                        String var21 = var20 == null ? "" : var20.getRegisteredName();

                        if (var15 == par10Str.equalsIgnoreCase(var21))
                        {
                            continue;
                        }
                    }

                    if (par1ChunkCoordinates != null && (par2 > 0 || par3 > 0))
                    {
                        float var22 = par1ChunkCoordinates.getDistanceSquaredToChunkCoordinates(var19.getCommandSenderPosition());

                        if (par2 > 0 && var22 < var16 || par3 > 0 && var22 > var17)
                        {
                            continue;
                        }
                    }

                    if (func_96457_a(var19, par8Map) && (par5 == WorldSettings.GameType.NOT_SET.getID() || par5 == var19.theItemInWorldManager.getGameType().getID()) && (par6 <= 0 || var19.experienceLevel >= par6) && var19.experienceLevel <= par7)
                    {
                        ((List)var12).add(var19);
                    }
                }
            }

            if (par1ChunkCoordinates != null)
            {
                Collections.sort((List)var12, new PlayerPositionComparator(par1ChunkCoordinates));
            }

            if (var13)
            {
                Collections.reverse((List)var12);
            }

            if (par4 > 0)
            {
                var12 = ((List)var12).subList(0, Math.min(par4, ((List)var12).size()));
            }

            return (List)var12;
        }
    }

    private boolean func_96457_a(EntityPlayer par1EntityPlayer, Map par2Map)
    {
        if (par2Map != null && par2Map.size() != 0)
        {
            Iterator var3 = par2Map.entrySet().iterator();
            Entry var4;
            boolean var6;
            int var10;

            do
            {
                if (!var3.hasNext()) { return true; }

                var4 = (Entry)var3.next();
                String var5 = (String)var4.getKey();
                var6 = false;

                if (var5.endsWith("_min") && var5.length() > 4)
                {
                    var6 = true;
                    var5 = var5.substring(0, var5.length() - 4);
                }

                Scoreboard var7 = par1EntityPlayer.getWorldScoreboard();
                ScoreObjective var8 = var7.getObjective(var5);

                if (var8 == null) { return false; }

                Score var9 = par1EntityPlayer.getWorldScoreboard().func_96529_a(par1EntityPlayer.getUsername(), var8);
                var10 = var9.getScorePoints();

                if (var10 < ((Integer)var4.getValue()).intValue() && var6) { return false; }
            } while (var10 <= ((Integer)var4.getValue()).intValue() || var6);

            return false;
        }
        else
        {
            return true;
        }
    }

    public void func_148541_a(double p_148541_1_, double p_148541_3_, double p_148541_5_, double p_148541_7_, int p_148541_9_, Packet p_148541_10_)
    {
        func_148543_a((EntityPlayer)null, p_148541_1_, p_148541_3_, p_148541_5_, p_148541_7_, p_148541_9_, p_148541_10_);
    }

    public void func_148543_a(EntityPlayer p_148543_1_, double p_148543_2_, double p_148543_4_, double p_148543_6_, double p_148543_8_, int p_148543_10_, Packet p_148543_11_)
    {
        for (int var12 = 0; var12 < playerEntityList.size(); ++var12)
        {
            EntityPlayerMP var13 = (EntityPlayerMP)playerEntityList.get(var12);

            if (var13 != p_148543_1_ && var13.dimension == p_148543_10_)
            {
                double var14 = p_148543_2_ - var13.posX;
                double var16 = p_148543_4_ - var13.posY;
                double var18 = p_148543_6_ - var13.posZ;

                if (var14 * var14 + var16 * var16 + var18 * var18 < p_148543_8_ * p_148543_8_)
                {
                    var13.playerNetServerHandler.sendPacket(p_148543_11_);
                }
            }
        }
    }

    /**
     * Saves all of the players' current states.
     */
    public void saveAllPlayerData()
    {
        for (int var1 = 0; var1 < playerEntityList.size(); ++var1)
        {
            writePlayerData((EntityPlayerMP)playerEntityList.get(var1));
        }
    }

    /**
     * Add the specified player to the white list.
     */
    public void addToWhiteList(String par1Str)
    {
        whiteListedPlayers.add(par1Str);
    }

    /**
     * Remove the specified player from the whitelist.
     */
    public void removeFromWhitelist(String par1Str)
    {
        whiteListedPlayers.remove(par1Str);
    }

    /**
     * Returns the whitelisted players.
     */
    public Set getWhiteListedPlayers()
    {
        return whiteListedPlayers;
    }

    public Set getOps()
    {
        return ops;
    }

    public Set getOwners()
    {
        return owners;
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void loadWhiteList()
    {
    }

    /**
     * Updates the time and weather for the given player to those of the given
     * world
     */
    public void updateTimeAndWeatherForPlayer(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S03PacketTimeUpdate(par2WorldServer.getTotalWorldTime(), par2WorldServer.getWorldTime(), par2WorldServer.getGameRules().getGameRuleBooleanValue("doDaylightCycle")));

        if (par2WorldServer.isRaining())
        {
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, par2WorldServer.getRainStrength(1.0F)));
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, par2WorldServer.getWeightedThunderStrength(1.0F)));
        }
    }

    /**
     * sends the players inventory to himself
     */
    public void syncPlayerInventory(EntityPlayerMP par1EntityPlayerMP)
    {
        par1EntityPlayerMP.sendContainerToPlayer(par1EntityPlayerMP.inventoryContainer);
        par1EntityPlayerMP.setPlayerHealthUpdated();
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(par1EntityPlayerMP.inventory.currentItem));
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount()
    {
        return playerEntityList.size();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getAvailablePlayerDat()
    {
        return mcServer.worldServers[0].getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
    }

    public boolean isWhiteListEnabled()
    {
        return whiteListEnforced;
    }

    public void setWhiteListEnabled(boolean par1)
    {
        whiteListEnforced = par1;
    }

    public List getPlayerList(String par1Str)
    {
        ArrayList var2 = new ArrayList();
        Iterator var3 = playerEntityList.iterator();

        while (var3.hasNext())
        {
            EntityPlayerMP var4 = (EntityPlayerMP)var3.next();

            if (var4.getPlayerIP().equals(par1Str))
            {
                var2.add(var4);
            }
        }

        return var2;
    }

    /**
     * Gets the View Distance.
     */
    public int getViewDistance()
    {
        return viewDistance;
    }

    public MinecraftServer getServerInstance()
    {
        return mcServer;
    }

    /**
     * On integrated servers, returns the host's player data to be written to
     * level.dat.
     */
    public NBTTagCompound getHostPlayerData()
    {
        return null;
    }

    private void func_72381_a(EntityPlayerMP par1EntityPlayerMP, EntityPlayerMP par2EntityPlayerMP, World par3World)
    {
        if (par2EntityPlayerMP != null)
        {
            par1EntityPlayerMP.theItemInWorldManager.setGameType(par2EntityPlayerMP.theItemInWorldManager.getGameType());
        }
        else if (gameType != null)
        {
            par1EntityPlayerMP.theItemInWorldManager.setGameType(gameType);
        }

        par1EntityPlayerMP.theItemInWorldManager.initializeGameType(par3World.getWorldInfo().getGameType());
    }

    /**
     * Kicks everyone with "Server closed" as reason.
     */
    public void removeAllPlayers()
    {
        for (int var1 = 0; var1 < playerEntityList.size(); ++var1)
        {
            ((EntityPlayerMP)playerEntityList.get(var1)).playerNetServerHandler.kickPlayerFromServer("Server closed");
        }
    }

    /**
     * Sends a chat message to all players and logs it.
     * 
     * @param p_148544_1_
     * @param p_148544_2_
     */
    public void sendChatMessageToAllPlayersAndLog(IChatComponent p_148544_1_, boolean p_148544_2_)
    {
        mcServer.addChatMessage(p_148544_1_); // log it
        sendPacketToAllPlayers(new S02PacketChat(p_148544_1_, p_148544_2_)); // send
                                                                             // it
    }

    public void func_148539_a(IChatComponent p_148539_1_)
    {
        sendChatMessageToAllPlayersAndLog(p_148539_1_, true);
    }

    public StatisticsFile func_148538_i(String p_148538_1_)
    {
        StatisticsFile var2 = (StatisticsFile)field_148547_k.get(p_148538_1_);

        if (var2 == null)
        {
            var2 = new StatisticsFile(mcServer, new File(mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats/" + p_148538_1_ + ".json"));
            var2.func_150882_a();
            field_148547_k.put(p_148538_1_, var2);
        }

        return var2;
    }

    public abstract String[] getMotd();
}
