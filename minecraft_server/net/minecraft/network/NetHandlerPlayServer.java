package net.minecraft.network;

import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;

import mx.x10.afffsdd.vanillaanticheat.VACUtils;
import mx.x10.afffsdd.vanillaanticheat.module.VACState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

public class NetHandlerPlayServer implements INetHandlerPlayServer
{
    private static final Logger logger = LogManager.getLogger();
    public final NetworkManager netManager;
    private final MinecraftServer serverController;
    public EntityPlayerMP playerEntity;
    private int networkTickCount;

    /**
     * Used to keep track of how the player is floating while gamerules should
     * prevent that. Surpassing 80 ticks means kick
     */
    private int floatingTickCount;
    private boolean field_147366_g;
    private int field_147378_h;
    private long field_147379_i;
    private static Random field_147376_j = new Random();
    private long field_147377_k;

    /**
     * Incremented by 20 each time a user sends a chat message, decreased by one
     * every tick. Non-ops kicked when over 200
     */
    private int chatSpamThresholdCount;
    private int field_147375_m;
    private IntHashMap field_147372_n = new IntHashMap();
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private boolean hasMoved = true;
    private static final String __OBFID = "CL_00001452";

    private VACState vacState;

    public NetHandlerPlayServer(MinecraftServer par1MinecraftServer, NetworkManager par2INetworkManager, EntityPlayerMP par3EntityPlayerMP)
    {
        serverController = par1MinecraftServer;
        netManager = par2INetworkManager;
        par2INetworkManager.setNetHandler(this);
        playerEntity = par3EntityPlayerMP;
        par3EntityPlayerMP.playerNetServerHandler = this;
        vacState = new VACState();
    }

    public VACState getVACState()
    {
        return vacState;
    }

    /**
     * For scheduled network tasks. Used in NetHandlerPlayServer to send
     * keep-alive packets and in NetHandlerLoginServer for a login-timeout
     */
    // ALSO KNOWN AS HANDLEPACKETS FROM A WHILE AGO
    public void onNetworkTick()
    {
        field_147366_g = false;
        ++networkTickCount;
        serverController.theProfiler.startSection("keepAlive");

        if (networkTickCount - field_147377_k > 40L)
        {
            field_147377_k = networkTickCount;
            field_147379_i = func_147363_d();
            field_147378_h = (int)field_147379_i;
            sendPacket(new S00PacketKeepAlive(field_147378_h));
        }

        if (chatSpamThresholdCount > 0)
        {
            --chatSpamThresholdCount;
        }

        if (field_147375_m > 0)
        {
            --field_147375_m;
        }

        vacState.updateState();

        serverController.theProfiler.endStartSection("playerTick");
        serverController.theProfiler.endSection();
    }

    public NetworkManager func_147362_b()
    {
        return netManager;
    }

    /**
     * Kick a player from the server with a reason
     */
    public void kickPlayerFromServer(String p_147360_1_)
    {
        final ChatComponentText var2 = new ChatComponentText(p_147360_1_);
        netManager.scheduleOutboundPacket(new S40PacketDisconnect(var2), new GenericFutureListener[] {new GenericFutureListener()
        {
            private static final String __OBFID = "CL_00001453";

            public void operationComplete(Future p_operationComplete_1_)
            {
                netManager.closeChannel(var2);
            }
        }});
        netManager.disableAutoRead();
    }

    public void func_147358_a(C0CPacketInput p_147358_1_)
    {
        playerEntity.setEntityActionState(p_147358_1_.func_149620_c(), p_147358_1_.func_149616_d(), p_147358_1_.func_149618_e(), p_147358_1_.func_149617_f());
    }

    // Utility methods
    private double getHorizontalSpeed()
    {
        if (Double.valueOf(lastPosX) != null && Double.valueOf(lastPosZ) != null)
        {
            return Math.sqrt(Math.pow((playerEntity.posX - lastPosX), 2.0) + Math.pow((playerEntity.posZ - lastPosZ), 2));
        }
        else
        {
            return 0;
        }
    }

    private double getVerticalSpeed()
    {
        if (Double.valueOf(lastPosY) != null)
        {
            return Math.abs(playerEntity.posY - lastPosY);
        }
        else
        {
            return 0;
        }
    }

    private void setBackPlayer()
    {
        setPlayerLocation(lastPosX, lastPosY, lastPosZ, playerEntity.rotationYaw, playerEntity.rotationPitch);
    }

    // Where all of our anticheat hooks for the player moving will go
    private void processPlayerMoved(C03PacketPlayer packet)
    {
        checkForSpeedhack();
        checkForVClip();
        checkForSneakSprint();

        // Patch crash exploit
        if (playerEntity.ridingEntity != null)
        {
            if (packet.getMoving() && packet.getX() == -999.0D && packet.getStance() == -999.0D)
            {
                if (Math.abs(packet.getX()) > 1.0D || Math.abs(packet.getZ()) > 1.0D)
                {
                    VACUtils.notifyAndLog(playerEntity.getUsername() + " was caught trying to crash the server with an invalid position!");
                    kickPlayerFromServer("Nope!");
                    return;
                }
            }
        }
    }

    private void checkForSneakSprint()
    {
        if (MinecraftServer.isPlayerOpped(playerEntity)) return;

        // No sneaking and sprinting
        if (playerEntity.isSneaking() && playerEntity.isSprinting())
        {
            kickPlayerFromServer("Silly hacker, this isn't Counterstrike! You can't sneak and sprint!");
            VACUtils.notifyAndLog(playerEntity.getUsername() + " was kicked for sneaking and sprinting!");
            return;
        }
    }

    private void checkForVClip()
    {
        if (MinecraftServer.isPlayerOppedOrCreative(playerEntity)) return;

        // Anti-VClip
        if ((Double.valueOf(lastPosY) != null))
        {
            if (getVerticalSpeed() > 3.0D)
            {
                setBackPlayer();
                vacState.aVClip.incrementDetections();

                if (vacState.aVClip.getDetections() == 1)
                {
                    VACUtils.notifyAndLog(vacState.aVClip, playerEntity.getUsername() + " might be VClipping!");
                }

                if (vacState.aVClip.getDetections() == 3)
                {
                    kickPlayerFromServer("Teleport hacking detected on the Y-Axis (VClipping).");
                    VACUtils.notifyAndLog(vacState.aVClip, playerEntity.getUsername() + " was kicked for teleport hacking (vclipping)!");
                }
            }
        }
    }

