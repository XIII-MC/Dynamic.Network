import net.NetClients;
import net.NetScan;
import options.Settings;
import utils.ColorsUtils;
import utils.InputUtils;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.*;

public class Main extends Settings {

    public static void main(final String[] args) throws IOException {

        if (System.getProperty("os.name").startsWith("Windows")) {

            // Ask if we should enable ANSI support or not
            final String selectedInterfaceID = InputUtils.getUserInput("[?] | Do you wanna enable ANSI support for Windows ?" + "\n" + "    | It could improve the visual aspect of the outputs but might also break the output" + "\n" + "    | You choose! (Y/N):");

            // Temporarily enable ANSI support for Windows
            if (selectedInterfaceID.contains("y")) {
                ColorsUtils.enableWindows10AnsiSupport();
                System.out.println(ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | ANSI Color support enabled!");
                ANSI = true;
            }
            else System.out.println("[v] | ANSI Color support disabled.");
        }

        defineSettings(-1);

        // Prepare to enumerate net interfaces
        final List<String> networkInterfaces = new ArrayList<>();
        System.out.println(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Running network interfaces lookup..." : "[-] | Running network interfaces lookup...");

        // Grab and list net interfaces
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        // Loop through all interfaces and add them
        int interfacesSize = 0;
        while(interfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.getInterfaceAddresses().isEmpty()) {
                networkInterfaces.add("    | #" + networkInterfaces.size() + " | '" + networkInterface.getDisplayName() + "' | '" + networkInterface.getInterfaceAddresses().get(0) + "'");
                interfacesSize++;
            }
        }

        if (interfacesSize == 0) {
            System.out.println(ANSI ? ColorsUtils.RED + "[x] " + ColorsUtils.RESET + " | No network interface detected, are they enabled?" : "[x] | No network interface detected, are they enabled?");
            return;
        } else System.out.println(ANSI ? ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | Found " + interfacesSize + " network interfaces." : "[v] | Found " + interfacesSize + " network interfaces.");

        // Print net interfaces
        networkInterfaces.forEach(System.out::println);

        // Ask which net interface to use (according to the arraylist index)
        final int interfaceID = Integer.parseInt(InputUtils.getUserInput("    | Please select the network interface's ID below:"));

        // Extract the IP from the interface name + IP output
        final String interfaceIP = networkInterfaces.get(interfaceID).replaceAll(".*?(?<='/)", "").replaceAll("\\[.*", "").replaceAll("/.*", "");

        // Logging
        System.out.println(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Running network scan on '" + interfaceIP + "', please wait..." : "[-] | Running network scan on '" + interfaceIP + "', please wait...");

        // Net scan
        // Make a hashmap in order to be able to identify
        final HashMap<Integer, String> netID = new HashMap<>();
        final HashMap<String, String> scanResults = NetScan.getNetworkIPs(interfaceIP, 24, false);

        for (final Map.Entry<String, String> set : scanResults.entrySet()) {

            System.out.println(ANSI ? "    | #" + netID.size() + " | " + (set.getValue().equals("N/A") ? ColorsUtils.YELLOW : ColorsUtils.GREEN) + "[C] v<--->v [H]" + ColorsUtils.RESET + " | Reached host '" + set.getKey() + "' ! (" + set.getValue() + ")" : "    | #" + netID.size() + " | " + "[C] v<--->v [H]" + " | Reached host '" + set.getKey() + "' ! (" + set.getValue() + ")");
            netID.put(netID.size(), set.getKey());
        }

        System.out.println(ANSI ? "    | " + ColorsUtils.RED + "[C] v<-x->? [H]" + ColorsUtils.RESET + " | We couldn't reach other hosts :(" : "    | [C] v<-x->? [H] | We couldn't reach other hosts :(");

        // Identify clients function
        int selectedNetID = 0;

        while (selectedNetID != -1) {
            selectedNetID = Integer.parseInt(InputUtils.getUserInput(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Please select the host you would like to identify by entering it's net ID:" : "[-] | Please select the host you would like to identify by entering it's net ID:"));

            for (final String score : NetClients.getClientTypeByPorts(netID.get(selectedNetID))) {

                System.out.println(score);
            }
        }
    }
}