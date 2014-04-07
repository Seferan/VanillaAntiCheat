package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase
{
    /** The byte array stored in the tag. */
    private byte[] byteArray;
    private static final String __OBFID = "CL_00001213";

    NBTTagByteArray()
    {
    }

    public NBTTagByteArray(byte[] p_i45128_1_)
    {
        byteArray = p_i45128_1_;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(byteArray.length);
        par1DataOutput.write(byteArray);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        int var3 = par1DataInput.readInt();
        byteArray = new byte[var3];
        par1DataInput.readFully(byteArray);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)7;
    }

    public String toString()
    {
        return "[" + byteArray.length + " bytes]";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        byte[] var1 = new byte[byteArray.length];
        System.arraycopy(byteArray, 0, var1, 0, byteArray.length);
        return new NBTTagByteArray(var1);
    }

    public boolean equals(Object par1Obj)
    {
        return super.equals(par1Obj) ? Arrays.equals(byteArray, ((NBTTagByteArray)par1Obj).byteArray) : false;
    }

    public int hashCode()
    {
        return super.hashCode() ^ Arrays.hashCode(byteArray);
    }

    public byte[] func_150292_c()
    {
        return byteArray;
    }
}
