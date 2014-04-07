package net.minecraft.scoreboard;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ScorePlayerTeam extends Team
{
    private final Scoreboard theScoreboard;
    private final String field_96675_b;

    /** A set of all team member usernames. */
    private final Set membershipSet = new HashSet();
    private String teamNameSPT;
    private String namePrefixSPT = "";
    private String colorSuffix = "";
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = true;
    private static final String __OBFID = "CL_00000616";

    public ScorePlayerTeam(Scoreboard par1Scoreboard, String par2Str)
    {
        theScoreboard = par1Scoreboard;
        field_96675_b = par2Str;
        teamNameSPT = par2Str;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard
     */
    public String getRegisteredName()
    {
        return field_96675_b;
    }

    public String func_96669_c()
    {
        return teamNameSPT;
    }

    public void setTeamName(String par1Str)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Name cannot be null");
        }
        else
        {
            teamNameSPT = par1Str;
            theScoreboard.broadcastTeamRemoved(this);
        }
    }

    public Collection getMembershipCollection()
    {
        return membershipSet;
    }

    /**
     * Returns the color prefix for the player's team name
     */
    public String getColorPrefix()
    {
        return namePrefixSPT;
    }

    public void setNamePrefix(String par1Str)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        else
        {
            namePrefixSPT = par1Str;
            theScoreboard.broadcastTeamRemoved(this);
        }
    }

    /**
     * Returns the color suffix for the player's team name
     */
    public String getColorSuffix()
    {
        return colorSuffix;
    }

    public void setNameSuffix(String par1Str)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Suffix cannot be null");
        }
        else
        {
            colorSuffix = par1Str;
            theScoreboard.broadcastTeamRemoved(this);
        }
    }

    public String formatString(String par1Str)
    {
        return getColorPrefix() + par1Str + getColorSuffix();
    }

    /**
     * Returns the player name including the color prefixes and suffixes
     */
    public static String formatPlayerName(Team par0Team, String par1Str)
    {
        return par0Team == null ? par1Str : par0Team.formatString(par1Str);
    }

    public boolean getAllowFriendlyFire()
    {
        return allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean par1)
    {
        allowFriendlyFire = par1;
        theScoreboard.broadcastTeamRemoved(this);
    }

    public boolean func_98297_h()
    {
        return canSeeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisiblesEnabled(boolean par1)
    {
        canSeeFriendlyInvisibles = par1;
        theScoreboard.broadcastTeamRemoved(this);
    }

    public int func_98299_i()
    {
        int var1 = 0;

        if (getAllowFriendlyFire())
        {
            var1 |= 1;
        }

        if (func_98297_h())
        {
            var1 |= 2;
        }

        return var1;
    }
}
