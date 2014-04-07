package net.minecraft.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity
{
    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier sprintingSpeedBoostModifier = (new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
    private BaseAttributeMap attributeMap;
    private final CombatTracker _combatTracker = new CombatTracker(this);
    private final HashMap activePotionsMap = new HashMap();

    /** The equipment this mob was previously wearing, used for syncing. */
    private final ItemStack[] previousEquipment = new ItemStack[5];

    /** Whether an arm swing is currently in progress. */
    public boolean isSwingInProgress;
    public int swingProgressInt;
    public int arrowHitTimer;
    public float prevHealth;

    /**
     * The amount of time remaining this entity should act 'hurt'. (Visual
     * appearance of red tint)
     */
    public int hurtTime;

    /** What the hurt time was max set to last. */
    public int maxHurtTime;

    /** The yaw at which this entity was last attacked from. */
    public float attackedAtYaw;

    /**
     * The amount of time remaining this entity should act 'dead', i.e. have a
     * corpse in the world.
     */
    public int deathTime;
    public int attackTime;
    public float prevSwingProgress;
    public float swingProgress;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;

    /**
     * Only relevant when limbYaw is not 0(the entity is moving). Influences
     * where in its swing legs and arms currently are.
     */
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float field_70769_ao;
    public float field_70770_ap;
    public float renderYawOffset;
    public float prevRenderYawOffset;

    /** Entity head rotation yaw */
    public float rotationYawHead;

    /** Entity head rotation yaw at previous tick */
    public float prevRotationYawHead;

    /**
     * A factor used to determine how far this entity will move each tick if it
     * is jumping or falling.
     */
    public float jumpMovementFactor = 0.02F;

    /** The most recent player that has attacked this entity */
    protected EntityPlayer attackingPlayer;

    /**
     * Set to 60 when hit by the player or the player's wolf, then decrements.
     * Used to determine whether the entity should drop items on death.
     */
    protected int recentlyHit;

    /**
     * This gets set on entity death, but never used. Looks like a duplicate of
     * isDead
     */
    protected boolean dead;

    /** The age of this EntityLiving (used to determine when it dies) */
    protected int entityAge;
    protected float field_70768_au;
    protected float field_110154_aX;
    protected float field_70764_aw;
    protected float field_70763_ax;
    protected float field_70741_aB;

    /** The score value of the Mob, the amount of points the mob is worth. */
    protected int scoreValue;

    /**
     * Damage taken in the last hit. Mobs are resistant to damage less than this
     * for a short time after taking damage.
     */
    protected float lastDamage;

    /** used to check whether entity is jumping. */
    protected boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;

    /**
     * The number of updates over which the new position and rotation are to be
     * applied to the entity.
     */
    protected int newPosRotationIncrements;

    /** The new X position to be applied to the entity. */
    protected double newPosX;

    /** The new Y position to be applied to the entity. */
    protected double newPosY;
    protected double newPosZ;

    /** The new yaw rotation to be applied to the entity. */
    protected double newRotationYaw;

    /** The new yaw rotation to be applied to the entity. */
    protected double newRotationPitch;

    /** Whether the DataWatcher needs to be updated with the active potions */
    private boolean potionsNeedUpdate = true;

    /** is only being set, has no uses as of MC 1.1 */
    private EntityLivingBase entityLivingToAttack;
    private int revengeTimer;
    private EntityLivingBase lastAttacker;

    /** Holds the value of ticksExisted when setLastAttacker was last called. */
    private int lastAttackerTime;

    /**
     * A factor used to determine how far this entity will move each tick if it
     * is walking on land. Adjusted by speed, and slipperiness of the current
     * block.
     */
    private float landMovementFactor;

    /** Number of ticks since last jump */
    private int jumpTicks;
    private float field_110151_bq;
    private static final String __OBFID = "CL_00001549";

    public EntityLivingBase(World par1World)
    {
        super(par1World);
        applyEntityAttributes();
        setHealth(getMaxHealth());
        preventEntitySpawning = true;
        field_70770_ap = (float)(Math.random() + 1.0D) * 0.01F;
        setPosition(posX, posY, posZ);
        field_70769_ao = (float)Math.random() * 12398.0F;
        rotationYaw = (float)(Math.random() * Math.PI * 2.0D);
        rotationYawHead = rotationYaw;
        stepHeight = 0.5F;
    }

    protected void entityInit()
    {
        dataWatcher.addObject(7, Integer.valueOf(0));
        dataWatcher.addObject(8, Byte.valueOf((byte)0));
        dataWatcher.addObject(9, Byte.valueOf((byte)0));
        dataWatcher.addObject(6, Float.valueOf(1.0F));
    }

    protected void applyEntityAttributes()
    {
        getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);

        if (!isAIEnabled())
        {
            getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.10000000149011612D);
        }
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on
     * the ground to update the fall distance and deal fall damage if landing on
     * the ground. Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
        if (!isInWater())
        {
            handleWaterMovement();
        }

        if (par3 && fallDistance > 0.0F)
        {
            int var4 = MathHelper.floor_double(posX);
            int var5 = MathHelper.floor_double(posY - 0.20000000298023224D - yOffset);
            int var6 = MathHelper.floor_double(posZ);
            Block var7 = worldObj.getBlock(var4, var5, var6);

            if (var7.getMaterial() == Material.air)
            {
                int var8 = worldObj.getBlock(var4, var5 - 1, var6).getRenderType();

                if (var8 == 11 || var8 == 32 || var8 == 21)
                {
                    var7 = worldObj.getBlock(var4, var5 - 1, var6);
                }
            }
            else if (!worldObj.isClient && fallDistance > 3.0F)
            {
                worldObj.playAuxSFX(2006, var4, var5, var6, MathHelper.ceiling_float_int(fallDistance - 3.0F));
            }

            var7.onFallenUpon(worldObj, var4, var5, var6, this, fallDistance);
        }

        super.updateFallState(par1, par3);
    }

    public boolean canBreatheUnderwater()
    {
        return false;
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        prevSwingProgress = swingProgress;
        super.onEntityUpdate();
        worldObj.theProfiler.startSection("livingEntityBaseTick");

        if (isEntityAlive() && isEntityInsideOpaqueBlock())
        {
            attackEntityFrom(DamageSource.inWall, 1.0F);
        }

        if (isImmuneToFire() || worldObj.isClient)
        {
            extinguish();
        }

        boolean var1 = this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.disableDamage;

        if (isEntityAlive() && isInsideOfMaterial(Material.field_151586_h))
        {
            if (!canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !var1)
            {
                setAir(decreaseAirSupply(getAir()));

                if (getAir() == -20)
                {
                    setAir(0);

                    for (int var2 = 0; var2 < 8; ++var2)
                    {
                        float var3 = rand.nextFloat() - rand.nextFloat();
                        float var4 = rand.nextFloat() - rand.nextFloat();
                        float var5 = rand.nextFloat() - rand.nextFloat();
                        worldObj.spawnParticle("bubble", posX + var3, posY + var4, posZ + var5, motionX, motionY, motionZ);
                    }

                    attackEntityFrom(DamageSource.drown, 2.0F);
                }
            }

            if (!worldObj.isClient && isRiding() && ridingEntity instanceof EntityLivingBase)
            {
                mountEntity((Entity)null);
            }
        }
        else
        {
            setAir(300);
        }

        if (isEntityAlive() && isWet())
        {
            extinguish();
        }

        prevCameraPitch = cameraPitch;

        if (attackTime > 0)
        {
            --attackTime;
        }

        if (hurtTime > 0)
        {
            --hurtTime;
        }

        if (hurtResistantTime > 0 && !(this instanceof EntityPlayerMP))
        {
            --hurtResistantTime;
        }

        if (getHealth() <= 0.0F)
        {
            onDeathUpdate();
        }

        if (recentlyHit > 0)
        {
            --recentlyHit;
        }
        else
        {
            attackingPlayer = null;
        }

        if (lastAttacker != null && !lastAttacker.isEntityAlive())
        {
            lastAttacker = null;
        }

        if (entityLivingToAttack != null)
        {
            if (!entityLivingToAttack.isEntityAlive())
            {
                setRevengeTarget((EntityLivingBase)null);
            }
            else if (ticksExisted - revengeTimer > 100)
            {
                setRevengeTarget((EntityLivingBase)null);
            }
        }

        updatePotionEffects();
        field_70763_ax = field_70764_aw;
        prevRenderYawOffset = renderYawOffset;
        prevRotationYawHead = rotationYawHead;
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
        worldObj.theProfiler.endSection();
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return false;
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate()
    {
        ++deathTime;

        if (deathTime == 20)
        {
            int var1;

            if (!worldObj.isClient && (recentlyHit > 0 || isPlayer()) && func_146066_aG() && worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
            {
                var1 = getExperiencePoints(attackingPlayer);

                while (var1 > 0)
                {
                    int var2 = EntityXPOrb.getXPSplit(var1);
                    var1 -= var2;
                    worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj, posX, posY, posZ, var2));
                }
            }

            setDead();

            for (var1 = 0; var1 < 20; ++var1)
            {
                double var8 = rand.nextGaussian() * 0.02D;
                double var4 = rand.nextGaussian() * 0.02D;
                double var6 = rand.nextGaussian() * 0.02D;
                worldObj.spawnParticle("explode", posX + rand.nextFloat() * width * 2.0F - width, posY + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, var8, var4, var6);
            }
        }
    }

    protected boolean func_146066_aG()
    {
        return !isChild();
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int par1)
    {
        int var2 = EnchantmentHelper.getRespiration(this);
        return var2 > 0 && rand.nextInt(var2 + 1) > 0 ? par1 : par1 - 1;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        return 0;
    }

    /**
     * Only use is to identify if class is an instance of player for experience
     * dropping
     */
    protected boolean isPlayer()
    {
        return false;
    }

    public Random getRNG()
    {
        return rand;
    }

    public EntityLivingBase getAITarget()
    {
        return entityLivingToAttack;
    }

    public int func_142015_aE()
    {
        return revengeTimer;
    }

    public void setRevengeTarget(EntityLivingBase par1EntityLivingBase)
    {
        entityLivingToAttack = par1EntityLivingBase;
        revengeTimer = ticksExisted;
    }

    public EntityLivingBase getLastAttacker()
    {
        return lastAttacker;
    }

    public int getLastAttackerTime()
    {
        return lastAttackerTime;
    }

    public void setLastAttacker(Entity par1Entity)
    {
        if (par1Entity instanceof EntityLivingBase)
        {
            lastAttacker = (EntityLivingBase)par1Entity;
        }
        else
        {
            lastAttacker = null;
        }

        lastAttackerTime = ticksExisted;
    }

    public int getAge()
    {
        return entityAge;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setFloat("HealF", getHealth());
        par1NBTTagCompound.setShort("Health", (short)((int)Math.ceil(getHealth())));
        par1NBTTagCompound.setShort("HurtTime", (short)hurtTime);
        par1NBTTagCompound.setShort("DeathTime", (short)deathTime);
        par1NBTTagCompound.setShort("AttackTime", (short)attackTime);
        par1NBTTagCompound.setFloat("AbsorptionAmount", getAbsorptionAmount());
        ItemStack[] var2 = getInventory();
        int var3 = var2.length;
        int var4;
        ItemStack var5;

        for (var4 = 0; var4 < var3; ++var4)
        {
            var5 = var2[var4];

            if (var5 != null)
            {
                attributeMap.removeAttributeModifiers(var5.getAttributeModifiers());
            }
        }

        par1NBTTagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(getAttributeMap()));
        var2 = getInventory();
        var3 = var2.length;

        for (var4 = 0; var4 < var3; ++var4)
        {
            var5 = var2[var4];

            if (var5 != null)
            {
                attributeMap.applyAttributeModifiers(var5.getAttributeModifiers());
            }
        }

        if (!activePotionsMap.isEmpty())
        {
            NBTTagList var6 = new NBTTagList();
            Iterator var7 = activePotionsMap.values().iterator();

            while (var7.hasNext())
            {
                PotionEffect var8 = (PotionEffect)var7.next();
                var6.appendTag(var8.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            par1NBTTagCompound.setTag("ActiveEffects", var6);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        setAbsorptionAmount(par1NBTTagCompound.getFloat("AbsorptionAmount"));

        if (par1NBTTagCompound.func_150297_b("Attributes", 9) && worldObj != null && !worldObj.isClient)
        {
            SharedMonsterAttributes.func_151475_a(getAttributeMap(), par1NBTTagCompound.getTagList("Attributes", 10));
        }

        if (par1NBTTagCompound.func_150297_b("ActiveEffects", 9))
        {
            NBTTagList var2 = par1NBTTagCompound.getTagList("ActiveEffects", 10);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                PotionEffect var5 = PotionEffect.readCustomPotionEffectFromNBT(var4);

                if (var5 != null)
                {
                    activePotionsMap.put(Integer.valueOf(var5.getPotionID()), var5);
                }
            }
        }

        if (par1NBTTagCompound.func_150297_b("HealF", 99))
        {
            setHealth(par1NBTTagCompound.getFloat("HealF"));
        }
        else
        {
            NBTBase var6 = par1NBTTagCompound.getTag("Health");

            if (var6 == null)
            {
                setHealth(getMaxHealth());
            }
            else if (var6.getId() == 5)
            {
                setHealth(((NBTTagFloat)var6).func_150288_h());
            }
            else if (var6.getId() == 2)
            {
                setHealth(((NBTTagShort)var6).func_150289_e());
            }
        }

        hurtTime = par1NBTTagCompound.getShort("HurtTime");
        deathTime = par1NBTTagCompound.getShort("DeathTime");
        attackTime = par1NBTTagCompound.getShort("AttackTime");
    }

    protected void updatePotionEffects()
    {
        Iterator var1 = activePotionsMap.keySet().iterator();

        while (var1.hasNext())
        {
            Integer var2 = (Integer)var1.next();
            PotionEffect var3 = (PotionEffect)activePotionsMap.get(var2);

            if (!var3.onUpdate(this))
            {
                if (!worldObj.isClient)
                {
                    var1.remove();
                    onFinishedPotionEffect(var3);
                }
            }
            else if (var3.getDuration() % 600 == 0)
            {
                onChangedPotionEffect(var3, false);
            }
        }

        int var11;

        if (potionsNeedUpdate)
        {
            if (!worldObj.isClient)
            {
                if (activePotionsMap.isEmpty())
                {
                    dataWatcher.updateObject(8, Byte.valueOf((byte)0));
                    dataWatcher.updateObject(7, Integer.valueOf(0));
                    setInvisible(false);
                }
                else
                {
                    var11 = PotionHelper.calcPotionLiquidColor(activePotionsMap.values());
                    dataWatcher.updateObject(8, Byte.valueOf((byte)(PotionHelper.func_82817_b(activePotionsMap.values()) ? 1 : 0)));
                    dataWatcher.updateObject(7, Integer.valueOf(var11));
                    setInvisible(this.isPotionActive(Potion.invisibility.id));
                }
            }

            potionsNeedUpdate = false;
        }

        var11 = dataWatcher.getWatchableObjectInt(7);
        boolean var12 = dataWatcher.getWatchableObjectByte(8) > 0;

        if (var11 > 0)
        {
            boolean var4 = false;

            if (!isInvisible())
            {
                var4 = rand.nextBoolean();
            }
            else
            {
                var4 = rand.nextInt(15) == 0;
            }

            if (var12)
            {
                var4 &= rand.nextInt(5) == 0;
            }

            if (var4 && var11 > 0)
            {
                double var5 = (var11 >> 16 & 255) / 255.0D;
                double var7 = (var11 >> 8 & 255) / 255.0D;
                double var9 = (var11 >> 0 & 255) / 255.0D;
                worldObj.spawnParticle(var12 ? "mobSpellAmbient" : "mobSpell", posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height - yOffset, posZ + (rand.nextDouble() - 0.5D) * width, var5, var7, var9);
            }
        }
    }

    public void clearActivePotions()
    {
        Iterator var1 = activePotionsMap.keySet().iterator();

        while (var1.hasNext())
        {
            Integer var2 = (Integer)var1.next();
            PotionEffect var3 = (PotionEffect)activePotionsMap.get(var2);

            if (!worldObj.isClient)
            {
                var1.remove();
                onFinishedPotionEffect(var3);
            }
        }
    }

    public Collection getActivePotionEffects()
    {
        return activePotionsMap.values();
    }

    public boolean isPotionActive(int par1)
    {
        return activePotionsMap.containsKey(Integer.valueOf(par1));
    }

    public boolean isPotionActive(Potion par1Potion)
    {
        return activePotionsMap.containsKey(Integer.valueOf(par1Potion.id));
    }

    /**
     * returns the PotionEffect for the supplied Potion if it is active, null
     * otherwise.
     */
    public PotionEffect getActivePotionEffect(Potion par1Potion)
    {
        return (PotionEffect)activePotionsMap.get(Integer.valueOf(par1Potion.id));
    }

    /**
     * adds a PotionEffect to the entity
     */
    public void addPotionEffect(PotionEffect par1PotionEffect)
    {
        if (isPotionApplicable(par1PotionEffect))
        {
            if (activePotionsMap.containsKey(Integer.valueOf(par1PotionEffect.getPotionID())))
            {
                ((PotionEffect)activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID()))).combine(par1PotionEffect);
                onChangedPotionEffect((PotionEffect)activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID())), true);
            }
            else
            {
                activePotionsMap.put(Integer.valueOf(par1PotionEffect.getPotionID()), par1PotionEffect);
                onNewPotionEffect(par1PotionEffect);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect par1PotionEffect)
    {
        if (getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
        {
            int var2 = par1PotionEffect.getPotionID();

            if (var2 == Potion.regeneration.id || var2 == Potion.poison.id) { return false; }
        }

        return true;
    }

    /**
     * Returns true if this entity is undead.
     */
    public boolean isEntityUndead()
    {
        return getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Remove the specified potion effect from this entity.
     */
    public void removePotionEffect(int par1)
    {
        PotionEffect var2 = (PotionEffect)activePotionsMap.remove(Integer.valueOf(par1));

        if (var2 != null)
        {
            onFinishedPotionEffect(var2);
        }
    }

    protected void onNewPotionEffect(PotionEffect par1PotionEffect)
    {
        potionsNeedUpdate = true;

        if (!worldObj.isClient)
        {
            Potion.potionTypes[par1PotionEffect.getPotionID()].applyAttributesModifiersToEntity(this, getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    protected void onChangedPotionEffect(PotionEffect par1PotionEffect, boolean par2)
    {
        potionsNeedUpdate = true;

        if (par2 && !worldObj.isClient)
        {
            Potion.potionTypes[par1PotionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, getAttributeMap(), par1PotionEffect.getAmplifier());
            Potion.potionTypes[par1PotionEffect.getPotionID()].applyAttributesModifiersToEntity(this, getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    protected void onFinishedPotionEffect(PotionEffect par1PotionEffect)
    {
        potionsNeedUpdate = true;

        if (!worldObj.isClient)
        {
            Potion.potionTypes[par1PotionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(float par1)
    {
        float var2 = getHealth();

        if (var2 > 0.0F)
        {
            setHealth(var2 + par1);
        }
    }

    public final float getHealth()
    {
        return dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float par1)
    {
        dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(par1, 0.0F, getMaxHealth())));
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
        else if (worldObj.isClient)
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
            else if (par1DamageSource.isFireDamage() && this.isPotionActive(Potion.fireResistance))
            {
                return false;
            }
            else
            {
                if ((par1DamageSource == DamageSource.anvil || par1DamageSource == DamageSource.fallingBlock) && getEquipmentInSlot(4) != null)
                {
                    getEquipmentInSlot(4).damageItem((int)(par2 * 4.0F + rand.nextFloat() * par2 * 2.0F), this);
                    par2 *= 0.75F;
                }

                limbSwingAmount = 1.5F;
                boolean var3 = true;

                if (hurtResistantTime > maxHurtResistantTime / 2.0F)
                {
                    if (par2 <= lastDamage) { return false; }

                    damageEntity(par1DamageSource, par2 - lastDamage);
                    lastDamage = par2;
                    var3 = false;
                }
                else
                {
                    lastDamage = par2;
                    prevHealth = getHealth();
                    hurtResistantTime = maxHurtResistantTime;
                    damageEntity(par1DamageSource, par2);
                    hurtTime = maxHurtTime = 10;
                }

                attackedAtYaw = 0.0F;
                Entity var4 = par1DamageSource.getEntity();

                if (var4 != null)
                {
                    if (var4 instanceof EntityLivingBase)
                    {
                        setRevengeTarget((EntityLivingBase)var4);
                    }

                    if (var4 instanceof EntityPlayer)
                    {
                        recentlyHit = 100;
                        attackingPlayer = (EntityPlayer)var4;
                    }
                    else if (var4 instanceof EntityWolf)
                    {
                        EntityWolf var5 = (EntityWolf)var4;

                        if (var5.isTamed())
                        {
                            recentlyHit = 100;
                            attackingPlayer = null;
                        }
                    }
                }

                if (var3)
                {
                    worldObj.setEntityState(this, (byte)2);

                    if (par1DamageSource != DamageSource.drown)
                    {
                        setBeenAttacked();
                    }

                    if (var4 != null)
                    {
                        double var9 = var4.posX - posX;
                        double var7;

                        for (var7 = var4.posZ - posZ; var9 * var9 + var7 * var7 < 1.0E-4D; var7 = (Math.random() - Math.random()) * 0.01D)
                        {
                            var9 = (Math.random() - Math.random()) * 0.01D;
                        }

                        attackedAtYaw = (float)(Math.atan2(var7, var9) * 180.0D / Math.PI) - rotationYaw;
                        knockBack(var4, par2, var9, var7);
                    }
                    else
                    {
                        attackedAtYaw = (int)(Math.random() * 2.0D) * 180;
                    }
                }

                String var10;

                if (getHealth() <= 0.0F)
                {
                    var10 = getDeathSound();

                    if (var3 && var10 != null)
                    {
                        playSound(var10, getSoundVolume(), getSoundPitch());
                    }

                    onDeath(par1DamageSource);
                }
                else
                {
                    var10 = getHurtSound();

                    if (var3 && var10 != null)
                    {
                        playSound(var10, getSoundVolume(), getSoundPitch());
                    }
                }

                return true;
            }
        }
    }

    /**
     * Renders broken item particles using the given ItemStack
     */
    public void renderBrokenItemStack(ItemStack par1ItemStack)
    {
        playSound("random.break", 0.8F, 0.8F + worldObj.rand.nextFloat() * 0.4F);

        for (int var2 = 0; var2 < 5; ++var2)
        {
            Vec3 var3 = worldObj.getWorldVec3Pool().getVecFromPool((rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            var3.rotateAroundX(-rotationPitch * (float)Math.PI / 180.0F);
            var3.rotateAroundY(-rotationYaw * (float)Math.PI / 180.0F);
            Vec3 var4 = worldObj.getWorldVec3Pool().getVecFromPool((rand.nextFloat() - 0.5D) * 0.3D, (-rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
            var4.rotateAroundX(-rotationPitch * (float)Math.PI / 180.0F);
            var4.rotateAroundY(-rotationYaw * (float)Math.PI / 180.0F);
            var4 = var4.addVector(posX, posY + getEyeHeight(), posZ);
            worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(par1ItemStack.getItem()), var4.xCoord, var4.yCoord, var4.zCoord, var3.xCoord, var3.yCoord + 0.05D, var3.zCoord);
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        Entity var2 = par1DamageSource.getEntity();
        EntityLivingBase var3 = func_94060_bK();

        if (scoreValue >= 0 && var3 != null)
        {
            var3.addToPlayerScore(this, scoreValue);
        }

        if (var2 != null)
        {
            var2.onKillEntity(this);
        }

        dead = true;

        if (!worldObj.isClient)
        {
            int var4 = 0;

            if (var2 instanceof EntityPlayer)
            {
                var4 = EnchantmentHelper.getLootingModifier((EntityLivingBase)var2);
            }

            if (func_146066_aG() && worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
            {
                dropFewItems(recentlyHit > 0, var4);
                dropEquipment(recentlyHit > 0, var4);

                if (recentlyHit > 0)
                {
                    int var5 = rand.nextInt(200) - var4;

                    if (var5 < 5)
                    {
                        dropRareDrop(var5 <= 0 ? 1 : 0);
                    }
                }
            }
        }

        worldObj.setEntityState(this, (byte)3);
    }

    /**
     * Drop the equipment for this entity.
     */
    protected void dropEquipment(boolean par1, int par2)
    {
    }

    /**
     * knocks back this entity
     */
    public void knockBack(Entity par1Entity, float par2, double par3, double par5)
    {
        if (rand.nextDouble() >= getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue())
        {
            isAirBorne = true;
            float var7 = MathHelper.sqrt_double(par3 * par3 + par5 * par5);
            float var8 = 0.4F;
            motionX /= 2.0D;
            motionY /= 2.0D;
            motionZ /= 2.0D;
            motionX -= par3 / var7 * var8;
            motionY += var8;
            motionZ -= par5 / var7 * var8;

            if (motionY > 0.4000000059604645D)
            {
                motionY = 0.4000000059604645D;
            }
        }
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "game.neutral.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "game.neutral.die";
    }

    protected void dropRareDrop(int par1)
    {
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        int var1 = MathHelper.floor_double(posX);
        int var2 = MathHelper.floor_double(boundingBox.minY);
        int var3 = MathHelper.floor_double(posZ);
        Block var4 = worldObj.getBlock(var1, var2, var3);
        return var4 == Blocks.ladder || var4 == Blocks.vine;
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive()
    {
        return !isDead && getHealth() > 0.0F;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        super.fall(par1);
        PotionEffect var2 = getActivePotionEffect(Potion.jump);
        float var3 = var2 != null ? (float)(var2.getAmplifier() + 1) : 0.0F;
        int var4 = MathHelper.ceiling_float_int(par1 - 3.0F - var3);

        if (var4 > 0)
        {
            playSound(func_146067_o(var4), 1.0F, 1.0F);
            attackEntityFrom(DamageSource.fall, var4);
            int var5 = MathHelper.floor_double(posX);
            int var6 = MathHelper.floor_double(posY - 0.20000000298023224D - yOffset);
            int var7 = MathHelper.floor_double(posZ);
            Block var8 = worldObj.getBlock(var5, var6, var7);

            if (var8.getMaterial() != Material.air)
            {
                Block.SoundType var9 = var8.stepSound;
                playSound(var9.getStepResourcePath(), var9.getVolume() * 0.5F, var9.getFrequency() * 0.75F);
            }
        }
    }

    protected String func_146067_o(int p_146067_1_)
    {
        return p_146067_1_ > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    /**
     * Returns the current armor value as determined by a call to
     * InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        int var1 = 0;
        ItemStack[] var2 = getInventory();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            ItemStack var5 = var2[var4];

            if (var5 != null && var5.getItem() instanceof ItemArmor)
            {
                int var6 = ((ItemArmor)var5.getItem()).damageReduceAmount;
                var1 += var6;
            }
        }

        return var1;
    }

    protected void damageArmor(float par1)
    {
    }

    /**
     * Reduces damage, depending on armor
     */
    protected float applyArmorCalculations(DamageSource par1DamageSource, float par2)
    {
        if (!par1DamageSource.isUnblockable())
        {
            int var3 = 25 - getTotalArmorValue();
            float var4 = par2 * var3;
            damageArmor(par2);
            par2 = var4 / 25.0F;
        }

        return par2;
    }

    /**
     * Reduces damage, depending on potions
     */
    protected float applyPotionDamageCalculations(DamageSource par1DamageSource, float par2)
    {
        if (par1DamageSource.isDamageAbsolute())
        {
            return par2;
        }
        else
        {
            if (this instanceof EntityZombie)
            {
                par2 = par2;
            }

            int var3;
            int var4;
            float var5;

            if (this.isPotionActive(Potion.resistance) && par1DamageSource != DamageSource.outOfWorld)
            {
                var3 = (getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
                var4 = 25 - var3;
                var5 = par2 * var4;
                par2 = var5 / 25.0F;
            }

            if (par2 <= 0.0F)
            {
                return 0.0F;
            }
            else
            {
                var3 = EnchantmentHelper.getEnchantmentModifierDamage(getInventory(), par1DamageSource);

                if (var3 > 20)
                {
                    var3 = 20;
                }

                if (var3 > 0 && var3 <= 20)
                {
                    var4 = 25 - var3;
                    var5 = par2 * var4;
                    par2 = var5 / 25.0F;
                }

                return par2;
            }
        }
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
            par2 = applyArmorCalculations(par1DamageSource, par2);
            par2 = applyPotionDamageCalculations(par1DamageSource, par2);
            float var3 = par2;
            par2 = Math.max(par2 - getAbsorptionAmount(), 0.0F);
            setAbsorptionAmount(getAbsorptionAmount() - (var3 - par2));

            if (par2 != 0.0F)
            {
                float var4 = getHealth();
                setHealth(var4 - par2);
                func_110142_aN().func_94547_a(par1DamageSource, var4, par2);
                setAbsorptionAmount(getAbsorptionAmount() - par2);
            }
        }
    }

    public CombatTracker func_110142_aN()
    {
        return _combatTracker;
    }

    public EntityLivingBase func_94060_bK()
    {
        return _combatTracker.func_94550_c() != null ? _combatTracker.func_94550_c() : (attackingPlayer != null ? attackingPlayer : (entityLivingToAttack != null ? entityLivingToAttack : null));
    }

    public final float getMaxHealth()
    {
        return (float)getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
    }

    /**
     * counts the amount of arrows stuck in the entity. getting hit by arrows
     * increases this, used in rendering
     */
    public final int getArrowCountInEntity()
    {
        return dataWatcher.getWatchableObjectByte(9);
    }

    /**
     * sets the amount of arrows stuck in the entity. used for rendering those
     */
    public final void setArrowCountInEntity(int par1)
    {
        dataWatcher.updateObject(9, Byte.valueOf((byte)par1));
    }

    /**
     * Returns an integer indicating the end point of the swing animation, used
     * by {@link #swingProgress} to provide a progress indicator. Takes dig
     * speed enchantments into account.
     */
    private int getArmSwingAnimationEnd()
    {
        return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem()
    {
        if (!isSwingInProgress || swingProgressInt >= getArmSwingAnimationEnd() / 2 || swingProgressInt < 0)
        {
            swingProgressInt = -1;
            isSwingInProgress = true;

            if (worldObj instanceof WorldServer)
            {
                ((WorldServer)worldObj).getEntityTracker().func_151247_a(this, new S0BPacketAnimation(this, 0));
            }
        }
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill()
    {
        attackEntityFrom(DamageSource.outOfWorld, 4.0F);
    }

    /**
     * Updates the arm swing progress counters and animation progress
     */
    protected void updateArmSwingProgress()
    {
        int var1 = getArmSwingAnimationEnd();

        if (isSwingInProgress)
        {
            ++swingProgressInt;

            if (swingProgressInt >= var1)
            {
                swingProgressInt = 0;
                isSwingInProgress = false;
            }
        }
        else
        {
            swingProgressInt = 0;
        }

        swingProgress = (float)swingProgressInt / (float)var1;
    }

    public IAttributeInstance getEntityAttribute(IAttribute par1Attribute)
    {
        return getAttributeMap().getAttributeInstance(par1Attribute);
    }

    public BaseAttributeMap getAttributeMap()
    {
        if (attributeMap == null)
        {
            attributeMap = new ServersideAttributeMap();
        }

        return attributeMap;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEFINED;
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public abstract ItemStack getHeldItem();

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public abstract ItemStack getEquipmentInSlot(int var1);

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is
     * armor. Params: Item, slot
     */
    public abstract void setCurrentItemOrArmor(int var1, ItemStack var2);

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean par1)
    {
        super.setSprinting(par1);
        IAttributeInstance var2 = getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (var2.getModifier(sprintingSpeedBoostModifierUUID) != null)
        {
            var2.removeModifier(sprintingSpeedBoostModifier);
        }

        if (par1)
        {
            var2.applyModifier(sprintingSpeedBoostModifier);
        }
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it
     * seems)
     */
    public abstract ItemStack[] getInventory();

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 1.0F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch()
    {
        return isChild() ? (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.5F : (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F;
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked()
    {
        return getHealth() <= 0.0F;
    }

    /**
     * Sets the position of the entity and updates the 'last' variables
     */
    public void setPositionAndUpdate(double par1, double par3, double par5)
    {
        setLocationAndAngles(par1, par3, par5, rotationYaw, rotationPitch);
    }

    /**
     * Moves the entity to a position out of the way of its mount.
     */
    public void dismountEntity(Entity par1Entity)
    {
        double var3 = par1Entity.posX;
        double var5 = par1Entity.boundingBox.minY + par1Entity.height;
        double var7 = par1Entity.posZ;
        byte var9 = 3;

        for (int var10 = -var9; var10 <= var9; ++var10)
        {
            for (int var11 = -var9; var11 < var9; ++var11)
            {
                if (var10 != 0 || var11 != 0)
                {
                    int var12 = (int)(posX + var10);
                    int var13 = (int)(posZ + var11);
                    AxisAlignedBB var2 = boundingBox.getOffsetBoundingBox(var10, 1.0D, var11);

                    if (worldObj.func_147461_a(var2).isEmpty())
                    {
                        if (World.doesBlockHaveSolidTopSurface(worldObj, var12, (int)posY, var13))
                        {
                            setPositionAndUpdate(posX + var10, posY + 1.0D, posZ + var11);
                            return;
                        }

                        if (World.doesBlockHaveSolidTopSurface(worldObj, var12, (int)posY - 1, var13) || worldObj.getBlock(var12, (int)posY - 1, var13).getMaterial() == Material.field_151586_h)
                        {
                            var3 = posX + var10;
                            var5 = posY + 1.0D;
                            var7 = posZ + var11;
                        }
                    }
                }
            }
        }

        setPositionAndUpdate(var3, var5, var7);
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        motionY = 0.41999998688697815D;

        if (this.isPotionActive(Potion.jump))
        {
            motionY += (getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }

        if (isSprinting())
        {
            float var1 = rotationYaw * 0.017453292F;
            motionX -= MathHelper.sin(var1) * 0.2F;
            motionZ += MathHelper.cos(var1) * 0.2F;
        }

        isAirBorne = true;
    }

    /**
     * Moves the entity based on the specified heading. Args: strafe, forward
     */
    public void moveEntityWithHeading(float par1, float par2)
    {
        double var8;

        if (isInWater() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying))
        {
            var8 = posY;
            moveFlying(par1, par2, isAIEnabled() ? 0.04F : 0.02F);
            moveEntity(motionX, motionY, motionZ);
            motionX *= 0.800000011920929D;
            motionY *= 0.800000011920929D;
            motionZ *= 0.800000011920929D;
            motionY -= 0.02D;

            if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, motionY + 0.6000000238418579D - posY + var8, motionZ))
            {
                motionY = 0.30000001192092896D;
            }
        }
        else if (handleLavaMovement() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying))
        {
            var8 = posY;
            moveFlying(par1, par2, 0.02F);
            moveEntity(motionX, motionY, motionZ);
            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
            motionY -= 0.02D;

            if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, motionY + 0.6000000238418579D - posY + var8, motionZ))
            {
                motionY = 0.30000001192092896D;
            }
        }
        else
        {
            float var3 = 0.91F;

            if (onGround)
            {
                var3 = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)).slipperiness * 0.91F;
            }

            float var4 = 0.16277136F / (var3 * var3 * var3);
            float var5;

            if (onGround)
            {
                var5 = getAIMoveSpeed() * var4;
            }
            else
            {
                var5 = jumpMovementFactor;
            }

            moveFlying(par1, par2, var5);
            var3 = 0.91F;

            if (onGround)
            {
                var3 = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)).slipperiness * 0.91F;
            }

            if (isOnLadder())
            {
                float var6 = 0.15F;

                if (motionX < (-var6))
                {
                    motionX = (-var6);
                }

                if (motionX > var6)
                {
                    motionX = var6;
                }

                if (motionZ < (-var6))
                {
                    motionZ = (-var6);
                }

                if (motionZ > var6)
                {
                    motionZ = var6;
                }

                fallDistance = 0.0F;

                if (motionY < -0.15D)
                {
                    motionY = -0.15D;
                }

                boolean var7 = isSneaking() && this instanceof EntityPlayer;

                if (var7 && motionY < 0.0D)
                {
                    motionY = 0.0D;
                }
            }

            moveEntity(motionX, motionY, motionZ);

            if (isCollidedHorizontally && isOnLadder())
            {
                motionY = 0.2D;
            }

            if (worldObj.isClient && (!worldObj.blockExists((int)posX, 0, (int)posZ) || !worldObj.getChunkFromBlockCoords((int)posX, (int)posZ).isChunkLoaded))
            {
                if (posY > 0.0D)
                {
                    motionY = -0.1D;
                }
                else
                {
                    motionY = 0.0D;
                }
            }
            else
            {
                motionY -= 0.08D;
            }

            motionY *= 0.9800000190734863D;
            motionX *= var3;
            motionZ *= var3;
        }

        prevLimbSwingAmount = limbSwingAmount;
        var8 = posX - prevPosX;
        double var9 = posZ - prevPosZ;
        float var10 = MathHelper.sqrt_double(var8 * var8 + var9 * var9) * 4.0F;

        if (var10 > 1.0F)
        {
            var10 = 1.0F;
        }

        limbSwingAmount += (var10 - limbSwingAmount) * 0.4F;
        limbSwing += limbSwingAmount;
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return false;
    }

    /**
     * the movespeed used for the new AI system
     */
    public float getAIMoveSpeed()
    {
        return isAIEnabled() ? landMovementFactor : 0.1F;
    }

    /**
     * set the movespeed used for the new AI system
     */
    public void setAIMoveSpeed(float par1)
    {
        landMovementFactor = par1;
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        setLastAttacker(par1Entity);
        return false;
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping()
    {
        return false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!worldObj.isClient)
        {
            int var1 = getArrowCountInEntity();

            if (var1 > 0)
            {
                if (arrowHitTimer <= 0)
                {
                    arrowHitTimer = 20 * (30 - var1);
                }

                --arrowHitTimer;

                if (arrowHitTimer <= 0)
                {
                    setArrowCountInEntity(var1 - 1);
                }
            }

            for (int var2 = 0; var2 < 5; ++var2)
            {
                ItemStack var3 = previousEquipment[var2];
                ItemStack var4 = getEquipmentInSlot(var2);

                if (!ItemStack.areItemStacksEqual(var4, var3))
                {
                    ((WorldServer)worldObj).getEntityTracker().func_151247_a(this, new S04PacketEntityEquipment(getEntityId(), var2, var4));

                    if (var3 != null)
                    {
                        attributeMap.removeAttributeModifiers(var3.getAttributeModifiers());
                    }

                    if (var4 != null)
                    {
                        attributeMap.applyAttributeModifiers(var4.getAttributeModifiers());
                    }

                    previousEquipment[var2] = var4 == null ? null : var4.copy();
                }
            }
        }

        onLivingUpdate();
        double var9 = posX - prevPosX;
        double var10 = posZ - prevPosZ;
        float var5 = (float)(var9 * var9 + var10 * var10);
        float var6 = renderYawOffset;
        float var7 = 0.0F;
        field_70768_au = field_110154_aX;
        float var8 = 0.0F;

        if (var5 > 0.0025000002F)
        {
            var8 = 1.0F;
            var7 = (float)Math.sqrt(var5) * 3.0F;
            var6 = (float)Math.atan2(var10, var9) * 180.0F / (float)Math.PI - 90.0F;
        }

        if (swingProgress > 0.0F)
        {
            var6 = rotationYaw;
        }

        if (!onGround)
        {
            var8 = 0.0F;
        }

        field_110154_aX += (var8 - field_110154_aX) * 0.3F;
        worldObj.theProfiler.startSection("headTurn");
        var7 = func_110146_f(var6, var7);
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("rangeChecks");

        while (rotationYaw - prevRotationYaw < -180.0F)
        {
            prevRotationYaw -= 360.0F;
        }

        while (rotationYaw - prevRotationYaw >= 180.0F)
        {
            prevRotationYaw += 360.0F;
        }

        while (renderYawOffset - prevRenderYawOffset < -180.0F)
        {
            prevRenderYawOffset -= 360.0F;
        }

        while (renderYawOffset - prevRenderYawOffset >= 180.0F)
        {
            prevRenderYawOffset += 360.0F;
        }

        while (rotationPitch - prevRotationPitch < -180.0F)
        {
            prevRotationPitch -= 360.0F;
        }

        while (rotationPitch - prevRotationPitch >= 180.0F)
        {
            prevRotationPitch += 360.0F;
        }

        while (rotationYawHead - prevRotationYawHead < -180.0F)
        {
            prevRotationYawHead -= 360.0F;
        }

        while (rotationYawHead - prevRotationYawHead >= 180.0F)
        {
            prevRotationYawHead += 360.0F;
        }

        worldObj.theProfiler.endSection();
        field_70764_aw += var7;
    }

    protected float func_110146_f(float par1, float par2)
    {
        float var3 = MathHelper.wrapAngleTo180_float(par1 - renderYawOffset);
        renderYawOffset += var3 * 0.3F;
        float var4 = MathHelper.wrapAngleTo180_float(rotationYaw - renderYawOffset);
        boolean var5 = var4 < -90.0F || var4 >= 90.0F;

        if (var4 < -75.0F)
        {
            var4 = -75.0F;
        }

        if (var4 >= 75.0F)
        {
            var4 = 75.0F;
        }

        renderYawOffset = rotationYaw - var4;

        if (var4 * var4 > 2500.0F)
        {
            renderYawOffset += var4 * 0.2F;
        }

        if (var5)
        {
            par2 *= -1.0F;
        }

        return par2;
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (jumpTicks > 0)
        {
            --jumpTicks;
        }

        if (newPosRotationIncrements > 0)
        {
            double var1 = posX + (newPosX - posX) / newPosRotationIncrements;
            double var3 = posY + (newPosY - posY) / newPosRotationIncrements;
            double var5 = posZ + (newPosZ - posZ) / newPosRotationIncrements;
            double var7 = MathHelper.wrapAngleTo180_double(newRotationYaw - rotationYaw);
            rotationYaw = (float)(rotationYaw + var7 / newPosRotationIncrements);
            rotationPitch = (float)(rotationPitch + (newRotationPitch - rotationPitch) / newPosRotationIncrements);
            --newPosRotationIncrements;
            setPosition(var1, var3, var5);
            setRotation(rotationYaw, rotationPitch);
        }
        else if (!isClientWorld())
        {
            motionX *= 0.98D;
            motionY *= 0.98D;
            motionZ *= 0.98D;
        }

        if (Math.abs(motionX) < 0.005D)
        {
            motionX = 0.0D;
        }

        if (Math.abs(motionY) < 0.005D)
        {
            motionY = 0.0D;
        }

        if (Math.abs(motionZ) < 0.005D)
        {
            motionZ = 0.0D;
        }

        worldObj.theProfiler.startSection("ai");

        if (isMovementBlocked())
        {
            isJumping = false;
            moveStrafing = 0.0F;
            moveForward = 0.0F;
            randomYawVelocity = 0.0F;
        }
        else if (isClientWorld())
        {
            if (isAIEnabled())
            {
                worldObj.theProfiler.startSection("newAi");
                updateAITasks();
                worldObj.theProfiler.endSection();
            }
            else
            {
                worldObj.theProfiler.startSection("oldAi");
                updateEntityActionState();
                worldObj.theProfiler.endSection();
                rotationYawHead = rotationYaw;
            }
        }

        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("jump");

        if (isJumping)
        {
            if (!isInWater() && !handleLavaMovement())
            {
                if (onGround && jumpTicks == 0)
                {
                    jump();
                    jumpTicks = 10;
                }
            }
            else
            {
                motionY += 0.03999999910593033D;
            }
        }
        else
        {
            jumpTicks = 0;
        }

        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("travel");
        moveStrafing *= 0.98F;
        moveForward *= 0.98F;
        randomYawVelocity *= 0.9F;
        moveEntityWithHeading(moveStrafing, moveForward);
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.startSection("push");

        if (!worldObj.isClient)
        {
            collideWithNearbyEntities();
        }

        worldObj.theProfiler.endSection();
    }

    protected void updateAITasks()
    {
    }

    protected void collideWithNearbyEntities()
    {
        List var1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        if (var1 != null && !var1.isEmpty())
        {
            for (int var2 = 0; var2 < var1.size(); ++var2)
            {
                Entity var3 = (Entity)var1.get(var2);

                if (var3.canBePushed())
                {
                    collideWithEntity(var3);
                }
            }
        }
    }

    protected void collideWithEntity(Entity par1Entity)
    {
        par1Entity.applyEntityCollision(this);
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        super.updateRidden();
        field_70768_au = field_110154_aX;
        field_110154_aX = 0.0F;
        fallDistance = 0.0F;
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick()
    {
    }

    protected void updateEntityActionState()
    {
        ++entityAge;
    }

    public void setJumping(boolean par1)
    {
        isJumping = par1;
    }

    /**
     * Called whenever an item is picked up from walking over it. Args:
     * pickedUpEntity, stackSize
     */
    public void onItemPickup(Entity par1Entity, int par2)
    {
        if (!par1Entity.isDead && !worldObj.isClient)
        {
            EntityTracker var3 = ((WorldServer)worldObj).getEntityTracker();

            if (par1Entity instanceof EntityItem)
            {
                var3.func_151247_a(par1Entity, new S0DPacketCollectItem(par1Entity.getEntityId(), getEntityId()));
            }

            if (par1Entity instanceof EntityArrow)
            {
                var3.func_151247_a(par1Entity, new S0DPacketCollectItem(par1Entity.getEntityId(), getEntityId()));
            }

            if (par1Entity instanceof EntityXPOrb)
            {
                var3.func_151247_a(par1Entity, new S0DPacketCollectItem(par1Entity.getEntityId(), getEntityId()));
            }
        }
    }

    /**
     * returns true if the entity provided in the argument can be seen.
     * (Raytrace)
     */
    public boolean canEntityBeSeen(Entity par1Entity)
    {
        return worldObj.rayTraceBlocks(worldObj.getWorldVec3Pool().getVecFromPool(posX, posY + getEyeHeight(), posZ), worldObj.getWorldVec3Pool().getVecFromPool(par1Entity.posX, par1Entity.posY + par1Entity.getEyeHeight(), par1Entity.posZ)) == null;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3 getLookVec()
    {
        return getLook(1.0F);
    }

    /**
     * interpolated look vector
     */
    public Vec3 getLook(float par1)
    {
        float var2;
        float var3;
        float var4;
        float var5;

        if (par1 == 1.0F)
        {
            var2 = MathHelper.cos(-rotationYaw * 0.017453292F - (float)Math.PI);
            var3 = MathHelper.sin(-rotationYaw * 0.017453292F - (float)Math.PI);
            var4 = -MathHelper.cos(-rotationPitch * 0.017453292F);
            var5 = MathHelper.sin(-rotationPitch * 0.017453292F);
            return worldObj.getWorldVec3Pool().getVecFromPool(var3 * var4, var5, var2 * var4);
        }
        else
        {
            var2 = prevRotationPitch + (rotationPitch - prevRotationPitch) * par1;
            var3 = prevRotationYaw + (rotationYaw - prevRotationYaw) * par1;
            var4 = MathHelper.cos(-var3 * 0.017453292F - (float)Math.PI);
            var5 = MathHelper.sin(-var3 * 0.017453292F - (float)Math.PI);
            float var6 = -MathHelper.cos(-var2 * 0.017453292F);
            float var7 = MathHelper.sin(-var2 * 0.017453292F);
            return worldObj.getWorldVec3Pool().getVecFromPool(var5 * var6, var7, var4 * var6);
        }
    }

    /**
     * Returns whether the entity is in a local (client) world
     */
    public boolean isClientWorld()
    {
        return !worldObj.isClient;
    }

    /**
     * Returns true if other Entities should be prevented from moving through
     * this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities
     * when colliding.
     */
    public boolean canBePushed()
    {
        return !isDead;
    }

    public float getEyeHeight()
    {
        return height * 0.85F;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked()
    {
        velocityChanged = rand.nextDouble() >= getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
    }

    public float getRotationYawHead()
    {
        return rotationYawHead;
    }

    public float getAbsorptionAmount()
    {
        return field_110151_bq;
    }

    public void setAbsorptionAmount(float par1)
    {
        if (par1 < 0.0F)
        {
            par1 = 0.0F;
        }

        field_110151_bq = par1;
    }

    public Team getTeam()
    {
        return null;
    }

    public boolean isOnSameTeam(EntityLivingBase par1EntityLivingBase)
    {
        return isOnTeam(par1EntityLivingBase.getTeam());
    }

    /**
     * Returns true if the entity is on a specific team.
     */
    public boolean isOnTeam(Team par1Team)
    {
        return getTeam() != null ? getTeam().isSameTeam(par1Team) : false;
    }
}
