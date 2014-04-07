package net.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;

public class Score
{
    public static final Comparator field_96658_a = new Comparator()
    {
        private static final String __OBFID = "CL_00000618";

        public int compare(Score par1Score, Score par2Score)
        {
            return par1Score.getScorePoints() > par2Score.getScorePoints() ? 1 : (par1Score.getScorePoints() < par2Score.getScorePoints() ? -1 : 0);
        }

        public int compare(Object par1Obj, Object par2Obj)
        {
            return this.compare((Score)par1Obj, (Score)par2Obj);
        }
    };
    private final Scoreboard theScoreboard;
    private final ScoreObjective theScoreObjective;
    private final String scorePlayerName;
    private int field_96655_e;
    private static final String __OBFID = "CL_00000617";

    public Score(Scoreboard par1Scoreboard, ScoreObjective par2ScoreObjective, String par3Str)
    {
        theScoreboard = par1Scoreboard;
        theScoreObjective = par2ScoreObjective;
        scorePlayerName = par3Str;
    }

    public void increseScore(int par1)
    {
        if (theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            setScorePoints(getScorePoints() + par1);
        }
    }

    public void decreaseScore(int par1)
    {
        if (theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            setScorePoints(getScorePoints() - par1);
        }
    }

    public void func_96648_a()
    {
        if (theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            increseScore(1);
        }
    }

    public int getScorePoints()
    {
        return field_96655_e;
    }

    public void setScorePoints(int par1)
    {
        int var2 = field_96655_e;
        field_96655_e = par1;

        if (var2 != par1)
        {
            getScoreScoreboard().func_96536_a(this);
        }
    }

    public ScoreObjective func_96645_d()
    {
        return theScoreObjective;
    }

    /**
     * Returns the name of the player this score belongs to
     */
    public String getPlayerName()
    {
        return scorePlayerName;
    }

    public Scoreboard getScoreScoreboard()
    {
        return theScoreboard;
    }

    public void func_96651_a(List par1List)
    {
        setScorePoints(theScoreObjective.getCriteria().func_96635_a(par1List));
    }
}
