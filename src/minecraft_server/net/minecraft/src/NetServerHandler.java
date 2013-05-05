package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import tk.afffsdd.GeneralUtils;
import tk.afffsdd.vanillaanticheat.VanillaAntiCheatUtils;

public class NetServerHandler extends NetHandler
{
    /** The underlying network manager for this server handler. */
    public final INetworkManager netManager;

    /** Reference to the MinecraftServer object. */
    private final MinecraftServer mcServer;

    /** This is set to true whenever a player disconnects from the server. */
    public boolean connectionClosed = false;

    /** Reference to the EntityPlayerMP object. */
    public EntityPlayerMP playerEntity;

    /** incremented each tick */
    private int currentTicks;

    /** holds the amount of tick the player is floating */
    private int playerInAirTime;
    private boolean field_72584_h;
    private int keepAliveRandomID;
    private long keepAliveTimeSent;
    private int antiSpamCount = 0;
    private String antiSpamLastMsg = "";
    private int antiSpamCooldown = 0;
    private int movedWronglyWarnings = 0;
    private int antiFlyResetCount = 0;
    private boolean antiFlyHasBeenLogged = false;
    private double antiFlyX = 0.0;
    private double antiFlyZ = 0.0;
    private int antiFlyTickCounter = 0;
    private int vclipDetect = 0;
    private int speedingTickets = 0;
    private int sneakResetCount = 0;
    private int antiBuildHackBlockCount = 0;
    private boolean antiBuildHackAlreadyKicked = false;
    private int ticksToBreakBlock = 0;
    private int ticksTakenToBreakBlock = 0;
    private boolean isBreakingBlock = false;
    private boolean wasSneaking = false;
    private int timeSinceSneakingStarted = 0;
    private int timeSinceLastBhop = 0;
    private int totalMoved = 0;
    private int totalSpeeded = 0;
    private int ticksSinceLastOreMined = 0;
    private int veinsMined = 0;
    private int totalDeviations = 0;
    private int totalMined = 0;

    /** The Java Random object. */
    private static Random rndmObj = new Random();
    private long ticksOfLastKeepAlive;
    private int chatSpamThresholdCount = 0;
    private int creativeItemCreationSpamThresholdTally = 0;

    /** The last known x position for this connection. */
    private double lastPosX;

    /** The last known y position for this connection. */
    private double lastPosY;

    /** The last known z position for this connection. */
    private double lastPosZ;

    /** is true when the player has moved since his last movement packet */
    private boolean hasMoved = true;
    private IntHashMap field_72586_s = new IntHashMap();

    public NetServerHandler(MinecraftServer par1, INetworkManager par2, EntityPlayerMP par3)
    {
        mcServer = par1;
        netManager = par2;
        par2.setNetHandler(this);
        playerEntity = par3;
        par3.playerNetServerHandler = this;
    }

    /**
     * handle all the packets for the connection
     */
    public void handlePackets()
    {
        field_72584_h = false;
        ++currentTicks;
        if(isBreakingBlock) ticksTakenToBreakBlock++;
        mcServer.theProfiler.startSection("packetflow");
        netManager.processReadPackets();
        mcServer.theProfiler.endStartSection("keepAlive");

        if ((long)currentTicks - ticksOfLastKeepAlive > 20L)
        {
            ticksOfLastKeepAlive = (long)currentTicks;
            keepAliveTimeSent = System.nanoTime() / 1000000L;
            keepAliveRandomID = rndmObj.nextInt();
            sendPacket(new Packet0KeepAlive(keepAliveRandomID));
        }

        if (chatSpamThresholdCount > 0)
        {
            --chatSpamThresholdCount;
        }
        
        if (antiSpamCooldown > 0)
        {
        	--antiSpamCooldown;
        }

        if (creativeItemCreationSpamThresholdTally > 0)
        {
            --creativeItemCreationSpamThresholdTally;
        }
        
        if (antiBuildHackBlockCount > 0)
        {
        	--antiBuildHackBlockCount;
        }

        if(ticksSinceLastOreMined < 101)
        {
        	ticksSinceLastOreMined++;
        }
        
        mcServer.theProfiler.endStartSection("playerTick");
        mcServer.theProfiler.endSection();
    }

