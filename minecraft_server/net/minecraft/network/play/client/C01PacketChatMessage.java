package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C01PacketChatMessage extends Packet
{
    private String message;
    private static final String __OBFID = "CL_00001347";

    public C01PacketChatMessage()
    {
    }

    public C01PacketChatMessage(String p_i45240_1_)
    {
        if (p_i45240_1_.length() > 100)
        {
            p_i45240_1_ = p_i45240_1_.substring(0, 100);
        }

        message = p_i45240_1_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        message = p_148837_1_.readStringFromBuffer(100);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeStringToBuffer(message);
    }

    public void func_148833_a(INetHandlerPlayServer p_149438_1_)
    {
        p_149438_1_.handleChat(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values.
     * Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("message=\'%s\'", new Object[] {message});
    }

    public String getMessage()
    {
        return message;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}
