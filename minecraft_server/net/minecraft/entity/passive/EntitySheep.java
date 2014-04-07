package net.minecraft.entity.passive;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySheep extends EntityAnimal
{
    private final InventoryCrafting field_90016_e = new InventoryCrafting(new Container()
    {
        private static final String __OBFID = "CL_00001649";

        public boolean canInteractWith(EntityPlayer par1EntityPlayer)
        {
            return false;
        }
    }, 2, 1);

    /**
     * Holds the RGB table of the sheep colors - in OpenGL glColor3f values -
     * used to render the sheep colored fleece.
     */
    public static final float[][] fleeceColorTable = new float[][] { {1.0F, 1.0F, 1.0F}, {0.85F, 0.5F, 0.2F}, {0.7F, 0.3F, 0.85F}, {0.4F, 0.6F, 0.85F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.5F, 0.65F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.5F, 0.6F}, {0.5F, 0.25F, 0.7F}, {0.2F, 0.3F, 0.7F}, {0.4F, 0.3F, 0.2F}, {0.4F, 0.5F, 0.2F}, {0.6F, 0.2F, 0.2F}, {0.1F, 0.1F, 0.1F}};

    /**
     * Used to control movement as well as wool regrowth. Set to 40 on
     * handleHealthUpdate and counts down with each tick.
     */
    private int sheepTimer;
    private EntityAIEatGrass field_146087_bs = new EntityAIEatGrass(this);
    private static final String __OBFID = "CL_00001648";

    public EntitySheep(World par1World)
    {
        super(par1World);
        setSize(0.9F, 1.3F);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        tasks.addTask(2, new EntityAIMate(this, 1.0D));
        tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.wheat, false));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        tasks.addTask(5, field_146087_bs);
        tasks.addTask(6, new EntityAIWander(this, 1.0D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        field_90016_e.setInventorySlotContents(0, new ItemStack(Items.dye, 1, 0));
        field_90016_e.setInventorySlotContents(1, new ItemStack(Items.dye, 1, 0));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return true;
    }

    protected void updateAITasks()
    {
        sheepTimer = field_146087_bs.func_151499_f();
        super.updateAITasks();
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (worldObj.isClient)
        {
            sheepTimer = Math.max(0, sheepTimer - 1);
        }

        super.onLivingUpdate();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        if (!getSheared())
        {
            entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, getFleeceColor()), 0.0F);
        }
    }

    protected Item func_146068_u()
    {
        return Item.getItemFromBlock(Blocks.wool);
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.getItem() == Items.shears && !getSheared() && !isChild())
        {
            if (!worldObj.isClient)
            {
                setSheared(true);
                int var3 = 1 + rand.nextInt(3);

                for (int var4 = 0; var4 < var3; ++var4)
                {
                    EntityItem var5 = entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, getFleeceColor()), 1.0F);
                    var5.motionY += rand.nextFloat() * 0.05F;
                    var5.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    var5.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                }
            }

            var2.damageItem(1, par1EntityPlayer);
            playSound("mob.sheep.shear", 1.0F, 1.0F);
        }

        return super.interact(par1EntityPlayer);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("Sheared", getSheared());
        par1NBTTagCompound.setByte("Color", (byte)getFleeceColor());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        setSheared(par1NBTTagCompound.getBoolean("Sheared"));
        setFleeceColor(par1NBTTagCompound.getByte("Color"));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.sheep.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.sheep.say";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.sheep.say";
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        playSound("mob.sheep.step", 0.15F, 1.0F);
    }

    public int getFleeceColor()
    {
        return dataWatcher.getWatchableObjectByte(16) & 15;
    }

    public void setFleeceColor(int par1)
    {
        byte var2 = dataWatcher.getWatchableObjectByte(16);
        dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & 240 | par1 & 15)));
    }

    /**
     * returns true if a sheeps wool has been sheared
     */
    public boolean getSheared()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 16) != 0;
    }

    /**
     * make a sheep sheared if set to true
     */
    public void setSheared(boolean par1)
    {
        byte var2 = dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 16)));
        }
        else
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -17)));
        }
    }

    /**
     * This method is called when a sheep spawns in the world to select the
     * color of sheep fleece.
     */
    public static int getRandomFleeceColor(Random par0Random)
    {
        int var1 = par0Random.nextInt(100);
        return var1 < 5 ? 15 : (var1 < 10 ? 7 : (var1 < 15 ? 8 : (var1 < 18 ? 12 : (par0Random.nextInt(500) == 0 ? 6 : 0))));
    }

    public EntitySheep createChild(EntityAgeable par1EntityAgeable)
    {
        EntitySheep var2 = (EntitySheep)par1EntityAgeable;
        EntitySheep var3 = new EntitySheep(worldObj);
        int var4 = func_90014_a(this, var2);
        var3.setFleeceColor(15 - var4);
        return var3;
    }

    /**
     * This function applies the benefits of growing back wool and faster
     * growing up to the acting entity. (This function is used in the
     * AIEatGrass)
     */
    public void eatGrassBonus()
    {
        setSheared(false);

        if (isChild())
        {
            addGrowth(60);
        }
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
        setFleeceColor(getRandomFleeceColor(worldObj.rand));
        return par1EntityLivingData;
    }

    private int func_90014_a(EntityAnimal par1EntityAnimal, EntityAnimal par2EntityAnimal)
    {
        int var3 = func_90013_b(par1EntityAnimal);
        int var4 = func_90013_b(par2EntityAnimal);
        field_90016_e.getStackInSlot(0).setItemDamage(var3);
        field_90016_e.getStackInSlot(1).setItemDamage(var4);
        ItemStack var5 = CraftingManager.getInstance().findMatchingRecipe(field_90016_e, ((EntitySheep)par1EntityAnimal).worldObj);
        int var6;

        if (var5 != null && var5.getItem() == Items.dye)
        {
            var6 = var5.getItemDamage();
        }
        else
        {
            var6 = worldObj.rand.nextBoolean() ? var3 : var4;
        }

        return var6;
    }

    private int func_90013_b(EntityAnimal par1EntityAnimal)
    {
        return 15 - ((EntitySheep)par1EntityAnimal).getFleeceColor();
    }
}
