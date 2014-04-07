package net.minecraft.util;

import java.util.ArrayList;
import java.util.List;

public class AABBPool
{
    /**
     * Maximum number of times the pool can be "cleaned" before the list is
     * shrunk
     */
    private final int maxNumCleans;

    /**
     * Number of Pool entries to remove when cleanPool is called maxNumCleans
     * times.
     */
    private final int numEntriesToRemove;

    /** List of AABB stored in this Pool */
    private final List listAABB = new ArrayList();

    /** Next index to use when adding a Pool Entry. */
    private int nextPoolIndex;

    /**
     * Largest index reached by this Pool (can be reset to 0 upon calling
     * cleanPool)
     */
    private int maxPoolIndex;

    /** Number of times this Pool has been cleaned */
    private int numCleans;
    private static final String __OBFID = "CL_00000609";

    public AABBPool(int par1, int par2)
    {
        maxNumCleans = par1;
        numEntriesToRemove = par2;
    }

    /**
     * Creates a new AABB, or reuses one that's no longer in use. Parameters:
     * minX, minY, minZ, maxX, maxY, maxZ. AABBs returned from this function
     * should only be used for one frame or tick, as after that they will be
     * reused.
     */
    public AxisAlignedBB getAABB(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        AxisAlignedBB var13;

        if (nextPoolIndex >= listAABB.size())
        {
            var13 = new AxisAlignedBB(par1, par3, par5, par7, par9, par11);
            listAABB.add(var13);
        }
        else
        {
            var13 = (AxisAlignedBB)listAABB.get(nextPoolIndex);
            var13.setBounds(par1, par3, par5, par7, par9, par11);
        }

        ++nextPoolIndex;
        return var13;
    }

    /**
     * Marks the pool as "empty", starting over when adding new entries. If this
     * is called maxNumCleans times, the list size is reduced
     */
    public void cleanPool()
    {
        if (nextPoolIndex > maxPoolIndex)
        {
            maxPoolIndex = nextPoolIndex;
        }

        if (numCleans++ == maxNumCleans)
        {
            int var1 = Math.max(maxPoolIndex, listAABB.size() - numEntriesToRemove);

            while (listAABB.size() > var1)
            {
                listAABB.remove(var1);
            }

            maxPoolIndex = 0;
            numCleans = 0;
        }

        nextPoolIndex = 0;
    }

    public int getlistAABBsize()
    {
        return listAABB.size();
    }

    public int getnextPoolIndex()
    {
        return nextPoolIndex;
    }
}
