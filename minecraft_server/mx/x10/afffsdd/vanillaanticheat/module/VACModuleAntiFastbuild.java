package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiFastbuild implements IVacModule
{
    /**
     * The number of blocks built.
     */
    private int blockCount;
    /**
     * Whether a warning about the player buildhacking has already been sent or not.
     */
    private boolean warningSent;

    public VACModuleAntiFastbuild()
    {
        blockCount = 0;
        warningSent = false;
    }

    public String getModuleName()
    {
        return "Anti-Fastbuild";
    }

    public void updateState()
    {
        if (blockCount > 0) --blockCount;
        if (blockCount == 0) warningSent = false;
    }

    /**
     * Increments blockCount by i
     * 
     * @param i
     *            the number to increment blockCount by
     */
    public void incrementBlockCount(int i)
    {
        blockCount += i;
    }

    /**
     * Gets the number of blocks the player has built.
     * 
     * @return the number of blocks the player has built
     */
    public int getBuildCount()
    {
        return blockCount;
    }
    
    public void sendWarning()
    {
        warningSent = true;
    }
    
    public boolean isWarningSent()
    {
        return warningSent;
    }
}
