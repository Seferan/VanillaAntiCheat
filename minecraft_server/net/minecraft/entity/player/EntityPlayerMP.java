package net.minecraft.entity.player;

import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mx.x10.afffsdd.vanillaanticheat.module.VACState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

public class EntityPlayerMP extends EntityPlayer implements ICrafting
{
    private static final Logger logger = LogManager.getLogger();
    private String translator = "en_US";

    /**
     * The NetServerHandler assigned to this player by the
     * ServerConfigurationManager.
     */
    public NetHandlerPlayServer playerNetServerHandler;

    /** Reference to the MinecraftServer object. */
    public final MinecraftServer mcServer;

    /** The ItemInWorldManager belonging to this player */
    public final ItemInWorldManager theItemInWorldManager;

    /** player X position as seen by PlayerManager */
    public double managedPosX;

    /** player Z position as seen by PlayerManager */
    public double managedPosZ;

    /** LinkedList that holds the loaded chunks. */
    public final List loadedChunks = new LinkedList();

    /** entities added to this list will be packet29'd to the player */
    public final List destroyedItemsNetCache = new LinkedList();
    private final StatisticsFile field_147103_bO;
    private float field_130068_bO = Float.MIN_VALUE;

    /** amount of health the client was last set to */
    private float lastHealth = -1.0E8F;

    /** set to foodStats.GetFoodLevel */
    private int lastFoodLevel = -99999999;

    /** set to foodStats.getSaturationLevel() == 0.0F each tick */
    private boolean wasHungry = true;

    /** Amount of experience the client was last set to */
    private int lastExperience = -99999999;
    private int field_147101_bU = 60;

    /** must be between 3>x>15 (strictly between) */
    private int renderDistance;
    private EntityPlayer.EnumChatVisibility chatVisibility;
    private boolean chatColours = true;
    private long field_143005_bX = 0L;

    /**
     * The currently in use window ID. Incremented every time a window is
     * opened.
     */
    private int currentWindowId;

    /**
     * set to true when player is moving quantity of items from one inventory to
     * another(crafting) but item in either slot is not changed
     */
    public boolean isChangingQuantityOnly;
    public int ping;

    /**
     * Set when a player beats the ender dragon, used to respawn the player at
     * the spawn point while retaining inventory and XP
     */
    public boolean playerConqueredTheEnd;
    private static final String __OBFID = "CL_00001440";

