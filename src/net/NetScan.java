package net;

import options.Settings;
import utils.ProgressBarUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NetScan extends Settings {

    private static int scannedIpCount = 0;
    public static int aliveHost = 0, deadHost = 0, unknownHost = 0;

    public static HashMap<String, String> getNetworkIPs(final String ipv4, final int mask, final boolean reverse) throws IOException {

        final long startTime = System.currentTimeMillis();

        // To make the task faster (and by a lot), we're going to use multithreading, 256 threads because 256 IPs (0-255)
        final ExecutorService executorService = Executors.newFixedThreadPool(maxNetThreads);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Split the IP, so we can change the last byte
        final String[] ipv4Split = ipv4.split("\\.");
        final String ip = ipv4Split[0] + "." + ipv4Split[1] + "." + ipv4Split[2] + ".";

        // Define the list and if it should be reversed
        final HashMap<String, String> returnIP = new HashMap<>();

        // Pre cache the ARP list
        final Matcher arpCache = getArpCache(ipv4);

        for (int i = 0; i <= 254; i++) {

            int finalI = i;

            // Start task on a new thread
            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {

                    // Full IP
                    final String fullIp = ip + finalI;

                    // According to Oracle this works similarly to an ICMP ping
                    final InetAddress address = InetAddress.getByName(fullIp);

                    // Force network discovery
                    new Socket().connect(new InetSocketAddress(address, 10), 500);

                    // ARP
                    final String arpResult = getFromARP(fullIp);

                    if (!arpResult.equals("N/A")) {

                        if (!reverse) {
                            aliveHost++;

                            final String hostName =
                                    (Objects.equals(address.getHostName(), address.getHostAddress())
                                            ? "N/A"
                                            : address.getHostName());
                            returnIP.put(address.getHostAddress(),
                                    hostName + (new String(new char[(25 + fullIp.length()) - (hostName.length() + fullIp.length())]).replace("\0", " ")) + " | " + (new String(new char[fullIp.length() - 7]).replace("\0", " ")) +  arpResult);
                        }
                    } else {

                        unknownHost++;

                        if (reverse) {
                            deadHost++;
                            returnIP.put(address.getHostAddress(), "N/A");
                        }
                    }

                } catch (final IOException ignored) {}

                scannedIpCount++;

                //ProgressBarUtils.progressPercentage(scannedIpCount, 255);
                ProgressBarUtils.printProgress(startTime, 255, scannedIpCount);

            }, executorService);
            futures.add(future);
        }

        // Clear up all the threads
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdownNow();

        System.out.print("\r");

        // Logging
        System.out.println("    | Finished network scan in " + (System.currentTimeMillis() - startTime) + "ms!");

        return returnIP;
    }

    public static boolean sendICMP(final String ipv4) {

        try {

            final ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "ping -l 1024 -n 1 -w 1 " + ipv4);
            builder.redirectErrorStream(true);
            final Process process = builder.start();
            final InputStream is = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                if ((line.contains("bytes=") && line.contains("time=") && line.contains("TTL="))) return true;
            }

            return false;

        } catch (final Exception e) {
            return false;
        }
    }

    public static boolean validARP(final String ipv4) {

        try {

            final ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "arp -a " + ipv4);
            builder.redirectErrorStream(true);
            final Process process = builder.start();
            final InputStream is = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("No ARP Entries Found.")) return false;
            }

            return true;

        } catch (final Exception e) {
            return false;
        }
    }

    public static String getFromARP(final String ipv4) throws IOException {
        final Scanner s = new Scanner(Runtime.getRuntime().exec("arp -a " + ipv4).getInputStream()).useDelimiter("\\A");
        final Pattern macPattern = Pattern.compile("(?<= {9}).*(?= {5})");
        final Matcher macMatcher = macPattern.matcher(s.hasNext() ? s.next() : "");
        if (macMatcher.find()) return macMatcher.group();
        return "N/A";
    }

    public static Matcher getArpCache(final String ipv4) throws IOException {

        final Scanner s = new Scanner(Runtime.getRuntime().exec("arp -a " + ipv4).getInputStream()).useDelimiter("\\A");

        final Pattern ipPattern = Pattern.compile("\\d.*(?= {5}dynamic)");
        final Matcher ipMatcher = ipPattern.matcher(s.hasNext() ? s.next() : "");

        if (ipMatcher.find()) return ipMatcher;

        return null;
    }

    public static String getMacFromArp(final String arpLine) throws IOException {

        final Pattern macPattern = Pattern.compile("(?<= {11}).*");
        final Matcher macMatcher = macPattern.matcher(arpLine);

        if (macMatcher.find()) return macMatcher.group();

        return "N/A";
    }

    public static void broadcast(
            final String broadcastMessage, InetAddress address) throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        final DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
        socket.close();
    }
}
