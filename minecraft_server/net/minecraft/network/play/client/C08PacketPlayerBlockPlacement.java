package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C08PacketPlayerBlockPlacement extends Packet
{
    private int x;
    private int y;
    private int z;
    private int side;
    private ItemStack field_149580_e;
    private float xOffset;
    private float yOffset;
    private float field_149584_h;
    private static final String __OBFID = "CL_00001371";

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        x = p_148837_1_.readInt();
        y = p_148837_1_.readUnsignedByte();
        z = p_148837_1_.readInt();
        side = p_148837_1_.readUnsignedByte();
        field_149580_e = p_148837_1_.readItemStackFromBuffer();
        xOffset = p_148837_1_.readUnsignedByte() / 16.0F;
        yOffset = p_148837_1_.readUnsignedByte() / 16.0F;
        field_149584_h = p_148837_1_.readUnsignedByte() / 16.0F;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(x);
        p_148840_1_.writeByte(y);
        p_148840_1_.writeInt(z);
        p_148840_1_.writeByte(side);
        p_148840_1_.writeItemStackToBuffer(field_149580_e);
        p_148840_1_.writeByte((int)(xOffset * 16.0F));
        p_148840_1_.writeByte((int)(yOffset * 16.0F));
        p_148840_1_.writeByte((int)(field_149584_h * 16.0F));
    }

    public void func_148833_a(INetHandlerPlayServer p_149572_1_)
    {
        p_149572_1_.handlePlace(this);
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public int getSide()
    {
        return side;
    }

    public ItemStack func_149574_g()
    {
        return field_149580_e;
    }

    public float getXOffset()
    {
        return xOffset;
    }

    public float getYOffset()
    {
        return yOffset;
    }

    public float getZOffset()
    {
        return field_149584_h;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}