    public EntityPlayerMP(MinecraftServer p_i45285_1_, WorldServer p_i45285_2_, GameProfile p_i45285_3_, ItemInWorldManager p_i45285_4_)
    {
        super(p_i45285_2_, p_i45285_3_);
        p_i45285_4_.thisPlayerMP = this;
        theItemInWorldManager = p_i45285_4_;
        renderDistance = p_i45285_1_.getConfigurationManager().getViewDistance();
        ChunkCoordinates var5 = p_i45285_2_.getSpawnPoint();
        int var6 = var5.posX;
        int var7 = var5.posZ;
        int var8 = var5.posY;

        if (!p_i45285_2_.provider.hasNoSky && p_i45285_2_.getWorldInfo().getGameType() != WorldSettings.GameType.ADVENTURE)
        {
            int var9 = Math.max(5, p_i45285_1_.getSpawnProtectionSize() - 6);
            var6 += rand.nextInt(var9 * 2) - var9;
            var7 += rand.nextInt(var9 * 2) - var9;
            var8 = p_i45285_2_.getTopSolidOrLiquidBlock(var6, var7);
        }

        mcServer = p_i45285_1_;
        field_147103_bO = p_i45285_1_.getConfigurationManager().func_148538_i(getUsername());
        stepHeight = 0.0F;
        yOffset = 0.0F;
        setLocationAndAngles(var6 + 0.5D, var8, var7 + 0.5D, 0.0F, 0.0F);

        while (!p_i45285_2_.getCollidingBoundingBoxes(this, boundingBox).isEmpty())
        {
            setPosition(posX, posY + 1.0D, posZ);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.func_150297_b("playerGameType", 99))
        {
            if (MinecraftServer.getServer().getForceGamemode())
            {
                theItemInWorldManager.setGameType(MinecraftServer.getServer().getGameType());
            }
            else
            {
                theItemInWorldManager.setGameType(WorldSettings.GameType.getByID(par1NBTTagCompound.getInteger("playerGameType")));
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("playerGameType", theItemInWorldManager.getGameType().getID());
    }

    /**
     * Add experience levels to this player.
     */
    public void addExperienceLevel(int par1)
    {
        super.addExperienceLevel(par1);
        lastExperience = -1;
    }

    public void addSelfToInternalCraftingInventory()
    {
        openContainer.onCraftGuiOpened(this);
    }

    /**
     * sets the players height back to normal after doing things like sleeping
     * and dieing
     */
    protected void resetHeight()
    {
        yOffset = 0.0F;
    }

    public float getEyeHeight()
    {
        return 1.62F;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        theItemInWorldManager.updateBlockRemoving();
        --field_147101_bU;

        if (hurtResistantTime > 0)
        {
            --hurtResistantTime;
        }

        openContainer.detectAndSendChanges();

        if (!worldObj.isClient && !openContainer.canInteractWith(this))
        {
            closeScreen();
            openContainer = inventoryContainer;
        }

        while (!destroyedItemsNetCache.isEmpty())
        {
            int var1 = Math.min(destroyedItemsNetCache.size(), 127);
            int[] var2 = new int[var1];
            Iterator var3 = destroyedItemsNetCache.iterator();
            int var4 = 0;

            while (var3.hasNext() && var4 < var1)
            {
                var2[var4++] = ((Integer)var3.next()).intValue();
                var3.remove();
            }

            playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(var2));
        }

        if (!loadedChunks.isEmpty())
        {
            ArrayList var6 = new ArrayList();
            Iterator var7 = loadedChunks.iterator();
            ArrayList var8 = new ArrayList();
            Chunk var5;

            while (var7.hasNext() && var6.size() < S26PacketMapChunkBulk.func_149258_c())
            {
                ChunkCoordIntPair var9 = (ChunkCoordIntPair)var7.next();

                if (var9 != null)
                {
                    if (worldObj.blockExists(var9.chunkXPos << 4, 0, var9.chunkZPos << 4))
                    {
                        var5 = worldObj.getChunkFromChunkCoords(var9.chunkXPos, var9.chunkZPos);

                        if (var5.func_150802_k())
                        {
                            var6.add(var5);
                            var8.addAll(((WorldServer)worldObj).func_147486_a(var9.chunkXPos * 16, 0, var9.chunkZPos * 16, var9.chunkXPos * 16 + 16, 256, var9.chunkZPos * 16 + 16));
                            var7.remove();
                        }
                    }
                }
                else
                {
                    var7.remove();
                }
            }

            if (!var6.isEmpty())
            {
                playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(var6));
                Iterator var11 = var8.iterator();

                while (var11.hasNext())
                {
                    TileEntity var10 = (TileEntity)var11.next();
                    func_147097_b(var10);
                }

                var11 = var6.iterator();

                while (var11.hasNext())
                {
                    var5 = (Chunk)var11.next();
                    getServerForPlayer().getEntityTracker().func_85172_a(this, var5);
                }
            }
        }

        if (field_143005_bX > 0L && mcServer.func_143007_ar() > 0 && MinecraftServer.getCurrentTimeMillis() - field_143005_bX > mcServer.func_143007_ar() * 1000 * 60)
        {
            playerNetServerHandler.kickPlayerFromServer("You have been idle for too long!");
        }
    }

    public VACState getVACState()
    {
        return playerNetServerHandler.getVACState();
    }

