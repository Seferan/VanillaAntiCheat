package net.minecraft.entity.player;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import mx.x10.afffsdd.vanillaanticheat.module.VACState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Util;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

public abstract class EntityPlayer extends EntityLivingBase implements ICommandSender
{
    /** Inventory of the player */
    public InventoryPlayer inventory = new InventoryPlayer(this);
    private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();

    /**
     * The Container for the player's inventory (which opens when they press E)
     */
    public Container inventoryContainer;

    /** The Container the player has open. */
    public Container openContainer;

    /** The food object of the player, the general hunger logic. */
    protected FoodStats foodStats = new FoodStats();

    /**
     * Used to tell if the player pressed jump twice. If this is at 0 and it's
     * pressed (And they are allowed to fly, as defined in the player's
     * movementInput) it sets this to 7. If it's pressed and it's greater than 0
     * enable fly.
     */
    protected int flyToggleTimer;
    public float prevCameraYaw;
    public float cameraYaw;

    /**
     * Used by EntityPlayer to prevent too many xp orbs from getting absorbed at
     * once.
     */
    public int xpCooldown;
    public double field_71091_bM;
    public double field_71096_bN;
    public double field_71097_bO;
    public double field_71094_bP;
    public double field_71095_bQ;
    public double field_71085_bR;

    /** Boolean value indicating weather a player is sleeping or not */
    protected boolean sleeping;

    /** the current location of the player */
    public ChunkCoordinates playerLocation;
    private int sleepTimer;
    public float field_71079_bU;
    public float field_71089_bV;

    /** holds the spawn chunk of the player */
    private ChunkCoordinates spawnChunk;

    /**
     * Whether this player's spawn point is forced, preventing execution of bed
     * checks.
     */
    private boolean spawnForced;

    /** Holds the coordinate of the player when enter a minecraft to ride. */
    private ChunkCoordinates startMinecartRidingCoordinate;

    /** The player's capabilities. (See class PlayerCapabilities) */
    public PlayerCapabilities capabilities = new PlayerCapabilities();

    /** The current experience level the player is on. */
    public int experienceLevel;

    /**
     * The total amount of experience the player has. This also includes the
     * amount of experience within their Experience Bar.
     */
    public int experienceTotal;

    /**
     * The current amount of experience the player has within their Experience
     * Bar.
     */
    public float experience;

    /**
     * This is the item that is in use when the player is holding down the
     * useItemButton (e.g., bow, food, sword)
     */
    private ItemStack itemInUse;

    /**
     * This field starts off equal to getMaxItemUseDuration and is decremented
     * on each tick
     */
    private int itemInUseCount;
    protected float speedOnGround = 0.1F;
    protected float speedInAir = 0.02F;
    private int field_82249_h;
    private final GameProfile field_146106_i;

    /**
     * An instance of a fishing rod's hook. If this isn't null, the icon image
     * of the fishing rod is slightly different
     */
    public EntityFishHook fishEntity;
    private static final String __OBFID = "CL_00001711";

    public EntityPlayer(World p_i45324_1_, GameProfile p_i45324_2_)
    {
        super(p_i45324_1_);
        entityUniqueID = func_146094_a(p_i45324_2_);
        field_146106_i = p_i45324_2_;
        inventoryContainer = new ContainerPlayer(inventory, !p_i45324_1_.isClient, this);
        openContainer = inventoryContainer;
        yOffset = 1.62F;
        ChunkCoordinates var3 = p_i45324_1_.getSpawnPoint();
        setLocationAndAngles(var3.posX + 0.5D, var3.posY + 1, var3.posZ + 0.5D, 0.0F, 0.0F);
        field_70741_aB = 180.0F;
        fireResistance = 20;
    }

