package net;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class NetClients extends NetPorts {

    public static List<String> getClientTypeByPorts(final String ipv4) {

        final AtomicInteger pts_printer = new AtomicInteger();
        final AtomicInteger pts_nas = new AtomicInteger();
        final AtomicInteger pts_tv = new AtomicInteger();
        final AtomicInteger pts_server = new AtomicInteger();
        final AtomicInteger pts_wsServer = new AtomicInteger();
        final AtomicInteger pts_web = new AtomicInteger();
        final AtomicInteger pts_msClient = new AtomicInteger();

        final ExecutorService executorService = Executors.newFixedThreadPool(65535);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        for(Map.Entry<Integer, Integer> entry : ports_printer.entrySet()) {

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                if (checkPort(ipv4, entry.getKey())) pts_printer.addAndGet(entry.getValue());

            }, executorService);
            futures.add(future);
        }

        for(Map.Entry<Integer, Integer> entry : ports_nas.entrySet()) {

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                if (checkPort(ipv4, entry.getKey())) pts_nas.addAndGet(entry.getValue());

            }, executorService);
            futures.add(future);
        }

        for(Map.Entry<Integer, Integer> entry : ports_tv.entrySet()) {

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                if (checkPort(ipv4, entry.getKey())) pts_tv.addAndGet(entry.getValue());

            }, executorService);
            futures.add(future);
        }

        for(Map.Entry<Integer, Integer> entry : ports_server.entrySet()) {

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                if (checkPort(ipv4, entry.getKey())) pts_server.addAndGet(entry.getValue());

            }, executorService);
            futures.add(future);
        }

        for(Map.Entry<Integer, Integer> entry : ports_msServ.entrySet()) {

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                if (checkPort(ipv4, entry.getKey())) pts_wsServer.addAndGet(entry.getValue());

            }, executorService);
            futures.add(future);
        }

        for(Map.Entry<Integer, Integer> entry : ports_web.entrySet()) {

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                if (checkPort(ipv4, entry.getKey())) pts_web.addAndGet(entry.getValue());

            }, executorService);
            futures.add(future);
        }

        for(Map.Entry<Integer, Integer> entry : ports_msClient.entrySet()) {

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                if (checkPort(ipv4, entry.getKey())) pts_msClient.addAndGet(entry.getValue());

            }, executorService);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdownNow();

        final String[] tempPtsResults = { pts_printer.get() + "| Printer score: " + pts_printer.get(), pts_nas.get() + "| NAS score: " + pts_nas.get(), pts_tv.get() + "| TV score: " + pts_tv.get(), pts_server.get() + "| Server score: " + pts_server.get(), pts_wsServer.get() + "| MS Server score: " + pts_wsServer.get(), pts_web.get() + "| Web score: " + pts_web.get(), pts_msClient.get() + "| MS Client score: " + pts_msClient.get() };

        Arrays.sort(tempPtsResults, Collections.reverseOrder());

        final List<String> ptsResults = new ArrayList<>();

        int size = 1;
        for (final String result : tempPtsResults) {

            ptsResults.add("    | #" + size + " " + result.replaceAll(".*\\d\\|", "|"));

            size++;
        }

        return ptsResults;
    }
}
