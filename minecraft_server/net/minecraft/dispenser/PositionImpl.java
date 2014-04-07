package net.minecraft.dispenser;

public class PositionImpl implements IPosition
{
    protected final double x;
    protected final double y;
    protected final double z;
    private static final String __OBFID = "CL_00001208";

    public PositionImpl(double par1, double par3, double par5)
    {
        x = par1;
        y = par3;
        z = par5;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }
}
