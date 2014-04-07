package net.minecraft.world.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData
{
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public byte scale;

    /** colours */
    public byte[] colors = new byte[16384];

    /**
     * Holds a reference to the MapInfo of the players who own a copy of the map
     */
    public List playersArrayList = new ArrayList();

    /**
     * Holds a reference to the players who own a copy of the map and a
     * reference to their MapInfo
     */
    private Map playersHashMap = new HashMap();
    public Map playersVisibleOnMap = new LinkedHashMap();
    private static final String __OBFID = "CL_00000577";

    public MapData(String par1Str)
    {
        super(par1Str);
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        dimension = par1NBTTagCompound.getByte("dimension");
        xCenter = par1NBTTagCompound.getInteger("xCenter");
        zCenter = par1NBTTagCompound.getInteger("zCenter");
        scale = par1NBTTagCompound.getByte("scale");

        if (scale < 0)
        {
            scale = 0;
        }

        if (scale > 4)
        {
            scale = 4;
        }

        short var2 = par1NBTTagCompound.getShort("width");
        short var3 = par1NBTTagCompound.getShort("height");

        if (var2 == 128 && var3 == 128)
        {
            colors = par1NBTTagCompound.getByteArray("colors");
        }
        else
        {
            byte[] var4 = par1NBTTagCompound.getByteArray("colors");
            colors = new byte[16384];
            int var5 = (128 - var2) / 2;
            int var6 = (128 - var3) / 2;

            for (int var7 = 0; var7 < var3; ++var7)
            {
                int var8 = var7 + var6;

                if (var8 >= 0 || var8 < 128)
                {
                    for (int var9 = 0; var9 < var2; ++var9)
                    {
                        int var10 = var9 + var5;

                        if (var10 >= 0 || var10 < 128)
                        {
                            colors[var10 + var8 * 128] = var4[var9 + var7 * var2];
                        }
                    }
                }
            }
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities
     * and TileEntities
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("dimension", dimension);
        par1NBTTagCompound.setInteger("xCenter", xCenter);
        par1NBTTagCompound.setInteger("zCenter", zCenter);
        par1NBTTagCompound.setByte("scale", scale);
        par1NBTTagCompound.setShort("width", (short)128);
        par1NBTTagCompound.setShort("height", (short)128);
        par1NBTTagCompound.setByteArray("colors", colors);
    }

    /**
     * Adds the player passed to the list of visible players and checks to see
     * which players are visible
     */
    public void updateVisiblePlayers(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        if (!playersHashMap.containsKey(par1EntityPlayer))
        {
            MapData.MapInfo var3 = new MapData.MapInfo(par1EntityPlayer);
            playersHashMap.put(par1EntityPlayer, var3);
            playersArrayList.add(var3);
        }

        if (!par1EntityPlayer.inventory.hasItemStack(par2ItemStack))
        {
            playersVisibleOnMap.remove(par1EntityPlayer.getUsername());
        }

        for (int var5 = 0; var5 < playersArrayList.size(); ++var5)
        {
            MapData.MapInfo var4 = (MapData.MapInfo)playersArrayList.get(var5);

            if (!var4.entityplayerObj.isDead && (var4.entityplayerObj.inventory.hasItemStack(par2ItemStack) || par2ItemStack.isOnItemFrame()))
            {
                if (!par2ItemStack.isOnItemFrame() && var4.entityplayerObj.dimension == dimension)
                {
                    func_82567_a(0, var4.entityplayerObj.worldObj, var4.entityplayerObj.getUsername(), var4.entityplayerObj.posX, var4.entityplayerObj.posZ, var4.entityplayerObj.rotationYaw);
                }
            }
            else
            {
                playersHashMap.remove(var4.entityplayerObj);
                playersArrayList.remove(var4);
            }
        }

        if (par2ItemStack.isOnItemFrame())
        {
            func_82567_a(1, par1EntityPlayer.worldObj, "frame-" + par2ItemStack.getItemFrame().getEntityId(), par2ItemStack.getItemFrame().field_146063_b, par2ItemStack.getItemFrame().field_146062_d, par2ItemStack.getItemFrame().hangingDirection * 90);
        }
    }

    private void func_82567_a(int par1, World par2World, String par3Str, double par4, double par6, double par8)
    {
        int var10 = 1 << scale;
        float var11 = (float)(par4 - xCenter) / var10;
        float var12 = (float)(par6 - zCenter) / var10;
        byte var13 = (byte)((int)(var11 * 2.0F + 0.5D));
        byte var14 = (byte)((int)(var12 * 2.0F + 0.5D));
        byte var16 = 63;
        byte var15;

        if (var11 >= (-var16) && var12 >= (-var16) && var11 <= var16 && var12 <= var16)
        {
            par8 += par8 < 0.0D ? -8.0D : 8.0D;
            var15 = (byte)((int)(par8 * 16.0D / 360.0D));

            if (dimension < 0)
            {
                int var17 = (int)(par2World.getWorldInfo().getWorldTime() / 10L);
                var15 = (byte)(var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
            }
        }
        else
        {
            if (Math.abs(var11) >= 320.0F || Math.abs(var12) >= 320.0F)
            {
                playersVisibleOnMap.remove(par3Str);
                return;
            }

            par1 = 6;
            var15 = 0;

            if (var11 <= (-var16))
            {
                var13 = (byte)((int)(var16 * 2 + 2.5D));
            }

            if (var12 <= (-var16))
            {
                var14 = (byte)((int)(var16 * 2 + 2.5D));
            }

            if (var11 >= var16)
            {
                var13 = (byte)(var16 * 2 + 1);
            }

            if (var12 >= var16)
            {
                var14 = (byte)(var16 * 2 + 1);
            }
        }

        playersVisibleOnMap.put(par3Str, new MapData.MapCoord((byte)par1, var13, var14, var15));
    }

    /**
     * Get byte array of packet data to send to players on map for updating map
     * data
     */
    public byte[] getUpdatePacketData(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        MapData.MapInfo var4 = (MapData.MapInfo)playersHashMap.get(par3EntityPlayer);
        return var4 == null ? null : var4.getPlayersOnMap(par1ItemStack);
    }

    /**
     * Marks a vertical range of pixels as being modified so they will be resent
     * to clients. Parameters: X, lowest Y, highest Y
     */
    public void setColumnDirty(int par1, int par2, int par3)
    {
        super.markDirty();

        for (int var4 = 0; var4 < playersArrayList.size(); ++var4)
        {
            MapData.MapInfo var5 = (MapData.MapInfo)playersArrayList.get(var4);

            if (var5.field_76209_b[par1] < 0 || var5.field_76209_b[par1] > par2)
            {
                var5.field_76209_b[par1] = par2;
            }

            if (var5.field_76210_c[par1] < 0 || var5.field_76210_c[par1] < par3)
            {
                var5.field_76210_c[par1] = par3;
            }
        }
    }

    public MapData.MapInfo func_82568_a(EntityPlayer par1EntityPlayer)
    {
        MapData.MapInfo var2 = (MapData.MapInfo)playersHashMap.get(par1EntityPlayer);

        if (var2 == null)
        {
            var2 = new MapData.MapInfo(par1EntityPlayer);
            playersHashMap.put(par1EntityPlayer, var2);
            playersArrayList.add(var2);
        }

        return var2;
    }

    public class MapCoord
    {
        public byte iconSize;
        public byte centerX;
        public byte centerZ;
        public byte iconRotation;
        private static final String __OBFID = "CL_00000579";

        public MapCoord(byte par2, byte par3, byte par4, byte par5)
        {
            iconSize = par2;
            centerX = par3;
            centerZ = par4;
            iconRotation = par5;
        }
    }

    public class MapInfo
    {
        public final EntityPlayer entityplayerObj;
        public int[] field_76209_b = new int[128];
        public int[] field_76210_c = new int[128];
        private int currentRandomNumber;
        private int ticksUntilPlayerLocationMapUpdate;
        private byte[] lastPlayerLocationOnMap;
        public int field_82569_d;
        private boolean field_82570_i;
        private static final String __OBFID = "CL_00000578";

        public MapInfo(EntityPlayer par2EntityPlayer)
        {
            entityplayerObj = par2EntityPlayer;

            for (int var3 = 0; var3 < field_76209_b.length; ++var3)
            {
                field_76209_b[var3] = 0;
                field_76210_c[var3] = 127;
            }
        }

        public byte[] getPlayersOnMap(ItemStack par1ItemStack)
        {
            byte[] var2;

            if (!field_82570_i)
            {
                var2 = new byte[] {(byte)2, scale};
                field_82570_i = true;
                return var2;
            }
            else
            {
                int var3;
                int var10;

                if (--ticksUntilPlayerLocationMapUpdate < 0)
                {
                    ticksUntilPlayerLocationMapUpdate = 4;
                    var2 = new byte[playersVisibleOnMap.size() * 3 + 1];
                    var2[0] = 1;
                    var3 = 0;

                    for (Iterator var4 = playersVisibleOnMap.values().iterator(); var4.hasNext(); ++var3)
                    {
                        MapData.MapCoord var5 = (MapData.MapCoord)var4.next();
                        var2[var3 * 3 + 1] = (byte)(var5.iconSize << 4 | var5.iconRotation & 15);
                        var2[var3 * 3 + 2] = var5.centerX;
                        var2[var3 * 3 + 3] = var5.centerZ;
                    }

                    boolean var9 = !par1ItemStack.isOnItemFrame();

                    if (lastPlayerLocationOnMap != null && lastPlayerLocationOnMap.length == var2.length)
                    {
                        for (var10 = 0; var10 < var2.length; ++var10)
                        {
                            if (var2[var10] != lastPlayerLocationOnMap[var10])
                            {
                                var9 = false;
                                break;
                            }
                        }
                    }
                    else
                    {
                        var9 = false;
                    }

                    if (!var9)
                    {
                        lastPlayerLocationOnMap = var2;
                        return var2;
                    }
                }

                for (int var8 = 0; var8 < 1; ++var8)
                {
                    var3 = currentRandomNumber++ * 11 % 128;

                    if (field_76209_b[var3] >= 0)
                    {
                        int var11 = field_76210_c[var3] - field_76209_b[var3] + 1;
                        var10 = field_76209_b[var3];
                        byte[] var6 = new byte[var11 + 3];
                        var6[0] = 0;
                        var6[1] = (byte)var3;
                        var6[2] = (byte)var10;

                        for (int var7 = 0; var7 < var6.length - 3; ++var7)
                        {
                            var6[var7 + 3] = colors[(var7 + var10) * 128 + var3];
                        }

                        field_76210_c[var3] = -1;
                        field_76209_b[var3] = -1;
                        return var6;
                    }
                }

                return null;
            }
        }
    }
}
