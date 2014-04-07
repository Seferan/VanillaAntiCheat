package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityIronGolem extends EntityGolem
{
    /** deincrements, and a distance-to-home check is done at 0 */
    private int homeCheckTimer;
    Village villageObj;
    private int attackTimer;
    private int holdRoseTick;
    private static final String __OBFID = "CL_00001652";

    public EntityIronGolem(World par1World)
    {
        super(par1World);
        setSize(1.4F, 2.9F);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0D, true));
        tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
        tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.6D, true));
        tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        tasks.addTask(5, new EntityAILookAtVillager(this));
        tasks.addTask(6, new EntityAIWander(this, 0.6D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIDefendVillage(this));
        targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, IMob.mobSelector));
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick()
    {
        if (--homeCheckTimer <= 0)
        {
            homeCheckTimer = 70 + rand.nextInt(50);
            villageObj = worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ), 32);

            if (villageObj == null)
            {
                detachHome();
            }
            else
            {
                ChunkCoordinates var1 = villageObj.getCenter();
                setHomeArea(var1.posX, var1.posY, var1.posZ, (int)(villageObj.getVillageRadius() * 0.6F));
            }
        }

        super.updateAITick();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int par1)
    {
        return par1;
    }

    protected void collideWithEntity(Entity par1Entity)
    {
        if (par1Entity instanceof IMob && getRNG().nextInt(20) == 0)
        {
            setAttackTarget((EntityLivingBase)par1Entity);
        }

        super.collideWithEntity(par1Entity);
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (attackTimer > 0)
        {
            --attackTimer;
        }

        if (holdRoseTick > 0)
        {
            --holdRoseTick;
        }

        if (motionX * motionX + motionZ * motionZ > 2.500000277905201E-7D && rand.nextInt(5) == 0)
        {
            int var1 = MathHelper.floor_double(posX);
            int var2 = MathHelper.floor_double(posY - 0.20000000298023224D - yOffset);
            int var3 = MathHelper.floor_double(posZ);
            Block var4 = worldObj.getBlock(var1, var2, var3);

            if (var4.getMaterial() != Material.air)
            {
                worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(var4) + "_" + worldObj.getBlockMetadata(var1, var2, var3), posX + (rand.nextFloat() - 0.5D) * width, boundingBox.minY + 0.1D, posZ + (rand.nextFloat() - 0.5D) * width, 4.0D * (rand.nextFloat() - 0.5D), 0.5D, (rand.nextFloat() - 0.5D) * 4.0D);
            }
        }
    }

    /**
     * Returns true if this entity can attack entities of the specified class.
     */
    public boolean canAttackClass(Class par1Class)
    {
        return isPlayerCreated() && EntityPlayer.class.isAssignableFrom(par1Class) ? false : super.canAttackClass(par1Class);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("PlayerCreated", isPlayerCreated());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        setPlayerCreated(par1NBTTagCompound.getBoolean("PlayerCreated"));
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        attackTimer = 10;
        worldObj.setEntityState(this, (byte)4);
        boolean var2 = par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + rand.nextInt(15));

        if (var2)
        {
            par1Entity.motionY += 0.4000000059604645D;
        }

        playSound("mob.irongolem.throw", 1.0F, 1.0F);
        return var2;
    }

    public Village getVillage()
    {
        return villageObj;
    }

    public void setHoldingRose(boolean par1)
    {
        holdRoseTick = par1 ? 400 : 0;
        worldObj.setEntityState(this, (byte)11);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.irongolem.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.irongolem.death";
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        playSound("mob.irongolem.walk", 1.0F, 1.0F);
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = rand.nextInt(3);
        int var4;

        for (var4 = 0; var4 < var3; ++var4)
        {
            func_145778_a(Item.getItemFromBlock(Blocks.red_flower), 1, 0.0F);
        }

        var4 = 3 + rand.nextInt(3);

        for (int var5 = 0; var5 < var4; ++var5)
        {
            func_145779_a(Items.iron_ingot, 1);
        }
    }

    public int getHoldRoseTick()
    {
        return holdRoseTick;
    }

    public boolean isPlayerCreated()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setPlayerCreated(boolean par1)
    {
        byte var2 = dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 1)));
        }
        else
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -2)));
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        if (!isPlayerCreated() && attackingPlayer != null && villageObj != null)
        {
            villageObj.setReputationForPlayer(attackingPlayer.getUsername(), -5);
        }

        super.onDeath(par1DamageSource);
    }
}
