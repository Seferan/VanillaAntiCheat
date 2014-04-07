package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C10PacketCreativeInventoryAction extends Packet
{
    private int field_149629_a;
    private ItemStack field_149628_b;
    private static final String __OBFID = "CL_00001369";

    public void func_148833_a(INetHandlerPlayServer p_149626_1_)
    {
        p_149626_1_.func_147344_a(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149629_a = p_148837_1_.readShort();
        field_149628_b = p_148837_1_.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeShort(field_149629_a);
        p_148840_1_.writeItemStackToBuffer(field_149628_b);
    }

    public int func_149627_c()
    {
        return field_149629_a;
    }

    public ItemStack func_149625_d()
    {
        return field_149628_b;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}
