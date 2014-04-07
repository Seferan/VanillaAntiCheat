package net.minecraft.entity.ai;

import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeSun extends EntityAIBase
{
    private EntityCreature theCreature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private double movementSpeed;
    private World theWorld;
    private static final String __OBFID = "CL_00001583";

    public EntityAIFleeSun(EntityCreature par1EntityCreature, double par2)
    {
        theCreature = par1EntityCreature;
        movementSpeed = par2;
        theWorld = par1EntityCreature.worldObj;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!theWorld.isDaytime())
        {
            return false;
        }
        else if (!theCreature.isBurning())
        {
            return false;
        }
        else if (!theWorld.canBlockSeeTheSky(MathHelper.floor_double(theCreature.posX), (int)theCreature.boundingBox.minY, MathHelper.floor_double(theCreature.posZ)))
        {
            return false;
        }
        else
        {
            Vec3 var1 = findPossibleShelter();

            if (var1 == null)
            {
                return false;
            }
            else
            {
                shelterX = var1.xCoord;
                shelterY = var1.yCoord;
                shelterZ = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !theCreature.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        theCreature.getNavigator().tryMoveToXYZ(shelterX, shelterY, shelterZ, movementSpeed);
    }

    private Vec3 findPossibleShelter()
    {
        Random var1 = theCreature.getRNG();

        for (int var2 = 0; var2 < 10; ++var2)
        {
            int var3 = MathHelper.floor_double(theCreature.posX + var1.nextInt(20) - 10.0D);
            int var4 = MathHelper.floor_double(theCreature.boundingBox.minY + var1.nextInt(6) - 3.0D);
            int var5 = MathHelper.floor_double(theCreature.posZ + var1.nextInt(20) - 10.0D);

            if (!theWorld.canBlockSeeTheSky(var3, var4, var5) && theCreature.getBlockPathWeight(var3, var4, var5) < 0.0F) { return theWorld.getWorldVec3Pool().getVecFromPool(var3, var4, var5); }
        }

        return null;
    }
}
