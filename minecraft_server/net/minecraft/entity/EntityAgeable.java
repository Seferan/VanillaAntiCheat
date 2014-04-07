package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature
{
    private float field_98056_d = -1.0F;
    private float field_98057_e;
    private static final String __OBFID = "CL_00001530";

    public EntityAgeable(World par1World)
    {
        super(par1World);
    }

    public abstract EntityAgeable createChild(EntityAgeable var1);

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.getItem() == Items.spawn_egg)
        {
            if (!worldObj.isClient)
            {
                Class var3 = EntityList.getClassFromID(var2.getItemDamage());

                if (var3 != null && var3.isAssignableFrom(this.getClass()))
                {
                    EntityAgeable var4 = createChild(this);

                    if (var4 != null)
                    {
                        var4.setGrowingAge(-24000);
                        var4.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
                        worldObj.spawnEntityInWorld(var4);

                        if (var2.hasDisplayName())
                        {
                            var4.setCustomNameTag(var2.getDisplayName());
                        }

                        if (!par1EntityPlayer.capabilities.isCreativeMode)
                        {
                            --var2.stackSize;

                            if (var2.stackSize <= 0)
                            {
                                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                            }
                        }
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(12, new Integer(0));
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it
     * get's incremented on each tick, if it's positive, it get's decremented
     * each tick. Don't confuse this with EntityLiving.getAge. With a negative
     * value the Entity is considered a child.
     */
    public int getGrowingAge()
    {
        return dataWatcher.getWatchableObjectInt(12);
    }

    /**
     * "Adds the value of the parameter times 20 to the age of this entity. If
     * the entity is an adult (if the entity's age is greater than 0), it will
     * have no effect."
     */
    public void addGrowth(int par1)
    {
        int var2 = getGrowingAge();
        var2 += par1 * 20;

        if (var2 > 0)
        {
            var2 = 0;
        }

        setGrowingAge(var2);
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it
     * get's incremented on each tick, if it's positive, it get's decremented
     * each tick. With a negative value the Entity is considered a child.
     */
    public void setGrowingAge(int par1)
    {
        dataWatcher.updateObject(12, Integer.valueOf(par1));
        setScaleForAge(isChild());
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Age", getGrowingAge());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        setGrowingAge(par1NBTTagCompound.getInteger("Age"));
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (worldObj.isClient)
        {
            setScaleForAge(isChild());
        }
        else
        {
            int var1 = getGrowingAge();

            if (var1 < 0)
            {
                ++var1;
                setGrowingAge(var1);
            }
            else if (var1 > 0)
            {
                --var1;
                setGrowingAge(var1);
            }
        }
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return getGrowingAge() < 0;
    }

    /**
     * "Sets the scale for an ageable entity according to the boolean parameter, which says if it's a child."
     */
    public void setScaleForAge(boolean par1)
    {
        setScale(par1 ? 0.5F : 1.0F);
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected final void setSize(float par1, float par2)
    {
        boolean var3 = field_98056_d > 0.0F;
        field_98056_d = par1;
        field_98057_e = par2;

        if (!var3)
        {
            setScale(1.0F);
        }
    }

    protected final void setScale(float par1)
    {
        super.setSize(field_98056_d * par1, field_98057_e * par1);
    }
}
