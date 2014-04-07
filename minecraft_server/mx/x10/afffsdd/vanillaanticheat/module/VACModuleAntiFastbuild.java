package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiFastbuild implements IVacModule
{
    /**
     * The number of blocks built.
     */
    private int blockCount;
    /**
     * Whether the player has already been kicked for buildhacking.
     */
    private boolean kicked;

    public VACModuleAntiFastbuild()
    {
        blockCount = 0;
        kicked = false;
    }

    public String getModuleName()
    {
        return "Anti-Fastbuild";
    }

    public void updateState()
    {
        if (blockCount > 0) --blockCount;
    }

    public boolean isAlreadyKicked()
    {
        return kicked;
    }

    /**
     * Sets kicked to true.
     */
    public void kickMe()
    {
        kicked = true;
    }

    /**
     * Increments blockCount by i
     * @param i the number to increment blockCount by
     */
    public void incrementBlockCount(int i)
    {
        blockCount += i;
    }

    /**
     * Gets the number of blocks the player has built.
     * @return the number of blocks the player has built
     */
    public int getBuildCount()
    {
        return blockCount;
    }
}
