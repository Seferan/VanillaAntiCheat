package net.minecraft.world.gen.structure;

import java.util.Random;

import net.minecraft.block.BlockLever;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class ComponentScatteredFeaturePieces
{
    private static final String __OBFID = "CL_00000473";

    public static void registerScatteredFeaturePieces()
    {
        MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.DesertPyramid.class, "TeDP");
        MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.JunglePyramid.class, "TeJP");
        MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.SwampHut.class, "TeSH");
    }

    abstract static class Feature extends StructureComponent
    {
        protected int scatteredFeatureSizeX;
        protected int scatteredFeatureSizeY;
        protected int scatteredFeatureSizeZ;
        protected int field_74936_d = -1;
        private static final String __OBFID = "CL_00000479";

        public Feature()
        {
        }

        protected Feature(Random par1Random, int par2, int par3, int par4, int par5, int par6, int par7)
        {
            super(0);
            scatteredFeatureSizeX = par5;
            scatteredFeatureSizeY = par6;
            scatteredFeatureSizeZ = par7;
            coordBaseMode = par1Random.nextInt(4);

            switch (coordBaseMode)
            {
            case 0:
            case 2:
                boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + par5 - 1, par3 + par6 - 1, par4 + par7 - 1);
                break;

            default:
                boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + par7 - 1, par3 + par6 - 1, par4 + par5 - 1);
            }
        }

        protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
        {
            par1NBTTagCompound.setInteger("Width", scatteredFeatureSizeX);
            par1NBTTagCompound.setInteger("Height", scatteredFeatureSizeY);
            par1NBTTagCompound.setInteger("Depth", scatteredFeatureSizeZ);
            par1NBTTagCompound.setInteger("HPos", field_74936_d);
        }

        protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
        {
            scatteredFeatureSizeX = par1NBTTagCompound.getInteger("Width");
            scatteredFeatureSizeY = par1NBTTagCompound.getInteger("Height");
            scatteredFeatureSizeZ = par1NBTTagCompound.getInteger("Depth");
            field_74936_d = par1NBTTagCompound.getInteger("HPos");
        }

        protected boolean func_74935_a(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3)
        {
            if (field_74936_d >= 0)
            {
                return true;
            }
            else
            {
                int var4 = 0;
                int var5 = 0;

                for (int var6 = boundingBox.minZ; var6 <= boundingBox.maxZ; ++var6)
                {
                    for (int var7 = boundingBox.minX; var7 <= boundingBox.maxX; ++var7)
                    {
                        if (par2StructureBoundingBox.isVecInside(var7, 64, var6))
                        {
                            var4 += Math.max(par1World.getTopSolidOrLiquidBlock(var7, var6), par1World.provider.getAverageGroundLevel());
                            ++var5;
                        }
                    }
                }

                if (var5 == 0)
                {
                    return false;
                }
                else
                {
                    field_74936_d = var4 / var5;
                    boundingBox.offset(0, field_74936_d - boundingBox.minY + par3, 0);
                    return true;
                }
            }
        }
    }

    public static class JunglePyramid extends ComponentScatteredFeaturePieces.Feature
    {
        private boolean field_74947_h;
        private boolean field_74948_i;
        private boolean field_74945_j;
        private boolean field_74946_k;
        private static final WeightedRandomChestContent[] junglePyramidsChestContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15), new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2), new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20), new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)};
        private static final WeightedRandomChestContent[] junglePyramidsDispenserContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.arrow, 0, 2, 7, 30)};
        private static ComponentScatteredFeaturePieces.JunglePyramid.Stones junglePyramidsRandomScatteredStones = new ComponentScatteredFeaturePieces.JunglePyramid.Stones(null);
        private static final String __OBFID = "CL_00000477";

        public JunglePyramid()
        {
        }

        public JunglePyramid(Random par1Random, int par2, int par3)
        {
            super(par1Random, par2, 64, par3, 12, 10, 15);
        }

        protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
        {
            super.func_143012_a(par1NBTTagCompound);
            par1NBTTagCompound.setBoolean("placedMainChest", field_74947_h);
            par1NBTTagCompound.setBoolean("placedHiddenChest", field_74948_i);
            par1NBTTagCompound.setBoolean("placedTrap1", field_74945_j);
            par1NBTTagCompound.setBoolean("placedTrap2", field_74946_k);
        }

        protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
        {
            super.func_143011_b(par1NBTTagCompound);
            field_74947_h = par1NBTTagCompound.getBoolean("placedMainChest");
            field_74948_i = par1NBTTagCompound.getBoolean("placedHiddenChest");
            field_74945_j = par1NBTTagCompound.getBoolean("placedTrap1");
            field_74946_k = par1NBTTagCompound.getBoolean("placedTrap2");
        }

        public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
        {
            if (!func_74935_a(par1World, par3StructureBoundingBox, 0))
            {
                return false;
            }
            else
            {
                int var4 = func_151555_a(Blocks.stone_stairs, 3);
                int var5 = func_151555_a(Blocks.stone_stairs, 2);
                int var6 = func_151555_a(Blocks.stone_stairs, 0);
                int var7 = func_151555_a(Blocks.stone_stairs, 1);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, -4, 0, scatteredFeatureSizeX - 1, 0, scatteredFeatureSizeZ - 1, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 1, 2, 9, 2, 2, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 1, 12, 9, 2, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 1, 3, 2, 2, 11, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, 1, 3, 9, 2, 11, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 3, 1, 10, 6, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 3, 13, 10, 6, 13, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 3, 2, 1, 6, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 10, 3, 2, 10, 6, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 3, 2, 9, 3, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 6, 2, 9, 6, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 3, 7, 3, 8, 7, 11, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 8, 4, 7, 8, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithAir(par1World, par3StructureBoundingBox, 3, 1, 3, 8, 2, 11);
                fillWithAir(par1World, par3StructureBoundingBox, 4, 3, 6, 7, 3, 9);
                fillWithAir(par1World, par3StructureBoundingBox, 2, 4, 2, 9, 5, 12);
                fillWithAir(par1World, par3StructureBoundingBox, 4, 6, 5, 7, 6, 9);
                fillWithAir(par1World, par3StructureBoundingBox, 5, 7, 6, 6, 7, 8);
                fillWithAir(par1World, par3StructureBoundingBox, 5, 1, 2, 6, 2, 2);
                fillWithAir(par1World, par3StructureBoundingBox, 5, 2, 12, 6, 2, 12);
                fillWithAir(par1World, par3StructureBoundingBox, 5, 5, 1, 6, 5, 1);
                fillWithAir(par1World, par3StructureBoundingBox, 5, 5, 13, 6, 5, 13);
                func_151550_a(par1World, Blocks.air, 0, 1, 5, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.air, 0, 10, 5, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.air, 0, 1, 5, 9, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.air, 0, 10, 5, 9, par3StructureBoundingBox);
                int var8;

                for (var8 = 0; var8 <= 14; var8 += 14)
                {
                    fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 4, var8, 2, 5, var8, false, par2Random, junglePyramidsRandomScatteredStones);
                    fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 4, var8, 4, 5, var8, false, par2Random, junglePyramidsRandomScatteredStones);
                    fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, 4, var8, 7, 5, var8, false, par2Random, junglePyramidsRandomScatteredStones);
                    fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, 4, var8, 9, 5, var8, false, par2Random, junglePyramidsRandomScatteredStones);
                }

                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 5, 6, 0, 6, 6, 0, false, par2Random, junglePyramidsRandomScatteredStones);

                for (var8 = 0; var8 <= 11; var8 += 11)
                {
                    for (int var9 = 2; var9 <= 12; var9 += 2)
                    {
                        fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, var8, 4, var9, var8, 5, var9, false, par2Random, junglePyramidsRandomScatteredStones);
                    }

                    fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, var8, 6, 5, var8, 6, 5, false, par2Random, junglePyramidsRandomScatteredStones);
                    fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, var8, 6, 9, var8, 6, 9, false, par2Random, junglePyramidsRandomScatteredStones);
                }

                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 7, 2, 2, 9, 2, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, 7, 2, 9, 9, 2, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 7, 12, 2, 9, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, 7, 12, 9, 9, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 9, 4, 4, 9, 4, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, 9, 4, 7, 9, 4, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 9, 10, 4, 9, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, 9, 10, 7, 9, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 5, 9, 7, 6, 9, 7, false, par2Random, junglePyramidsRandomScatteredStones);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 5, 9, 6, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 6, 9, 6, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var5, 5, 9, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var5, 6, 9, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 4, 0, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 5, 0, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 6, 0, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 7, 0, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 4, 1, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 4, 2, 9, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 4, 3, 10, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 7, 1, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 7, 2, 9, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var4, 7, 3, 10, par3StructureBoundingBox);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 9, 4, 1, 9, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, 1, 9, 7, 1, 9, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 10, 7, 2, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 5, 4, 5, 6, 4, 5, false, par2Random, junglePyramidsRandomScatteredStones);
                func_151550_a(par1World, Blocks.stone_stairs, var6, 4, 4, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stone_stairs, var7, 7, 4, 5, par3StructureBoundingBox);

                for (var8 = 0; var8 < 4; ++var8)
                {
                    func_151550_a(par1World, Blocks.stone_stairs, var5, 5, 0 - var8, 6 + var8, par3StructureBoundingBox);
                    func_151550_a(par1World, Blocks.stone_stairs, var5, 6, 0 - var8, 6 + var8, par3StructureBoundingBox);
                    fillWithAir(par1World, par3StructureBoundingBox, 5, 0 - var8, 7 + var8, 6, 0 - var8, 9 + var8);
                }

                fillWithAir(par1World, par3StructureBoundingBox, 1, -3, 12, 10, -1, 13);
                fillWithAir(par1World, par3StructureBoundingBox, 1, -3, 1, 3, -1, 13);
                fillWithAir(par1World, par3StructureBoundingBox, 1, -3, 1, 9, -1, 5);

                for (var8 = 1; var8 <= 13; var8 += 2)
                {
                    fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, -3, var8, 1, -2, var8, false, par2Random, junglePyramidsRandomScatteredStones);
                }

                for (var8 = 2; var8 <= 12; var8 += 2)
                {
                    fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, -1, var8, 3, -1, var8, false, par2Random, junglePyramidsRandomScatteredStones);
                }

                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, -2, 1, 5, -2, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, -2, 1, 9, -2, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 6, -3, 1, 6, -3, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 6, -1, 1, 6, -1, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                func_151550_a(par1World, Blocks.tripwire_hook, func_151555_a(Blocks.tripwire_hook, 3) | 4, 1, -3, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.tripwire_hook, func_151555_a(Blocks.tripwire_hook, 1) | 4, 4, -3, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.tripwire, 4, 2, -3, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.tripwire, 4, 3, -3, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 7, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 6, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 4, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 4, -3, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 3, -3, 1, par3StructureBoundingBox);

                if (!field_74945_j)
                {
                    field_74945_j = generateStructureDispenserContents(par1World, par3StructureBoundingBox, par2Random, 3, -2, 1, 2, junglePyramidsDispenserContents, 2);
                }

                func_151550_a(par1World, Blocks.vine, 15, 3, -2, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.tripwire_hook, func_151555_a(Blocks.tripwire_hook, 2) | 4, 7, -3, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.tripwire_hook, func_151555_a(Blocks.tripwire_hook, 0) | 4, 7, -3, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.tripwire, 4, 7, -3, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.tripwire, 4, 7, -3, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.tripwire, 4, 7, -3, 4, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 8, -3, 6, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 9, -3, 6, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 9, -3, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 9, -3, 4, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 9, -2, 4, par3StructureBoundingBox);

                if (!field_74946_k)
                {
                    field_74946_k = generateStructureDispenserContents(par1World, par3StructureBoundingBox, par2Random, 9, -2, 3, 4, junglePyramidsDispenserContents, 2);
                }

                func_151550_a(par1World, Blocks.vine, 15, 8, -1, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.vine, 15, 8, -2, 3, par3StructureBoundingBox);

                if (!field_74947_h)
                {
                    field_74947_h = generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 8, -3, 3, WeightedRandomChestContent.func_92080_a(junglePyramidsChestContents, new WeightedRandomChestContent[] {Items.enchanted_book.func_92114_b(par2Random)}), 2 + par2Random.nextInt(5));
                }

                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 9, -3, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 8, -3, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 4, -3, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 5, -2, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 5, -1, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 6, -3, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 7, -2, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 7, -1, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 8, -3, 5, par3StructureBoundingBox);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, -1, 1, 9, -1, 5, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithAir(par1World, par3StructureBoundingBox, 8, -3, 8, 10, -1, 10);
                func_151550_a(par1World, Blocks.stonebrick, 3, 8, -2, 11, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stonebrick, 3, 9, -2, 11, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.stonebrick, 3, 10, -2, 11, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.lever, BlockLever.func_149819_b(func_151555_a(Blocks.lever, 2)), 8, -2, 12, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.lever, BlockLever.func_149819_b(func_151555_a(Blocks.lever, 2)), 9, -2, 12, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.lever, BlockLever.func_149819_b(func_151555_a(Blocks.lever, 2)), 10, -2, 12, par3StructureBoundingBox);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 8, -3, 8, 8, -3, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 10, -3, 8, 10, -3, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 10, -2, 9, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 8, -2, 9, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 8, -2, 10, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.redstone_wire, 0, 10, -1, 9, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sticky_piston, 1, 9, -2, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sticky_piston, func_151555_a(Blocks.sticky_piston, 4), 10, -2, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sticky_piston, func_151555_a(Blocks.sticky_piston, 4), 10, -1, 8, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.unpowered_repeater, func_151555_a(Blocks.unpowered_repeater, 2), 10, -2, 10, par3StructureBoundingBox);

                if (!field_74948_i)
                {
                    field_74948_i = generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 9, -3, 10, WeightedRandomChestContent.func_92080_a(junglePyramidsChestContents, new WeightedRandomChestContent[] {Items.enchanted_book.func_92114_b(par2Random)}), 2 + par2Random.nextInt(5));
                }

                return true;
            }
        }

        static class Stones extends StructureComponent.BlockSelector
        {
            private static final String __OBFID = "CL_00000478";

            private Stones()
            {
            }

            public void selectBlocks(Random par1Random, int par2, int par3, int par4, boolean par5)
            {
                if (par1Random.nextFloat() < 0.4F)
                {
                    field_151562_a = Blocks.cobblestone;
                }
                else
                {
                    field_151562_a = Blocks.mossy_cobblestone;
                }
            }

            Stones(Object par1ComponentScatteredFeaturePieces2)
            {
                this();
            }
        }
    }

    public static class SwampHut extends ComponentScatteredFeaturePieces.Feature
    {
        private boolean hasWitch;
        private static final String __OBFID = "CL_00000480";

        public SwampHut()
        {
        }

        public SwampHut(Random par1Random, int par2, int par3)
        {
            super(par1Random, par2, 64, par3, 7, 5, 9);
        }

        protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
        {
            super.func_143012_a(par1NBTTagCompound);
            par1NBTTagCompound.setBoolean("Witch", hasWitch);
        }

        protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
        {
            super.func_143011_b(par1NBTTagCompound);
            hasWitch = par1NBTTagCompound.getBoolean("Witch");
        }

        public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
        {
            if (!func_74935_a(par1World, par3StructureBoundingBox, 0))
            {
                return false;
            }
            else
            {
                func_151556_a(par1World, par3StructureBoundingBox, 1, 1, 1, 5, 1, 7, Blocks.planks, 1, Blocks.planks, 1, false);
                func_151556_a(par1World, par3StructureBoundingBox, 1, 4, 2, 5, 4, 7, Blocks.planks, 1, Blocks.planks, 1, false);
                func_151556_a(par1World, par3StructureBoundingBox, 2, 1, 0, 4, 1, 0, Blocks.planks, 1, Blocks.planks, 1, false);
                func_151556_a(par1World, par3StructureBoundingBox, 2, 2, 2, 3, 3, 2, Blocks.planks, 1, Blocks.planks, 1, false);
                func_151556_a(par1World, par3StructureBoundingBox, 1, 2, 3, 1, 3, 6, Blocks.planks, 1, Blocks.planks, 1, false);
                func_151556_a(par1World, par3StructureBoundingBox, 5, 2, 3, 5, 3, 6, Blocks.planks, 1, Blocks.planks, 1, false);
                func_151556_a(par1World, par3StructureBoundingBox, 2, 2, 7, 4, 3, 7, Blocks.planks, 1, Blocks.planks, 1, false);
                func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 2, 1, 3, 2, Blocks.log, Blocks.log, false);
                func_151549_a(par1World, par3StructureBoundingBox, 5, 0, 2, 5, 3, 2, Blocks.log, Blocks.log, false);
                func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 7, 1, 3, 7, Blocks.log, Blocks.log, false);
                func_151549_a(par1World, par3StructureBoundingBox, 5, 0, 7, 5, 3, 7, Blocks.log, Blocks.log, false);
                func_151550_a(par1World, Blocks.fence, 0, 2, 3, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.fence, 0, 3, 3, 7, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.air, 0, 1, 3, 4, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.air, 0, 5, 3, 4, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.air, 0, 5, 3, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.flower_pot, 7, 1, 3, 5, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.crafting_table, 0, 3, 2, 6, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.cauldron, 0, 4, 2, 6, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.fence, 0, 1, 2, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.fence, 0, 5, 2, 1, par3StructureBoundingBox);
                int var4 = func_151555_a(Blocks.oak_stairs, 3);
                int var5 = func_151555_a(Blocks.oak_stairs, 1);
                int var6 = func_151555_a(Blocks.oak_stairs, 0);
                int var7 = func_151555_a(Blocks.oak_stairs, 2);
                func_151556_a(par1World, par3StructureBoundingBox, 0, 4, 1, 6, 4, 1, Blocks.spruce_stairs, var4, Blocks.spruce_stairs, var4, false);
                func_151556_a(par1World, par3StructureBoundingBox, 0, 4, 2, 0, 4, 7, Blocks.spruce_stairs, var6, Blocks.spruce_stairs, var6, false);
                func_151556_a(par1World, par3StructureBoundingBox, 6, 4, 2, 6, 4, 7, Blocks.spruce_stairs, var5, Blocks.spruce_stairs, var5, false);
                func_151556_a(par1World, par3StructureBoundingBox, 0, 4, 8, 6, 4, 8, Blocks.spruce_stairs, var7, Blocks.spruce_stairs, var7, false);
                int var8;
                int var9;

                for (var8 = 2; var8 <= 7; var8 += 5)
                {
                    for (var9 = 1; var9 <= 5; var9 += 4)
                    {
                        func_151554_b(par1World, Blocks.log, 0, var9, -1, var8, par3StructureBoundingBox);
                    }
                }

                if (!hasWitch)
                {
                    var8 = getXWithOffset(2, 5);
                    var9 = getYWithOffset(2);
                    int var10 = getZWithOffset(2, 5);

                    if (par3StructureBoundingBox.isVecInside(var8, var9, var10))
                    {
                        hasWitch = true;
                        EntityWitch var11 = new EntityWitch(par1World);
                        var11.setLocationAndAngles(var8 + 0.5D, var9, var10 + 0.5D, 0.0F, 0.0F);
                        var11.onSpawnWithEgg((IEntityLivingData)null);
                        par1World.spawnEntityInWorld(var11);
                    }
                }

                return true;
            }
        }
    }

    public static class DesertPyramid extends ComponentScatteredFeaturePieces.Feature
    {
        private boolean[] field_74940_h = new boolean[4];
        private static final WeightedRandomChestContent[] itemsToGenerateInTemple = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15), new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2), new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20), new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)};
        private static final String __OBFID = "CL_00000476";

        public DesertPyramid()
        {
        }

        public DesertPyramid(Random par1Random, int par2, int par3)
        {
            super(par1Random, par2, 64, par3, 21, 15, 21);
        }

        protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
        {
            super.func_143012_a(par1NBTTagCompound);
            par1NBTTagCompound.setBoolean("hasPlacedChest0", field_74940_h[0]);
            par1NBTTagCompound.setBoolean("hasPlacedChest1", field_74940_h[1]);
            par1NBTTagCompound.setBoolean("hasPlacedChest2", field_74940_h[2]);
            par1NBTTagCompound.setBoolean("hasPlacedChest3", field_74940_h[3]);
        }

        protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
        {
            super.func_143011_b(par1NBTTagCompound);
            field_74940_h[0] = par1NBTTagCompound.getBoolean("hasPlacedChest0");
            field_74940_h[1] = par1NBTTagCompound.getBoolean("hasPlacedChest1");
            field_74940_h[2] = par1NBTTagCompound.getBoolean("hasPlacedChest2");
            field_74940_h[3] = par1NBTTagCompound.getBoolean("hasPlacedChest3");
        }

        public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
        {
            func_151549_a(par1World, par3StructureBoundingBox, 0, -4, 0, scatteredFeatureSizeX - 1, 0, scatteredFeatureSizeZ - 1, Blocks.sandstone, Blocks.sandstone, false);
            int var4;

            for (var4 = 1; var4 <= 9; ++var4)
            {
                func_151549_a(par1World, par3StructureBoundingBox, var4, var4, var4, scatteredFeatureSizeX - 1 - var4, var4, scatteredFeatureSizeZ - 1 - var4, Blocks.sandstone, Blocks.sandstone, false);
                func_151549_a(par1World, par3StructureBoundingBox, var4 + 1, var4, var4 + 1, scatteredFeatureSizeX - 2 - var4, var4, scatteredFeatureSizeZ - 2 - var4, Blocks.air, Blocks.air, false);
            }

            int var5;

            for (var4 = 0; var4 < scatteredFeatureSizeX; ++var4)
            {
                for (var5 = 0; var5 < scatteredFeatureSizeZ; ++var5)
                {
                    byte var6 = -5;
                    func_151554_b(par1World, Blocks.sandstone, 0, var4, var6, var5, par3StructureBoundingBox);
                }
            }

            var4 = func_151555_a(Blocks.sandstone_stairs, 3);
            var5 = func_151555_a(Blocks.sandstone_stairs, 2);
            int var13 = func_151555_a(Blocks.sandstone_stairs, 0);
            int var7 = func_151555_a(Blocks.sandstone_stairs, 1);
            byte var8 = 1;
            byte var9 = 11;
            func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 9, 4, Blocks.sandstone, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, 1, 10, 1, 3, 10, 3, Blocks.sandstone, Blocks.sandstone, false);
            func_151550_a(par1World, Blocks.sandstone_stairs, var4, 2, 10, 0, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var5, 2, 10, 4, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var13, 0, 10, 2, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var7, 4, 10, 2, par3StructureBoundingBox);
            func_151549_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 5, 0, 0, scatteredFeatureSizeX - 1, 9, 4, Blocks.sandstone, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 4, 10, 1, scatteredFeatureSizeX - 2, 10, 3, Blocks.sandstone, Blocks.sandstone, false);
            func_151550_a(par1World, Blocks.sandstone_stairs, var4, scatteredFeatureSizeX - 3, 10, 0, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var5, scatteredFeatureSizeX - 3, 10, 4, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var13, scatteredFeatureSizeX - 5, 10, 2, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var7, scatteredFeatureSizeX - 1, 10, 2, par3StructureBoundingBox);
            func_151549_a(par1World, par3StructureBoundingBox, 8, 0, 0, 12, 4, 4, Blocks.sandstone, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, 9, 1, 0, 11, 3, 4, Blocks.air, Blocks.air, false);
            func_151550_a(par1World, Blocks.sandstone, 2, 9, 1, 1, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 9, 2, 1, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 9, 3, 1, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 10, 3, 1, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 11, 3, 1, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 11, 2, 1, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 11, 1, 1, par3StructureBoundingBox);
            func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 1, 8, 3, 3, Blocks.sandstone, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 2, 8, 2, 2, Blocks.air, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, 12, 1, 1, 16, 3, 3, Blocks.sandstone, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, 12, 1, 2, 16, 2, 2, Blocks.air, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, 5, 4, 5, scatteredFeatureSizeX - 6, 4, scatteredFeatureSizeZ - 6, Blocks.sandstone, Blocks.sandstone, false);
            func_151549_a(par1World, par3StructureBoundingBox, 9, 4, 9, 11, 4, 11, Blocks.air, Blocks.air, false);
            func_151556_a(par1World, par3StructureBoundingBox, 8, 1, 8, 8, 3, 8, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151556_a(par1World, par3StructureBoundingBox, 12, 1, 8, 12, 3, 8, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151556_a(par1World, par3StructureBoundingBox, 8, 1, 12, 8, 3, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151556_a(par1World, par3StructureBoundingBox, 12, 1, 12, 12, 3, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 5, 4, 4, 11, Blocks.sandstone, Blocks.sandstone, false);
            func_151549_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 5, 1, 5, scatteredFeatureSizeX - 2, 4, 11, Blocks.sandstone, Blocks.sandstone, false);
            func_151549_a(par1World, par3StructureBoundingBox, 6, 7, 9, 6, 7, 11, Blocks.sandstone, Blocks.sandstone, false);
            func_151549_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 7, 7, 9, scatteredFeatureSizeX - 7, 7, 11, Blocks.sandstone, Blocks.sandstone, false);
            func_151556_a(par1World, par3StructureBoundingBox, 5, 5, 9, 5, 7, 11, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151556_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 6, 5, 9, scatteredFeatureSizeX - 6, 7, 11, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151550_a(par1World, Blocks.air, 0, 5, 5, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 5, 6, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 6, 6, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, scatteredFeatureSizeX - 6, 5, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, scatteredFeatureSizeX - 6, 6, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, scatteredFeatureSizeX - 7, 6, 10, par3StructureBoundingBox);
            func_151549_a(par1World, par3StructureBoundingBox, 2, 4, 4, 2, 6, 4, Blocks.air, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 3, 4, 4, scatteredFeatureSizeX - 3, 6, 4, Blocks.air, Blocks.air, false);
            func_151550_a(par1World, Blocks.sandstone_stairs, var4, 2, 4, 5, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var4, 2, 3, 4, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var4, scatteredFeatureSizeX - 3, 4, 5, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var4, scatteredFeatureSizeX - 3, 3, 4, par3StructureBoundingBox);
            func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 3, 2, 2, 3, Blocks.sandstone, Blocks.sandstone, false);
            func_151549_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 3, 1, 3, scatteredFeatureSizeX - 2, 2, 3, Blocks.sandstone, Blocks.sandstone, false);
            func_151550_a(par1World, Blocks.sandstone_stairs, 0, 1, 1, 2, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, 0, scatteredFeatureSizeX - 2, 1, 2, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.stone_slab, 1, 1, 2, 2, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.stone_slab, 1, scatteredFeatureSizeX - 2, 2, 2, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var7, 2, 1, 2, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone_stairs, var13, scatteredFeatureSizeX - 3, 1, 2, par3StructureBoundingBox);
            func_151549_a(par1World, par3StructureBoundingBox, 4, 3, 5, 4, 3, 18, Blocks.sandstone, Blocks.sandstone, false);
            func_151549_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 5, 3, 5, scatteredFeatureSizeX - 5, 3, 17, Blocks.sandstone, Blocks.sandstone, false);
            func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 5, 4, 2, 16, Blocks.air, Blocks.air, false);
            func_151549_a(par1World, par3StructureBoundingBox, scatteredFeatureSizeX - 6, 1, 5, scatteredFeatureSizeX - 5, 2, 16, Blocks.air, Blocks.air, false);
            int var10;

            for (var10 = 5; var10 <= 17; var10 += 2)
            {
                func_151550_a(par1World, Blocks.sandstone, 2, 4, 1, var10, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 1, 4, 2, var10, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, scatteredFeatureSizeX - 5, 1, var10, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 1, scatteredFeatureSizeX - 5, 2, var10, par3StructureBoundingBox);
            }

            func_151550_a(par1World, Blocks.wool, var8, 10, 0, 7, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 10, 0, 8, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 9, 0, 9, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 11, 0, 9, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 8, 0, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 12, 0, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 7, 0, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 13, 0, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 9, 0, 11, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 11, 0, 11, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 10, 0, 12, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 10, 0, 13, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var9, 10, 0, 10, par3StructureBoundingBox);

            for (var10 = 0; var10 <= scatteredFeatureSizeX - 1; var10 += scatteredFeatureSizeX - 1)
            {
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 2, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 2, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 2, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 3, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 3, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 3, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 4, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 1, var10, 4, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 4, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 5, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 5, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 5, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 6, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 1, var10, 6, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 6, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 7, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 7, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 7, 3, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 8, 1, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 8, 2, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 8, 3, par3StructureBoundingBox);
            }

            for (var10 = 2; var10 <= scatteredFeatureSizeX - 3; var10 += scatteredFeatureSizeX - 3 - 2)
            {
                func_151550_a(par1World, Blocks.sandstone, 2, var10 - 1, 2, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 2, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10 + 1, 2, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10 - 1, 3, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 3, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10 + 1, 3, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10 - 1, 4, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 1, var10, 4, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10 + 1, 4, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10 - 1, 5, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 5, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10 + 1, 5, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10 - 1, 6, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 1, var10, 6, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10 + 1, 6, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10 - 1, 7, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10, 7, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.wool, var8, var10 + 1, 7, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10 - 1, 8, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10, 8, 0, par3StructureBoundingBox);
                func_151550_a(par1World, Blocks.sandstone, 2, var10 + 1, 8, 0, par3StructureBoundingBox);
            }

            func_151556_a(par1World, par3StructureBoundingBox, 8, 4, 0, 12, 6, 0, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151550_a(par1World, Blocks.air, 0, 8, 6, 0, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 12, 6, 0, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 9, 5, 0, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 1, 10, 5, 0, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.wool, var8, 11, 5, 0, par3StructureBoundingBox);
            func_151556_a(par1World, par3StructureBoundingBox, 8, -14, 8, 12, -11, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151556_a(par1World, par3StructureBoundingBox, 8, -10, 8, 12, -10, 12, Blocks.sandstone, 1, Blocks.sandstone, 1, false);
            func_151556_a(par1World, par3StructureBoundingBox, 8, -9, 8, 12, -9, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            func_151549_a(par1World, par3StructureBoundingBox, 8, -8, 8, 12, -1, 12, Blocks.sandstone, Blocks.sandstone, false);
            func_151549_a(par1World, par3StructureBoundingBox, 9, -11, 9, 11, -1, 11, Blocks.air, Blocks.air, false);
            func_151550_a(par1World, Blocks.stone_pressure_plate, 0, 10, -11, 10, par3StructureBoundingBox);
            func_151549_a(par1World, par3StructureBoundingBox, 9, -13, 9, 11, -13, 11, Blocks.tnt, Blocks.air, false);
            func_151550_a(par1World, Blocks.air, 0, 8, -11, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 8, -10, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 1, 7, -10, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 7, -11, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 12, -11, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 12, -10, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 1, 13, -10, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 13, -11, 10, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 10, -11, 8, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 10, -10, 8, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 1, 10, -10, 7, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 10, -11, 7, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 10, -11, 12, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.air, 0, 10, -10, 12, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 1, 10, -10, 13, par3StructureBoundingBox);
            func_151550_a(par1World, Blocks.sandstone, 2, 10, -11, 13, par3StructureBoundingBox);

            for (var10 = 0; var10 < 4; ++var10)
            {
                if (!field_74940_h[var10])
                {
                    int var11 = Direction.offsetX[var10] * 2;
                    int var12 = Direction.offsetZ[var10] * 2;
                    field_74940_h[var10] = generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 10 + var11, -11, 10 + var12, WeightedRandomChestContent.func_92080_a(itemsToGenerateInTemple, new WeightedRandomChestContent[] {Items.enchanted_book.func_92114_b(par2Random)}), 2 + par2Random.nextInt(5));
                }
            }

            return true;
        }
    }
}
