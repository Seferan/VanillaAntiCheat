package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S00PacketKeepAlive extends Packet
{
    private int field_149136_a;
    private static final String __OBFID = "CL_00001303";

    public S00PacketKeepAlive()
    {
    }

    public S00PacketKeepAlive(int p_i45195_1_)
    {
        field_149136_a = p_i45195_1_;
    }

    public void func_148833_a(INetHandlerPlayClient p_149135_1_)
    {
        p_149135_1_.handleKeepAlive(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149136_a = p_148837_1_.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(field_149136_a);
    }

    /**
     * If true, the network manager will process the packet immediately when
     * received, otherwise it will queue it for processing. Currently true for:
     * Disconnect, LoginSuccess, KeepAlive, ServerQuery/Info, Ping/Pong
     */
    public boolean hasPriority()
    {
        return true;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}
