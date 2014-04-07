package net.minecraft.profiler;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper
{
    /** String map for report data */
    private Map dataMap = new HashMap();
    private final String uniqueID = UUID.randomUUID().toString();

    /** URL of the server to send the report to */
    private final URL serverUrl;
    private final IPlayerUsage playerStatsCollector;

    /** set to fire the snooperThread every 15 mins */
    private final Timer threadTrigger = new Timer("Snooper Timer", true);
    private final Object syncLock = new Object();
    private final long minecraftStartTimeMilis;
    private boolean isRunning;

    /** incremented on every getSelfCounterFor */
    private int selfCounter;
    private static final String __OBFID = "CL_00001515";

    public PlayerUsageSnooper(String par1Str, IPlayerUsage par2IPlayerUsage, long par3)
    {
        try
        {
            serverUrl = new URL("http://snoop.minecraft.net/" + par1Str + "?version=" + 1);
        }
        catch (MalformedURLException var6)
        {
            throw new IllegalArgumentException();
        }

        playerStatsCollector = par2IPlayerUsage;
        minecraftStartTimeMilis = par3;
    }

    /**
     * Note issuing start multiple times is not an error.
     */
    public void startSnooper()
    {
        if (!isRunning)
        {
            isRunning = true;
            addBaseDataToSnooper();
            threadTrigger.schedule(new TimerTask()
            {
                private static final String __OBFID = "CL_00001516";

                public void run()
                {
                    if (playerStatsCollector.isSnooperEnabled())
                    {
                        HashMap var1;

                        synchronized (syncLock)
                        {
                            var1 = new HashMap(dataMap);
                            var1.put("snooper_count", Integer.valueOf(PlayerUsageSnooper.getSelfCounterFor(PlayerUsageSnooper.this)));
                        }

                        HttpUtil.func_151226_a(serverUrl, var1, true);
                    }
                }
            }, 0L, 900000L);
        }
    }

    private void addBaseDataToSnooper()
    {
        addJvmArgsToSnooper();
        addData("snooper_token", uniqueID);
        addData("os_name", System.getProperty("os.name"));
        addData("os_version", System.getProperty("os.version"));
        addData("os_architecture", System.getProperty("os.arch"));
        addData("java_version", System.getProperty("java.version"));
        addData("version", "1.7.2");
        playerStatsCollector.addServerTypeToSnooper(this);
    }

    private void addJvmArgsToSnooper()
    {
        RuntimeMXBean var1 = ManagementFactory.getRuntimeMXBean();
        List var2 = var1.getInputArguments();
        int var3 = 0;
        Iterator var4 = var2.iterator();

        while (var4.hasNext())
        {
            String var5 = (String)var4.next();

            if (var5.startsWith("-X"))
            {
                addData("jvm_arg[" + var3++ + "]", var5);
            }
        }

        addData("jvm_args", Integer.valueOf(var3));
    }

    public void addMemoryStatsToSnooper()
    {
        addData("memory_total", Long.valueOf(Runtime.getRuntime().totalMemory()));
        addData("memory_max", Long.valueOf(Runtime.getRuntime().maxMemory()));
        addData("memory_free", Long.valueOf(Runtime.getRuntime().freeMemory()));
        addData("cpu_cores", Integer.valueOf(Runtime.getRuntime().availableProcessors()));
        playerStatsCollector.addServerStatsToSnooper(this);
    }

    /**
     * Adds information to the report
     */
    public void addData(String par1Str, Object par2Obj)
    {
        Object var3 = syncLock;

        synchronized (syncLock)
        {
            dataMap.put(par1Str, par2Obj);
        }
    }

    public boolean isSnooperRunning()
    {
        return isRunning;
    }

    public void stopSnooper()
    {
        threadTrigger.cancel();
    }

    /**
     * Returns the saved value of System#currentTimeMillis when the game started
     */
    public long getMinecraftStartTimeMillis()
    {
        return minecraftStartTimeMilis;
    }

    /**
     * returns a value indicating how many times this function has been run on
     * the snooper
     */
    static int getSelfCounterFor(PlayerUsageSnooper par0PlayerUsageSnooper)
    {
        return par0PlayerUsageSnooper.selfCounter++;
    }
}
