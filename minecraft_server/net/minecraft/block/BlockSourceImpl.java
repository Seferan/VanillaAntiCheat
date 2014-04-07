package net.minecraft.block;

import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSourceImpl implements IBlockSource
{
    private final World worldObj;
    private final int xPos;
    private final int yPos;
    private final int zPos;
    private static final String __OBFID = "CL_00001194";

    public BlockSourceImpl(World par1World, int par2, int par3, int par4)
    {
        worldObj = par1World;
        xPos = par2;
        yPos = par3;
        zPos = par4;
    }

    public World getWorld()
    {
        return worldObj;
    }

    public double getX()
    {
        return xPos + 0.5D;
    }

    public double getY()
    {
        return yPos + 0.5D;
    }

    public double getZ()
    {
        return zPos + 0.5D;
    }

    public int getXInt()
    {
        return xPos;
    }

    public int getYInt()
    {
        return yPos;
    }

    public int getZInt()
    {
        return zPos;
    }

    public int getBlockMetadata()
    {
        return worldObj.getBlockMetadata(xPos, yPos, zPos);
    }

    public TileEntity getBlockTileEntity()
    {
        return worldObj.getTileEntity(xPos, yPos, zPos);
    }
}