    private void checkForSpeedhack()
    {
        // Anti speedhack
        if (MinecraftServer.isPlayerOppedOrCreative(playerEntity)) return;
        if (Double.valueOf(lastPosX) == null || Double.valueOf(lastPosZ) == null) return;

        double speed = getHorizontalSpeed();
        double speedLimit;

        if (!playerEntity.onGround)
        {
            vacState.aSpeed.onBhop();
        }

        if (playerEntity.isSneaking() && vacState.aSpeed.wasSneaking()) // sneaking
        {
            speedLimit = MinecraftServer.getServer().getSneakSpeedLimit();
        }
        else
        {
            boolean sprinting = false;
            boolean jumping = false;
            boolean potion = false;
            if (playerEntity.isSprinting()) sprinting = true;
            if (!playerEntity.onGround || vacState.aSpeed.getTimeSinceLastBhop() <= 5) jumping = true;
            if (playerEntity.isPotionActive(1)) potion = true;
            speedLimit = MinecraftServer.getServer().getSpeedLimit(sprinting, jumping, potion);
        }

        // System.out.println(String.valueOf(playerEntity.isSprinting() +
        // " and " + String.valueOf(!playerEntity.onGround)));
        // System.out.println(String.valueOf(vacState.aSpeed.timeSinceLastBhop)
        // + ", " + String.valueOf(speed) + "/" + String.valueOf(speedLimit));

        if (speed > speedLimit)
        {
            // Give the player some leeway
            if (Math.abs(speedLimit - speed) > MinecraftServer.getServer().getSpeedhackLeeway())
            {
                vacState.aSpeed.onSpeeding();
                // Check if this player is full of it
                if (vacState.aSpeed.getSpeedingRatio() > MinecraftServer.getServer().getSpeedhackRatioKickThreshold())
                {
                    setBackPlayer();
                    vacState.aSpeed.giveSpeedingTicket();
                }
            }
        }
        vacState.aSpeed.onMove();

        // System.out.println(playerEntity.getUsername() + ": " +
        // vacState.aSpeed.totalSpeeded + "/" + vacState.aSpeed.totalMoved +
        // " (" + (vacState.aSpeed.totalSpeeded /
        // Math.max((double)vacState.aSpeed.totalMoved, 1.0)));

        if (vacState.aSpeed.resets() > 5)
        {
            kickPlayerFromServer("Speed hack detected.");
            VACUtils.notifyAndLog(playerEntity.getUsername() + " was kicked for moving too quickly (speed hacking)!");
        }

        vacState.aSpeed.setSneaking(playerEntity.isSneaking());
    }