    public void onUpdateEntity()
    {
        try
        {
            super.onUpdate();

            for (int var1 = 0; var1 < inventory.getSizeInventory(); ++var1)
            {
                ItemStack var6 = inventory.getStackInSlot(var1);

                if (var6 != null && var6.getItem().isMap())
                {
                    Packet var8 = ((ItemMapBase)var6.getItem()).func_150911_c(var6, worldObj, this);

                    if (var8 != null)
                    {
                        playerNetServerHandler.sendPacket(var8);
                    }
                }
            }

            if (getHealth() != lastHealth || lastFoodLevel != foodStats.getFoodLevel() || foodStats.getSaturationLevel() == 0.0F != wasHungry)
            {
                playerNetServerHandler.sendPacket(new S06PacketUpdateHealth(getHealth(), foodStats.getFoodLevel(), foodStats.getSaturationLevel()));
                lastHealth = getHealth();
                lastFoodLevel = foodStats.getFoodLevel();
                wasHungry = foodStats.getSaturationLevel() == 0.0F;
            }

            if (getHealth() + getAbsorptionAmount() != field_130068_bO)
            {
                field_130068_bO = getHealth() + getAbsorptionAmount();
                Collection var5 = getWorldScoreboard().func_96520_a(IScoreObjectiveCriteria.health);
                Iterator var7 = var5.iterator();

                while (var7.hasNext())
                {
                    ScoreObjective var9 = (ScoreObjective)var7.next();
                    getWorldScoreboard().func_96529_a(getUsername(), var9).func_96651_a(Arrays.asList(new EntityPlayer[] {this}));
                }
            }

            if (experienceTotal != lastExperience)
            {
                lastExperience = experienceTotal;
                playerNetServerHandler.sendPacket(new S1FPacketSetExperience(experience, experienceTotal, experienceLevel));
            }

            if (ticksExisted % 20 * 5 == 0 && !func_147099_x().func_77443_a(AchievementList.field_150961_L))
            {
                func_147098_j();
            }
        }
        catch (Throwable var4)
        {
            CrashReport var2 = CrashReport.makeCrashReport(var4, "Ticking player");
            CrashReportCategory var3 = var2.makeCategory("Player being ticked");
            addEntityCrashInfo(var3);
            throw new ReportedException(var2);
        }
    }

