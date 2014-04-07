package net.minecraft.scoreboard;

public class ScoreObjective
{
    private final Scoreboard theScoreboard;
    private final String name;

    /** The ScoreObjectiveCriteria for this objetive */
    private final IScoreObjectiveCriteria objectiveCriteria;
    private String displayName;
    private static final String __OBFID = "CL_00000614";

    public ScoreObjective(Scoreboard par1Scoreboard, String par2Str, IScoreObjectiveCriteria par3ScoreObjectiveCriteria)
    {
        theScoreboard = par1Scoreboard;
        name = par2Str;
        objectiveCriteria = par3ScoreObjectiveCriteria;
        displayName = par2Str;
    }

    public String getName()
    {
        return name;
    }

    public IScoreObjectiveCriteria getCriteria()
    {
        return objectiveCriteria;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String par1Str)
    {
        displayName = par1Str;
        theScoreboard.func_96532_b(this);
    }
}
