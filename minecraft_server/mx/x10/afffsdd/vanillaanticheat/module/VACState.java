package mx.x10.afffsdd.vanillaanticheat.module;

import net.minecraft.entity.player.EntityPlayerMP;

public class VACState implements IVacModule
{
    /**
     * The anti-fastbreak module.
     */
    public VACModuleAntiFastbreak aFastBreak;
    /**
     * The anti-fastbuild module.
     */
    public VACModuleAntiFastbuild aFastBuild;
    /**
     * The anti-fly module.
     */
    public VACModuleAntiFly aFly;
    /**
     * The diamond notifications module.
     */
    public VACModuleDiamondNotifications dNotifications;
    
    public VACState()
    {
        aFastBreak = new VACModuleAntiFastbreak();
        aFastBuild = new VACModuleAntiFastbuild();
        aFly = new VACModuleAntiFly();
        dNotifications = new VACModuleDiamondNotifications();
    }
    
    public String getModuleName()
    {
        return "VACState";
    }

    public void updateState()
    {
        aFastBreak.updateState();
        aFastBuild.updateState();
        aFly.updateState();
        dNotifications.updateState();
    }
}
