package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigate
{
    private EntityLiving theEntity;
    private World worldObj;

    /** The PathEntity being followed. */
    private PathEntity currentPath;
    private double speed;

    /**
     * The number of blocks (extra) +/- in each axis that get pulled out as
     * cache for the pathfinder's search space
     */
    private IAttributeInstance pathSearchRange;
    private boolean noSunPathfind;

    /** Time, in number of ticks, following the current path */
    private int totalTicks;

    /**
     * The time when the last position check was done (to detect successful
     * movement)
     */
    private int ticksAtLastPos;

    /**
     * Coordinates of the entity's position last time a check was done (part of
     * monitoring getting 'stuck')
     */
    private Vec3 lastPosCheck = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

    /**
     * Specifically, if a wooden door block is even considered to be passable by
     * the pathfinder
     */
    private boolean canPassOpenWoodenDoors = true;

    /** If door blocks are considered passable even when closed */
    private boolean canPassClosedWoodenDoors;

    /** If water blocks are avoided (at least by the pathfinder) */
    private boolean avoidsWater;

    /**
     * If the entity can swim. Swimming AI enables this and the pathfinder will
     * also cause the entity to swim straight upwards when underwater
     */
    private boolean canSwim;
    private static final String __OBFID = "CL_00001627";

    public PathNavigate(EntityLiving par1EntityLiving, World par2World)
    {
        theEntity = par1EntityLiving;
        worldObj = par2World;
        pathSearchRange = par1EntityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
    }

    public void setAvoidsWater(boolean par1)
    {
        avoidsWater = par1;
    }

    public boolean getAvoidsWater()
    {
        return avoidsWater;
    }

    public void setBreakDoors(boolean par1)
    {
        canPassClosedWoodenDoors = par1;
    }

    /**
     * Sets if the entity can enter open doors
     */
    public void setEnterDoors(boolean par1)
    {
        canPassOpenWoodenDoors = par1;
    }

    /**
     * Returns true if the entity can break doors, false otherwise
     */
    public boolean getCanBreakDoors()
    {
        return canPassClosedWoodenDoors;
    }

    /**
     * Sets if the path should avoid sunlight
     */
    public void setAvoidSun(boolean par1)
    {
        noSunPathfind = par1;
    }

    /**
     * Sets the speed
     */
    public void setSpeed(double par1)
    {
        speed = par1;
    }

    /**
     * Sets if the entity can swim
     */
    public void setCanSwim(boolean par1)
    {
        canSwim = par1;
    }

    /**
     * Gets the maximum distance that the path finding will search in.
     */
    public float getPathSearchRange()
    {
        return (float)pathSearchRange.getAttributeValue();
    }

    /**
     * Returns the path to the given coordinates
     */
    public PathEntity getPathToXYZ(double par1, double par3, double par5)
    {
        return !canNavigate() ? null : worldObj.getEntityPathToXYZ(theEntity, MathHelper.floor_double(par1), (int)par3, MathHelper.floor_double(par5), getPathSearchRange(), canPassOpenWoodenDoors, canPassClosedWoodenDoors, avoidsWater, canSwim);
    }

    /**
     * Try to find and set a path to XYZ. Returns true if successful.
     */
    public boolean tryMoveToXYZ(double par1, double par3, double par5, double par7)
    {
        PathEntity var9 = getPathToXYZ(MathHelper.floor_double(par1), ((int)par3), MathHelper.floor_double(par5));
        return setPath(var9, par7);
    }

    /**
     * Returns the path to the given EntityLiving
     */
    public PathEntity getPathToEntityLiving(Entity par1Entity)
    {
        return !canNavigate() ? null : worldObj.getPathEntityToEntity(theEntity, par1Entity, getPathSearchRange(), canPassOpenWoodenDoors, canPassClosedWoodenDoors, avoidsWater, canSwim);
    }

    /**
     * Try to find and set a path to EntityLiving. Returns true if successful.
     */
    public boolean tryMoveToEntityLiving(Entity par1Entity, double par2)
    {
        PathEntity var4 = getPathToEntityLiving(par1Entity);
        return var4 != null ? setPath(var4, par2) : false;
    }

    /**
     * sets the active path data if path is 100% unique compared to old path,
     * checks to adjust path for sun avoiding ents and stores end coords
     */
    public boolean setPath(PathEntity par1PathEntity, double par2)
    {
        if (par1PathEntity == null)
        {
            currentPath = null;
            return false;
        }
        else
        {
            if (!par1PathEntity.isSamePath(currentPath))
            {
                currentPath = par1PathEntity;
            }

            if (noSunPathfind)
            {
                removeSunnyPath();
            }

            if (currentPath.getCurrentPathLength() == 0)
            {
                return false;
            }
            else
            {
                speed = par2;
                Vec3 var4 = getEntityPosition();
                ticksAtLastPos = totalTicks;
                lastPosCheck.xCoord = var4.xCoord;
                lastPosCheck.yCoord = var4.yCoord;
                lastPosCheck.zCoord = var4.zCoord;
                return true;
            }
        }
    }

    /**
     * gets the actively used PathEntity
     */
    public PathEntity getPath()
    {
        return currentPath;
    }

    public void onUpdateNavigation()
    {
        ++totalTicks;

        if (!noPath())
        {
            if (canNavigate())
            {
                pathFollow();
            }

            if (!noPath())
            {
                Vec3 var1 = currentPath.getPosition(theEntity);

                if (var1 != null)
                {
                    theEntity.getMoveHelper().setMoveTo(var1.xCoord, var1.yCoord, var1.zCoord, speed);
                }
            }
        }
    }

    private void pathFollow()
    {
        Vec3 var1 = getEntityPosition();
        int var2 = currentPath.getCurrentPathLength();

        for (int var3 = currentPath.getCurrentPathIndex(); var3 < currentPath.getCurrentPathLength(); ++var3)
        {
            if (currentPath.getPathPointFromIndex(var3).yCoord != (int)var1.yCoord)
            {
                var2 = var3;
                break;
            }
        }

        float var8 = theEntity.width * theEntity.width;
        int var4;

        for (var4 = currentPath.getCurrentPathIndex(); var4 < var2; ++var4)
        {
            if (var1.squareDistanceTo(currentPath.getVectorFromIndex(theEntity, var4)) < var8)
            {
                currentPath.setCurrentPathIndex(var4 + 1);
            }
        }

        var4 = MathHelper.ceiling_float_int(theEntity.width);
        int var5 = (int)theEntity.height + 1;
        int var6 = var4;

        for (int var7 = var2 - 1; var7 >= currentPath.getCurrentPathIndex(); --var7)
        {
            if (isDirectPathBetweenPoints(var1, currentPath.getVectorFromIndex(theEntity, var7), var4, var5, var6))
            {
                currentPath.setCurrentPathIndex(var7);
                break;
            }
        }

        if (totalTicks - ticksAtLastPos > 100)
        {
            if (var1.squareDistanceTo(lastPosCheck) < 2.25D)
            {
                clearPathEntity();
            }

            ticksAtLastPos = totalTicks;
            lastPosCheck.xCoord = var1.xCoord;
            lastPosCheck.yCoord = var1.yCoord;
            lastPosCheck.zCoord = var1.zCoord;
        }
    }

    /**
     * If null path or reached the end
     */
    public boolean noPath()
    {
        return currentPath == null || currentPath.isFinished();
    }

    /**
     * sets active PathEntity to null
     */
    public void clearPathEntity()
    {
        currentPath = null;
    }

    private Vec3 getEntityPosition()
    {
        return worldObj.getWorldVec3Pool().getVecFromPool(theEntity.posX, getPathableYPos(), theEntity.posZ);
    }

    /**
     * Gets the safe pathing Y position for the entity depending on if it can
     * path swim or not
     */
    private int getPathableYPos()
    {
        if (theEntity.isInWater() && canSwim)
        {
            int var1 = (int)theEntity.boundingBox.minY;
            Block var2 = worldObj.getBlock(MathHelper.floor_double(theEntity.posX), var1, MathHelper.floor_double(theEntity.posZ));
            int var3 = 0;

            do
            {
                if (var2 != Blocks.flowing_water && var2 != Blocks.water) { return var1; }

                ++var1;
                var2 = worldObj.getBlock(MathHelper.floor_double(theEntity.posX), var1, MathHelper.floor_double(theEntity.posZ));
                ++var3;
            } while (var3 <= 16);

            return (int)theEntity.boundingBox.minY;
        }
        else
        {
            return (int)(theEntity.boundingBox.minY + 0.5D);
        }
    }

    /**
     * If on ground or swimming and can swim
     */
    private boolean canNavigate()
    {
        return theEntity.onGround || canSwim && isInFluid();
    }

    /**
     * Returns true if the entity is in water or lava, false otherwise
     */
    private boolean isInFluid()
    {
        return theEntity.isInWater() || theEntity.handleLavaMovement();
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    private void removeSunnyPath()
    {
        if (!worldObj.canBlockSeeTheSky(MathHelper.floor_double(theEntity.posX), (int)(theEntity.boundingBox.minY + 0.5D), MathHelper.floor_double(theEntity.posZ)))
        {
            for (int var1 = 0; var1 < currentPath.getCurrentPathLength(); ++var1)
            {
                PathPoint var2 = currentPath.getPathPointFromIndex(var1);

                if (worldObj.canBlockSeeTheSky(var2.xCoord, var2.yCoord, var2.zCoord))
                {
                    currentPath.setCurrentPathLength(var1 - 1);
                    return;
                }
            }
        }
    }

    /**
     * Returns true when an entity of specified size could safely walk in a
     * straight line between the two points. Args: pos1, pos2, entityXSize,
     * entityYSize, entityZSize
     */
    private boolean isDirectPathBetweenPoints(Vec3 par1Vec3, Vec3 par2Vec3, int par3, int par4, int par5)
    {
        int var6 = MathHelper.floor_double(par1Vec3.xCoord);
        int var7 = MathHelper.floor_double(par1Vec3.zCoord);
        double var8 = par2Vec3.xCoord - par1Vec3.xCoord;
        double var10 = par2Vec3.zCoord - par1Vec3.zCoord;
        double var12 = var8 * var8 + var10 * var10;

        if (var12 < 1.0E-8D)
        {
            return false;
        }
        else
        {
            double var14 = 1.0D / Math.sqrt(var12);
            var8 *= var14;
            var10 *= var14;
            par3 += 2;
            par5 += 2;

            if (!isSafeToStandAt(var6, (int)par1Vec3.yCoord, var7, par3, par4, par5, par1Vec3, var8, var10))
            {
                return false;
            }
            else
            {
                par3 -= 2;
                par5 -= 2;
                double var16 = 1.0D / Math.abs(var8);
                double var18 = 1.0D / Math.abs(var10);
                double var20 = var6 * 1 - par1Vec3.xCoord;
                double var22 = var7 * 1 - par1Vec3.zCoord;

                if (var8 >= 0.0D)
                {
                    ++var20;
                }

                if (var10 >= 0.0D)
                {
                    ++var22;
                }

                var20 /= var8;
                var22 /= var10;
                int var24 = var8 < 0.0D ? -1 : 1;
                int var25 = var10 < 0.0D ? -1 : 1;
                int var26 = MathHelper.floor_double(par2Vec3.xCoord);
                int var27 = MathHelper.floor_double(par2Vec3.zCoord);
                int var28 = var26 - var6;
                int var29 = var27 - var7;

                do
                {
                    if (var28 * var24 <= 0 && var29 * var25 <= 0) { return true; }

                    if (var20 < var22)
                    {
                        var20 += var16;
                        var6 += var24;
                        var28 = var26 - var6;
                    }
                    else
                    {
                        var22 += var18;
                        var7 += var25;
                        var29 = var27 - var7;
                    }
                } while (isSafeToStandAt(var6, (int)par1Vec3.yCoord, var7, par3, par4, par5, par1Vec3, var8, var10));

                return false;
            }
        }
    }

    /**
     * Returns true when an entity could stand at a position, including solid
     * blocks under the entire entity. Args: xOffset, yOffset, zOffset,
     * entityXSize, entityYSize, entityZSize, originPosition, vecX, vecZ
     */
    private boolean isSafeToStandAt(int par1, int par2, int par3, int par4, int par5, int par6, Vec3 par7Vec3, double par8, double par10)
    {
        int var12 = par1 - par4 / 2;
        int var13 = par3 - par6 / 2;

        if (!isPositionClear(var12, par2, var13, par4, par5, par6, par7Vec3, par8, par10))
        {
            return false;
        }
        else
        {
            for (int var14 = var12; var14 < var12 + par4; ++var14)
            {
                for (int var15 = var13; var15 < var13 + par6; ++var15)
                {
                    double var16 = var14 + 0.5D - par7Vec3.xCoord;
                    double var18 = var15 + 0.5D - par7Vec3.zCoord;

                    if (var16 * par8 + var18 * par10 >= 0.0D)
                    {
                        Block var20 = worldObj.getBlock(var14, par2 - 1, var15);
                        Material var21 = var20.getMaterial();

                        if (var21 == Material.air) { return false; }

                        if (var21 == Material.field_151586_h && !theEntity.isInWater()) { return false; }

                        if (var21 == Material.field_151587_i) { return false; }
                    }
                }
            }

            return true;
        }
    }

    /**
     * Returns true if an entity does not collide with any solid blocks at the
     * position. Args: xOffset, yOffset, zOffset, entityXSize, entityYSize,
     * entityZSize, originPosition, vecX, vecZ
     */
    private boolean isPositionClear(int par1, int par2, int par3, int par4, int par5, int par6, Vec3 par7Vec3, double par8, double par10)
    {
        for (int var12 = par1; var12 < par1 + par4; ++var12)
        {
            for (int var13 = par2; var13 < par2 + par5; ++var13)
            {
                for (int var14 = par3; var14 < par3 + par6; ++var14)
                {
                    double var15 = var12 + 0.5D - par7Vec3.xCoord;
                    double var17 = var14 + 0.5D - par7Vec3.zCoord;

                    if (var15 * par8 + var17 * par10 >= 0.0D)
                    {
                        Block var19 = worldObj.getBlock(var12, var13, var14);

                        if (!var19.getBlocksMovement(worldObj, var12, var13, var14)) { return false; }
                    }
                }
            }
        }

        return true;
    }
}
