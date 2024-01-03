package options;

import utils.ColorsUtils;
import utils.InputUtils;

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

    // Real ICMP mode
    public static boolean realICMPMode = false;

    public static void defineSettings(final Integer settingID) {

        // Settings list
        final Map<String, Map.Entry<Integer, String>> settingsList = new HashMap<String, Map.Entry<Integer, String>>()
        {{
            put("Exit", new SimpleEntry<>(0, "null"));
            put("maxNetThreads", new SimpleEntry<>(1, String.valueOf(maxNetThreads)));
            put("maxICMPWait", new SimpleEntry<>(2, String.valueOf(maxICMPWait)));
            put("getHostName", new SimpleEntry<>(3, String.valueOf(getHostName)));
            put("realICMPMode", new SimpleEntry<>(4, String.valueOf(realICMPMode)));
        }};

        // Enumerate settings
        if (settingID == -1) {

            System.out.println(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | There currently is " + settingsList.size() + " available settings" : "[-] | There are currently " + settingsList.size() + " available settings");

            // Since for whatever reason the hashmap is never in order I have to manually put it in order
            for (final Map.Entry<String, Map.Entry<Integer, String>> set : settingsList.entrySet()) {

                // Enumerate settings possibilities
                System.out.println(ANSI ? "    | #" + set.getValue().getKey() + " | '" + set.getKey() + "' is set @ | '" + ColorsUtils.GREEN + set.getValue().getValue() + ColorsUtils.RESET + "'" : "    | #" + set.getValue().getKey() + " | '" + set.getKey() + "' is set @ | '" + set.getValue().getValue() + "'");
            }

            defineSettings(Integer.parseInt(InputUtils.getUserInput("    | Choose the setting you wanna change with the setting ID:")));
        }

        // Define maxNetThreads
        if (settingID == 1) {

            maxNetThreads = Integer.parseInt(InputUtils.getUserInput(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Enter the value for 'maxNetThreads':" : "[-] | Enter the value for 'maxNetThreads':"));
            System.out.println(ANSI ? ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | Defined 'maxNetThreads' to '" + maxNetThreads + "'" : "[v] | Defined 'maxNetThreads' to '" + maxNetThreads + "'");
            defineSettings(-1);
        }

        // Define maxNetThreads
        if (settingID == 2) {

            maxICMPWait = Integer.parseInt(InputUtils.getUserInput(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Enter the value for 'maxICMPWait':" : "[-] | Enter the value for 'maxICMPWait':"));
            System.out.println(ANSI ? ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | Defined 'maxICMPWait' to '" + maxICMPWait + "'" : "[v] | Defined 'maxICMPWait' to '" + maxICMPWait + "'");
            defineSettings(-1);
        }

        // Define maxNetThreads
        if (settingID == 3) {

            getHostName = Boolean.parseBoolean(InputUtils.getUserInput(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Enter the value for 'getHostName':" : "[-] | Enter the value for 'getHostName':"));
            System.out.println(ANSI ? ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | Defined 'getHostName' to '" + getHostName + "'" : "[v] | Defined 'getHostName' to '" + getHostName + "'");
            defineSettings(-1);
        }

        if (settingID == 0) {

            System.out.println(ANSI ? ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | Settings saved, exiting..." : "[v] | Settings saved, exiting...");
        }
    }
}
