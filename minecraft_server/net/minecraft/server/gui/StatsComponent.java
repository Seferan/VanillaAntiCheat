package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.server.MinecraftServer;

public class StatsComponent extends JComponent
{
    private static final DecimalFormat field_120040_a = new DecimalFormat("########0.000");
    private int[] field_120038_b = new int[256];
    private int field_120039_c;
    private String[] field_120036_d = new String[11];
    private final MinecraftServer field_120037_e;
    private static final String __OBFID = "CL_00001796";

    public StatsComponent(MinecraftServer par1MinecraftServer)
    {
        this.field_120037_e = par1MinecraftServer;
        this.setPreferredSize(new Dimension(456, 246));
        this.setMinimumSize(new Dimension(456, 246));
        this.setMaximumSize(new Dimension(456, 246));
        (new Timer(500, new ActionListener()
        {
            private static final String __OBFID = "CL_00001797";
            public void actionPerformed(ActionEvent par1ActionEvent)
            {
                StatsComponent.this.func_120034_a();
            }
        })).start();
        this.setBackground(Color.BLACK);
    }

    private void func_120034_a()
    {
        long var1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.gc();
        this.field_120036_d[0] = "Memory use: " + var1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
        this.field_120036_d[1] = "Avg tick: " + field_120040_a.format(this.func_120035_a(this.field_120037_e.tickTimeArray) * 1.0E-6D) + " ms";
        this.repaint();
    }

    private double func_120035_a(long[] par1ArrayOfLong)
    {
        long var2 = 0L;

        for (int var4 = 0; var4 < par1ArrayOfLong.length; ++var4)
        {
            var2 += par1ArrayOfLong[var4];
        }

        return (double)var2 / (double)par1ArrayOfLong.length;
    }

    public void paint(Graphics par1Graphics)
    {
        par1Graphics.setColor(new Color(16777215));
        par1Graphics.fillRect(0, 0, 456, 246);
        int var2;

        for (var2 = 0; var2 < 256; ++var2)
        {
            int var3 = this.field_120038_b[var2 + this.field_120039_c & 255];
            par1Graphics.setColor(new Color(var3 + 28 << 16));
            par1Graphics.fillRect(var2, 100 - var3, 1, var3);
        }

        par1Graphics.setColor(Color.BLACK);

        for (var2 = 0; var2 < this.field_120036_d.length; ++var2)
        {
            String var4 = this.field_120036_d[var2];

            if (var4 != null)
            {
                par1Graphics.drawString(var4, 32, 116 + var2 * 16);
            }
        }
    }
}
