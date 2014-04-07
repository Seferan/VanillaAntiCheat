package net.minecraft.entity.item;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity
{
    private Block field_145811_e;
    public int field_145814_a;
    public int field_145812_b;
    public boolean field_145813_c;
    private boolean field_145808_f;
    private boolean field_145809_g;
    private int field_145815_h;
    private float field_145816_i;
    public NBTTagCompound field_145810_d;
    private static final String __OBFID = "CL_00001668";

    public EntityFallingBlock(World par1World)
    {
        super(par1World);
        field_145813_c = true;
        field_145815_h = 40;
        field_145816_i = 2.0F;
    }

    public EntityFallingBlock(World p_i45318_1_, double p_i45318_2_, double p_i45318_4_, double p_i45318_6_, Block p_i45318_8_)
    {
        this(p_i45318_1_, p_i45318_2_, p_i45318_4_, p_i45318_6_, p_i45318_8_, 0);
    }

    public EntityFallingBlock(World p_i45319_1_, double p_i45319_2_, double p_i45319_4_, double p_i45319_6_, Block p_i45319_8_, int p_i45319_9_)
    {
        super(p_i45319_1_);
        field_145813_c = true;
        field_145815_h = 40;
        field_145816_i = 2.0F;
        field_145811_e = p_i45319_8_;
        field_145814_a = p_i45319_9_;
        preventEntitySpawning = true;
        setSize(0.98F, 0.98F);
        yOffset = height / 2.0F;
        setPosition(p_i45319_2_, p_i45319_4_, p_i45319_6_);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = p_i45319_2_;
        prevPosY = p_i45319_4_;
        prevPosZ = p_i45319_6_;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
    }

    /**
     * Returns true if other Entities should be prevented from moving through
     * this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (field_145811_e.getMaterial() == Material.air)
        {
            setDead();
        }
        else
        {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            ++field_145812_b;
            motionY -= 0.03999999910593033D;
            moveEntity(motionX, motionY, motionZ);
            motionX *= 0.9800000190734863D;
            motionY *= 0.9800000190734863D;
            motionZ *= 0.9800000190734863D;

            if (!worldObj.isClient)
            {
                int var1 = MathHelper.floor_double(posX);
                int var2 = MathHelper.floor_double(posY);
                int var3 = MathHelper.floor_double(posZ);

                if (field_145812_b == 1)
                {
                    if (worldObj.getBlock(var1, var2, var3) != field_145811_e)
                    {
                        setDead();
                        return;
                    }

                    worldObj.setBlockToAir(var1, var2, var3);
                }

                if (onGround)
                {
                    motionX *= 0.699999988079071D;
                    motionZ *= 0.699999988079071D;
                    motionY *= -0.5D;

                    if (worldObj.getBlock(var1, var2, var3) != Blocks.piston_extension)
                    {
                        setDead();

                        if (!field_145808_f && worldObj.func_147472_a(field_145811_e, var1, var2, var3, true, 1, (Entity)null, (ItemStack)null) && !BlockFalling.func_149831_e(worldObj, var1, var2 - 1, var3) && worldObj.setBlock(var1, var2, var3, field_145811_e, field_145814_a, 3))
                        {
                            if (field_145811_e instanceof BlockFalling)
                            {
                                ((BlockFalling)field_145811_e).func_149828_a(worldObj, var1, var2, var3, field_145814_a);
                            }

                            if (field_145810_d != null && field_145811_e instanceof ITileEntityProvider)
                            {
                                TileEntity var4 = worldObj.getTileEntity(var1, var2, var3);

                                if (var4 != null)
                                {
                                    NBTTagCompound var5 = new NBTTagCompound();
                                    var4.writeToNBT(var5);
                                    Iterator var6 = field_145810_d.func_150296_c().iterator();

                                    while (var6.hasNext())
                                    {
                                        String var7 = (String)var6.next();
                                        NBTBase var8 = field_145810_d.getTag(var7);

                                        if (!var7.equals("x") && !var7.equals("y") && !var7.equals("z"))
                                        {
                                            var5.setTag(var7, var8.copy());
                                        }
                                    }

                                    var4.readFromNBT(var5);
                                    var4.onInventoryChanged();
                                }
                            }
                        }
                        else if (field_145813_c && !field_145808_f)
                        {
                            entityDropItem(new ItemStack(field_145811_e, 1, field_145811_e.damageDropped(field_145814_a)), 0.0F);
                        }
                    }
                }
                else if (field_145812_b > 100 && !worldObj.isClient && (var2 < 1 || var2 > 256) || field_145812_b > 600)
                {
                    if (field_145813_c)
                    {
                        entityDropItem(new ItemStack(field_145811_e, 1, field_145811_e.damageDropped(field_145814_a)), 0.0F);
                    }

                    setDead();
                }
            }
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        if (field_145809_g)
        {
            int var2 = MathHelper.ceiling_float_int(par1 - 1.0F);

            if (var2 > 0)
            {
                ArrayList var3 = new ArrayList(worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox));
                boolean var4 = field_145811_e == Blocks.anvil;
                DamageSource var5 = var4 ? DamageSource.anvil : DamageSource.fallingBlock;
                Iterator var6 = var3.iterator();

                while (var6.hasNext())
                {
                    Entity var7 = (Entity)var6.next();
                    var7.attackEntityFrom(var5, Math.min(MathHelper.floor_float(var2 * field_145816_i), field_145815_h));
                }

                if (var4 && rand.nextFloat() < 0.05000000074505806D + var2 * 0.05D)
                {
                    int var8 = field_145814_a >> 2;
                    int var9 = field_145814_a & 3;
                    ++var8;

                    if (var8 > 2)
                    {
                        field_145808_f = true;
                    }
                    else
                    {
                        field_145814_a = var9 | var8 << 2;
                    }
                }
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Tile", (byte)Block.getIdFromBlock(field_145811_e));
        par1NBTTagCompound.setInteger("TileID", Block.getIdFromBlock(field_145811_e));
        par1NBTTagCompound.setByte("Data", (byte)field_145814_a);
        par1NBTTagCompound.setByte("Time", (byte)field_145812_b);
        par1NBTTagCompound.setBoolean("DropItem", field_145813_c);
        par1NBTTagCompound.setBoolean("HurtEntities", field_145809_g);
        par1NBTTagCompound.setFloat("FallHurtAmount", field_145816_i);
        par1NBTTagCompound.setInteger("FallHurtMax", field_145815_h);

        if (field_145810_d != null)
        {
            par1NBTTagCompound.setTag("TileEntityData", field_145810_d);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.func_150297_b("TileID", 99))
        {
            field_145811_e = Block.getBlockById(par1NBTTagCompound.getInteger("TileID"));
        }
        else
        {
            field_145811_e = Block.getBlockById(par1NBTTagCompound.getByte("Tile") & 255);
        }

        field_145814_a = par1NBTTagCompound.getByte("Data") & 255;
        field_145812_b = par1NBTTagCompound.getByte("Time") & 255;

        if (par1NBTTagCompound.func_150297_b("HurtEntities", 99))
        {
            field_145809_g = par1NBTTagCompound.getBoolean("HurtEntities");
            field_145816_i = par1NBTTagCompound.getFloat("FallHurtAmount");
            field_145815_h = par1NBTTagCompound.getInteger("FallHurtMax");
        }
        else if (field_145811_e == Blocks.anvil)
        {
            field_145809_g = true;
        }

        if (par1NBTTagCompound.func_150297_b("DropItem", 99))
        {
            field_145813_c = par1NBTTagCompound.getBoolean("DropItem");
        }

        if (par1NBTTagCompound.func_150297_b("TileEntityData", 10))
        {
            field_145810_d = par1NBTTagCompound.getCompoundTag("TileEntityData");
        }

        if (field_145811_e.getMaterial() == Material.air)
        {
            field_145811_e = Blocks.sand;
        }
    }

    public void func_145806_a(boolean p_145806_1_)
    {
        field_145809_g = p_145806_1_;
    }

    public void addEntityCrashInfo(CrashReportCategory par1CrashReportCategory)
    {
        super.addEntityCrashInfo(par1CrashReportCategory);
        par1CrashReportCategory.addCrashSection("Immitating block ID", Integer.valueOf(Block.getIdFromBlock(field_145811_e)));
        par1CrashReportCategory.addCrashSection("Immitating block data", Integer.valueOf(field_145814_a));
    }

    public Block func_145805_f()
    {
        return field_145811_e;
    }
}
