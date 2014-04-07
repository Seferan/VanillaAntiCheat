package mx.x10.afffsdd.vanillaanticheat.module;

import net.minecraft.entity.player.EntityPlayerMP;

public class VACModuleAntiFly implements IVacModule
{
    /**
     * The number of times the player has been reset for flying
     */
    private int resetCount;
    /**
     * The x-coordinate to reset the player to
     */
    private double x;
    /**
     * The z-coordinate to reset the player to
     */
    private double z;

    public VACModuleAntiFly()
    {
        resetCount = 0;
        x = 0.0;
        z = 0.0;
    }

    public String getModuleName()
    {
        return "Anti-Fly";
    }

    public void updateState()
    {
    }

    /**
     * Sets the position to reset the player back to
     * @param player the player to reset later
     */
    public void setAntiFlyPosition(EntityPlayerMP player)
    {
        x = player.posX;
        z = player.posZ;
    }

    public double getX()
    {
        return x;
    }

    public double getZ()
    {
        return z;
    }

    public void incrementResetCount()
    {
        resetCount++;
    }

    public int getFlyResetCount()
    {
        return resetCount;
    }
}
