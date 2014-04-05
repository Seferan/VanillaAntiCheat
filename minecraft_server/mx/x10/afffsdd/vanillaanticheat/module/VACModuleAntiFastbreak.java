package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiFastbreak implements IVacModule
{
    /**
     * The number of ticks it ACTUALLY took for the player to break the block
     */
    private int ticksTaken;
    private boolean isBreakingBlock;
    /**
     * Number of times the player broke a block too quickly
     */
    private int deviations;
    /**
     *  Total number of times the block was mined (even if broken too quickly)
     */
    private int mined;
    
    public VACModuleAntiFastbreak()
    {
        ticksTaken = 0;
        isBreakingBlock = false;
        deviations = 0;
        mined = 0;
    }
    
    public String getModuleName()
    {
        return "Anti-Fastbreak";
    }
    
    public void updateState()
    {
        if (isBreakingBlock) ticksTaken++;
    }
    
    /**
     * Reset isBreakingBlock to false.
     */
    public void resetDigStatus()
    {
        isBreakingBlock = false;
    }

    /**
     * Start digging a block. Resets ticksTaken and sets isBreakingBlock to
     * true.
     */
    public void startDiggingBlock()
    {
        ticksTaken = 0;
        isBreakingBlock = true;
    }

    public void incrementDeviations()
    {
        deviations++;
    }

    /**
     * Increment mined by 1 and reset mined and deviations of mined reaches 100.
     */
    public void incrementMined()
    {
        mined++;
        // System.out.println(String.valueOf(totalDeviations) + "/" + String.valueOf(totalMined) + " (" + getDeviationRatio() + ")");
        // Reset the ratio periodically
        if (mined >= 100)
        {
            mined = 0;
            deviations = 0;
        }
    }

    public boolean isMinedNonzero()
    {
        return mined > 0;
    }

    public int getTicksTaken()
    {
        return ticksTaken;
    }

    /**
     * Gets the ratio of deviations to mined blocks.
     * @return the ratio of deviations to mined blocks.
     */
    public double getDeviationRatio()
    {
        return deviations / Math.max(mined, 0.0);
    }
}
