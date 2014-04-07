package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityHorse extends EntityAnimal implements IInvBasic
{
    private static final IEntitySelector horseBreedingSelector = new IEntitySelector()
    {
        private static final String __OBFID = "CL_00001642";

        public boolean isEntityApplicable(Entity par1Entity)
        {
            return par1Entity instanceof EntityHorse && ((EntityHorse)par1Entity).func_110205_ce();
        }
    };
    private static final IAttribute horseJumpStrength = (new RangedAttribute("horse.jumpStrength", 0.7D, 0.0D, 2.0D)).setDescription("Jump Strength").setShouldWatch(true);
    private static final String[] horseArmorTextures = new String[] {null, "textures/entity/horse/armor/horse_armor_iron.png", "textures/entity/horse/armor/horse_armor_gold.png", "textures/entity/horse/armor/horse_armor_diamond.png"};
    private static final String[] field_110273_bx = new String[] {"", "meo", "goo", "dio"};
    private static final int[] armorValues = new int[] {0, 5, 7, 11};
    private static final String[] horseTextures = new String[] {"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
    private static final String[] field_110269_bA = new String[] {"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    private static final String[] horseMarkingTextures = new String[] {null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] field_110292_bC = new String[] {"", "wo_", "wmo", "wdo", "bdo"};
    private int eatingHaystackCounter;
    private int openMouthCounter;
    private int jumpRearingCounter;
    public int field_110278_bp;
    public int field_110279_bq;
    protected boolean horseJumping;
    private AnimalChest horseChest;
    private boolean hasReproduced;

    /**
     * "The higher this value, the more likely the horse is to be tamed next time a player rides it."
     */
    protected int temper;
    protected float jumpPower;
    private boolean field_110294_bI;
    private float headLean;
    private float prevHeadLean;
    private float rearingAmount;
    private float prevRearingAmount;
    private float mouthOpenness;
    private float prevMouthOpenness;
    private int field_110285_bP;
    private String field_110286_bQ;
    private String[] field_110280_bR = new String[3];
    private static final String __OBFID = "CL_00001641";

    public EntityHorse(World par1World)
    {
        super(par1World);
        setSize(1.4F, 1.6F);
        isImmuneToFire = false;
        setChested(false);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 1.2D));
        tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
        tasks.addTask(2, new EntityAIMate(this, 1.0D));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.0D));
        tasks.addTask(6, new EntityAIWander(this, 0.7D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        func_110226_cD();
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, Integer.valueOf(0));
        dataWatcher.addObject(19, Byte.valueOf((byte)0));
        dataWatcher.addObject(20, Integer.valueOf(0));
        dataWatcher.addObject(21, String.valueOf(""));
        dataWatcher.addObject(22, Integer.valueOf(0));
    }

    public void setHorseType(int par1)
    {
        dataWatcher.updateObject(19, Byte.valueOf((byte)par1));
        func_110230_cF();
    }

    /**
     * returns the horse type
     */
    public int getHorseType()
    {
        return dataWatcher.getWatchableObjectByte(19);
    }

    public void setHorseVariant(int par1)
    {
        dataWatcher.updateObject(20, Integer.valueOf(par1));
        func_110230_cF();
    }

    public int getHorseVariant()
    {
        return dataWatcher.getWatchableObjectInt(20);
    }

    /**
     * Gets the name of this command sender (usually username, but possibly
     * "Rcon")
     */
    public String getUsername()
    {
        if (hasCustomNameTag())
        {
            return getCustomNameTag();
        }
        else
        {
            int var1 = getHorseType();

            switch (var1)
            {
            case 0:
            default:
                return StatCollector.translateToLocal("entity.horse.name");

            case 1:
                return StatCollector.translateToLocal("entity.donkey.name");

            case 2:
                return StatCollector.translateToLocal("entity.mule.name");

            case 3:
                return StatCollector.translateToLocal("entity.zombiehorse.name");

            case 4:
                return StatCollector.translateToLocal("entity.skeletonhorse.name");
            }
        }
    }

    private boolean getHorseWatchableBoolean(int par1)
    {
        return (dataWatcher.getWatchableObjectInt(16) & par1) != 0;
    }

    private void setHorseWatchableBoolean(int par1, boolean par2)
    {
        int var3 = dataWatcher.getWatchableObjectInt(16);

        if (par2)
        {
            dataWatcher.updateObject(16, Integer.valueOf(var3 | par1));
        }
        else
        {
            dataWatcher.updateObject(16, Integer.valueOf(var3 & ~par1));
        }
    }

    public boolean isAdultHorse()
    {
        return !isChild();
    }

    public boolean isTame()
    {
        return getHorseWatchableBoolean(2);
    }

    public boolean func_110253_bW()
    {
        return isAdultHorse();
    }

    public String getOwnerName()
    {
        return dataWatcher.getWatchableObjectString(21);
    }

    public void setOwnerName(String par1Str)
    {
        dataWatcher.updateObject(21, par1Str);
    }

    public float getHorseSize()
    {
        int var1 = getGrowingAge();
        return var1 >= 0 ? 1.0F : 0.5F + (-24000 - var1) / -24000.0F * 0.5F;
    }

    /**
     * "Sets the scale for an ageable entity according to the boolean parameter, which says if it's a child."
     */
    public void setScaleForAge(boolean par1)
    {
        if (par1)
        {
            setScale(getHorseSize());
        }
        else
        {
            setScale(1.0F);
        }
    }

    public boolean isHorseJumping()
    {
        return horseJumping;
    }

    public void setHorseTamed(boolean par1)
    {
        setHorseWatchableBoolean(2, par1);
    }

    public void setHorseJumping(boolean par1)
    {
        horseJumping = par1;
    }

    public boolean allowLeashing()
    {
        return !func_110256_cu() && super.allowLeashing();
    }

    protected void func_142017_o(float par1)
    {
        if (par1 > 6.0F && isEatingHaystack())
        {
            setEatingHaystack(false);
        }
    }

    public boolean isChested()
    {
        return getHorseWatchableBoolean(8);
    }

    public int func_110241_cb()
    {
        return dataWatcher.getWatchableObjectInt(22);
    }

    /**
     * 0 = iron, 1 = gold, 2 = diamond
     */
    private int getHorseArmorIndex(ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return 0;
        }
        else
        {
            Item var2 = par1ItemStack.getItem();
            return var2 == Items.iron_horse_armor ? 1 : (var2 == Items.golden_horse_armor ? 2 : (var2 == Items.diamond_horse_armor ? 3 : 0));
        }
    }

    public boolean isEatingHaystack()
    {
        return getHorseWatchableBoolean(32);
    }

    public boolean isRearing()
    {
        return getHorseWatchableBoolean(64);
    }

    public boolean func_110205_ce()
    {
        return getHorseWatchableBoolean(16);
    }

    public boolean getHasReproduced()
    {
        return hasReproduced;
    }

    public void func_146086_d(ItemStack p_146086_1_)
    {
        dataWatcher.updateObject(22, Integer.valueOf(getHorseArmorIndex(p_146086_1_)));
        func_110230_cF();
    }

    public void func_110242_l(boolean par1)
    {
        setHorseWatchableBoolean(16, par1);
    }

    public void setChested(boolean par1)
    {
        setHorseWatchableBoolean(8, par1);
    }

    public void setHasReproduced(boolean par1)
    {
        hasReproduced = par1;
    }

    public void setHorseSaddled(boolean par1)
    {
        setHorseWatchableBoolean(4, par1);
    }

    public int getTemper()
    {
        return temper;
    }

    public void setTemper(int par1)
    {
        temper = par1;
    }

    public int increaseTemper(int par1)
    {
        int var2 = MathHelper.clamp_int(getTemper() + par1, 0, getMaxTemper());
        setTemper(var2);
        return var2;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        Entity var3 = par1DamageSource.getEntity();
        return riddenByEntity != null && riddenByEntity.equals(var3) ? false : super.attackEntityFrom(par1DamageSource, par2);
    }

    /**
     * Returns the current armor value as determined by a call to
     * InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return armorValues[func_110241_cb()];
    }

    /**
     * Returns true if this entity should push and be pushed by other entities
     * when colliding.
     */
    public boolean canBePushed()
    {
        return riddenByEntity == null;
    }

    public boolean prepareChunkForSpawn()
    {
        int var1 = MathHelper.floor_double(posX);
        int var2 = MathHelper.floor_double(posZ);
        worldObj.getBiomeGenForCoords(var1, var2);
        return true;
    }

    public void dropChests()
    {
        if (!worldObj.isClient && isChested())
        {
            func_145779_a(Item.getItemFromBlock(Blocks.chest), 1);
            setChested(false);
        }
    }

    private void func_110266_cB()
    {
        openHorseMouth();
        worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        if (par1 > 1.0F)
        {
            playSound("mob.horse.land", 0.4F, 1.0F);
        }

        int var2 = MathHelper.ceiling_float_int(par1 * 0.5F - 3.0F);

        if (var2 > 0)
        {
            attackEntityFrom(DamageSource.fall, var2);

            if (riddenByEntity != null)
            {
                riddenByEntity.attackEntityFrom(DamageSource.fall, var2);
            }

            Block var3 = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.2D - prevRotationYaw), MathHelper.floor_double(posZ));

            if (var3.getMaterial() != Material.air)
            {
                Block.SoundType var4 = var3.stepSound;
                worldObj.playSoundAtEntity(this, var4.getStepResourcePath(), var4.getVolume() * 0.5F, var4.getFrequency() * 0.75F);
            }
        }
    }

    private int func_110225_cC()
    {
        int var1 = getHorseType();
        return isChested() && (var1 == 1 || var1 == 2) ? 17 : 2;
    }

    private void func_110226_cD()
    {
        AnimalChest var1 = horseChest;
        horseChest = new AnimalChest("HorseChest", func_110225_cC());
        horseChest.func_110133_a(getUsername());

        if (var1 != null)
        {
            var1.func_110132_b(this);
            int var2 = Math.min(var1.getSizeInventory(), horseChest.getSizeInventory());

            for (int var3 = 0; var3 < var2; ++var3)
            {
                ItemStack var4 = var1.getStackInSlot(var3);

                if (var4 != null)
                {
                    horseChest.setInventorySlotContents(var3, var4.copy());
                }
            }

            var1 = null;
        }

        horseChest.func_110134_a(this);
        func_110232_cE();
    }

    private void func_110232_cE()
    {
        if (!worldObj.isClient)
        {
            setHorseSaddled(horseChest.getStackInSlot(0) != null);

            if (func_110259_cr())
            {
                func_146086_d(horseChest.getStackInSlot(1));
            }
        }
    }

    /**
     * Called by InventoryBasic.onInventoryChanged() on a array that is never
     * filled.
     */
    public void onInventoryChanged(InventoryBasic par1InventoryBasic)
    {
        int var2 = func_110241_cb();
        boolean var3 = isHorseSaddled();
        func_110232_cE();

        if (ticksExisted > 20)
        {
            if (var2 == 0 && var2 != func_110241_cb())
            {
                playSound("mob.horse.armor", 0.5F, 1.0F);
            }
            else if (var2 != func_110241_cb())
            {
                playSound("mob.horse.armor", 0.5F, 1.0F);
            }

            if (!var3 && isHorseSaddled())
            {
                playSound("mob.horse.leather", 0.5F, 1.0F);
            }
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this
     * entity.
     */
    public boolean getCanSpawnHere()
    {
        prepareChunkForSpawn();
        return super.getCanSpawnHere();
    }

    protected EntityHorse getClosestHorse(Entity par1Entity, double par2)
    {
        double var4 = Double.MAX_VALUE;
        Entity var6 = null;
        List var7 = worldObj.getEntitiesWithinAABBExcludingEntity(par1Entity, par1Entity.boundingBox.addCoord(par2, par2, par2), horseBreedingSelector);
        Iterator var8 = var7.iterator();

        while (var8.hasNext())
        {
            Entity var9 = (Entity)var8.next();
            double var10 = var9.getDistanceSq(par1Entity.posX, par1Entity.posY, par1Entity.posZ);

            if (var10 < var4)
            {
                var6 = var9;
                var4 = var10;
            }
        }

        return (EntityHorse)var6;
    }

    public double getHorseJumpStrength()
    {
        return getEntityAttribute(horseJumpStrength).getAttributeValue();
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        openHorseMouth();
        int var1 = getHorseType();
        return var1 == 3 ? "mob.horse.zombie.death" : (var1 == 4 ? "mob.horse.skeleton.death" : (var1 != 1 && var1 != 2 ? "mob.horse.death" : "mob.horse.donkey.death"));
    }

    protected Item func_146068_u()
    {
        boolean var1 = rand.nextInt(4) == 0;
        int var2 = getHorseType();
        return var2 == 4 ? Items.bone : (var2 == 3 ? (var1 ? Item.getItemById(0) : Items.rotten_flesh) : Items.leather);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        openHorseMouth();

        if (rand.nextInt(3) == 0)
        {
            makeHorseRear();
        }

        int var1 = getHorseType();
        return var1 == 3 ? "mob.horse.zombie.hit" : (var1 == 4 ? "mob.horse.skeleton.hit" : (var1 != 1 && var1 != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit"));
    }

    public boolean isHorseSaddled()
    {
        return getHorseWatchableBoolean(4);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        openHorseMouth();

        if (rand.nextInt(10) == 0 && !isMovementBlocked())
        {
            makeHorseRear();
        }

        int var1 = getHorseType();
        return var1 == 3 ? "mob.horse.zombie.idle" : (var1 == 4 ? "mob.horse.skeleton.idle" : (var1 != 1 && var1 != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle"));
    }

    protected String getAngrySoundName()
    {
        openHorseMouth();
        makeHorseRear();
        int var1 = getHorseType();
        return var1 != 3 && var1 != 4 ? (var1 != 1 && var1 != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry") : null;
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        Block.SoundType var5 = p_145780_4_.stepSound;

        if (worldObj.getBlock(p_145780_1_, p_145780_2_ + 1, p_145780_3_) == Blocks.snow_layer)
        {
            var5 = Blocks.snow_layer.stepSound;
        }

        if (!p_145780_4_.getMaterial().isLiquid())
        {
            int var6 = getHorseType();

            if (riddenByEntity != null && var6 != 1 && var6 != 2)
            {
                ++field_110285_bP;

                if (field_110285_bP > 5 && field_110285_bP % 3 == 0)
                {
                    playSound("mob.horse.gallop", var5.getVolume() * 0.15F, var5.getFrequency());

                    if (var6 == 0 && rand.nextInt(10) == 0)
                    {
                        playSound("mob.horse.breathe", var5.getVolume() * 0.6F, var5.getFrequency());
                    }
                }
                else if (field_110285_bP <= 5)
                {
                    playSound("mob.horse.wood", var5.getVolume() * 0.15F, var5.getFrequency());
                }
            }
            else if (var5 == Block.soundTypeWood)
            {
                playSound("mob.horse.wood", var5.getVolume() * 0.15F, var5.getFrequency());
            }
            else
            {
                playSound("mob.horse.soft", var5.getVolume() * 0.15F, var5.getFrequency());
            }
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(horseJumpStrength);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(53.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22499999403953552D);
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 6;
    }

    public int getMaxTemper()
    {
        return 100;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.8F;
    }

    /**
     * Get number of ticks, at least during which the living entity will be
     * silent.
     */
    public int getTalkInterval()
    {
        return 400;
    }

    private void func_110230_cF()
    {
        field_110286_bQ = null;
    }

    public void openGUI(EntityPlayer par1EntityPlayer)
    {
        if (!worldObj.isClient && (riddenByEntity == null || riddenByEntity == par1EntityPlayer) && isTame())
        {
            horseChest.func_110133_a(getUsername());
            par1EntityPlayer.displayGUIHorse(this, horseChest);
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.getItem() == Items.spawn_egg)
        {
            return super.interact(par1EntityPlayer);
        }
        else if (!isTame() && func_110256_cu())
        {
            return false;
        }
        else if (isTame() && isAdultHorse() && par1EntityPlayer.isSneaking())
        {
            openGUI(par1EntityPlayer);
            return true;
        }
        else if (func_110253_bW() && riddenByEntity != null)
        {
            return super.interact(par1EntityPlayer);
        }
        else
        {
            if (var2 != null)
            {
                boolean var3 = false;

                if (func_110259_cr())
                {
                    byte var4 = -1;

                    if (var2.getItem() == Items.iron_horse_armor)
                    {
                        var4 = 1;
                    }
                    else if (var2.getItem() == Items.golden_horse_armor)
                    {
                        var4 = 2;
                    }
                    else if (var2.getItem() == Items.diamond_horse_armor)
                    {
                        var4 = 3;
                    }

                    if (var4 >= 0)
                    {
                        if (!isTame())
                        {
                            makeHorseRearWithSound();
                            return true;
                        }

                        openGUI(par1EntityPlayer);
                        return true;
                    }
                }

                if (!var3 && !func_110256_cu())
                {
                    float var7 = 0.0F;
                    short var5 = 0;
                    byte var6 = 0;

                    if (var2.getItem() == Items.wheat)
                    {
                        var7 = 2.0F;
                        var5 = 60;
                        var6 = 3;
                    }
                    else if (var2.getItem() == Items.sugar)
                    {
                        var7 = 1.0F;
                        var5 = 30;
                        var6 = 3;
                    }
                    else if (var2.getItem() == Items.bread)
                    {
                        var7 = 7.0F;
                        var5 = 180;
                        var6 = 3;
                    }
                    else if (Block.getBlockFromItem(var2.getItem()) == Blocks.hay_block)
                    {
                        var7 = 20.0F;
                        var5 = 180;
                    }
                    else if (var2.getItem() == Items.apple)
                    {
                        var7 = 3.0F;
                        var5 = 60;
                        var6 = 3;
                    }
                    else if (var2.getItem() == Items.golden_carrot)
                    {
                        var7 = 4.0F;
                        var5 = 60;
                        var6 = 5;

                        if (isTame() && getGrowingAge() == 0)
                        {
                            var3 = true;
                            func_146082_f(par1EntityPlayer);
                        }
                    }
                    else if (var2.getItem() == Items.golden_apple)
                    {
                        var7 = 10.0F;
                        var5 = 240;
                        var6 = 10;

                        if (isTame() && getGrowingAge() == 0)
                        {
                            var3 = true;
                            func_146082_f(par1EntityPlayer);
                        }
                    }

                    if (getHealth() < getMaxHealth() && var7 > 0.0F)
                    {
                        heal(var7);
                        var3 = true;
                    }

                    if (!isAdultHorse() && var5 > 0)
                    {
                        addGrowth(var5);
                        var3 = true;
                    }

                    if (var6 > 0 && (var3 || !isTame()) && var6 < getMaxTemper())
                    {
                        var3 = true;
                        increaseTemper(var6);
                    }

                    if (var3)
                    {
                        func_110266_cB();
                    }
                }

                if (!isTame() && !var3)
                {
                    if (var2 != null && var2.interactWithEntity(par1EntityPlayer, this)) { return true; }

                    makeHorseRearWithSound();
                    return true;
                }

                if (!var3 && func_110229_cs() && !isChested() && var2.getItem() == Item.getItemFromBlock(Blocks.chest))
                {
                    setChested(true);
                    playSound("mob.chickenplop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
                    var3 = true;
                    func_110226_cD();
                }

                if (!var3 && func_110253_bW() && !isHorseSaddled() && var2.getItem() == Items.saddle)
                {
                    openGUI(par1EntityPlayer);
                    return true;
                }

                if (var3)
                {
                    if (!par1EntityPlayer.capabilities.isCreativeMode && --var2.stackSize == 0)
                    {
                        par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                    }

                    return true;
                }
            }

            if (func_110253_bW() && riddenByEntity == null)
            {
                if (var2 != null && var2.interactWithEntity(par1EntityPlayer, this))
                {
                    return true;
                }
                else
                {
                    func_110237_h(par1EntityPlayer);
                    return true;
                }
            }
            else
            {
                return super.interact(par1EntityPlayer);
            }
        }
    }

    private void func_110237_h(EntityPlayer par1EntityPlayer)
    {
        par1EntityPlayer.rotationYaw = rotationYaw;
        par1EntityPlayer.rotationPitch = rotationPitch;
        setEatingHaystack(false);
        setRearing(false);

        if (!worldObj.isClient)
        {
            par1EntityPlayer.mountEntity(this);
        }
    }

    public boolean func_110259_cr()
    {
        return getHorseType() == 0;
    }

    public boolean func_110229_cs()
    {
        int var1 = getHorseType();
        return var1 == 2 || var1 == 1;
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked()
    {
        return riddenByEntity != null && isHorseSaddled() ? true : isEatingHaystack() || isRearing();
    }

    public boolean func_110256_cu()
    {
        int var1 = getHorseType();
        return var1 == 3 || var1 == 4;
    }

    public boolean func_110222_cv()
    {
        return func_110256_cu() || getHorseType() == 2;
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed
     * it (wheat, carrots or seeds depending on the animal type)
     */
    public boolean isBreedingItem(ItemStack par1ItemStack)
    {
        return false;
    }

    private void func_110210_cH()
    {
        field_110278_bp = 1;
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);

        if (!worldObj.isClient)
        {
            dropChestItems();
        }
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (rand.nextInt(200) == 0)
        {
            func_110210_cH();
        }

        super.onLivingUpdate();

        if (!worldObj.isClient)
        {
            if (rand.nextInt(900) == 0 && deathTime == 0)
            {
                heal(1.0F);
            }

            if (!isEatingHaystack() && riddenByEntity == null && rand.nextInt(300) == 0 && worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY) - 1, MathHelper.floor_double(posZ)) == Blocks.grass)
            {
                setEatingHaystack(true);
            }

            if (isEatingHaystack() && ++eatingHaystackCounter > 50)
            {
                eatingHaystackCounter = 0;
                setEatingHaystack(false);
            }

            if (func_110205_ce() && !isAdultHorse() && !isEatingHaystack())
            {
                EntityHorse var1 = getClosestHorse(this, 16.0D);

                if (var1 != null && getDistanceSqToEntity(var1) > 4.0D)
                {
                    PathEntity var2 = worldObj.getPathEntityToEntity(this, var1, 16.0F, true, false, false, true);
                    setPathToEntity(var2);
                }
            }
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (worldObj.isClient && dataWatcher.hasObjectChanged())
        {
            dataWatcher.func_111144_e();
            func_110230_cF();
        }

        if (openMouthCounter > 0 && ++openMouthCounter > 30)
        {
            openMouthCounter = 0;
            setHorseWatchableBoolean(128, false);
        }

        if (!worldObj.isClient && jumpRearingCounter > 0 && ++jumpRearingCounter > 20)
        {
            jumpRearingCounter = 0;
            setRearing(false);
        }

        if (field_110278_bp > 0 && ++field_110278_bp > 8)
        {
            field_110278_bp = 0;
        }

        if (field_110279_bq > 0)
        {
            ++field_110279_bq;

            if (field_110279_bq > 300)
            {
                field_110279_bq = 0;
            }
        }

        prevHeadLean = headLean;

        if (isEatingHaystack())
        {
            headLean += (1.0F - headLean) * 0.4F + 0.05F;

            if (headLean > 1.0F)
            {
                headLean = 1.0F;
            }
        }
        else
        {
            headLean += (0.0F - headLean) * 0.4F - 0.05F;

            if (headLean < 0.0F)
            {
                headLean = 0.0F;
            }
        }

        prevRearingAmount = rearingAmount;

        if (isRearing())
        {
            prevHeadLean = headLean = 0.0F;
            rearingAmount += (1.0F - rearingAmount) * 0.4F + 0.05F;

            if (rearingAmount > 1.0F)
            {
                rearingAmount = 1.0F;
            }
        }
        else
        {
            field_110294_bI = false;
            rearingAmount += (0.8F * rearingAmount * rearingAmount * rearingAmount - rearingAmount) * 0.6F - 0.05F;

            if (rearingAmount < 0.0F)
            {
                rearingAmount = 0.0F;
            }
        }

        prevMouthOpenness = mouthOpenness;

        if (getHorseWatchableBoolean(128))
        {
            mouthOpenness += (1.0F - mouthOpenness) * 0.7F + 0.05F;

            if (mouthOpenness > 1.0F)
            {
                mouthOpenness = 1.0F;
            }
        }
        else
        {
            mouthOpenness += (0.0F - mouthOpenness) * 0.7F - 0.05F;

            if (mouthOpenness < 0.0F)
            {
                mouthOpenness = 0.0F;
            }
        }
    }

    private void openHorseMouth()
    {
        if (!worldObj.isClient)
        {
            openMouthCounter = 1;
            setHorseWatchableBoolean(128, true);
        }
    }

    private boolean func_110200_cJ()
    {
        return riddenByEntity == null && ridingEntity == null && isTame() && isAdultHorse() && !func_110222_cv() && getHealth() >= getMaxHealth();
    }

    public void setEating(boolean par1)
    {
        setHorseWatchableBoolean(32, par1);
    }

    public void setEatingHaystack(boolean par1)
    {
        setEating(par1);
    }

    public void setRearing(boolean par1)
    {
        if (par1)
        {
            setEatingHaystack(false);
        }

        setHorseWatchableBoolean(64, par1);
    }

    private void makeHorseRear()
    {
        if (!worldObj.isClient)
        {
            jumpRearingCounter = 1;
            setRearing(true);
        }
    }

    public void makeHorseRearWithSound()
    {
        makeHorseRear();
        String var1 = getAngrySoundName();

        if (var1 != null)
        {
            playSound(var1, getSoundVolume(), getSoundPitch());
        }
    }

    public void dropChestItems()
    {
        dropItemsInChest(this, horseChest);
        dropChests();
    }

    private void dropItemsInChest(Entity par1Entity, AnimalChest par2AnimalChest)
    {
        if (par2AnimalChest != null && !worldObj.isClient)
        {
            for (int var3 = 0; var3 < par2AnimalChest.getSizeInventory(); ++var3)
            {
                ItemStack var4 = par2AnimalChest.getStackInSlot(var3);

                if (var4 != null)
                {
                    entityDropItem(var4, 0.0F);
                }
            }
        }
    }

    public boolean setTamedBy(EntityPlayer par1EntityPlayer)
    {
        setOwnerName(par1EntityPlayer.getUsername());
        setHorseTamed(true);
        return true;
    }

    /**
     * Moves the entity based on the specified heading. Args: strafe, forward
     */
    public void moveEntityWithHeading(float par1, float par2)
    {
        if (riddenByEntity != null && isHorseSaddled())
        {
            prevRotationYaw = rotationYaw = riddenByEntity.rotationYaw;
            rotationPitch = riddenByEntity.rotationPitch * 0.5F;
            setRotation(rotationYaw, rotationPitch);
            rotationYawHead = renderYawOffset = rotationYaw;
            par1 = ((EntityLivingBase)riddenByEntity).moveStrafing * 0.5F;
            par2 = ((EntityLivingBase)riddenByEntity).moveForward;

            if (par2 <= 0.0F)
            {
                par2 *= 0.25F;
                field_110285_bP = 0;
            }

            if (onGround && jumpPower == 0.0F && isRearing() && !field_110294_bI)
            {
                par1 = 0.0F;
                par2 = 0.0F;
            }

            if (jumpPower > 0.0F && !isHorseJumping() && onGround)
            {
                motionY = getHorseJumpStrength() * jumpPower;

                if (this.isPotionActive(Potion.jump))
                {
                    motionY += (getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
                }

                setHorseJumping(true);
                isAirBorne = true;

                if (par2 > 0.0F)
                {
                    float var3 = MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F);
                    float var4 = MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F);
                    motionX += -0.4F * var3 * jumpPower;
                    motionZ += 0.4F * var4 * jumpPower;
                    playSound("mob.horse.jump", 0.4F, 1.0F);
                }

                jumpPower = 0.0F;
            }

            stepHeight = 1.0F;
            jumpMovementFactor = getAIMoveSpeed() * 0.1F;

            if (!worldObj.isClient)
            {
                setAIMoveSpeed((float)getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
                super.moveEntityWithHeading(par1, par2);
            }

            if (onGround)
            {
                jumpPower = 0.0F;
                setHorseJumping(false);
            }

            prevLimbSwingAmount = limbSwingAmount;
            double var8 = posX - prevPosX;
            double var5 = posZ - prevPosZ;
            float var7 = MathHelper.sqrt_double(var8 * var8 + var5 * var5) * 4.0F;

            if (var7 > 1.0F)
            {
                var7 = 1.0F;
            }

            limbSwingAmount += (var7 - limbSwingAmount) * 0.4F;
            limbSwing += limbSwingAmount;
        }
        else
        {
            stepHeight = 0.5F;
            jumpMovementFactor = 0.02F;
            super.moveEntityWithHeading(par1, par2);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("EatingHaystack", isEatingHaystack());
        par1NBTTagCompound.setBoolean("ChestedHorse", isChested());
        par1NBTTagCompound.setBoolean("HasReproduced", getHasReproduced());
        par1NBTTagCompound.setBoolean("Bred", func_110205_ce());
        par1NBTTagCompound.setInteger("Type", getHorseType());
        par1NBTTagCompound.setInteger("Variant", getHorseVariant());
        par1NBTTagCompound.setInteger("Temper", getTemper());
        par1NBTTagCompound.setBoolean("Tame", isTame());
        par1NBTTagCompound.setString("OwnerName", getOwnerName());

        if (isChested())
        {
            NBTTagList var2 = new NBTTagList();

            for (int var3 = 2; var3 < horseChest.getSizeInventory(); ++var3)
            {
                ItemStack var4 = horseChest.getStackInSlot(var3);

                if (var4 != null)
                {
                    NBTTagCompound var5 = new NBTTagCompound();
                    var5.setByte("Slot", (byte)var3);
                    var4.writeToNBT(var5);
                    var2.appendTag(var5);
                }
            }

            par1NBTTagCompound.setTag("Items", var2);
        }

        if (horseChest.getStackInSlot(1) != null)
        {
            par1NBTTagCompound.setTag("ArmorItem", horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
        }

        if (horseChest.getStackInSlot(0) != null)
        {
            par1NBTTagCompound.setTag("SaddleItem", horseChest.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        setEatingHaystack(par1NBTTagCompound.getBoolean("EatingHaystack"));
        func_110242_l(par1NBTTagCompound.getBoolean("Bred"));
        setChested(par1NBTTagCompound.getBoolean("ChestedHorse"));
        setHasReproduced(par1NBTTagCompound.getBoolean("HasReproduced"));
        setHorseType(par1NBTTagCompound.getInteger("Type"));
        setHorseVariant(par1NBTTagCompound.getInteger("Variant"));
        setTemper(par1NBTTagCompound.getInteger("Temper"));
        setHorseTamed(par1NBTTagCompound.getBoolean("Tame"));

        if (par1NBTTagCompound.func_150297_b("OwnerName", 8))
        {
            setOwnerName(par1NBTTagCompound.getString("OwnerName"));
        }

        IAttributeInstance var2 = getAttributeMap().getAttributeInstanceByName("Speed");

        if (var2 != null)
        {
            getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(var2.getBaseValue() * 0.25D);
        }

        if (isChested())
        {
            NBTTagList var3 = par1NBTTagCompound.getTagList("Items", 10);
            func_110226_cD();

            for (int var4 = 0; var4 < var3.tagCount(); ++var4)
            {
                NBTTagCompound var5 = var3.getCompoundTagAt(var4);
                int var6 = var5.getByte("Slot") & 255;

                if (var6 >= 2 && var6 < horseChest.getSizeInventory())
                {
                    horseChest.setInventorySlotContents(var6, ItemStack.loadItemStackFromNBT(var5));
                }
            }
        }

        ItemStack var7;

        if (par1NBTTagCompound.func_150297_b("ArmorItem", 10))
        {
            var7 = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("ArmorItem"));

            if (var7 != null && func_146085_a(var7.getItem()))
            {
                horseChest.setInventorySlotContents(1, var7);
            }
        }

        if (par1NBTTagCompound.func_150297_b("SaddleItem", 10))
        {
            var7 = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("SaddleItem"));

            if (var7 != null && var7.getItem() == Items.saddle)
            {
                horseChest.setInventorySlotContents(0, var7);
            }
        }
        else if (par1NBTTagCompound.getBoolean("Saddle"))
        {
            horseChest.setInventorySlotContents(0, new ItemStack(Items.saddle));
        }

        func_110232_cE();
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    public boolean canMateWith(EntityAnimal par1EntityAnimal)
    {
        if (par1EntityAnimal == this)
        {
            return false;
        }
        else if (par1EntityAnimal.getClass() != this.getClass())
        {
            return false;
        }
        else
        {
            EntityHorse var2 = (EntityHorse)par1EntityAnimal;

            if (func_110200_cJ() && var2.func_110200_cJ())
            {
                int var3 = getHorseType();
                int var4 = var2.getHorseType();
                return var3 == var4 || var3 == 0 && var4 == 1 || var3 == 1 && var4 == 0;
            }
            else
            {
                return false;
            }
        }
    }

    public EntityAgeable createChild(EntityAgeable par1EntityAgeable)
    {
        EntityHorse var2 = (EntityHorse)par1EntityAgeable;
        EntityHorse var3 = new EntityHorse(worldObj);
        int var4 = getHorseType();
        int var5 = var2.getHorseType();
        int var6 = 0;

        if (var4 == var5)
        {
            var6 = var4;
        }
        else if (var4 == 0 && var5 == 1 || var4 == 1 && var5 == 0)
        {
            var6 = 2;
        }

        if (var6 == 0)
        {
            int var8 = rand.nextInt(9);
            int var7;

            if (var8 < 4)
            {
                var7 = getHorseVariant() & 255;
            }
            else if (var8 < 8)
            {
                var7 = var2.getHorseVariant() & 255;
            }
            else
            {
                var7 = rand.nextInt(7);
            }

            int var9 = rand.nextInt(5);

            if (var9 < 2)
            {
                var7 |= getHorseVariant() & 65280;
            }
            else if (var9 < 4)
            {
                var7 |= var2.getHorseVariant() & 65280;
            }
            else
            {
                var7 |= rand.nextInt(5) << 8 & 65280;
            }

            var3.setHorseVariant(var7);
        }

        var3.setHorseType(var6);
        double var14 = getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() + par1EntityAgeable.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() + func_110267_cL();
        var3.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(var14 / 3.0D);
        double var13 = getEntityAttribute(horseJumpStrength).getBaseValue() + par1EntityAgeable.getEntityAttribute(horseJumpStrength).getBaseValue() + func_110245_cM();
        var3.getEntityAttribute(horseJumpStrength).setBaseValue(var13 / 3.0D);
        double var11 = getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() + par1EntityAgeable.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() + func_110203_cN();
        var3.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(var11 / 3.0D);
        return var3;
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        Object par1EntityLivingData1 = super.onSpawnWithEgg(par1EntityLivingData);
        boolean var2 = false;
        int var3 = 0;
        int var7;

        if (par1EntityLivingData1 instanceof EntityHorse.GroupData)
        {
            var7 = ((EntityHorse.GroupData)par1EntityLivingData1).field_111107_a;
            var3 = ((EntityHorse.GroupData)par1EntityLivingData1).field_111106_b & 255 | rand.nextInt(5) << 8;
        }
        else
        {
            if (rand.nextInt(10) == 0)
            {
                var7 = 1;
            }
            else
            {
                int var4 = rand.nextInt(7);
                int var5 = rand.nextInt(5);
                var7 = 0;
                var3 = var4 | var5 << 8;
            }

            par1EntityLivingData1 = new EntityHorse.GroupData(var7, var3);
        }

        setHorseType(var7);
        setHorseVariant(var3);

        if (rand.nextInt(5) == 0)
        {
            setGrowingAge(-24000);
        }

        if (var7 != 4 && var7 != 3)
        {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(func_110267_cL());

            if (var7 == 0)
            {
                getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(func_110203_cN());
            }
            else
            {
                getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.17499999701976776D);
            }
        }
        else
        {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
            getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
        }

        if (var7 != 2 && var7 != 1)
        {
            getEntityAttribute(horseJumpStrength).setBaseValue(func_110245_cM());
        }
        else
        {
            getEntityAttribute(horseJumpStrength).setBaseValue(0.5D);
        }

        setHealth(getMaxHealth());
        return (IEntityLivingData)par1EntityLivingData1;
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return true;
    }

    public void setJumpPower(int par1)
    {
        if (isHorseSaddled())
        {
            if (par1 < 0)
            {
                par1 = 0;
            }
            else
            {
                field_110294_bI = true;
                makeHorseRear();
            }

            if (par1 >= 90)
            {
                jumpPower = 1.0F;
            }
            else
            {
                jumpPower = 0.4F + 0.4F * par1 / 90.0F;
            }
        }
    }

    public void updateRiderPosition()
    {
        super.updateRiderPosition();

        if (prevRearingAmount > 0.0F)
        {
            float var1 = MathHelper.sin(renderYawOffset * (float)Math.PI / 180.0F);
            float var2 = MathHelper.cos(renderYawOffset * (float)Math.PI / 180.0F);
            float var3 = 0.7F * prevRearingAmount;
            float var4 = 0.15F * prevRearingAmount;
            riddenByEntity.setPosition(posX + var3 * var1, posY + getMountedYOffset() + riddenByEntity.getYOffset() + var4, posZ - var3 * var2);

            if (riddenByEntity instanceof EntityLivingBase)
            {
                ((EntityLivingBase)riddenByEntity).renderYawOffset = renderYawOffset;
            }
        }
    }

    private float func_110267_cL()
    {
        return 15.0F + rand.nextInt(8) + rand.nextInt(9);
    }

    private double func_110245_cM()
    {
        return 0.4000000059604645D + rand.nextDouble() * 0.2D + rand.nextDouble() * 0.2D + rand.nextDouble() * 0.2D;
    }

    private double func_110203_cN()
    {
        return (0.44999998807907104D + rand.nextDouble() * 0.3D + rand.nextDouble() * 0.3D + rand.nextDouble() * 0.3D) * 0.25D;
    }

    public static boolean func_146085_a(Item p_146085_0_)
    {
        return p_146085_0_ == Items.iron_horse_armor || p_146085_0_ == Items.golden_horse_armor || p_146085_0_ == Items.diamond_horse_armor;
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        return false;
    }

    public static class GroupData implements IEntityLivingData
    {
        public int field_111107_a;
        public int field_111106_b;
        private static final String __OBFID = "CL_00001643";

        public GroupData(int par1, int par2)
        {
            field_111107_a = par1;
            field_111106_b = par2;
        }
    }
}
