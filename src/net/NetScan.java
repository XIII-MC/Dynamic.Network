package net;

import options.Settings;
import utils.ProgressBarUtils;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NetScan extends Settings {

    public static int scannedIpCount = 0, aliveHost = 0, deadHost = 0, unknownHost = 0;

    public static long timeTaken = 0L;

    public static HashMap<String, String> getNetworkIPs(final String ipv4, final int mask, final boolean reverse) throws IOException, InterruptedException {

        final long startTime = System.currentTimeMillis();

        // To make the task faster (and by a lot), we're going to use multithreading, 256 threads because 256 IPs (0-255)
        final ExecutorService executorService = Executors.newFixedThreadPool(256);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Split the IP, so we can change the last byte
        final String[] ipv4Split = ipv4.split("\\.");
        final String ip = ipv4Split[0] + "." + ipv4Split[1] + "." + ipv4Split[2] + ".";

        // Pre run a network scan to make arp update
        runPreArpCache(ip);

        try {

            Thread.sleep(Settings.waitARPSync ? 5000 : 0);
        } catch (final InterruptedException ignored) {}

        // Define the list and if it should be reversed
        final HashMap<String, String> returnIP = new HashMap<>();

        // Pre cache the ARP list
        final HashMap<String, String> arpCache = getArpCache(ipv4);

        for (int i = 0; i <= 254; i++) {

            int finalI = i;

            // Start task on a new thread
            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {

                    // Full IP
                    final String fullIp = ip + finalI;

                    // According to Oracle this works similarly to an ICMP ping
                    final InetAddress address = InetAddress.getByName(fullIp);

                    if (arpCache.containsKey(fullIp)) {

                        if (!reverse) {

                            aliveHost++;

                            final String hostName = Settings.getHostName ? (Objects.equals(address.getHostName(), address.getHostAddress()) ? "N/A (Unknown)" : address.getHostName()) : "N/A (Disabled)";
                            returnIP.put(address.getHostAddress(),
                                    hostName + (new String(new char[(25 + fullIp.length()) - (hostName.length() + fullIp.length())]).replace("\0", " ")) + " | " + arpCache.get(fullIp));
                        }
                    } else {

                        unknownHost++;

                        if (reverse) {

                            deadHost++;
                            returnIP.put(address.getHostAddress(), "N/A (Host is dead)");
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

        // Clear progress bar
        System.out.print("\r");

        // Stats for nerds (me)
        timeTaken = (System.currentTimeMillis() - startTime);

        return returnIP;
    }

    public static void runPreArpCache(final String ipv4_3bytes) {

        // To make the task faster (and by a lot), we're going to use multithreading, 256 threads because 256 IPs (0-255)
        final ExecutorService executorService = Executors.newFixedThreadPool(256);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i <= 254; i++) {

            int finalI = i;

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {

                    if (Settings.useCmdPing) {
                        Runtime.getRuntime().exec("ping " + ipv4_3bytes + finalI + " -n 5 -w 1 -a");
                    } else {
                        // TODO: find a faster WORKING solution...
                        //new Socket().connect(new InetSocketAddress(ipv4_3bytes + finalI, 10), 3000);
                        //sendPacket("00000000000000000000000000000-32", InetAddress.getByName(ipv4_3bytes + finalI));
                    }

                } catch (final IOException ignored) {}

            }, executorService);
            futures.add(future);
        }

        // Clear up all the threads
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdownNow();
    }

    public static HashMap<String, String> getArpCache(final String ipv4) throws IOException {

        final Scanner s = new Scanner(Runtime.getRuntime().exec("arp -a -N " + ipv4).getInputStream()).useDelimiter("\\A");

        final Pattern arpLinePattern = Pattern.compile("\\d.*(?= {5})");

        final Pattern ipPattern = Pattern.compile("\\d.*(?= )");
        final Pattern macPattern = Pattern.compile("(?<= {7}).*");

        // K=IP | V=MAC
        final HashMap<String, String> arpCache = new HashMap<>();

        while (s.hasNext()) {

            final Matcher arpLineMatcher = arpLinePattern.matcher(s.next());

            while (arpLineMatcher.find()) {

                String mac = null, ip = null;

                final Matcher ipMatcher = ipPattern.matcher(arpLineMatcher.group());
                final Matcher macMatcher = macPattern.matcher(arpLineMatcher.group());

                while (ipMatcher.find()) {

                    ip = ipMatcher.group().replaceAll(" ", "");
                }

                while (macMatcher.find()) {

                    mac = macMatcher.group().replaceAll(" ", "");
                }

                arpCache.put(ip, mac);
            }
        }

        return arpCache;
    }

    public static void sendPacket(final String broadcastMessage, final InetAddress address) throws IOException {
        final DatagramSocket socket;

        socket = new DatagramSocket();

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 9);
        socket.send(packet);
        socket.close();
    }
}
