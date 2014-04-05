package mx.x10.afffsdd.vanillaanticheat.module;

public interface IVacModule
{
    /**
     * Get the name of the VAC module.
     * @return name of the VAC module
     */
    public String getModuleName();
    
    /**
     * Update the state of the VAC Module. Should be performed
     * once per tick
     */
    public void updateState();
}
