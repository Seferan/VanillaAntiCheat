package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities
{
    /** Disables player damage. */
    public boolean disableDamage;

    /** Sets/indicates whether the player is flying. */
    public boolean isFlying;

    /** whether or not to allow the player to fly when they double jump. */
    public boolean allowFlying;

    /**
     * Used to determine if creative mode is enabled, and therefore if items
     * should be depleted on usage
     */
    public boolean isCreativeMode;

    /** Indicates whether the player is allowed to modify the surroundings */
    public boolean allowEdit = true;
    private float flySpeed = 0.05F;
    private float walkSpeed = 0.1F;
    private static final String __OBFID = "CL_00001708";

    public void writeCapabilitiesToNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound var2 = new NBTTagCompound();
        var2.setBoolean("invulnerable", disableDamage);
        var2.setBoolean("flying", isFlying);
        var2.setBoolean("mayfly", allowFlying);
        var2.setBoolean("instabuild", isCreativeMode);
        var2.setBoolean("mayBuild", allowEdit);
        var2.setFloat("flySpeed", flySpeed);
        var2.setFloat("walkSpeed", walkSpeed);
        par1NBTTagCompound.setTag("abilities", var2);
    }

    public void readCapabilitiesFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.func_150297_b("abilities", 10))
        {
            NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("abilities");
            disableDamage = var2.getBoolean("invulnerable");
            isFlying = var2.getBoolean("flying");
            allowFlying = var2.getBoolean("mayfly");
            isCreativeMode = var2.getBoolean("instabuild");

            if (var2.func_150297_b("flySpeed", 99))
            {
                flySpeed = var2.getFloat("flySpeed");
                walkSpeed = var2.getFloat("walkSpeed");
            }

            if (var2.func_150297_b("mayBuild", 1))
            {
                allowEdit = var2.getBoolean("mayBuild");
            }
        }
    }

    public float getFlySpeed()
    {
        return flySpeed;
    }

    public float getWalkSpeed()
    {
        return walkSpeed;
    }
}
