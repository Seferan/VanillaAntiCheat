package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C03PacketPlayer extends Packet
{
    protected double field_149479_a;
    protected double field_149477_b;
    protected double field_149478_c;
    protected double field_149475_d;
    protected float field_149476_e;
    protected float field_149473_f;
    protected boolean field_149474_g;
    protected boolean field_149480_h;
    protected boolean field_149481_i;
    private static final String __OBFID = "CL_00001360";

    public void func_148833_a(INetHandlerPlayServer p_149468_1_)
    {
        p_149468_1_.handleFlying(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149474_g = p_148837_1_.readUnsignedByte() != 0;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(field_149474_g ? 1 : 0);
    }

    public double getX()
    {
        return field_149479_a;
    }

    public double getY()
    {
        return field_149477_b;
    }

    public double getZ()
    {
        return field_149478_c;
    }

    public double getStance()
    {
        return field_149475_d;
    }

    public float yaw()
    {
        return field_149476_e;
    }

    public float pitch()
    {
        return field_149473_f;
    }

    public boolean getOnGround()
    {
        return field_149474_g;
    }

    public boolean getMoving()
    {
        return field_149480_h;
    }

    public boolean rotating()
    {
        return field_149481_i;
    }

    public void setMoving(boolean p_149469_1_)
    {
        field_149480_h = p_149469_1_;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }

    public static class C06PacketPlayerPosLook extends C03PacketPlayer
    {
        private static final String __OBFID = "CL_00001362";

        public C06PacketPlayerPosLook()
        {
            field_149480_h = true;
            field_149481_i = true;
        }

        public void readPacketData(PacketBuffer p_148837_1_) throws IOException
        {
            field_149479_a = p_148837_1_.readDouble();
            field_149477_b = p_148837_1_.readDouble();
            field_149475_d = p_148837_1_.readDouble();
            field_149478_c = p_148837_1_.readDouble();
            field_149476_e = p_148837_1_.readFloat();
            field_149473_f = p_148837_1_.readFloat();
            super.readPacketData(p_148837_1_);
        }

        public void writePacketData(PacketBuffer p_148840_1_) throws IOException
        {
            p_148840_1_.writeDouble(field_149479_a);
            p_148840_1_.writeDouble(field_149477_b);
            p_148840_1_.writeDouble(field_149475_d);
            p_148840_1_.writeDouble(field_149478_c);
            p_148840_1_.writeFloat(field_149476_e);
            p_148840_1_.writeFloat(field_149473_f);
            super.writePacketData(p_148840_1_);
        }

        public void func_148833_a(INetHandler p_148833_1_)
        {
            super.func_148833_a((INetHandlerPlayServer)p_148833_1_);
        }
    }

    public static class C05PacketPlayerLook extends C03PacketPlayer
    {
        private static final String __OBFID = "CL_00001363";

        public C05PacketPlayerLook()
        {
            field_149481_i = true;
        }

        public void readPacketData(PacketBuffer p_148837_1_) throws IOException
        {
            field_149476_e = p_148837_1_.readFloat();
            field_149473_f = p_148837_1_.readFloat();
            super.readPacketData(p_148837_1_);
        }

        public void writePacketData(PacketBuffer p_148840_1_) throws IOException
        {
            p_148840_1_.writeFloat(field_149476_e);
            p_148840_1_.writeFloat(field_149473_f);
            super.writePacketData(p_148840_1_);
        }

        public void func_148833_a(INetHandler p_148833_1_)
        {
            super.func_148833_a((INetHandlerPlayServer)p_148833_1_);
        }
    }

    public static class C04PacketPlayerPosition extends C03PacketPlayer
    {
        private static final String __OBFID = "CL_00001361";

        public C04PacketPlayerPosition()
        {
            field_149480_h = true;
        }

        public void readPacketData(PacketBuffer p_148837_1_) throws IOException
        {
            field_149479_a = p_148837_1_.readDouble();
            field_149477_b = p_148837_1_.readDouble();
            field_149475_d = p_148837_1_.readDouble();
            field_149478_c = p_148837_1_.readDouble();
            super.readPacketData(p_148837_1_);
        }

        public void writePacketData(PacketBuffer p_148840_1_) throws IOException
        {
            p_148840_1_.writeDouble(field_149479_a);
            p_148840_1_.writeDouble(field_149477_b);
            p_148840_1_.writeDouble(field_149475_d);
            p_148840_1_.writeDouble(field_149478_c);
            super.writePacketData(p_148840_1_);
        }

        public void func_148833_a(INetHandler p_148833_1_)
        {
            super.func_148833_a((INetHandlerPlayServer)p_148833_1_);
        }
    }
}
