import base.Settings;
import net.Clients;
import net.Scan;
import utils.Colors;
import utils.Input;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class Main extends Settings {

    public static void main(final String[] args) throws IOException {

        Clients.getClientTypeByPorts("192.168.1.254", "null");

        if (System.getProperty("os.name").startsWith("Windows")) {

            // Ask if we should enable ANSI support or not
            final String selectedInterfaceID = Input.getUserInput("[?] | Do you wanna enable ANSI support for Windows ?" + "\n" + "    | It could improve the visual aspect of the outputs but might also break the output" + "\n" + "    | You choose! (Y/N):");

            // Temporarily enable ANSI support for Windows
            if (selectedInterfaceID.contains("y")) {
                Colors.enableWindows10AnsiSupport();
                System.out.println(Colors.GREEN + "[v]" + Colors.RESET + " | ANSI Color support enabled!");
                ANSI = true;
            }
            else System.out.println("[v] | ANSI Color support disabled.");
        }

        defineSettings(-1);

        // Prepare to enumerate net interfaces
        final List<String> networkInterfaces = new ArrayList<>();
        System.out.println(ANSI ? Colors.PURPLE + "[-]" + Colors.RESET + " | Running network interfaces lookup..." : "[-] | Running network interfaces lookup...");

        // Grab and list net interfaces
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        // Loop through all interfaces and add them
        int interfacesSize = 0;
        while(interfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.getInterfaceAddresses().isEmpty()) {
                networkInterfaces.add("    | Interface #" + networkInterfaces.size() + " : '" + networkInterface.getDisplayName() + "' | '" + networkInterface.getInterfaceAddresses().get(0) + "'");
                interfacesSize++;
            }
        }

        if (interfacesSize == 0) System.out.println(ANSI ? Colors.GREEN + "[x] " + Colors.RESET + " | No network interface detected, are they enabled?" : "[x] | No network interface detected, are they enabled?");
        else System.out.println(ANSI ? Colors.GREEN + "[v]" + Colors.RESET + " | Found " + interfacesSize + " network interfaces." : "[v] | Found " + interfacesSize + " network interfaces.");

        // Print net interfaces
        networkInterfaces.forEach(System.out::println);

        // Ask which net interface to use (according to the arraylist index)
        final int interfaceID = Integer.parseInt(Input.getUserInput("    | Please select the network interface's ID below:"));

        // Extract the IP from the interface name + IP output
        final String interfaceIP = networkInterfaces.get(interfaceID).replaceAll(".*?(?<='/)", "").replaceAll("\\[.*", "").replaceAll("/.*", "");

        // Logging
        System.out.println(ANSI ? Colors.PURPLE + "[-]" + Colors.RESET + " | Running network scan on '" + interfaceIP + "', please wait..." : "[-] | Running network scan on '" + interfaceIP + "', please wait...");

        // Net scan
        for (final Map.Entry<String, String> set : Scan.getNetworkIPs(interfaceIP, 24, false).entrySet()) {
            // Dynamic net state
            final String netState = set.getValue().equals("N/A") ? "[C] v<-?->v [H]" :  "[C] v<--->v [H]";

            System.out.println(ANSI ? "    | " + (netState.contains("?") ? Colors.YELLOW : Colors.GREEN) + netState + Colors.RESET + " | Reached host '" + set.getKey() + "' ! (" + set.getValue() + ")" : "    | " + netState + " | Reached host '" + set.getKey() + "' ! (" + set.getValue() + ")");
        }

        System.out.println(ANSI ? "    | " + Colors.RED + "[C] v<-x->? [H]" + Colors.RESET + " | We couldn't reach other hosts :(" : "    | [C] v<-x->? [H] | We couldn't reach other hosts :(");
    }
}