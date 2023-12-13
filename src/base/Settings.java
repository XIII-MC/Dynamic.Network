package base;

import utils.Colors;
import utils.Input;

import java.util.*;

public class Settings {


    // ANSI Color support
    public static boolean ANSI = false;

    // Maximum threads for a full network scan (CDIR /24) | Settings ID : 1
    public static int maxNetThreads = 256;

    // Maximum wait time for an answer from an ICMP ping | Settings ID : 2
    public static int maxICMPWait = 10000;

    // Identify host names of IP addresses
    public static boolean getHostName = false;

    public static void defineSettings(final Integer settingID) {

        // Settings list
        final Map<String, Map.Entry<Integer, String>> settingsList = new HashMap<String, Map.Entry<Integer, String>>()
        {{
            put("Exit", new SimpleEntry<>(0, "null"));
            put("maxNetThreads", new SimpleEntry<>(1, String.valueOf(maxNetThreads)));
            put("maxICMPWait", new SimpleEntry<>(2, String.valueOf(maxICMPWait)));
            put("getHostName", new SimpleEntry<>(3, String.valueOf(getHostName)));
        }};

        // Enumerate settings
        if (settingID == -1) {
            System.out.println(ANSI ? Colors.PURPLE + "[-]" + Colors.RESET + " | There currently is " + settingsList.size() + " available settings" : "[-] | There are currently " + settingsList.size() + " available settings");
            for (final Map.Entry<String, Map.Entry<Integer, String>> set : settingsList.entrySet()) {

                // Enumerate settings possibilities
                System.out.println(ANSI ? "    | " + Colors.GREEN + set.getValue().getValue() + Colors.RESET + " | Setting #" + set.getValue().getKey() + " '" + set.getKey() + "'" : "    | " + set.getValue().getValue() + " | Setting #" + set.getValue().getKey() + " '" + set.getKey() + "'");
            }

            defineSettings(Integer.parseInt(Input.getUserInput("    | Choose the setting you wanna change with the setting ID:")));
        }

        // Define maxNetThreads
        if (settingID == 1) {

            maxNetThreads = Integer.parseInt(Input.getUserInput(ANSI ? Colors.PURPLE + "[-]" + Colors.RESET + " | Enter the value for 'maxNetThreads':" : "[-] | Enter the value for 'maxNetThreads':"));
            System.out.println(ANSI ? Colors.GREEN + "[v]" + Colors.RESET + " | Defined 'maxNetThreads' to '" + maxNetThreads + "'" : "[v] | Defined 'maxNetThreads' to '" + maxNetThreads + "'");
            defineSettings(-1);
        }

        // Define maxNetThreads
        if (settingID == 2) {

            maxICMPWait = Integer.parseInt(Input.getUserInput(ANSI ? Colors.PURPLE + "[-]" + Colors.RESET + " | Enter the value for 'maxICMPWait':" : "[-] | Enter the value for 'maxICMPWait':"));
            System.out.println(ANSI ? Colors.GREEN + "[v]" + Colors.RESET + " | Defined 'maxICMPWait' to '" + maxICMPWait + "'" : "[v] | Defined 'maxICMPWait' to '" + maxICMPWait + "'");
            defineSettings(-1);
        }

        // Define maxNetThreads
        if (settingID == 3) {

            getHostName = Boolean.parseBoolean(Input.getUserInput(ANSI ? Colors.PURPLE + "[-]" + Colors.RESET + " | Enter the value for 'getHostName':" : "[-] | Enter the value for 'getHostName':"));
            System.out.println(ANSI ? Colors.GREEN + "[v]" + Colors.RESET + " | Defined 'getHostName' to '" + getHostName + "'" : "[v] | Defined 'getHostName' to '" + getHostName + "'");
            defineSettings(-1);
        }

        if (settingID == 0) {

            System.out.println(ANSI ? Colors.GREEN + "[v]" + Colors.RESET + " | Settings saved, exiting..." : "[v] | Settings saved, exiting...");
        }
    }
}
