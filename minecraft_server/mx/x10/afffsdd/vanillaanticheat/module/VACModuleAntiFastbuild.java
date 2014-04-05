package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiFastbuild implements IVacModule
{
    private int builtBlockCount;
    private boolean kickedForBuildhack;

    public VACModuleAntiFastbuild()
    {
        builtBlockCount = 0;
        kickedForBuildhack = false;
    }
    
    public String getModuleName()
    {
        return "Anti-Fastbuild";
    }
    
    public void updateState()
    {
        if (builtBlockCount > 0) --builtBlockCount;
    }

    public boolean isAlreadyKicked()
    {
        return kickedForBuildhack;
    }

    public void kickMe()
    {
        kickedForBuildhack = true;
    }

    public void incrementBuildCount(int i)
    {
        builtBlockCount += i;
    }

    public int getBuildCount()
    {
        return builtBlockCount;
    }
}