    public VACState getVACState()
    {
        return ((EntityPlayerMP)this).getVACState();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte)0));
        dataWatcher.addObject(17, Float.valueOf(0.0F));
        dataWatcher.addObject(18, Integer.valueOf(0));
    }

    /**
     * Checks if the entity is currently using an item (e.g., bow, food, sword)
     * by holding down the useItemButton
     */
    public boolean isUsingItem()
    {
        return itemInUse != null;
    }

    public void stopUsingItem()
    {
        if (itemInUse != null)
        {
            itemInUse.onPlayerStoppedUsing(worldObj, this, itemInUseCount);
        }

        clearItemInUse();
    }

    public void clearItemInUse()
    {
        itemInUse = null;
        itemInUseCount = 0;

        if (!worldObj.isClient)
        {
            setEating(false);
        }
    }

    public boolean isBlocking()
    {
        return isUsingItem() && itemInUse.getItem().getItemUseAction(itemInUse) == EnumAction.block;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (itemInUse != null)
        {
            ItemStack var1 = inventory.getCurrentItem();

            if (var1 == itemInUse)
            {
                if (itemInUseCount <= 25 && itemInUseCount % 4 == 0)
                {
                    updateItemUse(var1, 5);
                }

                if (--itemInUseCount == 0 && !worldObj.isClient)
                {
                    onItemUseFinish();
                }
            }
            else
            {
                clearItemInUse();
            }
        }

        if (xpCooldown > 0)
        {
            --xpCooldown;
        }

        if (isPlayerSleeping())
        {
            ++sleepTimer;

            if (sleepTimer > 100)
            {
                sleepTimer = 100;
            }

            if (!worldObj.isClient)
            {
                if (!isInBed())
                {
                    wakeUpPlayer(true, true, false);
                }
                else if (worldObj.isDaytime())
                {
                    wakeUpPlayer(false, true, true);
                }
            }
        }
        else if (sleepTimer > 0)
        {
            ++sleepTimer;

            if (sleepTimer >= 110)
            {
                sleepTimer = 0;
            }
        }

        super.onUpdate();

        if (!worldObj.isClient && openContainer != null && !openContainer.canInteractWith(this))
        {
            closeScreen();
            openContainer = inventoryContainer;
        }

        if (isBurning() && capabilities.disableDamage)
        {
            extinguish();
        }

        field_71091_bM = field_71094_bP;
        field_71096_bN = field_71095_bQ;
        field_71097_bO = field_71085_bR;
        double var9 = posX - field_71094_bP;
        double var3 = posY - field_71095_bQ;
        double var5 = posZ - field_71085_bR;
        double var7 = 10.0D;

        if (var9 > var7)
        {
            field_71091_bM = field_71094_bP = posX;
        }

        if (var5 > var7)
        {
            field_71097_bO = field_71085_bR = posZ;
        }

        if (var3 > var7)
        {
            field_71096_bN = field_71095_bQ = posY;
        }

        if (var9 < -var7)
        {
            field_71091_bM = field_71094_bP = posX;
        }

        if (var5 < -var7)
        {
            field_71097_bO = field_71085_bR = posZ;
        }

        if (var3 < -var7)
        {
            field_71096_bN = field_71095_bQ = posY;
        }

        field_71094_bP += var9 * 0.25D;
        field_71085_bR += var5 * 0.25D;
        field_71095_bQ += var3 * 0.25D;

        if (ridingEntity == null)
        {
            startMinecartRidingCoordinate = null;
        }

        if (!worldObj.isClient)
        {
            foodStats.onUpdate(this);
            addStat(StatList.minutesPlayedStat, 1);
        }
    }

    /**
     * Return the amount of time this entity should stay in a portal before
     * being transported.
     */
    public int getMaxInPortalTime()
    {
        return capabilities.disableDamage ? 0 : 80;
    }

    protected String func_145776_H()
    {
        return "game.player.swim";
    }

    protected String func_145777_O()
    {
        return "game.player.swim.splash";
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int getPortalCooldown()
    {
        return 10;
    }

    public void playSound(String par1Str, float par2, float par3)
    {
        worldObj.playSoundToNearExcept(this, par1Str, par2, par3);
    }

    /**
     * Plays sounds and makes particles for item in use state
     */
    protected void updateItemUse(ItemStack par1ItemStack, int par2)
    {
        if (par1ItemStack.getItemUseAction() == EnumAction.drink)
        {
            playSound("random.drink", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (par1ItemStack.getItemUseAction() == EnumAction.eat)
        {
            for (int var3 = 0; var3 < par2; ++var3)
            {
                Vec3 var4 = worldObj.getWorldVec3Pool().getVecFromPool((rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                var4.rotateAroundX(-rotationPitch * (float)Math.PI / 180.0F);
                var4.rotateAroundY(-rotationYaw * (float)Math.PI / 180.0F);
                Vec3 var5 = worldObj.getWorldVec3Pool().getVecFromPool((rand.nextFloat() - 0.5D) * 0.3D, (-rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
                var5.rotateAroundX(-rotationPitch * (float)Math.PI / 180.0F);
                var5.rotateAroundY(-rotationYaw * (float)Math.PI / 180.0F);
                var5 = var5.addVector(posX, posY + getEyeHeight(), posZ);
                String var6 = "iconcrack_" + Item.getIdFromItem(par1ItemStack.getItem());

                if (par1ItemStack.getHasSubtypes())
                {
                    var6 = var6 + "_" + par1ItemStack.getItemDamage();
                }

                worldObj.spawnParticle(var6, var5.xCoord, var5.yCoord, var5.zCoord, var4.xCoord, var4.yCoord + 0.05D, var4.zCoord);
            }

            playSound("random.eat", 0.5F + 0.5F * rand.nextInt(2), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
        }
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void onItemUseFinish()
    {
        if (itemInUse != null)
        {
            updateItemUse(itemInUse, 16);
            int var1 = itemInUse.stackSize;
            ItemStack var2 = itemInUse.onFoodEaten(worldObj, this);

            if (var2 != itemInUse || var2 != null && var2.stackSize != var1)
            {
                inventory.mainInventory[inventory.currentItem] = var2;

                if (var2.stackSize == 0)
                {
                    inventory.mainInventory[inventory.currentItem] = null;
                }
            }

            clearItemInUse();
        }
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked()
    {
        return getHealth() <= 0.0F || isPlayerSleeping();
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    protected void closeScreen()
    {
        openContainer = inventoryContainer;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity par1Entity)
    {
        if (ridingEntity != null && par1Entity == null)
        {
            if (!worldObj.isClient)
            {
                dismountEntity(ridingEntity);
            }

            if (ridingEntity != null)
            {
                ridingEntity.riddenByEntity = null;
            }

            ridingEntity = null;
        }
        else
        {
            super.mountEntity(par1Entity);
        }
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        if (!worldObj.isClient && isSneaking())
        {
            mountEntity((Entity)null);
            setSneaking(false);
        }
        else
        {
            double var1 = posX;
            double var3 = posY;
            double var5 = posZ;
            float var7 = rotationYaw;
            float var8 = rotationPitch;
            super.updateRidden();
            prevCameraYaw = cameraYaw;
            cameraYaw = 0.0F;
            addMountedMovementStat(posX - var1, posY - var3, posZ - var5);

            if (ridingEntity instanceof EntityPig)
            {
                rotationPitch = var8;
                rotationYaw = var7;
                renderYawOffset = ((EntityPig)ridingEntity).renderYawOffset;
            }
        }
    }

    protected void updateEntityActionState()
    {
        super.updateEntityActionState();
        updateArmSwingProgress();
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (flyToggleTimer > 0)
        {
            --flyToggleTimer;
        }

        if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL && getHealth() < getMaxHealth() && worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && ticksExisted % 20 * 12 == 0)
        {
            heal(1.0F);
        }

        inventory.decrementAnimations();
        prevCameraYaw = cameraYaw;
        super.onLivingUpdate();
        IAttributeInstance var1 = getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (!worldObj.isClient)
        {
            var1.setBaseValue(capabilities.getWalkSpeed());
        }

        jumpMovementFactor = speedInAir;

        if (isSprinting())
        {
            jumpMovementFactor = (float)(jumpMovementFactor + speedInAir * 0.3D);
        }

        setAIMoveSpeed((float)var1.getAttributeValue());
        float var2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        float var3 = (float)Math.atan(-motionY * 0.20000000298023224D) * 15.0F;

        if (var2 > 0.1F)
        {
            var2 = 0.1F;
        }

        if (!onGround || getHealth() <= 0.0F)
        {
            var2 = 0.0F;
        }

        if (onGround || getHealth() <= 0.0F)
        {
            var3 = 0.0F;
        }

        cameraYaw += (var2 - cameraYaw) * 0.4F;
        cameraPitch += (var3 - cameraPitch) * 0.8F;

        if (getHealth() > 0.0F)
        {
            AxisAlignedBB var4 = null;

            if (ridingEntity != null && !ridingEntity.isDead)
            {
                var4 = boundingBox.func_111270_a(ridingEntity.boundingBox).expand(1.0D, 0.0D, 1.0D);
            }
            else
            {
                var4 = boundingBox.expand(1.0D, 0.5D, 1.0D);
            }

            List var5 = worldObj.getEntitiesWithinAABBExcludingEntity(this, var4);

            if (var5 != null)
            {
                for (int var6 = 0; var6 < var5.size(); ++var6)
                {
                    Entity var7 = (Entity)var5.get(var6);

                    if (!var7.isDead)
                    {
                        collideWithPlayer(var7);
                    }
                }
            }
        }
    }

    private void collideWithPlayer(Entity par1Entity)
    {
        par1Entity.onCollideWithPlayer(this);
    }

    public int getScore()
    {
        return dataWatcher.getWatchableObjectInt(18);
    }

    /**
     * Set player's score
     */
    public void setScore(int par1)
    {
        dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    /**
     * Add to player's score
     */
    public void addScore(int par1)
    {
        int var2 = getScore();
        dataWatcher.updateObject(18, Integer.valueOf(var2 + par1));
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);
        setSize(0.2F, 0.2F);
        setPosition(posX, posY, posZ);
        motionY = 0.10000000149011612D;

        if (getUsername().equals("Notch"))
        {
            dropItem(new ItemStack(Items.apple, 1), true, false);
        }

        if (!worldObj.getGameRules().getGameRuleBooleanValue("keepInventory") && !(worldObj.getGameRules().getGameRuleBooleanValue("adminsKeepInventory") && MinecraftServer.isPlayerOpped(getUsername())))
        {
            inventory.dropAllItems();
        }

        if (par1DamageSource != null)
        {
            motionX = -MathHelper.cos((attackedAtYaw + rotationYaw) * (float)Math.PI / 180.0F) * 0.1F;
            motionZ = -MathHelper.sin((attackedAtYaw + rotationYaw) * (float)Math.PI / 180.0F) * 0.1F;
        }
        else
        {
            motionX = motionZ = 0.0D;
        }

        yOffset = 0.1F;
        addStat(StatList.deathsStat, 1);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "game.player.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "game.player.die";
    }

    /**
     * Adds a value to the player score. Currently not actually used and the
     * entity passed in does nothing. Args: entity, scoreToAdd
     */
    public void addToPlayerScore(Entity par1Entity, int par2)
    {
        addScore(par2);
        Collection var3 = getWorldScoreboard().func_96520_a(IScoreObjectiveCriteria.totalKillCount);

        if (par1Entity instanceof EntityPlayer)
        {
            addStat(StatList.playerKillsStat, 1);
            var3.addAll(getWorldScoreboard().func_96520_a(IScoreObjectiveCriteria.playerKillCount));
        }
        else
        {
            addStat(StatList.mobKillsStat, 1);
        }

        Iterator var4 = var3.iterator();

        while (var4.hasNext())
        {
            ScoreObjective var5 = (ScoreObjective)var4.next();
            Score var6 = getWorldScoreboard().func_96529_a(getUsername(), var5);
            var6.func_96648_a();
        }
    }

    /**
     * Called when player presses the drop item key
     */
    public EntityItem dropOneItem(boolean par1)
    {
        return dropItem(inventory.decrStackSize(inventory.currentItem, par1 && inventory.getCurrentItem() != null ? inventory.getCurrentItem().stackSize : 1), false, true);
    }

    /**
     * Args: itemstack, flag
     */
    public EntityItem dropPlayerItemWithRandomChoice(ItemStack par1ItemStack, boolean par2)
    {
        return dropItem(par1ItemStack, false, false);
    }

    public EntityItem dropItem(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_)
    {
        if (p_146097_1_ == null)
        {
            return null;
        }
        else if (p_146097_1_.stackSize == 0)
        {
            return null;
        }
        else
        {
            EntityItem var4 = new EntityItem(worldObj, posX, posY - 0.30000001192092896D + getEyeHeight(), posZ, p_146097_1_);
            var4.delayBeforeCanPickup = 40;

            if (p_146097_3_)
            {
                var4.func_145799_b(getUsername());
            }

            float var5 = 0.1F;
            float var6;

            if (p_146097_2_)
            {
                var6 = rand.nextFloat() * 0.5F;
                float var7 = rand.nextFloat() * (float)Math.PI * 2.0F;
                var4.motionX = -MathHelper.sin(var7) * var6;
                var4.motionZ = MathHelper.cos(var7) * var6;
                var4.motionY = 0.20000000298023224D;
            }
            else
            {
                var5 = 0.3F;
                var4.motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * var5;
                var4.motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * var5;
                var4.motionY = -MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI) * var5 + 0.1F;
                var5 = 0.02F;
                var6 = rand.nextFloat() * (float)Math.PI * 2.0F;
                var5 *= rand.nextFloat();
                var4.motionX += Math.cos(var6) * var5;
                var4.motionY += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                var4.motionZ += Math.sin(var6) * var5;
            }

            joinEntityItemWithWorld(var4);
            addStat(StatList.dropStat, 1);
            return var4;
        }
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    protected void joinEntityItemWithWorld(EntityItem par1EntityItem)
    {
        worldObj.spawnEntityInWorld(par1EntityItem);
    }

    /**
     * Returns how strong the player is against the specified block at this
     * moment
     */
    public float getCurrentPlayerStrVsBlock(Block p_146096_1_, boolean p_146096_2_)
    {
        float var3 = inventory.func_146023_a(p_146096_1_);

        if (var3 > 1.0F)
        {
            int var4 = EnchantmentHelper.getEfficiencyModifier(this);
            ItemStack var5 = inventory.getCurrentItem();

            if (var4 > 0 && var5 != null)
            {
                float var6 = var4 * var4 + 1;

                if (!var5.func_150998_b(p_146096_1_) && var3 <= 1.0F)
                {
                    var3 += var6 * 0.08F;
                }
                else
                {
                    var3 += var6;
                }
            }
        }

        if (this.isPotionActive(Potion.digSpeed))
        {
            var3 *= 1.0F + (getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (this.isPotionActive(Potion.digSlowdown))
        {
            var3 *= 1.0F - (getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
        }

        if (isInsideOfMaterial(Material.field_151586_h) && !EnchantmentHelper.getAquaAffinityModifier(this))
        {
            var3 /= 5.0F;
        }

        if (!onGround)
        {
            var3 /= 5.0F;
        }

        return var3;
    }

    /**
     * Checks if the player has the ability to harvest a block (checks current
     * inventory item for a tool if necessary)
     */
    public boolean canHarvestBlock(Block p_146099_1_)
    {
        return inventory.func_146025_b(p_146099_1_);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        entityUniqueID = func_146094_a(field_146106_i);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Inventory", 10);
        inventory.readFromNBT(var2);
        inventory.currentItem = par1NBTTagCompound.getInteger("SelectedItemSlot");
        sleeping = par1NBTTagCompound.getBoolean("Sleeping");
        sleepTimer = par1NBTTagCompound.getShort("SleepTimer");
        experience = par1NBTTagCompound.getFloat("XpP");
        experienceLevel = par1NBTTagCompound.getInteger("XpLevel");
        experienceTotal = par1NBTTagCompound.getInteger("XpTotal");
        setScore(par1NBTTagCompound.getInteger("Score"));

        if (sleeping)
        {
            playerLocation = new ChunkCoordinates(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
            wakeUpPlayer(true, true, false);
        }

        if (par1NBTTagCompound.func_150297_b("SpawnX", 99) && par1NBTTagCompound.func_150297_b("SpawnY", 99) && par1NBTTagCompound.func_150297_b("SpawnZ", 99))
        {
            spawnChunk = new ChunkCoordinates(par1NBTTagCompound.getInteger("SpawnX"), par1NBTTagCompound.getInteger("SpawnY"), par1NBTTagCompound.getInteger("SpawnZ"));
            spawnForced = par1NBTTagCompound.getBoolean("SpawnForced");
        }

        foodStats.readNBT(par1NBTTagCompound);
        capabilities.readCapabilitiesFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.func_150297_b("EnderItems", 9))
        {
            NBTTagList var3 = par1NBTTagCompound.getTagList("EnderItems", 10);
            theInventoryEnderChest.loadInventoryFromNBT(var3);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setTag("Inventory", inventory.writeToNBT(new NBTTagList()));
        par1NBTTagCompound.setInteger("SelectedItemSlot", inventory.currentItem);
        par1NBTTagCompound.setBoolean("Sleeping", sleeping);
        par1NBTTagCompound.setShort("SleepTimer", (short)sleepTimer);
        par1NBTTagCompound.setFloat("XpP", experience);
        par1NBTTagCompound.setInteger("XpLevel", experienceLevel);
        par1NBTTagCompound.setInteger("XpTotal", experienceTotal);
        par1NBTTagCompound.setInteger("Score", getScore());

        if (spawnChunk != null)
        {
            par1NBTTagCompound.setInteger("SpawnX", spawnChunk.posX);
            par1NBTTagCompound.setInteger("SpawnY", spawnChunk.posY);
            par1NBTTagCompound.setInteger("SpawnZ", spawnChunk.posZ);
            par1NBTTagCompound.setBoolean("SpawnForced", spawnForced);
        }

        foodStats.writeNBT(par1NBTTagCompound);
        capabilities.writeCapabilitiesToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setTag("EnderItems", theInventoryEnderChest.saveInventoryToNBT());
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args:
     * chestInventory
     */
    public void displayGUIChest(IInventory par1IInventory)
    {
    }

    public void func_146093_a(TileEntityHopper p_146093_1_)
    {
    }

    public void displayGUIHopperMinecart(EntityMinecartHopper par1EntityMinecartHopper)
    {
    }

    public void displayGUIHorse(EntityHorse par1EntityHorse, IInventory par2IInventory)
    {
    }

    public void displayGUIEnchantment(int par1, int par2, int par3, String par4Str)
    {
    }

    /**
     * Displays the GUI for interacting with an anvil.
     */
    public void displayGUIAnvil(int par1, int par2, int par3)
    {
    }

    /**
     * Displays the crafting GUI for a workbench.
     */
    public void displayGUIWorkbench(int par1, int par2, int par3)
    {
    }

    public float getEyeHeight()
    {
        return 0.12F;
    }

    /**
     * sets the players height back to normal after doing things like sleeping
     * and dieing
     */
    protected void resetHeight()
    {
        yOffset = 1.62F;
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
        else if (capabilities.disableDamage && !par1DamageSource.canHarmInCreative())
        {
            return false;
        }
        else
        {
            entityAge = 0;

            if (getHealth() <= 0.0F)
            {
                return false;
            }
            else
            {
                if (isPlayerSleeping() && !worldObj.isClient)
                {
                    wakeUpPlayer(true, true, false);
                }

                if (par1DamageSource.isDifficultyScaled())
                {
                    if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
                    {
                        par2 = 0.0F;
                    }

                    if (worldObj.difficultySetting == EnumDifficulty.EASY)
                    {
                        par2 = par2 / 2.0F + 1.0F;
                    }

                    if (worldObj.difficultySetting == EnumDifficulty.HARD)
                    {
                        par2 = par2 * 3.0F / 2.0F;
                    }
                }

                if (par2 == 0.0F)
                {
                    return false;
                }
                else
                {
                    Entity var3 = par1DamageSource.getEntity();

                    if (var3 instanceof EntityArrow && ((EntityArrow)var3).shootingEntity != null)
                    {
                        var3 = ((EntityArrow)var3).shootingEntity;
                    }

                    addStat(StatList.damageTakenStat, Math.round(par2 * 10.0F));
                    return super.attackEntityFrom(par1DamageSource, par2);
                }
            }
        }
    }

    public boolean canAttackPlayer(EntityPlayer par1EntityPlayer)
    {
        Team var2 = getTeam();
        Team var3 = par1EntityPlayer.getTeam();
        return var2 == null ? true : (!var2.isSameTeam(var3) ? true : var2.getAllowFriendlyFire());
    }

    protected void damageArmor(float par1)
    {
        inventory.damageArmor(par1);
    }

    /**
     * Returns the current armor value as determined by a call to
     * InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return inventory.getTotalArmorValue();
    }

    /**
     * When searching for vulnerable players, if a player is invisible, the
     * return value of this is the chance of seeing them anyway.
     */
    public float getArmorVisibility()
    {
        int var1 = 0;
        ItemStack[] var2 = inventory.armorInventory;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            ItemStack var5 = var2[var4];

            if (var5 != null)
            {
                ++var1;
            }
        }

        return (float)var1 / (float)inventory.armorInventory.length;
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage
     * from the armor first and then health second with the reduced value. Args:
     * damageAmount
     */
    protected void damageEntity(DamageSource par1DamageSource, float par2)
    {
        if (!isEntityInvulnerable())
        {
            if (!par1DamageSource.isUnblockable() && isBlocking() && par2 > 0.0F)
            {
                par2 = (1.0F + par2) * 0.5F;
            }

            par2 = applyArmorCalculations(par1DamageSource, par2);
            par2 = applyPotionDamageCalculations(par1DamageSource, par2);
            float var3 = par2;
            par2 = Math.max(par2 - getAbsorptionAmount(), 0.0F);
            setAbsorptionAmount(getAbsorptionAmount() - (var3 - par2));

            if (par2 != 0.0F)
            {
                addExhaustion(par1DamageSource.getHungerDamage());
                float var4 = getHealth();
                setHealth(getHealth() - par2);
                func_110142_aN().func_94547_a(par1DamageSource, var4, par2);
            }
        }
    }

    public void func_146101_a(TileEntityFurnace p_146101_1_)
    {
    }

    public void func_146102_a(TileEntityDispenser p_146102_1_)
    {
    }

    public void func_146100_a(TileEntity p_146100_1_)
    {
    }

    public void func_146095_a(CommandBlockLogic p_146095_1_)
    {
    }

    public void func_146098_a(TileEntityBrewingStand p_146098_1_)
    {
    }

    public void func_146104_a(TileEntityBeacon p_146104_1_)
    {
    }

    public void displayGUIMerchant(IMerchant par1IMerchant, String par2Str)
    {
    }

    /**
     * Displays the GUI for interacting with a book.
     */
    public void displayGUIBook(ItemStack par1ItemStack)
    {
    }

    public boolean interactWith(Entity par1Entity)
    {
        ItemStack var2 = getCurrentEquippedItem();
        ItemStack var3 = var2 != null ? var2.copy() : null;

        if (!par1Entity.interactFirst(this))
        {
            if (var2 != null && par1Entity instanceof EntityLivingBase)
            {
                if (capabilities.isCreativeMode)
                {
                    var2 = var3;
                }

                if (var2.interactWithEntity(this, (EntityLivingBase)par1Entity))
                {
                    if (var2.stackSize <= 0 && !capabilities.isCreativeMode)
                    {
                        destroyCurrentEquippedItem();
                    }

                    return true;
                }
            }

            return false;
        }
        else
        {
            if (var2 != null && var2 == getCurrentEquippedItem())
            {
                if (var2.stackSize <= 0 && !capabilities.isCreativeMode)
                {
                    destroyCurrentEquippedItem();
                }
                else if (var2.stackSize < var3.stackSize && capabilities.isCreativeMode)
                {
                    var2.stackSize = var3.stackSize;
                }
            }

            return true;
        }
    }

    /**
     * Returns the currently being used item by the player.
     */
    public ItemStack getCurrentEquippedItem()
    {
        return inventory.getCurrentItem();
    }

    /**
     * Destroys the currently equipped item from the player's inventory.
     */
    public void destroyCurrentEquippedItem()
    {
        inventory.setInventorySlotContents(inventory.currentItem, (ItemStack)null);
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return yOffset - 0.5F;
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped
     * item. The equipped item has hitEntity called on it. Args: targetEntity
     */
    public void attackTargetEntityWithCurrentItem(Entity par1Entity)
    {
        if (par1Entity.canAttackWithItem())
        {
            if (!par1Entity.hitByEntity(this))
            {
                float var2 = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                int var3 = 0;
                float var4 = 0.0F;

                if (par1Entity instanceof EntityLivingBase)
                {
                    var4 = EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)par1Entity);
                    var3 += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)par1Entity);
                }

                if (isSprinting())
                {
                    ++var3;
                }

                if (var2 > 0.0F || var4 > 0.0F)
                {
                    boolean var5 = fallDistance > 0.0F && !onGround && !isOnLadder() && !isInWater() && !this.isPotionActive(Potion.blindness) && ridingEntity == null && par1Entity instanceof EntityLivingBase;

                    if (var5 && var2 > 0.0F)
                    {
                        var2 *= 1.5F;
                    }

                    var2 += var4;
                    boolean var6 = false;
                    int var7 = EnchantmentHelper.getFireAspectModifier(this);

                    if (par1Entity instanceof EntityLivingBase && var7 > 0 && !par1Entity.isBurning())
                    {
                        var6 = true;
                        par1Entity.setFire(1);
                    }

                    boolean var8 = par1Entity.attackEntityFrom(DamageSource.causePlayerDamage(this), var2);

                    if (var8)
                    {
                        if (var3 > 0)
                        {
                            par1Entity.addVelocity(-MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F) * var3 * 0.5F, 0.1D, MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F) * var3 * 0.5F);
                            motionX *= 0.6D;
                            motionZ *= 0.6D;
                            setSprinting(false);
                        }

                        if (var5)
                        {
                            onCriticalHit(par1Entity);
                        }

                        if (var4 > 0.0F)
                        {
                            onEnchantmentCritical(par1Entity);
                        }

                        if (var2 >= 18.0F)
                        {
                            triggerAchievement(AchievementList.overkill);
                        }

                        setLastAttacker(par1Entity);

                        if (par1Entity instanceof EntityLivingBase)
                        {
                            EnchantmentHelper.func_151384_a((EntityLivingBase)par1Entity, this);
                        }

                        EnchantmentHelper.func_151385_b(this, par1Entity);
                        ItemStack var9 = getCurrentEquippedItem();
                        Object var10 = par1Entity;

                        if (par1Entity instanceof EntityDragonPart)
                        {
                            IEntityMultiPart var11 = ((EntityDragonPart)par1Entity).entityDragonObj;

                            if (var11 != null && var11 instanceof EntityLivingBase)
                            {
                                var10 = var11;
                            }
                        }

                        if (var9 != null && var10 instanceof EntityLivingBase)
                        {
                            var9.hitEntity((EntityLivingBase)var10, this);

                            if (var9.stackSize <= 0)
                            {
                                destroyCurrentEquippedItem();
                            }
                        }

                        if (par1Entity instanceof EntityLivingBase)
                        {
                            addStat(StatList.damageDealtStat, Math.round(var2 * 10.0F));

                            if (var7 > 0)
                            {
                                par1Entity.setFire(var7 * 4);
                            }
                        }

                        addExhaustion(0.3F);
                    }
                    else if (var6)
                    {
                        par1Entity.extinguish();
                    }
                }
            }
        }
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args:
     * entity that was hit critically
     */
    public void onCriticalHit(Entity par1Entity)
    {
    }

    public void onEnchantmentCritical(Entity par1Entity)
    {
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        super.setDead();
        inventoryContainer.onContainerClosed(this);

        if (openContainer != null)
        {
            openContainer.onContainerClosed(this);
        }
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        return !sleeping && super.isEntityInsideOpaqueBlock();
    }

    /**
     * Returns the GameProfile for this player
     */
    public GameProfile getGameProfile()
    {
        return field_146106_i;
    }

    /**
     * puts player to sleep on specified bed if possible
     */
    public EntityPlayer.EnumStatus sleepInBedAt(int par1, int par2, int par3)
    {
        if (!worldObj.isClient)
        {
            if (isPlayerSleeping() || !isEntityAlive()) { return EntityPlayer.EnumStatus.OTHER_PROBLEM; }

            if (!worldObj.provider.isSurfaceWorld()) { return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE; }

            if (worldObj.isDaytime()) { return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW; }

            if (Math.abs(posX - par1) > 3.0D || Math.abs(posY - par2) > 2.0D || Math.abs(posZ - par3) > 3.0D) { return EntityPlayer.EnumStatus.TOO_FAR_AWAY; }

            double var4 = 8.0D;
            double var6 = 5.0D;
            List var8 = worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getAABBPool().getAABB(par1 - var4, par2 - var6, par3 - var4, par1 + var4, par2 + var6, par3 + var4));

            if (!var8.isEmpty()) { return EntityPlayer.EnumStatus.NOT_SAFE; }
        }

        if (isRiding())
        {
            mountEntity((Entity)null);
        }

        setSize(0.2F, 0.2F);
        yOffset = 0.2F;

        if (worldObj.blockExists(par1, par2, par3))
        {
            int var9 = worldObj.getBlockMetadata(par1, par2, par3);
            int var5 = BlockBed.func_149895_l(var9);
            float var10 = 0.5F;
            float var7 = 0.5F;

            switch (var5)
            {
            case 0:
                var7 = 0.9F;
                break;

            case 1:
                var10 = 0.1F;
                break;

            case 2:
                var7 = 0.1F;
                break;

            case 3:
                var10 = 0.9F;
            }

            func_71013_b(var5);
            setPosition(par1 + var10, par2 + 0.9375F, par3 + var7);
        }
        else
        {
            setPosition(par1 + 0.5F, par2 + 0.9375F, par3 + 0.5F);
        }

        sleeping = true;
        sleepTimer = 0;
        playerLocation = new ChunkCoordinates(par1, par2, par3);
        motionX = motionZ = motionY = 0.0D;

        if (!worldObj.isClient)
        {
            worldObj.updateAllPlayersSleepingFlag();
        }

        return EntityPlayer.EnumStatus.OK;
    }

    private void func_71013_b(int par1)
    {
        field_71079_bU = 0.0F;
        field_71089_bV = 0.0F;

        switch (par1)
        {
        case 0:
            field_71089_bV = -1.8F;
            break;

        case 1:
            field_71079_bU = 1.8F;
            break;

        case 2:
            field_71089_bV = 1.8F;
            break;

        case 3:
            field_71079_bU = -1.8F;
        }
    }

    /**
     * Wake up the player if they're sleeping.
     */
    public void wakeUpPlayer(boolean par1, boolean par2, boolean par3)
    {
        setSize(0.6F, 1.8F);
        resetHeight();
        ChunkCoordinates var4 = playerLocation;
        ChunkCoordinates var5 = playerLocation;

        if (var4 != null && worldObj.getBlock(var4.posX, var4.posY, var4.posZ) == Blocks.bed)
        {
            BlockBed.func_149979_a(worldObj, var4.posX, var4.posY, var4.posZ, false);
            var5 = BlockBed.func_149977_a(worldObj, var4.posX, var4.posY, var4.posZ, 0);

            if (var5 == null)
            {
                var5 = new ChunkCoordinates(var4.posX, var4.posY + 1, var4.posZ);
            }

            setPosition(var5.posX + 0.5F, var5.posY + yOffset + 0.1F, var5.posZ + 0.5F);
        }

        sleeping = false;

        if (!worldObj.isClient && par2)
        {
            worldObj.updateAllPlayersSleepingFlag();
        }

        if (par1)
        {
            sleepTimer = 0;
        }
        else
        {
            sleepTimer = 100;
        }

        if (par3)
        {
            setSpawnChunk(playerLocation, false);
        }
    }

    /**
     * Checks if the player is currently in a bed
     */
    private boolean isInBed()
    {
        return worldObj.getBlock(playerLocation.posX, playerLocation.posY, playerLocation.posZ) == Blocks.bed;
    }

    /**
     * Ensure that a block enabling respawning exists at the specified
     * coordinates and find an empty space nearby to spawn.
     */
    public static ChunkCoordinates verifyRespawnCoordinates(World par0World, ChunkCoordinates par1ChunkCoordinates, boolean par2)
    {
        IChunkProvider var3 = par0World.getChunkProvider();
        var3.loadChunk(par1ChunkCoordinates.posX - 3 >> 4, par1ChunkCoordinates.posZ - 3 >> 4);
        var3.loadChunk(par1ChunkCoordinates.posX + 3 >> 4, par1ChunkCoordinates.posZ - 3 >> 4);
        var3.loadChunk(par1ChunkCoordinates.posX - 3 >> 4, par1ChunkCoordinates.posZ + 3 >> 4);
        var3.loadChunk(par1ChunkCoordinates.posX + 3 >> 4, par1ChunkCoordinates.posZ + 3 >> 4);

        if (par0World.getBlock(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ) == Blocks.bed)
        {
            ChunkCoordinates var8 = BlockBed.func_149977_a(par0World, par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ, 0);
            return var8;
        }
        else
        {
            Material var4 = par0World.getBlock(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ).getMaterial();
            Material var5 = par0World.getBlock(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY + 1, par1ChunkCoordinates.posZ).getMaterial();
            boolean var6 = !var4.isSolid() && !var4.isLiquid();
            boolean var7 = !var5.isSolid() && !var5.isLiquid();
            return par2 && var6 && var7 ? par1ChunkCoordinates : null;
        }
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping()
    {
        return sleeping;
    }

    /**
     * Returns whether or not the player is asleep and the screen has fully
     * faded.
     */
    public boolean isPlayerFullyAsleep()
    {
        return sleeping && sleepTimer >= 100;
    }

    protected void setHideCape(int par1, boolean par2)
    {
        byte var3 = dataWatcher.getWatchableObjectByte(16);

        if (par2)
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var3 | 1 << par1)));
        }
        else
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var3 & ~(1 << par1))));
        }
    }

    public void addChatComponentMessage(IChatComponent p_146105_1_)
    {
    }

    /**
     * Returns the location of the bed the player will respawn at, or null if
     * the player has not slept in a bed.
     */
    public ChunkCoordinates getBedLocation()
    {
        return spawnChunk;
    }

    public boolean isSpawnForced()
    {
        return spawnForced;
    }

    /**
     * Defines a spawn coordinate to player spawn. Used by bed after the player
     * sleep on it.
     */
    public void setSpawnChunk(ChunkCoordinates par1ChunkCoordinates, boolean par2)
    {
        if (par1ChunkCoordinates != null)
        {
            spawnChunk = new ChunkCoordinates(par1ChunkCoordinates);
            spawnForced = par2;
        }
        else
        {
            spawnChunk = null;
            spawnForced = false;
        }
    }

    /**
     * Will trigger the specified trigger.
     */
    public void triggerAchievement(StatBase par1StatBase)
    {
        addStat(par1StatBase, 1);
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(StatBase par1StatBase, int par2)
    {
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    public void jump()
    {
        super.jump();
        addStat(StatList.jumpStat, 1);

        if (isSprinting())
        {
            addExhaustion(0.8F);
        }
        else
        {
            addExhaustion(0.2F);
        }
    }

    /**
     * Moves the entity based on the specified heading. Args: strafe, forward
     */
    public void moveEntityWithHeading(float par1, float par2)
    {
        double var3 = posX;
        double var5 = posY;
        double var7 = posZ;

        if (capabilities.isFlying && ridingEntity == null)
        {
            double var9 = motionY;
            float var11 = jumpMovementFactor;
            jumpMovementFactor = capabilities.getFlySpeed();
            super.moveEntityWithHeading(par1, par2);
            motionY = var9 * 0.6D;
            jumpMovementFactor = var11;
        }
        else
        {
            super.moveEntityWithHeading(par1, par2);
        }

        addMovementStat(posX - var3, posY - var5, posZ - var7);
    }

    /**
     * the movespeed used for the new AI system
     */
    public float getAIMoveSpeed()
    {
        return (float)getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
    }

    /**
     * Adds a value to a movement statistic field - like run, walk, swin or
     * climb.
     */
    public void addMovementStat(double par1, double par3, double par5)
    {
        if (ridingEntity == null)
        {
            int var7;

            if (isInsideOfMaterial(Material.field_151586_h))
            {
                var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0F);

                if (var7 > 0)
                {
                    addStat(StatList.distanceDoveStat, var7);
                    addExhaustion(0.015F * var7 * 0.01F);
                }
            }
            else if (isInWater())
            {
                var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (var7 > 0)
                {
                    addStat(StatList.distanceSwumStat, var7);
                    addExhaustion(0.015F * var7 * 0.01F);
                }
            }
            else if (isOnLadder())
            {
                if (par3 > 0.0D)
                {
                    addStat(StatList.distanceClimbedStat, (int)Math.round(par3 * 100.0D));
                }
            }
            else if (onGround)
            {
                var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (var7 > 0)
                {
                    addStat(StatList.distanceWalkedStat, var7);

                    if (isSprinting())
                    {
                        addExhaustion(0.099999994F * var7 * 0.01F);
                    }
                    else
                    {
                        addExhaustion(0.01F * var7 * 0.01F);
                    }
                }
            }
            else
            {
                var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (var7 > 25)
                {
                    addStat(StatList.distanceFlownStat, var7);
                }
            }
        }
    }

    /**
     * Adds a value to a mounted movement statistic field - by minecart, boat,
     * or pig.
     */
    private void addMountedMovementStat(double par1, double par3, double par5)
    {
        if (ridingEntity != null)
        {
            int var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0F);

            if (var7 > 0)
            {
                if (ridingEntity instanceof EntityMinecart)
                {
                    addStat(StatList.distanceByMinecartStat, var7);

                    if (startMinecartRidingCoordinate == null)
                    {
                        startMinecartRidingCoordinate = new ChunkCoordinates(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
                    }
                    else if (startMinecartRidingCoordinate.getDistanceSquared(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) >= 1000000.0D)
                    {
                        addStat(AchievementList.onARail, 1);
                    }
                }
                else if (ridingEntity instanceof EntityBoat)
                {
                    addStat(StatList.distanceByBoatStat, var7);
                }
                else if (ridingEntity instanceof EntityPig)
                {
                    addStat(StatList.distanceByPigStat, var7);
                }
                else if (ridingEntity instanceof EntityHorse)
                {
                    addStat(StatList.field_151185_q, var7);
                }
            }
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        if (!capabilities.allowFlying)
        {
            if (par1 >= 2.0F)
            {
                addStat(StatList.distanceFallenStat, (int)Math.round(par1 * 100.0D));
            }

            super.fall(par1);
        }
    }

    protected String func_146067_o(int p_146067_1_)
    {
        return p_146067_1_ > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(EntityLivingBase par1EntityLivingBase)
    {
        if (par1EntityLivingBase instanceof IMob)
        {
            triggerAchievement(AchievementList.killEnemy);
        }

        int var2 = EntityList.getEntityID(par1EntityLivingBase);
        EntityList.EntityEggInfo var3 = (EntityList.EntityEggInfo)EntityList.entityEggs.get(Integer.valueOf(var2));

        if (var3 != null)
        {
            addStat(var3.field_151512_d, 1);
        }
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
        if (!capabilities.isFlying)
        {
            super.setInWeb();
        }
    }

    public ItemStack getCurrentArmor(int par1)
    {
        return inventory.armorItemInSlot(par1);
    }

    /**
     * Add experience points to player.
     */
    public void addExperience(int par1)
    {
        addScore(par1);
        int var2 = Integer.MAX_VALUE - experienceTotal;

        if (par1 > var2)
        {
            par1 = var2;
        }

        experience += (float)par1 / (float)xpBarCap();

        for (experienceTotal += par1; experience >= 1.0F; experience /= xpBarCap())
        {
            experience = (experience - 1.0F) * xpBarCap();
            addExperienceLevel(1);
        }
    }

    /**
     * Add experience levels to this player.
     */
    public void addExperienceLevel(int par1)
    {
        experienceLevel += par1;

        if (experienceLevel < 0)
        {
            experienceLevel = 0;
            experience = 0.0F;
            experienceTotal = 0;
        }

        if (par1 > 0 && experienceLevel % 5 == 0 && field_82249_h < ticksExisted - 100.0F)
        {
            float var2 = experienceLevel > 30 ? 1.0F : experienceLevel / 30.0F;
            worldObj.playSoundAtEntity(this, "random.levelup", var2 * 0.75F, 1.0F);
            field_82249_h = ticksExisted;
        }
    }

    /**
     * This method returns the cap amount of experience that the experience bar
     * can hold. With each level, the experience cap on the player's experience
     * bar is raised by 10.
     */
    public int xpBarCap()
    {
        return experienceLevel >= 30 ? 62 + (experienceLevel - 30) * 7 : (experienceLevel >= 15 ? 17 + (experienceLevel - 15) * 3 : 17);
    }

    /**
     * increases exhaustion level by supplied amount
     */
    public void addExhaustion(float par1)
    {
        if (!capabilities.disableDamage)
        {
            if (!worldObj.isClient)
            {
                foodStats.addExhaustion(par1);
            }
        }
    }

    /**
     * Returns the player's FoodStats object.
     */
    public FoodStats getFoodStats()
    {
        return foodStats;
    }

    public boolean canEat(boolean par1)
    {
        return (par1 || foodStats.needFood()) && !capabilities.disableDamage;
    }

    /**
     * Checks if the player's health is not full and not zero.
     */
    public boolean shouldHeal()
    {
        return getHealth() > 0.0F && getHealth() < getMaxHealth();
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack,
     * int maxItemUseDuration
     */
    public void setItemInUse(ItemStack par1ItemStack, int par2)
    {
        if (par1ItemStack != itemInUse)
        {
            itemInUse = par1ItemStack;
            itemInUseCount = par2;

            if (!worldObj.isClient)
            {
                setEating(true);
            }
        }
    }

    /**
     * Returns true if the given block can be mined with the current tool in
     * adventure mode.
     */
    public boolean isCurrentToolAdventureModeExempt(int par1, int par2, int par3)
    {
        if (capabilities.allowEdit)
        {
            return true;
        }
        else
        {
            Block var4 = worldObj.getBlock(par1, par2, par3);

            if (var4.getMaterial() != Material.air)
            {
                if (var4.getMaterial().isAdventureModeExempt()) { return true; }

                if (getCurrentEquippedItem() != null)
                {
                    ItemStack var5 = getCurrentEquippedItem();

                    if (var5.func_150998_b(var4) || var5.func_150997_a(var4) > 1.0F) { return true; }
                }
            }

            return false;
        }
    }

    public boolean canPlayerEdit(int par1, int par2, int par3, int par4, ItemStack par5ItemStack)
    {
        return capabilities.allowEdit ? true : (par5ItemStack != null ? par5ItemStack.canEditBlocks() : false);
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        if (worldObj.getGameRules().getGameRuleBooleanValue("keepInventory") || (worldObj.getGameRules().getGameRuleBooleanValue("adminsKeepInventory") && MinecraftServer.isPlayerOpped(getUsername())))
        {
            return 0;
        }
        else
        {
            int var2 = experienceLevel * 7;
            return var2 > 100 ? 100 : var2;
        }
    }

    /**
     * Only use is to identify if class is an instance of player for experience
     * dropping
     */
    protected boolean isPlayer()
    {
        return true;
    }

    /**
     * Copies the values from the given player into this player if boolean par2
     * is true. Always clones Ender Chest Inventory.
     */
    public void clonePlayer(EntityPlayer par1EntityPlayer, boolean par2)
    {
        if (par2)
        {
            inventory.copyInventory(par1EntityPlayer.inventory);
            setHealth(par1EntityPlayer.getHealth());
            foodStats = par1EntityPlayer.foodStats;
            experienceLevel = par1EntityPlayer.experienceLevel;
            experienceTotal = par1EntityPlayer.experienceTotal;
            experience = par1EntityPlayer.experience;
            setScore(par1EntityPlayer.getScore());
            teleportDirection = par1EntityPlayer.teleportDirection;
        }
        else if (worldObj.getGameRules().getGameRuleBooleanValue("keepInventory") || (worldObj.getGameRules().getGameRuleBooleanValue("adminsKeepInventory") && MinecraftServer.isPlayerOpped(getUsername())))
        {
            inventory.copyInventory(par1EntityPlayer.inventory);
            experienceLevel = par1EntityPlayer.experienceLevel;
            experienceTotal = par1EntityPlayer.experienceTotal;
            experience = par1EntityPlayer.experience;
            setScore(par1EntityPlayer.getScore());
        }

        theInventoryEnderChest = par1EntityPlayer.theInventoryEnderChest;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return !capabilities.isFlying;
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void sendPlayerAbilities()
    {
    }

    /**
     * Sets the player's game mode and sends it to them.
     */
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
    }

    /**
     * Gets the name of this command sender (usually username, but possibly
     * "Rcon")
     */
    public String getUsername()
    {
        return field_146106_i.getName();
    }

    public World getEntityWorld()
    {
        return worldObj;
    }

    /**
     * Returns the InventoryEnderChest of this player.
     */
    public InventoryEnderChest getInventoryEnderChest()
    {
        return theInventoryEnderChest;
    }

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public ItemStack getEquipmentInSlot(int par1)
    {
        return par1 == 0 ? inventory.getCurrentItem() : inventory.armorInventory[par1 - 1];
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public ItemStack getHeldItem()
    {
        return inventory.getCurrentItem();
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is
     * armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack)
    {
        inventory.armorInventory[par1] = par2ItemStack;
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it
     * seems)
     */
    public ItemStack[] getInventory()
    {
        return inventory.armorInventory;
    }

    public boolean isPushedByWater()
    {
        return !capabilities.isFlying;
    }

    public Scoreboard getWorldScoreboard()
    {
        return worldObj.getScoreboard();
    }

    public Team getTeam()
    {
        return getWorldScoreboard().getPlayersTeam(getUsername());
    }

    public IChatComponent getUsernameAsIChatComponent()
    {
        ChatComponentText var1 = new ChatComponentText(ScorePlayerTeam.formatPlayerName(getTeam(), getUsername()));
        var1.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + getUsername() + " "));
        return var1;
    }

    public void setAbsorptionAmount(float par1)
    {
        if (par1 < 0.0F)
        {
            par1 = 0.0F;
        }

        getDataWatcher().updateObject(17, Float.valueOf(par1));
    }

    public float getAbsorptionAmount()
    {
        return getDataWatcher().getWatchableObjectFloat(17);
    }

    public static UUID func_146094_a(GameProfile p_146094_0_)
    {
        UUID var1 = Util.tryGetUUIDFromString(p_146094_0_.getId());

        if (var1 == null)
        {
            var1 = UUID.nameUUIDFromBytes(("OfflinePlayer:" + p_146094_0_.getName()).getBytes(Charsets.UTF_8));
        }

        return var1;
    }

    public static enum EnumStatus
    {
        OK("OK", 0), NOT_POSSIBLE_HERE("NOT_POSSIBLE_HERE", 1), NOT_POSSIBLE_NOW("NOT_POSSIBLE_NOW", 2), TOO_FAR_AWAY("TOO_FAR_AWAY", 3), OTHER_PROBLEM("OTHER_PROBLEM", 4), NOT_SAFE("NOT_SAFE", 5);

        private static final EntityPlayer.EnumStatus[] $VALUES = new EntityPlayer.EnumStatus[] {OK, NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW, TOO_FAR_AWAY, OTHER_PROBLEM, NOT_SAFE};
        private static final String __OBFID = "CL_00001712";

        private EnumStatus(String par1Str, int par2)
        {
        }
    }

    public static enum EnumChatVisibility
    {
        FULL("FULL", 0, 0, "options.chat.visibility.full"), SYSTEM("SYSTEM", 1, 1, "options.chat.visibility.system"), HIDDEN("HIDDEN", 2, 2, "options.chat.visibility.hidden");
        private static final EntityPlayer.EnumChatVisibility[] field_151432_d = new EntityPlayer.EnumChatVisibility[values().length];
        private final int field_151433_e;
        private final String field_151430_f;

        private static final EntityPlayer.EnumChatVisibility[] $VALUES = new EntityPlayer.EnumChatVisibility[] {FULL, SYSTEM, HIDDEN};
        private static final String __OBFID = "CL_00001714";

        private EnumChatVisibility(String p_i45323_1_, int p_i45323_2_, int p_i45323_3_, String p_i45323_4_)
        {
            field_151433_e = p_i45323_3_;
            field_151430_f = p_i45323_4_;
        }

        public int func_151428_a()
        {
            return field_151433_e;
        }

        public static EntityPlayer.EnumChatVisibility func_151426_a(int p_151426_0_)
        {
            return field_151432_d[p_151426_0_ % field_151432_d.length];
        }

        static
        {
            EntityPlayer.EnumChatVisibility[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                EntityPlayer.EnumChatVisibility var3 = var0[var2];
                field_151432_d[var3.field_151433_e] = var3;
            }
        }
    }
}
