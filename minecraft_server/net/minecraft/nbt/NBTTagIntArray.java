package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase
{
    /** The array of saved integers */
    private int[] intArray;
    private static final String __OBFID = "CL_00001221";

    NBTTagIntArray()
    {
    }

    public NBTTagIntArray(int[] p_i45132_1_)
    {
        intArray = p_i45132_1_;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(intArray.length);

        for (int var2 = 0; var2 < intArray.length; ++var2)
        {
            par1DataOutput.writeInt(intArray[var2]);
        }
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        int var3 = par1DataInput.readInt();
        intArray = new int[var3];

        for (int var4 = 0; var4 < var3; ++var4)
        {
            intArray[var4] = par1DataInput.readInt();
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)11;
    }

    public String toString()
    {
        String var1 = "[";
        int[] var2 = intArray;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            int var5 = var2[var4];
            var1 = var1 + var5 + ",";
        }

        return var1 + "]";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        int[] var1 = new int[intArray.length];
        System.arraycopy(intArray, 0, var1, 0, intArray.length);
        return new NBTTagIntArray(var1);
    }

    public boolean equals(Object par1Obj)
    {
        return super.equals(par1Obj) ? Arrays.equals(intArray, ((NBTTagIntArray)par1Obj).intArray) : false;
    }

    public int hashCode()
    {
        return super.hashCode() ^ Arrays.hashCode(intArray);
    }

    public int[] func_150302_c()
    {
        return intArray;
    }
}
