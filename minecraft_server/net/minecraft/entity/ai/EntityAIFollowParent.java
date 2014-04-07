package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIFollowParent extends EntityAIBase
{
    /** The child that is following its parent. */
    EntityAnimal childAnimal;
    EntityAnimal parentAnimal;
    double field_75347_c;
    private int field_75345_d;
    private static final String __OBFID = "CL_00001586";

    public EntityAIFollowParent(EntityAnimal par1EntityAnimal, double par2)
    {
        childAnimal = par1EntityAnimal;
        field_75347_c = par2;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (childAnimal.getGrowingAge() >= 0)
        {
            return false;
        }
        else
        {
            List var1 = childAnimal.worldObj.getEntitiesWithinAABB(childAnimal.getClass(), childAnimal.boundingBox.expand(8.0D, 4.0D, 8.0D));
            EntityAnimal var2 = null;
            double var3 = Double.MAX_VALUE;
            Iterator var5 = var1.iterator();

            while (var5.hasNext())
            {
                EntityAnimal var6 = (EntityAnimal)var5.next();

                if (var6.getGrowingAge() >= 0)
                {
                    double var7 = childAnimal.getDistanceSqToEntity(var6);

                    if (var7 <= var3)
                    {
                        var3 = var7;
                        var2 = var6;
                    }
                }
            }

            if (var2 == null)
            {
                return false;
            }
            else if (var3 < 9.0D)
            {
                return false;
            }
            else
            {
                parentAnimal = var2;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (!parentAnimal.isEntityAlive())
        {
            return false;
        }
        else
        {
            double var1 = childAnimal.getDistanceSqToEntity(parentAnimal);
            return var1 >= 9.0D && var1 <= 256.0D;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        field_75345_d = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        parentAnimal = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (--field_75345_d <= 0)
        {
            field_75345_d = 10;
            childAnimal.getNavigator().tryMoveToEntityLiving(parentAnimal, field_75347_c);
        }
    }
}
