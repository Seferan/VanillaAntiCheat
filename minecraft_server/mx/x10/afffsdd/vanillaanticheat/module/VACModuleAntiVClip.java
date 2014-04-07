package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiVClip implements IVacModule
{
    /**
     * The number of times the player has been detected for VClipping.
     */
    private int detections = 0;

    public VACModuleAntiVClip()
    {

    }

    public String getModuleName()
    {
        return "Anti-VClip (Teleport)";
    }

    public void updateState()
    {

    }

    public void incrementDetections()
    {
        detections++;
    }

    public int getDetections()
    {
        return detections;
    }
}
