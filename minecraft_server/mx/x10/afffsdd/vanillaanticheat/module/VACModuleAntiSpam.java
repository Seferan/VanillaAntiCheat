package mx.x10.afffsdd.vanillaanticheat.module;

public class VACModuleAntiSpam implements IVacModule
{
    private int spamCount;
    private String lastMessage;
    private int cooldownTicks;
    private int spamThresholdCount;
    
    public VACModuleAntiSpam()
    {
        spamCount = 0;
        lastMessage = "";
        cooldownTicks = 0;
        spamThresholdCount = 0;
    }
    
    public String getModuleName()
    {
        return "Anti-Spam";
    }

    public void updateState()
    {
        if (spamThresholdCount > 0) --spamThresholdCount;
        
        if (cooldownTicks > 0) --cooldownTicks;
    }
    
    public boolean messageEqualsLast(String message)
    {
        boolean equals = message.equals(lastMessage);
        lastMessage = message;
        return equals;
    }
    
    public void incrementSpamCount()
    {
        spamCount++;
        if (spamCount == 3 || spamThresholdCount > 100)
        {
            cooldownTicks = 200;
        }
    }
    
    public void resetSpamCount()
    {
        spamCount = 0;
    }
    
    public int getSpamCount()
    {
        return spamCount;
    }
    
    public boolean isInCooldown()
    {
        return cooldownTicks > 0;
    }
}
