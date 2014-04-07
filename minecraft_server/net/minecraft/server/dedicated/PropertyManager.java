package net.minecraft.server.dedicated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyManager
{
    private static final Logger field_164440_a = LogManager.getLogger();

    /** The server properties object. */
    private final Properties serverProperties = new Properties();

    /** The server properties file. */
    private final File serverPropertiesFile;
    private static final String __OBFID = "CL_00001782";

    public PropertyManager(File p_i45278_1_)
    {
        serverPropertiesFile = p_i45278_1_;

        if (p_i45278_1_.exists())
        {
            FileInputStream var2 = null;

            try
            {
                var2 = new FileInputStream(p_i45278_1_);
                serverProperties.load(var2);
            }
            catch (Exception var12)
            {
                field_164440_a.warn("Failed to load " + p_i45278_1_, var12);
                generateNewProperties();
            }
            finally
            {
                if (var2 != null)
                {
                    try
                    {
                        var2.close();
                    }
                    catch (IOException var11)
                    {
                        ;
                    }
                }
            }
        }
        else
        {
            field_164440_a.warn(p_i45278_1_ + " does not exist");
            generateNewProperties();
        }
    }

    /**
     * Generates a new properties file.
     */
    public void generateNewProperties()
    {
        field_164440_a.info("Generating new properties file");
        saveProperties();
    }

    /**
     * Writes the properties to the properties file.
     */
    public void saveProperties()
    {
        FileOutputStream var1 = null;

        try
        {
            Properties sortedProperties = new Properties()
            {
                public synchronized Enumeration<Object> keys()
                {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            sortedProperties.putAll(serverProperties);
            var1 = new FileOutputStream(serverPropertiesFile);
            sortedProperties.store(var1, "Minecraft server properties");
        }
        catch (Exception var11)
        {
            field_164440_a.warn("Failed to save " + serverPropertiesFile, var11);
            generateNewProperties();
        }
        finally
        {
            if (var1 != null)
            {
                try
                {
                    var1.close();
                }
                catch (IOException var10)
                {
                    ;
                }
            }
        }
    }

    /**
     * Returns this PropertyManager's file object used for property saving.
     */
    public File getPropertiesFile()
    {
        return serverPropertiesFile;
    }

    /**
     * Returns a string property. If the property doesn't exist the default is
     * returned.
     */
    public String getStringProperty(String par1Str, String par2Str)
    {
        if (!serverProperties.containsKey(par1Str))
        {
            serverProperties.setProperty(par1Str, par2Str);
            saveProperties();
            saveProperties();
        }

        return serverProperties.getProperty(par1Str, par2Str);
    }

    /**
     * Gets an integer property. If it does not exist, set it to the specified
     * value.
     */
    public int getIntProperty(String par1Str, int par2)
    {
        try
        {
            return Integer.parseInt(getStringProperty(par1Str, "" + par2));
        }
        catch (Exception var4)
        {
            serverProperties.setProperty(par1Str, "" + par2);
            saveProperties();
            return par2;
        }
    }

    /**
     * Gets an double property. If it does not exist, set it to the specified
     * value.
     */
    public double getDoubleProperty(String propertyName, double defaultValue)
    {
        try
        {
            return Double.parseDouble(getStringProperty(propertyName, "" + defaultValue));
        }
        catch (Exception ex)
        {
            serverProperties.setProperty(propertyName, "" + defaultValue);
            saveProperties();
            return defaultValue;
        }
    }

    /**
     * Gets a boolean property. If it does not exist, set it to the specified
     * value.
     */
    public boolean getBooleanProperty(String par1Str, boolean par2)
    {
        try
        {
            return Boolean.parseBoolean(getStringProperty(par1Str, "" + par2));
        }
        catch (Exception var4)
        {
            serverProperties.setProperty(par1Str, "" + par2);
            saveProperties();
            return par2;
        }
    }

    /**
     * Saves an Object with the given property name.
     */
    public void setProperty(String par1Str, Object par2Obj)
    {
        serverProperties.setProperty(par1Str, "" + par2Obj);
    }
}
