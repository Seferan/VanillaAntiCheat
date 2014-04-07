package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public abstract class StructureStart
{
    /** List of all StructureComponents that are part of this structure */
    protected LinkedList components = new LinkedList();
    protected StructureBoundingBox boundingBox;
    private int field_143024_c;
    private int field_143023_d;
    private static final String __OBFID = "CL_00000513";

    public StructureStart()
    {
    }

    public StructureStart(int par1, int par2)
    {
        field_143024_c = par1;
        field_143023_d = par2;
    }

    public StructureBoundingBox getBoundingBox()
    {
        return boundingBox;
    }

    public LinkedList getComponents()
    {
        return components;
    }

    /**
     * Keeps iterating Structure Pieces and spawning them until the checks tell
     * it to stop
     */
    public void generateStructure(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        Iterator var4 = components.iterator();

        while (var4.hasNext())
        {
            StructureComponent var5 = (StructureComponent)var4.next();

            if (var5.getBoundingBox().intersectsWith(par3StructureBoundingBox) && !var5.addComponentParts(par1World, par2Random, par3StructureBoundingBox))
            {
                var4.remove();
            }
        }
    }

    /**
     * Calculates total bounding box based on components' bounding boxes and
     * saves it to boundingBox
     */
    protected void updateBoundingBox()
    {
        boundingBox = StructureBoundingBox.getNewBoundingBox();
        Iterator var1 = components.iterator();

        while (var1.hasNext())
        {
            StructureComponent var2 = (StructureComponent)var1.next();
            boundingBox.expandTo(var2.getBoundingBox());
        }
    }

    public NBTTagCompound func_143021_a(int par1, int par2)
    {
        NBTTagCompound var3 = new NBTTagCompound();
        var3.setString("id", MapGenStructureIO.func_143033_a(this));
        var3.setInteger("ChunkX", par1);
        var3.setInteger("ChunkZ", par2);
        var3.setTag("BB", boundingBox.func_151535_h());
        NBTTagList var4 = new NBTTagList();
        Iterator var5 = components.iterator();

        while (var5.hasNext())
        {
            StructureComponent var6 = (StructureComponent)var5.next();
            var4.appendTag(var6.func_143010_b());
        }

        var3.setTag("Children", var4);
        func_143022_a(var3);
        return var3;
    }

    public void func_143022_a(NBTTagCompound par1NBTTagCompound)
    {
    }

    public void func_143020_a(World par1World, NBTTagCompound par2NBTTagCompound)
    {
        field_143024_c = par2NBTTagCompound.getInteger("ChunkX");
        field_143023_d = par2NBTTagCompound.getInteger("ChunkZ");

        if (par2NBTTagCompound.hasKey("BB"))
        {
            boundingBox = new StructureBoundingBox(par2NBTTagCompound.getIntArray("BB"));
        }

        NBTTagList var3 = par2NBTTagCompound.getTagList("Children", 10);

        for (int var4 = 0; var4 < var3.tagCount(); ++var4)
        {
            components.add(MapGenStructureIO.func_143032_b(var3.getCompoundTagAt(var4), par1World));
        }

        func_143017_b(par2NBTTagCompound);
    }

    public void func_143017_b(NBTTagCompound par1NBTTagCompound)
    {
    }

    /**
     * offsets the structure Bounding Boxes up to a certain height, typically 63
     * - 10
     */
    protected void markAvailableHeight(World par1World, Random par2Random, int par3)
    {
        int var4 = 63 - par3;
        int var5 = boundingBox.getYSize() + 1;

        if (var5 < var4)
        {
            var5 += par2Random.nextInt(var4 - var5);
        }

        int var6 = var5 - boundingBox.maxY;
        boundingBox.offset(0, var6, 0);
        Iterator var7 = components.iterator();

        while (var7.hasNext())
        {
            StructureComponent var8 = (StructureComponent)var7.next();
            var8.getBoundingBox().offset(0, var6, 0);
        }
    }

    protected void setRandomHeight(World par1World, Random par2Random, int par3, int par4)
    {
        int var5 = par4 - par3 + 1 - boundingBox.getYSize();
        boolean var6 = true;
        int var10;

        if (var5 > 1)
        {
            var10 = par3 + par2Random.nextInt(var5);
        }
        else
        {
            var10 = par3;
        }

        int var7 = var10 - boundingBox.minY;
        boundingBox.offset(0, var7, 0);
        Iterator var8 = components.iterator();

        while (var8.hasNext())
        {
            StructureComponent var9 = (StructureComponent)var8.next();
            var9.getBoundingBox().offset(0, var7, 0);
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more
     * than 2 non-road components
     */
    public boolean isSizeableStructure()
    {
        return true;
    }

    public int func_143019_e()
    {
        return field_143024_c;
    }

    public int func_143018_f()
    {
        return field_143023_d;
    }
}
