package net.minecraft.entity.monster;

import java.util.Calendar;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;

public class EntitySkeleton extends EntityMob implements IRangedAttackMob
{
    private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F);
    private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2D, false);
    private static final String __OBFID = "CL_00001697";

    public EntitySkeleton(World par1World)
    {
        super(par1World);
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIRestrictSun(this));
        tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
        tasks.addTask(5, new EntityAIWander(this, 1.0D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(6, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

        if (par1World != null && !par1World.isClient)
        {
            setCombatTask();
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(13, new Byte((byte)0));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.skeleton.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.skeleton.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.skeleton.death";
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        playSound("mob.skeleton.step", 0.15F, 1.0F);
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        if (super.attackEntityAsMob(par1Entity))
        {
            if (getSkeletonType() == 1 && par1Entity instanceof EntityLivingBase)
            {
                ((EntityLivingBase)par1Entity).addPotionEffect(new PotionEffect(Potion.wither.id, 200));
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (worldObj.isDaytime() && !worldObj.isClient)
        {
            float var1 = getBrightness(1.0F);

            if (var1 > 0.5F && rand.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)))
            {
                boolean var2 = true;
                ItemStack var3 = getEquipmentInSlot(4);

                if (var3 != null)
                {
                    if (var3.isItemStackDamageable())
                    {
                        var3.setItemDamage(var3.getItemDamageForDisplay() + rand.nextInt(2));

                        if (var3.getItemDamageForDisplay() >= var3.getMaxDamage())
                        {
                            renderBrokenItemStack(var3);
                            setCurrentItemOrArmor(4, (ItemStack)null);
                        }
                    }

                    var2 = false;
                }

                if (var2)
                {
                    setFire(8);
                }
            }
        }

        if (worldObj.isClient && getSkeletonType() == 1)
        {
            setSize(0.72F, 2.34F);
        }

        super.onLivingUpdate();
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        super.updateRidden();

        if (ridingEntity instanceof EntityCreature)
        {
            EntityCreature var1 = (EntityCreature)ridingEntity;
            renderYawOffset = var1.renderYawOffset;
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);

        if (par1DamageSource.getSourceOfDamage() instanceof EntityArrow && par1DamageSource.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer var2 = (EntityPlayer)par1DamageSource.getEntity();
            double var3 = var2.posX - posX;
            double var5 = var2.posZ - posZ;

            if (var3 * var3 + var5 * var5 >= 2500.0D)
            {
                var2.triggerAchievement(AchievementList.snipeSkeleton);
            }
        }
    }

    protected Item func_146068_u()
    {
        return Items.arrow;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3;
        int var4;

        if (getSkeletonType() == 1)
        {
            var3 = rand.nextInt(3 + par2) - 1;

            for (var4 = 0; var4 < var3; ++var4)
            {
                func_145779_a(Items.coal, 1);
            }
        }
        else
        {
            var3 = rand.nextInt(3 + par2);

            for (var4 = 0; var4 < var3; ++var4)
            {
                func_145779_a(Items.arrow, 1);
            }
        }

        var3 = rand.nextInt(3 + par2);

        for (var4 = 0; var4 < var3; ++var4)
        {
            func_145779_a(Items.bone, 1);
        }
    }

    protected void dropRareDrop(int par1)
    {
        if (getSkeletonType() == 1)
        {
            entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0F);
        }
    }

    /**
     * Makes entity wear random armor based on difficulty
     */
    protected void addRandomArmor()
    {
        super.addRandomArmor();
        setCurrentItemOrArmor(0, new ItemStack(Items.bow));
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);

        if (worldObj.provider instanceof WorldProviderHell && getRNG().nextInt(5) > 0)
        {
            tasks.addTask(4, aiAttackOnCollide);
            setSkeletonType(1);
            setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
            getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
        }
        else
        {
            tasks.addTask(4, aiArrowAttack);
            addRandomArmor();
            enchantEquipment();
        }

        setCanPickUpLoot(rand.nextFloat() < 0.55F * worldObj.func_147462_b(posX, posY, posZ));

        if (getEquipmentInSlot(4) == null)
        {
            Calendar var2 = worldObj.getCurrentDate();

            if (var2.get(2) + 1 == 10 && var2.get(5) == 31 && rand.nextFloat() < 0.25F)
            {
                setCurrentItemOrArmor(4, new ItemStack(rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                equipmentDropChances[4] = 0.0F;
            }
        }

        return par1EntityLivingData;
    }

    /**
     * sets this entity's combat AI.
     */
    public void setCombatTask()
    {
        tasks.removeTask(aiAttackOnCollide);
        tasks.removeTask(aiArrowAttack);
        ItemStack var1 = getHeldItem();

        if (var1 != null && var1.getItem() == Items.bow)
        {
            tasks.addTask(4, aiArrowAttack);
        }
        else
        {
            tasks.addTask(4, aiAttackOnCollide);
        }
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2)
    {
        EntityArrow var3 = new EntityArrow(worldObj, this, par1EntityLivingBase, 1.6F, 14 - worldObj.difficultySetting.func_151525_a() * 4);
        int var4 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, getHeldItem());
        int var5 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, getHeldItem());
        var3.setDamage(par2 * 2.0F + rand.nextGaussian() * 0.25D + worldObj.difficultySetting.func_151525_a() * 0.11F);

        if (var4 > 0)
        {
            var3.setDamage(var3.getDamage() + var4 * 0.5D + 0.5D);
        }

        if (var5 > 0)
        {
            var3.setKnockbackStrength(var5);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, getHeldItem()) > 0 || getSkeletonType() == 1)
        {
            var3.setFire(100);
        }

        playSound("random.bow", 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
        worldObj.spawnEntityInWorld(var3);
    }

    /**
     * Return this skeleton's type.
     */
    public int getSkeletonType()
    {
        return dataWatcher.getWatchableObjectByte(13);
    }

    /**
     * Set this skeleton's type.
     */
    public void setSkeletonType(int par1)
    {
        dataWatcher.updateObject(13, Byte.valueOf((byte)par1));
        isImmuneToFire = par1 == 1;

        if (par1 == 1)
        {
            setSize(0.72F, 2.34F);
        }
        else
        {
            setSize(0.6F, 1.8F);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.func_150297_b("SkeletonType", 99))
        {
            byte var2 = par1NBTTagCompound.getByte("SkeletonType");
            setSkeletonType(var2);
        }

        setCombatTask();
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByte("SkeletonType", (byte)getSkeletonType());
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is
     * armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack)
    {
        super.setCurrentItemOrArmor(par1, par2ItemStack);

        if (!worldObj.isClient && par1 == 0)
        {
            setCombatTask();
        }
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return super.getYOffset() - 0.5D;
    }
}
