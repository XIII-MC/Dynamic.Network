package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetPorts {

    /*
    Port lists containing interesting ports that could be used/lead to exploits or vulnerabilities.
    Some of those port lists are also used to identify hosts and what their main functionality is.
    Based on a points system we try to be the most accurate when identifying the host, this has to be done quickly and efficiently.
    Later on I'd like to implement some sort of "CVE against Port" exploit method:
        Searching on an online database known exploits on certain ports and trying them on the host.

     The point rewarding system is rather pretty simple:
        Port that could or could not be what we are expecting -> 5 PTS
        Port that is most likely what we are expecting -> 10 PTS
        Port is definitely what we are expecting -> 20 PTS
        Port is DEDICATED to what we are expecting -> 50 PTS
     */



    // 20-21 | FTP
    // 25 | SMTP | Fax
    // 115 | SFTP
    // 135 | NetBIOS Printer & Sharing | MS-WIN (All Versions)
    // 139 | NetBIOS Printer & Sharing | MS-WIN (All Versions)
    // 445 | SMB | Network Sharing
    // 515 | Printing Services Listener | Line Printer DAEMON
    // 631 | MacOS Sharing
    // 808 | Port Sharing Services | MS-Net.TCP
    // 3910 | Printing Port Request
    // 9100 | HP JetDirect

    // [K] PORT, [V] POINT_VALUE
    // Printer, Scanner, Fax
    public static Map<Integer, Integer> ports_printer = new HashMap<Integer, Integer>()
    {{
        put(20, 5);
        put(21, 5);
        put(25, 10);
        put(115, 5);
        put(135, 5);
        put(139, 5);
        put(445, 10);
        put(515, 20);
        put(631, 20);
        put(808, 20);
        put(3910, 50);
        put(9100, 20);
    }};

    // 80 | HTTP | Web Interface
    // 111 | NFS | NFSv2/v3
    // 445 | SMB | Network Share
    // 548 | Share
    // 2049 | NFS (Network File System) | NFSv1
    // 5000 | NAS Port | Synology
    // 5001 | NAS Port | Synology Management

    // [K] PORT, [V] POINT_VALUE
    // NAS, Network Attached Storage
    public static Map<Integer, Integer> ports_nas = new HashMap<Integer, Integer>()
    {{
        put(80, 10);
        put(111, 10);
        put(445, 10);
        put(548, 20);
        put(2049, 10);
        put(5000, 20);
        put(5001, 20);
    }};

    // 9080 | ??? | Tells the Netflix services status

    // [K] PORT, [V] POINT_VALUE
    // Smart TVs, FireStick, ChromeCast
    public static Map<Integer, Integer> ports_tv = new HashMap<Integer, Integer>()
    {{
        put(9080, 50);
    }};

    // 20-21 | FTP
    // 22 | SSH
    // 23 | TelNet
    // 25 | SMTP | Emails
    // 53 | DNS
    // 115 | SFTP
    // 143 | IMAP
    // 445 | SMB | Network Sharing
    // 992 | TelNet
    // 1521 | Oracle DB
    // 1830 | Oracle DB
    // 3306 | MySQL
    // 3389 | RDP

    // [K] PORT, [V] POINT_VALUE
    // Linux Servers, Windows Servers, Routers, L2/3 Switch
    public static Map<Integer, Integer> ports_server = new HashMap<Integer, Integer>()
    {{
        put(20, 5);
        put(21, 5);
        put(22, 10);
        put(23, 10);
        put(25, 10);
        put(53, 20);
        put(115, 5);
        put(143, 10);
        put(445, 5);
        put(992, 10);
        put(1521, 50);
        put(1830, 50);
        put(3306, 50);
        put(3389, 20);
    }};

    // 53 | DNS
    // 88 | Kerberos
    // 135 | NETBios
    // 137 | NETBios
    // 389 | LDAP
    // 464 | Kerberos v5
    // 593 | MS Security
    // 636 | LDAPS
    // 3268 | LDAP Catalog
    // 3269 | LDAP Catalog over SSL
    // 5722 | MS DFS Replication Service
    // 5985 | MS WinRM
    // 9389 | AD Web Services

    // [K] PORT, [V] POINT_VALUE
    // Windows focused Servers
    public static Map<Integer, Integer> ports_wsServ = new HashMap<Integer, Integer>()
    {{
        put(53, 10);
        put(88, 10);
        put(135, 5);
        put(137, 10);
        put(389, 50);
        put(464, 10);
        put(593, 50);
        put(636, 50);
        put(3268, 50);
        put(3269, 50);
        put(5722, 10);
        put(5985, 10);
        put(9389, 50);
    }};

    // 80 | HTTP
    // 443 | HTTPS
    // 4443 | HTTP
    // 8000 | HTTP
    // 8080 | HTTP

    // [K] PORT, [V] POINT_VALUE
    // Web Interfaces
    public static Map<Integer, Integer> ports_web = new HashMap<Integer, Integer>()
    {{
        put(80, 10);
        put(443, 10);
        put(4443, 10);
        put(8000, 10);
        put(8080, 10);
    }};

    // 135 | RPC
    // 139 | NetBIOS
    // 445 | SMB

    // [K] PORT, [V] POINT_VALUE
    // Windows NetClients
    public static Map<Integer, Integer> ports_msClient = new HashMap<Integer, Integer>()
    {{
        put(135, 10);
        put(139, 10);
        put(445, 10);
    }};

    public static boolean checkPort(final String ipv4, final int port) {

        try {
            new Socket().connect(new InetSocketAddress(ipv4, port), 1000);
            return true;
        } catch (final IOException ignored) {
            return false;
        }
    }

    public static List<Integer> checkPorts(final String ipv4, final List<Integer> ports, final boolean reverse) {

        // Here reverse tells if we should return open ports (false) or closed ports (true).

        final ExecutorService executorService = Executors.newFixedThreadPool(ports.size());
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        // List of open ports we will return at the end
        final List<Integer> returnPorts = new ArrayList<>();

        ports.forEach(i -> {
            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {
                    new Socket().connect(new InetSocketAddress(ipv4, i), 1000);
                    if (!reverse) returnPorts.add(i);
                } catch (final IOException ignored) {
                    if (reverse) returnPorts.add(i);
                }
            }, executorService);
            futures.add(future);
        });
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdownNow();

        if (returnPorts.isEmpty()) return null;

        return returnPorts;
    }

    public static List<Integer> checkAllPorts(final String ipv4, final boolean reverse) {

        // Here reverse tells if we should return open ports (false) or closed ports (true).

        final ExecutorService executorService = Executors.newFixedThreadPool(49152);
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        // List of open ports we will return at the end
        final List<Integer> returnPorts = new ArrayList<>();

        for (int i = 0; i <= 49152; i++) {

            int finalI = i;

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {
                    new Socket().connect(new InetSocketAddress(ipv4, finalI), 1000);
                    if (!reverse) returnPorts.add(finalI);
                } catch (final IOException ignored) {
                    if (reverse) returnPorts.add(finalI);
                }
            }, executorService);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdownNow();

        if (returnPorts.isEmpty()) return null;

        return returnPorts;
    }
}
