package net;

import options.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class NetScan extends Settings {

    private static DatagramSocket socket = null;

    public static HashMap<String, String> getNetworkIPs(final String ipv4, final int mask, final boolean reverse) {

        final long startTime = System.currentTimeMillis();

        // To make the task faster (and by a lot), we're going to use multithreading, 256 threads because 256 IPs (0-255)
        final ExecutorService executorService = Executors.newFixedThreadPool(maxNetThreads);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Split the IP, so we can change the last byte
        final String[] ipv4Split = ipv4.split("\\.");
        final String ip = ipv4Split[0] + "." + ipv4Split[1] + "." + ipv4Split[2] + ".";

        // Define the list and if it should be reversed
        final HashMap<String, String> returnIP = new HashMap<>();

        for (int i = 0; i <= 254; i++) {

            int finalI = i;

            // Start task on a new thread
            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {

                    // According to Oracle this works similarly to an ICMP ping
                    final InetAddress address = InetAddress.getByName(ip + finalI);

                    broadcast("ARP_DISCOVERY", InetAddress.getByName(ip + finalI));

                    //Precision and speed
                    if (validARP(ip + finalI)) {
                        if (!reverse) returnIP.put(ip + finalI, "*DISABLED*");
                        //if (!reverse) returnIP.put(address.getHostAddress(), getHostName ? (Objects.equals(address.getHostName(), address.getHostAddress()) ? "N/A" : address.getHostName()) : "*DISABLED*");
                    } else if (reverse) returnIP.put(address.getHostAddress(), null);

                    //Speed, however ARP could be wrong
                    //if (validARP(ip + finalI)) {
                    //    if (!reverse) returnIP.put(ip + finalI, "*DISABLED*");
                    //    //if (!reverse) returnIP.put(address.getHostAddress(), getHostName ? (Objects.equals(address.getHostName(), address.getHostAddress()) ? "N/A" : address.getHostName()) : "*DISABLED*");
                    //} else if (reverse) returnIP.put(address.getHostAddress(), null);

                } catch (final IOException ignored) {}

            }, executorService);
            futures.add(future);
        }

        // Clear up all the threads
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdownNow();

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

            String line = null;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
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

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("No ARP Entries Found.")) return false;
            }

            return true;

        } catch (final Exception e) {
            return false;
        }
    }

    public static void broadcast(
            String broadcastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
        socket.close();
    }
}
