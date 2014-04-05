package mx.x10.afffsdd.vanillaanticheat;

import net.minecraft.entity.player.EntityPlayerMP;

public class VACState
{
    public VACState()
    {
        ticksTakenToBreakBlock = 0;
        isBreakingBlock = false;
        totalDeviations = 0;
        totalMined = 0;

        builtBlockCount = 0;
        kickedForBuildhack = false;

        afResetCount = 0;
        afHasBeenLogged = false;
        antiFlyX = 0.0;
        antiFlyZ = 0.0;
    }

    // ANTI-FASTBREAK
    // ===============================================================================

    // The number of ticks it ACTUALLY took for the player to break the block
    private int ticksTakenToBreakBlock;
    private boolean isBreakingBlock;
    // Number of times the player broke a block too quickly
    private int totalDeviations;
    // Total number of times the block was mined (even if broken too quickly)
    private int totalMined;

    public void updateState()
    {
        if (builtBlockCount > 0) --builtBlockCount;
        if (isBreakingBlock) ticksTakenToBreakBlock++;
    }

    public void resetDigStatus()
    {
        isBreakingBlock = false;
    }

    public void startDiggingBlock()
    {
        ticksTakenToBreakBlock = 0;
        isBreakingBlock = true;
    }

    public void incrementTotalDeviations()
    {
        totalDeviations++;
    }

    public void incrementTotalMined()
    {
        totalMined++;
        // Reset the ratio periodically
        if (totalMined >= 100)
        {
            totalMined = 0;
            totalDeviations = 0;
        }
    }

    public boolean isTotalMinedNonzero()
    {
        return totalMined > 0;
    }

    public int getTicksTakenToBreakBlock()
    {
        return ticksTakenToBreakBlock;
    }

    public double getDeviationRatio()
    {
        return totalDeviations / totalMined;
    }

    // ANTI-FASTBUILD
    // ===============================================================================
    private int builtBlockCount;
    private boolean kickedForBuildhack;

    public boolean isAlreadyKicked()
    {
        return kickedForBuildhack;
    }

    public void kickMe()
    {
        kickedForBuildhack = true;
    }

    public void incrementBuildCount(int i)
    {
        builtBlockCount += i;
    }

    public int getBuildCount()
    {
        return builtBlockCount;
    }

    // ANTI-FLY
    // ===============================================================================
    private int afResetCount;
    private boolean afHasBeenLogged;
    private double antiFlyX;
    private double antiFlyZ;

    public void setAntiFlyPosition(EntityPlayerMP player)
    {
        antiFlyX = player.posX;
        antiFlyZ = player.posZ;
    }

    public double getAntiFlyX()
    {
        return antiFlyX;
    }

    public double getAntiFlyZ()
    {
        return antiFlyZ;
    }

    public void incrementFlyResetCount()
    {
        afResetCount++;
    }

    public int getFlyResetCount()
    {
        return afResetCount;
    }

    public boolean hasFlyResetBeenLogged()
    {
        return afHasBeenLogged;
    }

    public void logFlyReset()
    {
        afHasBeenLogged = true;
    }
}
