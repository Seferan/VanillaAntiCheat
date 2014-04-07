package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIPanic extends EntityAIBase
{
    private EntityCreature theEntityCreature;
    private double speed;
    private double randPosX;
    private double randPosY;
    private double randPosZ;
    private static final String __OBFID = "CL_00001604";

    public EntityAIPanic(EntityCreature par1EntityCreature, double par2)
    {
        theEntityCreature = par1EntityCreature;
        speed = par2;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (theEntityCreature.getAITarget() == null && !theEntityCreature.isBurning())
        {
            return false;
        }
        else
        {
            Vec3 var1 = RandomPositionGenerator.findRandomTarget(theEntityCreature, 5, 4);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                randPosX = var1.xCoord;
                randPosY = var1.yCoord;
                randPosZ = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        theEntityCreature.getNavigator().tryMoveToXYZ(randPosX, randPosY, randPosZ, speed);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !theEntityCreature.getNavigator().noPath();
    }
}
