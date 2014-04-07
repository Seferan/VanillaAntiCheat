package net.minecraft.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic
{
    /** The delay to spawn. */
    public int spawnDelay = 20;
    private String mobID = "Pig";

    /** List of minecart to spawn. */
    private List minecartToSpawn;
    private MobSpawnerBaseLogic.WeightedRandomMinecart randomMinecart;
    public double field_98287_c;
    public double field_98284_d;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    private Entity field_98291_j;
    private int maxNearbyEntities = 6;

    /** The distance from which a player activates the spawner. */
    private int activatingRangeFromPlayer = 16;

    /** The range coefficient for spawning entities around. */
    private int spawnRange = 4;
    private static final String __OBFID = "CL_00000129";

    /**
     * Gets the entity name that should be spawned.
     */
    public String getEntityNameToSpawn()
    {
        if (getRandomMinecart() == null)
        {
            if (mobID.equals("Minecart"))
            {
                mobID = "MinecartRideable";
            }

            return mobID;
        }
        else
        {
            return getRandomMinecart().minecartName;
        }
    }

    public void setMobID(String par1Str)
    {
        mobID = par1Str;
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to
     * activate it.
     */
    public boolean canRun()
    {
        return getSpawnerWorld().getClosestPlayer(getSpawnerX() + 0.5D, getSpawnerY() + 0.5D, getSpawnerZ() + 0.5D, activatingRangeFromPlayer) != null;
    }

    public void updateSpawner()
    {
        if (canRun())
        {
            double var5;

            if (getSpawnerWorld().isClient)
            {
                double var1 = getSpawnerX() + getSpawnerWorld().rand.nextFloat();
                double var3 = getSpawnerY() + getSpawnerWorld().rand.nextFloat();
                var5 = getSpawnerZ() + getSpawnerWorld().rand.nextFloat();
                getSpawnerWorld().spawnParticle("smoke", var1, var3, var5, 0.0D, 0.0D, 0.0D);
                getSpawnerWorld().spawnParticle("flame", var1, var3, var5, 0.0D, 0.0D, 0.0D);

                if (spawnDelay > 0)
                {
                    --spawnDelay;
                }

                field_98284_d = field_98287_c;
                field_98287_c = (field_98287_c + 1000.0F / (spawnDelay + 200.0F)) % 360.0D;
            }
            else
            {
                if (spawnDelay == -1)
                {
                    func_98273_j();
                }

                if (spawnDelay > 0)
                {
                    --spawnDelay;
                    return;
                }

                boolean var12 = false;

                for (int var2 = 0; var2 < spawnCount; ++var2)
                {
                    Entity var13 = EntityList.createEntityByName(getEntityNameToSpawn(), getSpawnerWorld());

                    if (var13 == null) { return; }

                    int var4 = getSpawnerWorld().getEntitiesWithinAABB(var13.getClass(), AxisAlignedBB.getAABBPool().getAABB(getSpawnerX(), getSpawnerY(), getSpawnerZ(), getSpawnerX() + 1, getSpawnerY() + 1, getSpawnerZ() + 1).expand(spawnRange * 2, 4.0D, spawnRange * 2)).size();

                    if (var4 >= maxNearbyEntities)
                    {
                        func_98273_j();
                        return;
                    }

                    var5 = getSpawnerX() + (getSpawnerWorld().rand.nextDouble() - getSpawnerWorld().rand.nextDouble()) * spawnRange;
                    double var7 = getSpawnerY() + getSpawnerWorld().rand.nextInt(3) - 1;
                    double var9 = getSpawnerZ() + (getSpawnerWorld().rand.nextDouble() - getSpawnerWorld().rand.nextDouble()) * spawnRange;
                    EntityLiving var11 = var13 instanceof EntityLiving ? (EntityLiving)var13 : null;
                    var13.setLocationAndAngles(var5, var7, var9, getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);

                    if (var11 == null || var11.getCanSpawnHere())
                    {
                        func_98265_a(var13);
                        getSpawnerWorld().playAuxSFX(2004, getSpawnerX(), getSpawnerY(), getSpawnerZ(), 0);

                        if (var11 != null)
                        {
                            var11.spawnExplosionParticle();
                        }

                        var12 = true;
                    }
                }

                if (var12)
                {
                    func_98273_j();
                }
            }
        }
    }

    public Entity func_98265_a(Entity par1Entity)
    {
        if (getRandomMinecart() != null)
        {
            NBTTagCompound var2 = new NBTTagCompound();
            par1Entity.writeToNBTOptional(var2);
            Iterator var3 = getRandomMinecart().field_98222_b.func_150296_c().iterator();

            while (var3.hasNext())
            {
                String var4 = (String)var3.next();
                NBTBase var5 = getRandomMinecart().field_98222_b.getTag(var4);
                var2.setTag(var4, var5.copy());
            }

            par1Entity.readFromNBT(var2);

            if (par1Entity.worldObj != null)
            {
                par1Entity.worldObj.spawnEntityInWorld(par1Entity);
            }

            NBTTagCompound var11;

            for (Entity var10 = par1Entity; var2.func_150297_b("Riding", 10); var2 = var11)
            {
                var11 = var2.getCompoundTag("Riding");
                Entity var12 = EntityList.createEntityByName(var11.getString("id"), par1Entity.worldObj);

                if (var12 != null)
                {
                    NBTTagCompound var6 = new NBTTagCompound();
                    var12.writeToNBTOptional(var6);
                    Iterator var7 = var11.func_150296_c().iterator();

                    while (var7.hasNext())
                    {
                        String var8 = (String)var7.next();
                        NBTBase var9 = var11.getTag(var8);
                        var6.setTag(var8, var9.copy());
                    }

                    var12.readFromNBT(var6);
                    var12.setLocationAndAngles(var10.posX, var10.posY, var10.posZ, var10.rotationYaw, var10.rotationPitch);

                    if (par1Entity.worldObj != null)
                    {
                        par1Entity.worldObj.spawnEntityInWorld(var12);
                    }

                    var10.mountEntity(var12);
                }

                var10 = var12;
            }
        }
        else if (par1Entity instanceof EntityLivingBase && par1Entity.worldObj != null)
        {
            ((EntityLiving)par1Entity).onSpawnWithEgg((IEntityLivingData)null);
            getSpawnerWorld().spawnEntityInWorld(par1Entity);
        }

        return par1Entity;
    }

    private void func_98273_j()
    {
        if (maxSpawnDelay <= minSpawnDelay)
        {
            spawnDelay = minSpawnDelay;
        }
        else
        {
            int var10003 = maxSpawnDelay - minSpawnDelay;
            spawnDelay = minSpawnDelay + getSpawnerWorld().rand.nextInt(var10003);
        }

        if (minecartToSpawn != null && minecartToSpawn.size() > 0)
        {
            setRandomMinecart((MobSpawnerBaseLogic.WeightedRandomMinecart)WeightedRandom.getRandomItem(getSpawnerWorld().rand, minecartToSpawn));
        }

        func_98267_a(1);
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        mobID = par1NBTTagCompound.getString("EntityId");
        spawnDelay = par1NBTTagCompound.getShort("Delay");

        if (par1NBTTagCompound.func_150297_b("SpawnPotentials", 9))
        {
            minecartToSpawn = new ArrayList();
            NBTTagList var2 = par1NBTTagCompound.getTagList("SpawnPotentials", 10);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(var2.getCompoundTagAt(var3)));
            }
        }
        else
        {
            minecartToSpawn = null;
        }

        if (par1NBTTagCompound.func_150297_b("SpawnData", 10))
        {
            setRandomMinecart(new MobSpawnerBaseLogic.WeightedRandomMinecart(par1NBTTagCompound.getCompoundTag("SpawnData"), mobID));
        }
        else
        {
            setRandomMinecart((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
        }

        if (par1NBTTagCompound.func_150297_b("MinSpawnDelay", 99))
        {
            minSpawnDelay = par1NBTTagCompound.getShort("MinSpawnDelay");
            maxSpawnDelay = par1NBTTagCompound.getShort("MaxSpawnDelay");
            spawnCount = par1NBTTagCompound.getShort("SpawnCount");
        }

        if (par1NBTTagCompound.func_150297_b("MaxNearbyEntities", 99))
        {
            maxNearbyEntities = par1NBTTagCompound.getShort("MaxNearbyEntities");
            activatingRangeFromPlayer = par1NBTTagCompound.getShort("RequiredPlayerRange");
        }

        if (par1NBTTagCompound.func_150297_b("SpawnRange", 99))
        {
            spawnRange = par1NBTTagCompound.getShort("SpawnRange");
        }

        if (getSpawnerWorld() != null && getSpawnerWorld().isClient)
        {
            field_98291_j = null;
        }
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setString("EntityId", getEntityNameToSpawn());
        par1NBTTagCompound.setShort("Delay", (short)spawnDelay);
        par1NBTTagCompound.setShort("MinSpawnDelay", (short)minSpawnDelay);
        par1NBTTagCompound.setShort("MaxSpawnDelay", (short)maxSpawnDelay);
        par1NBTTagCompound.setShort("SpawnCount", (short)spawnCount);
        par1NBTTagCompound.setShort("MaxNearbyEntities", (short)maxNearbyEntities);
        par1NBTTagCompound.setShort("RequiredPlayerRange", (short)activatingRangeFromPlayer);
        par1NBTTagCompound.setShort("SpawnRange", (short)spawnRange);

        if (getRandomMinecart() != null)
        {
            par1NBTTagCompound.setTag("SpawnData", getRandomMinecart().field_98222_b.copy());
        }

        if (getRandomMinecart() != null || minecartToSpawn != null && minecartToSpawn.size() > 0)
        {
            NBTTagList var2 = new NBTTagList();

            if (minecartToSpawn != null && minecartToSpawn.size() > 0)
            {
                Iterator var3 = minecartToSpawn.iterator();

                while (var3.hasNext())
                {
                    MobSpawnerBaseLogic.WeightedRandomMinecart var4 = (MobSpawnerBaseLogic.WeightedRandomMinecart)var3.next();
                    var2.appendTag(var4.func_98220_a());
                }
            }
            else
            {
                var2.appendTag(getRandomMinecart().func_98220_a());
            }

            par1NBTTagCompound.setTag("SpawnPotentials", var2);
        }
    }

    /**
     * Sets the delay to minDelay if parameter given is 1, else return false.
     */
    public boolean setDelayToMin(int par1)
    {
        if (par1 == 1 && getSpawnerWorld().isClient)
        {
            spawnDelay = minSpawnDelay;
            return true;
        }
        else
        {
            return false;
        }
    }

    public MobSpawnerBaseLogic.WeightedRandomMinecart getRandomMinecart()
    {
        return randomMinecart;
    }

    public void setRandomMinecart(MobSpawnerBaseLogic.WeightedRandomMinecart par1WeightedRandomMinecart)
    {
        randomMinecart = par1WeightedRandomMinecart;
    }

    public abstract void func_98267_a(int var1);

    public abstract World getSpawnerWorld();

    public abstract int getSpawnerX();

    public abstract int getSpawnerY();

    public abstract int getSpawnerZ();

    public class WeightedRandomMinecart extends WeightedRandom.Item
    {
        public final NBTTagCompound field_98222_b;
        public final String minecartName;
        private static final String __OBFID = "CL_00000130";

        public WeightedRandomMinecart(NBTTagCompound par2NBTTagCompound)
        {
            super(par2NBTTagCompound.getInteger("Weight"));
            NBTTagCompound var3 = par2NBTTagCompound.getCompoundTag("Properties");
            String var4 = par2NBTTagCompound.getString("Type");

            if (var4.equals("Minecart"))
            {
                if (var3 != null)
                {
                    switch (var3.getInteger("Type"))
                    {
                    case 0:
                        var4 = "MinecartRideable";
                        break;

                    case 1:
                        var4 = "MinecartChest";
                        break;

                    case 2:
                        var4 = "MinecartFurnace";
                    }
                }
                else
                {
                    var4 = "MinecartRideable";
                }
            }

            field_98222_b = var3;
            minecartName = var4;
        }

        public WeightedRandomMinecart(NBTTagCompound par2NBTTagCompound, String par3Str)
        {
            super(1);

            if (par3Str.equals("Minecart"))
            {
                if (par2NBTTagCompound != null)
                {
                    switch (par2NBTTagCompound.getInteger("Type"))
                    {
                    case 0:
                        par3Str = "MinecartRideable";
                        break;

                    case 1:
                        par3Str = "MinecartChest";
                        break;

                    case 2:
                        par3Str = "MinecartFurnace";
                    }
                }
                else
                {
                    par3Str = "MinecartRideable";
                }
            }

            field_98222_b = par2NBTTagCompound;
            minecartName = par3Str;
        }

        public NBTTagCompound func_98220_a()
        {
            NBTTagCompound var1 = new NBTTagCompound();
            var1.setTag("Properties", field_98222_b);
            var1.setString("Type", minecartName);
            var1.setInteger("Weight", itemWeight);
            return var1;
        }
    }
}
