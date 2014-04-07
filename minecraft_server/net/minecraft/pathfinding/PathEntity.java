package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class PathEntity
{
    /** The actual points in the path */
    private final PathPoint[] points;

    /** PathEntity Array Index the Entity is currently targeting */
    private int currentPathIndex;

    /** The total length of the path */
    private int pathLength;
    private static final String __OBFID = "CL_00000575";

    public PathEntity(PathPoint[] par1ArrayOfPathPoint)
    {
        points = par1ArrayOfPathPoint;
        pathLength = par1ArrayOfPathPoint.length;
    }

    /**
     * Directs this path to the next point in its array
     */
    public void incrementPathIndex()
    {
        ++currentPathIndex;
    }

    /**
     * Returns true if this path has reached the end
     */
    public boolean isFinished()
    {
        return currentPathIndex >= pathLength;
    }

    /**
     * returns the last PathPoint of the Array
     */
    public PathPoint getFinalPathPoint()
    {
        return pathLength > 0 ? points[pathLength - 1] : null;
    }

    /**
     * return the PathPoint located at the specified PathIndex, usually the
     * current one
     */
    public PathPoint getPathPointFromIndex(int par1)
    {
        return points[par1];
    }

    public int getCurrentPathLength()
    {
        return pathLength;
    }

    public void setCurrentPathLength(int par1)
    {
        pathLength = par1;
    }

    public int getCurrentPathIndex()
    {
        return currentPathIndex;
    }

    public void setCurrentPathIndex(int par1)
    {
        currentPathIndex = par1;
    }

    /**
     * Gets the vector of the PathPoint associated with the given index.
     */
    public Vec3 getVectorFromIndex(Entity par1Entity, int par2)
    {
        double var3 = points[par2].xCoord + ((int)(par1Entity.width + 1.0F)) * 0.5D;
        double var5 = points[par2].yCoord;
        double var7 = points[par2].zCoord + ((int)(par1Entity.width + 1.0F)) * 0.5D;
        return par1Entity.worldObj.getWorldVec3Pool().getVecFromPool(var3, var5, var7);
    }

    /**
     * returns the current PathEntity target node as Vec3D
     */
    public Vec3 getPosition(Entity par1Entity)
    {
        return getVectorFromIndex(par1Entity, currentPathIndex);
    }

    /**
     * Returns true if the EntityPath are the same. Non instance related equals.
     */
    public boolean isSamePath(PathEntity par1PathEntity)
    {
        if (par1PathEntity == null)
        {
            return false;
        }
        else if (par1PathEntity.points.length != points.length)
        {
            return false;
        }
        else
        {
            for (int var2 = 0; var2 < points.length; ++var2)
            {
                if (points[var2].xCoord != par1PathEntity.points[var2].xCoord || points[var2].yCoord != par1PathEntity.points[var2].yCoord || points[var2].zCoord != par1PathEntity.points[var2].zCoord) { return false; }
            }

            return true;
        }
    }

    /**
     * Returns true if the final PathPoint in the PathEntity is equal to Vec3D
     * coords.
     */
    public boolean isDestinationSame(Vec3 par1Vec3)
    {
        PathPoint var2 = getFinalPathPoint();
        return var2 == null ? false : var2.xCoord == (int)par1Vec3.xCoord && var2.zCoord == (int)par1Vec3.zCoord;
    }
}
