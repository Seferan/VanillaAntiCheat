package mx.x10.afffsdd.vanillaanticheat.module;

import net.minecraft.entity.player.EntityPlayerMP;

public class VACModuleAntiFly implements IVacModule
{
    private int afResetCount;
    private double antiFlyX;
    private double antiFlyZ;
    
    public VACModuleAntiFly()
    {
        afResetCount = 0;
        antiFlyX = 0.0;
        antiFlyZ = 0.0;
    }
    
    public String getModuleName()
    {
        return "Anti-Fly";
    }

    public void updateState()
    {
    }

    public void setAntiFlyPosition(EntityPlayerMP player)
    {
        antiFlyX = player.posX;
        antiFlyZ = player.posZ;
    }

    public double getAntiFlyX()
    {
        return antiFlyX;
    }

    public double getAntiFlyZ()
    {
        return antiFlyZ;
    }

    public void incrementFlyResetCount()
    {
        afResetCount++;
    }

    public int getFlyResetCount()
    {
        return afResetCount;
    }
}
