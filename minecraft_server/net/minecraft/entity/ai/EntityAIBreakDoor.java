package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakDoor extends EntityAIDoorInteract
{
    private int breakingTime;
    private int field_75358_j = -1;
    private static final String __OBFID = "CL_00001577";

    public EntityAIBreakDoor(EntityLiving par1EntityLiving)
    {
        super(par1EntityLiving);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return !super.shouldExecute() ? false : (!theEntity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") ? false : !field_151504_e.func_150015_f(theEntity.worldObj, entityPosX, entityPosY, entityPosZ));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
        breakingTime = 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        double var1 = theEntity.getDistanceSq(entityPosX, entityPosY, entityPosZ);
        return breakingTime <= 240 && !field_151504_e.func_150015_f(theEntity.worldObj, entityPosX, entityPosY, entityPosZ) && var1 < 4.0D;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        super.resetTask();
        theEntity.worldObj.destroyBlockInWorldPartially(theEntity.getEntityId(), entityPosX, entityPosY, entityPosZ, -1);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        super.updateTask();

        if (theEntity.getRNG().nextInt(20) == 0)
        {
            theEntity.worldObj.playAuxSFX(1010, entityPosX, entityPosY, entityPosZ, 0);
        }

        ++breakingTime;
        int var1 = (int)(breakingTime / 240.0F * 10.0F);

        if (var1 != field_75358_j)
        {
            theEntity.worldObj.destroyBlockInWorldPartially(theEntity.getEntityId(), entityPosX, entityPosY, entityPosZ, var1);
            field_75358_j = var1;
        }

        if (breakingTime == 240 && theEntity.worldObj.difficultySetting == EnumDifficulty.HARD)
        {
            theEntity.worldObj.setBlockToAir(entityPosX, entityPosY, entityPosZ);
            theEntity.worldObj.playAuxSFX(1012, entityPosX, entityPosY, entityPosZ, 0);
            theEntity.worldObj.playAuxSFX(2001, entityPosX, entityPosY, entityPosZ, Block.getIdFromBlock(field_151504_e));
        }
    }
}
