package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityAIVillagerMate extends EntityAIBase
{
    private EntityVillager villagerObj;
    private EntityVillager mate;
    private World worldObj;
    private int matingTimeout;
    Village villageObj;
    private static final String __OBFID = "CL_00001594";

    public EntityAIVillagerMate(EntityVillager par1EntityVillager)
    {
        villagerObj = par1EntityVillager;
        worldObj = par1EntityVillager.worldObj;
        setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (villagerObj.getGrowingAge() != 0)
        {
            return false;
        }
        else if (villagerObj.getRNG().nextInt(500) != 0)
        {
            return false;
        }
        else
        {
            villageObj = worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(villagerObj.posX), MathHelper.floor_double(villagerObj.posY), MathHelper.floor_double(villagerObj.posZ), 0);

            if (villageObj == null)
            {
                return false;
            }
            else if (!checkSufficientDoorsPresentForNewVillager())
            {
                return false;
            }
            else
            {
                Entity var1 = worldObj.findNearestEntityWithinAABB(EntityVillager.class, villagerObj.boundingBox.expand(8.0D, 3.0D, 8.0D), villagerObj);

                if (var1 == null)
                {
                    return false;
                }
                else
                {
                    mate = (EntityVillager)var1;
                    return mate.getGrowingAge() == 0;
                }
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        matingTimeout = 300;
        villagerObj.setMating(true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        villageObj = null;
        mate = null;
        villagerObj.setMating(false);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return matingTimeout >= 0 && checkSufficientDoorsPresentForNewVillager() && villagerObj.getGrowingAge() == 0;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        --matingTimeout;
        villagerObj.getLookHelper().setLookPositionWithEntity(mate, 10.0F, 30.0F);

        if (villagerObj.getDistanceSqToEntity(mate) > 2.25D)
        {
            villagerObj.getNavigator().tryMoveToEntityLiving(mate, 0.25D);
        }
        else if (matingTimeout == 0 && mate.isMating())
        {
            giveBirth();
        }

        if (villagerObj.getRNG().nextInt(35) == 0)
        {
            worldObj.setEntityState(villagerObj, (byte)12);
        }
    }

    private boolean checkSufficientDoorsPresentForNewVillager()
    {
        if (!villageObj.isMatingSeason())
        {
            return false;
        }
        else
        {
            int var1 = (int)((villageObj.getNumVillageDoors()) * 0.35D);
            return villageObj.getNumVillagers() < var1;
        }
    }

    private void giveBirth()
    {
        EntityVillager var1 = villagerObj.createChild(mate);
        mate.setGrowingAge(6000);
        villagerObj.setGrowingAge(6000);
        var1.setGrowingAge(-24000);
        var1.setLocationAndAngles(villagerObj.posX, villagerObj.posY, villagerObj.posZ, 0.0F, 0.0F);
        worldObj.spawnEntityInWorld(var1);
        worldObj.setEntityState(var1, (byte)12);
    }
}
