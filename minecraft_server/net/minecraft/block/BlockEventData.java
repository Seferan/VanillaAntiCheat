package net.minecraft.block;

public class BlockEventData
{
    private int coordX;
    private int coordY;
    private int coordZ;
    private Block field_151344_d;

    /** Different for each blockID */
    private int eventID;
    private int eventParameter;
    private static final String __OBFID = "CL_00000131";

    public BlockEventData(int p_i45362_1_, int p_i45362_2_, int p_i45362_3_, Block p_i45362_4_, int p_i45362_5_, int p_i45362_6_)
    {
        coordX = p_i45362_1_;
        coordY = p_i45362_2_;
        coordZ = p_i45362_3_;
        eventID = p_i45362_5_;
        eventParameter = p_i45362_6_;
        field_151344_d = p_i45362_4_;
    }

    public int func_151340_a()
    {
        return coordX;
    }

    public int func_151342_b()
    {
        return coordY;
    }

    public int func_151341_c()
    {
        return coordZ;
    }

    /**
     * Get the Event ID (different for each BlockID)
     */
    public int getEventID()
    {
        return eventID;
    }

    public int getEventParameter()
    {
        return eventParameter;
    }

    public Block getBlock()
    {
        return field_151344_d;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof BlockEventData))
        {
            return false;
        }
        else
        {
            BlockEventData var2 = (BlockEventData)par1Obj;
            return coordX == var2.coordX && coordY == var2.coordY && coordZ == var2.coordZ && eventID == var2.eventID && eventParameter == var2.eventParameter && field_151344_d == var2.field_151344_d;
        }
    }

    public String toString()
    {
        return "TE(" + coordX + "," + coordY + "," + coordZ + ")," + eventID + "," + eventParameter + "," + field_151344_d;
    }
}
