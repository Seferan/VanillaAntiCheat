package net.minecraft.tileentity;

import net.minecraft.block.BlockDaylightDetector;

public class TileEntityDaylightDetector extends TileEntity
{
    private static final String __OBFID = "CL_00000350";

    public void updateEntity()
    {
        if (worldObj != null && !worldObj.isClient && worldObj.getTotalWorldTime() % 20L == 0L)
        {
            blockType = getBlockType();

            if (blockType instanceof BlockDaylightDetector)
            {
                ((BlockDaylightDetector)blockType).func_149957_e(worldObj, xCoord, yCoord, zCoord);
            }
        }
    }
}
