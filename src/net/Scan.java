package net;

import base.Settings;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Scan extends Settings {

    public static List<String> getNetworkIPs(final String ipv4, final int mask, final boolean reverse) {

        final long startTime = System.currentTimeMillis();

        // To make the task faster (and by a lot), we're going to use multithreading, 256 threads because 256 IPs (0-255)
        final ExecutorService executorService = Executors.newFixedThreadPool(maxNetThreads);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Split the IP, so we can change the last byte
        final String[] ipv4Split = ipv4.split("\\.");
        final String ip = ipv4Split[0] + "." + ipv4Split[1] + "." + ipv4Split[2] + ".";

        // Define the list and if it should be reversed
        final List<String> returnIP = new ArrayList<>();

        for (int i = 0; i <= 254; i++) {

            int finalI = i;

            // Start task on a new thread
            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {

                    // According to Oracle this works similarly to an ICMP ping
                    final InetAddress address = InetAddress.getByName(ip + finalI);
                    if (address.isReachable(maxICMPWait)) {
                        if (!reverse) returnIP.add(address.getHostAddress());
                    } else if (reverse) returnIP.add(address.getHostAddress());

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
}
