package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCreeper extends EntityMob
{
    /**
     * Time when this creeper was last in an active state (Messed up code here,
     * probably causes creeper animation to go weird)
     */
    private int lastActiveTime;

    /**
     * The amount of time since the creeper was close enough to the player to
     * ignite
     */
    private int timeSinceIgnited;
    private int fuseTime = 30;

    /** Explosion radius for this creeper. */
    private int explosionRadius = 3;
    private static final String __OBFID = "CL_00001684";

    public EntityCreeper(World par1World)
    {
        super(par1World);
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAICreeperSwell(this));
        tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, false));
        tasks.addTask(5, new EntityAIWander(this, 0.8D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(6, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    /**
     * The number of iterations PathFinder.getSafePoint will execute before
     * giving up.
     */
    public int getMaxSafePointTries()
    {
        return getAttackTarget() == null ? 3 : 3 + (int)(getHealth() - 1.0F);
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        super.fall(par1);
        timeSinceIgnited = (int)(timeSinceIgnited + par1 * 1.5F);

        if (timeSinceIgnited > fuseTime - 5)
        {
            timeSinceIgnited = fuseTime - 5;
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte)-1));
        dataWatcher.addObject(17, Byte.valueOf((byte)0));
        dataWatcher.addObject(18, Byte.valueOf((byte)0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);

        if (dataWatcher.getWatchableObjectByte(17) == 1)
        {
            par1NBTTagCompound.setBoolean("powered", true);
        }

        par1NBTTagCompound.setShort("Fuse", (short)fuseTime);
        par1NBTTagCompound.setByte("ExplosionRadius", (byte)explosionRadius);
        par1NBTTagCompound.setBoolean("ignited", func_146078_ca());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        dataWatcher.updateObject(17, Byte.valueOf((byte)(par1NBTTagCompound.getBoolean("powered") ? 1 : 0)));

        if (par1NBTTagCompound.func_150297_b("Fuse", 99))
        {
            fuseTime = par1NBTTagCompound.getShort("Fuse");
        }

        if (par1NBTTagCompound.func_150297_b("ExplosionRadius", 99))
        {
            explosionRadius = par1NBTTagCompound.getByte("ExplosionRadius");
        }

        if (par1NBTTagCompound.getBoolean("ignited"))
        {
            func_146079_cb();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (isEntityAlive())
        {
            lastActiveTime = timeSinceIgnited;

            if (func_146078_ca())
            {
                setCreeperState(1);
            }

            int var1 = getCreeperState();

            if (var1 > 0 && timeSinceIgnited == 0)
            {
                playSound("creeper.primed", 1.0F, 0.5F);
            }

            timeSinceIgnited += var1;

            if (timeSinceIgnited < 0)
            {
                timeSinceIgnited = 0;
            }

            if (timeSinceIgnited >= fuseTime)
            {
                timeSinceIgnited = fuseTime;
                func_146077_cc();
            }
        }

        super.onUpdate();
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.creeper.say";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.creeper.death";
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);

        if (par1DamageSource.getEntity() instanceof EntitySkeleton)
        {
            int var2 = Item.getIdFromItem(Items.record_13);
            int var3 = Item.getIdFromItem(Items.record_wait);
            int var4 = var2 + rand.nextInt(var3 - var2 + 1);
            func_145779_a(Item.getItemById(var4), 1);
        }
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        return true;
    }

    /**
     * Returns true if the creeper is powered by a lightning bolt.
     */
    public boolean getPowered()
    {
        return dataWatcher.getWatchableObjectByte(17) == 1;
    }

    protected Item func_146068_u()
    {
        return Items.gunpowder;
    }

    /**
     * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
     */
    public int getCreeperState()
    {
        return dataWatcher.getWatchableObjectByte(16);
    }

    /**
     * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
     */
    public void setCreeperState(int par1)
    {
        dataWatcher.updateObject(16, Byte.valueOf((byte)par1));
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt)
    {
        super.onStruckByLightning(par1EntityLightningBolt);
        dataWatcher.updateObject(17, Byte.valueOf((byte)1));
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    protected boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.getItem() == Items.flint_and_steel)
        {
            worldObj.playSoundEffect(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
            par1EntityPlayer.swingItem();

            if (!worldObj.isClient)
            {
                func_146079_cb();
                var2.damageItem(1, par1EntityPlayer);
                return true;
            }
        }

        return super.interact(par1EntityPlayer);
    }

    private void func_146077_cc()
    {
        if (!worldObj.isClient)
        {
            boolean var1 = worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

            if (getPowered())
            {
                worldObj.createExplosion(this, posX, posY, posZ, explosionRadius * 2, var1);
            }
            else
            {
                worldObj.createExplosion(this, posX, posY, posZ, explosionRadius, var1);
            }

            setDead();
        }
    }

    public boolean func_146078_ca()
    {
        return dataWatcher.getWatchableObjectByte(18) != 0;
    }

    public void func_146079_cb()
    {
        dataWatcher.updateObject(18, Byte.valueOf((byte)1));
    }
}
