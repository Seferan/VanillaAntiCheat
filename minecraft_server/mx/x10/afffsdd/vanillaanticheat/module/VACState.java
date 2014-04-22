package mx.x10.afffsdd.vanillaanticheat.module;

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
    /**
     * The anti-spam module.
     */
    public VACModuleAntiSpam aSpam;
    /**
     * The anti-wrong movement module.
     */
    public VACModuleAntiWrongMovement aWrong;
    /**
     * The block log module
     */
    public VACModuleBlockLog blockLog;

    public VACState()
    {
        aFastBreak = new VACModuleAntiFastbreak();
        aFastBuild = new VACModuleAntiFastbuild();
        aFly = new VACModuleAntiFly();
        dNotifications = new VACModuleDiamondNotifications();
        aVClip = new VACModuleAntiVClip();
        aRegen = new VACModuleAntiRegen();
        aSpeed = new VACModuleAntiSpeed();
        aSpam = new VACModuleAntiSpam();
        aWrong = new VACModuleAntiWrongMovement();
        blockLog = new VACModuleBlockLog();
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
        aSpam.updateState();
        aWrong.updateState();
        blockLog.updateState();
    }
}
