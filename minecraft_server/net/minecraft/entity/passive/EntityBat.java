package net.minecraft.entity.passive;

import java.util.Calendar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBat extends EntityAmbientCreature
{
    /** Coordinates of where the bat spawned. */
    private ChunkCoordinates spawnPosition;
    private static final String __OBFID = "CL_00001637";

    public EntityBat(World par1World)
    {
        super(par1World);
        setSize(0.5F, 0.9F);
        setIsBatHanging(true);
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.1F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch()
    {
        return super.getSoundPitch() * 0.95F;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return getIsBatHanging() && rand.nextInt(4) != 0 ? null : "mob.bat.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.bat.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.bat.death";
    }

    /**
     * Returns true if this entity should push and be pushed by other entities
     * when colliding.
     */
    public boolean canBePushed()
    {
        return false;
    }

    protected void collideWithEntity(Entity par1Entity)
    {
    }

    protected void collideWithNearbyEntities()
    {
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }

    public boolean getIsBatHanging()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setIsBatHanging(boolean par1)
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
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return true;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (getIsBatHanging())
        {
            motionX = motionY = motionZ = 0.0D;
            posY = MathHelper.floor_double(posY) + 1.0D - height;
        }
        else
        {
            motionY *= 0.6000000238418579D;
        }
    }

    protected void updateAITasks()
    {
        super.updateAITasks();

        if (getIsBatHanging())
        {
            if (!worldObj.getBlock(MathHelper.floor_double(posX), (int)posY + 1, MathHelper.floor_double(posZ)).isNormalCube())
            {
                setIsBatHanging(false);
                worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, (int)posX, (int)posY, (int)posZ, 0);
            }
            else
            {
                if (rand.nextInt(200) == 0)
                {
                    rotationYawHead = rand.nextInt(360);
                }

                if (worldObj.getClosestPlayerToEntity(this, 4.0D) != null)
                {
                    setIsBatHanging(false);
                    worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, (int)posX, (int)posY, (int)posZ, 0);
                }
            }
        }
        else
        {
            if (spawnPosition != null && (!worldObj.isAirBlock(spawnPosition.posX, spawnPosition.posY, spawnPosition.posZ) || spawnPosition.posY < 1))
            {
                spawnPosition = null;
            }

            if (spawnPosition == null || rand.nextInt(30) == 0 || spawnPosition.getDistanceSquared((int)posX, (int)posY, (int)posZ) < 4.0F)
            {
                spawnPosition = new ChunkCoordinates((int)posX + rand.nextInt(7) - rand.nextInt(7), (int)posY + rand.nextInt(6) - 2, (int)posZ + rand.nextInt(7) - rand.nextInt(7));
            }

            double var1 = spawnPosition.posX + 0.5D - posX;
            double var3 = spawnPosition.posY + 0.1D - posY;
            double var5 = spawnPosition.posZ + 0.5D - posZ;
            motionX += (Math.signum(var1) * 0.5D - motionX) * 0.10000000149011612D;
            motionY += (Math.signum(var3) * 0.699999988079071D - motionY) * 0.10000000149011612D;
            motionZ += (Math.signum(var5) * 0.5D - motionZ) * 0.10000000149011612D;
            float var7 = (float)(Math.atan2(motionZ, motionX) * 180.0D / Math.PI) - 90.0F;
            float var8 = MathHelper.wrapAngleTo180_float(var7 - rotationYaw);
            moveForward = 0.5F;
            rotationYaw += var8;

            if (rand.nextInt(100) == 0 && worldObj.getBlock(MathHelper.floor_double(posX), (int)posY + 1, MathHelper.floor_double(posZ)).isNormalCube())
            {
                setIsBatHanging(true);
            }
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on
     * the ground to update the fall distance and deal fall damage if landing on
     * the ground. Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
    }

    public boolean func_145773_az()
    {
        return true;
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
        else
        {
            if (!worldObj.isClient && getIsBatHanging())
            {
                setIsBatHanging(false);
            }

            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        dataWatcher.updateObject(16, Byte.valueOf(par1NBTTagCompound.getByte("BatFlags")));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByte("BatFlags", dataWatcher.getWatchableObjectByte(16));
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this
     * entity.
     */
    public boolean getCanSpawnHere()
    {
        int var1 = MathHelper.floor_double(boundingBox.minY);

        if (var1 >= 63)
        {
            return false;
        }
        else
        {
            int var2 = MathHelper.floor_double(posX);
            int var3 = MathHelper.floor_double(posZ);
            int var4 = worldObj.getBlockLightValue(var2, var1, var3);
            byte var5 = 4;
            Calendar var6 = worldObj.getCurrentDate();

            if ((var6.get(2) + 1 != 10 || var6.get(5) < 20) && (var6.get(2) + 1 != 11 || var6.get(5) > 3))
            {
                if (rand.nextBoolean()) { return false; }
            }
            else
            {
                var5 = 7;
            }

            return var4 > rand.nextInt(var5) ? false : super.getCanSpawnHere();
        }
    }
}
