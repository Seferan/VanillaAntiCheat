package net.minecraft.network.status.client;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C01PacketPing extends Packet
{
    private long field_149290_a;
    private static final String __OBFID = "CL_00001392";

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149290_a = p_148837_1_.readLong();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeLong(field_149290_a);
    }

    public void func_148833_a(INetHandlerStatusServer p_149288_1_)
    {
        p_149288_1_.processPing(this);
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

    public long func_149289_c()
    {
        return field_149290_a;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerStatusServer)p_148833_1_);
    }
}
