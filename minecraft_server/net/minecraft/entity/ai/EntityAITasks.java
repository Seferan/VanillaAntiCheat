package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.profiler.Profiler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks
{
    private static final Logger logger = LogManager.getLogger();

    /** A list of EntityAITaskEntrys in EntityAITasks. */
    private List taskEntries = new ArrayList();

    /** A list of EntityAITaskEntrys that are currently being executed. */
    private List executingTaskEntries = new ArrayList();

    /** Instance of Profiler. */
    private final Profiler theProfiler;
    private int tickCount;
    private int tickRate = 3;
    private static final String __OBFID = "CL_00001588";

    public EntityAITasks(Profiler par1Profiler)
    {
        theProfiler = par1Profiler;
    }

    public void addTask(int par1, EntityAIBase par2EntityAIBase)
    {
        taskEntries.add(new EntityAITasks.EntityAITaskEntry(par1, par2EntityAIBase));
    }

    /**
     * removes the indicated task from the entity's AI tasks.
     */
    public void removeTask(EntityAIBase par1EntityAIBase)
    {
        Iterator var2 = taskEntries.iterator();

        while (var2.hasNext())
        {
            EntityAITasks.EntityAITaskEntry var3 = (EntityAITasks.EntityAITaskEntry)var2.next();
            EntityAIBase var4 = var3.action;

            if (var4 == par1EntityAIBase)
            {
                if (executingTaskEntries.contains(var3))
                {
                    var4.resetTask();
                    executingTaskEntries.remove(var3);
                }

                var2.remove();
            }
        }
    }

    public void onUpdateTasks()
    {
        ArrayList var1 = new ArrayList();
        Iterator var2;
        EntityAITasks.EntityAITaskEntry var3;

        if (tickCount++ % tickRate == 0)
        {
            var2 = taskEntries.iterator();

            while (var2.hasNext())
            {
                var3 = (EntityAITasks.EntityAITaskEntry)var2.next();
                boolean var4 = executingTaskEntries.contains(var3);

                if (var4)
                {
                    if (canUse(var3) && canContinue(var3))
                    {
                        continue;
                    }

                    var3.action.resetTask();
                    executingTaskEntries.remove(var3);
                }

                if (canUse(var3) && var3.action.shouldExecute())
                {
                    var1.add(var3);
                    executingTaskEntries.add(var3);
                }
            }
        }
        else
        {
            var2 = executingTaskEntries.iterator();

            while (var2.hasNext())
            {
                var3 = (EntityAITasks.EntityAITaskEntry)var2.next();

                if (!var3.action.continueExecuting())
                {
                    var3.action.resetTask();
                    var2.remove();
                }
            }
        }

        theProfiler.startSection("goalStart");
        var2 = var1.iterator();

        while (var2.hasNext())
        {
            var3 = (EntityAITasks.EntityAITaskEntry)var2.next();
            theProfiler.startSection(var3.action.getClass().getSimpleName());
            var3.action.startExecuting();
            theProfiler.endSection();
        }

        theProfiler.endSection();
        theProfiler.startSection("goalTick");
        var2 = executingTaskEntries.iterator();

        while (var2.hasNext())
        {
            var3 = (EntityAITasks.EntityAITaskEntry)var2.next();
            var3.action.updateTask();
        }

        theProfiler.endSection();
    }

    /**
     * Determine if a specific AI Task should continue being executed.
     */
    private boolean canContinue(EntityAITasks.EntityAITaskEntry par1EntityAITaskEntry)
    {
        theProfiler.startSection("canContinue");
        boolean var2 = par1EntityAITaskEntry.action.continueExecuting();
        theProfiler.endSection();
        return var2;
    }

    /**
     * Determine if a specific AI Task can be executed, which means that all
     * running higher (= lower int value) priority tasks are compatible with it
     * or all lower priority tasks can be interrupted.
     */
    private boolean canUse(EntityAITasks.EntityAITaskEntry par1EntityAITaskEntry)
    {
        theProfiler.startSection("canUse");
        Iterator var2 = taskEntries.iterator();

        while (var2.hasNext())
        {
            EntityAITasks.EntityAITaskEntry var3 = (EntityAITasks.EntityAITaskEntry)var2.next();

            if (var3 != par1EntityAITaskEntry)
            {
                if (par1EntityAITaskEntry.priority >= var3.priority)
                {
                    if (executingTaskEntries.contains(var3) && !areTasksCompatible(par1EntityAITaskEntry, var3))
                    {
                        theProfiler.endSection();
                        return false;
                    }
                }
                else if (executingTaskEntries.contains(var3) && !var3.action.isContinuous())
                {
                    theProfiler.endSection();
                    return false;
                }
            }
        }

        theProfiler.endSection();
        return true;
    }

    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry par1EntityAITaskEntry, EntityAITasks.EntityAITaskEntry par2EntityAITaskEntry)
    {
        return (par1EntityAITaskEntry.action.getMutexBits() & par2EntityAITaskEntry.action.getMutexBits()) == 0;
    }

    class EntityAITaskEntry
    {
        public EntityAIBase action;
        public int priority;
        private static final String __OBFID = "CL_00001589";

        public EntityAITaskEntry(int par2, EntityAIBase par3EntityAIBase)
        {
            priority = par2;
            action = par3EntityAIBase;
        }
    }
}
