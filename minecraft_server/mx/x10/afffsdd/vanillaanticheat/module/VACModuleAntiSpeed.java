package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiSpeed implements IVacModule
{
    /**
     * The number of times the player has been reset for speeding.
     */
    public int resets;
    /**
     * The number of times the player has moved.
     */
    public int moved;
    /**
     * The number of times the player has moved to quickly.
     */
    public int speeded;
    /**
     * Whether the player was sneaking or not.
     */
    public boolean wasSneaking;
    /**
     * The amount of ticks since the player started sneaking.
     */
    public long timeSinceSneakingStarted;
    /**
     * The amount of ticks since the player last jumped in bunnyhop.
     */
    public long timeSinceLastBhop;

    public VACModuleAntiSpeed()
    {
        resets = 0;
        moved = 0;
        speeded = 0;
        wasSneaking = false;
        timeSinceSneakingStarted = 0;
        timeSinceLastBhop = 0;
    }

    public String getModuleName()
    {
        return "Anti-Speedhack";
    }

    public void updateState()
    {
        if (timeSinceLastBhop <= 5) timeSinceLastBhop++;
    }

    /**
     * Increment moved and reset the ratio if necessary.
     */
    public void onMove()
    {
        moved++;
        // Reset the ratio periodically
        if (moved >= 1000)
        {
            moved = 0;
            speeded = 0;
        }
    }

    /**
     * Get the ratio of the number of times the player moved to fast compared to
     * the number of times they moved
     */
    public double getSpeedingRatio()
    {
        return speeded / Math.max(moved, 1.0);
    }

    /**
     * Give the player a speeding ticket and reset them.
     */
    public void giveSpeedingTicket()
    {
        resets++;
    }

    /**
     * Increment the number of times the player moved too quickly.
     */
    public void onSpeeding()
    {
        speeded++;
    }

    public boolean wasSneaking()
    {
        return wasSneaking;
    }

    public long getTimeSinceLastBhop()
    {
        return timeSinceLastBhop;
    }

    /**
     * Reset the time since the player last jumped.
     */
    public void onBhop()
    {
        timeSinceLastBhop = 0;
    }

    public int resets()
    {
        return resets;
    }

    /**
     * Sets whether the player was sneaking or not depending on how long ago
     * they started sneaking. This is to give them some time to slow down after
     * sneaking.
     * 
     * @param isSneaking
     *            whether the player is sneaking or not
     */
    public void setSneaking(boolean isSneaking)
    {
        wasSneaking = isSneaking;
        // This will give the player 1 second to slow down as Minecraft changes
        // the speed gradually
        if (timeSinceSneakingStarted >= 10)
            wasSneaking = isSneaking;
        else
            timeSinceSneakingStarted++;
        // If the player stops sneaking, reset the timer
        if (!isSneaking && !wasSneaking) timeSinceSneakingStarted = 0;
    }
}
