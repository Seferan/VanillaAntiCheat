package net.minecraft.server.management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BanEntry
{
    private static final Logger logger = LogManager.getLogger();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final String username;
    private Date banStartDate = new Date();
    private String bannedBy = "(Unknown)";
    private Date banEndDate;
    private String reason = "Banned by an operator.";
    private static final String __OBFID = "CL_00001395";

    public BanEntry(String par1Str)
    {
        username = par1Str;
    }

    public String getBannedUsername()
    {
        return username;
    }

    public Date getBanStartDate()
    {
        return banStartDate;
    }

    /**
     * null == start ban now
     */
    public void setBanStartDate(Date par1Date)
    {
        banStartDate = par1Date != null ? par1Date : new Date();
    }

    public String getBannedBy()
    {
        return bannedBy;
    }

    public void setBannedBy(String par1Str)
    {
        bannedBy = par1Str;
    }

    public Date getBanEndDate()
    {
        return banEndDate;
    }

    public void setBanEndDate(Date par1Date)
    {
        banEndDate = par1Date;
    }

    public boolean hasBanExpired()
    {
        return banEndDate == null ? false : banEndDate.before(new Date());
    }

    public String getBanReason()
    {
        return reason;
    }

    public void setBanReason(String par1Str)
    {
        reason = par1Str;
    }

    public String buildBanString()
    {
        StringBuilder var1 = new StringBuilder();
        var1.append(getBannedUsername());
        var1.append("|");
        var1.append(dateFormat.format(getBanStartDate()));
        var1.append("|");
        var1.append(getBannedBy());
        var1.append("|");
        var1.append(getBanEndDate() == null ? "Forever" : dateFormat.format(getBanEndDate()));
        var1.append("|");
        var1.append(getBanReason());
        return var1.toString();
    }

    public static BanEntry parse(String par0Str)
    {
        if (par0Str.trim().length() < 2)
        {
            return null;
        }
        else
        {
            String[] var1 = par0Str.trim().split(Pattern.quote("|"), 5);
            BanEntry var2 = new BanEntry(var1[0].trim());
            byte var3 = 0;
            int var10000 = var1.length;
            int var7 = var3 + 1;

            if (var10000 <= var7)
            {
                return var2;
            }
            else
            {
                try
                {
                    var2.setBanStartDate(dateFormat.parse(var1[var7].trim()));
                }
                catch (ParseException var6)
                {
                    logger.warn("Could not read creation date format for ban entry \'" + var2.getBannedUsername() + "\' (was: \'" + var1[var7] + "\')", var6);
                }

                var10000 = var1.length;
                ++var7;

                if (var10000 <= var7)
                {
                    return var2;
                }
                else
                {
                    var2.setBannedBy(var1[var7].trim());
                    var10000 = var1.length;
                    ++var7;

                    if (var10000 <= var7)
                    {
                        return var2;
                    }
                    else
                    {
                        try
                        {
                            String var4 = var1[var7].trim();

                            if (!var4.equalsIgnoreCase("Forever") && var4.length() > 0)
                            {
                                var2.setBanEndDate(dateFormat.parse(var4));
                            }
                        }
                        catch (ParseException var5)
                        {
                            logger.warn("Could not read expiry date format for ban entry \'" + var2.getBannedUsername() + "\' (was: \'" + var1[var7] + "\')", var5);
                        }

                        var10000 = var1.length;
                        ++var7;

                        if (var10000 <= var7)
                        {
                            return var2;
                        }
                        else
                        {
                            var2.setBanReason(var1[var7].trim());
                            return var2;
                        }
                    }
                }
            }
        }
    }
}
