package net.minecraft.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry
{
    private static final Logger logger = LogManager.getLogger();

    /** The entity that this EntityTrackerEntry tracks. */
    public Entity trackedEntity;
    public int trackingDistanceThreshold;

    /** check for sync when ticks % updateFrequency==0 */
    public int updateFrequency;

    /** The encoded entity X position. */
    public int encodedPosX;

    /** The encoded entity Y position. */
    public int encodedPosY;

    /** The encoded entity Z position. */
    public int encodedPosZ;

    /** The encoded entity yaw rotation. */
    public int encodedRotationYaw;

    /** The encoded entity pitch rotation. */
    public int encodedRotationPitch;
    public int lastHeadMotion;
    public double lastTrackedEntityMotionX;
    public double lastTrackedEntityMotionY;
    public double motionZ;
    public int updateCounter;
    private double lastTrackedEntityPosX;
    private double lastTrackedEntityPosY;
    private double lastTrackedEntityPosZ;
    private boolean firstUpdateDone;
    private boolean sendVelocityUpdates;

    /**
     * every 400 ticks a full teleport packet is sent, rather than just a
     * "move me +x" command, so that position remains fully synced.
     */
    private int ticksSinceLastForcedTeleport;
    private Entity field_85178_v;
    private boolean ridingEntity;
    public boolean playerEntitiesUpdated;

    /**
     * Holds references to all the players that are currently receiving position
     * updates for this entity.
     */
    public Set trackingPlayers = new HashSet();
    private static final String __OBFID = "CL_00001443";

    public EntityTrackerEntry(Entity par1Entity, int par2, int par3, boolean par4)
    {
        trackedEntity = par1Entity;
        trackingDistanceThreshold = par2;
        updateFrequency = par3;
        sendVelocityUpdates = par4;
        encodedPosX = MathHelper.floor_double(par1Entity.posX * 32.0D);
        encodedPosY = MathHelper.floor_double(par1Entity.posY * 32.0D);
        encodedPosZ = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        encodedRotationYaw = MathHelper.floor_float(par1Entity.rotationYaw * 256.0F / 360.0F);
        encodedRotationPitch = MathHelper.floor_float(par1Entity.rotationPitch * 256.0F / 360.0F);
        lastHeadMotion = MathHelper.floor_float(par1Entity.getRotationYawHead() * 256.0F / 360.0F);
    }

    public boolean equals(Object par1Obj)
    {
        return par1Obj instanceof EntityTrackerEntry ? ((EntityTrackerEntry)par1Obj).trackedEntity.getEntityId() == trackedEntity.getEntityId() : false;
    }

    public int hashCode()
    {
        return trackedEntity.getEntityId();
    }

    public void updatePlayerList(List par1List)
    {
        playerEntitiesUpdated = false;

        if (!firstUpdateDone || trackedEntity.getDistanceSq(lastTrackedEntityPosX, lastTrackedEntityPosY, lastTrackedEntityPosZ) > 16.0D)
        {
            lastTrackedEntityPosX = trackedEntity.posX;
            lastTrackedEntityPosY = trackedEntity.posY;
            lastTrackedEntityPosZ = trackedEntity.posZ;
            firstUpdateDone = true;
            playerEntitiesUpdated = true;
            updatePlayerEntities(par1List);
        }

        if (field_85178_v != trackedEntity.ridingEntity || trackedEntity.ridingEntity != null && updateCounter % 60 == 0)
        {
            field_85178_v = trackedEntity.ridingEntity;
            func_151259_a(new S1BPacketEntityAttach(0, trackedEntity, trackedEntity.ridingEntity));
        }

        if (trackedEntity instanceof EntityItemFrame && updateCounter % 10 == 0)
        {
            EntityItemFrame var23 = (EntityItemFrame)trackedEntity;
            ItemStack var24 = var23.getDisplayedItem();

            if (var24 != null && var24.getItem() instanceof ItemMap)
            {
                MapData var26 = Items.filled_map.getMapData(var24, trackedEntity.worldObj);
                Iterator var27 = par1List.iterator();

                while (var27.hasNext())
                {
                    EntityPlayer var28 = (EntityPlayer)var27.next();
                    EntityPlayerMP var29 = (EntityPlayerMP)var28;
                    var26.updateVisiblePlayers(var29, var24);
                    Packet var30 = Items.filled_map.func_150911_c(var24, trackedEntity.worldObj, var29);

                    if (var30 != null)
                    {
                        var29.playerNetServerHandler.sendPacket(var30);
                    }
                }
            }

            sendMetadataToAllAssociatedPlayers();
        }
        else if (updateCounter % updateFrequency == 0 || trackedEntity.isAirBorne || trackedEntity.getDataWatcher().hasObjectChanged())
        {
            int var2;
            int var3;

            if (trackedEntity.ridingEntity == null)
            {
                ++ticksSinceLastForcedTeleport;
                var2 = trackedEntity.myEntitySize.multiplyBy32AndRound(trackedEntity.posX);
                var3 = MathHelper.floor_double(trackedEntity.posY * 32.0D);
                int var4 = trackedEntity.myEntitySize.multiplyBy32AndRound(trackedEntity.posZ);
                int var5 = MathHelper.floor_float(trackedEntity.rotationYaw * 256.0F / 360.0F);
                int var6 = MathHelper.floor_float(trackedEntity.rotationPitch * 256.0F / 360.0F);
                int var7 = var2 - encodedPosX;
                int var8 = var3 - encodedPosY;
                int var9 = var4 - encodedPosZ;
                Object var10 = null;
                boolean var11 = Math.abs(var7) >= 4 || Math.abs(var8) >= 4 || Math.abs(var9) >= 4 || updateCounter % 60 == 0;
                boolean var12 = Math.abs(var5 - encodedRotationYaw) >= 4 || Math.abs(var6 - encodedRotationPitch) >= 4;

                if (updateCounter > 0 || trackedEntity instanceof EntityArrow)
                {
                    if (var7 >= -128 && var7 < 128 && var8 >= -128 && var8 < 128 && var9 >= -128 && var9 < 128 && ticksSinceLastForcedTeleport <= 400 && !ridingEntity)
                    {
                        if (var11 && var12)
                        {
                            var10 = new S14PacketEntity.S17PacketEntityLookMove(trackedEntity.getEntityId(), (byte)var7, (byte)var8, (byte)var9, (byte)var5, (byte)var6);
                        }
                        else if (var11)
                        {
                            var10 = new S14PacketEntity.S15PacketEntityRelMove(trackedEntity.getEntityId(), (byte)var7, (byte)var8, (byte)var9);
                        }
                        else if (var12)
                        {
                            var10 = new S14PacketEntity.S16PacketEntityLook(trackedEntity.getEntityId(), (byte)var5, (byte)var6);
                        }
                    }
                    else
                    {
                        ticksSinceLastForcedTeleport = 0;
                        var10 = new S18PacketEntityTeleport(trackedEntity.getEntityId(), var2, var3, var4, (byte)var5, (byte)var6);
                    }
                }

                if (sendVelocityUpdates)
                {
                    double var13 = trackedEntity.motionX - lastTrackedEntityMotionX;
                    double var15 = trackedEntity.motionY - lastTrackedEntityMotionY;
                    double var17 = trackedEntity.motionZ - motionZ;
                    double var19 = 0.02D;
                    double var21 = var13 * var13 + var15 * var15 + var17 * var17;

                    if (var21 > var19 * var19 || var21 > 0.0D && trackedEntity.motionX == 0.0D && trackedEntity.motionY == 0.0D && trackedEntity.motionZ == 0.0D)
                    {
                        lastTrackedEntityMotionX = trackedEntity.motionX;
                        lastTrackedEntityMotionY = trackedEntity.motionY;
                        motionZ = trackedEntity.motionZ;
                        func_151259_a(new S12PacketEntityVelocity(trackedEntity.getEntityId(), lastTrackedEntityMotionX, lastTrackedEntityMotionY, motionZ));
                    }
                }

                if (var10 != null)
                {
                    func_151259_a((Packet)var10);
                }

                sendMetadataToAllAssociatedPlayers();

                if (var11)
                {
                    encodedPosX = var2;
                    encodedPosY = var3;
                    encodedPosZ = var4;
                }

                if (var12)
                {
                    encodedRotationYaw = var5;
                    encodedRotationPitch = var6;
                }

                ridingEntity = false;
            }
            else
            {
                var2 = MathHelper.floor_float(trackedEntity.rotationYaw * 256.0F / 360.0F);
                var3 = MathHelper.floor_float(trackedEntity.rotationPitch * 256.0F / 360.0F);
                boolean var25 = Math.abs(var2 - encodedRotationYaw) >= 4 || Math.abs(var3 - encodedRotationPitch) >= 4;

                if (var25)
                {
                    func_151259_a(new S14PacketEntity.S16PacketEntityLook(trackedEntity.getEntityId(), (byte)var2, (byte)var3));
                    encodedRotationYaw = var2;
                    encodedRotationPitch = var3;
                }

                encodedPosX = trackedEntity.myEntitySize.multiplyBy32AndRound(trackedEntity.posX);
                encodedPosY = MathHelper.floor_double(trackedEntity.posY * 32.0D);
                encodedPosZ = trackedEntity.myEntitySize.multiplyBy32AndRound(trackedEntity.posZ);
                sendMetadataToAllAssociatedPlayers();
                ridingEntity = true;
            }

            var2 = MathHelper.floor_float(trackedEntity.getRotationYawHead() * 256.0F / 360.0F);

            if (Math.abs(var2 - lastHeadMotion) >= 4)
            {
                func_151259_a(new S19PacketEntityHeadLook(trackedEntity, (byte)var2));
                lastHeadMotion = var2;
            }

            trackedEntity.isAirBorne = false;
        }

        ++updateCounter;

        if (trackedEntity.velocityChanged)
        {
            func_151261_b(new S12PacketEntityVelocity(trackedEntity));
            trackedEntity.velocityChanged = false;
        }
    }

    /**
     * Sends the entity metadata (DataWatcher) and attributes to all players
     * tracking this entity, including the entity itself if a player.
     */
    private void sendMetadataToAllAssociatedPlayers()
    {
        DataWatcher var1 = trackedEntity.getDataWatcher();

        if (var1.hasObjectChanged())
        {
            func_151261_b(new S1CPacketEntityMetadata(trackedEntity.getEntityId(), var1, false));
        }

        if (trackedEntity instanceof EntityLivingBase)
        {
            ServersideAttributeMap var2 = (ServersideAttributeMap)((EntityLivingBase)trackedEntity).getAttributeMap();
            Set var3 = var2.getAttributeInstanceSet();

            if (!var3.isEmpty())
            {
                func_151261_b(new S20PacketEntityProperties(trackedEntity.getEntityId(), var3));
            }

            var3.clear();
        }
    }

    public void func_151259_a(Packet p_151259_1_)
    {
        Iterator var2 = trackingPlayers.iterator();

        while (var2.hasNext())
        {
            EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
            var3.playerNetServerHandler.sendPacket(p_151259_1_);
        }
    }

    public void func_151261_b(Packet p_151261_1_)
    {
        func_151259_a(p_151261_1_);

        if (trackedEntity instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)trackedEntity).playerNetServerHandler.sendPacket(p_151261_1_);
        }
    }

    public void sendDestroyEntityPacketToTrackedPlayers()
    {
        Iterator var1 = trackingPlayers.iterator();

        while (var1.hasNext())
        {
            EntityPlayerMP var2 = (EntityPlayerMP)var1.next();
            var2.destroyedItemsNetCache.add(Integer.valueOf(trackedEntity.getEntityId()));
        }
    }

    public void removeFromTrackedPlayers(EntityPlayerMP par1EntityPlayerMP)
    {
        if (trackingPlayers.contains(par1EntityPlayerMP))
        {
            par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(trackedEntity.getEntityId()));
            trackingPlayers.remove(par1EntityPlayerMP);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP par1EntityPlayerMP)
    {
        if (par1EntityPlayerMP != trackedEntity)
        {
            double var2 = par1EntityPlayerMP.posX - encodedPosX / 32;
            double var4 = par1EntityPlayerMP.posZ - encodedPosZ / 32;

            if (var2 >= (-trackingDistanceThreshold) && var2 <= trackingDistanceThreshold && var4 >= (-trackingDistanceThreshold) && var4 <= trackingDistanceThreshold)
            {
                if (!trackingPlayers.contains(par1EntityPlayerMP) && (isPlayerWatchingThisChunk(par1EntityPlayerMP) || trackedEntity.forceSpawn))
                {
                    trackingPlayers.add(par1EntityPlayerMP);
                    Packet var6 = func_151260_c();
                    par1EntityPlayerMP.playerNetServerHandler.sendPacket(var6);

                    if (!trackedEntity.getDataWatcher().getIsBlank())
                    {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S1CPacketEntityMetadata(trackedEntity.getEntityId(), trackedEntity.getDataWatcher(), true));
                    }

                    if (trackedEntity instanceof EntityLivingBase)
                    {
                        ServersideAttributeMap var7 = (ServersideAttributeMap)((EntityLivingBase)trackedEntity).getAttributeMap();
                        Collection var8 = var7.getWatchedAttributes();

                        if (!var8.isEmpty())
                        {
                            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S20PacketEntityProperties(trackedEntity.getEntityId(), var8));
                        }
                    }

                    lastTrackedEntityMotionX = trackedEntity.motionX;
                    lastTrackedEntityMotionY = trackedEntity.motionY;
                    motionZ = trackedEntity.motionZ;

                    if (sendVelocityUpdates && !(var6 instanceof S0FPacketSpawnMob))
                    {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(trackedEntity.getEntityId(), trackedEntity.motionX, trackedEntity.motionY, trackedEntity.motionZ));
                    }

                    if (trackedEntity.ridingEntity != null)
                    {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, trackedEntity, trackedEntity.ridingEntity));
                    }

                    if (trackedEntity instanceof EntityLiving && ((EntityLiving)trackedEntity).getLeashedToEntity() != null)
                    {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(1, trackedEntity, ((EntityLiving)trackedEntity).getLeashedToEntity()));
                    }

                    if (trackedEntity instanceof EntityLivingBase)
                    {
                        for (int var10 = 0; var10 < 5; ++var10)
                        {
                            ItemStack var13 = ((EntityLivingBase)trackedEntity).getEquipmentInSlot(var10);

                            if (var13 != null)
                            {
                                par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S04PacketEntityEquipment(trackedEntity.getEntityId(), var10, var13));
                            }
                        }
                    }

                    if (trackedEntity instanceof EntityPlayer)
                    {
                        EntityPlayer var11 = (EntityPlayer)trackedEntity;

                        if (var11.isPlayerSleeping())
                        {
                            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S0APacketUseBed(var11, MathHelper.floor_double(trackedEntity.posX), MathHelper.floor_double(trackedEntity.posY), MathHelper.floor_double(trackedEntity.posZ)));
                        }
                    }

                    if (trackedEntity instanceof EntityLivingBase)
                    {
                        EntityLivingBase var14 = (EntityLivingBase)trackedEntity;
                        Iterator var12 = var14.getActivePotionEffects().iterator();

                        while (var12.hasNext())
                        {
                            PotionEffect var9 = (PotionEffect)var12.next();
                            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(trackedEntity.getEntityId(), var9));
                        }
                    }
                }
            }
            else if (trackingPlayers.contains(par1EntityPlayerMP))
            {
                trackingPlayers.remove(par1EntityPlayerMP);
                par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(trackedEntity.getEntityId()));
            }
        }
    }

    private boolean isPlayerWatchingThisChunk(EntityPlayerMP par1EntityPlayerMP)
    {
        return par1EntityPlayerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(par1EntityPlayerMP, trackedEntity.chunkCoordX, trackedEntity.chunkCoordZ);
    }

    public void updatePlayerEntities(List par1List)
    {
        for (int var2 = 0; var2 < par1List.size(); ++var2)
        {
            updatePlayerEntity((EntityPlayerMP)par1List.get(var2));
        }
    }

    private Packet func_151260_c()
    {
        if (trackedEntity.isDead)
        {
            logger.warn("Fetching addPacket for removed entity");
        }

        if (trackedEntity instanceof EntityItem)
        {
            return new S0EPacketSpawnObject(trackedEntity, 2, 1);
        }
        else if (trackedEntity instanceof EntityPlayerMP)
        {
            return new S0CPacketSpawnPlayer((EntityPlayer)trackedEntity);
        }
        else if (trackedEntity instanceof EntityMinecart)
        {
            EntityMinecart var9 = (EntityMinecart)trackedEntity;
            return new S0EPacketSpawnObject(trackedEntity, 10, var9.getMinecartType());
        }
        else if (trackedEntity instanceof EntityBoat)
        {
            return new S0EPacketSpawnObject(trackedEntity, 1);
        }
        else if (!(trackedEntity instanceof IAnimals) && !(trackedEntity instanceof EntityDragon))
        {
            if (trackedEntity instanceof EntityFishHook)
            {
                EntityPlayer var8 = ((EntityFishHook)trackedEntity).field_146042_b;
                return new S0EPacketSpawnObject(trackedEntity, 90, var8 != null ? var8.getEntityId() : trackedEntity.getEntityId());
            }
            else if (trackedEntity instanceof EntityArrow)
            {
                Entity var7 = ((EntityArrow)trackedEntity).shootingEntity;
                return new S0EPacketSpawnObject(trackedEntity, 60, var7 != null ? var7.getEntityId() : trackedEntity.getEntityId());
            }
            else if (trackedEntity instanceof EntitySnowball)
            {
                return new S0EPacketSpawnObject(trackedEntity, 61);
            }
            else if (trackedEntity instanceof EntityPotion)
            {
                return new S0EPacketSpawnObject(trackedEntity, 73, ((EntityPotion)trackedEntity).getPotionDamage());
            }
            else if (trackedEntity instanceof EntityExpBottle)
            {
                return new S0EPacketSpawnObject(trackedEntity, 75);
            }
            else if (trackedEntity instanceof EntityEnderPearl)
            {
                return new S0EPacketSpawnObject(trackedEntity, 65);
            }
            else if (trackedEntity instanceof EntityEnderEye)
            {
                return new S0EPacketSpawnObject(trackedEntity, 72);
            }
            else if (trackedEntity instanceof EntityFireworkRocket)
            {
                return new S0EPacketSpawnObject(trackedEntity, 76);
            }
            else
            {
                S0EPacketSpawnObject var2;

                if (trackedEntity instanceof EntityFireball)
                {
                    EntityFireball var6 = (EntityFireball)trackedEntity;
                    var2 = null;
                    byte var3 = 63;

                    if (trackedEntity instanceof EntitySmallFireball)
                    {
                        var3 = 64;
                    }
                    else if (trackedEntity instanceof EntityWitherSkull)
                    {
                        var3 = 66;
                    }

                    if (var6.shootingEntity != null)
                    {
                        var2 = new S0EPacketSpawnObject(trackedEntity, var3, ((EntityFireball)trackedEntity).shootingEntity.getEntityId());
                    }
                    else
                    {
                        var2 = new S0EPacketSpawnObject(trackedEntity, var3, 0);
                    }

                    var2.func_149003_d((int)(var6.accelerationX * 8000.0D));
                    var2.func_149000_e((int)(var6.accelerationY * 8000.0D));
                    var2.func_149007_f((int)(var6.accelerationZ * 8000.0D));
                    return var2;
                }
                else if (trackedEntity instanceof EntityEgg)
                {
                    return new S0EPacketSpawnObject(trackedEntity, 62);
                }
                else if (trackedEntity instanceof EntityTNTPrimed)
                {
                    return new S0EPacketSpawnObject(trackedEntity, 50);
                }
                else if (trackedEntity instanceof EntityEnderCrystal)
                {
                    return new S0EPacketSpawnObject(trackedEntity, 51);
                }
                else if (trackedEntity instanceof EntityFallingBlock)
                {
                    EntityFallingBlock var5 = (EntityFallingBlock)trackedEntity;
                    return new S0EPacketSpawnObject(trackedEntity, 70, Block.getIdFromBlock(var5.func_145805_f()) | var5.field_145814_a << 16);
                }
                else if (trackedEntity instanceof EntityPainting)
                {
                    return new S10PacketSpawnPainting((EntityPainting)trackedEntity);
                }
                else if (trackedEntity instanceof EntityItemFrame)
                {
                    EntityItemFrame var4 = (EntityItemFrame)trackedEntity;
                    var2 = new S0EPacketSpawnObject(trackedEntity, 71, var4.hangingDirection);
                    var2.func_148996_a(MathHelper.floor_float(var4.field_146063_b * 32));
                    var2.func_148995_b(MathHelper.floor_float(var4.field_146064_c * 32));
                    var2.func_149005_c(MathHelper.floor_float(var4.field_146062_d * 32));
                    return var2;
                }
                else if (trackedEntity instanceof EntityLeashKnot)
                {
                    EntityLeashKnot var1 = (EntityLeashKnot)trackedEntity;
                    var2 = new S0EPacketSpawnObject(trackedEntity, 77);
                    var2.func_148996_a(MathHelper.floor_float(var1.field_146063_b * 32));
                    var2.func_148995_b(MathHelper.floor_float(var1.field_146064_c * 32));
                    var2.func_149005_c(MathHelper.floor_float(var1.field_146062_d * 32));
                    return var2;
                }
                else if (trackedEntity instanceof EntityXPOrb)
                {
                    return new S11PacketSpawnExperienceOrb((EntityXPOrb)trackedEntity);
                }
                else
                {
                    throw new IllegalArgumentException("Don\'t know how to add " + trackedEntity.getClass() + "!");
                }
            }
        }
        else
        {
            lastHeadMotion = MathHelper.floor_float(trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
            return new S0FPacketSpawnMob((EntityLivingBase)trackedEntity);
        }
    }

    /**
     * Remove a tracked player from our list and tell the tracked player to
     * destroy us from their world.
     */
    public void removeTrackedPlayerSymmetric(EntityPlayerMP par1EntityPlayerMP)
    {
        if (trackingPlayers.contains(par1EntityPlayerMP))
        {
            trackingPlayers.remove(par1EntityPlayerMP);
            par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(trackedEntity.getEntityId()));
        }
    }
}
