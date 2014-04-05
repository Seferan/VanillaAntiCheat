package mx.x10.afffsdd.vanillaanticheat;

public class VACState {
    // ANTI-FASTBREAK
	// ===============================================================================
	
    // The number of ticks it ACTUALLY took for the player to break the block 
    private static int ticksTakenToBreakBlock = 0;
    private static boolean isBreakingBlock = false;
    // Number of times the player broke a block too quickly
    private static int totalDeviations = 0;
    // Total number of times the block was mined (even if broken too quickly)
    private static int totalMined = 0;

    public static void updateState()
    {
        if (builtBlockCount > 0) --builtBlockCount;
        if(isBreakingBlock) ticksTakenToBreakBlock++;
    }
    
    public static void resetDigStatus()
    {
    	isBreakingBlock = false;
    }
    
    public static void startDiggingBlock()
    {
    	ticksTakenToBreakBlock = 0;
    	isBreakingBlock = true;
    }
    
    public static void incrementTotalDeviations()
    {
    	totalDeviations++;
    }
    
    public static void incrementTotalMined()
    {
    	totalMined++;
        // Reset the ratio periodically
        if(totalMined >= 100)
        {
        	totalMined = 0;
        	totalDeviations = 0;
        }
    }
    
    public static boolean isTotalMinedNonzero()
    {
    	return totalMined > 0;
    }
    
    public static int getTicksTakenToBreakBlock()
    {
    	return ticksTakenToBreakBlock;
    }
    
    public static double getDeviationRatio()
    {
    	return totalDeviations / totalMined;
    }
    
    // ANTI-FASTBUILD
 	// ===============================================================================
	private static int builtBlockCount = 0;
    private static boolean kickedForBuildhack = false;
    
    public static boolean isAlreadyKicked()
    {
    	return kickedForBuildhack;
    }
    
    public static void kickMe()
    {
    	kickedForBuildhack = true;
    }
    
    public static void incrementBuildCount(int i)
    {
    	builtBlockCount += i;
    }
    
    public static int getBuildCount()
    {
    	return builtBlockCount;
    }
}
