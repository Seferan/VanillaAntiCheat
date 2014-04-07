package net.minecraft.world;

import net.minecraft.block.Block;

public class NextTickListEntry implements Comparable
{
    /** The id number for the next tick entry */
    private static long nextTickEntryID;
    private final Block field_151352_g;

    /** X position this tick is occuring at */
    public int xCoord;

    /** Y position this tick is occuring at */
    public int yCoord;

    /** Z position this tick is occuring at */
    public int zCoord;

    /** Time this tick is scheduled to occur at */
    public long scheduledTime;
    public int priority;

    /** The id of the tick entry */
    private long tickEntryID;
    private static final String __OBFID = "CL_00000156";

    public NextTickListEntry(int p_i45370_1_, int p_i45370_2_, int p_i45370_3_, Block p_i45370_4_)
    {
        tickEntryID = (nextTickEntryID++);
        xCoord = p_i45370_1_;
        yCoord = p_i45370_2_;
        zCoord = p_i45370_3_;
        field_151352_g = p_i45370_4_;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof NextTickListEntry))
        {
            return false;
        }
        else
        {
            NextTickListEntry var2 = (NextTickListEntry)par1Obj;
            return xCoord == var2.xCoord && yCoord == var2.yCoord && zCoord == var2.zCoord && Block.isEqualTo(field_151352_g, var2.field_151352_g);
        }
    }

    public int hashCode()
    {
        return (xCoord * 1024 * 1024 + zCoord * 1024 + yCoord) * 256;
    }

    /**
     * Sets the scheduled time for this tick entry
     */
    public NextTickListEntry setScheduledTime(long par1)
    {
        scheduledTime = par1;
        return this;
    }

    public void setPriority(int par1)
    {
        priority = par1;
    }

    public int compareTo(NextTickListEntry par1NextTickListEntry)
    {
        return scheduledTime < par1NextTickListEntry.scheduledTime ? -1 : (scheduledTime > par1NextTickListEntry.scheduledTime ? 1 : (priority != par1NextTickListEntry.priority ? priority - par1NextTickListEntry.priority : (tickEntryID < par1NextTickListEntry.tickEntryID ? -1 : (tickEntryID > par1NextTickListEntry.tickEntryID ? 1 : 0))));
    }

    public String toString()
    {
        return Block.getIdFromBlock(field_151352_g) + ": (" + xCoord + ", " + yCoord + ", " + zCoord + "), " + scheduledTime + ", " + priority + ", " + tickEntryID;
    }

    public Block func_151351_a()
    {
        return field_151352_g;
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareTo((NextTickListEntry)par1Obj);
    }
}
