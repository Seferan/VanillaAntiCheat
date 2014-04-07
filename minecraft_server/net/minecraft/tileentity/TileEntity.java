package net.minecraft.tileentity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntity
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * A HashMap storing string names of classes mapping to the actual
     * java.lang.Class type.
     */
    private static Map nameToClassMap = new HashMap();

    /**
     * A HashMap storing the classes and mapping to the string names (reverse of
     * nameToClassMap).
     */
    private static Map classToNameMap = new HashMap();

    /** the instance of the world the tile entity is in. */
    protected World worldObj;
    public int xCoord;
    public int yCoord;
    public int zCoord;
    protected boolean tileEntityInvalid;
    public int blockMetadata = -1;

    /** the Block type that this TileEntity is contained within */
    public Block blockType;
    private static final String __OBFID = "CL_00000340";

    private static void func_145826_a(Class p_145826_0_, String p_145826_1_)
    {
        if (nameToClassMap.containsKey(p_145826_1_))
        {
            throw new IllegalArgumentException("Duplicate id: " + p_145826_1_);
        }
        else
        {
            nameToClassMap.put(p_145826_1_, p_145826_0_);
            classToNameMap.put(p_145826_0_, p_145826_1_);
        }
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorldObj()
    {
        return worldObj;
    }

    /**
     * Sets the worldObj for this tileEntity.
     */
    public void setWorldObj(World p_145834_1_)
    {
        worldObj = p_145834_1_;
    }

    /**
     * Returns true if the worldObj isn't null.
     */
    public boolean hasWorldObj()
    {
        return worldObj != null;
    }

    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
        xCoord = p_145839_1_.getInteger("x");
        yCoord = p_145839_1_.getInteger("y");
        zCoord = p_145839_1_.getInteger("z");
    }

    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
        String var2 = (String)classToNameMap.get(this.getClass());

        if (var2 == null)
        {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        else
        {
            p_145841_1_.setString("id", var2);
            p_145841_1_.setInteger("x", xCoord);
            p_145841_1_.setInteger("y", yCoord);
            p_145841_1_.setInteger("z", zCoord);
        }
    }

    public void updateEntity()
    {
    }

    /**
     * Creates a new entity and loads its data from the specified NBT.
     */
    public static TileEntity createAndLoadEntity(NBTTagCompound p_145827_0_)
    {
        TileEntity var1 = null;

        try
        {
            Class var2 = (Class)nameToClassMap.get(p_145827_0_.getString("id"));

            if (var2 != null)
            {
                var1 = (TileEntity)var2.newInstance();
            }
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }

        if (var1 != null)
        {
            var1.readFromNBT(p_145827_0_);
        }
        else
        {
            logger.warn("Skipping BlockEntity with id " + p_145827_0_.getString("id"));
        }

        return var1;
    }

    public int getBlockMetadata()
    {
        if (blockMetadata == -1)
        {
            blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        }

        return blockMetadata;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        if (worldObj != null)
        {
            blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
            worldObj.func_147476_b(xCoord, yCoord, zCoord, this);

            if (getBlockType() != Blocks.air)
            {
                worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
            }
        }
    }

    /**
     * Gets the block type at the location of this entity (client-only).
     */
    public Block getBlockType()
    {
        if (blockType == null)
        {
            blockType = worldObj.getBlock(xCoord, yCoord, zCoord);
        }

        return blockType;
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        return null;
    }

    public boolean isInvalid()
    {
        return tileEntityInvalid;
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        tileEntityInvalid = true;
    }

    /**
     * validates a tile entity
     */
    public void validate()
    {
        tileEntityInvalid = false;
    }

    public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_)
    {
        return false;
    }

    public void updateContainingBlockInfo()
    {
        blockType = null;
        blockMetadata = -1;
    }

    public void func_145828_a(CrashReportCategory p_145828_1_)
    {
        p_145828_1_.addCrashSectionCallable("Name", new Callable()
        {
            private static final String __OBFID = "CL_00000341";

            public String call()
            {
                return (String)TileEntity.classToNameMap.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
            }
        });
        CrashReportCategory.func_147153_a(p_145828_1_, xCoord, yCoord, zCoord, getBlockType(), getBlockMetadata());
        p_145828_1_.addCrashSectionCallable("Actual block type", new Callable()
        {
            private static final String __OBFID = "CL_00000343";

            public String call()
            {
                int var1 = Block.getIdFromBlock(worldObj.getBlock(xCoord, yCoord, zCoord));

                try
                {
                    return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(var1), Block.getBlockById(var1).getUnlocalizedName(), Block.getBlockById(var1).getClass().getCanonicalName()});
                }
                catch (Throwable var3)
                {
                    return "ID #" + var1;
                }
            }
        });
        p_145828_1_.addCrashSectionCallable("Actual block data value", new Callable()
        {
            private static final String __OBFID = "CL_00000344";

            public String call()
            {
                int var1 = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

                if (var1 < 0)
                {
                    return "Unknown? (Got " + var1 + ")";
                }
                else
                {
                    String var2 = String.format("%4s", new Object[] {Integer.toBinaryString(var1)}).replace(" ", "0");
                    return String.format("%1$d / 0x%1$X / 0b%2$s", new Object[] {Integer.valueOf(var1), var2});
                }
            }
        });
    }

    static
    {
        func_145826_a(TileEntityFurnace.class, "Furnace");
        func_145826_a(TileEntityChest.class, "Chest");
        func_145826_a(TileEntityEnderChest.class, "EnderChest");
        func_145826_a(BlockJukebox.TileEntityJukebox.class, "RecordPlayer");
        func_145826_a(TileEntityDispenser.class, "Trap");
        func_145826_a(TileEntityDropper.class, "Dropper");
        func_145826_a(TileEntitySign.class, "Sign");
        func_145826_a(TileEntityMobSpawner.class, "MobSpawner");
        func_145826_a(TileEntityNote.class, "Music");
        func_145826_a(TileEntityPiston.class, "Piston");
        func_145826_a(TileEntityBrewingStand.class, "Cauldron");
        func_145826_a(TileEntityEnchantmentTable.class, "EnchantTable");
        func_145826_a(TileEntityEndPortal.class, "Airportal");
        func_145826_a(TileEntityCommandBlock.class, "Control");
        func_145826_a(TileEntityBeacon.class, "Beacon");
        func_145826_a(TileEntitySkull.class, "Skull");
        func_145826_a(TileEntityDaylightDetector.class, "DLDetector");
        func_145826_a(TileEntityHopper.class, "Hopper");
        func_145826_a(TileEntityComparator.class, "Comparator");
        func_145826_a(TileEntityFlowerPot.class, "FlowerPot");
    }
}
