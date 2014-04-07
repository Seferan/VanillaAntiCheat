package net.minecraft.profiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler
{
    private static final Logger logger = LogManager.getLogger();

    /** List of parent sections */
    private final List sectionList = new ArrayList();

    /** List of timestamps (System.nanoTime) */
    private final List timestampList = new ArrayList();

    /** Flag profiling enabled */
    public boolean profilingEnabled;

    /** Current profiling section */
    private String profilingSection = "";

    /** Profiling map */
    private final Map profilingMap = new HashMap();
    private static final String __OBFID = "CL_00001497";

    /**
     * Clear profiling.
     */
    public void clearProfiling()
    {
        profilingMap.clear();
        profilingSection = "";
        sectionList.clear();
    }

    /**
     * Start section
     */
    public void startSection(String par1Str)
    {
        if (profilingEnabled)
        {
            if (profilingSection.length() > 0)
            {
                profilingSection = profilingSection + ".";
            }

            profilingSection = profilingSection + par1Str;
            sectionList.add(profilingSection);
            timestampList.add(Long.valueOf(System.nanoTime()));
        }
    }

    /**
     * End section
     */
    public void endSection()
    {
        if (profilingEnabled)
        {
            long var1 = System.nanoTime();
            long var3 = ((Long)timestampList.remove(timestampList.size() - 1)).longValue();
            sectionList.remove(sectionList.size() - 1);
            long var5 = var1 - var3;

            if (profilingMap.containsKey(profilingSection))
            {
                profilingMap.put(profilingSection, Long.valueOf(((Long)profilingMap.get(profilingSection)).longValue() + var5));
            }
            else
            {
                profilingMap.put(profilingSection, Long.valueOf(var5));
            }

            if (var5 > 100000000L)
            {
                logger.warn("Something\'s taking too long! \'" + profilingSection + "\' took aprox " + var5 / 1000000.0D + " ms");
            }

            profilingSection = !sectionList.isEmpty() ? (String)sectionList.get(sectionList.size() - 1) : "";
        }
    }

    /**
     * Get profiling data
     */
    public List getProfilingData(String par1Str)
    {
        if (!profilingEnabled)
        {
            return null;
        }
        else
        {
            long var3 = profilingMap.containsKey("root") ? ((Long)profilingMap.get("root")).longValue() : 0L;
            long var5 = profilingMap.containsKey(par1Str) ? ((Long)profilingMap.get(par1Str)).longValue() : -1L;
            ArrayList var7 = new ArrayList();

            if (par1Str.length() > 0)
            {
                par1Str = par1Str + ".";
            }

            long var8 = 0L;
            Iterator var10 = profilingMap.keySet().iterator();

            while (var10.hasNext())
            {
                String var11 = (String)var10.next();

                if (var11.length() > par1Str.length() && var11.startsWith(par1Str) && var11.indexOf(".", par1Str.length() + 1) < 0)
                {
                    var8 += ((Long)profilingMap.get(var11)).longValue();
                }
            }

            float var21 = var8;

            if (var8 < var5)
            {
                var8 = var5;
            }

            if (var3 < var8)
            {
                var3 = var8;
            }

            Iterator var20 = profilingMap.keySet().iterator();
            String var12;

            while (var20.hasNext())
            {
                var12 = (String)var20.next();

                if (var12.length() > par1Str.length() && var12.startsWith(par1Str) && var12.indexOf(".", par1Str.length() + 1) < 0)
                {
                    long var13 = ((Long)profilingMap.get(var12)).longValue();
                    double var15 = var13 * 100.0D / var8;
                    double var17 = var13 * 100.0D / var3;
                    String var19 = var12.substring(par1Str.length());
                    var7.add(new Profiler.Result(var19, var15, var17));
                }
            }

            var20 = profilingMap.keySet().iterator();

            while (var20.hasNext())
            {
                var12 = (String)var20.next();
                profilingMap.put(var12, Long.valueOf(((Long)profilingMap.get(var12)).longValue() * 999L / 1000L));
            }

            if (var8 > var21)
            {
                var7.add(new Profiler.Result("unspecified", (var8 - var21) * 100.0D / var8, (var8 - var21) * 100.0D / var3));
            }

            Collections.sort(var7);
            var7.add(0, new Profiler.Result(par1Str, 100.0D, var8 * 100.0D / var3));
            return var7;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String par1Str)
    {
        endSection();
        startSection(par1Str);
    }

    public String getNameOfLastSection()
    {
        return sectionList.size() == 0 ? "[UNKNOWN]" : (String)sectionList.get(sectionList.size() - 1);
    }

    public static final class Result implements Comparable
    {
        public double field_76332_a;
        public double field_76330_b;
        public String field_76331_c;
        private static final String __OBFID = "CL_00001498";

        public Result(String par1Str, double par2, double par4)
        {
            field_76331_c = par1Str;
            field_76332_a = par2;
            field_76330_b = par4;
        }

        public int compareTo(Profiler.Result par1ProfilerResult)
        {
            return par1ProfilerResult.field_76332_a < field_76332_a ? -1 : (par1ProfilerResult.field_76332_a > field_76332_a ? 1 : par1ProfilerResult.field_76331_c.compareTo(field_76331_c));
        }

        public int compareTo(Object par1Obj)
        {
            return this.compareTo((Profiler.Result)par1Obj);
        }
    }
}
