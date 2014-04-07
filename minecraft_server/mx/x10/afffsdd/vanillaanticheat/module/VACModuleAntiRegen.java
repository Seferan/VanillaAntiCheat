package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiRegen implements IVacModule
{
    /**
     * The number of ticks since the last heal.
     */
    long ticksSince;
    /**
     * Whether this is the first time the player is healing or not.
     */
    boolean firstHeal;
    /**
     * Whether the player regenerating health too fast was already logged or
     * not.
     */
    boolean logged;

    public VACModuleAntiRegen()
    {
        ticksSince = -1;
        firstHeal = true;
        logged = false;
    }

    public String getModuleName()
    {
        return "Anti-Regen";
    }

    public void updateState()
    {
        if (!firstHeal) ticksSince++;
    }

    public long getTicksSinceLastHeal()
    {
        return ticksSince;
    }

    public void heal()
    {
        ticksSince = 0;
        if (firstHeal) firstHeal = false;
    }

    /**
     * Set logged to true;
     */
    public void log()
    {
        logged = true;
    }

    /**
     * Returns logged.
     * 
     * @return logged
     */
    public boolean hasBeenLogged()
    {
        return logged;
    }
}
