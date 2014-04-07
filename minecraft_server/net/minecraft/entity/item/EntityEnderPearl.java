package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEnderPearl extends EntityThrowable
{
    private static final String __OBFID = "CL_00001725";

    public EntityEnderPearl(World par1World)
    {
        super(par1World);
    }

    public EntityEnderPearl(World par1World, EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
    {
        if (par1MovingObjectPosition.entityHit != null)
        {
            par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
        }

        for (int var2 = 0; var2 < 32; ++var2)
        {
            worldObj.spawnParticle("portal", posX, posY + rand.nextDouble() * 2.0D, posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian());
        }

        if (!worldObj.isClient)
        {
            if (getThrower() != null && getThrower() instanceof EntityPlayerMP)
            {
                EntityPlayerMP var3 = (EntityPlayerMP)getThrower();

                if (var3.playerNetServerHandler.func_147362_b().isChannelOpen() && var3.worldObj == worldObj)
                {
                    if (getThrower().isRiding())
                    {
                        getThrower().mountEntity((Entity)null);
                    }

                    getThrower().setPositionAndUpdate(posX, posY, posZ);
                    getThrower().fallDistance = 0.0F;
                    getThrower().attackEntityFrom(DamageSource.fall, 5.0F);
                }
            }

            setDead();
        }
    }
}
