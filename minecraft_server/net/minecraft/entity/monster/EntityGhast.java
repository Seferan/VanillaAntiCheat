package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob
{
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private Entity targetedEntity;

    /** Cooldown time between target loss and new target aquirement. */
    private int aggroCooldown;
    public int prevAttackCounter;
    public int attackCounter;

    /** The explosion radius of spawned fireballs. */
    private int explosionStrength = 1;
    private static final String __OBFID = "CL_00001689";

    public EntityGhast(World par1World)
    {
        super(par1World);
        setSize(4.0F, 4.0F);
        isImmuneToFire = true;
        experienceValue = 5;
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
        else if ("fireball".equals(par1DamageSource.getDamageType()) && par1DamageSource.getEntity() instanceof EntityPlayer)
        {
            super.attackEntityFrom(par1DamageSource, 1000.0F);
            ((EntityPlayer)par1DamageSource.getEntity()).triggerAchievement(AchievementList.ghast);
            return true;
        }
        else
        {
            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }

    protected void updateEntityActionState()
    {
        if (!worldObj.isClient && worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
        {
            setDead();
        }

        despawnEntity();
        prevAttackCounter = attackCounter;
        double var1 = waypointX - posX;
        double var3 = waypointY - posY;
        double var5 = waypointZ - posZ;
        double var7 = var1 * var1 + var3 * var3 + var5 * var5;

        if (var7 < 1.0D || var7 > 3600.0D)
        {
            waypointX = posX + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
            waypointY = posY + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
            waypointZ = posZ + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
        }

        if (courseChangeCooldown-- <= 0)
        {
            courseChangeCooldown += rand.nextInt(5) + 2;
            var7 = MathHelper.sqrt_double(var7);

            if (isCourseTraversable(waypointX, waypointY, waypointZ, var7))
            {
                motionX += var1 / var7 * 0.1D;
                motionY += var3 / var7 * 0.1D;
                motionZ += var5 / var7 * 0.1D;
            }
            else
            {
                waypointX = posX;
                waypointY = posY;
                waypointZ = posZ;
            }
        }

        if (targetedEntity != null && targetedEntity.isDead)
        {
            targetedEntity = null;
        }

        if (targetedEntity == null || aggroCooldown-- <= 0)
        {
            targetedEntity = worldObj.getClosestVulnerablePlayerToEntity(this, 100.0D);

            if (targetedEntity != null)
            {
                aggroCooldown = 20;
            }
        }

        double var9 = 64.0D;

        if (targetedEntity != null && targetedEntity.getDistanceSqToEntity(this) < var9 * var9)
        {
            double var11 = targetedEntity.posX - posX;
            double var13 = targetedEntity.boundingBox.minY + targetedEntity.height / 2.0F - (posY + height / 2.0F);
            double var15 = targetedEntity.posZ - posZ;
            renderYawOffset = rotationYaw = -((float)Math.atan2(var11, var15)) * 180.0F / (float)Math.PI;

            if (canEntityBeSeen(targetedEntity))
            {
                if (attackCounter == 10)
                {
                    worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1007, (int)posX, (int)posY, (int)posZ, 0);
                }

                ++attackCounter;

                if (attackCounter == 20)
                {
                    worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1008, (int)posX, (int)posY, (int)posZ, 0);
                    EntityLargeFireball var17 = new EntityLargeFireball(worldObj, this, var11, var13, var15);
                    var17.field_92057_e = explosionStrength;
                    double var18 = 4.0D;
                    Vec3 var20 = getLook(1.0F);
                    var17.posX = posX + var20.xCoord * var18;
                    var17.posY = posY + height / 2.0F + 0.5D;
                    var17.posZ = posZ + var20.zCoord * var18;
                    worldObj.spawnEntityInWorld(var17);
                    attackCounter = -40;
                }
            }
            else if (attackCounter > 0)
            {
                --attackCounter;
            }
        }
        else
        {
            renderYawOffset = rotationYaw = -((float)Math.atan2(motionX, motionZ)) * 180.0F / (float)Math.PI;

            if (attackCounter > 0)
            {
                --attackCounter;
            }
        }

        if (!worldObj.isClient)
        {
            byte var21 = dataWatcher.getWatchableObjectByte(16);
            byte var12 = (byte)(attackCounter > 10 ? 1 : 0);

            if (var21 != var12)
            {
                dataWatcher.updateObject(16, Byte.valueOf(var12));
            }
        }
    }

    /**
     * True if the ghast has an unobstructed line of travel to the waypoint.
     */
    private boolean isCourseTraversable(double par1, double par3, double par5, double par7)
    {
        double var9 = (waypointX - posX) / par7;
        double var11 = (waypointY - posY) / par7;
        double var13 = (waypointZ - posZ) / par7;
        AxisAlignedBB var15 = boundingBox.copy();

        for (int var16 = 1; var16 < par7; ++var16)
        {
            var15.offset(var9, var11, var13);

            if (!worldObj.getCollidingBoundingBoxes(this, var15).isEmpty()) { return false; }
        }

        return true;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.ghast.moan";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.ghast.scream";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.ghast.death";
    }

    protected Item func_146068_u()
    {
        return Items.gunpowder;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = rand.nextInt(2) + rand.nextInt(1 + par2);
        int var4;

        for (var4 = 0; var4 < var3; ++var4)
        {
            func_145779_a(Items.ghast_tear, 1);
        }

        var3 = rand.nextInt(3) + rand.nextInt(1 + par2);

        for (var4 = 0; var4 < var3; ++var4)
        {
            func_145779_a(Items.gunpowder, 1);
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 10.0F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this
     * entity.
     */
    public boolean getCanSpawnHere()
    {
        return rand.nextInt(20) == 0 && super.getCanSpawnHere() && worldObj.difficultySetting != EnumDifficulty.PEACEFUL;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("ExplosionPower", explosionStrength);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.func_150297_b("ExplosionPower", 99))
        {
            explosionStrength = par1NBTTagCompound.getInteger("ExplosionPower");
        }
    }
}
