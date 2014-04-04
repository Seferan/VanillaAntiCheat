package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO
{
    private static final Logger logger = LogManager.getLogger();
    private static Map field_143040_a = new HashMap();
    private static Map field_143038_b = new HashMap();
    private static Map field_143039_c = new HashMap();
    private static Map field_143037_d = new HashMap();
    private static final String __OBFID = "CL_00000509";

    private static void registerStructure(Class par0Class, String par1Str)
    {
        field_143040_a.put(par1Str, par0Class);
        field_143038_b.put(par0Class, par1Str);
    }

    static void func_143031_a(Class par0Class, String par1Str)
    {
        field_143039_c.put(par1Str, par0Class);
        field_143037_d.put(par0Class, par1Str);
    }

    public static String func_143033_a(StructureStart par0StructureStart)
    {
        return (String)field_143038_b.get(par0StructureStart.getClass());
    }

    public static String func_143036_a(StructureComponent par0StructureComponent)
    {
        return (String)field_143037_d.get(par0StructureComponent.getClass());
    }

    public static StructureStart func_143035_a(NBTTagCompound par0NBTTagCompound, World par1World)
    {
        StructureStart var2 = null;

        try
        {
            Class var3 = (Class)field_143040_a.get(par0NBTTagCompound.getString("id"));

            if (var3 != null)
            {
                var2 = (StructureStart)var3.newInstance();
            }
        }
        catch (Exception var4)
        {
            logger.warn("Failed Start with id " + par0NBTTagCompound.getString("id"));
            var4.printStackTrace();
        }

        if (var2 != null)
        {
            var2.func_143020_a(par1World, par0NBTTagCompound);
        }
        else
        {
            logger.warn("Skipping Structure with id " + par0NBTTagCompound.getString("id"));
        }

        return var2;
    }

    public static StructureComponent func_143032_b(NBTTagCompound par0NBTTagCompound, World par1World)
    {
        StructureComponent var2 = null;

        try
        {
            Class var3 = (Class)field_143039_c.get(par0NBTTagCompound.getString("id"));

            if (var3 != null)
            {
                var2 = (StructureComponent)var3.newInstance();
            }
        }
        catch (Exception var4)
        {
            logger.warn("Failed Piece with id " + par0NBTTagCompound.getString("id"));
            var4.printStackTrace();
        }

        if (var2 != null)
        {
            var2.func_143009_a(par1World, par0NBTTagCompound);
        }
        else
        {
            logger.warn("Skipping Piece with id " + par0NBTTagCompound.getString("id"));
        }

        return var2;
    }

    static
    {
        registerStructure(StructureMineshaftStart.class, "Mineshaft");
        registerStructure(MapGenVillage.Start.class, "Village");
        registerStructure(MapGenNetherBridge.Start.class, "Fortress");
        registerStructure(MapGenStronghold.Start.class, "Stronghold");
        registerStructure(MapGenScatteredFeature.Start.class, "Temple");
        StructureMineshaftPieces.registerStructurePieces();
        StructureVillagePieces.registerVillagePieces();
        StructureNetherBridgePieces.registerNetherFortressPieces();
        StructureStrongholdPieces.registerStrongholdPieces();
        ComponentScatteredFeaturePieces.registerScatteredFeaturePieces();
    }
}
