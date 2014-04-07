package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityMinecartTNT extends EntityMinecart
{
    private int minecartTNTFuse = -1;
    private static final String __OBFID = "CL_00001680";

    public EntityMinecartTNT(World par1World)
    {
        super(par1World);
    }

    public EntityMinecartTNT(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public int getMinecartType()
    {
        return 3;
    }

    public Block func_145817_o()
    {
        return Blocks.tnt;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (minecartTNTFuse > 0)
        {
            --minecartTNTFuse;
            worldObj.spawnParticle("smoke", posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
        }
        else if (minecartTNTFuse == 0)
        {
            explodeCart(motionX * motionX + motionZ * motionZ);
        }

        if (isCollidedHorizontally)
        {
            double var1 = motionX * motionX + motionZ * motionZ;

            if (var1 >= 0.009999999776482582D)
            {
                explodeCart(var1);
            }
        }
    }

    public void killMinecart(DamageSource par1DamageSource)
    {
        super.killMinecart(par1DamageSource);
        double var2 = motionX * motionX + motionZ * motionZ;

        if (!par1DamageSource.isExplosion())
        {
            entityDropItem(new ItemStack(Blocks.tnt, 1), 0.0F);
        }

        if (par1DamageSource.isFireDamage() || par1DamageSource.isExplosion() || var2 >= 0.009999999776482582D)
        {
            explodeCart(var2);
        }
    }

    /**
     * Makes the minecart explode.
     */
    protected void explodeCart(double par1)
    {
        if (!worldObj.isClient)
        {
            double var3 = Math.sqrt(par1);

            if (var3 > 5.0D)
            {
                var3 = 5.0D;
            }

            worldObj.createExplosion(this, posX, posY, posZ, (float)(4.0D + rand.nextDouble() * 1.5D * var3), true);
            setDead();
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        if (par1 >= 3.0F)
        {
            float var2 = par1 / 10.0F;
            explodeCart(var2 * var2);
        }

        super.fall(par1);
    }

    /**
     * Called every tick the minecart is on an activator rail. Args: x, y, z, is
     * the rail receiving power
     */
    public void onActivatorRailPass(int par1, int par2, int par3, boolean par4)
    {
        if (par4 && minecartTNTFuse < 0)
        {
            ignite();
        }
    }

    /**
     * Ignites this TNT cart.
     */
    public void ignite()
    {
        minecartTNTFuse = 80;

        if (!worldObj.isClient)
        {
            worldObj.setEntityState(this, (byte)10);
            worldObj.playSoundAtEntity(this, "game.tnt.primed", 1.0F, 1.0F);
        }
    }

    /**
     * Returns true if the TNT minecart is ignited.
     */
    public boolean isIgnited()
    {
        return minecartTNTFuse > -1;
    }

    public float func_145772_a(Explosion p_145772_1_, World p_145772_2_, int p_145772_3_, int p_145772_4_, int p_145772_5_, Block p_145772_6_)
    {
        return isIgnited() && (BlockRailBase.func_150051_a(p_145772_6_) || BlockRailBase.func_150049_b_(p_145772_2_, p_145772_3_, p_145772_4_ + 1, p_145772_5_)) ? 0.0F : super.func_145772_a(p_145772_1_, p_145772_2_, p_145772_3_, p_145772_4_, p_145772_5_, p_145772_6_);
    }

    public boolean func_145774_a(Explosion p_145774_1_, World p_145774_2_, int p_145774_3_, int p_145774_4_, int p_145774_5_, Block p_145774_6_, float p_145774_7_)
    {
        return isIgnited() && (BlockRailBase.func_150051_a(p_145774_6_) || BlockRailBase.func_150049_b_(p_145774_2_, p_145774_3_, p_145774_4_ + 1, p_145774_5_)) ? false : super.func_145774_a(p_145774_1_, p_145774_2_, p_145774_3_, p_145774_4_, p_145774_5_, p_145774_6_, p_145774_7_);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.func_150297_b("TNTFuse", 99))
        {
            minecartTNTFuse = par1NBTTagCompound.getInteger("TNTFuse");
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("TNTFuse", minecartTNTFuse);
    }
}
