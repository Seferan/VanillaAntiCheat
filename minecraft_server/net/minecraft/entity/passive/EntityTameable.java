package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable
{
    protected EntityAISit aiSit = new EntityAISit(this);
    private static final String __OBFID = "CL_00001561";

    public EntityTameable(World par1World)
    {
        super(par1World);
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte)0));
        dataWatcher.addObject(17, "");
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);

        if (getOwnerName() == null)
        {
            par1NBTTagCompound.setString("Owner", "");
        }
        else
        {
            par1NBTTagCompound.setString("Owner", getOwnerName());
        }

        par1NBTTagCompound.setBoolean("Sitting", isSitting());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        String var2 = par1NBTTagCompound.getString("Owner");

        if (var2.length() > 0)
        {
            setOwner(var2);
            setTamed(true);
        }

        aiSit.setSitting(par1NBTTagCompound.getBoolean("Sitting"));
        setSitting(par1NBTTagCompound.getBoolean("Sitting"));
    }

    /**
     * Play the taming effect, will either be hearts or smoke depending on
     * status
     */
    protected void playTameEffect(boolean par1)
    {
        String var2 = "heart";

        if (!par1)
        {
            var2 = "smoke";
        }

        for (int var3 = 0; var3 < 7; ++var3)
        {
            double var4 = rand.nextGaussian() * 0.02D;
            double var6 = rand.nextGaussian() * 0.02D;
            double var8 = rand.nextGaussian() * 0.02D;
            worldObj.spawnParticle(var2, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, var4, var6, var8);
        }
    }

    public boolean isTamed()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 4) != 0;
    }

    public void setTamed(boolean par1)
    {
        byte var2 = dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 4)));
        }
        else
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -5)));
        }
    }

    public boolean isSitting()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setSitting(boolean par1)
    {
        byte var2 = dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 1)));
        }
        else
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -2)));
        }
    }

    public String getOwnerName()
    {
        return dataWatcher.getWatchableObjectString(17);
    }

    public void setOwner(String par1Str)
    {
        dataWatcher.updateObject(17, par1Str);
    }

    public EntityLivingBase getOwner()
    {
        return worldObj.getPlayerEntityByName(getOwnerName());
    }

    public EntityAISit func_70907_r()
    {
        return aiSit;
    }

    public boolean func_142018_a(EntityLivingBase par1EntityLivingBase, EntityLivingBase par2EntityLivingBase)
    {
        return true;
    }

    public Team getTeam()
    {
        if (isTamed())
        {
            EntityLivingBase var1 = getOwner();

            if (var1 != null) { return var1.getTeam(); }
        }

        return super.getTeam();
    }

    public boolean isOnSameTeam(EntityLivingBase par1EntityLivingBase)
    {
        if (isTamed())
        {
            EntityLivingBase var2 = getOwner();

            if (par1EntityLivingBase == var2) { return true; }

            if (var2 != null) { return var2.isOnSameTeam(par1EntityLivingBase); }
        }

        return super.isOnSameTeam(par1EntityLivingBase);
    }
}
