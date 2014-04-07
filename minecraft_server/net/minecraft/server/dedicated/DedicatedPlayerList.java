package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.MinecraftServer;
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
        this.opsList = par1DedicatedServer.getFile("ops.txt");
        this.ownersList = par1DedicatedServer.getFile("owners.txt");
        this.whiteList = par1DedicatedServer.getFile("white-list.txt");
        motdFile = par1DedicatedServer.getFile("motd.txt");
        this.viewDistance = par1DedicatedServer.getIntProperty("view-distance", 10);
        this.maxPlayers = par1DedicatedServer.getIntProperty("max-players", 20);
        this.setWhiteListEnabled(par1DedicatedServer.getBooleanProperty("white-list", false));

        if (!par1DedicatedServer.isSinglePlayer())
        {
            this.getBannedPlayers().setListActive(true);
            this.getBannedIPs().setListActive(true);
        }

        this.getBannedPlayers().loadBanList();
        this.getBannedPlayers().saveToFileWithHeader();
        this.getBannedIPs().loadBanList();
        this.getBannedIPs().saveToFileWithHeader();
        this.loadOpsList();
        this.loadOwnersList();
        this.readWhiteList();
        this.readMotd();
        this.saveOpsList();

        if (!this.whiteList.exists())
        {
            this.saveWhiteList();
        }
        if (!this.ownersList.exists())
        {
            this.saveOwnersList();
        }
    }

    public void setWhiteListEnabled(boolean par1)
    {
        super.setWhiteListEnabled(par1);
        this.getServerInstance().setProperty("white-list", Boolean.valueOf(par1));
        this.getServerInstance().saveProperties();
    }

    /**
     * This adds a username to the ops list, then saves the op list
     */
    public void addOp(String par1Str)
    {
        super.addOp(par1Str);
        this.saveOpsList();
    }

    /**
     * This removes a username from the ops list, then saves the op list
     */
    public void removeOp(String par1Str)
    {
        super.removeOp(par1Str);
        this.saveOpsList();
    }

    public void addOwner(String par1Str)
    {
        super.addOwner(par1Str);
        this.saveOwnersList();
    }

    public void removeOwner(String par1Str)
    {
        super.removeOwner(par1Str);
        this.saveOwnersList();
    }

    /**
     * Remove the specified player from the whitelist.
     */
    public void removeFromWhitelist(String par1Str)
    {
        super.removeFromWhitelist(par1Str);
        this.saveWhiteList();
    }

    /**
     * Add the specified player to the white list.
     */
    public void addToWhiteList(String par1Str)
    {
        super.addToWhiteList(par1Str);
        this.saveWhiteList();
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void loadWhiteList()
    {
        this.readWhiteList();
    }

    private void loadOpsList()
    {
        try
        {
            this.getOps().clear();
            BufferedReader var1 = new BufferedReader(new FileReader(this.opsList));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                this.getOps().add(var2.trim().toLowerCase());
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
            PrintWriter var1 = new PrintWriter(new FileWriter(this.opsList, false));
            Iterator var2 = this.getOps().iterator();

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
            this.getOwners().clear();
            BufferedReader var1 = new BufferedReader(new FileReader(this.ownersList));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                this.getOwners().add(var2.trim().toLowerCase());
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
            PrintWriter var1 = new PrintWriter(new FileWriter(this.ownersList, false));
            Iterator var2 = this.getOwners().iterator();

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
            this.getWhiteListedPlayers().clear();
            BufferedReader var1 = new BufferedReader(new FileReader(this.whiteList));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                this.getWhiteListedPlayers().add(var2.trim().toLowerCase());
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
            PrintWriter var1 = new PrintWriter(new FileWriter(this.whiteList, false));
            Iterator var2 = this.getWhiteListedPlayers().iterator();

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
        return !this.isWhiteListEnabled() || this.isPlayerOpped(par1Str) || this.getWhiteListedPlayers().contains(par1Str);
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
            PrintWriter writer = new PrintWriter(new FileWriter(this.motdFile, false));
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
