package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village
{
    private World worldObj;

    /** list of VillageDoorInfo objects */
    private final List villageDoorInfoList = new ArrayList();

    /**
     * This is the sum of all door coordinates and used to calculate the actual
     * village center by dividing by the number of doors.
     */
    private final ChunkCoordinates centerHelper = new ChunkCoordinates(0, 0, 0);

    /** This is the actual village center. */
    private final ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
    private int villageRadius;
    private int lastAddDoorTimestamp;
    private int tickCounter;
    private int numVillagers;

    /** Timestamp of tick count when villager last bred */
    private int noBreedTicks;

    /** List of player reputations with this village */
    private TreeMap playerReputation = new TreeMap();
    private List villageAgressors = new ArrayList();
    private int numIronGolems;
    private static final String __OBFID = "CL_00001631";

    public Village()
    {
    }

    public Village(World par1World)
    {
        worldObj = par1World;
    }

    public void func_82691_a(World par1World)
    {
        worldObj = par1World;
    }

    /**
     * Called periodically by VillageCollection
     */
    public void tick(int par1)
    {
        tickCounter = par1;
        removeDeadAndOutOfRangeDoors();
        removeDeadAndOldAgressors();

        if (par1 % 20 == 0)
        {
            updateNumVillagers();
        }

        if (par1 % 30 == 0)
        {
            updateNumIronGolems();
        }

        int var2 = numVillagers / 10;

        if (numIronGolems < var2 && villageDoorInfoList.size() > 20 && worldObj.rand.nextInt(7000) == 0)
        {
            Vec3 var3 = tryGetIronGolemSpawningLocation(MathHelper.floor_float(center.posX), MathHelper.floor_float(center.posY), MathHelper.floor_float(center.posZ), 2, 4, 2);

            if (var3 != null)
            {
                EntityIronGolem var4 = new EntityIronGolem(worldObj);
                var4.setPosition(var3.xCoord, var3.yCoord, var3.zCoord);
                worldObj.spawnEntityInWorld(var4);
                ++numIronGolems;
            }
        }
    }

    /**
     * Tries up to 10 times to get a valid spawning location before eventually
     * failing and returning null.
     */
    private Vec3 tryGetIronGolemSpawningLocation(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        for (int var7 = 0; var7 < 10; ++var7)
        {
            int var8 = par1 + worldObj.rand.nextInt(16) - 8;
            int var9 = par2 + worldObj.rand.nextInt(6) - 3;
            int var10 = par3 + worldObj.rand.nextInt(16) - 8;

            if (isInRange(var8, var9, var10) && isValidIronGolemSpawningLocation(var8, var9, var10, par4, par5, par6)) { return worldObj.getWorldVec3Pool().getVecFromPool(var8, var9, var10); }
        }

        return null;
    }

    private boolean isValidIronGolemSpawningLocation(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        if (!World.doesBlockHaveSolidTopSurface(worldObj, par1, par2 - 1, par3))
        {
            return false;
        }
        else
        {
            int var7 = par1 - par4 / 2;
            int var8 = par3 - par6 / 2;

            for (int var9 = var7; var9 < var7 + par4; ++var9)
            {
                for (int var10 = par2; var10 < par2 + par5; ++var10)
                {
                    for (int var11 = var8; var11 < var8 + par6; ++var11)
                    {
                        if (worldObj.getBlock(var9, var10, var11).isNormalCube()) { return false; }
                    }
                }
            }

            return true;
        }
    }

    private void updateNumIronGolems()
    {
        List var1 = worldObj.getEntitiesWithinAABB(EntityIronGolem.class, AxisAlignedBB.getAABBPool().getAABB(center.posX - villageRadius, center.posY - 4, center.posZ - villageRadius, center.posX + villageRadius, center.posY + 4, center.posZ + villageRadius));
        numIronGolems = var1.size();
    }

    private void updateNumVillagers()
    {
        List var1 = worldObj.getEntitiesWithinAABB(EntityVillager.class, AxisAlignedBB.getAABBPool().getAABB(center.posX - villageRadius, center.posY - 4, center.posZ - villageRadius, center.posX + villageRadius, center.posY + 4, center.posZ + villageRadius));
        numVillagers = var1.size();

        if (numVillagers == 0)
        {
            playerReputation.clear();
        }
    }

    public ChunkCoordinates getCenter()
    {
        return center;
    }

    public int getVillageRadius()
    {
        return villageRadius;
    }

    /**
     * Actually get num village door info entries, but that boils down to number
     * of doors. Called by EntityAIVillagerMate and VillageSiege
     */
    public int getNumVillageDoors()
    {
        return villageDoorInfoList.size();
    }

    public int getTicksSinceLastDoorAdding()
    {
        return tickCounter - lastAddDoorTimestamp;
    }

    public int getNumVillagers()
    {
        return numVillagers;
    }

    /**
     * Returns true, if the given coordinates are within the bounding box of the
     * village.
     */
    public boolean isInRange(int par1, int par2, int par3)
    {
        return center.getDistanceSquared(par1, par2, par3) < villageRadius * villageRadius;
    }

    /**
     * called only by class EntityAIMoveThroughVillage
     */
    public List getVillageDoorInfoList()
    {
        return villageDoorInfoList;
    }

    public VillageDoorInfo findNearestDoor(int par1, int par2, int par3)
    {
        VillageDoorInfo var4 = null;
        int var5 = Integer.MAX_VALUE;
        Iterator var6 = villageDoorInfoList.iterator();

        while (var6.hasNext())
        {
            VillageDoorInfo var7 = (VillageDoorInfo)var6.next();
            int var8 = var7.getDistanceSquared(par1, par2, par3);

            if (var8 < var5)
            {
                var4 = var7;
                var5 = var8;
            }
        }

        return var4;
    }

    /**
     * Find a door suitable for shelter. If there are more doors in a distance
     * of 16 blocks, then the least restricted one (i.e. the one protecting the
     * lowest number of villagers) of them is chosen, else the nearest one
     * regardless of restriction.
     */
    public VillageDoorInfo findNearestDoorUnrestricted(int par1, int par2, int par3)
    {
        VillageDoorInfo var4 = null;
        int var5 = Integer.MAX_VALUE;
        Iterator var6 = villageDoorInfoList.iterator();

        while (var6.hasNext())
        {
            VillageDoorInfo var7 = (VillageDoorInfo)var6.next();
            int var8 = var7.getDistanceSquared(par1, par2, par3);

            if (var8 > 256)
            {
                var8 *= 1000;
            }
            else
            {
                var8 = var7.getDoorOpeningRestrictionCounter();
            }

            if (var8 < var5)
            {
                var4 = var7;
                var5 = var8;
            }
        }

        return var4;
    }

    public VillageDoorInfo getVillageDoorAt(int par1, int par2, int par3)
    {
        if (center.getDistanceSquared(par1, par2, par3) > villageRadius * villageRadius)
        {
            return null;
        }
        else
        {
            Iterator var4 = villageDoorInfoList.iterator();
            VillageDoorInfo var5;

            do
            {
                if (!var4.hasNext()) { return null; }

                var5 = (VillageDoorInfo)var4.next();
            } while (var5.posX != par1 || var5.posZ != par3 || Math.abs(var5.posY - par2) > 1);

            return var5;
        }
    }

    public void addVillageDoorInfo(VillageDoorInfo par1VillageDoorInfo)
    {
        villageDoorInfoList.add(par1VillageDoorInfo);
        centerHelper.posX += par1VillageDoorInfo.posX;
        centerHelper.posY += par1VillageDoorInfo.posY;
        centerHelper.posZ += par1VillageDoorInfo.posZ;
        updateVillageRadiusAndCenter();
        lastAddDoorTimestamp = par1VillageDoorInfo.lastActivityTimestamp;
    }

    /**
     * Returns true, if there is not a single village door left. Called by
     * VillageCollection
     */
    public boolean isAnnihilated()
    {
        return villageDoorInfoList.isEmpty();
    }

    public void addOrRenewAgressor(EntityLivingBase par1EntityLivingBase)
    {
        Iterator var2 = villageAgressors.iterator();
        Village.VillageAgressor var3;

        do
        {
            if (!var2.hasNext())
            {
                villageAgressors.add(new Village.VillageAgressor(par1EntityLivingBase, tickCounter));
                return;
            }

            var3 = (Village.VillageAgressor)var2.next();
        } while (var3.agressor != par1EntityLivingBase);

        var3.agressionTime = tickCounter;
    }

    public EntityLivingBase findNearestVillageAggressor(EntityLivingBase par1EntityLivingBase)
    {
        double var2 = Double.MAX_VALUE;
        Village.VillageAgressor var4 = null;

        for (int var5 = 0; var5 < villageAgressors.size(); ++var5)
        {
            Village.VillageAgressor var6 = (Village.VillageAgressor)villageAgressors.get(var5);
            double var7 = var6.agressor.getDistanceSqToEntity(par1EntityLivingBase);

            if (var7 <= var2)
            {
                var4 = var6;
                var2 = var7;
            }
        }

        return var4 != null ? var4.agressor : null;
    }

    public EntityPlayer func_82685_c(EntityLivingBase par1EntityLivingBase)
    {
        double var2 = Double.MAX_VALUE;
        EntityPlayer var4 = null;
        Iterator var5 = playerReputation.keySet().iterator();

        while (var5.hasNext())
        {
            String var6 = (String)var5.next();

            if (isPlayerReputationTooLow(var6))
            {
                EntityPlayer var7 = worldObj.getPlayerEntityByName(var6);

                if (var7 != null)
                {
                    double var8 = var7.getDistanceSqToEntity(par1EntityLivingBase);

                    if (var8 <= var2)
                    {
                        var4 = var7;
                        var2 = var8;
                    }
                }
            }
        }

        return var4;
    }

    private void removeDeadAndOldAgressors()
    {
        Iterator var1 = villageAgressors.iterator();

        while (var1.hasNext())
        {
            Village.VillageAgressor var2 = (Village.VillageAgressor)var1.next();

            if (!var2.agressor.isEntityAlive() || Math.abs(tickCounter - var2.agressionTime) > 300)
            {
                var1.remove();
            }
        }
    }

    private void removeDeadAndOutOfRangeDoors()
    {
        boolean var1 = false;
        boolean var2 = worldObj.rand.nextInt(50) == 0;
        Iterator var3 = villageDoorInfoList.iterator();

        while (var3.hasNext())
        {
            VillageDoorInfo var4 = (VillageDoorInfo)var3.next();

            if (var2)
            {
                var4.resetDoorOpeningRestrictionCounter();
            }

            if (!isBlockDoor(var4.posX, var4.posY, var4.posZ) || Math.abs(tickCounter - var4.lastActivityTimestamp) > 1200)
            {
                centerHelper.posX -= var4.posX;
                centerHelper.posY -= var4.posY;
                centerHelper.posZ -= var4.posZ;
                var1 = true;
                var4.isDetachedFromVillageFlag = true;
                var3.remove();
            }
        }

        if (var1)
        {
            updateVillageRadiusAndCenter();
        }
    }

    private boolean isBlockDoor(int par1, int par2, int par3)
    {
        return worldObj.getBlock(par1, par2, par3) == Blocks.wooden_door;
    }

    private void updateVillageRadiusAndCenter()
    {
        int var1 = villageDoorInfoList.size();

        if (var1 == 0)
        {
            center.set(0, 0, 0);
            villageRadius = 0;
        }
        else
        {
            center.set(centerHelper.posX / var1, centerHelper.posY / var1, centerHelper.posZ / var1);
            int var2 = 0;
            VillageDoorInfo var4;

            for (Iterator var3 = villageDoorInfoList.iterator(); var3.hasNext(); var2 = Math.max(var4.getDistanceSquared(center.posX, center.posY, center.posZ), var2))
            {
                var4 = (VillageDoorInfo)var3.next();
            }

            villageRadius = Math.max(32, (int)Math.sqrt(var2) + 1);
        }
    }

    /**
     * Return the village reputation for a player
     */
    public int getReputationForPlayer(String par1Str)
    {
        Integer var2 = (Integer)playerReputation.get(par1Str);
        return var2 != null ? var2.intValue() : 0;
    }

    /**
     * Set the village reputation for a player.
     */
    public int setReputationForPlayer(String par1Str, int par2)
    {
        int var3 = getReputationForPlayer(par1Str);
        int var4 = MathHelper.clamp_int(var3 + par2, -30, 10);
        playerReputation.put(par1Str, Integer.valueOf(var4));
        return var4;
    }

    /**
     * Return whether this player has a too low reputation with this village.
     */
    public boolean isPlayerReputationTooLow(String par1Str)
    {
        return getReputationForPlayer(par1Str) <= -15;
    }

    /**
     * Read this village's data from NBT.
     */
    public void readVillageDataFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        numVillagers = par1NBTTagCompound.getInteger("PopSize");
        villageRadius = par1NBTTagCompound.getInteger("Radius");
        numIronGolems = par1NBTTagCompound.getInteger("Golems");
        lastAddDoorTimestamp = par1NBTTagCompound.getInteger("Stable");
        tickCounter = par1NBTTagCompound.getInteger("Tick");
        noBreedTicks = par1NBTTagCompound.getInteger("MTick");
        center.posX = par1NBTTagCompound.getInteger("CX");
        center.posY = par1NBTTagCompound.getInteger("CY");
        center.posZ = par1NBTTagCompound.getInteger("CZ");
        centerHelper.posX = par1NBTTagCompound.getInteger("ACX");
        centerHelper.posY = par1NBTTagCompound.getInteger("ACY");
        centerHelper.posZ = par1NBTTagCompound.getInteger("ACZ");
        NBTTagList var2 = par1NBTTagCompound.getTagList("Doors", 10);

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            VillageDoorInfo var5 = new VillageDoorInfo(var4.getInteger("X"), var4.getInteger("Y"), var4.getInteger("Z"), var4.getInteger("IDX"), var4.getInteger("IDZ"), var4.getInteger("TS"));
            villageDoorInfoList.add(var5);
        }

        NBTTagList var6 = par1NBTTagCompound.getTagList("Players", 10);

        for (int var7 = 0; var7 < var6.tagCount(); ++var7)
        {
            NBTTagCompound var8 = var6.getCompoundTagAt(var7);
            playerReputation.put(var8.getString("Name"), Integer.valueOf(var8.getInteger("S")));
        }
    }

    /**
     * Write this village's data to NBT.
     */
    public void writeVillageDataToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("PopSize", numVillagers);
        par1NBTTagCompound.setInteger("Radius", villageRadius);
        par1NBTTagCompound.setInteger("Golems", numIronGolems);
        par1NBTTagCompound.setInteger("Stable", lastAddDoorTimestamp);
        par1NBTTagCompound.setInteger("Tick", tickCounter);
        par1NBTTagCompound.setInteger("MTick", noBreedTicks);
        par1NBTTagCompound.setInteger("CX", center.posX);
        par1NBTTagCompound.setInteger("CY", center.posY);
        par1NBTTagCompound.setInteger("CZ", center.posZ);
        par1NBTTagCompound.setInteger("ACX", centerHelper.posX);
        par1NBTTagCompound.setInteger("ACY", centerHelper.posY);
        par1NBTTagCompound.setInteger("ACZ", centerHelper.posZ);
        NBTTagList var2 = new NBTTagList();
        Iterator var3 = villageDoorInfoList.iterator();

        while (var3.hasNext())
        {
            VillageDoorInfo var4 = (VillageDoorInfo)var3.next();
            NBTTagCompound var5 = new NBTTagCompound();
            var5.setInteger("X", var4.posX);
            var5.setInteger("Y", var4.posY);
            var5.setInteger("Z", var4.posZ);
            var5.setInteger("IDX", var4.insideDirectionX);
            var5.setInteger("IDZ", var4.insideDirectionZ);
            var5.setInteger("TS", var4.lastActivityTimestamp);
            var2.appendTag(var5);
        }

        par1NBTTagCompound.setTag("Doors", var2);
        NBTTagList var7 = new NBTTagList();
        Iterator var8 = playerReputation.keySet().iterator();

        while (var8.hasNext())
        {
            String var9 = (String)var8.next();
            NBTTagCompound var6 = new NBTTagCompound();
            var6.setString("Name", var9);
            var6.setInteger("S", ((Integer)playerReputation.get(var9)).intValue());
            var7.appendTag(var6);
        }

        par1NBTTagCompound.setTag("Players", var7);
    }

    /**
     * Prevent villager breeding for a fixed interval of time
     */
    public void endMatingSeason()
    {
        noBreedTicks = tickCounter;
    }

    /**
     * Return whether villagers mating refractory period has passed
     */
    public boolean isMatingSeason()
    {
        return noBreedTicks == 0 || tickCounter - noBreedTicks >= 3600;
    }

    public void func_82683_b(int par1)
    {
        Iterator var2 = playerReputation.keySet().iterator();

        while (var2.hasNext())
        {
            String var3 = (String)var2.next();
            setReputationForPlayer(var3, par1);
        }
    }

    class VillageAgressor
    {
        public EntityLivingBase agressor;
        public int agressionTime;
        private static final String __OBFID = "CL_00001632";

        VillageAgressor(EntityLivingBase par2EntityLivingBase, int par3)
        {
            agressor = par2EntityLivingBase;
            agressionTime = par3;
        }
    }
}
