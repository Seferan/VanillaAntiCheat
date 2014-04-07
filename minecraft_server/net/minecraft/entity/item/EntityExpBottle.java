package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable
{
    private static final String __OBFID = "CL_00001726";

    public EntityExpBottle(World par1World)
    {
        super(par1World);
    }

    public EntityExpBottle(World par1World, EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
    }

    public EntityExpBottle(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity()
    {
        return 0.07F;
    }

    protected float func_70182_d()
    {
        return 0.7F;
    }

    protected float func_70183_g()
    {
        return -20.0F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
    {
        if (!worldObj.isClient)
        {
            worldObj.playAuxSFX(2002, (int)Math.round(posX), (int)Math.round(posY), (int)Math.round(posZ), 0);
            int var2 = 3 + worldObj.rand.nextInt(5) + worldObj.rand.nextInt(5);

            while (var2 > 0)
            {
                int var3 = EntityXPOrb.getXPSplit(var2);
                var2 -= var3;
                worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj, posX, posY, posZ, var3));
            }

            setDead();
        }
    }
}
