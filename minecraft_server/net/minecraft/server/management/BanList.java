package net.minecraft.server.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BanList
{
    private static final Logger logger = LogManager.getLogger();
    private final LowerStringMap theBanList = new LowerStringMap();
    private final File fileName;

    /** set to true if not singlePlayer */
    private boolean listActive = true;
    private static final String __OBFID = "CL_00001396";

    public BanList(File par1File)
    {
        fileName = par1File;
    }

    public boolean isListActive()
    {
        return listActive;
    }

    public void setListActive(boolean par1)
    {
        listActive = par1;
    }

    /**
     * removes expired Bans before returning
     */
    public Map getBannedList()
    {
        removeExpiredBans();
        return theBanList;
    }

    public boolean isBanned(String par1Str)
    {
        if (!isListActive())
        {
            return false;
        }
        else
        {
            removeExpiredBans();
            return theBanList.containsKey(par1Str);
        }
    }

    public void put(BanEntry par1BanEntry)
    {
        theBanList.put(par1BanEntry.getBannedUsername(), par1BanEntry);
        saveToFileWithHeader();
    }

    public void remove(String par1Str)
    {
        theBanList.remove(par1Str);
        saveToFileWithHeader();
    }

    public void removeExpiredBans()
    {
        Iterator var1 = theBanList.values().iterator();

        while (var1.hasNext())
        {
            BanEntry var2 = (BanEntry)var1.next();

            if (var2.hasBanExpired())
            {
                var1.remove();
            }
        }
    }

    /**
     * Loads the ban list from the file (adds every entry, does not clear the
     * current list).
     */
    public void loadBanList()
    {
        if (fileName.isFile())
        {
            BufferedReader var1;

            try
            {
                var1 = new BufferedReader(new FileReader(fileName));
            }
            catch (FileNotFoundException var4)
            {
                throw new Error();
            }

            String var2;

            try
            {
                while ((var2 = var1.readLine()) != null)
                {
                    if (!var2.startsWith("#"))
                    {
                        BanEntry var3 = BanEntry.parse(var2);

                        if (var3 != null)
                        {
                            theBanList.put(var3.getBannedUsername(), var3);
                        }
                    }
                }
            }
            catch (IOException var5)
            {
                logger.error("Could not load ban list", var5);
            }
        }
    }

    public void saveToFileWithHeader()
    {
        saveToFile(true);
    }

    /**
     * par1: include header
     */
    public void saveToFile(boolean par1)
    {
        removeExpiredBans();

        try
        {
            PrintWriter var2 = new PrintWriter(new FileWriter(fileName, false));

            if (par1)
            {
                var2.println("# Updated " + (new SimpleDateFormat()).format(new Date()) + " by Minecraft " + "1.7.2");
                var2.println("# victim name | ban date | banned by | banned until | reason");
                var2.println();
            }

            Iterator var3 = theBanList.values().iterator();

            while (var3.hasNext())
            {
                BanEntry var4 = (BanEntry)var3.next();
                var2.println(var4.buildBanString());
            }

            var2.close();
        }
        catch (IOException var5)
        {
            logger.error("Could not save ban list", var5);
        }
    }
}
