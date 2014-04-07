package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleDiamondNotifications implements IVacModule
{
    /**
     * The number of ticks since the player last mined a diamond ore.
     */
    private int ticksSinceLastOre;
    /**
     * The number of diamond veins the player has mined in total.
     */
    private int veinsMined;

    public VACModuleDiamondNotifications()
    {
        ticksSinceLastOre = 0;
        veinsMined = 0;
    }

    public String getModuleName()
    {
        return "Diamond Notifications";
    }

    public void updateState()
    {
        ticksSinceLastOre++;
    }

    /**
     * Reset ticksSinceLastOre to 0.
     */
    public void resetTicksSinceLastOre()
    {
        ticksSinceLastOre = 0;
    }

    /**
     * Increment the number of veins mined by 1.
     */
    public void incrementVeinsMined()
    {
        veinsMined++;
    }

    public int getNumberOfVeins()
    {
        return veinsMined;
    }

    /**
     * Returns true if the player is mining a new vein.
     * 
     * @return whether the player is mining a new vein or not.
     */
    // TODO: add detection by seeing if blocks are adjacent or not
    public boolean isMiningNewVein()
    {
        return ticksSinceLastOre > 100;
    }
}
