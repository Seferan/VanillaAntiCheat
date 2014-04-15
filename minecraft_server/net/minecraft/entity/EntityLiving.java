package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLiving extends EntityLivingBase
{
    /** Number of ticks since this EntityLiving last produced its sound */
    public int livingSoundTime;

    /** The experience points the Entity gives. */
    protected int experienceValue;
    private EntityLookHelper lookHelper;
    private EntityMoveHelper moveHelper;

    /** Entity jumping helper */
    private EntityJumpHelper jumpHelper;
    private EntityBodyHelper bodyHelper;
    private PathNavigate navigator;
    protected final EntityAITasks tasks;
    protected final EntityAITasks targetTasks;

    /** The active target the Task system uses for tracking */
    private EntityLivingBase attackTarget;
    private EntitySenses senses;

    /** Equipment (armor and held item) for this entity. */
    private ItemStack[] equipment = new ItemStack[5];

    /** Chances for each equipment piece from dropping when this entity dies. */
    protected float[] equipmentDropChances = new float[5];

    /** Whether this entity can pick up items from the ground. */
    private boolean canPickUpLoot;

    /** Whether this entity should NOT despawn. */
    private boolean persistenceRequired;
    protected float defaultPitch;

    /** This entity's current target. */
    private Entity currentTarget;

    /** How long to keep a specific target entity */
    protected int numTicksToChaseTarget;
    private boolean isLeashed;
    private Entity leashedToEntity;
    private NBTTagCompound field_110170_bx;
    private static final String __OBFID = "CL_00001550";

    public EntityLiving(World par1World)
    {
        super(par1World);
        tasks = new EntityAITasks(par1World != null && par1World.theProfiler != null ? par1World.theProfiler : null);
        targetTasks = new EntityAITasks(par1World != null && par1World.theProfiler != null ? par1World.theProfiler : null);
        lookHelper = new EntityLookHelper(this);
        moveHelper = new EntityMoveHelper(this);
        jumpHelper = new EntityJumpHelper(this);
        bodyHelper = new EntityBodyHelper(this);
        navigator = new PathNavigate(this, par1World);
        senses = new EntitySenses(this);

        for (int var2 = 0; var2 < equipmentDropChances.length; ++var2)
        {
            equipmentDropChances[var2] = 0.085F;
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
    }

    public EntityLookHelper getLookHelper()
    {
        return lookHelper;
    }

    public EntityMoveHelper getMoveHelper()
    {
        return moveHelper;
    }

    public EntityJumpHelper getJumpHelper()
    {
        return jumpHelper;
    }

    public PathNavigate getNavigator()
    {
        return navigator;
    }

    /**
     * returns the EntitySenses Object for the EntityLiving
     */
    public EntitySenses getEntitySenses()
    {
        return senses;
    }

    /**
     * Gets the active target the Task system uses for tracking
     */
    public EntityLivingBase getAttackTarget()
    {
        return attackTarget;
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(EntityLivingBase par1EntityLivingBase)
    {
        attackTarget = par1EntityLivingBase;
    }

    /**
     * Returns true if this entity can attack entities of the specified class.
     */
    public boolean canAttackClass(Class par1Class)
    {
        return EntityCreeper.class != par1Class && EntityGhast.class != par1Class;
    }

    /**
     * This function applies the benefits of growing back wool and faster
     * growing up to the acting entity. (This function is used in the
     * AIEatGrass)
     */
    public void eatGrassBonus()
    {
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(11, Byte.valueOf((byte)0));
        dataWatcher.addObject(10, "");
    }

    /**
     * Get number of ticks, at least during which the living entity will be
     * silent.
     */
    public int getTalkInterval()
    {
        return 80;
    }

    /**
     * Plays living's sound at its position
     */
    public void playLivingSound()
    {
        String var1 = getLivingSound();

        if (var1 != null)
        {
            playSound(var1, getSoundVolume(), getSoundPitch());
        }
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        super.onEntityUpdate();
        worldObj.theProfiler.startSection("mobBaseTick");

        if (isEntityAlive() && rand.nextInt(1000) < livingSoundTime++)
        {
            livingSoundTime = -getTalkInterval();
            playLivingSound();
        }

        worldObj.theProfiler.endSection();
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        if (experienceValue > 0)
        {
            int var2 = experienceValue;
            ItemStack[] var3 = getInventory();

            for (int var4 = 0; var4 < var3.length; ++var4)
            {
                if (var3[var4] != null && equipmentDropChances[var4] <= 1.0F)
                {
                    var2 += 1 + rand.nextInt(3);
                }
            }

            return var2;
        }
        else
        {
            return experienceValue;
        }
    }

    /**
     * Spawns an explosion particle around the Entity's location
     */
    public void spawnExplosionParticle()
    {
        for (int var1 = 0; var1 < 20; ++var1)
        {
            double var2 = rand.nextGaussian() * 0.02D;
            double var4 = rand.nextGaussian() * 0.02D;
            double var6 = rand.nextGaussian() * 0.02D;
            double var8 = 10.0D;
            worldObj.spawnParticle("explode", posX + rand.nextFloat() * width * 2.0F - width - var2 * var8, posY + rand.nextFloat() * height - var4 * var8, posZ + rand.nextFloat() * width * 2.0F - width - var6 * var8, var2, var4, var6);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!worldObj.isClient)
        {
            updateLeashedState();
        }
    }

    protected float func_110146_f(float par1, float par2)
    {
        if (isAIEnabled())
        {
            bodyHelper.func_75664_a();
            return par2;
        }
        else
        {
            return super.func_110146_f(par1, par2);
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return null;
    }

    protected Item func_146068_u()
    {
        return Item.getItemById(0);
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        Item var3 = func_146068_u();

        if (var3 != null)
        {
            int var4 = rand.nextInt(3);

            if (par2 > 0)
            {
                var4 += rand.nextInt(par2 + 1);
            }

            for (int var5 = 0; var5 < var4; ++var5)
            {
                func_145779_a(var3, 1);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("CanPickUpLoot", canPickUpLoot());
        par1NBTTagCompound.setBoolean("PersistenceRequired", persistenceRequired);
        NBTTagList var2 = new NBTTagList();
        NBTTagCompound var4;

        for (int var3 = 0; var3 < equipment.length; ++var3)
        {
            var4 = new NBTTagCompound();

            if (equipment[var3] != null)
            {
                equipment[var3].writeToNBT(var4);
            }

            var2.appendTag(var4);
        }

        par1NBTTagCompound.setTag("Equipment", var2);
        NBTTagList var6 = new NBTTagList();

        for (int var7 = 0; var7 < equipmentDropChances.length; ++var7)
        {
            var6.appendTag(new NBTTagFloat(equipmentDropChances[var7]));
        }

        par1NBTTagCompound.setTag("DropChances", var6);
        par1NBTTagCompound.setString("CustomName", getCustomNameTag());
        par1NBTTagCompound.setBoolean("CustomNameVisible", getAlwaysRenderNameTag());
        par1NBTTagCompound.setBoolean("Leashed", isLeashed);

        if (leashedToEntity != null)
        {
            var4 = new NBTTagCompound();

            if (leashedToEntity instanceof EntityLivingBase)
            {
                var4.setLong("UUIDMost", leashedToEntity.getUniqueID().getMostSignificantBits());
                var4.setLong("UUIDLeast", leashedToEntity.getUniqueID().getLeastSignificantBits());
            }
            else if (leashedToEntity instanceof EntityHanging)
            {
                EntityHanging var5 = (EntityHanging)leashedToEntity;
                var4.setInteger("X", var5.field_146063_b);
                var4.setInteger("Y", var5.field_146064_c);
                var4.setInteger("Z", var5.field_146062_d);
            }

            par1NBTTagCompound.setTag("Leash", var4);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        setCanPickUpLoot(par1NBTTagCompound.getBoolean("CanPickUpLoot"));
        persistenceRequired = par1NBTTagCompound.getBoolean("PersistenceRequired");

        if (par1NBTTagCompound.func_150297_b("CustomName", 8) && par1NBTTagCompound.getString("CustomName").length() > 0)
        {
            setCustomNameTag(par1NBTTagCompound.getString("CustomName"));
        }

        setAlwaysRenderNameTag(par1NBTTagCompound.getBoolean("CustomNameVisible"));
        NBTTagList var2;
        int var3;

        if (par1NBTTagCompound.func_150297_b("Equipment", 9))
        {
            var2 = par1NBTTagCompound.getTagList("Equipment", 10);

            for (var3 = 0; var3 < equipment.length; ++var3)
            {
                equipment[var3] = ItemStack.loadItemStackFromNBT(var2.getCompoundTagAt(var3));
            }
        }

        if (par1NBTTagCompound.func_150297_b("DropChances", 9))
        {
            var2 = par1NBTTagCompound.getTagList("DropChances", 5);

            for (var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                equipmentDropChances[var3] = var2.func_150308_e(var3);
            }
        }

        isLeashed = par1NBTTagCompound.getBoolean("Leashed");

        if (isLeashed && par1NBTTagCompound.func_150297_b("Leash", 10))
        {
            field_110170_bx = par1NBTTagCompound.getCompoundTag("Leash");
        }
    }

    public void setMoveForward(float par1)
    {
        moveForward = par1;
    }

    /**
     * set the movespeed used for the new AI system
     */
    public void setAIMoveSpeed(float par1)
    {
        super.setAIMoveSpeed(par1);
        setMoveForward(par1);
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        worldObj.theProfiler.startSection("looting");

        if (!worldObj.isClient && canPickUpLoot() && !dead && worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
        {
            List var1 = worldObj.getEntitiesWithinAABB(EntityItem.class, boundingBox.expand(1.0D, 0.0D, 1.0D));
            Iterator var2 = var1.iterator();

            while (var2.hasNext())
            {
                EntityItem var3 = (EntityItem)var2.next();

                if (!var3.isDead && var3.getEntityItem() != null)
                {
                    ItemStack var4 = var3.getEntityItem();
                    int var5 = getArmorPosition(var4);

                    if (var5 > -1)
                    {
                        boolean var6 = true;
                        ItemStack var7 = getEquipmentInSlot(var5);

                        if (var7 != null)
                        {
                            if (var5 == 0)
                            {
                                if (var4.getItem() instanceof ItemSword && !(var7.getItem() instanceof ItemSword))
                                {
                                    var6 = true;
                                }
                                else if (var4.getItem() instanceof ItemSword && var7.getItem() instanceof ItemSword)
                                {
                                    ItemSword var8 = (ItemSword)var4.getItem();
                                    ItemSword var9 = (ItemSword)var7.getItem();

                                    if (var8.func_150931_i() == var9.func_150931_i())
                                    {
                                        var6 = var4.getItemDamage() > var7.getItemDamage() || var4.hasTagCompound() && !var7.hasTagCompound();
                                    }
                                    else
                                    {
                                        var6 = var8.func_150931_i() > var9.func_150931_i();
                                    }
                                }
                                else
                                {
                                    var6 = false;
                                }
                            }
                            else if (var4.getItem() instanceof ItemArmor && !(var7.getItem() instanceof ItemArmor))
                            {
                                var6 = true;
                            }
                            else if (var4.getItem() instanceof ItemArmor && var7.getItem() instanceof ItemArmor)
                            {
                                ItemArmor var11 = (ItemArmor)var4.getItem();
                                ItemArmor var12 = (ItemArmor)var7.getItem();

                                if (var11.damageReduceAmount == var12.damageReduceAmount)
                                {
                                    var6 = var4.getItemDamage() > var7.getItemDamage() || var4.hasTagCompound() && !var7.hasTagCompound();
                                }
                                else
                                {
                                    var6 = var11.damageReduceAmount > var12.damageReduceAmount;
                                }
                            }
                            else
                            {
                                var6 = false;
                            }
                        }

                        if (var6)
                        {
                            if (var7 != null && rand.nextFloat() - 0.1F < equipmentDropChances[var5])
                            {
                                entityDropItem(var7, 0.0F);
                            }

                            if (var4.getItem() == Items.diamond && var3.getThrower() != null)
                            {
                                EntityPlayer var10 = worldObj.getPlayerEntityByName(var3.getThrower());

                                if (var10 != null)
                                {
                                    var10.triggerAchievement(AchievementList.field_150966_x);
                                }
                            }

                            setCurrentItemOrArmor(var5, var4);
                            equipmentDropChances[var5] = 2.0F;
                            persistenceRequired = true;
                            onItemPickup(var3, 1);
                            var3.setDead();
                        }
                    }
                }
            }
        }

        worldObj.theProfiler.endSection();
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return false;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return true;
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void despawnEntity()
    {
        if (persistenceRequired)
        {
            entityAge = 0;
        }
        else
        {
            EntityPlayer var1 = worldObj.getClosestPlayerToEntity(this, -1.0D);

            if (var1 != null)
            {
                double var2 = var1.posX - posX;
                double var4 = var1.posY - posY;
                double var6 = var1.posZ - posZ;
                double var8 = var2 * var2 + var4 * var4 + var6 * var6;

                if (canDespawn() && var8 > 16384.0D)
                {
                    setDead();
                }

                if (entityAge > 600 && rand.nextInt(800) == 0 && var8 > 1024.0D && canDespawn())
                {
                    setDead();
                }
                else if (var8 < 1024.0D)
                {
                    entityAge = 0;
                }
            }
        }
    }

    protected void updateAITasks()
    {
        ++entityAge;
        worldObj.theProfiler.startSection("checkDespawn");
        despawnEntity();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("sensing");
        senses.clearSensingCache();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("targetSelector");
        targetTasks.onUpdateTasks();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("goalSelector");
        tasks.onUpdateTasks();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("navigation");
        navigator.onUpdateNavigation();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("mob tick");
        updateAITick();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("controls");
        worldObj.theProfiler.startSection("move");
        moveHelper.onUpdateMoveHelper();
        worldObj.theProfiler.endStartSection("look");
        lookHelper.onUpdateLook();
        worldObj.theProfiler.endStartSection("jump");
        jumpHelper.doJump();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.endSection();
    }

    protected void updateEntityActionState()
    {
        super.updateEntityActionState();
        moveStrafing = 0.0F;
        moveForward = 0.0F;
        despawnEntity();
        float var1 = 8.0F;

        if (rand.nextFloat() < 0.02F)
        {
            EntityPlayer var2 = worldObj.getClosestPlayerToEntity(this, var1);

            if (var2 != null)
            {
                currentTarget = var2;
                numTicksToChaseTarget = 10 + rand.nextInt(20);
            }
            else
            {
                randomYawVelocity = (rand.nextFloat() - 0.5F) * 20.0F;
            }
        }

        if (currentTarget != null)
        {
            faceEntity(currentTarget, 10.0F, getVerticalFaceSpeed());

            if (numTicksToChaseTarget-- <= 0 || currentTarget.isDead || currentTarget.getDistanceSqToEntity(this) > var1 * var1)
            {
                currentTarget = null;
            }
        }
        else
        {
            if (rand.nextFloat() < 0.05F)
            {
                randomYawVelocity = (rand.nextFloat() - 0.5F) * 20.0F;
            }

            rotationYaw += randomYawVelocity;
            rotationPitch = defaultPitch;
        }

        boolean var4 = isInWater();
        boolean var3 = handleLavaMovement();

        if (var4 || var3)
        {
            isJumping = rand.nextFloat() < 0.8F;
        }
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the
     * faceEntity method. This is only currently use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return 40;
    }

    /**
     * Changes pitch and yaw so that the entity calling the function is facing
     * the entity provided as an argument.
     */
    public void faceEntity(Entity par1Entity, float par2, float par3)
    {
        double var4 = par1Entity.posX - posX;
        double var8 = par1Entity.posZ - posZ;
        double var6;

        if (par1Entity instanceof EntityLivingBase)
        {
            EntityLivingBase var10 = (EntityLivingBase)par1Entity;
            var6 = var10.posY + var10.getEyeHeight() - (posY + getEyeHeight());
        }
        else
        {
            var6 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D - (posY + getEyeHeight());
        }

        double var14 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float)(Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
        float var13 = (float)(-(Math.atan2(var6, var14) * 180.0D / Math.PI));
        rotationPitch = updateRotation(rotationPitch, var13, par3);
        rotationYaw = updateRotation(rotationYaw, var12, par2);
    }

    /**
     * Arguments: current rotation, intended rotation, max increment.
     */
    private float updateRotation(float par1, float par2, float par3)
    {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (var4 > par3)
        {
            var4 = par3;
        }

        if (var4 < -par3)
        {
            var4 = -par3;
        }

        return par1 + var4;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this
     * entity.
     */
    public boolean getCanSpawnHere()
    {
        return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

    /**
     * The number of iterations PathFinder.getSafePoint will execute before
     * giving up.
     */
    public int getMaxSafePointTries()
    {
        if (getAttackTarget() == null)
        {
            return 3;
        }
        else
        {
            int var1 = (int)(getHealth() - getMaxHealth() * 0.33F);
            var1 -= (3 - worldObj.difficultySetting.func_151525_a()) * 4;

            if (var1 < 0)
            {
                var1 = 0;
            }

            return var1 + 3;
        }
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public ItemStack getHeldItem()
    {
        return equipment[0];
    }

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public ItemStack getEquipmentInSlot(int par1)
    {
        return equipment[par1];
    }

    public ItemStack func_130225_q(int par1)
    {
        return equipment[par1 + 1];
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is
     * armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack)
    {
        equipment[par1] = par2ItemStack;
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it
     * seems)
     */
    public ItemStack[] getInventory()
    {
        return equipment;
    }

    /**
     * Drop the equipment for this entity.
     */
    protected void dropEquipment(boolean par1, int par2)
    {
        for (int var3 = 0; var3 < getInventory().length; ++var3)
        {
            ItemStack var4 = getEquipmentInSlot(var3);
            boolean var5 = equipmentDropChances[var3] > 1.0F;

            if (var4 != null && (par1 || var5) && rand.nextFloat() - par2 * 0.01F < equipmentDropChances[var3])
            {
                if (!var5 && var4.isItemStackDamageable())
                {
                    int var6 = Math.max(var4.getMaxDamage() - 25, 1);
                    int var7 = var4.getMaxDamage() - rand.nextInt(rand.nextInt(var6) + 1);

                    if (var7 > var6)
                    {
                        var7 = var6;
                    }

                    if (var7 < 1)
                    {
                        var7 = 1;
                    }

                    var4.setItemDamage(var7);
                }

                entityDropItem(var4, 0.0F);
            }
        }
    }

    /**
     * Makes entity wear random armor based on difficulty
     */
    protected void addRandomArmor()
    {
        if (rand.nextFloat() < 0.15F * worldObj.func_147462_b(posX, posY, posZ))
        {
            int var1 = rand.nextInt(2);
            float var2 = worldObj.difficultySetting == EnumDifficulty.HARD ? 0.1F : 0.25F;

            if (rand.nextFloat() < 0.095F)
            {
                ++var1;
            }

            if (rand.nextFloat() < 0.095F)
            {
                ++var1;
            }

            if (rand.nextFloat() < 0.095F)
            {
                ++var1;
            }

            for (int var3 = 3; var3 >= 0; --var3)
            {
                ItemStack var4 = func_130225_q(var3);

                if (var3 < 3 && rand.nextFloat() < var2)
                {
                    break;
                }

                if (var4 == null)
                {
                    Item var5 = getArmorItemForSlot(var3 + 1, var1);

                    if (var5 != null)
                    {
                        setCurrentItemOrArmor(var3 + 1, new ItemStack(var5));
                    }
                }
            }
        }
    }

    public static int getArmorPosition(ItemStack par0ItemStack)
    {
        if (par0ItemStack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && par0ItemStack.getItem() != Items.skull)
        {
            if (par0ItemStack.getItem() instanceof ItemArmor)
            {
                switch (((ItemArmor)par0ItemStack.getItem()).armorType)
                {
                case 0:
                    return 4;

                case 1:
                    return 3;

                case 2:
                    return 2;

                case 3:
                    return 1;
                }
            }

            return 0;
        }
        else
        {
            return 4;
        }
    }

    /**
     * Params: Armor slot, Item tier
     */
    public static Item getArmorItemForSlot(int par0, int par1)
    {
        switch (par0)
        {
        case 4:
            if (par1 == 0)
            {
                return Items.leather_helmet;
            }
            else if (par1 == 1)
            {
                return Items.golden_helmet;
            }
            else if (par1 == 2)
            {
                return Items.chainmail_helmet;
            }
            else if (par1 == 3)
            {
                return Items.iron_helmet;
            }
            else if (par1 == 4) { return Items.diamond_helmet; }

        case 3:
            if (par1 == 0)
            {
                return Items.leather_chestplate;
            }
            else if (par1 == 1)
            {
                return Items.golden_chestplate;
            }
            else if (par1 == 2)
            {
                return Items.chainmail_chestplate;
            }
            else if (par1 == 3)
            {
                return Items.iron_chestplate;
            }
            else if (par1 == 4) { return Items.diamond_chestplate; }

        case 2:
            if (par1 == 0)
            {
                return Items.leather_leggings;
            }
            else if (par1 == 1)
            {
                return Items.golden_leggings;
            }
            else if (par1 == 2)
            {
                return Items.chainmail_leggings;
            }
            else if (par1 == 3)
            {
                return Items.iron_leggings;
            }
            else if (par1 == 4) { return Items.diamond_leggings; }

        case 1:
            if (par1 == 0)
            {
                return Items.leather_boots;
            }
            else if (par1 == 1)
            {
                return Items.golden_boots;
            }
            else if (par1 == 2)
            {
                return Items.chainmail_boots;
            }
            else if (par1 == 3)
            {
                return Items.iron_boots;
            }
            else if (par1 == 4) { return Items.diamond_boots; }

        default:
            return null;
        }
    }

    /**
     * Enchants the entity's armor and held item based on difficulty
     */
    protected void enchantEquipment()
    {
        float var1 = worldObj.func_147462_b(posX, posY, posZ);

        if (getHeldItem() != null && rand.nextFloat() < 0.25F * var1)
        {
            EnchantmentHelper.addRandomEnchantment(rand, getHeldItem(), (int)(5.0F + var1 * rand.nextInt(18)));
        }

        for (int var2 = 0; var2 < 4; ++var2)
        {
            ItemStack var3 = func_130225_q(var2);

            if (var3 != null && rand.nextFloat() < 0.5F * var1)
            {
                EnchantmentHelper.addRandomEnchantment(rand, var3, (int)(5.0F + var1 * rand.nextInt(18)));
            }
        }
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random spawn bonus", rand.nextGaussian() * 0.05D, 1));
        return par1EntityLivingData;
    }

    /**
     * returns true if all the conditions for steering the entity are met. For
     * pigs, this is true if it is being ridden by a player and the player is
     * holding a carrot-on-a-stick
     */
    public boolean canBeSteered()
    {
        return false;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly
     * "Rcon")
     */
    public String getUsername()
    {
        return hasCustomNameTag() ? getCustomNameTag() : super.getUsername();
    }

    public void func_110163_bv()
    {
        persistenceRequired = true;
    }

    public void setCustomNameTag(String par1Str)
    {
        dataWatcher.updateObject(10, par1Str);
    }

    public String getCustomNameTag()
    {
        return dataWatcher.getWatchableObjectString(10);
    }

    public boolean hasCustomNameTag()
    {
        return dataWatcher.getWatchableObjectString(10).length() > 0;
    }

    public void setAlwaysRenderNameTag(boolean par1)
    {
        dataWatcher.updateObject(11, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    public boolean getAlwaysRenderNameTag()
    {
        return dataWatcher.getWatchableObjectByte(11) == 1;
    }

    public void setEquipmentDropChance(int par1, float par2)
    {
        equipmentDropChances[par1] = par2;
    }

    public boolean canPickUpLoot()
    {
        return canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean par1)
    {
        canPickUpLoot = par1;
    }

    public boolean isNoDespawnRequired()
    {
        return persistenceRequired;
    }

    /**
     * First layer of player interaction
     */
    public final boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        if (getLeashed() && getLeashedToEntity() == par1EntityPlayer)
        {
            clearLeashed(true, !par1EntityPlayer.capabilities.isCreativeMode);
            return true;
        }
        else
        {
            ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

            if (var2 != null && var2.getItem() == Items.lead && allowLeashing())
            {
                if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed())
                {
                    setLeashedToEntity(par1EntityPlayer, true);
                    --var2.stackSize;
                    return true;
                }

                if (par1EntityPlayer.getUsername().equalsIgnoreCase(((EntityTameable)this).getOwnerName()))
                {
                    setLeashedToEntity(par1EntityPlayer, true);
                    --var2.stackSize;
                    return true;
                }
            }

            return interact(par1EntityPlayer) ? true : super.interactFirst(par1EntityPlayer);
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    protected boolean interact(EntityPlayer par1EntityPlayer)
    {
        return false;
    }

    /**
     * Applies logic related to leashes, for example dragging the entity or
     * breaking the leash.
     */
    protected void updateLeashedState()
    {
        if (field_110170_bx != null)
        {
            recreateLeash();
        }

        if (isLeashed)
        {
            if (leashedToEntity == null || leashedToEntity.isDead)
            {
                clearLeashed(true, true);
            }
        }
    }

    /**
     * Removes the leash from this entity. Second parameter tells whether to
     * send a packet to surrounding players.
     */
    public void clearLeashed(boolean par1, boolean par2)
    {
        if (isLeashed)
        {
            isLeashed = false;
            leashedToEntity = null;

            if (!worldObj.isClient && par2)
            {
                func_145779_a(Items.lead, 1);
            }

            if (!worldObj.isClient && par1 && worldObj instanceof WorldServer)
            {
                ((WorldServer)worldObj).getEntityTracker().func_151247_a(this, new S1BPacketEntityAttach(1, this, (Entity)null));
            }
        }
    }

    public boolean allowLeashing()
    {
        return !getLeashed() && !(this instanceof IMob);
    }

    public boolean getLeashed()
    {
        return isLeashed;
    }

    public Entity getLeashedToEntity()
    {
        return leashedToEntity;
    }

    /**
     * Sets the entity to be leashed to.\nArgs:\n@param par1Entity: The entity
     * to be tethered to.\n@param par2: Whether to send an attaching
     * notification packet to surrounding players.
     */
    public void setLeashedToEntity(Entity par1Entity, boolean par2)
    {
        isLeashed = true;
        leashedToEntity = par1Entity;

        if (!worldObj.isClient && par2 && worldObj instanceof WorldServer)
        {
            ((WorldServer)worldObj).getEntityTracker().func_151247_a(this, new S1BPacketEntityAttach(1, this, leashedToEntity));
        }
    }

    private void recreateLeash()
    {
        if (isLeashed && field_110170_bx != null)
        {
            if (field_110170_bx.func_150297_b("UUIDMost", 4) && field_110170_bx.func_150297_b("UUIDLeast", 4))
            {
                UUID var5 = new UUID(field_110170_bx.getLong("UUIDMost"), field_110170_bx.getLong("UUIDLeast"));
                List var6 = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox.expand(10.0D, 10.0D, 10.0D));
                Iterator var7 = var6.iterator();

                while (var7.hasNext())
                {
                    EntityLivingBase var8 = (EntityLivingBase)var7.next();

                    if (var8.getUniqueID().equals(var5))
                    {
                        leashedToEntity = var8;
                        break;
                    }
                }
            }
            else if (field_110170_bx.func_150297_b("X", 99) && field_110170_bx.func_150297_b("Y", 99) && field_110170_bx.func_150297_b("Z", 99))
            {
                int var1 = field_110170_bx.getInteger("X");
                int var2 = field_110170_bx.getInteger("Y");
                int var3 = field_110170_bx.getInteger("Z");
                EntityLeashKnot var4 = EntityLeashKnot.getKnotForBlock(worldObj, var1, var2, var3);

                if (var4 == null)
                {
                    var4 = EntityLeashKnot.func_110129_a(worldObj, var1, var2, var3);
                }

                leashedToEntity = var4;
            }
            else
            {
                clearLeashed(false, true);
            }
        }

        field_110170_bx = null;
    }
}
