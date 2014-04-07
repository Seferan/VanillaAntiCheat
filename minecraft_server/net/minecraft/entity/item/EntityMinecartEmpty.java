package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart
{
    private static final String __OBFID = "CL_00001677";

    public EntityMinecartEmpty(World par1World)
    {
        super(par1World);
    }

    public EntityMinecartEmpty(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer && riddenByEntity != par1EntityPlayer)
        {
            return true;
        }
        else if (riddenByEntity != null && riddenByEntity != par1EntityPlayer)
        {
            return false;
        }
        else
        {
            if (!worldObj.isClient)
            {
                par1EntityPlayer.mountEntity(this);
            }

            return true;
        }
    }

    public int getMinecartType()
    {
        return 0;
    }
}
