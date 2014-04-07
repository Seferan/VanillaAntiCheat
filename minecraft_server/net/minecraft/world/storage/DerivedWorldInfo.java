package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class DerivedWorldInfo extends WorldInfo
{
    /** Instance of WorldInfo. */
    private final WorldInfo theWorldInfo;
    private static final String __OBFID = "CL_00000584";

    public DerivedWorldInfo(WorldInfo par1WorldInfo)
    {
        theWorldInfo = par1WorldInfo;
    }

    /**
     * Gets the NBTTagCompound for the worldInfo
     */
    public NBTTagCompound getNBTTagCompound()
    {
        return theWorldInfo.getNBTTagCompound();
    }

    /**
     * Creates a new NBTTagCompound for the world, with the given NBTTag as the
     * "Player"
     */
    public NBTTagCompound cloneNBTCompound(NBTTagCompound par1NBTTagCompound)
    {
        return theWorldInfo.cloneNBTCompound(par1NBTTagCompound);
    }

    /**
     * Returns the seed of current world.
     */
    public long getSeed()
    {
        return theWorldInfo.getSeed();
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX()
    {
        return theWorldInfo.getSpawnX();
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY()
    {
        return theWorldInfo.getSpawnY();
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ()
    {
        return theWorldInfo.getSpawnZ();
    }

    public long getWorldTotalTime()
    {
        return theWorldInfo.getWorldTotalTime();
    }

    /**
     * Get current world time
     */
    public long getWorldTime()
    {
        return theWorldInfo.getWorldTime();
    }

    /**
     * Returns the player's NBTTagCompound to be loaded
     */
    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return theWorldInfo.getPlayerNBTTagCompound();
    }

    public int getDimension()
    {
        return theWorldInfo.getDimension();
    }

    /**
     * Get current world name
     */
    public String getWorldName()
    {
        return theWorldInfo.getWorldName();
    }

    /**
     * Returns the save version of this world
     */
    public int getSaveVersion()
    {
        return theWorldInfo.getSaveVersion();
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering()
    {
        return theWorldInfo.isThundering();
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime()
    {
        return theWorldInfo.getThunderTime();
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean isRaining()
    {
        return theWorldInfo.isRaining();
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime()
    {
        return theWorldInfo.getRainTime();
    }

    /**
     * Gets the GameType.
     */
    public WorldSettings.GameType getGameType()
    {
        return theWorldInfo.getGameType();
    }

    public void incrementTotalWorldTime(long par1)
    {
    }

    /**
     * Set current world time
     */
    public void setWorldTime(long par1)
    {
    }

    /**
     * Sets the spawn zone position. Args: x, y, z
     */
    public void setSpawnPosition(int par1, int par2, int par3)
    {
    }

    public void setWorldName(String par1Str)
    {
    }

    /**
     * Sets the save version of the world
     */
    public void setSaveVersion(int par1)
    {
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(boolean par1)
    {
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(int par1)
    {
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setRaining(boolean par1)
    {
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(int par1)
    {
    }

    /**
     * Get whether the map features (e.g. strongholds) generation is enabled or
     * disabled.
     */
    public boolean isMapFeaturesEnabled()
    {
        return theWorldInfo.isMapFeaturesEnabled();
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcoreModeEnabled()
    {
        return theWorldInfo.isHardcoreModeEnabled();
    }

    public WorldType getTerrainType()
    {
        return theWorldInfo.getTerrainType();
    }

    public void setTerrainType(WorldType par1WorldType)
    {
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean areCommandsAllowed()
    {
        return theWorldInfo.areCommandsAllowed();
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized()
    {
        return theWorldInfo.isInitialized();
    }

    /**
     * Sets the initialization status of the World.
     */
    public void setServerInitialized(boolean par1)
    {
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRulesInstance()
    {
        return theWorldInfo.getGameRulesInstance();
    }
}
