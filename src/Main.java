import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static List<String> argList = new ArrayList<>();

    public static void main(String[] args) {

        if (args.length > 0) {
            for (final Object arg : Arrays.stream(args).toArray()) {
                argList.add(arg.toString());
            }

            if (argList.contains("scan=") || argList.contains("help")) {

                if (args[0].contains("scan=")) {
                    checkOpenPorts(args[0].replace("scan=", ""), argList.contains("dport=true"));
                } else if (args[0].contains("help")) {
                    System.out.println(" ");
                    System.out.println("Welcome to Dynamic.Network (DCN) vD1!");
                    System.out.println("arg 0 = help | Show this page");
                    System.out.println("arg 0 = scan=<IP> | Scan a precise IP scope");
                    System.out.println("arg 0-99 = auto=true | Automatically start a port scan whenever an IP is reachable in your network.");
                    System.out.println("arg 0-99 = dport=true | Scan the dynamic ports range (49152-65535)");
                    System.out.println("No args | Basic network scan without port scanning");
                    System.out.println(" ");
                }
            } else getNetworkIPs();
        } else getNetworkIPs();
    }

    private static void getNetworkIPs() {

        final long startTime = System.currentTimeMillis();

        final ExecutorService executorService = Executors.newFixedThreadPool(254);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        try {

            final byte[] ip = InetAddress.getLocalHost().getAddress();

            for (int i = 0; i <= 254; i++) {

                int finalI = i;

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {

                        ip[3] = (byte) finalI;
                        final InetAddress address = InetAddress.getByAddress(ip);
                        final String convIP = address.toString().substring(1);

                        if (address.isReachable(5000)) {
                            System.out.println("Host '" + convIP + "' is reachable");
                            if (!argList.isEmpty() && argList.contains("auto=true")) checkOpenPorts(convIP, argList.contains("dport=true"));
                        }
                    } catch (final IOException ignored) {
                    }
                }, executorService);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executorService.shutdown();
            System.out.println("Finished network scan in " + (System.currentTimeMillis() - startTime) + "ms!");

        } catch (final UnknownHostException ignored) {
        }
    }

    private static void checkOpenPorts(final String ip, final boolean dynamicPorts) {

        final long startTime = System.currentTimeMillis();

        System.out.println("Starting port scan on '" + ip + "'... (AUTO " + (dynamicPorts ? "65535" : "49152") + " THREADS)");

        final ExecutorService executorService = Executors.newFixedThreadPool(65535);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i <= (dynamicPorts ? 65535 : 49152); i++) {
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    new Socket().connect(new InetSocketAddress(ip, finalI), 1000);
                    System.out.println("Port '" + finalI + "' is open on '" + ip + "'");
                } catch (final IOException ignored) {
                }
            }, executorService);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
        System.out.println("Port scan finished on '" + ip + "' in " + (System.currentTimeMillis() - startTime) + "ms!");
    }
}