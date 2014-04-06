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
    /**
     * The anti-vclip module.
     */
    public VACModuleAntiVClip aVClip;
    /**
     * The anti-regen module.
     */
    public VACModuleAntiRegen aRegen;
    /**
     * The anti-speedhack module.
     */
    public VACModuleAntiSpeed aSpeed;
    
    public VACState()
    {
        aFastBreak = new VACModuleAntiFastbreak();
        aFastBuild = new VACModuleAntiFastbuild();
        aFly = new VACModuleAntiFly();
        dNotifications = new VACModuleDiamondNotifications();
        aVClip = new VACModuleAntiVClip();
        aRegen = new VACModuleAntiRegen();
        aSpeed = new VACModuleAntiSpeed();
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
        aVClip.updateState();
        aRegen.updateState();
        aSpeed.updateState();
    }
}