    public void handleFlying(C03PacketPlayer packet)
    {
        WorldServer var2 = serverController.worldServerForDimension(playerEntity.dimension);
        field_147366_g = true;

        if (!playerEntity.playerConqueredTheEnd)
        {
            double var3;

            if (!hasMoved)
            {
                var3 = packet.getY() - lastPosY;

                if (packet.getX() == lastPosX && var3 * var3 < 0.01D && packet.getZ() == lastPosZ)
                {
                    hasMoved = true;
                }
            }

            if (hasMoved)
            {
                double x;
                double y;
                double z;

                processPlayerMoved(packet);

                if (playerEntity.ridingEntity != null)
                {
                    float var34 = playerEntity.rotationYaw;
                    float var4 = playerEntity.rotationPitch;
                    playerEntity.ridingEntity.updateRiderPosition();
                    x = playerEntity.posX;
                    y = playerEntity.posY;
                    z = playerEntity.posZ;

                    if (packet.rotating())
                    {
                        var34 = packet.yaw();
                        var4 = packet.pitch();
                    }

                    if (packet.getMoving() && packet.getX() == -999.0D && packet.getStance() == -999.0D)
                    {
                        if (Math.abs(packet.getX()) > 1.0D || Math.abs(packet.getZ()) > 1.0D)
                        {
                            VACUtils.notifyAndLog(playerEntity.getUsername() + " was caught trying to crash the server with an invalid position!");
                            kickPlayerFromServer("Nope!");
                            return;
                        }
                    }

                    playerEntity.onGround = packet.getOnGround();
                    playerEntity.onUpdateEntity();
                    playerEntity.ySize = 0.0F;
                    playerEntity.setPositionAndRotation(x, y, z, var34, var4);

                    if (playerEntity.ridingEntity != null)
                    {
                        playerEntity.ridingEntity.updateRiderPosition();
                    }

                    serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(playerEntity);

                    if (hasMoved)
                    {
                        lastPosX = playerEntity.posX;
                        lastPosY = playerEntity.posY;
                        lastPosZ = playerEntity.posZ;
                    }

                    var2.updateEntity(playerEntity);
                    return;
                }

                if (playerEntity.isPlayerSleeping())
                {
                    playerEntity.onUpdateEntity();
                    playerEntity.setPositionAndRotation(lastPosX, lastPosY, lastPosZ, playerEntity.rotationYaw, playerEntity.rotationPitch);
                    var2.updateEntity(playerEntity);
                    return;
                }

                var3 = playerEntity.posY;
                lastPosX = playerEntity.posX;
                lastPosY = playerEntity.posY;
                lastPosZ = playerEntity.posZ;
                x = playerEntity.posX;
                y = playerEntity.posY;
                z = playerEntity.posZ;
                float var11 = playerEntity.rotationYaw;
                float var12 = playerEntity.rotationPitch;

                if (packet.getMoving() && packet.getY() == -999.0D && packet.getStance() == -999.0D)
                {
                    packet.setMoving(false);
                }

                double deltaXPacket;

                if (packet.getMoving())
                {
                    x = packet.getX();
                    y = packet.getY();
                    z = packet.getZ();
                    deltaXPacket = packet.getStance() - packet.getY();

                    if (!playerEntity.isPlayerSleeping() && (deltaXPacket > 1.65D || deltaXPacket < 0.1D))
                    {
                        kickPlayerFromServer("Illegal stance");
                        logger.warn(playerEntity.getUsername() + " had an illegal stance: " + deltaXPacket);
                        return;
                    }

                    if (Math.abs(packet.getX()) > 3.2E7D || Math.abs(packet.getZ()) > 3.2E7D)
                    {
                        kickPlayerFromServer("Illegal position");
                        return;
                    }
                }

                if (packet.rotating())
                {
                    var11 = packet.yaw();
                    var12 = packet.pitch();
                }

                playerEntity.onUpdateEntity();
                playerEntity.ySize = 0.0F;
                playerEntity.setPositionAndRotation(lastPosX, lastPosY, lastPosZ, var11, var12);

                if (!hasMoved) { return; }

                deltaXPacket = x - playerEntity.posX;
                double deltaYPacket = y - playerEntity.posY;
                double deltaZPacket = z - playerEntity.posZ;
                double deltaX = Math.min(Math.abs(deltaXPacket), Math.abs(playerEntity.motionX));
                double deltaY = Math.min(Math.abs(deltaYPacket), Math.abs(playerEntity.motionY));
                double deltaZ = Math.min(Math.abs(deltaZPacket), Math.abs(playerEntity.motionZ));
                double velocitySquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

                if (velocitySquared > 100.0D && (!serverController.isSinglePlayer() || !serverController.getServerOwner().equals(playerEntity.getUsername())))
                {
                    logger.warn(playerEntity.getUsername() + " moved too quickly! " + deltaXPacket + "," + deltaYPacket + "," + deltaZPacket + " (" + deltaX + ", " + deltaY + ", " + deltaZ + ")");
                    setBackPlayer();
                    kickPlayerFromServer("Moved too quickly. ICanHasTeleportHax?");
                    VACUtils.notifyAndLog(playerEntity.getUsername() + " might be teleporting!");
                    return;
                }

                float var27 = 0.0625F;
                boolean var28 = var2.getCollidingBoundingBoxes(playerEntity, playerEntity.boundingBox.copy().contract(var27, var27, var27)).isEmpty();

                if (playerEntity.onGround && !packet.getOnGround() && deltaYPacket > 0.0D)
                {
                    playerEntity.jump();
                }

                playerEntity.moveEntity(deltaXPacket, deltaYPacket, deltaZPacket);
                playerEntity.onGround = packet.getOnGround();
                playerEntity.addMovementStat(deltaXPacket, deltaYPacket, deltaZPacket);
                double deltaYPacketRaw = deltaYPacket;
                deltaXPacket = x - playerEntity.posX;
                deltaYPacket = y - playerEntity.posY;

                if (deltaYPacket > -0.5D || deltaYPacket < 0.5D)
                {
                    deltaYPacket = 0.0D;
                }

                deltaZPacket = z - playerEntity.posZ;
                velocitySquared = deltaXPacket * deltaXPacket + deltaYPacket * deltaYPacket + deltaZPacket * deltaZPacket;
                boolean var31 = false;

                if (velocitySquared > 0.0625D && !playerEntity.isPlayerSleeping() && !playerEntity.theItemInWorldManager.isCreative())
                {
                    var31 = true;
                    kickPlayerFromServer("Moved wrongly. ICanHasMovementHax?");
                    VACUtils.notifyAndLog(playerEntity.getUsername() + " was kicked for moving wrongly!");
                }

                playerEntity.setPositionAndRotation(x, y, z, var11, var12);
                boolean var32 = var2.getCollidingBoundingBoxes(playerEntity, playerEntity.boundingBox.copy().contract(var27, var27, var27)).isEmpty();

                if (var28 && (var31 || !var32) && !playerEntity.isPlayerSleeping())
                {
                    setPlayerLocation(lastPosX, lastPosY, lastPosZ, var11, var12);
                    return;
                }

                AxisAlignedBB var33 = playerEntity.boundingBox.copy().expand(var27, var27, var27).addCoord(0.0D, -0.55D, 0.0D);

                if (!serverController.isFlightAllowed() && !playerEntity.theItemInWorldManager.isCreative() && !var2.checkBlockCollision(var33))
                {
                    if (deltaYPacketRaw >= -0.03125D)
                    {
                        floatingTickCount++;

                        processFloating();
                    }
                }
                else
                {
                    floatingTickCount = 0;
                    vacState.aFly.setAntiFlyPosition(playerEntity);
                }

                playerEntity.onGround = packet.getOnGround();
                serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(playerEntity);
                playerEntity.handleFalling(playerEntity.posY - var3, packet.getOnGround());
            }
            else if (networkTickCount % 20 == 0)
            {
                setPlayerLocation(lastPosX, lastPosY, lastPosZ, playerEntity.rotationYaw, playerEntity.rotationPitch);
            }
        }
    }

    // Where all our hooks will go for flying
    private void processFloating()
    {
        if (MinecraftServer.isPlayerOpped(playerEntity)) return;

        // System.out.println(this.floatingTickCount);
        int logThreshold = MinecraftServer.getServer().getFlyResetLogThreshold();
        int kickThreshold = MinecraftServer.getServer().getFlyResetKickThreshold();
        if (floatingTickCount > MinecraftServer.getServer().getFloatingTicksThreshold())
        {
            resetPlayerForFlying();

            if (vacState.aFly.getFlyResetCount() == logThreshold)
            {
                StringBuilder message = new StringBuilder();
                message.append(playerEntity.getUsername());
                message.append(" has been reset for flying ");
                message.append(logThreshold).append(" times now.");
                VACUtils.notifyAndLog(vacState.aFly, message.toString());
            }

            floatingTickCount = 0;
            return;
        }
        if (vacState.aFly.getFlyResetCount() > kickThreshold)
        {
            kickPlayerFromServer("Flying is not allowed on this server.");
            String message = playerEntity.getUsername() + " was kicked for flying!";
            VACUtils.notifyAndLog(vacState.aFly, message);
            return;
        }
    }

    private void resetPlayerForFlying()
    {
        double setBackX = vacState.aFly.getX();
        double setBackZ = vacState.aFly.getZ();
        double setBackY = playerEntity.worldObj.getTopSolidOrLiquidBlock((int)setBackX, (int)setBackZ);
        setPlayerLocation(setBackX, setBackY, setBackZ, playerEntity.rotationYaw, playerEntity.rotationPitch);
        playerEntity.attackEntityFrom(DamageSource.fall, 4);
        vacState.aFly.incrementResetCount();
    }

