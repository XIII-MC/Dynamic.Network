
import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main extends Colors {

    /*
    Settings
     */
    // Maximum amount of threads for the net scan
    public static int maxNetThreads = 256;

    // How long should we wait for an answer from an ICMP ping
    public static int maxICMPWait = 10000;

    //Common ports to scan for exploits
    public static List<Integer> commonPortList = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        // Temporarily enable ANSI support for Windows
        if (System.getProperty("os.name").startsWith("Windows")) enableWindows10AnsiSupport();

        // Initialize settings
        commonPortList.add(21); // FTP
        commonPortList.add(22); // SSH
        commonPortList.add(25); // SMTP
        commonPortList.add(80); // HTTP
        commonPortList.add(135); // RPC
        commonPortList.add(139); // NetBIOS, Printer & Sharing
        commonPortList.add(443); // HTTPS
        commonPortList.add(445); // SMB
        commonPortList.add(515); // Printer
        commonPortList.add(808); // MS Sharing Services
        commonPortList.add(3389); // RDP
        commonPortList.add(9100); // Printer | HP JetDirect

        // Prepare to enumerate net interfaces
        final List<String> networkInterfaces = new ArrayList<>();
        System.out.println(BLACK_BOLD_BRIGHT + WHITE_BACKGROUND + "[sys]" + RESET + " | Network interface lookup...");

        // Grab and list net interfaces
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while(interfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.getInterfaceAddresses().isEmpty()) networkInterfaces.add(BLACK_BOLD_BRIGHT + WHITE_BACKGROUND + "[sys]" + RESET + " | Interface #" + networkInterfaces.size() + " : '" + networkInterface.getDisplayName() + "' | '" + networkInterface.getInterfaceAddresses().get(0) + "'");
        }

        // Print net interfaces
        networkInterfaces.forEach(System.out::println);

        // Ask which net interface to use (according to the arraylist index)
        final Scanner scannerIntID = new Scanner(System.in);
        System.out.println(BLACK_BOLD_BRIGHT + WHITE_BACKGROUND + "[sys]" + RESET + " | Please type the interface's ID that you would like lookup: ");
        final int interfaceID = Integer.parseInt(scannerIntID.nextLine());

        // Extract the IP from the interface name + IP output
        final String interfaceIP = networkInterfaces.get(interfaceID).replaceAll(".*?(?<='/)", "").replaceAll("\\[.*", "").replaceAll("/.*", "");

        // Logging
        System.out.println(BLACK_BOLD_BRIGHT + WHITE_BACKGROUND + "[net]" + RESET + " | Starting network scan on '" + interfaceIP + "'...");

        // Start net scan
        getNetworkIPs(interfaceIP, 0);
    }

    private static void getNetworkIPs(final String ipv4, final int mask) {

        final long startTime = System.currentTimeMillis();

        // To make the task faster (and by a lot), we're going to use multithreading, 256 threads because 256 IPs (0-255)
        final ExecutorService executorService = Executors.newFixedThreadPool(maxNetThreads);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Split the IP, so we can change the last byte
        final String[] ipv4Split = ipv4.split("\\.");
        final String ip = ipv4Split[0] + "." + ipv4Split[1] + "." + ipv4Split[2] + ".";

        for (int i = 0; i <= 254; i++) {

            int finalI = i;

            // Start task on a new thread
            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {
                    // According to Oracle this works similarly to an ICMP ping
                    final InetAddress address = InetAddress.getByName(ip + finalI);
                    if (address.isReachable(maxICMPWait)) {

                        //Do a pre basic port scan to see if any port is vulnerable
                        //Also establish which OS/Service the host is running
                    }
                    checkOpenPorts(ip + finalI, commonPortList);
                } catch (final IOException ignored) {}

            }, executorService);
            futures.add(future);
        }

        // Clear up all the threads
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdownNow();

        // Logging
        System.out.println(BLACK_BOLD_BRIGHT + WHITE_BACKGROUND + "[sys]" + RESET + " | Finished network scan in " + (System.currentTimeMillis() - startTime) + "ms!");

    }

    private static void checkOpenPorts(final String ip, final List<Integer> ports) {

        final ExecutorService executorService = Executors.newFixedThreadPool(ports.size());
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        ports.forEach(i -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {
                    new Socket().connect(new InetSocketAddress(ip, i), 1000);
                    System.out.println(GREEN + "✓" + WHITE + " | Port " + i + " is open on '" + ip + "'");
                } catch (final IOException ignored) {
                    //System.out.println(ANSI_RED + "⬤" + ANSI_RESET + " | Port " + i + " is closed.");
                }
            }, executorService);
            futures.add(future);
        });
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdownNow();
    }

    private static void enableWindows10AnsiSupport() {
        Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
        WinDef.DWORD STD_OUTPUT_HANDLE = new WinDef.DWORD(-11);
        WinNT.HANDLE hOut = (WinNT.HANDLE) GetStdHandleFunc.invoke(WinNT.HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

        WinDef.DWORDByReference p_dwMode = new WinDef.DWORDByReference(new WinDef.DWORD(0));
        Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
        GetConsoleModeFunc.invoke(WinDef.BOOL.class, new Object[]{hOut, p_dwMode});

        int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
        WinDef.DWORD dwMode = p_dwMode.getValue();
        dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
        Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
        SetConsoleModeFunc.invoke(WinDef.BOOL.class, new Object[]{hOut, dwMode});
    }
}