package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiFastbreak implements IVacModule
{
    // The number of ticks it ACTUALLY took for the player to break the block
    private int ticksTakenToBreakBlock;
    private boolean isBreakingBlock;
    // Number of times the player broke a block too quickly
    private int totalDeviations;
    // Total number of times the block was mined (even if broken too quickly)
    private int totalMined;
    
    public VACModuleAntiFastbreak()
    {
        ticksTakenToBreakBlock = 0;
        isBreakingBlock = false;
        totalDeviations = 0;
        totalMined = 0;
    }
    
    public String getModuleName()
    {
        return "Anti-Fastbreak";
    }
    
    public void updateState()
    {
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
        // System.out.println(String.valueOf(totalDeviations) + "/" + String.valueOf(totalMined) + " (" + getDeviationRatio() + ")");
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
        return totalDeviations / Math.max(totalMined, 0.0);
    }
}
