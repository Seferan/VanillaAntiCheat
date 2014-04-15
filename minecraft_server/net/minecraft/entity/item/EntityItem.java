package net.minecraft.entity.item;

import java.util.Iterator;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem extends Entity
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * The age of this EntityItem (used to animate it up and down as well as
     * expire it)
     */
    public int age;
    public int delayBeforeCanPickup;

    /** The health of this EntityItem. (For example, damage for tools) */
    private int health;
    private String thrower;
    private String owner;

    /** The EntityItem's random initial float height. */
    public float hoverStart;
    private static final String __OBFID = "CL_00001669";

    public EntityItem(World par1World, double par2, double par4, double par6)
    {
        super(par1World);
        health = 5;
        hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        setSize(0.25F, 0.25F);
        yOffset = height / 2.0F;
        setPosition(par2, par4, par6);
        rotationYaw = (float)(Math.random() * 360.0D);
        motionX = ((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        motionY = 0.20000000298023224D;
        motionZ = ((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
    }

    public EntityItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack)
    {
        this(par1World, par2, par4, par6);
        setEntityItemStack(par8ItemStack);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public EntityItem(World par1World)
    {
        super(par1World);
        health = 5;
        hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        setSize(0.25F, 0.25F);
        yOffset = height / 2.0F;
    }

    protected void entityInit()
    {
        getDataWatcher().addObjectByDataType(10, 5);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (getEntityItem() == null)
        {
            setDead();
        }
        else
        {
            super.onUpdate();

            if (delayBeforeCanPickup > 0)
            {
                --delayBeforeCanPickup;
            }

            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            motionY -= 0.03999999910593033D;
            noClip = func_145771_j(posX, (boundingBox.minY + boundingBox.maxY) / 2.0D, posZ);
            moveEntity(motionX, motionY, motionZ);
            boolean var1 = (int)prevPosX != (int)posX || (int)prevPosY != (int)posY || (int)prevPosZ != (int)posZ;

            if (var1 || ticksExisted % 25 == 0)
            {
                if (worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)).getMaterial() == Material.field_151587_i)
                {
                    motionY = 0.20000000298023224D;
                    motionX = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
                    motionZ = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
                    playSound("random.fizz", 0.4F, 2.0F + rand.nextFloat() * 0.4F);
                }

                if (!worldObj.isClient)
                {
                    searchForOtherItemsNearby();
                }
            }

            float var2 = 0.98F;

            if (onGround)
            {
                var2 = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)).slipperiness * 0.98F;
            }

            motionX *= var2;
            motionY *= 0.9800000190734863D;
            motionZ *= var2;

            if (onGround)
            {
                motionY *= -0.5D;
            }

            ++age;

            if (!worldObj.isClient && age >= 6000)
            {
                setDead();
            }
        }
    }

    /**
     * Looks for other itemstacks nearby and tries to stack them together
     */
    private void searchForOtherItemsNearby()
    {
        Iterator var1 = worldObj.getEntitiesWithinAABB(EntityItem.class, boundingBox.expand(0.5D, 0.0D, 0.5D)).iterator();

        while (var1.hasNext())
        {
            EntityItem var2 = (EntityItem)var1.next();
            combineItems(var2);
        }
    }

    /**
     * Tries to merge this item with the item passed as the parameter. Returns
     * true if successful. Either this item or the other item will be removed
     * from the world.
     */
    public boolean combineItems(EntityItem par1EntityItem)
    {
        if (par1EntityItem == this)
        {
            return false;
        }
        else if (par1EntityItem.isEntityAlive() && isEntityAlive())
        {
            ItemStack var2 = getEntityItem();
            ItemStack var3 = par1EntityItem.getEntityItem();

            if (var3.getItem() != var2.getItem())
            {
                return false;
            }
            else if (var3.hasTagCompound() ^ var2.hasTagCompound())
            {
                return false;
            }
            else if (var3.hasTagCompound() && !var3.getTagCompound().equals(var2.getTagCompound()))
            {
                return false;
            }
            else if (var3.getItem() == null)
            {
                return false;
            }
            else if (var3.getItem().getHasSubtypes() && var3.getItemDamage() != var2.getItemDamage())
            {
                return false;
            }
            else if (var3.stackSize < var2.stackSize)
            {
                return par1EntityItem.combineItems(this);
            }
            else if (var3.stackSize + var2.stackSize > var3.getMaxStackSize())
            {
                return false;
            }
            else
            {
                var3.stackSize += var2.stackSize;
                par1EntityItem.delayBeforeCanPickup = Math.max(par1EntityItem.delayBeforeCanPickup, delayBeforeCanPickup);
                par1EntityItem.age = Math.min(par1EntityItem.age, age);
                par1EntityItem.setEntityItemStack(var3);
                setDead();
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * sets the age of the item so that it'll despawn one minute after it has
     * been dropped (instead of five). Used when items are dropped from players
     * in creative mode
     */
    public void setAgeToCreativeDespawnTime()
    {
        age = 4800;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters
     * velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        return worldObj.handleMaterialAcceleration(boundingBox, Material.field_151586_h, this);
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity
     * isn't immune to fire damage. Args: amountDamage
     */
    protected void dealFireDamage(int par1)
    {
        attackEntityFrom(DamageSource.inFire, par1);
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
        else if (getEntityItem() != null && getEntityItem().getItem() == Items.nether_star && par1DamageSource.isExplosion())
        {
            return false;
        }
        else
        {
            setBeenAttacked();
            health = (int)(health - par2);

            if (health <= 0)
            {
                setDead();
            }

            return false;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("Health", ((byte)health));
        par1NBTTagCompound.setShort("Age", (short)age);

        if (getThrower() != null)
        {
            par1NBTTagCompound.setString("Thrower", thrower);
        }

        if (getOwner() != null)
        {
            par1NBTTagCompound.setString("Owner", owner);
        }

        if (getEntityItem() != null)
        {
            par1NBTTagCompound.setTag("Item", getEntityItem().writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        health = par1NBTTagCompound.getShort("Health") & 255;
        age = par1NBTTagCompound.getShort("Age");

        if (par1NBTTagCompound.hasKey("Owner"))
        {
            owner = par1NBTTagCompound.getString("Owner");
        }

        if (par1NBTTagCompound.hasKey("Thrower"))
        {
            thrower = par1NBTTagCompound.getString("Thrower");
        }

        NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("Item");
        setEntityItemStack(ItemStack.loadItemStackFromNBT(var2));

        if (getEntityItem() == null)
        {
            setDead();
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer)
    {
        if (!worldObj.isClient)
        {
            ItemStack var2 = getEntityItem();
            int var3 = var2.stackSize;

            if (delayBeforeCanPickup == 0 && (owner == null || 6000 - age <= 200 || owner.equals(par1EntityPlayer.getUsername())) && par1EntityPlayer.inventory.addItemStackToInventory(var2))
            {
                if (var2.getItem() == Item.getItemFromBlock(Blocks.log))
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                }

                if (var2.getItem() == Item.getItemFromBlock(Blocks.log2))
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                }

                if (var2.getItem() == Items.leather)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.killCow);
                }

                if (var2.getItem() == Items.diamond)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.diamonds);
                }

                if (var2.getItem() == Items.blaze_rod)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.blazeRod);
                }

                if (var2.getItem() == Items.diamond && getThrower() != null)
                {
                    EntityPlayer var4 = worldObj.getPlayerEntityByName(getThrower());

                    if (var4 != null && var4 != par1EntityPlayer)
                    {
                        var4.triggerAchievement(AchievementList.field_150966_x);
                    }
                }

                worldObj.playSoundAtEntity(par1EntityPlayer, "random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, var3);

                if (var2.stackSize <= 0)
                {
                    setDead();
                }
            }
        }
    }

    /**
     * Gets the name of this command sender (usually username, but possibly
     * "Rcon")
     */
    public String getUsername()
    {
        return StatCollector.translateToLocal("item." + getEntityItem().getUnlocalizedName());
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to
     * teleport to
     */
    public void travelToDimension(int par1)
    {
        super.travelToDimension(par1);

        if (!worldObj.isClient)
        {
            searchForOtherItemsNearby();
        }
    }

    /**
     * Returns the ItemStack corresponding to the Entity (Note: if no item
     * exists, will log an error but still return an ItemStack containing
     * Block.stone)
     */
    public ItemStack getEntityItem()
    {
        ItemStack var1 = getDataWatcher().getWatchableObjectItemStack(10);

        if (var1 == null)
        {
            if (worldObj != null)
            {
                logger.error("Item entity " + getEntityId() + " has no item?!");
            }

            return new ItemStack(Blocks.stone);
        }
        else
        {
            return var1;
        }
    }

    /**
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack(ItemStack par1ItemStack)
    {
        getDataWatcher().updateObject(10, par1ItemStack);
        getDataWatcher().setObjectWatched(10);
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String newOwner)
    {
        owner = newOwner;
    }

    public String getThrower()
    {
        return thrower;
    }

    public void setThrower(String newThrower)
    {
        thrower = newThrower;
    }
}
