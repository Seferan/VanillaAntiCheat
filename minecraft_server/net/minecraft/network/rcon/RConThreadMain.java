package net.minecraft.network.rcon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RConThreadMain extends RConThreadBase
{
    /** Port RCon is running on */
    private int rconPort;

    /** Port the server is running on */
    private int serverPort;

    /** Hostname RCon is running on */
    private String hostname;

    /** The RCon ServerSocket. */
    private ServerSocket serverSocket;

    /** The RCon password */
    private String rconPassword;

    /** A map of client addresses to their running Threads */
    private Map clientThreads;
    private static final String __OBFID = "CL_00001805";

    public RConThreadMain(IServer par1IServer)
    {
        super(par1IServer, "RCON Listener");
        rconPort = par1IServer.getIntProperty("rcon.port", 0);
        rconPassword = par1IServer.getStringProperty("rcon.password", "");
        hostname = par1IServer.getHostname();
        serverPort = par1IServer.getPort();

        if (0 == rconPort)
        {
            rconPort = serverPort + 10;
            logInfo("Setting default rcon port to " + rconPort);
            par1IServer.setProperty("rcon.port", Integer.valueOf(rconPort));

            if (0 == rconPassword.length())
            {
                par1IServer.setProperty("rcon.password", "");
            }

            par1IServer.saveProperties();
        }

        if (0 == hostname.length())
        {
            hostname = "0.0.0.0";
        }

        initClientThreadList();
        serverSocket = null;
    }

    private void initClientThreadList()
    {
        clientThreads = new HashMap();
    }

    /**
     * Cleans up the clientThreads map by removing client Threads that are not
     * running
     */
    private void cleanClientThreadsMap()
    {
        Iterator var1 = clientThreads.entrySet().iterator();

        while (var1.hasNext())
        {
            Entry var2 = (Entry)var1.next();

            if (!((RConThreadClient)var2.getValue()).isRunning())
            {
                var1.remove();
            }
        }
    }

    public void run()
    {
        logInfo("RCON running on " + hostname + ":" + rconPort);

        try
        {
            while (running)
            {
                try
                {
                    Socket var1 = serverSocket.accept();
                    var1.setSoTimeout(500);
                    RConThreadClient var2 = new RConThreadClient(server, var1);
                    var2.startThread();
                    clientThreads.put(var1.getRemoteSocketAddress(), var2);
                    cleanClientThreadsMap();
                }
                catch (SocketTimeoutException var7)
                {
                    cleanClientThreadsMap();
                }
                catch (IOException var8)
                {
                    if (running)
                    {
                        logInfo("IO: " + var8.getMessage());
                    }
                }
            }
        }
        finally
        {
            closeServerSocket(serverSocket);
        }
    }

    /**
     * Creates a new Thread object from this class and starts running
     */
    public void startThread()
    {
        if (0 == rconPassword.length())
        {
            logWarning("No rcon password set in \'" + server.getSettingsFilename() + "\', rcon disabled!");
        }
        else if (0 < rconPort && 65535 >= rconPort)
        {
            if (!running)
            {
                try
                {
                    serverSocket = new ServerSocket(rconPort, 0, InetAddress.getByName(hostname));
                    serverSocket.setSoTimeout(500);
                    super.startThread();
                }
                catch (IOException var2)
                {
                    logWarning("Unable to initialise rcon on " + hostname + ":" + rconPort + " : " + var2.getMessage());
                }
            }
        }
        else
        {
            logWarning("Invalid rcon port " + rconPort + " found in \'" + server.getSettingsFilename() + "\', rcon disabled!");
        }
    }
}
