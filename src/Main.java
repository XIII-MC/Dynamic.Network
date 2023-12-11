import net.Clients;
import net.Ports;
import net.Scan;
import utils.Colors;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.*;

public class Main extends Ports {

    public static boolean ANSI = false;

    public static void main(String[] args) throws IOException {

        final String ANSISupport = "[?] | Do you wanna enable ANSI support for Windows ?" + "\n" + "    | It could improve the visual aspect of the outputs but might also break the output" + "\n" + "    | You choose! (Y/N):";

        if (System.getProperty("os.name").startsWith("Windows")) {

            // Ask if we should enable ANSI support or not
            final Scanner scannerIntID = new Scanner(System.in);
            System.out.println(ANSISupport);
            final String answer = String.valueOf(scannerIntID.nextLine());

            // Temporarily enable ANSI support for Windows
            if (answer.contains("y")) {
                Colors.enableWindows10AnsiSupport();
                System.out.println(Colors.GREEN + "[v]" + Colors.RESET + " | ANSI Color support enabled!");
                ANSI = true;
            }
            else System.out.println("[v] | ANSI Color support disabled.");
        }

        // Prepare to enumerate net interfaces
        final List<String> networkInterfaces = new ArrayList<>();
        System.out.println(ANSI ? Colors.PURPLE + "[-]" + Colors.RESET + " | Running network interfaces lookup..." : "[-] | Running network interfaces lookup...");

        // Grab and list net interfaces
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        //if (Collections.list(interfaces).isEmpty()) {
        //    System.out.println(ANSI ? Colors.RED + "[x]" + Colors.RESET + " | No network interface detected, are they enabled?" : "[x] | No network interface detected, are they enabled?");
        //    return;
        //} else System.out.println(ANSI ? Colors.GREEN + "[v]" + Colors.RESET + " | Found " + Collections.list(interfaces).size() + " network interfaces." : "[v] | Found " + Collections.list(interfaces).size() + " network interfaces.");

        while(interfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.getInterfaceAddresses().isEmpty()) networkInterfaces.add("    | Interface #" + networkInterfaces.size() + " : '" + networkInterface.getDisplayName() + "' | '" + networkInterface.getInterfaceAddresses().get(0) + "'");
        }

        // Print net interfaces
        networkInterfaces.forEach(System.out::println);

        // Ask which net interface to use (according to the arraylist index)
        final Scanner scannerIntID = new Scanner(System.in);
        System.out.println("    | Please select the network interface's ID below:");
        final int interfaceID = Integer.parseInt(scannerIntID.nextLine());

        // Extract the IP from the interface name + IP output
        final String interfaceIP = networkInterfaces.get(interfaceID).replaceAll(".*?(?<='/)", "").replaceAll("\\[.*", "").replaceAll("/.*", "");

        // Logging
        System.out.println(ANSI ? Colors.PURPLE + "[-]" + Colors.RESET + " | Running network scan on '" + interfaceIP + "', please wait..." : "[-] | Running network scan on '" + interfaceIP + "', please wait...");

        // Start net scan
        for (final String reachedIP : Scan.getNetworkIPs(interfaceIP, 24, false)) {
            System.out.println(ANSI ? "    | " + Colors.GREEN + "[C] v<--->v [H]" + Colors.RESET + " | Reached host '" + reachedIP + "' !" : "    | [C] v<--->v [H] | Reached host '" + reachedIP + "' !");
        }

        System.out.println(ANSI ? "    | " + Colors.RED + "[C] v<-x->? [H]" + Colors.RESET + " | We couldn't reach other hosts :(" : "    | [C] v<-x->? [H] | We couldn't reach other hosts :(");
    }
}