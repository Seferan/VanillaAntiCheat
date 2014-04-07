package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.management.ServerConfigurationManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerList extends ServerConfigurationManager
{
    private static final Logger field_164439_d = LogManager.getLogger();
    private File opsList;
    private File ownersList;
    private File whiteList;
    private static final String __OBFID = "CL_00001783";

    /**
     * The long MOTD message that is given by /motd.
     */
    private String[] longMotd;
    private File motdFile;

    public DedicatedPlayerList(DedicatedServer par1DedicatedServer)
    {
        super(par1DedicatedServer);
        opsList = par1DedicatedServer.getFile("ops.txt");
        ownersList = par1DedicatedServer.getFile("owners.txt");
        whiteList = par1DedicatedServer.getFile("white-list.txt");
        motdFile = par1DedicatedServer.getFile("motd.txt");
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
        saveOpsList();

        if (!whiteList.exists())
        {
            saveWhiteList();
        }
        if (!ownersList.exists())
        {
            saveOwnersList();
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

    private void loadOpsList()
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
            field_164439_d.warn("Failed to load operators list: " + var3);
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
            field_164439_d.warn("Failed to save operators list: " + var4);
        }
    }

    private void loadOwnersList()
    {
        try
        {
            getOwners().clear();
            BufferedReader var1 = new BufferedReader(new FileReader(ownersList));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                getOwners().add(var2.trim().toLowerCase());
                System.out.println("added owner " + var2.trim().toLowerCase());
            }

            var1.close();
        }
        catch (Exception var3)
        {
            field_164439_d.warn("Failed to load owners list: " + var3);
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
            field_164439_d.warn("Failed to save owners list: " + var4);
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
            field_164439_d.warn("Failed to load white-list: " + var3);
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
            field_164439_d.warn("Failed to save white-list: " + var4);
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
            field_164439_d.warn("Failed to read motd: " + e);
            longMotd = new String[0];
            createMotdFile();
        }
    }

    private void createMotdFile()
    {
        try
        {
            PrintWriter writer = new PrintWriter(new FileWriter(motdFile, false));
            writer.close();
        }
        catch (Exception e)
        {
            field_164439_d.warn("Failed to create motd file: " + e);
        }
    }

    public String[] getMotd()
    {
        return longMotd;
    }
}
