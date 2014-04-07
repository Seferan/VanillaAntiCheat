package net.minecraft.network.rcon;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class RConThreadBase implements Runnable
{
    private static final AtomicInteger field_164004_h = new AtomicInteger(0);

    /** True if the Thread is running, false otherwise */
    protected boolean running;

    /** Reference to the IServer object. */
    protected IServer server;
    protected final String field_164003_c;

    /** Thread for this runnable class */
    protected Thread rconThread;
    protected int field_72615_d = 5;

    /** A list of registered DatagramSockets */
    protected List socketList = new ArrayList();

    /** A list of registered ServerSockets */
    protected List serverSocketList = new ArrayList();
    private static final String __OBFID = "CL_00001801";

    protected RConThreadBase(IServer p_i45300_1_, String p_i45300_2_)
    {
        server = p_i45300_1_;
        field_164003_c = p_i45300_2_;

        if (server.isDebuggingEnabled())
        {
            logWarning("Debugging is enabled, performance maybe reduced!");
        }
    }

    /**
     * Creates a new Thread object from this class and starts running
     */
    public synchronized void startThread()
    {
        rconThread = new Thread(this, field_164003_c + " #" + field_164004_h.incrementAndGet());
        rconThread.start();
        running = true;
    }

    /**
     * Returns true if the Thread is running, false otherwise
     */
    public boolean isRunning()
    {
        return running;
    }

    /**
     * Log debug message
     */
    protected void logDebug(String par1Str)
    {
        server.logDebug(par1Str);
    }

    /**
     * Log information message
     */
    protected void logInfo(String par1Str)
    {
        server.logInfo(par1Str);
    }

    /**
     * Log warning message
     */
    protected void logWarning(String par1Str)
    {
        server.logWarning(par1Str);
    }

    /**
     * Log severe error message
     */
    protected void logSevere(String par1Str)
    {
        server.logSevere(par1Str);
    }

    /**
     * Returns the number of players on the server
     */
    protected int getNumberOfPlayers()
    {
        return server.getCurrentPlayerCount();
    }

    /**
     * Registers a DatagramSocket with this thread
     */
    protected void registerSocket(DatagramSocket par1DatagramSocket)
    {
        logDebug("registerSocket: " + par1DatagramSocket);
        socketList.add(par1DatagramSocket);
    }

    /**
     * Closes the specified DatagramSocket
     */
    protected boolean closeSocket(DatagramSocket par1DatagramSocket, boolean par2)
    {
        logDebug("closeSocket: " + par1DatagramSocket);

        if (null == par1DatagramSocket)
        {
            return false;
        }
        else
        {
            boolean var3 = false;

            if (!par1DatagramSocket.isClosed())
            {
                par1DatagramSocket.close();
                var3 = true;
            }

            if (par2)
            {
                socketList.remove(par1DatagramSocket);
            }

            return var3;
        }
    }

    /**
     * Closes the specified ServerSocket
     */
    protected boolean closeServerSocket(ServerSocket par1ServerSocket)
    {
        return closeServerSocket_do(par1ServerSocket, true);
    }

    /**
     * Closes the specified ServerSocket
     */
    protected boolean closeServerSocket_do(ServerSocket par1ServerSocket, boolean par2)
    {
        logDebug("closeSocket: " + par1ServerSocket);

        if (null == par1ServerSocket)
        {
            return false;
        }
        else
        {
            boolean var3 = false;

            try
            {
                if (!par1ServerSocket.isClosed())
                {
                    par1ServerSocket.close();
                    var3 = true;
                }
            }
            catch (IOException var5)
            {
                logWarning("IO: " + var5.getMessage());
            }

            if (par2)
            {
                serverSocketList.remove(par1ServerSocket);
            }

            return var3;
        }
    }

    /**
     * Closes all of the opened sockets
     */
    protected void closeAllSockets()
    {
        closeAllSockets_do(false);
    }

    /**
     * Closes all of the opened sockets
     */
    protected void closeAllSockets_do(boolean par1)
    {
        int var2 = 0;
        Iterator var3 = socketList.iterator();

        while (var3.hasNext())
        {
            DatagramSocket var4 = (DatagramSocket)var3.next();

            if (closeSocket(var4, false))
            {
                ++var2;
            }
        }

        socketList.clear();
        var3 = serverSocketList.iterator();

        while (var3.hasNext())
        {
            ServerSocket var5 = (ServerSocket)var3.next();

            if (closeServerSocket_do(var5, false))
            {
                ++var2;
            }
        }

        serverSocketList.clear();

        if (par1 && 0 < var2)
        {
            logWarning("Force closed " + var2 + " sockets");
        }
    }
}
