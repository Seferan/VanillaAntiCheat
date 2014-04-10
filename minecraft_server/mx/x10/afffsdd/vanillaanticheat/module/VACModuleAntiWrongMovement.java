package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiWrongMovement implements IVacModule
{
    /**
     * The number of times the player has moved wrongly.
     */
    public int count;
    
    public VACModuleAntiWrongMovement()
    {
        count = 0;
    }
    
    public String getModuleName()
    {
        return "Anti-Wrong Movement";
    }

    public void updateState()
    {
    }

    public void incrementCount()
    {
        count++;
    }
    
    public int getCount()
    {
        return count;
    }
}
