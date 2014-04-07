package net.minecraft.entity.monster;

import java.util.Calendar;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombie extends EntityMob
{
    protected static final IAttribute field_110186_bp = (new RangedAttribute("zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).setDescription("Spawn Reinforcements Chance");
    private static final UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.5D, 1);
    private final EntityAIBreakDoor field_146075_bs = new EntityAIBreakDoor(this);

    /**
     * Ticker used to determine the time remaining for this zombie to convert
     * into a villager when cured.
     */
    private int conversionTime;
    private boolean field_146076_bu = false;
    private float field_146074_bv = -1.0F;
    private float field_146073_bw;
    private static final String __OBFID = "CL_00001702";

    public EntityZombie(World par1World)
    {
        super(par1World);
        getNavigator().setBreakDoors(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
        tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        tasks.addTask(7, new EntityAIWander(this, 1.0D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
        setSize(0.6F, 1.8F);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
        getAttributeMap().registerAttribute(field_110186_bp).setBaseValue(rand.nextDouble() * 0.10000000149011612D);
    }

    protected void entityInit()
    {
        super.entityInit();
        getDataWatcher().addObject(12, Byte.valueOf((byte)0));
        getDataWatcher().addObject(13, Byte.valueOf((byte)0));
        getDataWatcher().addObject(14, Byte.valueOf((byte)0));
    }

    /**
     * Returns the current armor value as determined by a call to
     * InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        int var1 = super.getTotalArmorValue() + 2;

        if (var1 > 20)
        {
            var1 = 20;
        }

        return var1;
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return true;
    }

    public boolean func_146072_bX()
    {
        return field_146076_bu;
    }

    public void func_146070_a(boolean p_146070_1_)
    {
        if (field_146076_bu != p_146070_1_)
        {
            field_146076_bu = p_146070_1_;

            if (p_146070_1_)
            {
                tasks.addTask(1, field_146075_bs);
            }
            else
            {
                tasks.removeTask(field_146075_bs);
            }
        }
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return getDataWatcher().getWatchableObjectByte(12) == 1;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        if (isChild())
        {
            experienceValue = (int)(experienceValue * 2.5F);
        }

        return super.getExperiencePoints(par1EntityPlayer);
    }

    /**
     * Set whether this zombie is a child.
     */
    public void setChild(boolean par1)
    {
        getDataWatcher().updateObject(12, Byte.valueOf((byte)(par1 ? 1 : 0)));

        if (worldObj != null && !worldObj.isClient)
        {
            IAttributeInstance var2 = getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            var2.removeModifier(babySpeedBoostModifier);

            if (par1)
            {
                var2.applyModifier(babySpeedBoostModifier);
            }
        }

        func_146071_k(par1);
    }

    /**
     * Return whether this zombie is a villager.
     */
    public boolean isVillager()
    {
        return getDataWatcher().getWatchableObjectByte(13) == 1;
    }

    /**
     * Set whether this zombie is a villager.
     */
    public void setVillager(boolean par1)
    {
        getDataWatcher().updateObject(13, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (worldObj.isDaytime() && !worldObj.isClient && !isChild())
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

        super.onLivingUpdate();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (!super.attackEntityFrom(par1DamageSource, par2))
        {
            return false;
        }
        else
        {
            EntityLivingBase var3 = getAttackTarget();

            if (var3 == null && getEntityToAttack() instanceof EntityLivingBase)
            {
                var3 = (EntityLivingBase)getEntityToAttack();
            }

            if (var3 == null && par1DamageSource.getEntity() instanceof EntityLivingBase)
            {
                var3 = (EntityLivingBase)par1DamageSource.getEntity();
            }

            if (var3 != null && worldObj.difficultySetting == EnumDifficulty.HARD && rand.nextFloat() < getEntityAttribute(field_110186_bp).getAttributeValue())
            {
                int var4 = MathHelper.floor_double(posX);
                int var5 = MathHelper.floor_double(posY);
                int var6 = MathHelper.floor_double(posZ);
                EntityZombie var7 = new EntityZombie(worldObj);

                for (int var8 = 0; var8 < 50; ++var8)
                {
                    int var9 = var4 + MathHelper.getRandomIntegerInRange(rand, 7, 40) * MathHelper.getRandomIntegerInRange(rand, -1, 1);
                    int var10 = var5 + MathHelper.getRandomIntegerInRange(rand, 7, 40) * MathHelper.getRandomIntegerInRange(rand, -1, 1);
                    int var11 = var6 + MathHelper.getRandomIntegerInRange(rand, 7, 40) * MathHelper.getRandomIntegerInRange(rand, -1, 1);

                    if (World.doesBlockHaveSolidTopSurface(worldObj, var9, var10 - 1, var11) && worldObj.getBlockLightValue(var9, var10, var11) < 10)
                    {
                        var7.setPosition(var9, var10, var11);

                        if (worldObj.checkNoEntityCollision(var7.boundingBox) && worldObj.getCollidingBoundingBoxes(var7, var7.boundingBox).isEmpty() && !worldObj.isAnyLiquid(var7.boundingBox))
                        {
                            worldObj.spawnEntityInWorld(var7);
                            var7.setAttackTarget(var3);
                            var7.onSpawnWithEgg((IEntityLivingData)null);
                            getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, 0));
                            var7.getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, 0));
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (!worldObj.isClient && isConverting())
        {
            int var1 = getConversionTimeBoost();
            conversionTime -= var1;

            if (conversionTime <= 0)
            {
                convertToVillager();
            }
        }

        super.onUpdate();
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        boolean var2 = super.attackEntityAsMob(par1Entity);

        if (var2)
        {
            int var3 = worldObj.difficultySetting.func_151525_a();

            if (getHeldItem() == null && isBurning() && rand.nextFloat() < var3 * 0.3F)
            {
                par1Entity.setFire(2 * var3);
            }
        }

        return var2;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.zombie.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.zombie.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.zombie.death";
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        playSound("mob.zombie.step", 0.15F, 1.0F);
    }

    protected Item func_146068_u()
    {
        return Items.rotten_flesh;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    protected void dropRareDrop(int par1)
    {
        switch (rand.nextInt(3))
        {
        case 0:
            func_145779_a(Items.iron_ingot, 1);
            break;

        case 1:
            func_145779_a(Items.carrot, 1);
            break;

        case 2:
            func_145779_a(Items.potato, 1);
        }
    }

    /**
     * Makes entity wear random armor based on difficulty
     */
    protected void addRandomArmor()
    {
        super.addRandomArmor();

        if (rand.nextFloat() < (worldObj.difficultySetting == EnumDifficulty.HARD ? 0.05F : 0.01F))
        {
            int var1 = rand.nextInt(3);

            if (var1 == 0)
            {
                setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
            }
            else
            {
                setCurrentItemOrArmor(0, new ItemStack(Items.iron_shovel));
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);

        if (isChild())
        {
            par1NBTTagCompound.setBoolean("IsBaby", true);
        }

        if (isVillager())
        {
            par1NBTTagCompound.setBoolean("IsVillager", true);
        }

        par1NBTTagCompound.setInteger("ConversionTime", isConverting() ? conversionTime : -1);
        par1NBTTagCompound.setBoolean("CanBreakDoors", func_146072_bX());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.getBoolean("IsBaby"))
        {
            setChild(true);
        }

        if (par1NBTTagCompound.getBoolean("IsVillager"))
        {
            setVillager(true);
        }

        if (par1NBTTagCompound.func_150297_b("ConversionTime", 99) && par1NBTTagCompound.getInteger("ConversionTime") > -1)
        {
            startConversion(par1NBTTagCompound.getInteger("ConversionTime"));
        }

        func_146070_a(par1NBTTagCompound.getBoolean("CanBreakDoors"));
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(EntityLivingBase par1EntityLivingBase)
    {
        super.onKillEntity(par1EntityLivingBase);

        if ((worldObj.difficultySetting == EnumDifficulty.NORMAL || worldObj.difficultySetting == EnumDifficulty.HARD) && par1EntityLivingBase instanceof EntityVillager)
        {
            if (rand.nextBoolean()) { return; }

            EntityZombie var2 = new EntityZombie(worldObj);
            var2.copyLocationAndAnglesFrom(par1EntityLivingBase);
            worldObj.removeEntity(par1EntityLivingBase);
            var2.onSpawnWithEgg((IEntityLivingData)null);
            var2.setVillager(true);

            if (par1EntityLivingBase.isChild())
            {
                var2.setChild(true);
            }

            worldObj.spawnEntityInWorld(var2);
            worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)posX, (int)posY, (int)posZ, 0);
        }
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        Object par1EntityLivingData1 = super.onSpawnWithEgg(par1EntityLivingData);
        float var2 = worldObj.func_147462_b(posX, posY, posZ);
        setCanPickUpLoot(rand.nextFloat() < 0.55F * var2);

        if (par1EntityLivingData1 == null)
        {
            par1EntityLivingData1 = new EntityZombie.GroupData(worldObj.rand.nextFloat() < 0.05F, worldObj.rand.nextFloat() < 0.05F, null);
        }

        if (par1EntityLivingData1 instanceof EntityZombie.GroupData)
        {
            EntityZombie.GroupData var3 = (EntityZombie.GroupData)par1EntityLivingData1;

            if (var3.field_142046_b)
            {
                setVillager(true);
            }

            if (var3.field_142048_a)
            {
                setChild(true);
            }
        }

        func_146070_a(rand.nextFloat() < var2 * 0.1F);
        addRandomArmor();
        enchantEquipment();

        if (getEquipmentInSlot(4) == null)
        {
            Calendar var6 = worldObj.getCurrentDate();

            if (var6.get(2) + 1 == 10 && var6.get(5) == 31 && rand.nextFloat() < 0.25F)
            {
                setCurrentItemOrArmor(4, new ItemStack(rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                equipmentDropChances[4] = 0.0F;
            }
        }

        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(new AttributeModifier("Random spawn bonus", rand.nextDouble() * 0.05000000074505806D, 0));
        double var7 = rand.nextDouble() * 1.5D * worldObj.func_147462_b(posX, posY, posZ);

        if (var7 > 1.0D)
        {
            getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random zombie-spawn bonus", var7, 2));
        }

        if (rand.nextFloat() < var2 * 0.05F)
        {
            getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Leader zombie bonus", rand.nextDouble() * 0.25D + 0.5D, 0));
            getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier("Leader zombie bonus", rand.nextDouble() * 3.0D + 1.0D, 2));
            func_146070_a(true);
        }

        return (IEntityLivingData)par1EntityLivingData1;
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.getCurrentEquippedItem();

        if (var2 != null && var2.getItem() == Items.golden_apple && var2.getItemDamage() == 0 && isVillager() && this.isPotionActive(Potion.weakness))
        {
            if (!par1EntityPlayer.capabilities.isCreativeMode)
            {
                --var2.stackSize;
            }

            if (var2.stackSize <= 0)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
            }

            if (!worldObj.isClient)
            {
                startConversion(rand.nextInt(2401) + 3600);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Starts converting this zombie into a villager. The zombie converts into a
     * villager after the specified time in ticks.
     */
    protected void startConversion(int par1)
    {
        conversionTime = par1;
        getDataWatcher().updateObject(14, Byte.valueOf((byte)1));
        removePotionEffect(Potion.weakness.id);
        addPotionEffect(new PotionEffect(Potion.damageBoost.id, par1, Math.min(worldObj.difficultySetting.func_151525_a() - 1, 0)));
        worldObj.setEntityState(this, (byte)16);
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return !isConverting();
    }

    /**
     * Returns whether this zombie is in the process of converting to a villager
     */
    public boolean isConverting()
    {
        return getDataWatcher().getWatchableObjectByte(14) == 1;
    }

    /**
     * Convert this zombie into a villager.
     */
    protected void convertToVillager()
    {
        EntityVillager var1 = new EntityVillager(worldObj);
        var1.copyLocationAndAnglesFrom(this);
        var1.onSpawnWithEgg((IEntityLivingData)null);
        var1.setLookingForHome();

        if (isChild())
        {
            var1.setGrowingAge(-24000);
        }

        worldObj.removeEntity(this);
        worldObj.spawnEntityInWorld(var1);
        var1.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
        worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1017, (int)posX, (int)posY, (int)posZ, 0);
    }

    /**
     * Return the amount of time decremented from conversionTime every tick.
     */
    protected int getConversionTimeBoost()
    {
        int var1 = 1;

        if (rand.nextFloat() < 0.01F)
        {
            int var2 = 0;

            for (int var3 = (int)posX - 4; var3 < (int)posX + 4 && var2 < 14; ++var3)
            {
                for (int var4 = (int)posY - 4; var4 < (int)posY + 4 && var2 < 14; ++var4)
                {
                    for (int var5 = (int)posZ - 4; var5 < (int)posZ + 4 && var2 < 14; ++var5)
                    {
                        Block var6 = worldObj.getBlock(var3, var4, var5);

                        if (var6 == Blocks.iron_bars || var6 == Blocks.bed)
                        {
                            if (rand.nextFloat() < 0.3F)
                            {
                                ++var1;
                            }

                            ++var2;
                        }
                    }
                }
            }
        }

        return var1;
    }

    public void func_146071_k(boolean p_146071_1_)
    {
        func_146069_a(p_146071_1_ ? 0.5F : 1.0F);
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected final void setSize(float par1, float par2)
    {
        boolean var3 = field_146074_bv > 0.0F && field_146073_bw > 0.0F;
        field_146074_bv = par1;
        field_146073_bw = par2;

        if (!var3)
        {
            func_146069_a(1.0F);
        }
    }

    protected final void func_146069_a(float p_146069_1_)
    {
        super.setSize(field_146074_bv * p_146069_1_, field_146073_bw * p_146069_1_);
    }

    class GroupData implements IEntityLivingData
    {
        public boolean field_142048_a;
        public boolean field_142046_b;
        private static final String __OBFID = "CL_00001704";

        private GroupData(boolean par2, boolean par3)
        {
            field_142048_a = false;
            field_142046_b = false;
            field_142048_a = par2;
            field_142046_b = par3;
        }

        GroupData(boolean par2, boolean par3, Object par4EntityZombieINNER1)
        {
            this(par2, par3);
        }
    }
}
