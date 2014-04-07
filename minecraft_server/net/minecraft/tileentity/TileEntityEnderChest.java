package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

public class TileEntityEnderChest extends TileEntity
{
    public float field_145972_a;
    public float field_145975_i;
    public int field_145973_j;
    private int field_145974_k;
    private static final String __OBFID = "CL_00000355";

    public void updateEntity()
    {
        super.updateEntity();

        if (++field_145974_k % 20 * 4 == 0)
        {
            worldObj.func_147452_c(xCoord, yCoord, zCoord, Blocks.ender_chest, 1, field_145973_j);
        }

        field_145975_i = field_145972_a;
        float var1 = 0.1F;
        double var4;

        if (field_145973_j > 0 && field_145972_a == 0.0F)
        {
            double var2 = xCoord + 0.5D;
            var4 = zCoord + 0.5D;
            worldObj.playSoundEffect(var2, yCoord + 0.5D, var4, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (field_145973_j == 0 && field_145972_a > 0.0F || field_145973_j > 0 && field_145972_a < 1.0F)
        {
            float var8 = field_145972_a;

            if (field_145973_j > 0)
            {
                field_145972_a += var1;
            }
            else
            {
                field_145972_a -= var1;
            }

            if (field_145972_a > 1.0F)
            {
                field_145972_a = 1.0F;
            }

            float var3 = 0.5F;

            if (field_145972_a < var3 && var8 >= var3)
            {
                var4 = xCoord + 0.5D;
                double var6 = zCoord + 0.5D;
                worldObj.playSoundEffect(var4, yCoord + 0.5D, var6, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (field_145972_a < 0.0F)
            {
                field_145972_a = 0.0F;
            }
        }
    }

    public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_)
    {
        if (p_145842_1_ == 1)
        {
            field_145973_j = p_145842_2_;
            return true;
        }
        else
        {
            return super.receiveClientEvent(p_145842_1_, p_145842_2_);
        }
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        updateContainingBlockInfo();
        super.invalidate();
    }

    public void func_145969_a()
    {
        ++field_145973_j;
        worldObj.func_147452_c(xCoord, yCoord, zCoord, Blocks.ender_chest, 1, field_145973_j);
    }

    public void func_145970_b()
    {
        --field_145973_j;
        worldObj.func_147452_c(xCoord, yCoord, zCoord, Blocks.ender_chest, 1, field_145973_j);
    }

    public boolean func_145971_a(EntityPlayer p_145971_1_)
    {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : p_145971_1_.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }
}
