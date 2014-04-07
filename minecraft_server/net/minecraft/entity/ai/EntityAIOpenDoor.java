package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract
{
    boolean field_75361_i;
    int field_75360_j;
    private static final String __OBFID = "CL_00001603";

    public EntityAIOpenDoor(EntityLiving par1EntityLiving, boolean par2)
    {
        super(par1EntityLiving);
        theEntity = par1EntityLiving;
        field_75361_i = par2;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return field_75361_i && field_75360_j > 0 && super.continueExecuting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        field_75360_j = 20;
        field_151504_e.func_150014_a(theEntity.worldObj, entityPosX, entityPosY, entityPosZ, true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        if (field_75361_i)
        {
            field_151504_e.func_150014_a(theEntity.worldObj, entityPosX, entityPosY, entityPosZ, false);
        }
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        --field_75360_j;
        super.updateTask();
    }
}