    /**
     * Kick the offending player and give a reason why
     */
    public void kickPlayer(String par1Str)
    {
        if (!connectionClosed)
        {
            playerEntity.mountEntityAndWakeUp();
            sendPacket(new Packet255KickDisconnect(par1Str));
            netManager.serverShutdown();
            mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(EnumChatFormatting.YELLOW + playerEntity.username + " left the game."));
            mcServer.getConfigurationManager().playerLoggedOut(playerEntity);
            connectionClosed = true;
        }
    }
    
    private double getHorizontalSpeed()
    {
    	if(Double.valueOf(lastPosX) != null && Double.valueOf(lastPosZ) != null)
    	{
    		return Math.sqrt(Math.pow((playerEntity.posX - lastPosX), 2.0) + Math.pow((playerEntity.posZ - lastPosZ), 2));	
    	} else {
    		return 0;
    	}
    }
    
    private double getVerticalSpeed()
    {
    	if(Double.valueOf(lastPosY) != null)
    	{
    		return Math.abs(playerEntity.posY - lastPosY);
    	} else {
    		return 0;
    	}
    }
    
    private void setBackPlayer()
    {
    	setPlayerLocation(lastPosX, lastPosY, lastPosZ, playerEntity.rotationYaw, playerEntity.rotationPitch);
    }

    public void handleFlying(Packet10Flying par1Packet10Flying)
    {
        WorldServer var2 = mcServer.worldServerForDimension(playerEntity.dimension);
        field_72584_h = true;

        if (!playerEntity.playerConqueredTheEnd)
        {
            double var3;

            if (!hasMoved)
            {
                var3 = par1Packet10Flying.yPosition - lastPosY;

                if (par1Packet10Flying.xPosition == lastPosX && var3 * var3 < 0.01D && par1Packet10Flying.zPosition == lastPosZ)
                {
                    hasMoved = true;
                }
            }

            if (hasMoved)
            {
                double var5;
                double var7;
                double var9;
                double var13;
                
            	if(playerEntity.isSneaking() && playerEntity.isSprinting() && !VanillaAntiCheatUtils.isOp(playerEntity.username))
            	{
            		kickPlayer("Silly hacker, this isn't Counterstrike! You can't sneak and sprint!");
            		VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " was kicked for sneaking and sprinting!");
            		return;
            	}
                
                ++antiFlyTickCounter;
                if (antiFlyTickCounter == 11)
                {
                	antiFlyTickCounter = 0;
                	antiFlyX = playerEntity.posX;
                	antiFlyZ = playerEntity.posZ;
                }
                
                if ((Double.valueOf(lastPosY) != null))
                {
                	if (getVerticalSpeed() > 3.0D && !VanillaAntiCheatUtils.isOp(playerEntity.username))
                	{
                		setBackPlayer();
                		vclipDetect++;
                		
                		if (vclipDetect == 1)
                		{
                			VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " might be vclipping!");
                		}
                		
                		if (vclipDetect == 3)
                		{
                			kickPlayer("Teleport hacking detected on the Y-Axis.");
                			VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " was kicked for teleport hacking (vclipping)!");
                		}
                	}
                }
                
                if ((Double.valueOf(lastPosX) != null) && (Double.valueOf(lastPosZ) != null) && !VanillaAntiCheatUtils.isOpOrCreative(playerEntity))
                {
                	double speed = getHorizontalSpeed();
                	double speedLimit = 0.3;
                	byte detectionType = 0; //0 = normal, 1 = sprinting, 2 = sneaking
                	if(timeSinceLastBhop <= 15)
                	{
                		timeSinceLastBhop++;
                	}
                	if((playerEntity.isSprinting() && !playerEntity.onGround) || timeSinceLastBhop <= 15) //bunnyhopping
                	{
                		speedLimit = 0.8;
                		if(playerEntity.activePotionsMap.containsKey(1))
                		{
                			speedLimit = 0.9;
                		}
                		detectionType = 1;
                		if(!(timeSinceLastBhop <= 15)) timeSinceLastBhop = 0;
                	}
                	if(playerEntity.isSneaking() && wasSneaking) //sneaking
                	{
                		speedLimit = 0.16;
                		detectionType = 2;
                	}
                	//System.out.println(String.valueOf(playerEntity.isSprinting() + " and " + String.valueOf(!playerEntity.onGround)));
                	//System.out.println(String.valueOf(timeSinceLastBhop) + ", " + String.valueOf(speed) + "/" + String.valueOf(speedLimit));
                	if(speed > speedLimit)
                	{
                		if(Math.abs(speed - speedLimit) > 0.035) //give the player some leeway
                		{
                			totalSpeeded++; //log and track it
                			//check if this player is full of it
                			double ratio = totalSpeeded / Math.max((double) totalMoved, 1.0);
                			
                			if(ratio > 0.25)
                			{
                            	setBackPlayer();
                            	speedingTickets++;		
                			}
                		}
                	}
                    totalMoved++;
                    if(totalMoved >= 1000)
                    {
                    	//reset the ratio periodically
                    	totalMoved = 0;
                    	totalSpeeded = 0;
                    }
                	//System.out.println(playerEntity.username + ": " + String.valueOf(totalSpeeded) + "/" + String.valueOf(totalMoved) + " (" + String.valueOf(totalSpeeded / Math.max((double) totalMoved, 1.0)));
            		
                	if(speed > 10)
                	{
                		kickPlayer("Speed limit reached.");
                	}
                	
                	if(speedingTickets > 5)
                	{
                		String hackName;
                		switch(detectionType)
                		{
                		case 0:
                			hackName = "speed";
                			break;
                		case 1:
                			hackName = "speed (bunnyhop)";
                			break;
                		case 2:
                			hackName = "sneak";
                			break;
                		default:
                			hackName = "speed";
                		}
                		kickPlayer(GeneralUtils.capitalize(hackName) + " hack detected.");
                		VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " was kicked for " + hackName + " hacking!");
                	}
                    
                	if(timeSinceSneakingStarted >= 20) //this will give the player 1 second to slow down as minecraft changes the speed gradually
                	{
                    	wasSneaking = playerEntity.isSneaking();	
                	} else {
                		timeSinceSneakingStarted++;
                	}
                	if(!playerEntity.isSneaking() && !wasSneaking) //if the player stops sneaking, reset the timer
                	{
                		timeSinceSneakingStarted = 0;
                	}
                }

                if (playerEntity.ridingEntity != null)
                {
                    float var34 = playerEntity.rotationYaw;
                    float var4 = playerEntity.rotationPitch;
                    playerEntity.ridingEntity.updateRiderPosition();
                    var5 = playerEntity.posX;
                    var7 = playerEntity.posY;
                    var9 = playerEntity.posZ;
                    double var35 = 0.0D;
                    var13 = 0.0D;

                    if (par1Packet10Flying.rotating)
                    {
                        var34 = par1Packet10Flying.yaw;
                        var4 = par1Packet10Flying.pitch;
                    }

                    if (par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0D && par1Packet10Flying.stance == -999.0D)
                    {
                        if (Math.abs(par1Packet10Flying.xPosition) > 1.0D || Math.abs(par1Packet10Flying.zPosition) > 1.0D)
                        {
                            System.err.println(playerEntity.username + " was caught trying to crash the server with an invalid position.");
                            kickPlayer("Nope!");
                            return;
                        }

                        var35 = par1Packet10Flying.xPosition;
                        var13 = par1Packet10Flying.zPosition;
                    }

                    playerEntity.onGround = par1Packet10Flying.onGround;
                    playerEntity.onUpdateEntity();
                    playerEntity.moveEntity(var35, 0.0D, var13);
                    playerEntity.setPositionAndRotation(var5, var7, var9, var34, var4);
                    playerEntity.motionX = var35;
                    playerEntity.motionZ = var13;

                    if (playerEntity.ridingEntity != null)
                    {
                        var2.uncheckedUpdateEntity(playerEntity.ridingEntity, true);
                    }

                    if (playerEntity.ridingEntity != null)
                    {
                        playerEntity.ridingEntity.updateRiderPosition();
                    }

                    mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(playerEntity);
                    lastPosX = playerEntity.posX;
                    lastPosY = playerEntity.posY;
                    lastPosZ = playerEntity.posZ;
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
                var5 = playerEntity.posX;
                var7 = playerEntity.posY;
                var9 = playerEntity.posZ;
                float var11 = playerEntity.rotationYaw;
                float var12 = playerEntity.rotationPitch;

                if (par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0D && par1Packet10Flying.stance == -999.0D)
                {
                    par1Packet10Flying.moving = false;
                }

                if (par1Packet10Flying.moving)
                {
                    var5 = par1Packet10Flying.xPosition;
                    var7 = par1Packet10Flying.yPosition;
                    var9 = par1Packet10Flying.zPosition;
                    var13 = par1Packet10Flying.stance - par1Packet10Flying.yPosition;

                    if (!playerEntity.isPlayerSleeping() && (var13 > 1.65D || var13 < 0.1D))
                    {
                        kickPlayer("Illegal stance");
                        mcServer.getLogAgent().func_98236_b(playerEntity.username + " had an illegal stance: " + var13);
                        return;
                    }

                    if (Math.abs(par1Packet10Flying.xPosition) > 3.2E7D || Math.abs(par1Packet10Flying.zPosition) > 3.2E7D)
                    {
                        kickPlayer("Illegal position");
                        return;
                    }
                }

                if (par1Packet10Flying.rotating)
                {
                    var11 = par1Packet10Flying.yaw;
                    var12 = par1Packet10Flying.pitch;
                }

                playerEntity.onUpdateEntity();
                playerEntity.ySize = 0.0F;
                playerEntity.setPositionAndRotation(lastPosX, lastPosY, lastPosZ, var11, var12);

                if (!hasMoved)
                {
                    return;
                }

                var13 = var5 - playerEntity.posX;
                double var15 = var7 - playerEntity.posY;
                double var17 = var9 - playerEntity.posZ;
                double var19 = Math.min(Math.abs(var13), Math.abs(playerEntity.motionX));
                double var21 = Math.min(Math.abs(var15), Math.abs(playerEntity.motionY));
                double var23 = Math.min(Math.abs(var17), Math.abs(playerEntity.motionZ));
                double var25 = var19 * var19 + var21 * var21 + var23 * var23;

                if (var25 > 100.0D && (!mcServer.isSinglePlayer() || !mcServer.getServerOwner().equals(playerEntity.username)))
                {
                    mcServer.getLogAgent().func_98236_b(playerEntity.username + " moved too quickly! " + var13 + "," + var15 + "," + var17 + " (" + var19 + ", " + var21 + ", " + var23 + ")");
                    setBackPlayer();
                    kickPlayer("Moved too quickly. ICanHasSpeedHax?");
                    VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " might be speedhacking!");
                    return;
                }

                float var27 = 0.0625F;
                boolean var28 = var2.getCollidingBoundingBoxes(playerEntity, playerEntity.boundingBox.copy().contract((double)var27, (double)var27, (double)var27)).isEmpty();

                if (playerEntity.onGround && !par1Packet10Flying.onGround && var15 > 0.0D)
                {
                    playerEntity.addExhaustion(0.2F);
                }

                playerEntity.moveEntity(var13, var15, var17);
                playerEntity.onGround = par1Packet10Flying.onGround;
                playerEntity.addMovementStat(var13, var15, var17);
                double var29 = var15;
                var13 = var5 - playerEntity.posX;
                var15 = var7 - playerEntity.posY;

                if (var15 > -0.5D || var15 < 0.5D)
                {
                    var15 = 0.0D;
                }

                var17 = var9 - playerEntity.posZ;
                var25 = var13 * var13 + var15 * var15 + var17 * var17;
                boolean var31 = false;

                if (var25 > 0.0625D && !playerEntity.isPlayerSleeping() && !playerEntity.theItemInWorldManager.isCreative())
                {
                    var31 = true;
                    mcServer.getLogAgent().func_98236_b(playerEntity.username + " moved wrongly!");
                    ++movedWronglyWarnings;
                    if (movedWronglyWarnings > 3)
                    {
                    	kickPlayer("Moved wrongly. ICanHasMovementHax?");
                    	VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " was kicked for moving wrongly!");
                    }
                }

                playerEntity.setPositionAndRotation(var5, var7, var9, var11, var12);
                boolean var32 = var2.getCollidingBoundingBoxes(playerEntity, playerEntity.boundingBox.copy().contract((double)var27, (double)var27, (double)var27)).isEmpty();

                if (var28 && (var31 || !var32) && !playerEntity.isPlayerSleeping())
                {
                    setPlayerLocation(lastPosX, lastPosY, lastPosZ, var11, var12);
                    return;
                }

                AxisAlignedBB var33 = playerEntity.boundingBox.copy().expand((double)var27, (double)var27, (double)var27).addCoord(0.0D, -0.55D, 0.0D);

                if (!mcServer.isFlightAllowed() && !playerEntity.theItemInWorldManager.isCreative() && !var2.checkBlockCollision(var33))
                {
                    if (var29 >= -0.03125D)
                    {
                        ++playerInAirTime;
                        
                        if (playerInAirTime > 10 && !VanillaAntiCheatUtils.isOp(playerEntity.username))
                        {
                        	setPlayerLocation(antiFlyX, playerEntity.worldObj.getTopSolidOrLiquidBlock((int)antiFlyX, (int)antiFlyZ), antiFlyZ, var11, var12);
                        	playerEntity.damageEntity(DamageSource.fall, 4);
                        	antiFlyResetCount++;
                        	
                        	if (antiFlyResetCount == 3 && !antiFlyHasBeenLogged)
                        	{
                        		antiFlyHasBeenLogged = true;
                        		VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " has been reset for flying 3 times now.");
                        	}
                        }

                        if (antiFlyResetCount > 5)
                        {
                            kickPlayer("Flying is not allowed on this server.");
                            VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " was kicked for flying!");
                            return;
                        }
                    }
                }
                else
                {
                    playerInAirTime = 0;
                }

                playerEntity.onGround = par1Packet10Flying.onGround;
                mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(playerEntity);
                playerEntity.handleFalling(playerEntity.posY - var3, par1Packet10Flying.onGround);
            }
        }
    }

    /**
     * Moves the player to the specified destination and rotation
     */
    public void setPlayerLocation(double par1, double par3, double par5, float par7, float par8)
    {
        hasMoved = false;
        lastPosX = par1;
        lastPosY = par3;
        lastPosZ = par5;
        playerEntity.setPositionAndRotation(par1, par3, par5, par7, par8);
        playerEntity.playerNetServerHandler.sendPacket(new Packet13PlayerLookMove(par1, par3 + 1.6200000047683716D, par3, par5, par7, par8, false));
    }

    public void handleBlockDig(Packet14BlockDig par1Packet14BlockDig)
    {
        WorldServer var2 = mcServer.worldServerForDimension(playerEntity.dimension);
        
        int var4 = par1Packet14BlockDig.xPosition;
        int var5 = par1Packet14BlockDig.yPosition;
        int var6 = par1Packet14BlockDig.zPosition;
        
        int blockId = var2.getBlockId(var4, var5, var6);
        Block block = Block.blocksList[blockId];
        
        if(playerEntity == null || var2 == null)
        {
        	return;
        }
        if(block == null)
        {
        	return;
        }
        
		ticksToBreakBlock = (int) Math.ceil((1.0f / block.getPlayerRelativeBlockHardness(playerEntity, var2, var4, var5, var6)));
		
        if (par1Packet14BlockDig.status == 4)
        {
            playerEntity.dropOneItem(false);
        }
        else if (par1Packet14BlockDig.status == 3)
        {
            playerEntity.dropOneItem(true);
        }
        else if (par1Packet14BlockDig.status == 5)
        {
            playerEntity.stopUsingItem();
        }
        else
        {
            boolean var3 = false;

            if (par1Packet14BlockDig.status == 0)
            {
                var3 = true;
                ticksTakenToBreakBlock = 0;
                isBreakingBlock = true;
            }

            if (par1Packet14BlockDig.status == 1)
            {
                var3 = true;
                ticksTakenToBreakBlock = 0;
                isBreakingBlock = false;
            }

            if (par1Packet14BlockDig.status == 2)
            {
                var3 = true;
                if(block.blockID == 56 && !VanillaAntiCheatUtils.isOp(playerEntity))
                {
                	if(ticksSinceLastOreMined > 100)
                	{
                    	veinsMined++;
                    	VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " found diamonds. (" + String.valueOf(veinsMined) + " veins found since login)");	
                	}
                	ticksSinceLastOreMined = 0;
                }
                if(ticksTakenToBreakBlock < ticksToBreakBlock && !VanillaAntiCheatUtils.isOpOrCreative(playerEntity)) //did this player break this block too quickly?
                {
                	if(ticksToBreakBlock - ticksTakenToBreakBlock > Math.ceil(ticksToBreakBlock * 0.4)) // give the player some leeway
                	{
                    	//log and track it
                    	totalDeviations++;
                    	if(totalMined > 0)
                    	{
                        	//check if this guy is bullshit
                        	double ratio = totalDeviations / totalMined;
                        	if(ratio > 0.5)
                        	{
                        		playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(par1Packet14BlockDig.xPosition, par1Packet14BlockDig.yPosition, par1Packet14BlockDig.zPosition, var2)); //update the player's client on the rollback
                        		VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " broke blocks too quickly! (" + String.valueOf(ticksTakenToBreakBlock) + " ticks /" + String.valueOf(ticksToBreakBlock) + ")");
                        		return;	
                        	}		
                    	}
                	}
                }
                totalMined++;
                if(totalMined >= 100)
                {
                	//reset the ratio periodically
                	totalMined = 0;
                	totalDeviations = 0;
                }
                System.out.println(String.valueOf(totalDeviations) + "/" + String.valueOf(totalMined) + " (" + String.valueOf(totalDeviations / Math.max((double) totalMined, 1.0)));
                //System.out.println(String.valueOf(ticksTakenToBreakBlock) + "/" + String.valueOf(ticksToBreakBlock));
                isBreakingBlock = false;
            }

            if (var3)
            {
                double var7 = playerEntity.posX - ((double)var4 + 0.5D);
                double var9 = playerEntity.posY - ((double)var5 + 0.5D) + 1.5D;
                double var11 = playerEntity.posZ - ((double)var6 + 0.5D);
                double var13 = var7 * var7 + var9 * var9 + var11 * var11;

                if (var13 > 36.0D) //reach check (not allowed to do this if it's over 6 blocks away
                {
                    return;
                }

                if (var5 >= mcServer.getBuildLimit())
                {
                    return;
                }
            }

            if (par1Packet14BlockDig.status == 0)
            {
                if (!mcServer.func_96290_a(var2, var4, var5, var6, playerEntity))
                {
                    playerEntity.theItemInWorldManager.onBlockClicked(var4, var5, var6, par1Packet14BlockDig.face);
                }
                else
                {
                    playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var4, var5, var6, var2));
                }
            }
            else if (par1Packet14BlockDig.status == 2)
            {
                playerEntity.theItemInWorldManager.blockRemoving(var4, var5, var6);

                if (var2.getBlockId(var4, var5, var6) != 0)
                {
                    playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var4, var5, var6, var2));
                }
            }
            else if (par1Packet14BlockDig.status == 1)
            {
                playerEntity.theItemInWorldManager.cancelDestroyingBlock(var4, var5, var6);

                if (var2.getBlockId(var4, var5, var6) != 0)
                {
                    playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var4, var5, var6, var2));
                }
            }
        }
    }

    public void handlePlace(Packet15Place par1Packet15Place)
    {
    	if (antiBuildHackAlreadyKicked)
    	{
    		return;
    	}
    	
        WorldServer var2 = mcServer.worldServerForDimension(playerEntity.dimension);
        ItemStack var3 = playerEntity.inventory.getCurrentItem();
        boolean var4 = false;
        int var5 = par1Packet15Place.getXPosition();
        int var6 = par1Packet15Place.getYPosition();
        int var7 = par1Packet15Place.getZPosition();
        int var8 = par1Packet15Place.getDirection();
        
        if(var3 == null)
        {
        	return;
        }
        
        boolean isContainer = (Block.blocksList[var2.getBlockId(var5, var6, var7)]) instanceof BlockContainer;
        
        if (var3.itemID < 256 && !(var3.itemID == 69) && !isContainer)
        {
        	++antiBuildHackBlockCount;
        }
        
        //sendPacket(new Packet3Chat(String.valueOf(var3.itemID)));
        
        if (antiBuildHackBlockCount > 3 && !antiBuildHackAlreadyKicked && !VanillaAntiCheatUtils.isOp(playerEntity.username))
        {
        	kickPlayer("Build hacking detected.");
        	VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " was kicked for buildhacking!");
        	antiBuildHackAlreadyKicked = true;
        	return;
        }

        if (par1Packet15Place.getDirection() == 255)
        {

            playerEntity.theItemInWorldManager.tryUseItem(playerEntity, var2, var3);
        }
        else if (par1Packet15Place.getYPosition() >= mcServer.getBuildLimit() - 1 && (par1Packet15Place.getDirection() == 1 || par1Packet15Place.getYPosition() >= mcServer.getBuildLimit()))
        {
            playerEntity.playerNetServerHandler.sendPacket(new Packet3Chat("" + EnumChatFormatting.GRAY + "Height limit for building is " + mcServer.getBuildLimit()));
            var4 = true;
        }
        else
        {
            if (hasMoved && playerEntity.getDistanceSq((double)var5 + 0.5D, (double)var6 + 0.5D, (double)var7 + 0.5D) < 64.0D && !mcServer.func_96290_a(var2, var5, var6, var7, playerEntity))
            {
                playerEntity.theItemInWorldManager.activateBlockOrUseItem(playerEntity, var2, var3, var5, var6, var7, var8, par1Packet15Place.getXOffset(), par1Packet15Place.getYOffset(), par1Packet15Place.getZOffset());
            }

            var4 = true;
        }

        if (var4)
        {
            playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));

            if (var8 == 0)
            {
                --var6;
            }

            if (var8 == 1)
            {
                ++var6;
            }

            if (var8 == 2)
            {
                --var7;
            }

            if (var8 == 3)
            {
                ++var7;
            }

            if (var8 == 4)
            {
                --var5;
            }

            if (var8 == 5)
            {
                ++var5;
            }

            playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
        }

        var3 = playerEntity.inventory.getCurrentItem();

        if (var3 != null && var3.stackSize == 0)
        {
            playerEntity.inventory.mainInventory[playerEntity.inventory.currentItem] = null;
            var3 = null;
        }

        if (var3 == null || var3.getMaxItemUseDuration() == 0)
        {
            playerEntity.isChangingQuantityOnly = true;
            playerEntity.inventory.mainInventory[playerEntity.inventory.currentItem] = ItemStack.copyItemStack(playerEntity.inventory.mainInventory[playerEntity.inventory.currentItem]);
            Slot var9 = playerEntity.openContainer.getSlotFromInventory(playerEntity.inventory, playerEntity.inventory.currentItem);
            playerEntity.openContainer.detectAndSendChanges();
            playerEntity.isChangingQuantityOnly = false;

            if (!ItemStack.areItemStacksEqual(playerEntity.inventory.getCurrentItem(), par1Packet15Place.getItemStack()))
            {
                sendPacket(new Packet103SetSlot(playerEntity.openContainer.windowId, var9.slotNumber, playerEntity.inventory.getCurrentItem()));
            }
        }
    }

    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        mcServer.getLogAgent().func_98233_a(playerEntity.username + " lost connection: " + par1Str);
        mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(EnumChatFormatting.YELLOW + playerEntity.func_96090_ax() + " left the game."));
        mcServer.getConfigurationManager().playerLoggedOut(playerEntity);
        connectionClosed = true;

        if (mcServer.isSinglePlayer() && playerEntity.username.equals(mcServer.getServerOwner()))
        {
            mcServer.getLogAgent().func_98233_a("Stopping singleplayer server as player logged out");
            mcServer.initiateShutdown();
        }
    }

    /**
     * Default handler called for packets that don't have their own handlers in NetServerHandler; kicks player from the
     * server.
     */
    public void unexpectedPacket(Packet par1Packet)
    {
        mcServer.getLogAgent().func_98236_b(getClass() + " wasn\'t prepared to deal with a " + par1Packet.getClass());
        kickPlayer("Protocol error, unexpected packet");
    }

    /**
     * Adds the packet to the underlying network manager's send queue.
     */
    public void sendPacket(Packet par1Packet)
    {
        if (par1Packet instanceof Packet3Chat)
        {
            Packet3Chat var2 = (Packet3Chat)par1Packet;
            int var3 = playerEntity.getChatVisibility();

            if (var3 == 2)
            {
                return;
            }

            if (var3 == 1 && !var2.getIsServer())
            {
                return;
            }
        }

        try
        {
            netManager.addToSendQueue(par1Packet);
        }
        catch (Throwable var5)
        {
            CrashReport var6 = CrashReport.makeCrashReport(var5, "Sending packet");
            CrashReportCategory var4 = var6.makeCategory("Packet being sent");
            var4.addCrashSectionCallable("Packet ID", new CallablePacketID(this, par1Packet));
            var4.addCrashSectionCallable("Packet class", new CallablePacketClass(this, par1Packet));
            throw new ReportedException(var6);
        }
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
    {
        if (par1Packet16BlockItemSwitch.id >= 0 && par1Packet16BlockItemSwitch.id < InventoryPlayer.getHotbarSize())
        {
            playerEntity.inventory.currentItem = par1Packet16BlockItemSwitch.id;
        }
        else
        {
            mcServer.getLogAgent().func_98236_b(playerEntity.username + " tried to set an invalid carried item");
        }
    }

    public void handleChat(Packet3Chat par1Packet3Chat)
    {
    	if (playerEntity.isSneaking() && !VanillaAntiCheatUtils.isOp(playerEntity.username))
    	{
    		kickPlayer("Silly hacker, you can't sneak and chat!");
    		VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " was kicked for sneaking and chatting!");
    		return;
    	}
    	
        if (playerEntity.getChatVisibility() == 2)
        {
            sendPacket(new Packet3Chat("Cannot send chat message."));
        }
        else
        {
            String var2 = par1Packet3Chat.message;

            if (var2.length() > 100)
            {
                kickPlayer("Chat message too long");
            }
            else
            {
                var2 = var2.trim();

                for (int var3 = 0; var3 < var2.length(); ++var3)
                {
                    if (!ChatAllowedCharacters.isAllowedCharacter(var2.charAt(var3)))
                    {
                    	int restrictionLevel = mcServer.getAllCharactersRestrictionLevel();
                    	if(restrictionLevel > 0)
                    	{
                    		if(!VanillaAntiCheatUtils.isOp(playerEntity.username) && restrictionLevel != 2)
                    		{
                                kickPlayer("Illegal characters in chat");
                                return;
                    		}
                		} else {
                			kickPlayer("Illegal characters in chat");
                            return;
                		}
                    }
                }
                
                if (var2.equals(antiSpamLastMsg) && !VanillaAntiCheatUtils.isOp(playerEntity.username))
                {
                	antiSpamCount++;
                	if (antiSpamCount == 3 || chatSpamThresholdCount > 100)
                	{
                		antiSpamCooldown = 200;
                	}
                } else {
                	antiSpamCount = 0;
                }
                
                antiSpamLastMsg = var2;
                
                if (antiSpamCount > 2 || antiSpamCooldown > 0)
                {
                	sendPacket(new Packet3Chat("Spamming will result in an automatic ban!"));
                	sendPacket(new Packet3Chat("Please wait a few seconds before chatting again."));
                }

                if (var2.startsWith("/"))
                {
                    handleSlashCommand(var2);
                }
                else
                {
                    if (playerEntity.getChatVisibility() == 1)
                    {
                        sendPacket(new Packet3Chat("Cannot send chat message."));
                        return;
                    }

                    if (antiSpamCount < 3 && antiSpamCooldown == 0)
                    {
                    	int restrictionLevel = mcServer.getGreentextRestrictionLevel();
                    	if(var2.startsWith(">") && restrictionLevel > 0)
                    	{
                    		//if everbody is allowed OR if only ops are allowed AND the player is an op
                    		if(restrictionLevel == 2 || (restrictionLevel == 1 && (VanillaAntiCheatUtils.isOp(playerEntity.username))))	{
                    			var2 = EnumChatFormatting.GREEN + var2;	
                    		}
                    	}
                    	var2 = "<" + playerEntity.func_96090_ax() + "> " + var2;
                    	mcServer.getLogAgent().func_98233_a(var2);
                    	mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(var2, false));
                    }
                }

                chatSpamThresholdCount += 20;

                if (chatSpamThresholdCount > 200 && !mcServer.getConfigurationManager().areCommandsAllowed(playerEntity.username))
                {
                	BanEntry spamBan = new BanEntry(playerEntity.username);
                	spamBan.setBannedBy("Server");
                	spamBan.setBanReason("Auto-banned for spamming.");
                	mcServer.getConfigurationManager().getBannedPlayers().put(spamBan);
                    kickPlayer("You have been auto-banned for spamming.");
                    VanillaAntiCheatUtils.notifyAndLog(playerEntity.username + " was auto-banned for spamming.");
                }
            }
        }
    }

    /**
     * Processes a / command
     */
    private void handleSlashCommand(String par1Str)
    {
        mcServer.getCommandManager().executeCommand(playerEntity, par1Str);
    }

    public void handleAnimation(Packet18Animation par1Packet18Animation)
    {
        if (par1Packet18Animation.animate == 1)
        {
            playerEntity.swingItem();
        }
    }

    /**
     * runs registerPacket on the given Packet19EntityAction
     */
    public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction)
    {
        if (par1Packet19EntityAction.state == 1)
        {
            playerEntity.setSneaking(true);
        }
        else if (par1Packet19EntityAction.state == 2)
        {
            playerEntity.setSneaking(false);
        }
        else if (par1Packet19EntityAction.state == 4)
        {
            playerEntity.setSprinting(true);
        }
        else if (par1Packet19EntityAction.state == 5)
        {
            playerEntity.setSprinting(false);
        }
        else if (par1Packet19EntityAction.state == 3)
        {
            playerEntity.wakeUpPlayer(false, true, true);
            hasMoved = false;
        }
    }

    public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect)
    {
        netManager.networkShutdown("disconnect.quitting", new Object[0]);
    }

    /**
     * return the number of chuckDataPackets from the netManager
     */
    public int getNumChunkDataPackets()
    {
        return netManager.getNumChunkDataPackets();
    }

    public void handleUseEntity(Packet7UseEntity par1Packet7UseEntity)
    {
        WorldServer var2 = mcServer.worldServerForDimension(playerEntity.dimension);
        Entity var3 = var2.getEntityByID(par1Packet7UseEntity.targetEntity);

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
                if (par1Packet7UseEntity.isLeftClick == 0)
                {
                    playerEntity.interactWith(var3);
                }
                else if (par1Packet7UseEntity.isLeftClick == 1)
                {
                    playerEntity.attackTargetEntityWithCurrentItem(var3);
                }
            }
        }
    }

    public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand)
    {
        if (par1Packet205ClientCommand.forceRespawn == 1)
        {
            if (playerEntity.playerConqueredTheEnd)
            {
                playerEntity = mcServer.getConfigurationManager().recreatePlayerEntity(playerEntity, 0, true);
            }
            else if (playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled())
            {
                if (mcServer.isSinglePlayer() && playerEntity.username.equals(mcServer.getServerOwner()))
                {
                    playerEntity.playerNetServerHandler.kickPlayer("You have died. Game over, man, it\'s game over!");
                    mcServer.deleteWorldAndStopServer();
                }
                else
                {
                    BanEntry var2 = new BanEntry(playerEntity.username);
                    var2.setBanReason("Death in Hardcore");
                    mcServer.getConfigurationManager().getBannedPlayers().put(var2);
                    playerEntity.playerNetServerHandler.kickPlayer("You have died. Game over, man, it\'s game over!");
                }
            }
            else
            {
                if (playerEntity.getHealth() > 0)
                {
                    return;
                }

                playerEntity = mcServer.getConfigurationManager().recreatePlayerEntity(playerEntity, 0, false);
            }
        }
    }

    /**
     * If this returns false, all packets will be queued for the main thread to handle, even if they would otherwise be
     * processed asynchronously. Used to avoid processing packets on the client before the world has been downloaded
     * (which happens on the main thread)
     */
    public boolean canProcessPacketsAsync()
    {
        return true;
    }

    /**
     * respawns the player
     */
    public void handleRespawn(Packet9Respawn par1Packet9Respawn) {}

    public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow)
    {
        playerEntity.closeCraftingGui();
    }

    public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick)
    {
        if (playerEntity.openContainer.windowId == par1Packet102WindowClick.window_Id && playerEntity.openContainer.getCanCraft(playerEntity))
        {
            ItemStack var2 = playerEntity.openContainer.slotClick(par1Packet102WindowClick.inventorySlot, par1Packet102WindowClick.mouseClick, par1Packet102WindowClick.holdingShift, playerEntity);

            if (ItemStack.areItemStacksEqual(par1Packet102WindowClick.itemStack, var2))
            {
                playerEntity.playerNetServerHandler.sendPacket(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, true));
                playerEntity.isChangingQuantityOnly = true;
                playerEntity.openContainer.detectAndSendChanges();
                playerEntity.updateHeldItem();
                playerEntity.isChangingQuantityOnly = false;
            }
            else
            {
                field_72586_s.addKey(playerEntity.openContainer.windowId, Short.valueOf(par1Packet102WindowClick.action));
                playerEntity.playerNetServerHandler.sendPacket(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, false));
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

    public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem)
    {
        if (playerEntity.openContainer.windowId == par1Packet108EnchantItem.windowId && playerEntity.openContainer.getCanCraft(playerEntity))
        {
            playerEntity.openContainer.enchantItem(playerEntity, par1Packet108EnchantItem.enchantment);
            playerEntity.openContainer.detectAndSendChanges();
        }
    }

    /**
     * Handle a creative slot packet.
     */
    public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot)
    {
        if (playerEntity.theItemInWorldManager.isCreative())
        {
            boolean var2 = par1Packet107CreativeSetSlot.slot < 0;
            ItemStack var3 = par1Packet107CreativeSetSlot.itemStack;
            boolean var4 = par1Packet107CreativeSetSlot.slot >= 1 && par1Packet107CreativeSetSlot.slot < 36 + InventoryPlayer.getHotbarSize();
            boolean var5 = var3 == null || var3.itemID < Item.itemsList.length && var3.itemID >= 0 && Item.itemsList[var3.itemID] != null;
            boolean var6 = var3 == null || var3.getItemDamage() >= 0 && var3.getItemDamage() >= 0 && var3.stackSize <= 64 && var3.stackSize > 0;

            if (var4 && var5 && var6)
            {
                if (var3 == null)
                {
                    playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, (ItemStack)null);
                }
                else
                {
                    playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, var3);
                }

                playerEntity.inventoryContainer.setCanCraft(playerEntity, true);
            }
            else if (var2 && var5 && var6 && creativeItemCreationSpamThresholdTally < 200)
            {
                creativeItemCreationSpamThresholdTally += 20;
                EntityItem var7 = playerEntity.dropPlayerItem(var3);

                if (var7 != null)
                {
                    var7.setAgeToCreativeDespawnTime();
                }
            }
        }
    }

    public void handleTransaction(Packet106Transaction par1Packet106Transaction)
    {
        Short var2 = (Short)field_72586_s.lookup(playerEntity.openContainer.windowId);

        if (var2 != null && par1Packet106Transaction.shortWindowId == var2.shortValue() && playerEntity.openContainer.windowId == par1Packet106Transaction.windowId && !playerEntity.openContainer.getCanCraft(playerEntity))
        {
            playerEntity.openContainer.setCanCraft(playerEntity, true);
        }
    }

    /**
     * Updates Client side signs
     */
    public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign)
    {
        WorldServer var2 = mcServer.worldServerForDimension(playerEntity.dimension);

        if (var2.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition))
        {
            TileEntity var3 = var2.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);

            if (var3 instanceof TileEntitySign)
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (!var4.isEditable())
                {
                    VanillaAntiCheatUtils.notifyAndLog("Player " + playerEntity.username + " just tried to change non-editable sign");
                    return;
                }
            }

            int var6;
            int var8;

            for (var8 = 0; var8 < 4; ++var8)
            {
                boolean var5 = true;

                if (par1Packet130UpdateSign.signLines[var8].length() > 15)
                {
                    var5 = false;
                }
                else
                {
                    for (var6 = 0; var6 < par1Packet130UpdateSign.signLines[var8].length(); ++var6)
                    {
                        if (ChatAllowedCharacters.allowedCharacters.indexOf(par1Packet130UpdateSign.signLines[var8].charAt(var6)) < 0)
                        {
                            var5 = false;
                        }
                    }
                }

                if (!var5)
                {
                    par1Packet130UpdateSign.signLines[var8] = "!?";
                }
            }

            if (var3 instanceof TileEntitySign)
            {
                var8 = par1Packet130UpdateSign.xPosition;
                int var9 = par1Packet130UpdateSign.yPosition;
                var6 = par1Packet130UpdateSign.zPosition;
                TileEntitySign var7 = (TileEntitySign)var3;
                System.arraycopy(par1Packet130UpdateSign.signLines, 0, var7.signText, 0, 4);
                var7.onInventoryChanged();
                var2.markBlockForUpdate(var8, var9, var6);
            }
        }
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive)
    {
        if (par1Packet0KeepAlive.randomId == keepAliveRandomID)
        {
            int var2 = (int)(System.nanoTime() / 1000000L - keepAliveTimeSent);
            playerEntity.ping = (playerEntity.ping * 3 + var2) / 4;
        }
    }

    /**
     * determine if it is a server handler
     */
    public boolean isServerHandler()
    {
        return true;
    }

    /**
     * Handle a player abilities packet.
     */
    public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities)
    {
        playerEntity.capabilities.isFlying = par1Packet202PlayerAbilities.getFlying() && playerEntity.capabilities.allowFlying;
    }

    public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete)
    {
        StringBuilder var2 = new StringBuilder();
        String var4;

        for (Iterator var3 = mcServer.getPossibleCompletions(playerEntity, par1Packet203AutoComplete.getText()).iterator(); var3.hasNext(); var2.append(var4))
        {
            var4 = (String)var3.next();

            if (var2.length() > 0)
            {
                var2.append("\u0000");
            }
        }

        playerEntity.playerNetServerHandler.sendPacket(new Packet203AutoComplete(var2.toString()));
    }

    public void handleClientInfo(Packet204ClientInfo par1Packet204ClientInfo)
    {
        playerEntity.updateClientInfo(par1Packet204ClientInfo);
    }

    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload)
    {
        DataInputStream var2;
        ItemStack var3;
        ItemStack var4;

        if ("MC|BEdit".equals(par1Packet250CustomPayload.channel))
        {
            try
            {
                var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                var3 = Packet.readItemStack(var2);

                if (!ItemWritableBook.validBookTagPages(var3.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                var4 = playerEntity.inventory.getCurrentItem();

                if (var3 != null && var3.itemID == Item.writableBook.itemID && var3.itemID == var4.itemID)
                {
                    var4.setTagInfo("pages", var3.getTagCompound().getTagList("pages"));
                }
            }
            catch (Exception var12)
            {
                var12.printStackTrace();
            }
        }
        else if ("MC|BSign".equals(par1Packet250CustomPayload.channel))
        {
            try
            {
                var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                var3 = Packet.readItemStack(var2);

                if (!ItemEditableBook.validBookTagContents(var3.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                var4 = playerEntity.inventory.getCurrentItem();

                if (var3 != null && var3.itemID == Item.writtenBook.itemID && var4.itemID == Item.writableBook.itemID)
                {
                    var4.setTagInfo("author", new NBTTagString("author", playerEntity.username));
                    var4.setTagInfo("title", new NBTTagString("title", var3.getTagCompound().getString("title")));
                    var4.setTagInfo("pages", var3.getTagCompound().getTagList("pages"));
                    var4.itemID = Item.writtenBook.itemID;
                }
            }
            catch (Exception var11)
            {
                var11.printStackTrace();
            }
        }
        else
        {
            int var14;

            if ("MC|TrSel".equals(par1Packet250CustomPayload.channel))
            {
                try
                {
                    var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                    var14 = var2.readInt();
                    Container var16 = playerEntity.openContainer;

                    if (var16 instanceof ContainerMerchant)
                    {
                        ((ContainerMerchant)var16).setCurrentRecipeIndex(var14);
                    }
                }
                catch (Exception var10)
                {
                    var10.printStackTrace();
                }
            }
            else
            {
                int var18;

                if ("MC|AdvCdm".equals(par1Packet250CustomPayload.channel))
                {
                    if (!mcServer.isCommandBlockEnabled())
                    {
                        playerEntity.sendChatToPlayer(playerEntity.translateString("advMode.notEnabled", new Object[0]));
                    }
                    else if (playerEntity.canCommandSenderUseCommand(2, "") && playerEntity.capabilities.isCreativeMode)
                    {
                        try
                        {
                            var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                            var14 = var2.readInt();
                            var18 = var2.readInt();
                            int var5 = var2.readInt();
                            String var6 = Packet.readString(var2, 256);
                            TileEntity var7 = playerEntity.worldObj.getBlockTileEntity(var14, var18, var5);

                            if (var7 != null && var7 instanceof TileEntityCommandBlock)
                            {
                                ((TileEntityCommandBlock)var7).setCommand(var6);
                                playerEntity.worldObj.markBlockForUpdate(var14, var18, var5);
                                playerEntity.sendChatToPlayer("Command set: " + var6);
                            }
                        }
                        catch (Exception var9)
                        {
                            var9.printStackTrace();
                        }
                    }
                    else
                    {
                        playerEntity.sendChatToPlayer(playerEntity.translateString("advMode.notAllowed", new Object[0]));
                    }
                }
                else if ("MC|Beacon".equals(par1Packet250CustomPayload.channel))
                {
                    if (playerEntity.openContainer instanceof ContainerBeacon)
                    {
                        try
                        {
                            var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                            var14 = var2.readInt();
                            var18 = var2.readInt();
                            ContainerBeacon var17 = (ContainerBeacon)playerEntity.openContainer;
                            Slot var19 = var17.getSlot(0);

                            if (var19.getHasStack())
                            {
                                var19.decrStackSize(1);
                                TileEntityBeacon var20 = var17.getBeacon();
                                var20.setPrimaryEffect(var14);
                                var20.setSecondaryEffect(var18);
                                var20.onInventoryChanged();
                            }
                        }
                        catch (Exception var8)
                        {
                            var8.printStackTrace();
                        }
                    }
                }
                else if ("MC|ItemName".equals(par1Packet250CustomPayload.channel) && playerEntity.openContainer instanceof ContainerRepair)
                {
                    ContainerRepair var13 = (ContainerRepair)playerEntity.openContainer;

                    if (par1Packet250CustomPayload.data != null && par1Packet250CustomPayload.data.length >= 1)
                    {
                        String var15 = ChatAllowedCharacters.filerAllowedCharacters(new String(par1Packet250CustomPayload.data));

                        if (var15.length() <= 30)
                        {
                            var13.updateItemName(var15);
                        }
                    }
                    else
                    {
                        var13.updateItemName("");
                    }
                }
            }
        }
    }
}
