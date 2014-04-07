package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity
{
    private final MobSpawnerBaseLogic field_145882_a = new MobSpawnerBaseLogic()
    {
        private static final String __OBFID = "CL_00000361";

        public void func_98267_a(int par1)
        {
            TileEntityMobSpawner.this.worldObj.func_147452_c(TileEntityMobSpawner.this.xCoord, TileEntityMobSpawner.this.yCoord, TileEntityMobSpawner.this.zCoord, Blocks.mob_spawner, par1, 0);
        }

        public World getSpawnerWorld()
        {
            return TileEntityMobSpawner.this.worldObj;
        }

        public int getSpawnerX()
        {
            return TileEntityMobSpawner.this.xCoord;
        }

        public int getSpawnerY()
        {
            return TileEntityMobSpawner.this.yCoord;
        }

        public int getSpawnerZ()
        {
            return TileEntityMobSpawner.this.zCoord;
        }

        public void setRandomMinecart(MobSpawnerBaseLogic.WeightedRandomMinecart par1WeightedRandomMinecart)
        {
            super.setRandomMinecart(par1WeightedRandomMinecart);

            if (getSpawnerWorld() != null)
            {
                getSpawnerWorld().markBlockForUpdate(TileEntityMobSpawner.this.xCoord, TileEntityMobSpawner.this.yCoord, TileEntityMobSpawner.this.zCoord);
            }
        }
    };
    private static final String __OBFID = "CL_00000360";

    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
        super.readFromNBT(p_145839_1_);
        field_145882_a.readFromNBT(p_145839_1_);
    }

    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
        super.writeToNBT(p_145841_1_);
        field_145882_a.writeToNBT(p_145841_1_);
    }

    public void updateEntity()
    {
        field_145882_a.updateSpawner();
        super.updateEntity();
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        writeToNBT(var1);
        var1.removeTag("SpawnPotentials");
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, var1);
    }

    public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_)
    {
        return field_145882_a.setDelayToMin(p_145842_1_) ? true : super.receiveClientEvent(p_145842_1_, p_145842_2_);
    }

    public MobSpawnerBaseLogic func_145881_a()
    {
        return field_145882_a;
    }
}