    public void setPlayerLocation(double p_147364_1_, double p_147364_3_, double p_147364_5_, float p_147364_7_, float p_147364_8_)
    {
        hasMoved = false;
        lastPosX = p_147364_1_;
        lastPosY = p_147364_3_;
        lastPosZ = p_147364_5_;
        playerEntity.setPositionAndRotation(p_147364_1_, p_147364_3_, p_147364_5_, p_147364_7_, p_147364_8_);
        playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(p_147364_1_, p_147364_3_ + 1.6200000047683716D, p_147364_5_, p_147364_7_, p_147364_8_, false));
    }

    public void handleBlockDig(C07PacketPlayerDigging packetBlockDig)
    {
        WorldServer world = serverController.worldServerForDimension(playerEntity.dimension);
        playerEntity.func_143004_u();

        int x = packetBlockDig.getX();
        int y = packetBlockDig.getY();
        int z = packetBlockDig.getZ();

        Block block = world.getBlock(x, y, z);

        if (packetBlockDig.getStatus() == 4)
        {
            playerEntity.dropOneItem(false);
        }
        else if (packetBlockDig.getStatus() == 3)
        {
            playerEntity.dropOneItem(true);
        }
        else if (packetBlockDig.getStatus() == 5)
        {
            playerEntity.stopUsingItem();
        }
        else
        {
            boolean var3 = false;

            // Started digging
            if (packetBlockDig.getStatus() == 0)
            {
                var3 = true;
                vacState.aFastBreak.startDiggingBlock();
            }

            // Stopped digging
            if (packetBlockDig.getStatus() == 1)
            {
                var3 = true;
                vacState.aFastBreak.resetDigStatus();
            }

            // Broke block
            if (packetBlockDig.getStatus() == 2)
            {
                var3 = true;

                if (processBlockDug(world, x, y, z, block)) return;
            }

            if (var3)
            {
                double var7 = playerEntity.posX - (x + 0.5D);
                double var9 = playerEntity.posY - (y + 0.5D) + 1.5D;
                double var11 = playerEntity.posZ - (z + 0.5D);
                double var13 = var7 * var7 + var9 * var9 + var11 * var11;

                if (var13 > 36.0D) { return; }

                if (y >= serverController.getBuildLimit()) { return; }
            }

            if (packetBlockDig.getStatus() == 0)
            {
                if (!serverController.isBlockProtected(world, x, y, z, playerEntity))
                {
                    playerEntity.theItemInWorldManager.onBlockClicked(x, y, z, packetBlockDig.getSide());
                }
                else
                {
                    playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
                }
            }
            else if (packetBlockDig.getStatus() == 2)
            {
                playerEntity.theItemInWorldManager.blockRemoving(x, y, z);

                if (world.getBlock(x, y, z).getMaterial() != Material.air)
                {
                    playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
                }
            }
            else if (packetBlockDig.getStatus() == 1)
            {
                playerEntity.theItemInWorldManager.cancelDestroyingBlock(x, y, z);

                if (world.getBlock(x, y, z).getMaterial() != Material.air)
                {
                    playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
                }
            }
        }
    }

    // Where all our anticheat hooks shall go for a block being mined
    // Returns false if not set back, returns true if set back
    private boolean processBlockDug(WorldServer world, int x, int y, int z, Block block)
    {
        if (MinecraftServer.isPlayerOppedOrCreative(playerEntity)) return false;

        // Diamond notification code
        if (block.getBlockId() == 56 && MinecraftServer.getServer().useDiamondNotifications())
        {
            // Assume any ores mined within 100 ticks of each other are from the
            // same vein
            if (vacState.dNotifications.isMiningNewVein())
            {
                vacState.dNotifications.incrementVeinsMined();
                StringBuilder message = new StringBuilder();
                message.append(playerEntity.getUsername());
                message.append(" found diamonds. (");
                message.append(vacState.dNotifications.getNumberOfVeins());
                message.append(" veins found since login)");
                VACUtils.notifyAndLog(vacState.dNotifications, message.toString());
            }
            vacState.dNotifications.resetTicksSinceLastOre();
        }

        float hardness = block.getPlayerRelativeBlockHardness(playerEntity, world, x, y, z);
        // The number of ticks it SHOULD take for a player to break the
        // block under ideal circumstances
        int ticksToBreakBlock = (int)Math.ceil(1.0f / hardness);

        // Add this check for players with shitty internet connections
        if (vacState.aFastBreak.getTicksTaken() > 0)
        {
            // Did the player break this block too quickly?
            if (vacState.aFastBreak.getTicksTaken() < ticksToBreakBlock)
            {
                // Give the player some leeway
                int leewayDifference = (int)Math.ceil(ticksToBreakBlock * MinecraftServer.getServer().getFastbreakLeeway());
                if (ticksToBreakBlock - vacState.aFastBreak.getTicksTaken() > leewayDifference)
                {
                    // If broken so fast it was above the leeway, track it
                    vacState.aFastBreak.incrementDeviations();
                    if (vacState.aFastBreak.isMinedNonzero())
                    {
                        // Check if this guy is bullshit
                        if (vacState.aFastBreak.getDeviationRatio() > MinecraftServer.getServer().getFastbreakRatioThreshold())
                        {
                            // If he's bullshit, update the client and tell
                            // him that he didn't mine the block
                            playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
                            // Log it and notify admins
                            StringBuilder message = new StringBuilder();
                            message.append(playerEntity.getUsername());
                            message.append(" broke blocks too quickly! (");
                            message.append(vacState.aFastBreak.getTicksTaken());
                            message.append(" ticks /");
                            message.append(ticksToBreakBlock);
                            message.append(")");
                            VACUtils.notifyAndLog(vacState.aFastBreak, message.toString());
                            return true;
                        }
                    }
                }
            }
        }

        vacState.aFastBreak.incrementMined();
        vacState.aFastBreak.resetDigStatus();

        return false;
    }

    public void handlePlace(C08PacketPlayerBlockPlacement packetPlace)
    {
        if (vacState.aFastBuild.isAlreadyKicked()) return;

        WorldServer world = serverController.worldServerForDimension(playerEntity.dimension);
        ItemStack itemStack = playerEntity.inventory.getCurrentItem();
        boolean var4 = false;
        int x = packetPlace.getX();
        int y = packetPlace.getY();
        int z = packetPlace.getZ();
        int side = packetPlace.getSide();
        playerEntity.func_143004_u();

        if (itemStack == null) { return; }
        if (packetPlace.getSide() == 255)
        {
            playerEntity.theItemInWorldManager.tryUseItem(playerEntity, world, itemStack);
        }
        else if (packetPlace.getY() >= serverController.getBuildLimit() - 1 && (packetPlace.getSide() == 1 || packetPlace.getY() >= serverController.getBuildLimit()))
        {
            ChatComponentTranslation var9 = new ChatComponentTranslation("build.tooHigh", new Object[] {Integer.valueOf(serverController.getBuildLimit())});
            var9.getChatStyle().setColor(EnumChatFormatting.RED);
            playerEntity.playerNetServerHandler.sendPacket(new S02PacketChat(var9));
            var4 = true;
        }
        else
        {
            processBlockPlaced(world.getBlock(x, y, z), itemStack);

            if (hasMoved && playerEntity.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) < 64.0D && !serverController.isBlockProtected(world, x, y, z, playerEntity))
            {
                playerEntity.theItemInWorldManager.activateBlockOrUseItem(playerEntity, world, itemStack, x, y, z, side, packetPlace.getXOffset(), packetPlace.getYOffset(), packetPlace.getZOffset());
            }

            var4 = true;
        }

        if (var4)
        {
            playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));

            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }

            playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
        }

        itemStack = playerEntity.inventory.getCurrentItem();

        if (itemStack != null && itemStack.stackSize == 0)
        {
            playerEntity.inventory.mainInventory[playerEntity.inventory.currentItem] = null;
            itemStack = null;
        }

        if (itemStack == null || itemStack.getMaxItemUseDuration() == 0)
        {
            playerEntity.isChangingQuantityOnly = true;
            playerEntity.inventory.mainInventory[playerEntity.inventory.currentItem] = ItemStack.copyItemStack(playerEntity.inventory.mainInventory[playerEntity.inventory.currentItem]);
            Slot var10 = playerEntity.openContainer.getSlotFromInventory(playerEntity.inventory, playerEntity.inventory.currentItem);
            playerEntity.openContainer.detectAndSendChanges();
            playerEntity.isChangingQuantityOnly = false;

            if (!ItemStack.areItemStacksEqual(playerEntity.inventory.getCurrentItem(), packetPlace.func_149574_g()))
            {
                sendPacket(new S2FPacketSetSlot(playerEntity.openContainer.windowId, var10.slotNumber, playerEntity.inventory.getCurrentItem()));
            }
        }
    }

    // Where all our hooks will go for a block being placed
    private void processBlockPlaced(Block block, ItemStack itemStack)
    {
        if (MinecraftServer.isPlayerOpped(playerEntity)) return;

        boolean isContainer = block instanceof BlockContainer;

        // System.out.println(itemStack.getItemId() + " " +
        // vacState.getBuildCount() + " " + isContainer);
        if (itemStack.getItemId() < 256 && itemStack.getItemId() != 69 && !isContainer)
        {
            vacState.aFastBuild.incrementBlockCount(2);
        }
        if (vacState.aFastBuild.getBuildCount() > MinecraftServer.getServer().getBuildhackThreshold())
        {
            kickPlayerFromServer("Build hacking detected.");
            String message = playerEntity.getUsername() + " was kicked for buildhacking!";
            VACUtils.notifyAndLog(vacState.aFastBuild, message);
            vacState.aFastBuild.kickMe();
            return;
        }
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing
     * the reason for termination
     */
    public void onDisconnect(IChatComponent p_147231_1_)
    {
        logger.info(playerEntity.getUsername() + " lost connection: " + p_147231_1_);
        serverController.func_147132_au();
        ChatComponentTranslation var2 = new ChatComponentTranslation("multiplayer.player.left", new Object[] {playerEntity.getUsernameAsIChatComponent()});
        var2.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        serverController.getConfigurationManager().func_148539_a(var2);
        playerEntity.mountEntityAndWakeUp();
        serverController.getConfigurationManager().playerLoggedOut(playerEntity);

        if (serverController.isSinglePlayer() && playerEntity.getUsername().equals(serverController.getServerOwner()))
        {
            logger.info("Stopping singleplayer server as player logged out");
            serverController.initiateShutdown();
        }
    }

    public void sendPacket(final Packet p_147359_1_)
    {
        if (p_147359_1_ instanceof S02PacketChat)
        {
            S02PacketChat var2 = (S02PacketChat)p_147359_1_;
            EntityPlayer.EnumChatVisibility var3 = playerEntity.func_147096_v();

            if (var3 == EntityPlayer.EnumChatVisibility.HIDDEN) { return; }

            if (var3 == EntityPlayer.EnumChatVisibility.SYSTEM && !var2.func_148916_d()) { return; }
        }

        try
        {
            netManager.scheduleOutboundPacket(p_147359_1_, new GenericFutureListener[0]);
        }
        catch (Throwable var5)
        {
            CrashReport var6 = CrashReport.makeCrashReport(var5, "Sending packet");
            CrashReportCategory var4 = var6.makeCategory("Packet being sent");
            var4.addCrashSectionCallable("Packet class", new Callable()
            {
                private static final String __OBFID = "CL_00001454";

                public String call()
                {
                    return p_147359_1_.getClass().getCanonicalName();
                }
            });
            throw new ReportedException(var6);
        }
    }

    public void func_147355_a(C09PacketHeldItemChange p_147355_1_)
    {
        if (p_147355_1_.func_149614_c() >= 0 && p_147355_1_.func_149614_c() < InventoryPlayer.getHotbarSize())
        {
            playerEntity.inventory.currentItem = p_147355_1_.func_149614_c();
            playerEntity.func_143004_u();
        }
        else
        {
            kickPlayerFromServer("Silly hacker, that slot isn't even in your hotbar!");
            VACUtils.notifyAndLog(playerEntity.getUsername() + " tried to set an invalid carried item (attempted durability hack)");
        }
    }

    public void handleChat(C01PacketChatMessage packetChat)
    {
        if (playerEntity.func_147096_v() == EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            ChatComponentTranslation var4 = new ChatComponentTranslation("chat.cannotSend", new Object[0]);
            var4.getChatStyle().setColor(EnumChatFormatting.RED);
            sendPacket(new S02PacketChat(var4));
        }
        else
        {
            playerEntity.func_143004_u();
            String message = packetChat.getMessage();
            message = StringUtils.normalizeSpace(message);

            for (int var3 = 0; var3 < message.length(); ++var3)
            {
                if (!ChatAllowedCharacters.isAllowedCharacter(message.charAt(var3)))
                {
                    VACUtils.notifyAndLog(playerEntity.getUsername() + " tried to send illegal characters in chat!");
                    kickPlayerFromServer("Illegal characters in chat");
                    return;
                }
            }
            
            processChat(message);

            if (message.startsWith("/"))
            {
                handleSlashCommand(message);
            }
            else
            {
                ChatComponentTranslation var5 = new ChatComponentTranslation("chat.type.text", new Object[] {playerEntity.getUsernameAsIChatComponent(), message});
                serverController.getConfigurationManager().sendChatMessageToAllPlayersAndLog(var5, false);
            }

            chatSpamThresholdCount += 20;

            if (chatSpamThresholdCount > 200 && !serverController.getConfigurationManager().isPlayerOpped(playerEntity.getUsername()))
            {
                kickPlayerFromServer("disconnect.spam");
            }
        }
    }

    // Where the anticheat hooks for chat will go
    private void processChat(String message)
    {
        // No sneaking and chatting
        if (playerEntity.isSneaking() && !MinecraftServer.isPlayerOpped(playerEntity))
        {
            kickPlayerFromServer("Silly hacker, you can't sneak and chat!");
            VACUtils.notifyAndLog(playerEntity.getUsername() + " was kicked for sneaking and chatting!");
            return;
        }
        
        if (vacState.aSpam.messageEqualsLast(message) && !MinecraftServer.isPlayerOpped(playerEntity))
        {
            vacState.aSpam.incrementSpamCount();
        } else {
            vacState.aSpam.resetSpamCount();
        }
        
        if (vacState.aSpam.getSpamCount() > 2 || vacState.aSpam.isInCooldown())
        {
            playerEntity.addChatMessage("Spamming will result in an automatic ban!");
            playerEntity.addChatMessage("Please wait a few seconds before chatting again.");
        }
        
        chatSpamThresholdCount += 20;

        if (chatSpamThresholdCount > 200 && !MinecraftServer.isPlayerOpped(playerEntity))
        {
            BanEntry spamBan = new BanEntry(playerEntity.getUsername());
            spamBan.setBannedBy("Server");
            spamBan.setBanReason("Auto-banned for spamming.");
            spamBan.setBanEndDate(new Date(new Date().getTime() + 60 * 60000L));
            MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().put(spamBan);
            kickPlayerFromServer("You have been auto-banned for spamming for 1 hour.");
            VACUtils.notifyAndLog(vacState.aSpam, playerEntity.getUsername() + " was auto-banned for spamming.");
        }
    }

    /**
     * Handle commands that start with a /
     */
    private void handleSlashCommand(String p_147361_1_)
    {
        serverController.getCommandManager().executeCommand(playerEntity, p_147361_1_);
    }

    public void func_147350_a(C0APacketAnimation p_147350_1_)
    {
        playerEntity.func_143004_u();

        if (p_147350_1_.func_149421_d() == 1)
        {
            playerEntity.swingItem();
        }
    }

    public void func_147357_a(C0BPacketEntityAction p_147357_1_)
    {
        playerEntity.func_143004_u();

        if (p_147357_1_.func_149513_d() == 1)
        {
            playerEntity.setSneaking(true);
        }
        else if (p_147357_1_.func_149513_d() == 2)
        {
            playerEntity.setSneaking(false);
        }
        else if (p_147357_1_.func_149513_d() == 4)
        {
            playerEntity.setSprinting(true);
        }
        else if (p_147357_1_.func_149513_d() == 5)
        {
            playerEntity.setSprinting(false);
        }
        else if (p_147357_1_.func_149513_d() == 3)
        {
            playerEntity.wakeUpPlayer(false, true, true);
            hasMoved = false;
        }
        else if (p_147357_1_.func_149513_d() == 6)
        {
            if (playerEntity.ridingEntity != null && playerEntity.ridingEntity instanceof EntityHorse)
            {
                ((EntityHorse)playerEntity.ridingEntity).setJumpPower(p_147357_1_.func_149512_e());
            }
        }
        else if (p_147357_1_.func_149513_d() == 7 && playerEntity.ridingEntity != null && playerEntity.ridingEntity instanceof EntityHorse)
        {
            ((EntityHorse)playerEntity.ridingEntity).openGUI(playerEntity);
        }
    }

    public void handleUseEntity(C02PacketUseEntity packetUseEntity)
    {
        WorldServer var2 = serverController.worldServerForDimension(playerEntity.dimension);
        Entity var3 = packetUseEntity.getEntity(var2);
        playerEntity.func_143004_u();

        if (var3 != null)
        {
            boolean var4 = playerEntity.canEntityBeSeen(var3);
            double var5 = 36.0D;

            if (!var4)
            {
                var5 = 9.0D;
            }

            if (playerEntity.getDistanceSqToEntity(var3) < var5)
            {
                if (packetUseEntity.getAction() == C02PacketUseEntity.Action.INTERACT)
                {
                    playerEntity.interactWith(var3);
                }
                else if (packetUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK)
                {
                    if (var3 instanceof EntityItem || var3 instanceof EntityXPOrb || var3 instanceof EntityArrow || var3 == playerEntity)
                    {
                        kickPlayerFromServer("Attempting to attack an invalid entity");
                        VACUtils.notifyAndLog(playerEntity.getUsername() + " tried to attack an invalid entity!");
                        return;
                    }

                    playerEntity.attackTargetEntityWithCurrentItem(var3);
                }
            }
        }
    }

    public void handleCLientCommand(C16PacketClientStatus packet)
    {
        playerEntity.func_143004_u();
        C16PacketClientStatus.EnumState var2 = packet.func_149435_c();

        switch (NetHandlerPlayServer.SwitchEnumState.field_151290_a[var2.ordinal()])
        {
        case 1:
            if (playerEntity.playerConqueredTheEnd)
            {
                playerEntity = serverController.getConfigurationManager().recreatePlayerEntity(playerEntity, 0, true);
            }
            else if (playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled())
            {
                if (serverController.isSinglePlayer() && playerEntity.getUsername().equals(serverController.getServerOwner()))
                {
                    playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                    serverController.deleteWorldAndStopServer();
                }
                else
                {
                    BanEntry var3 = new BanEntry(playerEntity.getUsername());
                    var3.setBanReason("Death in Hardcore");
                    serverController.getConfigurationManager().getBannedPlayers().put(var3);
                    playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                }
            }
            else
            {
                if (playerEntity.getHealth() > 0.0F) { return; }

                playerEntity = serverController.getConfigurationManager().recreatePlayerEntity(playerEntity, 0, false);
            }

            break;

        case 2:
            playerEntity.func_147099_x().func_150876_a(playerEntity);
            break;

        case 3:
            playerEntity.triggerAchievement(AchievementList.openInventory);
        }
    }

    public void func_147356_a(C0DPacketCloseWindow p_147356_1_)
    {
        playerEntity.closeContainer();
    }

    public void func_147351_a(C0EPacketClickWindow p_147351_1_)
    {
        playerEntity.func_143004_u();

        if (playerEntity.openContainer.windowId == p_147351_1_.func_149548_c() && playerEntity.openContainer.getCanCraft(playerEntity))
        {
            ItemStack var2 = playerEntity.openContainer.slotClick(p_147351_1_.func_149544_d(), p_147351_1_.func_149543_e(), p_147351_1_.func_149542_h(), playerEntity);

            if (ItemStack.areItemStacksEqual(p_147351_1_.func_149546_g(), var2))
            {
                playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(p_147351_1_.func_149548_c(), p_147351_1_.func_149547_f(), true));
                playerEntity.isChangingQuantityOnly = true;
                playerEntity.openContainer.detectAndSendChanges();
                playerEntity.updateHeldItem();
                playerEntity.isChangingQuantityOnly = false;
            }
            else
            {
                field_147372_n.addKey(playerEntity.openContainer.windowId, Short.valueOf(p_147351_1_.func_149547_f()));
                playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(p_147351_1_.func_149548_c(), p_147351_1_.func_149547_f(), false));
                playerEntity.openContainer.setCanCraft(playerEntity, false);
                ArrayList var3 = new ArrayList();

                for (int var4 = 0; var4 < playerEntity.openContainer.inventorySlots.size(); ++var4)
                {
                    var3.add(((Slot)playerEntity.openContainer.inventorySlots.get(var4)).getStack());
                }

                playerEntity.updateCraftingInventory(playerEntity.openContainer, var3);
            }
        }
    }

    public void func_147338_a(C11PacketEnchantItem p_147338_1_)
    {
        playerEntity.func_143004_u();

        if (playerEntity.openContainer.windowId == p_147338_1_.func_149539_c() && playerEntity.openContainer.getCanCraft(playerEntity))
        {
            playerEntity.openContainer.enchantItem(playerEntity, p_147338_1_.func_149537_d());
            playerEntity.openContainer.detectAndSendChanges();
        }
    }

    public void func_147344_a(C10PacketCreativeInventoryAction p_147344_1_)
    {
        if (playerEntity.theItemInWorldManager.isCreative())
        {
            boolean var2 = p_147344_1_.func_149627_c() < 0;
            ItemStack var3 = p_147344_1_.func_149625_d();
            boolean var4 = p_147344_1_.func_149627_c() >= 1 && p_147344_1_.func_149627_c() < 36 + InventoryPlayer.getHotbarSize();
            boolean var5 = var3 == null || var3.getItem() != null;
            boolean var6 = var3 == null || var3.getItemDamage() >= 0 && var3.stackSize <= 64 && var3.stackSize > 0;

            if (var4 && var5 && var6)
            {
                if (var3 == null)
                {
                    playerEntity.inventoryContainer.putStackInSlot(p_147344_1_.func_149627_c(), (ItemStack)null);
                }
                else
                {
                    playerEntity.inventoryContainer.putStackInSlot(p_147344_1_.func_149627_c(), var3);
                }

                playerEntity.inventoryContainer.setCanCraft(playerEntity, true);
            }
            else if (var2 && var5 && var6 && field_147375_m < 200)
            {
                field_147375_m += 20;
                EntityItem var7 = playerEntity.dropPlayerItemWithRandomChoice(var3, true);

                if (var7 != null)
                {
                    var7.setAgeToCreativeDespawnTime();
                }
            }
        }
    }

    public void func_147339_a(C0FPacketConfirmTransaction p_147339_1_)
    {
        Short var2 = (Short)field_147372_n.lookup(playerEntity.openContainer.windowId);

        if (var2 != null && p_147339_1_.func_149533_d() == var2.shortValue() && playerEntity.openContainer.windowId == p_147339_1_.func_149532_c() && !playerEntity.openContainer.getCanCraft(playerEntity))
        {
            playerEntity.openContainer.setCanCraft(playerEntity, true);
        }
    }

    public void handleUpdateSign(C12PacketUpdateSign packetUpdateSign)
    {
        playerEntity.func_143004_u();
        WorldServer var2 = serverController.worldServerForDimension(playerEntity.dimension);

        int x = packetUpdateSign.getX();
        int y = packetUpdateSign.getY();
        int z = packetUpdateSign.getZ();

        if (var2.blockExists(x, y, z))
        {
            TileEntity var3 = var2.getTileEntity(x, y, z);

            if (var3 instanceof TileEntitySign)
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (!var4.func_145914_a() || var4.func_145911_b() != playerEntity)
                {
                    kickPlayerFromServer("Silly hacker, you can't change that sign!");
                    VACUtils.notifyAndLog(playerEntity.getUsername() + " just tried to change non-editable sign!");
                    return;
                }
            }

            int var6;
            int var8;

            for (var8 = 0; var8 < 4; ++var8)
            {
                boolean var5 = true;

                if (packetUpdateSign.func_149589_f()[var8].length() > 15)
                {
                    var5 = false;
                }
                else
                {
                    for (var6 = 0; var6 < packetUpdateSign.func_149589_f()[var8].length(); ++var6)
                    {
                        if (!ChatAllowedCharacters.isAllowedCharacter(packetUpdateSign.func_149589_f()[var8].charAt(var6)))
                        {
                            var5 = false;
                        }
                    }
                }

                if (!var5)
                {
                    packetUpdateSign.func_149589_f()[var8] = "!?";
                }
            }

            if (var3 instanceof TileEntitySign)
            {
                var8 = packetUpdateSign.getX();
                int var9 = packetUpdateSign.getY();
                var6 = packetUpdateSign.getZ();
                TileEntitySign var7 = (TileEntitySign)var3;
                System.arraycopy(packetUpdateSign.func_149589_f(), 0, var7.field_145915_a, 0, 4);
                var7.onInventoryChanged();
                var2.markBlockForUpdate(var8, var9, var6);
            }
        }
    }

    public void func_147353_a(C00PacketKeepAlive p_147353_1_)
    {
        if (p_147353_1_.func_149460_c() == field_147378_h)
        {
            int var2 = (int)(func_147363_d() - field_147379_i);
            playerEntity.ping = (playerEntity.ping * 3 + var2) / 4;
        }
    }

    private long func_147363_d()
    {
        return System.nanoTime() / 1000000L;
    }

    public void func_147348_a(C13PacketPlayerAbilities p_147348_1_)
    {
        playerEntity.capabilities.isFlying = p_147348_1_.func_149488_d() && playerEntity.capabilities.allowFlying;
    }

    public void func_147341_a(C14PacketTabComplete p_147341_1_)
    {
        ArrayList var2 = Lists.newArrayList();
        Iterator var3 = serverController.getPossibleCompletions(playerEntity, p_147341_1_.func_149419_c()).iterator();

        while (var3.hasNext())
        {
            String var4 = (String)var3.next();
            var2.add(var4);
        }

        playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete((String[])var2.toArray(new String[var2.size()])));
    }

    public void func_147352_a(C15PacketClientSettings p_147352_1_)
    {
        playerEntity.func_147100_a(p_147352_1_);
    }

    public void func_147349_a(C17PacketCustomPayload p_147349_1_)
    {
        ItemStack var2;
        ItemStack var3;

        if ("MC|BEdit".equals(p_147349_1_.func_149559_c()))
        {
            try
            {
                var2 = (new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()))).readItemStackFromBuffer();

                if (!ItemWritableBook.func_150930_a(var2.getTagCompound())) { throw new IOException("Invalid book tag!"); }

                var3 = playerEntity.inventory.getCurrentItem();

                if (var2.getItem() == Items.writable_book && var2.getItem() == var3.getItem())
                {
                    var3.setTagInfo("pages", var2.getTagCompound().getTagList("pages", 8));
                }
            }
            catch (Exception var12)
            {
                logger.error("Couldn\'t handle book info", var12);
            }
        }
        else if ("MC|BSign".equals(p_147349_1_.func_149559_c()))
        {
            try
            {
                var2 = (new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()))).readItemStackFromBuffer();

                if (!ItemEditableBook.validBookTagContents(var2.getTagCompound())) { throw new IOException("Invalid book tag!"); }

                var3 = playerEntity.inventory.getCurrentItem();

                if (var2.getItem() == Items.written_book && var3.getItem() == Items.writable_book)
                {
                    var3.setTagInfo("author", new NBTTagString(playerEntity.getUsername()));
                    var3.setTagInfo("title", new NBTTagString(var2.getTagCompound().getString("title")));
                    var3.setTagInfo("pages", var2.getTagCompound().getTagList("pages", 8));
                    var3.func_150996_a(Items.written_book);
                }
            }
            catch (Exception var11)
            {
                logger.error("Couldn\'t sign book", var11);
            }
        }
        else
        {
            DataInputStream var13;
            int var16;

            if ("MC|TrSel".equals(p_147349_1_.func_149559_c()))
            {
                try
                {
                    var13 = new DataInputStream(new ByteArrayInputStream(p_147349_1_.func_149558_e()));
                    var16 = var13.readInt();
                    Container var4 = playerEntity.openContainer;

                    if (var4 instanceof ContainerMerchant)
                    {
                        ((ContainerMerchant)var4).setCurrentRecipeIndex(var16);
                    }
                }
                catch (Exception var10)
                {
                    logger.error("Couldn\'t select trade", var10);
                }
            }
            else if ("MC|AdvCdm".equals(p_147349_1_.func_149559_c()))
            {
                if (!serverController.isCommandBlockEnabled())
                {
                    playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
                }
                else if (playerEntity.canCommandSenderUseCommand(2, "") && playerEntity.capabilities.isCreativeMode)
                {
                    try
                    {
                        PacketBuffer var14 = new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()));
                        byte var17 = var14.readByte();
                        CommandBlockLogic var18 = null;

                        if (var17 == 0)
                        {
                            TileEntity var5 = playerEntity.worldObj.getTileEntity(var14.readInt(), var14.readInt(), var14.readInt());

                            if (var5 instanceof TileEntityCommandBlock)
                            {
                                var18 = ((TileEntityCommandBlock)var5).func_145993_a();
                            }
                        }
                        else if (var17 == 1)
                        {
                            Entity var20 = playerEntity.worldObj.getEntityByID(var14.readInt());

                            if (var20 instanceof EntityMinecartCommandBlock)
                            {
                                var18 = ((EntityMinecartCommandBlock)var20).func_145822_e();
                            }
                        }

                        String var23 = var14.readStringFromBuffer(var14.readableBytes());

                        if (var18 != null)
                        {
                            var18.func_145752_a(var23);
                            var18.func_145756_e();
                            playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", new Object[] {var23}));
                        }
                    }
                    catch (Exception var9)
                    {
                        logger.error("Couldn\'t set command block", var9);
                    }
                }
                else
                {
                    playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed", new Object[0]));
                }
            }
            else if ("MC|Beacon".equals(p_147349_1_.func_149559_c()))
            {
                if (playerEntity.openContainer instanceof ContainerBeacon)
                {
                    try
                    {
                        var13 = new DataInputStream(new ByteArrayInputStream(p_147349_1_.func_149558_e()));
                        var16 = var13.readInt();
                        int var22 = var13.readInt();
                        ContainerBeacon var21 = (ContainerBeacon)playerEntity.openContainer;
                        Slot var6 = var21.getSlot(0);

                        if (var6.getHasStack())
                        {
                            var6.decrStackSize(1);
                            TileEntityBeacon var7 = var21.func_148327_e();
                            var7.func_146001_d(var16);
                            var7.func_146004_e(var22);
                            var7.onInventoryChanged();
                        }
                    }
                    catch (Exception var8)
                    {
                        logger.error("Couldn\'t set beacon", var8);
                    }
                }
            }
            else if ("MC|ItemName".equals(p_147349_1_.func_149559_c()) && playerEntity.openContainer instanceof ContainerRepair)
            {
                ContainerRepair var15 = (ContainerRepair)playerEntity.openContainer;

                if (p_147349_1_.func_149558_e() != null && p_147349_1_.func_149558_e().length >= 1)
                {
                    String var19 = ChatAllowedCharacters.filerAllowedCharacters(new String(p_147349_1_.func_149558_e(), Charsets.UTF_8));

                    if (var19.length() <= 30)
                    {
                        var15.updateItemName(var19);
                    }
                }
                else
                {
                    var15.updateItemName("");
                }
            }
        }
    }

    /**
     * Allows validation of the connection state transition. Parameters: from,
     * to (connection state). Typically throws IllegalStateException or
     * UnsupportedOperationException if validation fails
     */
    public void onConnectionStateTransition(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_)
    {
        if (p_147232_2_ != EnumConnectionState.PLAY) { throw new IllegalStateException("Unexpected change in protocol!"); }
    }

    static final class SwitchEnumState
    {
        static final int[] field_151290_a = new int[C16PacketClientStatus.EnumState.values().length];
        private static final String __OBFID = "CL_00001455";

        static
        {
            try
            {
                field_151290_a[C16PacketClientStatus.EnumState.PERFORM_RESPAWN.ordinal()] = 1;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                field_151290_a[C16PacketClientStatus.EnumState.REQUEST_STATS.ordinal()] = 2;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                field_151290_a[C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT.ordinal()] = 3;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