    protected void func_147098_j()
    {
        BiomeGenBase var1 = worldObj.getBiomeGenForCoords(MathHelper.floor_double(posX), MathHelper.floor_double(posZ));

        if (var1 != null)
        {
            String var2 = var1.biomeName;
            JsonSerializableSet var3 = (JsonSerializableSet)func_147099_x().func_150870_b(AchievementList.field_150961_L);

            if (var3 == null)
            {
                var3 = (JsonSerializableSet)func_147099_x().func_150872_a(AchievementList.field_150961_L, new JsonSerializableSet());
            }

            var3.add(var2);

            if (func_147099_x().func_77442_b(AchievementList.field_150961_L) && var3.size() == BiomeGenBase.field_150597_n.size())
            {
                HashSet var4 = Sets.newHashSet(BiomeGenBase.field_150597_n);
                Iterator var5 = var3.iterator();

                while (var5.hasNext())
                {
                    String var6 = (String)var5.next();
                    Iterator var7 = var4.iterator();

                    while (var7.hasNext())
                    {
                        BiomeGenBase var8 = (BiomeGenBase)var7.next();

                        if (var8.biomeName.equals(var6))
                        {
                            var7.remove();
                        }
                    }

                    if (var4.isEmpty())
                    {
                        break;
                    }
                }

                if (var4.isEmpty())
                {
                    triggerAchievement(AchievementList.field_150961_L);
                }
            }
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        mcServer.getConfigurationManager().func_148539_a(func_110142_aN().func_151521_b());

        if (!worldObj.getGameRules().getGameRuleBooleanValue("keepInventory") && !(worldObj.getGameRules().getGameRuleBooleanValue("adminsKeepInventory") && MinecraftServer.isPlayerOpped(getUsername())))
        {
            inventory.dropAllItems();
        }

        Collection var2 = worldObj.getScoreboard().func_96520_a(IScoreObjectiveCriteria.deathCount);
        Iterator var3 = var2.iterator();

        while (var3.hasNext())
        {
            ScoreObjective var4 = (ScoreObjective)var3.next();
            Score var5 = getWorldScoreboard().func_96529_a(getUsername(), var4);
            var5.func_96648_a();
        }

        EntityLivingBase var6 = func_94060_bK();

        if (var6 != null)
        {
            int var7 = EntityList.getEntityID(var6);
            EntityList.EntityEggInfo var8 = (EntityList.EntityEggInfo)EntityList.entityEggs.get(Integer.valueOf(var7));

            if (var8 != null)
            {
                addStat(var8.field_151513_e, 1);
            }

            var6.addToPlayerScore(this, scoreValue);
        }

        addStat(StatList.deathsStat, 1);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            boolean var3 = mcServer.isDedicatedServer() && mcServer.isPVPEnabled() && "fall".equals(par1DamageSource.damageType);

            if (!var3 && field_147101_bU > 0 && par1DamageSource != DamageSource.outOfWorld)
            {
                return false;
            }
            else
            {
                if (par1DamageSource instanceof EntityDamageSource)
                {
                    Entity var4 = par1DamageSource.getEntity();

                    if (var4 instanceof EntityPlayer && !canAttackPlayer((EntityPlayer)var4)) { return false; }

                    if (var4 instanceof EntityArrow)
                    {
                        EntityArrow var5 = (EntityArrow)var4;

                        if (var5.shootingEntity instanceof EntityPlayer && !canAttackPlayer((EntityPlayer)var5.shootingEntity)) { return false; }
                    }
                }

                return super.attackEntityFrom(par1DamageSource, par2);
            }
        }
    }

    public boolean canAttackPlayer(EntityPlayer par1EntityPlayer)
    {
        return !mcServer.isPVPEnabled() ? false : super.canAttackPlayer(par1EntityPlayer);
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to
     * teleport to
     */
    public void travelToDimension(int par1)
    {
        if (dimension == 1 && par1 == 1)
        {
            triggerAchievement(AchievementList.theEnd2);
            worldObj.removeEntity(this);
            playerConqueredTheEnd = true;
            playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(4, 0.0F));
        }
        else
        {
            if (dimension == 0 && par1 == 1)
            {
                triggerAchievement(AchievementList.theEnd);
                ChunkCoordinates var2 = mcServer.worldServerForDimension(par1).getEntrancePortalLocation();

                if (var2 != null)
                {
                    playerNetServerHandler.setPlayerLocation(var2.posX, var2.posY, var2.posZ, 0.0F, 0.0F);
                }

                par1 = 1;
            }
            else
            {
                triggerAchievement(AchievementList.portal);
            }

            mcServer.getConfigurationManager().transferPlayerToDimension(this, par1);
            lastExperience = -1;
            lastHealth = -1.0F;
            lastFoodLevel = -1;
        }
    }

    private void func_147097_b(TileEntity p_147097_1_)
    {
        if (p_147097_1_ != null)
        {
            Packet var2 = p_147097_1_.getDescriptionPacket();

            if (var2 != null)
            {
                playerNetServerHandler.sendPacket(var2);
            }
        }
    }

    /**
     * Called whenever an item is picked up from walking over it. Args:
     * pickedUpEntity, stackSize
     */
    public void onItemPickup(Entity par1Entity, int par2)
    {
        super.onItemPickup(par1Entity, par2);
        openContainer.detectAndSendChanges();
    }

