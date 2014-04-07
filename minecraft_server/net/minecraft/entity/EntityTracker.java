package net.minecraft.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker
{
    private static final Logger logger = LogManager.getLogger();
    private final WorldServer theWorld;

    /**
     * List of tracked entities, used for iteration operations on tracked
     * entities.
     */
    private Set trackedEntities = new HashSet();

    /** Used for identity lookup of tracked entities. */
    private IntHashMap trackedEntityHashTable = new IntHashMap();
    private int maxTrackingDistanceThreshold;
    private static final String __OBFID = "CL_00001431";

    public EntityTracker(WorldServer par1WorldServer)
    {
        theWorld = par1WorldServer;
        maxTrackingDistanceThreshold = par1WorldServer.func_73046_m().getConfigurationManager().getEntityViewDistance();
    }

    public void trackEntity(Entity par1Entity)
    {
        if (par1Entity instanceof EntityPlayerMP)
        {
            this.trackEntity(par1Entity, 512, 2);
            EntityPlayerMP var2 = (EntityPlayerMP)par1Entity;
            Iterator var3 = trackedEntities.iterator();

            while (var3.hasNext())
            {
                EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();

                if (var4.trackedEntity != var2)
                {
                    var4.updatePlayerEntity(var2);
                }
            }
        }
        else if (par1Entity instanceof EntityFishHook)
        {
            this.trackEntity(par1Entity, 64, 5, true);
        }
        else if (par1Entity instanceof EntityArrow)
        {
            this.trackEntity(par1Entity, 64, 20, false);
        }
        else if (par1Entity instanceof EntitySmallFireball)
        {
            this.trackEntity(par1Entity, 64, 10, false);
        }
        else if (par1Entity instanceof EntityFireball)
        {
            this.trackEntity(par1Entity, 64, 10, false);
        }
        else if (par1Entity instanceof EntitySnowball)
        {
            this.trackEntity(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityEnderPearl)
        {
            this.trackEntity(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityEnderEye)
        {
            this.trackEntity(par1Entity, 64, 4, true);
        }
        else if (par1Entity instanceof EntityEgg)
        {
            this.trackEntity(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityPotion)
        {
            this.trackEntity(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityExpBottle)
        {
            this.trackEntity(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityFireworkRocket)
        {
            this.trackEntity(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityItem)
        {
            this.trackEntity(par1Entity, 64, 20, true);
        }
        else if (par1Entity instanceof EntityMinecart)
        {
            this.trackEntity(par1Entity, 80, 3, true);
        }
        else if (par1Entity instanceof EntityBoat)
        {
            this.trackEntity(par1Entity, 80, 3, true);
        }
        else if (par1Entity instanceof EntitySquid)
        {
            this.trackEntity(par1Entity, 64, 3, true);
        }
        else if (par1Entity instanceof EntityWither)
        {
            this.trackEntity(par1Entity, 80, 3, false);
        }
        else if (par1Entity instanceof EntityBat)
        {
            this.trackEntity(par1Entity, 80, 3, false);
        }
        else if (par1Entity instanceof IAnimals)
        {
            this.trackEntity(par1Entity, 80, 3, true);
        }
        else if (par1Entity instanceof EntityDragon)
        {
            this.trackEntity(par1Entity, 160, 3, true);
        }
        else if (par1Entity instanceof EntityTNTPrimed)
        {
            this.trackEntity(par1Entity, 160, 10, true);
        }
        else if (par1Entity instanceof EntityFallingBlock)
        {
            this.trackEntity(par1Entity, 160, 20, true);
        }
        else if (par1Entity instanceof EntityHanging)
        {
            this.trackEntity(par1Entity, 160, Integer.MAX_VALUE, false);
        }
        else if (par1Entity instanceof EntityXPOrb)
        {
            this.trackEntity(par1Entity, 160, 20, true);
        }
        else if (par1Entity instanceof EntityEnderCrystal)
        {
            this.trackEntity(par1Entity, 256, Integer.MAX_VALUE, false);
        }
    }

    public void trackEntity(Entity par1Entity, int par2, int par3)
    {
        this.trackEntity(par1Entity, par2, par3, false);
    }

    public void trackEntity(Entity par1Entity, int par2, final int par3, boolean par4)
    {
        if (par2 > maxTrackingDistanceThreshold)
        {
            par2 = maxTrackingDistanceThreshold;
        }

        try
        {
            if (trackedEntityHashTable.containsItem(par1Entity.getEntityId())) { throw new IllegalStateException("Entity is already tracked!"); }

            EntityTrackerEntry var5 = new EntityTrackerEntry(par1Entity, par2, par3, par4);
            trackedEntities.add(var5);
            trackedEntityHashTable.addKey(par1Entity.getEntityId(), var5);
            var5.updatePlayerEntities(theWorld.playerEntities);
        }
        catch (Throwable var11)
        {
            CrashReport var6 = CrashReport.makeCrashReport(var11, "Adding entity to track");
            CrashReportCategory var7 = var6.makeCategory("Entity To Track");
            var7.addCrashSection("Tracking range", par2 + " blocks");
            var7.addCrashSectionCallable("Update interval", new Callable()
            {
                private static final String __OBFID = "CL_00001432";

                public String call()
                {
                    String var1 = "Once per " + par3 + " ticks";

                    if (par3 == Integer.MAX_VALUE)
                    {
                        var1 = "Maximum (" + var1 + ")";
                    }

                    return var1;
                }
            });
            par1Entity.addEntityCrashInfo(var7);
            CrashReportCategory var8 = var6.makeCategory("Entity That Is Already Tracked");
            ((EntityTrackerEntry)trackedEntityHashTable.lookup(par1Entity.getEntityId())).trackedEntity.addEntityCrashInfo(var8);

            try
            {
                throw new ReportedException(var6);
            }
            catch (ReportedException var10)
            {
                logger.error("\"Silently\" catching entity tracking error.", var10);
            }
        }
    }

    public void untrackEntity(Entity par1Entity)
    {
        if (par1Entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP var2 = (EntityPlayerMP)par1Entity;
            Iterator var3 = trackedEntities.iterator();

            while (var3.hasNext())
            {
                EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();
                var4.removeFromTrackedPlayers(var2);
            }
        }

        EntityTrackerEntry var5 = (EntityTrackerEntry)trackedEntityHashTable.removeObject(par1Entity.getEntityId());

        if (var5 != null)
        {
            trackedEntities.remove(var5);
            var5.sendDestroyEntityPacketToTrackedPlayers();
        }
    }

    public void updateTrackedEntities()
    {
        ArrayList var1 = new ArrayList();
        Iterator var2 = trackedEntities.iterator();

        while (var2.hasNext())
        {
            EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
            var3.updatePlayerList(theWorld.playerEntities);

            if (var3.playerEntitiesUpdated && var3.trackedEntity instanceof EntityPlayerMP)
            {
                var1.add(var3.trackedEntity);
            }
        }

        for (int var6 = 0; var6 < var1.size(); ++var6)
        {
            EntityPlayerMP var7 = (EntityPlayerMP)var1.get(var6);
            Iterator var4 = trackedEntities.iterator();

            while (var4.hasNext())
            {
                EntityTrackerEntry var5 = (EntityTrackerEntry)var4.next();

                if (var5.trackedEntity != var7)
                {
                    var5.updatePlayerEntity(var7);
                }
            }
        }
    }

    public void func_151247_a(Entity p_151247_1_, Packet p_151247_2_)
    {
        EntityTrackerEntry var3 = (EntityTrackerEntry)trackedEntityHashTable.lookup(p_151247_1_.getEntityId());

        if (var3 != null)
        {
            var3.func_151259_a(p_151247_2_);
        }
    }

    public void func_151248_b(Entity p_151248_1_, Packet p_151248_2_)
    {
        EntityTrackerEntry var3 = (EntityTrackerEntry)trackedEntityHashTable.lookup(p_151248_1_.getEntityId());

        if (var3 != null)
        {
            var3.func_151261_b(p_151248_2_);
        }
    }

    public void removePlayerFromTrackers(EntityPlayerMP par1EntityPlayerMP)
    {
        Iterator var2 = trackedEntities.iterator();

        while (var2.hasNext())
        {
            EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
            var3.removeTrackedPlayerSymmetric(par1EntityPlayerMP);
        }
    }

    public void func_85172_a(EntityPlayerMP par1EntityPlayerMP, Chunk par2Chunk)
    {
        Iterator var3 = trackedEntities.iterator();

        while (var3.hasNext())
        {
            EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();

            if (var4.trackedEntity != par1EntityPlayerMP && var4.trackedEntity.chunkCoordX == par2Chunk.xPosition && var4.trackedEntity.chunkCoordZ == par2Chunk.zPosition)
            {
                var4.updatePlayerEntity(par1EntityPlayerMP);
            }
        }
    }
}
