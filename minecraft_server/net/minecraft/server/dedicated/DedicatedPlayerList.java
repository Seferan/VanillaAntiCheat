package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.hash.BloomFilter;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.management.ServerConfigurationManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerList extends ServerConfigurationManager
{
    private static final Logger logger = LogManager.getLogger();
    private File opsList;
    private File ownersList;
    private File whiteList;
    private File proxyCheckCacheFile;
    private File motdFile;
    private File blockHistoryLog;
    private static final String __OBFID = "CL_00001783";

    public DedicatedPlayerList(DedicatedServer par1DedicatedServer)
    {
        super(par1DedicatedServer);
        opsList = par1DedicatedServer.getFile("ops.txt");
        ownersList = par1DedicatedServer.getFile("owners.txt");
        whiteList = par1DedicatedServer.getFile("white-list.txt");
        motdFile = par1DedicatedServer.getFile("motd.txt");
        blockHistoryLog  = par1DedicatedServer.getFile("block-history.txt");
        proxyCheckCacheFile = par1DedicatedServer.getFile("proxy-check-cache.txt");
        viewDistance = par1DedicatedServer.getIntProperty("view-distance", 10);
        maxPlayers = par1DedicatedServer.getIntProperty("max-players", 20);
        setWhiteListEnabled(par1DedicatedServer.getBooleanProperty("white-list", false));

        if (!par1DedicatedServer.isSinglePlayer())
        {
            getBannedPlayers().setListActive(true);
            getBannedIPs().setListActive(true);
        }

        getBannedPlayers().loadBanList();
        getBannedPlayers().saveToFileWithHeader();
        getBannedIPs().loadBanList();
        getBannedIPs().saveToFileWithHeader();
        loadOpsList();
        loadOwnersList();
        readWhiteList();
        readMotd();
        loadProxyCache();
        saveOpsList();

        if (!whiteList.exists())
        {
            saveWhiteList();
        }
        if (!ownersList.exists())
        {
            saveOwnersList();
        }
        if (!blockHistoryLog.exists())
        {
            createBlockHistoryLog();
        }
    }

    public void setWhiteListEnabled(boolean par1)
    {
        super.setWhiteListEnabled(par1);
        getServerInstance().setProperty("white-list", Boolean.valueOf(par1));
        getServerInstance().saveProperties();
    }

    /**
     * This adds a username to the ops list, then saves the op list
     */
    public void addOp(String par1Str)
    {
        super.addOp(par1Str);
        saveOpsList();
    }

    /**
     * This removes a username from the ops list, then saves the op list
     */
    public void removeOp(String par1Str)
    {
        super.removeOp(par1Str);
        saveOpsList();
    }

    public void addOwner(String par1Str)
    {
        super.addOwner(par1Str);
        saveOwnersList();
    }

    public void removeOwner(String par1Str)
    {
        super.removeOwner(par1Str);
        saveOwnersList();
    }

    /**
     * Remove the specified player from the whitelist.
     */
    public void removeFromWhitelist(String par1Str)
    {
        super.removeFromWhitelist(par1Str);
        saveWhiteList();
    }

    /**
     * Add the specified player to the white list.
     */
    public void addToWhiteList(String par1Str)
    {
        super.addToWhiteList(par1Str);
        saveWhiteList();
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void loadWhiteList()
    {
        readWhiteList();
    }

    public void loadOpsList()
    {
        try
        {
            getOps().clear();
            BufferedReader var1 = new BufferedReader(new FileReader(opsList));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                getOps().add(var2.trim().toLowerCase());
            }

            var1.close();
        }
        catch (Exception var3)
        {
            logger.warn("Failed to load operators list: " + var3);
        }
    }

    private void saveOpsList()
    {
        try
        {
            PrintWriter var1 = new PrintWriter(new FileWriter(opsList, false));
            Iterator var2 = getOps().iterator();

            while (var2.hasNext())
            {
                String var3 = (String)var2.next();
                var1.println(var3);
            }

            var1.close();
        }
        catch (Exception var4)
        {
            logger.warn("Failed to save operators list: " + var4);
        }
    }

    public void loadOwnersList()
    {
        try
        {
            getOwners().clear();
            BufferedReader var1 = new BufferedReader(new FileReader(ownersList));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                getOwners().add(var2.trim().toLowerCase());
            }

            var1.close();
        }
        catch (Exception var3)
        {
            logger.warn("Failed to load owners list: " + var3);
        }
    }

    private void saveOwnersList()
    {
        try
        {
            PrintWriter var1 = new PrintWriter(new FileWriter(ownersList, false));
            Iterator var2 = getOwners().iterator();

            while (var2.hasNext())
            {
                String var3 = (String)var2.next();
                var1.println(var3);
            }

            var1.close();
        }
        catch (Exception var4)
        {
            logger.warn("Failed to save owners list: " + var4);
        }
    }

    private void readWhiteList()
    {
        try
        {
            getWhiteListedPlayers().clear();
            BufferedReader var1 = new BufferedReader(new FileReader(whiteList));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                getWhiteListedPlayers().add(var2.trim().toLowerCase());
            }

            var1.close();
        }
        catch (Exception var3)
        {
            logger.warn("Failed to load white-list: " + var3);
        }
    }

    private void saveWhiteList()
    {
        try
        {
            PrintWriter var1 = new PrintWriter(new FileWriter(whiteList, false));
            Iterator var2 = getWhiteListedPlayers().iterator();

            while (var2.hasNext())
            {
                String var3 = (String)var2.next();
                var1.println(var3);
            }

            var1.close();
        }
        catch (Exception var4)
        {
            logger.warn("Failed to save white-list: " + var4);
        }
    }
    
    /**
     * Append a line to the block history file.
     * 
     * @param state
     *            true if the block was placed, false if it was broken
     */
    public void addBlockHistory(Block block, EntityPlayerMP player, boolean state, int x, int y, int z)
    {
        super.addBlockHistory(block, player, state, x, y, z);
        addBlockHistory(Item.getItemFromBlock(block), player, state, x, y, z);
    }
    
    public void addBlockHistory(Item item, EntityPlayerMP player, boolean state, int x, int y, int z)
    {
        super.addBlockHistory(item, player, state, x, y, z);
        PrintWriter blockHistoryWriter;
        try
        {
            blockHistoryWriter = new PrintWriter(new BufferedWriter(new FileWriter(blockHistoryLog, true)));
            StringBuilder line = new StringBuilder();
            line.append(player.getUsername());
            line.append(" ");
            line.append(state ? "placed" : "broke");
            line.append(" ");
            line.append(item.getUnlocalizedName());
            line.append(" ");
            line.append(x);
            line.append(" ");
            line.append(y);
            line.append(" ");
            line.append(z);
            blockHistoryWriter.println(line.toString());
            blockHistoryWriter.close();
        }
        catch (Exception e)
        {
            logger.warn("Failed to add block history!", e);
            logger.warn("Player: " + player.getUsername() + " Item: " + item.getUnlocalizedName() + " State: " + state);
        }
    }
    
    private void createBlockHistoryLog()
    {
        try
        {
            blockHistoryLog.createNewFile();
        }
        catch (IOException e)
        {
            logger.warn("Failed to create block history log: " + e);
        }
    }

    /**
     * Determine if the player is allowed to connect based on current server
     * settings.
     */
    public boolean isAllowedToLogin(String par1Str)
    {
        par1Str = par1Str.trim().toLowerCase();
        return !isWhiteListEnabled() || isPlayerOpped(par1Str) || getWhiteListedPlayers().contains(par1Str);
    }

    public DedicatedServer getServerInstance()
    {
        return (DedicatedServer)super.getServerInstance();
    }

    public void readMotd()
    {
        try
        {
            List<String> lines = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(motdFile));
            String line = "";

            while ((line = reader.readLine()) != null)
            {
                lines.add(line.trim());
            }

            reader.close();

            longMotd = lines.toArray(new String[lines.size()]);
        }
        catch (Exception e)
        {
            logger.warn("Failed to read motd: " + e);
            longMotd = new String[0];
            createMotdFile();
        }
    }
    
    public void loadProxyCache()
    {        
        try
        {
            proxyCheckCache = new HashMap<String, Boolean>();
            BufferedReader reader = new BufferedReader(new FileReader(proxyCheckCacheFile));
            String line = "";

            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split("|");
                String ip = parts[0];
                boolean proxy = Boolean.parseBoolean(parts[1]);
                proxyCheckCache.put(ip, proxy);
            }

            reader.close();

        }
        catch (Exception e)
        {
            logger.warn("Failed to load proxy check cache: " + e);
            proxyCheckCache = new HashMap<String, Boolean>();
            createProxyCheckCache();
        }
    }

    private void saveProxyCache()
    {
        try
        {
            PrintWriter writer = new PrintWriter(new FileWriter(proxyCheckCacheFile, false));
            Iterator it = proxyCheckCache.entrySet().iterator();
            
            while (it.hasNext())
            {
                Entry pair = (Entry)it.next();
                writer.println(pair.getKey() + "|" + pair.getValue());
            }
            
            writer.close();
        }
        catch (Exception e)
        {
            logger.warn("Failed to save proxy check cache: " + e);
        }
    }

    private void createMotdFile()
    {
        try
        {
            motdFile.createNewFile();
        }
        catch (IOException e)
        {
            logger.warn("Failed to create motd file: " + e);
        }
    }
    
    private void createProxyCheckCache()
    {
        try
        {
            proxyCheckCacheFile.createNewFile();
        }
        catch (Exception e)
        {
            logger.warn("Failed to create proxy check cache: " + e);
        }
    }
    
    public void addIpToProxyCache(String ip, boolean proxy)
    {
        super.addIpToProxyCache(ip, proxy);
        saveProxyCache();
    }
}
