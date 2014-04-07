package net.minecraft.entity.projectile;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityFishHook extends Entity
{
    private static final List field_146039_d = Arrays.asList(new WeightedRandomFishable[] {(new WeightedRandomFishable(new ItemStack(Items.leather_boots), 10)).func_150709_a(0.9F), new WeightedRandomFishable(new ItemStack(Items.leather), 10), new WeightedRandomFishable(new ItemStack(Items.bone), 10), new WeightedRandomFishable(new ItemStack(Items.potionitem), 10), new WeightedRandomFishable(new ItemStack(Items.string), 5), (new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 2)).func_150709_a(0.9F), new WeightedRandomFishable(new ItemStack(Items.bowl), 10), new WeightedRandomFishable(new ItemStack(Items.stick), 5), new WeightedRandomFishable(new ItemStack(Items.dye, 10, 0), 1), new WeightedRandomFishable(new ItemStack(Blocks.tripwire_hook), 10), new WeightedRandomFishable(new ItemStack(Items.rotten_flesh), 10)});
    private static final List field_146041_e = Arrays.asList(new WeightedRandomFishable[] {new WeightedRandomFishable(new ItemStack(Blocks.waterlily), 1), new WeightedRandomFishable(new ItemStack(Items.name_tag), 1), new WeightedRandomFishable(new ItemStack(Items.saddle), 1), (new WeightedRandomFishable(new ItemStack(Items.bow), 1)).func_150709_a(0.25F).func_150707_a(), (new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1)).func_150709_a(0.25F).func_150707_a(), (new WeightedRandomFishable(new ItemStack(Items.book), 1)).func_150707_a()});
    private static final List field_146036_f = Arrays.asList(new WeightedRandomFishable[] {new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.COD.func_150976_a()), 60), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.SALMON.func_150976_a()), 25), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.CLOWNFISH.func_150976_a()), 2), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.PUFFERFISH.func_150976_a()), 13)});
    private int field_146037_g = -1;
    private int field_146048_h = -1;
    private int field_146050_i = -1;
    private Block field_146046_j;
    private boolean field_146051_au;
    public int field_146044_a;
    public EntityPlayer field_146042_b;
    private int field_146049_av;
    private int field_146047_aw;
    private int field_146045_ax;
    private int field_146040_ay;
    private int field_146038_az;
    private float field_146054_aA;
    public Entity field_146043_c;
    private int field_146055_aB;
    private double field_146056_aC;
    private double field_146057_aD;
    private double field_146058_aE;
    private double field_146059_aF;
    private double field_146060_aG;
    private static final String __OBFID = "CL_00001663";

    public EntityFishHook(World par1World)
    {
        super(par1World);
        setSize(0.25F, 0.25F);
        ignoreFrustumCheck = true;
    }

    public EntityFishHook(World par1World, EntityPlayer par2EntityPlayer)
    {
        super(par1World);
        ignoreFrustumCheck = true;
        field_146042_b = par2EntityPlayer;
        field_146042_b.fishEntity = this;
        setSize(0.25F, 0.25F);
        setLocationAndAngles(par2EntityPlayer.posX, par2EntityPlayer.posY + 1.62D - par2EntityPlayer.yOffset, par2EntityPlayer.posZ, par2EntityPlayer.rotationYaw, par2EntityPlayer.rotationPitch);
        posX -= MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        posY -= 0.10000000149011612D;
        posZ -= MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        float var3 = 0.4F;
        motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * var3;
        motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * var3;
        motionY = -MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI) * var3;
        func_146035_c(motionX, motionY, motionZ, 1.5F, 1.0F);
    }

    protected void entityInit()
    {
    }

    public void func_146035_c(double p_146035_1_, double p_146035_3_, double p_146035_5_, float p_146035_7_, float p_146035_8_)
    {
        float var9 = MathHelper.sqrt_double(p_146035_1_ * p_146035_1_ + p_146035_3_ * p_146035_3_ + p_146035_5_ * p_146035_5_);
        p_146035_1_ /= var9;
        p_146035_3_ /= var9;
        p_146035_5_ /= var9;
        p_146035_1_ += rand.nextGaussian() * 0.007499999832361937D * p_146035_8_;
        p_146035_3_ += rand.nextGaussian() * 0.007499999832361937D * p_146035_8_;
        p_146035_5_ += rand.nextGaussian() * 0.007499999832361937D * p_146035_8_;
        p_146035_1_ *= p_146035_7_;
        p_146035_3_ *= p_146035_7_;
        p_146035_5_ *= p_146035_7_;
        motionX = p_146035_1_;
        motionY = p_146035_3_;
        motionZ = p_146035_5_;
        float var10 = MathHelper.sqrt_double(p_146035_1_ * p_146035_1_ + p_146035_5_ * p_146035_5_);
        prevRotationYaw = rotationYaw = (float)(Math.atan2(p_146035_1_, p_146035_5_) * 180.0D / Math.PI);
        prevRotationPitch = rotationPitch = (float)(Math.atan2(p_146035_3_, var10) * 180.0D / Math.PI);
        field_146049_av = 0;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (field_146055_aB > 0)
        {
            double var27 = posX + (field_146056_aC - posX) / field_146055_aB;
            double var28 = posY + (field_146057_aD - posY) / field_146055_aB;
            double var29 = posZ + (field_146058_aE - posZ) / field_146055_aB;
            double var7 = MathHelper.wrapAngleTo180_double(field_146059_aF - rotationYaw);
            rotationYaw = (float)(rotationYaw + var7 / field_146055_aB);
            rotationPitch = (float)(rotationPitch + (field_146060_aG - rotationPitch) / field_146055_aB);
            --field_146055_aB;
            setPosition(var27, var28, var29);
            setRotation(rotationYaw, rotationPitch);
        }
        else
        {
            if (!worldObj.isClient)
            {
                ItemStack var1 = field_146042_b.getCurrentEquippedItem();

                if (field_146042_b.isDead || !field_146042_b.isEntityAlive() || var1 == null || var1.getItem() != Items.fishing_rod || getDistanceSqToEntity(field_146042_b) > 1024.0D)
                {
                    setDead();
                    field_146042_b.fishEntity = null;
                    return;
                }

                if (field_146043_c != null)
                {
                    if (!field_146043_c.isDead)
                    {
                        posX = field_146043_c.posX;
                        posY = field_146043_c.boundingBox.minY + field_146043_c.height * 0.8D;
                        posZ = field_146043_c.posZ;
                        return;
                    }

                    field_146043_c = null;
                }
            }

            if (field_146044_a > 0)
            {
                --field_146044_a;
            }

            if (field_146051_au)
            {
                if (worldObj.getBlock(field_146037_g, field_146048_h, field_146050_i) == field_146046_j)
                {
                    ++field_146049_av;

                    if (field_146049_av == 1200)
                    {
                        setDead();
                    }

                    return;
                }

                field_146051_au = false;
                motionX *= rand.nextFloat() * 0.2F;
                motionY *= rand.nextFloat() * 0.2F;
                motionZ *= rand.nextFloat() * 0.2F;
                field_146049_av = 0;
                field_146047_aw = 0;
            }
            else
            {
                ++field_146047_aw;
            }

            Vec3 var26 = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
            Vec3 var2 = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);
            MovingObjectPosition var3 = worldObj.rayTraceBlocks(var26, var2);
            var26 = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
            var2 = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);

            if (var3 != null)
            {
                var2 = worldObj.getWorldVec3Pool().getVecFromPool(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
            }

            Entity var4 = null;
            List var5 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
            double var6 = 0.0D;
            double var13;

            for (int var8 = 0; var8 < var5.size(); ++var8)
            {
                Entity var9 = (Entity)var5.get(var8);

                if (var9.canBeCollidedWith() && (var9 != field_146042_b || field_146047_aw >= 5))
                {
                    float var10 = 0.3F;
                    AxisAlignedBB var11 = var9.boundingBox.expand(var10, var10, var10);
                    MovingObjectPosition var12 = var11.calculateIntercept(var26, var2);

                    if (var12 != null)
                    {
                        var13 = var26.distanceTo(var12.hitVec);

                        if (var13 < var6 || var6 == 0.0D)
                        {
                            var4 = var9;
                            var6 = var13;
                        }
                    }
                }
            }

            if (var4 != null)
            {
                var3 = new MovingObjectPosition(var4);
            }

            if (var3 != null)
            {
                if (var3.entityHit != null)
                {
                    if (var3.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, field_146042_b), 0.0F))
                    {
                        field_146043_c = var3.entityHit;
                    }
                }
                else
                {
                    field_146051_au = true;
                }
            }

            if (!field_146051_au)
            {
                moveEntity(motionX, motionY, motionZ);
                float var30 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
                rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

                for (rotationPitch = (float)(Math.atan2(motionY, var30) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
                {
                    ;
                }

                while (rotationPitch - prevRotationPitch >= 180.0F)
                {
                    prevRotationPitch += 360.0F;
                }

                while (rotationYaw - prevRotationYaw < -180.0F)
                {
                    prevRotationYaw -= 360.0F;
                }

                while (rotationYaw - prevRotationYaw >= 180.0F)
                {
                    prevRotationYaw += 360.0F;
                }

                rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
                rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
                float var31 = 0.92F;

                if (onGround || isCollidedHorizontally)
                {
                    var31 = 0.5F;
                }

                byte var33 = 5;
                double var32 = 0.0D;

                for (int var35 = 0; var35 < var33; ++var35)
                {
                    double var14 = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * (var35 + 0) / var33 - 0.125D + 0.125D;
                    double var16 = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * (var35 + 1) / var33 - 0.125D + 0.125D;
                    AxisAlignedBB var18 = AxisAlignedBB.getAABBPool().getAABB(boundingBox.minX, var14, boundingBox.minZ, boundingBox.maxX, var16, boundingBox.maxZ);

                    if (worldObj.isAABBInMaterial(var18, Material.field_151586_h))
                    {
                        var32 += 1.0D / var33;
                    }
                }

                if (!worldObj.isClient && var32 > 0.0D)
                {
                    WorldServer var34 = (WorldServer)worldObj;
                    int var36 = 1;

                    if (rand.nextFloat() < 0.25F && worldObj.canLightningStrikeAt(MathHelper.floor_double(posX), MathHelper.floor_double(posY) + 1, MathHelper.floor_double(posZ)))
                    {
                        var36 = 2;
                    }

                    if (rand.nextFloat() < 0.5F && !worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY) + 1, MathHelper.floor_double(posZ)))
                    {
                        --var36;
                    }

                    if (field_146045_ax > 0)
                    {
                        --field_146045_ax;

                        if (field_146045_ax <= 0)
                        {
                            field_146040_ay = 0;
                            field_146038_az = 0;
                        }
                    }
                    else
                    {
                        float var15;
                        float var17;
                        double var20;
                        double var22;
                        double var38;
                        float var37;

                        if (field_146038_az > 0)
                        {
                            field_146038_az -= var36;

                            if (field_146038_az <= 0)
                            {
                                motionY -= 0.20000000298023224D;
                                playSound("random.splash", 0.25F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
                                var15 = MathHelper.floor_double(boundingBox.minY);
                                var34.func_147487_a("bubble", posX, var15 + 1.0F, posZ, (int)(1.0F + width * 20.0F), width, 0.0D, width, 0.20000000298023224D);
                                var34.func_147487_a("wake", posX, var15 + 1.0F, posZ, (int)(1.0F + width * 20.0F), width, 0.0D, width, 0.20000000298023224D);
                                field_146045_ax = MathHelper.getRandomIntegerInRange(rand, 10, 30);
                            }
                            else
                            {
                                field_146054_aA = (float)(field_146054_aA + rand.nextGaussian() * 4.0D);
                                var15 = field_146054_aA * 0.017453292F;
                                var37 = MathHelper.sin(var15);
                                var17 = MathHelper.cos(var15);
                                var38 = posX + var37 * field_146038_az * 0.1F;
                                var20 = MathHelper.floor_double(boundingBox.minY) + 1.0F;
                                var22 = posZ + var17 * field_146038_az * 0.1F;

                                if (rand.nextFloat() < 0.15F)
                                {
                                    var34.func_147487_a("bubble", var38, var20 - 0.10000000149011612D, var22, 1, var37, 0.1D, var17, 0.0D);
                                }

                                float var24 = var37 * 0.04F;
                                float var25 = var17 * 0.04F;
                                var34.func_147487_a("wake", var38, var20, var22, 0, var25, 0.01D, (-var24), 1.0D);
                                var34.func_147487_a("wake", var38, var20, var22, 0, (-var25), 0.01D, var24, 1.0D);
                            }
                        }
                        else if (field_146040_ay > 0)
                        {
                            field_146040_ay -= var36;
                            var15 = 0.15F;

                            if (field_146040_ay < 20)
                            {
                                var15 = (float)(var15 + (20 - field_146040_ay) * 0.05D);
                            }
                            else if (field_146040_ay < 40)
                            {
                                var15 = (float)(var15 + (40 - field_146040_ay) * 0.02D);
                            }
                            else if (field_146040_ay < 60)
                            {
                                var15 = (float)(var15 + (60 - field_146040_ay) * 0.01D);
                            }

                            if (rand.nextFloat() < var15)
                            {
                                var37 = MathHelper.func_151240_a(rand, 0.0F, 360.0F) * 0.017453292F;
                                var17 = MathHelper.func_151240_a(rand, 25.0F, 60.0F);
                                var38 = posX + MathHelper.sin(var37) * var17 * 0.1F;
                                var20 = MathHelper.floor_double(boundingBox.minY) + 1.0F;
                                var22 = posZ + MathHelper.cos(var37) * var17 * 0.1F;
                                var34.func_147487_a("splash", var38, var20, var22, 2 + rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                            }

                            if (field_146040_ay <= 0)
                            {
                                field_146054_aA = MathHelper.func_151240_a(rand, 0.0F, 360.0F);
                                field_146038_az = MathHelper.getRandomIntegerInRange(rand, 20, 80);
                            }
                        }
                        else
                        {
                            field_146040_ay = MathHelper.getRandomIntegerInRange(rand, 100, 900);
                            field_146040_ay -= EnchantmentHelper.func_151387_h(field_146042_b) * 20 * 5;
                        }
                    }

                    if (field_146045_ax > 0)
                    {
                        motionY -= rand.nextFloat() * rand.nextFloat() * rand.nextFloat() * 0.2D;
                    }
                }

                var13 = var32 * 2.0D - 1.0D;
                motionY += 0.03999999910593033D * var13;

                if (var32 > 0.0D)
                {
                    var31 = (float)(var31 * 0.9D);
                    motionY *= 0.8D;
                }

                motionX *= var31;
                motionY *= var31;
                motionZ *= var31;
                setPosition(posX, posY, posZ);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short)field_146037_g);
        par1NBTTagCompound.setShort("yTile", (short)field_146048_h);
        par1NBTTagCompound.setShort("zTile", (short)field_146050_i);
        par1NBTTagCompound.setByte("inTile", (byte)Block.getIdFromBlock(field_146046_j));
        par1NBTTagCompound.setByte("shake", (byte)field_146044_a);
        par1NBTTagCompound.setByte("inGround", (byte)(field_146051_au ? 1 : 0));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        field_146037_g = par1NBTTagCompound.getShort("xTile");
        field_146048_h = par1NBTTagCompound.getShort("yTile");
        field_146050_i = par1NBTTagCompound.getShort("zTile");
        field_146046_j = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 255);
        field_146044_a = par1NBTTagCompound.getByte("shake") & 255;
        field_146051_au = par1NBTTagCompound.getByte("inGround") == 1;
    }

    public int func_146034_e()
    {
        if (worldObj.isClient)
        {
            return 0;
        }
        else
        {
            byte var1 = 0;

            if (field_146043_c != null)
            {
                double var2 = field_146042_b.posX - posX;
                double var4 = field_146042_b.posY - posY;
                double var6 = field_146042_b.posZ - posZ;
                double var8 = MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
                double var10 = 0.1D;
                field_146043_c.motionX += var2 * var10;
                field_146043_c.motionY += var4 * var10 + MathHelper.sqrt_double(var8) * 0.08D;
                field_146043_c.motionZ += var6 * var10;
                var1 = 3;
            }
            else if (field_146045_ax > 0)
            {
                EntityItem var13 = new EntityItem(worldObj, posX, posY, posZ, func_146033_f());
                double var3 = field_146042_b.posX - posX;
                double var5 = field_146042_b.posY - posY;
                double var7 = field_146042_b.posZ - posZ;
                double var9 = MathHelper.sqrt_double(var3 * var3 + var5 * var5 + var7 * var7);
                double var11 = 0.1D;
                var13.motionX = var3 * var11;
                var13.motionY = var5 * var11 + MathHelper.sqrt_double(var9) * 0.08D;
                var13.motionZ = var7 * var11;
                worldObj.spawnEntityInWorld(var13);
                field_146042_b.worldObj.spawnEntityInWorld(new EntityXPOrb(field_146042_b.worldObj, field_146042_b.posX, field_146042_b.posY + 0.5D, field_146042_b.posZ + 0.5D, rand.nextInt(6) + 1));
                var1 = 1;
            }

            if (field_146051_au)
            {
                var1 = 2;
            }

            setDead();
            field_146042_b.fishEntity = null;
            return var1;
        }
    }

    private ItemStack func_146033_f()
    {
        float var1 = worldObj.rand.nextFloat();
        int var2 = EnchantmentHelper.func_151386_g(field_146042_b);
        int var3 = EnchantmentHelper.func_151387_h(field_146042_b);
        float var4 = 0.1F - var2 * 0.025F - var3 * 0.01F;
        float var5 = 0.05F + var2 * 0.01F - var3 * 0.01F;
        var4 = MathHelper.clamp_float(var4, 0.0F, 1.0F);
        var5 = MathHelper.clamp_float(var5, 0.0F, 1.0F);

        if (var1 < var4)
        {
            field_146042_b.addStat(StatList.field_151183_A, 1);
            return ((WeightedRandomFishable)WeightedRandom.getRandomItem(rand, field_146039_d)).func_150708_a(rand);
        }
        else
        {
            var1 -= var4;

            if (var1 < var5)
            {
                field_146042_b.addStat(StatList.field_151184_B, 1);
                return ((WeightedRandomFishable)WeightedRandom.getRandomItem(rand, field_146041_e)).func_150708_a(rand);
            }
            else
            {
                float var10000 = var1 - var5;
                field_146042_b.addStat(StatList.fishCaughtStat, 1);
                return ((WeightedRandomFishable)WeightedRandom.getRandomItem(rand, field_146036_f)).func_150708_a(rand);
            }
        }
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        super.setDead();

        if (field_146042_b != null)
        {
            field_146042_b.fishEntity = null;
        }
    }
}
