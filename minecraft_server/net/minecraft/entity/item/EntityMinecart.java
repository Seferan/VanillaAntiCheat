package net.minecraft.entity.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart extends Entity
{
    private boolean isInReverse;
    private String entityName;

    /** Minecart rotational logic matrix */
    private static final int[][][] matrix = new int[][][] { { {0, 0, -1}, {0, 0, 1}}, { {-1, 0, 0}, {1, 0, 0}}, { {-1, -1, 0}, {1, 0, 0}}, { {-1, 0, 0}, {1, -1, 0}}, { {0, 0, -1}, {0, -1, 1}}, { {0, -1, -1}, {0, 0, 1}}, { {0, 0, 1}, {1, 0, 0}}, { {0, 0, 1}, {-1, 0, 0}}, { {0, 0, -1}, {-1, 0, 0}}, { {0, 0, -1}, {1, 0, 0}}};

    /** appears to be the progress of the turn */
    private int turnProgress;
    private double minecartX;
    private double minecartY;
    private double minecartZ;
    private double minecartYaw;
    private double minecartPitch;
    private static final String __OBFID = "CL_00001670";

    public EntityMinecart(World par1World)
    {
        super(par1World);
        preventEntitySpawning = true;
        setSize(0.98F, 0.7F);
        yOffset = height / 2.0F;
    }

    /**
     * Creates a new minecart of the specified type in the specified location in
     * the given world. par0World - world to create the minecart in, double
     * par1,par3,par5 represent x,y,z respectively. int par7 specifies the type:
     * 1 for MinecartChest, 2 for MinecartFurnace, 3 for MinecartTNT, 4 for
     * MinecartMobSpawner, 5 for MinecartHopper and 0 for a standard empty
     * minecart
     */
    public static EntityMinecart createMinecart(World par0World, double par1, double par3, double par5, int par7)
    {
        switch (par7)
        {
        case 1:
            return new EntityMinecartChest(par0World, par1, par3, par5);

        case 2:
            return new EntityMinecartFurnace(par0World, par1, par3, par5);

        case 3:
            return new EntityMinecartTNT(par0World, par1, par3, par5);

        case 4:
            return new EntityMinecartMobSpawner(par0World, par1, par3, par5);

        case 5:
            return new EntityMinecartHopper(par0World, par1, par3, par5);

        case 6:
            return new EntityMinecartCommandBlock(par0World, par1, par3, par5);

        default:
            return new EntityMinecartEmpty(par0World, par1, par3, par5);
        }
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
        dataWatcher.addObject(17, new Integer(0));
        dataWatcher.addObject(18, new Integer(1));
        dataWatcher.addObject(19, new Float(0.0F));
        dataWatcher.addObject(20, new Integer(0));
        dataWatcher.addObject(21, new Integer(6));
        dataWatcher.addObject(22, Byte.valueOf((byte)0));
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and
     * blocks. This enables the entity to be pushable on contact, like boats or
     * minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return par1Entity.canBePushed() ? par1Entity.boundingBox : null;
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return null;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities
     * when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }

    public EntityMinecart(World par1World, double par2, double par4, double par6)
    {
        this(par1World);
        setPosition(par2, par4, par6);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = par2;
        prevPosY = par4;
        prevPosZ = par6;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding
     * this one.
     */
    public double getMountedYOffset()
    {
        return height * 0.0D - 0.30000001192092896D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (!worldObj.isClient && !isDead)
        {
            if (isEntityInvulnerable())
            {
                return false;
            }
            else
            {
                setRollingDirection(-getRollingDirection());
                setRollingAmplitude(10);
                setBeenAttacked();
                setDamage(getDamage() + par2 * 10.0F);
                boolean var3 = par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode;

                if (var3 || getDamage() > 40.0F)
                {
                    if (riddenByEntity != null)
                    {
                        riddenByEntity.mountEntity(this);
                    }

                    if (var3 && !isInventoryNameLocalized())
                    {
                        setDead();
                    }
                    else
                    {
                        killMinecart(par1DamageSource);
                    }
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    public void killMinecart(DamageSource par1DamageSource)
    {
        setDead();
        ItemStack var2 = new ItemStack(Items.minecart, 1);

        if (entityName != null)
        {
            var2.setStackDisplayName(entityName);
        }

        entityDropItem(var2, 0.0F);
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
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        super.setDead();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (getRollingAmplitude() > 0)
        {
            setRollingAmplitude(getRollingAmplitude() - 1);
        }

        if (getDamage() > 0.0F)
        {
            setDamage(getDamage() - 1.0F);
        }

        if (posY < -64.0D)
        {
            kill();
        }

        int var2;

        if (!worldObj.isClient && worldObj instanceof WorldServer)
        {
            worldObj.theProfiler.startSection("portal");
            MinecraftServer var1 = ((WorldServer)worldObj).func_73046_m();
            var2 = getMaxInPortalTime();

            if (inPortal)
            {
                if (var1.getAllowNether())
                {
                    if (ridingEntity == null && portalCounter++ >= var2)
                    {
                        portalCounter = var2;
                        timeUntilPortal = getPortalCooldown();
                        byte var3;

                        if (worldObj.provider.dimensionId == -1)
                        {
                            var3 = 0;
                        }
                        else
                        {
                            var3 = -1;
                        }

                        travelToDimension(var3);
                    }

                    inPortal = false;
                }
            }
            else
            {
                if (portalCounter > 0)
                {
                    portalCounter -= 4;
                }

                if (portalCounter < 0)
                {
                    portalCounter = 0;
                }
            }

            if (timeUntilPortal > 0)
            {
                --timeUntilPortal;
            }

            worldObj.theProfiler.endSection();
        }

        if (worldObj.isClient)
        {
            if (turnProgress > 0)
            {
                double var19 = posX + (minecartX - posX) / turnProgress;
                double var21 = posY + (minecartY - posY) / turnProgress;
                double var5 = posZ + (minecartZ - posZ) / turnProgress;
                double var7 = MathHelper.wrapAngleTo180_double(minecartYaw - rotationYaw);
                rotationYaw = (float)(rotationYaw + var7 / turnProgress);
                rotationPitch = (float)(rotationPitch + (minecartPitch - rotationPitch) / turnProgress);
                --turnProgress;
                setPosition(var19, var21, var5);
                setRotation(rotationYaw, rotationPitch);
            }
            else
            {
                setPosition(posX, posY, posZ);
                setRotation(rotationYaw, rotationPitch);
            }
        }
        else
        {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            motionY -= 0.03999999910593033D;
            int var18 = MathHelper.floor_double(posX);
            var2 = MathHelper.floor_double(posY);
            int var20 = MathHelper.floor_double(posZ);

            if (BlockRailBase.func_150049_b_(worldObj, var18, var2 - 1, var20))
            {
                --var2;
            }

            double var4 = 0.4D;
            double var6 = 0.0078125D;
            Block var8 = worldObj.getBlock(var18, var2, var20);

            if (BlockRailBase.func_150051_a(var8))
            {
                int var9 = worldObj.getBlockMetadata(var18, var2, var20);
                func_145821_a(var18, var2, var20, var4, var6, var8, var9);

                if (var8 == Blocks.activator_rail)
                {
                    onActivatorRailPass(var18, var2, var20, (var9 & 8) != 0);
                }
            }
            else
            {
                func_94088_b(var4);
            }

            func_145775_I();
            rotationPitch = 0.0F;
            double var22 = prevPosX - posX;
            double var11 = prevPosZ - posZ;

            if (var22 * var22 + var11 * var11 > 0.001D)
            {
                rotationYaw = (float)(Math.atan2(var11, var22) * 180.0D / Math.PI);

                if (isInReverse)
                {
                    rotationYaw += 180.0F;
                }
            }

            double var13 = MathHelper.wrapAngleTo180_float(rotationYaw - prevRotationYaw);

            if (var13 < -170.0D || var13 >= 170.0D)
            {
                rotationYaw += 180.0F;
                isInReverse = !isInReverse;
            }

            setRotation(rotationYaw, rotationPitch);
            List var15 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

            if (var15 != null && !var15.isEmpty())
            {
                for (int var16 = 0; var16 < var15.size(); ++var16)
                {
                    Entity var17 = (Entity)var15.get(var16);

                    if (var17 != riddenByEntity && var17.canBePushed() && var17 instanceof EntityMinecart)
                    {
                        var17.applyEntityCollision(this);
                    }
                }
            }

            if (riddenByEntity != null && riddenByEntity.isDead)
            {
                if (riddenByEntity.ridingEntity == this)
                {
                    riddenByEntity.ridingEntity = null;
                }

                riddenByEntity = null;
            }
        }
    }

    /**
     * Called every tick the minecart is on an activator rail. Args: x, y, z, is
     * the rail receiving power
     */
    public void onActivatorRailPass(int par1, int par2, int par3, boolean par4)
    {
    }

    protected void func_94088_b(double par1)
    {
        if (motionX < -par1)
        {
            motionX = -par1;
        }

        if (motionX > par1)
        {
            motionX = par1;
        }

        if (motionZ < -par1)
        {
            motionZ = -par1;
        }

        if (motionZ > par1)
        {
            motionZ = par1;
        }

        if (onGround)
        {
            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
        }

        moveEntity(motionX, motionY, motionZ);

        if (!onGround)
        {
            motionX *= 0.949999988079071D;
            motionY *= 0.949999988079071D;
            motionZ *= 0.949999988079071D;
        }
    }

    protected void func_145821_a(int p_145821_1_, int p_145821_2_, int p_145821_3_, double p_145821_4_, double p_145821_6_, Block p_145821_8_, int p_145821_9_)
    {
        fallDistance = 0.0F;
        Vec3 var10 = func_70489_a(posX, posY, posZ);
        posY = p_145821_2_;
        boolean var11 = false;
        boolean var12 = false;

        if (p_145821_8_ == Blocks.golden_rail)
        {
            var11 = (p_145821_9_ & 8) != 0;
            var12 = !var11;
        }

        if (((BlockRailBase)p_145821_8_).func_150050_e())
        {
            p_145821_9_ &= 7;
        }

        if (p_145821_9_ >= 2 && p_145821_9_ <= 5)
        {
            posY = p_145821_2_ + 1;
        }

        if (p_145821_9_ == 2)
        {
            motionX -= p_145821_6_;
        }

        if (p_145821_9_ == 3)
        {
            motionX += p_145821_6_;
        }

        if (p_145821_9_ == 4)
        {
            motionZ += p_145821_6_;
        }

        if (p_145821_9_ == 5)
        {
            motionZ -= p_145821_6_;
        }

        int[][] var13 = matrix[p_145821_9_];
        double var14 = var13[1][0] - var13[0][0];
        double var16 = var13[1][2] - var13[0][2];
        double var18 = Math.sqrt(var14 * var14 + var16 * var16);
        double var20 = motionX * var14 + motionZ * var16;

        if (var20 < 0.0D)
        {
            var14 = -var14;
            var16 = -var16;
        }

        double var22 = Math.sqrt(motionX * motionX + motionZ * motionZ);

        if (var22 > 2.0D)
        {
            var22 = 2.0D;
        }

        motionX = var22 * var14 / var18;
        motionZ = var22 * var16 / var18;
        double var24;
        double var26;
        double var28;
        double var30;

        if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase)
        {
            var24 = ((EntityLivingBase)riddenByEntity).moveForward;

            if (var24 > 0.0D)
            {
                var26 = -Math.sin(riddenByEntity.rotationYaw * (float)Math.PI / 180.0F);
                var28 = Math.cos(riddenByEntity.rotationYaw * (float)Math.PI / 180.0F);
                var30 = motionX * motionX + motionZ * motionZ;

                if (var30 < 0.01D)
                {
                    motionX += var26 * 0.1D;
                    motionZ += var28 * 0.1D;
                    var12 = false;
                }
            }
        }

        if (var12)
        {
            var24 = Math.sqrt(motionX * motionX + motionZ * motionZ);

            if (var24 < 0.03D)
            {
                motionX *= 0.0D;
                motionY *= 0.0D;
                motionZ *= 0.0D;
            }
            else
            {
                motionX *= 0.5D;
                motionY *= 0.0D;
                motionZ *= 0.5D;
            }
        }

        var24 = 0.0D;
        var26 = p_145821_1_ + 0.5D + var13[0][0] * 0.5D;
        var28 = p_145821_3_ + 0.5D + var13[0][2] * 0.5D;
        var30 = p_145821_1_ + 0.5D + var13[1][0] * 0.5D;
        double var32 = p_145821_3_ + 0.5D + var13[1][2] * 0.5D;
        var14 = var30 - var26;
        var16 = var32 - var28;
        double var34;
        double var36;

        if (var14 == 0.0D)
        {
            posX = p_145821_1_ + 0.5D;
            var24 = posZ - p_145821_3_;
        }
        else if (var16 == 0.0D)
        {
            posZ = p_145821_3_ + 0.5D;
            var24 = posX - p_145821_1_;
        }
        else
        {
            var34 = posX - var26;
            var36 = posZ - var28;
            var24 = (var34 * var14 + var36 * var16) * 2.0D;
        }

        posX = var26 + var14 * var24;
        posZ = var28 + var16 * var24;
        setPosition(posX, posY + yOffset, posZ);
        var34 = motionX;
        var36 = motionZ;

        if (riddenByEntity != null)
        {
            var34 *= 0.75D;
            var36 *= 0.75D;
        }

        if (var34 < -p_145821_4_)
        {
            var34 = -p_145821_4_;
        }

        if (var34 > p_145821_4_)
        {
            var34 = p_145821_4_;
        }

        if (var36 < -p_145821_4_)
        {
            var36 = -p_145821_4_;
        }

        if (var36 > p_145821_4_)
        {
            var36 = p_145821_4_;
        }

        moveEntity(var34, 0.0D, var36);

        if (var13[0][1] != 0 && MathHelper.floor_double(posX) - p_145821_1_ == var13[0][0] && MathHelper.floor_double(posZ) - p_145821_3_ == var13[0][2])
        {
            setPosition(posX, posY + var13[0][1], posZ);
        }
        else if (var13[1][1] != 0 && MathHelper.floor_double(posX) - p_145821_1_ == var13[1][0] && MathHelper.floor_double(posZ) - p_145821_3_ == var13[1][2])
        {
            setPosition(posX, posY + var13[1][1], posZ);
        }

        applyDrag();
        Vec3 var38 = func_70489_a(posX, posY, posZ);

        if (var38 != null && var10 != null)
        {
            double var39 = (var10.yCoord - var38.yCoord) * 0.05D;
            var22 = Math.sqrt(motionX * motionX + motionZ * motionZ);

            if (var22 > 0.0D)
            {
                motionX = motionX / var22 * (var22 + var39);
                motionZ = motionZ / var22 * (var22 + var39);
            }

            setPosition(posX, var38.yCoord, posZ);
        }

        int var45 = MathHelper.floor_double(posX);
        int var40 = MathHelper.floor_double(posZ);

        if (var45 != p_145821_1_ || var40 != p_145821_3_)
        {
            var22 = Math.sqrt(motionX * motionX + motionZ * motionZ);
            motionX = var22 * (var45 - p_145821_1_);
            motionZ = var22 * (var40 - p_145821_3_);
        }

        if (var11)
        {
            double var41 = Math.sqrt(motionX * motionX + motionZ * motionZ);

            if (var41 > 0.01D)
            {
                double var43 = 0.06D;
                motionX += motionX / var41 * var43;
                motionZ += motionZ / var41 * var43;
            }
            else if (p_145821_9_ == 1)
            {
                if (worldObj.getBlock(p_145821_1_ - 1, p_145821_2_, p_145821_3_).isNormalCube())
                {
                    motionX = 0.02D;
                }
                else if (worldObj.getBlock(p_145821_1_ + 1, p_145821_2_, p_145821_3_).isNormalCube())
                {
                    motionX = -0.02D;
                }
            }
            else if (p_145821_9_ == 0)
            {
                if (worldObj.getBlock(p_145821_1_, p_145821_2_, p_145821_3_ - 1).isNormalCube())
                {
                    motionZ = 0.02D;
                }
                else if (worldObj.getBlock(p_145821_1_, p_145821_2_, p_145821_3_ + 1).isNormalCube())
                {
                    motionZ = -0.02D;
                }
            }
        }
    }

    protected void applyDrag()
    {
        if (riddenByEntity != null)
        {
            motionX *= 0.996999979019165D;
            motionY *= 0.0D;
            motionZ *= 0.996999979019165D;
        }
        else
        {
            motionX *= 0.9599999785423279D;
            motionY *= 0.0D;
            motionZ *= 0.9599999785423279D;
        }
    }

    public Vec3 func_70489_a(double par1, double par3, double par5)
    {
        int var7 = MathHelper.floor_double(par1);
        int var8 = MathHelper.floor_double(par3);
        int var9 = MathHelper.floor_double(par5);

        if (BlockRailBase.func_150049_b_(worldObj, var7, var8 - 1, var9))
        {
            --var8;
        }

        Block var10 = worldObj.getBlock(var7, var8, var9);

        if (BlockRailBase.func_150051_a(var10))
        {
            int var11 = worldObj.getBlockMetadata(var7, var8, var9);
            par3 = var8;

            if (((BlockRailBase)var10).func_150050_e())
            {
                var11 &= 7;
            }

            if (var11 >= 2 && var11 <= 5)
            {
                par3 = var8 + 1;
            }

            int[][] var12 = matrix[var11];
            double var13 = 0.0D;
            double var15 = var7 + 0.5D + var12[0][0] * 0.5D;
            double var17 = var8 + 0.5D + var12[0][1] * 0.5D;
            double var19 = var9 + 0.5D + var12[0][2] * 0.5D;
            double var21 = var7 + 0.5D + var12[1][0] * 0.5D;
            double var23 = var8 + 0.5D + var12[1][1] * 0.5D;
            double var25 = var9 + 0.5D + var12[1][2] * 0.5D;
            double var27 = var21 - var15;
            double var29 = (var23 - var17) * 2.0D;
            double var31 = var25 - var19;

            if (var27 == 0.0D)
            {
                par1 = var7 + 0.5D;
                var13 = par5 - var9;
            }
            else if (var31 == 0.0D)
            {
                par5 = var9 + 0.5D;
                var13 = par1 - var7;
            }
            else
            {
                double var33 = par1 - var15;
                double var35 = par5 - var19;
                var13 = (var33 * var27 + var35 * var31) * 2.0D;
            }

            par1 = var15 + var27 * var13;
            par3 = var17 + var29 * var13;
            par5 = var19 + var31 * var13;

            if (var29 < 0.0D)
            {
                ++par3;
            }

            if (var29 > 0.0D)
            {
                par3 += 0.5D;
            }

            return worldObj.getWorldVec3Pool().getVecFromPool(par1, par3, par5);
        }
        else
        {
            return null;
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.getBoolean("CustomDisplayTile"))
        {
            func_145819_k(par1NBTTagCompound.getInteger("DisplayTile"));
            setDisplayTileData(par1NBTTagCompound.getInteger("DisplayData"));
            setDisplayTileOffset(par1NBTTagCompound.getInteger("DisplayOffset"));
        }

        if (par1NBTTagCompound.func_150297_b("CustomName", 8) && par1NBTTagCompound.getString("CustomName").length() > 0)
        {
            entityName = par1NBTTagCompound.getString("CustomName");
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (hasDisplayTile())
        {
            par1NBTTagCompound.setBoolean("CustomDisplayTile", true);
            par1NBTTagCompound.setInteger("DisplayTile", func_145820_n().getMaterial() == Material.air ? 0 : Block.getIdFromBlock(func_145820_n()));
            par1NBTTagCompound.setInteger("DisplayData", getDisplayTileData());
            par1NBTTagCompound.setInteger("DisplayOffset", getDisplayTileOffset());
        }

        if (entityName != null && entityName.length() > 0)
        {
            par1NBTTagCompound.setString("CustomName", entityName);
        }
    }

    /**
     * Applies a velocity to each of the entities pushing them away from each
     * other. Args: entity
     */
    public void applyEntityCollision(Entity par1Entity)
    {
        if (!worldObj.isClient)
        {
            if (par1Entity != riddenByEntity)
            {
                if (par1Entity instanceof EntityLivingBase && !(par1Entity instanceof EntityPlayer) && !(par1Entity instanceof EntityIronGolem) && getMinecartType() == 0 && motionX * motionX + motionZ * motionZ > 0.01D && riddenByEntity == null && par1Entity.ridingEntity == null)
                {
                    par1Entity.mountEntity(this);
                }

                double var2 = par1Entity.posX - posX;
                double var4 = par1Entity.posZ - posZ;
                double var6 = var2 * var2 + var4 * var4;

                if (var6 >= 9.999999747378752E-5D)
                {
                    var6 = MathHelper.sqrt_double(var6);
                    var2 /= var6;
                    var4 /= var6;
                    double var8 = 1.0D / var6;

                    if (var8 > 1.0D)
                    {
                        var8 = 1.0D;
                    }

                    var2 *= var8;
                    var4 *= var8;
                    var2 *= 0.10000000149011612D;
                    var4 *= 0.10000000149011612D;
                    var2 *= 1.0F - entityCollisionReduction;
                    var4 *= 1.0F - entityCollisionReduction;
                    var2 *= 0.5D;
                    var4 *= 0.5D;

                    if (par1Entity instanceof EntityMinecart)
                    {
                        double var10 = par1Entity.posX - posX;
                        double var12 = par1Entity.posZ - posZ;
                        Vec3 var14 = worldObj.getWorldVec3Pool().getVecFromPool(var10, 0.0D, var12).normalize();
                        Vec3 var15 = worldObj.getWorldVec3Pool().getVecFromPool(MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F), 0.0D, MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F)).normalize();
                        double var16 = Math.abs(var14.dotProduct(var15));

                        if (var16 < 0.800000011920929D) { return; }

                        double var18 = par1Entity.motionX + motionX;
                        double var20 = par1Entity.motionZ + motionZ;

                        if (((EntityMinecart)par1Entity).getMinecartType() == 2 && getMinecartType() != 2)
                        {
                            motionX *= 0.20000000298023224D;
                            motionZ *= 0.20000000298023224D;
                            addVelocity(par1Entity.motionX - var2, 0.0D, par1Entity.motionZ - var4);
                            par1Entity.motionX *= 0.949999988079071D;
                            par1Entity.motionZ *= 0.949999988079071D;
                        }
                        else if (((EntityMinecart)par1Entity).getMinecartType() != 2 && getMinecartType() == 2)
                        {
                            par1Entity.motionX *= 0.20000000298023224D;
                            par1Entity.motionZ *= 0.20000000298023224D;
                            par1Entity.addVelocity(motionX + var2, 0.0D, motionZ + var4);
                            motionX *= 0.949999988079071D;
                            motionZ *= 0.949999988079071D;
                        }
                        else
                        {
                            var18 /= 2.0D;
                            var20 /= 2.0D;
                            motionX *= 0.20000000298023224D;
                            motionZ *= 0.20000000298023224D;
                            addVelocity(var18 - var2, 0.0D, var20 - var4);
                            par1Entity.motionX *= 0.20000000298023224D;
                            par1Entity.motionZ *= 0.20000000298023224D;
                            par1Entity.addVelocity(var18 + var2, 0.0D, var20 + var4);
                        }
                    }
                    else
                    {
                        addVelocity(-var2, 0.0D, -var4);
                        par1Entity.addVelocity(var2 / 4.0D, 0.0D, var4 / 4.0D);
                    }
                }
            }
        }
    }

    /**
     * Sets the current amount of damage the minecart has taken. Decreases over
     * time. The cart breaks when this is over 40.
     */
    public void setDamage(float par1)
    {
        dataWatcher.updateObject(19, Float.valueOf(par1));
    }

    /**
     * Gets the current amount of damage the minecart has taken. Decreases over
     * time. The cart breaks when this is over 40.
     */
    public float getDamage()
    {
        return dataWatcher.getWatchableObjectFloat(19);
    }

    /**
     * Sets the rolling amplitude the cart rolls while being attacked.
     */
    public void setRollingAmplitude(int par1)
    {
        dataWatcher.updateObject(17, Integer.valueOf(par1));
    }

    /**
     * Gets the rolling amplitude the cart rolls while being attacked.
     */
    public int getRollingAmplitude()
    {
        return dataWatcher.getWatchableObjectInt(17);
    }

    /**
     * Sets the rolling direction the cart rolls while being attacked. Can be 1
     * or -1.
     */
    public void setRollingDirection(int par1)
    {
        dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    /**
     * Gets the rolling direction the cart rolls while being attacked. Can be 1
     * or -1.
     */
    public int getRollingDirection()
    {
        return dataWatcher.getWatchableObjectInt(18);
    }

    public abstract int getMinecartType();

    public Block func_145820_n()
    {
        if (!hasDisplayTile())
        {
            return func_145817_o();
        }
        else
        {
            int var1 = getDataWatcher().getWatchableObjectInt(20) & 65535;
            return Block.getBlockById(var1);
        }
    }

    public Block func_145817_o()
    {
        return Blocks.air;
    }

    public int getDisplayTileData()
    {
        return !hasDisplayTile() ? getDefaultDisplayTileData() : getDataWatcher().getWatchableObjectInt(20) >> 16;
    }

    public int getDefaultDisplayTileData()
    {
        return 0;
    }

    public int getDisplayTileOffset()
    {
        return !hasDisplayTile() ? getDefaultDisplayTileOffset() : getDataWatcher().getWatchableObjectInt(21);
    }

    public int getDefaultDisplayTileOffset()
    {
        return 6;
    }

    public void func_145819_k(int p_145819_1_)
    {
        getDataWatcher().updateObject(20, Integer.valueOf(p_145819_1_ & 65535 | getDisplayTileData() << 16));
        setHasDisplayTile(true);
    }

    public void setDisplayTileData(int par1)
    {
        getDataWatcher().updateObject(20, Integer.valueOf(Block.getIdFromBlock(func_145820_n()) & 65535 | par1 << 16));
        setHasDisplayTile(true);
    }

    public void setDisplayTileOffset(int par1)
    {
        getDataWatcher().updateObject(21, Integer.valueOf(par1));
        setHasDisplayTile(true);
    }

    public boolean hasDisplayTile()
    {
        return getDataWatcher().getWatchableObjectByte(22) == 1;
    }

    public void setHasDisplayTile(boolean par1)
    {
        getDataWatcher().updateObject(22, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    /**
     * Sets the minecart's name.
     */
    public void setMinecartName(String par1Str)
    {
        entityName = par1Str;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly
     * "Rcon")
     */
    public String getUsername()
    {
        return entityName != null ? entityName : super.getUsername();
    }

    /**
     * Returns if the inventory name is localized
     */
    public boolean isInventoryNameLocalized()
    {
        return entityName != null;
    }

    public String func_95999_t()
    {
        return entityName;
    }
}
