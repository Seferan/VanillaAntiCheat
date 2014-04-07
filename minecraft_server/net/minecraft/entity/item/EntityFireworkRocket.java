package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkRocket extends Entity
{
    /** The age of the firework in ticks. */
    private int fireworkAge;

    /**
     * The lifetime of the firework in ticks. When the age reaches the lifetime
     * the firework explodes.
     */
    private int lifetime;
    private static final String __OBFID = "CL_00001718";

    public EntityFireworkRocket(World par1World)
    {
        super(par1World);
        setSize(0.25F, 0.25F);
    }

    protected void entityInit()
    {
        dataWatcher.addObjectByDataType(8, 5);
    }

    public EntityFireworkRocket(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack)
    {
        super(par1World);
        fireworkAge = 0;
        setSize(0.25F, 0.25F);
        setPosition(par2, par4, par6);
        yOffset = 0.0F;
        int var9 = 1;

        if (par8ItemStack != null && par8ItemStack.hasTagCompound())
        {
            dataWatcher.updateObject(8, par8ItemStack);
            NBTTagCompound var10 = par8ItemStack.getTagCompound();
            NBTTagCompound var11 = var10.getCompoundTag("Fireworks");

            if (var11 != null)
            {
                var9 += var11.getByte("Flight");
            }
        }

        motionX = rand.nextGaussian() * 0.001D;
        motionZ = rand.nextGaussian() * 0.001D;
        motionY = 0.05D;
        lifetime = 10 * var9 + rand.nextInt(6) + rand.nextInt(7);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;
        super.onUpdate();
        motionX *= 1.15D;
        motionZ *= 1.15D;
        motionY += 0.04D;
        moveEntity(motionX, motionY, motionZ);
        float var1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

        for (rotationPitch = (float)(Math.atan2(motionY, var1) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
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

        if (fireworkAge == 0)
        {
            worldObj.playSoundAtEntity(this, "fireworks.launch", 3.0F, 1.0F);
        }

        ++fireworkAge;

        if (worldObj.isClient && fireworkAge % 2 < 2)
        {
            worldObj.spawnParticle("fireworksSpark", posX, posY - 0.3D, posZ, rand.nextGaussian() * 0.05D, -motionY * 0.5D, rand.nextGaussian() * 0.05D);
        }

        if (!worldObj.isClient && fireworkAge > lifetime)
        {
            worldObj.setEntityState(this, (byte)17);
            setDead();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("Life", fireworkAge);
        par1NBTTagCompound.setInteger("LifeTime", lifetime);
        ItemStack var2 = dataWatcher.getWatchableObjectItemStack(8);

        if (var2 != null)
        {
            NBTTagCompound var3 = new NBTTagCompound();
            var2.writeToNBT(var3);
            par1NBTTagCompound.setTag("FireworksItem", var3);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        fireworkAge = par1NBTTagCompound.getInteger("Life");
        lifetime = par1NBTTagCompound.getInteger("LifeTime");
        NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("FireworksItem");

        if (var2 != null)
        {
            ItemStack var3 = ItemStack.loadItemStackFromNBT(var2);

            if (var3 != null)
            {
                dataWatcher.updateObject(8, var3);
            }
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float par1)
    {
        return super.getBrightness(par1);
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }
}
