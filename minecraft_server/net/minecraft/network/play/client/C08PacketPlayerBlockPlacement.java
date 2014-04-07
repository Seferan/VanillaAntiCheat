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
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readUnsignedByte();
        this.z = p_148837_1_.readInt();
        this.side = p_148837_1_.readUnsignedByte();
        this.field_149580_e = p_148837_1_.readItemStackFromBuffer();
        this.xOffset = (float)p_148837_1_.readUnsignedByte() / 16.0F;
        this.yOffset = (float)p_148837_1_.readUnsignedByte() / 16.0F;
        this.field_149584_h = (float)p_148837_1_.readUnsignedByte() / 16.0F;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeByte(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeByte(this.side);
        p_148840_1_.writeItemStackToBuffer(this.field_149580_e);
        p_148840_1_.writeByte((int)(this.xOffset * 16.0F));
        p_148840_1_.writeByte((int)(this.yOffset * 16.0F));
        p_148840_1_.writeByte((int)(this.field_149584_h * 16.0F));
    }

    public void func_148833_a(INetHandlerPlayServer p_149572_1_)
    {
        p_149572_1_.handlePlace(this);
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }

    public int getSide()
    {
        return this.side;
    }

    public ItemStack func_149574_g()
    {
        return this.field_149580_e;
    }

    public float getXOffset()
    {
        return this.xOffset;
    }

    public float getYOffset()
    {
        return this.yOffset;
    }

    public float getZOffset()
    {
        return this.field_149584_h;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}
