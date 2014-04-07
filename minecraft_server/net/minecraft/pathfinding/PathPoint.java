package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint
{
    /** The x coordinate of this point */
    public final int xCoord;

    /** The y coordinate of this point */
    public final int yCoord;

    /** The z coordinate of this point */
    public final int zCoord;

    /** A hash of the coordinates used to identify this point */
    private final int hash;

    /** The index of this point in its assigned path */
    int index = -1;

    /** The distance along the path to this point */
    float totalPathDistance;

    /** The linear distance to the next point */
    float distanceToNext;

    /** The distance to the target */
    float distanceToTarget;

    /** The point preceding this in its assigned path */
    PathPoint previous;

    /** Indicates this is the origin */
    public boolean isFirst;
    private static final String __OBFID = "CL_00000574";

    public PathPoint(int par1, int par2, int par3)
    {
        xCoord = par1;
        yCoord = par2;
        zCoord = par3;
        hash = makeHash(par1, par2, par3);
    }

    public static int makeHash(int par0, int par1, int par2)
    {
        return par1 & 255 | (par0 & 32767) << 8 | (par2 & 32767) << 24 | (par0 < 0 ? Integer.MIN_VALUE : 0) | (par2 < 0 ? 32768 : 0);
    }

    /**
     * Returns the linear distance to another path point
     */
    public float distanceTo(PathPoint par1PathPoint)
    {
        float var2 = par1PathPoint.xCoord - xCoord;
        float var3 = par1PathPoint.yCoord - yCoord;
        float var4 = par1PathPoint.zCoord - zCoord;
        return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
    }

    public float func_75832_b(PathPoint par1PathPoint)
    {
        float var2 = par1PathPoint.xCoord - xCoord;
        float var3 = par1PathPoint.yCoord - yCoord;
        float var4 = par1PathPoint.zCoord - zCoord;
        return var2 * var2 + var3 * var3 + var4 * var4;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof PathPoint))
        {
            return false;
        }
        else
        {
            PathPoint var2 = (PathPoint)par1Obj;
            return hash == var2.hash && xCoord == var2.xCoord && yCoord == var2.yCoord && zCoord == var2.zCoord;
        }
    }

    public int hashCode()
    {
        return hash;
    }

    /**
     * Returns true if this point has already been assigned to a path
     */
    public boolean isAssigned()
    {
        return index >= 0;
    }

    public String toString()
    {
        return xCoord + ", " + yCoord + ", " + zCoord;
    }
}
