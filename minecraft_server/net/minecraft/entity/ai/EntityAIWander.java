package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIWander extends EntityAIBase
{
    private EntityCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private double speed;
    private static final String __OBFID = "CL_00001608";

    public EntityAIWander(EntityCreature par1EntityCreature, double par2)
    {
        entity = par1EntityCreature;
        speed = par2;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (entity.getAge() >= 100)
        {
            return false;
        }
        else if (entity.getRNG().nextInt(120) != 0)
        {
            return false;
        }
        else
        {
            Vec3 var1 = RandomPositionGenerator.findRandomTarget(entity, 10, 7);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                xPosition = var1.xCoord;
                yPosition = var1.yCoord;
                zPosition = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        entity.getNavigator().tryMoveToXYZ(xPosition, yPosition, zPosition, speed);
    }
}
