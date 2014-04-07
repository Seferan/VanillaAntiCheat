package net.minecraft.world.storage;

import java.util.concurrent.Callable;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class WorldInfo
{
    /** Holds the seed of the currently world. */
    private long randomSeed;
    private WorldType terrainType;
    private String generatorOptions;

    /** The spawn zone position X coordinate. */
    private int spawnX;

    /** The spawn zone position Y coordinate. */
    private int spawnY;

    /** The spawn zone position Z coordinate. */
    private int spawnZ;

    /** Total time for this world. */
    private long totalTime;

    /** The current world time in ticks, ranging from 0 to 23999. */
    private long worldTime;

    /** The last time the player was in this world. */
    private long lastTimePlayed;

    /** The size of entire save of current world on the disk, isn't exactly. */
    private long sizeOnDisk;
    private NBTTagCompound playerTag;
    private int dimension;

    /** The name of the save defined at world creation. */
    private String levelName;

    /** Introduced in beta 1.3, is the save version for future control. */
    private int saveVersion;

    /** True if it's raining, false otherwise. */
    private boolean raining;

    /** Number of ticks until next rain. */
    private int rainTime;

    /** Is thunderbolts failing now? */
    private boolean thundering;

    /** Number of ticks untils next thunderbolt. */
    private int thunderTime;

    /** The Game Type. */
    private WorldSettings.GameType theGameType;

    /**
     * Whether the map features (e.g. strongholds) generation is enabled or
     * disabled.
     */
    private boolean mapFeaturesEnabled;

    /** Hardcore mode flag */
    private boolean hardcore;
    private boolean allowCommands;
    private boolean initialized;
    private GameRules theGameRules;
    private static final String __OBFID = "CL_00000587";

    protected WorldInfo()
    {
        terrainType = WorldType.DEFAULT;
        generatorOptions = "";
        theGameRules = new GameRules();
    }

    public WorldInfo(NBTTagCompound par1NBTTagCompound)
    {
        terrainType = WorldType.DEFAULT;
        generatorOptions = "";
        theGameRules = new GameRules();
        randomSeed = par1NBTTagCompound.getLong("RandomSeed");

        if (par1NBTTagCompound.func_150297_b("generatorName", 8))
        {
            String var2 = par1NBTTagCompound.getString("generatorName");
            terrainType = WorldType.parseWorldType(var2);

            if (terrainType == null)
            {
                terrainType = WorldType.DEFAULT;
            }
            else if (terrainType.isVersioned())
            {
                int var3 = 0;

                if (par1NBTTagCompound.func_150297_b("generatorVersion", 99))
                {
                    var3 = par1NBTTagCompound.getInteger("generatorVersion");
                }

                terrainType = terrainType.getWorldTypeForGeneratorVersion(var3);
            }

            if (par1NBTTagCompound.func_150297_b("generatorOptions", 8))
            {
                generatorOptions = par1NBTTagCompound.getString("generatorOptions");
            }
        }

        theGameType = WorldSettings.GameType.getByID(par1NBTTagCompound.getInteger("GameType"));

        if (par1NBTTagCompound.func_150297_b("MapFeatures", 99))
        {
            mapFeaturesEnabled = par1NBTTagCompound.getBoolean("MapFeatures");
        }
        else
        {
            mapFeaturesEnabled = true;
        }

        spawnX = par1NBTTagCompound.getInteger("SpawnX");
        spawnY = par1NBTTagCompound.getInteger("SpawnY");
        spawnZ = par1NBTTagCompound.getInteger("SpawnZ");
        totalTime = par1NBTTagCompound.getLong("Time");

        if (par1NBTTagCompound.func_150297_b("DayTime", 99))
        {
            worldTime = par1NBTTagCompound.getLong("DayTime");
        }
        else
        {
            worldTime = totalTime;
        }

        lastTimePlayed = par1NBTTagCompound.getLong("LastPlayed");
        sizeOnDisk = par1NBTTagCompound.getLong("SizeOnDisk");
        levelName = par1NBTTagCompound.getString("LevelName");
        saveVersion = par1NBTTagCompound.getInteger("version");
        rainTime = par1NBTTagCompound.getInteger("rainTime");
        raining = par1NBTTagCompound.getBoolean("raining");
        thunderTime = par1NBTTagCompound.getInteger("thunderTime");
        thundering = par1NBTTagCompound.getBoolean("thundering");
        hardcore = par1NBTTagCompound.getBoolean("hardcore");

        if (par1NBTTagCompound.func_150297_b("initialized", 99))
        {
            initialized = par1NBTTagCompound.getBoolean("initialized");
        }
        else
        {
            initialized = true;
        }

        if (par1NBTTagCompound.func_150297_b("allowCommands", 99))
        {
            allowCommands = par1NBTTagCompound.getBoolean("allowCommands");
        }
        else
        {
            allowCommands = theGameType == WorldSettings.GameType.CREATIVE;
        }

        if (par1NBTTagCompound.func_150297_b("Player", 10))
        {
            playerTag = par1NBTTagCompound.getCompoundTag("Player");
            dimension = playerTag.getInteger("Dimension");
        }

        if (par1NBTTagCompound.func_150297_b("GameRules", 10))
        {
            theGameRules.readGameRulesFromNBT(par1NBTTagCompound.getCompoundTag("GameRules"));
        }
    }

    public WorldInfo(WorldSettings par1WorldSettings, String par2Str)
    {
        terrainType = WorldType.DEFAULT;
        generatorOptions = "";
        theGameRules = new GameRules();
        randomSeed = par1WorldSettings.getSeed();
        theGameType = par1WorldSettings.getGameType();
        mapFeaturesEnabled = par1WorldSettings.isMapFeaturesEnabled();
        levelName = par2Str;
        hardcore = par1WorldSettings.getHardcoreEnabled();
        terrainType = par1WorldSettings.getTerrainType();
        generatorOptions = par1WorldSettings.func_82749_j();
        allowCommands = par1WorldSettings.areCommandsAllowed();
        initialized = false;
    }

    public WorldInfo(WorldInfo par1WorldInfo)
    {
        terrainType = WorldType.DEFAULT;
        generatorOptions = "";
        theGameRules = new GameRules();
        randomSeed = par1WorldInfo.randomSeed;
        terrainType = par1WorldInfo.terrainType;
        generatorOptions = par1WorldInfo.generatorOptions;
        theGameType = par1WorldInfo.theGameType;
        mapFeaturesEnabled = par1WorldInfo.mapFeaturesEnabled;
        spawnX = par1WorldInfo.spawnX;
        spawnY = par1WorldInfo.spawnY;
        spawnZ = par1WorldInfo.spawnZ;
        totalTime = par1WorldInfo.totalTime;
        worldTime = par1WorldInfo.worldTime;
        lastTimePlayed = par1WorldInfo.lastTimePlayed;
        sizeOnDisk = par1WorldInfo.sizeOnDisk;
        playerTag = par1WorldInfo.playerTag;
        dimension = par1WorldInfo.dimension;
        levelName = par1WorldInfo.levelName;
        saveVersion = par1WorldInfo.saveVersion;
        rainTime = par1WorldInfo.rainTime;
        raining = par1WorldInfo.raining;
        thunderTime = par1WorldInfo.thunderTime;
        thundering = par1WorldInfo.thundering;
        hardcore = par1WorldInfo.hardcore;
        allowCommands = par1WorldInfo.allowCommands;
        initialized = par1WorldInfo.initialized;
        theGameRules = par1WorldInfo.theGameRules;
    }

    /**
     * Gets the NBTTagCompound for the worldInfo
     */
    public NBTTagCompound getNBTTagCompound()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        updateTagCompound(var1, playerTag);
        return var1;
    }

    /**
     * Creates a new NBTTagCompound for the world, with the given NBTTag as the
     * "Player"
     */
    public NBTTagCompound cloneNBTCompound(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound var2 = new NBTTagCompound();
        updateTagCompound(var2, par1NBTTagCompound);
        return var2;
    }

    private void updateTagCompound(NBTTagCompound par1NBTTagCompound, NBTTagCompound par2NBTTagCompound)
    {
        par1NBTTagCompound.setLong("RandomSeed", randomSeed);
        par1NBTTagCompound.setString("generatorName", terrainType.getWorldTypeName());
        par1NBTTagCompound.setInteger("generatorVersion", terrainType.getGeneratorVersion());
        par1NBTTagCompound.setString("generatorOptions", generatorOptions);
        par1NBTTagCompound.setInteger("GameType", theGameType.getID());
        par1NBTTagCompound.setBoolean("MapFeatures", mapFeaturesEnabled);
        par1NBTTagCompound.setInteger("SpawnX", spawnX);
        par1NBTTagCompound.setInteger("SpawnY", spawnY);
        par1NBTTagCompound.setInteger("SpawnZ", spawnZ);
        par1NBTTagCompound.setLong("Time", totalTime);
        par1NBTTagCompound.setLong("DayTime", worldTime);
        par1NBTTagCompound.setLong("SizeOnDisk", sizeOnDisk);
        par1NBTTagCompound.setLong("LastPlayed", MinecraftServer.getCurrentTimeMillis());
        par1NBTTagCompound.setString("LevelName", levelName);
        par1NBTTagCompound.setInteger("version", saveVersion);
        par1NBTTagCompound.setInteger("rainTime", rainTime);
        par1NBTTagCompound.setBoolean("raining", raining);
        par1NBTTagCompound.setInteger("thunderTime", thunderTime);
        par1NBTTagCompound.setBoolean("thundering", thundering);
        par1NBTTagCompound.setBoolean("hardcore", hardcore);
        par1NBTTagCompound.setBoolean("allowCommands", allowCommands);
        par1NBTTagCompound.setBoolean("initialized", initialized);
        par1NBTTagCompound.setTag("GameRules", theGameRules.writeGameRulesToNBT());

        if (par2NBTTagCompound != null)
        {
            par1NBTTagCompound.setTag("Player", par2NBTTagCompound);
        }
    }

    /**
     * Returns the seed of current world.
     */
    public long getSeed()
    {
        return randomSeed;
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX()
    {
        return spawnX;
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY()
    {
        return spawnY;
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ()
    {
        return spawnZ;
    }

    public long getWorldTotalTime()
    {
        return totalTime;
    }

    /**
     * Get current world time
     */
    public long getWorldTime()
    {
        return worldTime;
    }

    /**
     * Returns the player's NBTTagCompound to be loaded
     */
    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return playerTag;
    }

    public int getDimension()
    {
        return dimension;
    }

    public void incrementTotalWorldTime(long par1)
    {
        totalTime = par1;
    }

    /**
     * Set current world time
     */
    public void setWorldTime(long par1)
    {
        worldTime = par1;
    }

    /**
     * Sets the spawn zone position. Args: x, y, z
     */
    public void setSpawnPosition(int par1, int par2, int par3)
    {
        spawnX = par1;
        spawnY = par2;
        spawnZ = par3;
    }

    /**
     * Get current world name
     */
    public String getWorldName()
    {
        return levelName;
    }

    public void setWorldName(String par1Str)
    {
        levelName = par1Str;
    }

    /**
     * Returns the save version of this world
     */
    public int getSaveVersion()
    {
        return saveVersion;
    }

    /**
     * Sets the save version of the world
     */
    public void setSaveVersion(int par1)
    {
        saveVersion = par1;
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering()
    {
        return thundering;
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(boolean par1)
    {
        thundering = par1;
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime()
    {
        return thunderTime;
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(int par1)
    {
        thunderTime = par1;
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean isRaining()
    {
        return raining;
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setRaining(boolean par1)
    {
        raining = par1;
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime()
    {
        return rainTime;
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(int par1)
    {
        rainTime = par1;
    }

    /**
     * Gets the GameType.
     */
    public WorldSettings.GameType getGameType()
    {
        return theGameType;
    }

    /**
     * Get whether the map features (e.g. strongholds) generation is enabled or
     * disabled.
     */
    public boolean isMapFeaturesEnabled()
    {
        return mapFeaturesEnabled;
    }

    /**
     * Sets the GameType.
     */
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        theGameType = par1EnumGameType;
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcoreModeEnabled()
    {
        return hardcore;
    }

    public WorldType getTerrainType()
    {
        return terrainType;
    }

    public void setTerrainType(WorldType par1WorldType)
    {
        terrainType = par1WorldType;
    }

    public String getGeneratorOptions()
    {
        return generatorOptions;
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean areCommandsAllowed()
    {
        return allowCommands;
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized()
    {
        return initialized;
    }

    /**
     * Sets the initialization status of the World.
     */
    public void setServerInitialized(boolean par1)
    {
        initialized = par1;
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRulesInstance()
    {
        return theGameRules;
    }

    /**
     * Adds this WorldInfo instance to the crash report.
     */
    public void addToCrashReport(CrashReportCategory par1CrashReportCategory)
    {
        par1CrashReportCategory.addCrashSectionCallable("Level seed", new Callable()
        {
            private static final String __OBFID = "CL_00000588";

            public String call()
            {
                return String.valueOf(WorldInfo.this.getSeed());
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level generator", new Callable()
        {
            private static final String __OBFID = "CL_00000589";

            public String call()
            {
                return String.format("ID %02d - %s, ver %d. Features enabled: %b", new Object[] {Integer.valueOf(terrainType.getWorldTypeID()), terrainType.getWorldTypeName(), Integer.valueOf(terrainType.getGeneratorVersion()), Boolean.valueOf(mapFeaturesEnabled)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level generator options", new Callable()
        {
            private static final String __OBFID = "CL_00000590";

            public String call()
            {
                return generatorOptions;
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level spawn location", new Callable()
        {
            private static final String __OBFID = "CL_00000591";

            public String call()
            {
                return CrashReportCategory.getLocationInfo(spawnX, spawnY, spawnZ);
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level time", new Callable()
        {
            private static final String __OBFID = "CL_00000592";

            public String call()
            {
                return String.format("%d game time, %d day time", new Object[] {Long.valueOf(totalTime), Long.valueOf(worldTime)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level dimension", new Callable()
        {
            private static final String __OBFID = "CL_00000593";

            public String call()
            {
                return String.valueOf(dimension);
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level storage version", new Callable()
        {
            private static final String __OBFID = "CL_00000594";

            public String call()
            {
                String var1 = "Unknown?";

                try
                {
                    switch (saveVersion)
                    {
                    case 19132:
                        var1 = "McRegion";
                        break;

                    case 19133:
                        var1 = "Anvil";
                    }
                }
                catch (Throwable var3)
                {
                    ;
                }

                return String.format("0x%05X - %s", new Object[] {Integer.valueOf(saveVersion), var1});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level weather", new Callable()
        {
            private static final String __OBFID = "CL_00000595";

            public String call()
            {
                return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", new Object[] {Integer.valueOf(rainTime), Boolean.valueOf(raining), Integer.valueOf(thunderTime), Boolean.valueOf(thundering)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level game mode", new Callable()
        {
            private static final String __OBFID = "CL_00000597";

            public String call()
            {
                return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", new Object[] {theGameType.getName(), Integer.valueOf(theGameType.getID()), Boolean.valueOf(hardcore), Boolean.valueOf(allowCommands)});
            }
        });
    }
}
