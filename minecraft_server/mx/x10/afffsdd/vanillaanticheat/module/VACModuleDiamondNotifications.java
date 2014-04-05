package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleDiamondNotifications implements IVacModule
{
    private int ticksSinceLastOreMined;
    private int veinsMined;
    
    public VACModuleDiamondNotifications()
    {
        ticksSinceLastOreMined = 0;
        veinsMined = 0;
    }
    
    public String getModuleName()
    {
        return "Diamond Notifications";
    }

    public void updateState()
    {
        ticksSinceLastOreMined++;
    }
    
    public void resetTicksSinceLastOre()
    {
        ticksSinceLastOreMined = 0;
    }
    
    public void incrementVeinsMined()
    {
        veinsMined++;
    }
    
    public int getNumberOfVeins()
    {
        return veinsMined;
    }
    
    public boolean isMiningNewVein()
    {
        return ticksSinceLastOreMined > 100;
    }
}