    /**
     * puts player to sleep on specified bed if possible
     */
    public EntityPlayer.EnumStatus sleepInBedAt(int par1, int par2, int par3)
    {
        EntityPlayer.EnumStatus var4 = super.sleepInBedAt(par1, par2, par3);

        if (var4 == EntityPlayer.EnumStatus.OK)
        {
            S0APacketUseBed var5 = new S0APacketUseBed(this, par1, par2, par3);
            getServerForPlayer().getEntityTracker().func_151247_a(this, var5);
            playerNetServerHandler.setPlayerLocation(posX, posY, posZ, rotationYaw, rotationPitch);
            playerNetServerHandler.sendPacket(var5);
        }

        return var4;
    }

    /**
     * Wake up the player if they're sleeping.
     */
    public void wakeUpPlayer(boolean par1, boolean par2, boolean par3)
    {
        if (isPlayerSleeping())
        {
            getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(this, 2));
        }

        super.wakeUpPlayer(par1, par2, par3);

        if (playerNetServerHandler != null)
        {
            playerNetServerHandler.setPlayerLocation(posX, posY, posZ, rotationYaw, rotationPitch);
        }
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity par1Entity)
    {
        super.mountEntity(par1Entity);
        playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this, ridingEntity));
        playerNetServerHandler.setPlayerLocation(posX, posY, posZ, rotationYaw, rotationPitch);
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on
     * the ground to update the fall distance and deal fall damage if landing on
     * the ground. Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
    }

    /**
     * process player falling based on movement packet
     */
    public void handleFalling(double par1, boolean par3)
    {
        super.updateFallState(par1, par3);
    }

    public void func_146100_a(TileEntity p_146100_1_)
    {
        if (p_146100_1_ instanceof TileEntitySign)
        {
            ((TileEntitySign)p_146100_1_).func_145912_a(this);
            playerNetServerHandler.sendPacket(new S36PacketSignEditorOpen(p_146100_1_.xCoord, p_146100_1_.yCoord, p_146100_1_.zCoord));
        }
    }

    /**
     * get the next window id to use
     */
    private void getNextWindowId()
    {
        currentWindowId = currentWindowId % 100 + 1;
    }

    /**
     * Displays the crafting GUI for a workbench.
     */
    public void displayGUIWorkbench(int par1, int par2, int par3)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 1, "Crafting", 9, true));
        openContainer = new ContainerWorkbench(inventory, worldObj, par1, par2, par3);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    public void displayGUIEnchantment(int par1, int par2, int par3, String par4Str)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 4, par4Str == null ? "" : par4Str, 9, par4Str != null));
        openContainer = new ContainerEnchantment(inventory, worldObj, par1, par2, par3);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    /**
     * Displays the GUI for interacting with an anvil.
     */
    public void displayGUIAnvil(int par1, int par2, int par3)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 8, "Repairing", 9, true));
        openContainer = new ContainerRepair(inventory, worldObj, par1, par2, par3, this);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args:
     * chestInventory
     */
    public void displayGUIChest(IInventory par1IInventory)
    {
        if (openContainer != inventoryContainer)
        {
            closeScreen();
        }

        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 0, par1IInventory.getInventoryName(), par1IInventory.getSizeInventory(), par1IInventory.isInventoryNameLocalized()));
        openContainer = new ContainerChest(inventory, par1IInventory);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    public void func_146093_a(TileEntityHopper p_146093_1_)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 9, p_146093_1_.getInventoryName(), p_146093_1_.getSizeInventory(), p_146093_1_.isInventoryNameLocalized()));
        openContainer = new ContainerHopper(inventory, p_146093_1_);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    public void displayGUIHopperMinecart(EntityMinecartHopper par1EntityMinecartHopper)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 9, par1EntityMinecartHopper.getInventoryName(), par1EntityMinecartHopper.getSizeInventory(), par1EntityMinecartHopper.isInventoryNameLocalized()));
        openContainer = new ContainerHopper(inventory, par1EntityMinecartHopper);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    public void func_146101_a(TileEntityFurnace p_146101_1_)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 2, p_146101_1_.getInventoryName(), p_146101_1_.getSizeInventory(), p_146101_1_.isInventoryNameLocalized()));
        openContainer = new ContainerFurnace(inventory, p_146101_1_);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    public void func_146102_a(TileEntityDispenser p_146102_1_)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, p_146102_1_ instanceof TileEntityDropper ? 10 : 3, p_146102_1_.getInventoryName(), p_146102_1_.getSizeInventory(), p_146102_1_.isInventoryNameLocalized()));
        openContainer = new ContainerDispenser(inventory, p_146102_1_);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    public void func_146098_a(TileEntityBrewingStand p_146098_1_)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 5, p_146098_1_.getInventoryName(), p_146098_1_.getSizeInventory(), p_146098_1_.isInventoryNameLocalized()));
        openContainer = new ContainerBrewingStand(inventory, p_146098_1_);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    public void func_146104_a(TileEntityBeacon p_146104_1_)
    {
        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 7, p_146104_1_.getInventoryName(), p_146104_1_.getSizeInventory(), p_146104_1_.isInventoryNameLocalized()));
        openContainer = new ContainerBeacon(inventory, p_146104_1_);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    public void displayGUIMerchant(IMerchant par1IMerchant, String par2Str)
    {
        getNextWindowId();
        openContainer = new ContainerMerchant(inventory, par1IMerchant, worldObj);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
        InventoryMerchant var3 = ((ContainerMerchant)openContainer).getMerchantInventory();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 6, par2Str == null ? "" : par2Str, var3.getSizeInventory(), par2Str != null));
        MerchantRecipeList var4 = par1IMerchant.getRecipes(this);

        if (var4 != null)
        {
            try
            {
                PacketBuffer var5 = new PacketBuffer(Unpooled.buffer());
                var5.writeInt(currentWindowId);
                var4.func_151391_a(var5);
                playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|TrList", var5));
            }
            catch (IOException var6)
            {
                logger.error("Couldn\'t send trade list", var6);
            }
        }
    }

    public void displayGUIHorse(EntityHorse par1EntityHorse, IInventory par2IInventory)
    {
        if (openContainer != inventoryContainer)
        {
            closeScreen();
        }

        getNextWindowId();
        playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(currentWindowId, 11, par2IInventory.getInventoryName(), par2IInventory.getSizeInventory(), par2IInventory.isInventoryNameLocalized(), par1EntityHorse.getEntityId()));
        openContainer = new ContainerHorseInventory(inventory, par2IInventory, par1EntityHorse);
        openContainer.windowId = currentWindowId;
        openContainer.onCraftGuiOpened(this);
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container.
     * This doesn't have to match the actual contents of that slot. Args:
     * Container, slot number, slot contents
     */
    public void sendSlotContents(Container par1Container, int par2, ItemStack par3ItemStack)
    {
        if (!(par1Container.getSlot(par2) instanceof SlotCrafting))
        {
            if (!isChangingQuantityOnly)
            {
                playerNetServerHandler.sendPacket(new S2FPacketSetSlot(par1Container.windowId, par2, par3ItemStack));
            }
        }
    }

    public void sendContainerToPlayer(Container par1Container)
    {
        updateCraftingInventory(par1Container, par1Container.getInventory());
    }

    /**
     * update the crafting window inventory with the items in the list
     */
    public void updateCraftingInventory(Container par1Container, List par2List)
    {
        playerNetServerHandler.sendPacket(new S30PacketWindowItems(par1Container.windowId, par2List));
        playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, inventory.getItemStack()));
    }

    /**
     * Sends two ints to the client-side Container. Used for furnace burning
     * time, smelting progress, brewing progress, and enchanting level. Normally
     * the first int identifies which variable to update, and the second
     * contains the new value. Both are truncated to shorts in non-local SMP.
     */
    public void sendProgressBarUpdate(Container par1Container, int par2, int par3)
    {
        playerNetServerHandler.sendPacket(new S31PacketWindowProperty(par1Container.windowId, par2, par3));
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    public void closeScreen()
    {
        playerNetServerHandler.sendPacket(new S2EPacketCloseWindow(openContainer.windowId));
        closeContainer();
    }

    /**
     * updates item held by mouse
     */
    public void updateHeldItem()
    {
        if (!isChangingQuantityOnly)
        {
            playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, inventory.getItemStack()));
        }
    }

    /**
     * Closes the container the player currently has open.
     */
    public void closeContainer()
    {
        openContainer.onContainerClosed(this);
        openContainer = inventoryContainer;
    }

    public void setEntityActionState(float par1, float par2, boolean par3, boolean par4)
    {
        if (ridingEntity != null)
        {
            if (par1 >= -1.0F && par1 <= 1.0F)
            {
                moveStrafing = par1;
            }

            if (par2 >= -1.0F && par2 <= 1.0F)
            {
                moveForward = par2;
            }

            isJumping = par3;
            setSneaking(par4);
        }
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(StatBase par1StatBase, int par2)
    {
        if (par1StatBase != null)
        {
            field_147103_bO.func_150871_b(this, par1StatBase, par2);
            Iterator var3 = getWorldScoreboard().func_96520_a(par1StatBase.func_150952_k()).iterator();

            while (var3.hasNext())
            {
                ScoreObjective var4 = (ScoreObjective)var3.next();
                getWorldScoreboard().func_96529_a(getUsername(), var4).func_96648_a();
            }

            if (field_147103_bO.func_150879_e())
            {
                field_147103_bO.func_150876_a(this);
            }
        }
    }

    public void mountEntityAndWakeUp()
    {
        if (riddenByEntity != null)
        {
            riddenByEntity.mountEntity(this);
        }

        if (sleeping)
        {
            wakeUpPlayer(true, false, false);
        }
    }

    /**
     * this function is called when a players inventory is sent to him,
     * lastHealth is updated on any dimension transitions, then reset.
     */
    public void setPlayerHealthUpdated()
    {
        lastHealth = -1.0E8F;
    }

    public void addChatComponentMessage(IChatComponent p_146105_1_)
    {
        playerNetServerHandler.sendPacket(new S02PacketChat(p_146105_1_));
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void onItemUseFinish()
    {
        playerNetServerHandler.sendPacket(new S19PacketEntityStatus(this, (byte)9));
        super.onItemUseFinish();
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack,
     * int maxItemUseDuration
     */
    public void setItemInUse(ItemStack par1ItemStack, int par2)
    {
        super.setItemInUse(par1ItemStack, par2);

        if (par1ItemStack != null && par1ItemStack.getItem() != null && par1ItemStack.getItem().getItemUseAction(par1ItemStack) == EnumAction.eat)
        {
            getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(this, 3));
        }
    }

    /**
     * Copies the values from the given player into this player if boolean par2
     * is true. Always clones Ender Chest Inventory.
     */
    public void clonePlayer(EntityPlayer par1EntityPlayer, boolean par2)
    {
        super.clonePlayer(par1EntityPlayer, par2);
        lastExperience = -1;
        lastHealth = -1.0F;
        lastFoodLevel = -1;
        destroyedItemsNetCache.addAll(((EntityPlayerMP)par1EntityPlayer).destroyedItemsNetCache);
    }

    protected void onNewPotionEffect(PotionEffect par1PotionEffect)
    {
        super.onNewPotionEffect(par1PotionEffect);
        playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(getEntityId(), par1PotionEffect));
    }

    protected void onChangedPotionEffect(PotionEffect par1PotionEffect, boolean par2)
    {
        super.onChangedPotionEffect(par1PotionEffect, par2);
        playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(getEntityId(), par1PotionEffect));
    }

    protected void onFinishedPotionEffect(PotionEffect par1PotionEffect)
    {
        super.onFinishedPotionEffect(par1PotionEffect);
        playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(getEntityId(), par1PotionEffect));
    }

    /**
     * Sets the position of the entity and updates the 'last' variables
     */
    public void setPositionAndUpdate(double par1, double par3, double par5)
    {
        playerNetServerHandler.setPlayerLocation(par1, par3, par5, rotationYaw, rotationPitch);
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args:
     * entity that was hit critically
     */
    public void onCriticalHit(Entity par1Entity)
    {
        getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(par1Entity, 4));
    }

    public void onEnchantmentCritical(Entity par1Entity)
    {
        getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(par1Entity, 5));
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void sendPlayerAbilities()
    {
        if (playerNetServerHandler != null)
        {
            playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(capabilities));
        }
    }

    public WorldServer getServerForPlayer()
    {
        return (WorldServer)worldObj;
    }

    /**
     * Sets the player's game mode and sends it to them.
     */
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        theItemInWorldManager.setGameType(par1EnumGameType);
        playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(3, par1EnumGameType.getID()));
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
        playerNetServerHandler.sendPacket(new S02PacketChat(p_145747_1_));
    }

    public void addChatMessage(String message)
    {
        addChatMessage(new ChatComponentText(message));
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean canCommandSenderUseCommand(int commandpermissionLevel, String commandName)
    {
        if ("seed".equals(commandName) && !mcServer.isDedicatedServer())
        {
            return true;
        }
        else
        {
            if (!("tell".equals(commandName) || "help".equals(commandName) || "me".equals(commandName) || "myip".equals(commandName) || "motd".equals(commandName)))
            {
                if (mcServer.getConfigurationManager().isPlayerOpped(getUsername()))
                {
                    return mcServer.getOpPermissionLevel() >= commandpermissionLevel;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return true;
            }
        }
        // return "seed".equals(par2Str) && !this.mcServer.isDedicatedServer() ?
        // true : (!"tell".equals(par2Str) && !"help".equals(par2Str) &&
        // !"me".equals(par2Str) ?
        // (this.mcServer.getConfigurationManager().isPlayerOpped(this.getCommandSenderName())
        // ? this.mcServer.getOpPermissionLevel() >= par1 : false) : true);
    }

    /**
     * Gets the player's IP address. Used in /banip.
     */
    public String getPlayerIP()
    {
        String var1 = playerNetServerHandler.netManager.getRemoteAddress().toString();
        var1 = var1.substring(var1.indexOf("/") + 1);
        var1 = var1.substring(0, var1.indexOf(":"));
        return var1;
    }

    public void func_147100_a(C15PacketClientSettings p_147100_1_)
    {
        translator = p_147100_1_.func_149524_c();
        int var2 = 256 >> p_147100_1_.func_149521_d();

        if (var2 > 3 && var2 < 15)
        {
            renderDistance = var2;
        }

        chatVisibility = p_147100_1_.func_149523_e();
        chatColours = p_147100_1_.func_149520_f();

        if (mcServer.isSinglePlayer() && mcServer.getServerOwner().equals(getUsername()))
        {
            mcServer.func_147139_a(p_147100_1_.func_149518_g());
        }

        setHideCape(1, !p_147100_1_.func_149519_h());
    }

    public EntityPlayer.EnumChatVisibility func_147096_v()
    {
        return chatVisibility;
    }

    public void func_147095_a(String p_147095_1_)
    {
        playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|RPack", p_147095_1_.getBytes(Charsets.UTF_8)));
    }

    /**
     * Return the position for this command sender.
     */
    public ChunkCoordinates getCommandSenderPosition()
    {
        return new ChunkCoordinates(MathHelper.floor_double(posX), MathHelper.floor_double(posY + 0.5D), MathHelper.floor_double(posZ));
    }

    public void func_143004_u()
    {
        field_143005_bX = MinecraftServer.getCurrentTimeMillis();
    }

    public StatisticsFile func_147099_x()
    {
        return field_147103_bO;
    }
}
