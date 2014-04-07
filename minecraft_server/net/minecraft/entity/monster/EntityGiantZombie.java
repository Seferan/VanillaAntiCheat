package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityGiantZombie extends EntityMob
{
    private static final String __OBFID = "CL_00001690";

    public EntityGiantZombie(World par1World)
    {
        super(par1World);
        yOffset *= 6.0F;
        setSize(width * 6.0F, height * 6.0F);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(50.0D);
    }

    /**
     * Takes a coordinate in and returns a weight to determine how likely this
     * creature will try to path to the block. Args: x, y, z
     */
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return worldObj.getLightBrightness(par1, par2, par3) - 0.5F;
    }
}
