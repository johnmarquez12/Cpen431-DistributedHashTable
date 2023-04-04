package com.g10.CPEN431.A11;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static long pid = ProcessHandle.current().pid();

    public static void main( String[] args ) throws Exception {
        Logger.log( "Hello World!" );

        Options options = new Options();
        Option portOption = new Option("p", "port", true,
            "(Required) Port to run the server on");
        portOption.setRequired(true);
        options.addOption(portOption);

        Option serverListOption = new Option("s", "server-list", true,
            "(Required) File containing ip:port of all nodes");
        serverListOption.setRequired(true);
        options.addOption(serverListOption);

        options.addOption(new Option("l", "localhost", false,
            "Hardcode my host to localhost"));

        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = (new BasicParser()).parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("G10 Proj", options);
            System.exit(1);
        }

        int port = 0;
        try {
            port = Integer.parseInt(cmd.getOptionValue("p"));
        } catch(NumberFormatException e) {
            formatter.printHelp(
                "Cannot parse port number: "+ cmd.getOptionValue("p"), options);

            System.exit(1);
        }

        List<Host> servers = parseServerFile(cmd.getOptionValue("s"));


        Host me;
        if(cmd.hasOption("l")) {
            me = new Host(InetAddress.getLoopbackAddress(), port);
        } else {
            me = new Host(getMyHost(), port);
        }


        Logger.log(servers);
        Logger.log(me);

        if(!servers.contains(me)) {
            System.err.println("Current node ("+ me.address() +") cannot be found in server list!");
            System.err.println("Either fix this, or add '-l' to cmd arguments if running local host mode.");
            System.exit(1);
        }

        NodePool.create(me, servers);

        (new SendHeartbeatThread()).start();
        (new HeartbeatServer(port + NodePool.TOTAL_NUM_NODES)).start();

        UDPServer.run(port);
    }

    public static InetAddress getMyHost() {
        URL url = null;
        try {
            url = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),
            StandardCharsets.UTF_8))) {
            return InetAddress.getByName(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<Host> parseServerFile(String fileName) {
        // Modified from chatGPT
        List<Host> hostList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(":");
                if (tokens.length != 2) throw new RuntimeException("Issue parsing host");
                String hostName = tokens[0].trim();
                int port = Integer.parseInt(tokens[1].trim());
                InetAddress address = InetAddress.getByName(hostName);
                Host host = new Host(address, port);
                hostList.add(host);
            }
        } catch (UnknownHostException e) {
            System.err.println("Cannot convert host to InetAddress");
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            System.err.println("Unknown file: "+fileName);
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return hostList;
    }

    public static long freeMemory() {
        long max = Runtime.getRuntime().maxMemory();
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();

        long used = total - free;
        return max - used - (3000 * 1024); // some buffer, so we never die
    }
    public static long trueFreeMemory() {
        long max = Runtime.getRuntime().maxMemory();
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();

        long used = total - free;
        return max - used;
    }
}
