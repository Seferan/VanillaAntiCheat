package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C07PacketPlayerDigging extends Packet
{
    private int x;
    private int y;
    private int z;
    private int side;
    private int status;
    private static final String __OBFID = "CL_00001365";

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        status = p_148837_1_.readUnsignedByte();
        x = p_148837_1_.readInt();
        y = p_148837_1_.readUnsignedByte();
        z = p_148837_1_.readInt();
        side = p_148837_1_.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(status);
        p_148840_1_.writeInt(x);
        p_148840_1_.writeByte(y);
        p_148840_1_.writeInt(z);
        p_148840_1_.writeByte(side);
    }

    public void func_148833_a(INetHandlerPlayServer p_149504_1_)
    {
        p_149504_1_.handleBlockDig(this);
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

    public int getStatus()
    {
        return status;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}
