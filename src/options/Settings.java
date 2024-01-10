package options;

import utils.ColorsUtils;
import utils.InputUtils;

import java.util.HashMap;
import java.util.Map;

public class Settings {

    // ANSI Color support
    public static boolean ANSI = false;

    // Identify host names of IP addresses
    public static boolean getHostName = true;

    // Wait ARP cache sync (takes ages)
    public static boolean waitARPSync = true;

    // Use real CMD's ping command or a faster way around (might miss hosts)
    public static boolean useCmdPing = true;

    public static void defineSettings(final Integer settingID) {

        // Settings list
        final Map<String, Map.Entry<Integer, String>> settingsList = new HashMap<String, Map.Entry<Integer, String>>()
        {{
            put("Exit", new SimpleEntry<>(0, "null"));
            put("getHostName", new SimpleEntry<>(1, String.valueOf(getHostName)));
            put("waitARPSync", new SimpleEntry<>(2, String.valueOf(waitARPSync)));
            put("useCmdPing", new SimpleEntry<>(3, String.valueOf(useCmdPing)));
        }};

        // Enumerate settings
        if (settingID == -1) {

            System.out.println(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | There currently are " + settingsList.size() + " available settings" : "[-] | There currently are " + settingsList.size() + " available settings");

            // Since for whatever reason the hashmap is never in order I have to manually put it in order
            for (final Map.Entry<String, Map.Entry<Integer, String>> set : settingsList.entrySet()) {

                // Enumerate settings possibilities
                System.out.println(ANSI ? "    | #" + set.getValue().getKey() + " | '" + set.getKey() + "' is set to '" + ColorsUtils.GREEN + set.getValue().getValue() + ColorsUtils.RESET + "'" : "    | #" + set.getValue().getKey() + " | '" + set.getKey() + "' is set to '" + set.getValue().getValue() + "'");
            }

            defineSettings(Integer.parseInt(InputUtils.getUserInput("    | Choose the setting you wanna change with the setting ID:")));
        }

        // Define maxNetThreads
        if (settingID == 1) {

            getHostName = Boolean.parseBoolean(InputUtils.getUserInput(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Do you want to get hosts names ? (PTR/Reverse DNS). This will be slower. (True/False):" : "[-] | Do you want to get hosts names ? (PTR/Reverse DNS). This will be slower. (True/False):"));
            System.out.println(ANSI ? ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | You set the get hosts names feature to " + getHostName : "[v] | You set the get hosts names feature to " + getHostName);
            defineSettings(-1);
        }

        if (settingID == 2) {

            waitARPSync = Boolean.parseBoolean(InputUtils.getUserInput(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Do you want to add to delay to wait for ARP to add the hosts to it's table ? (Takes about +5s) (True/False):" : "[-] | Do you want to add to delay to wait for ARP to add the hosts to it's table ? (Takes about +5s) (True/False):"));
            System.out.println(ANSI ? ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | You set the wait ARP sync feature to " + waitARPSync : "[v] | You set the wait ARP sync feature to " + waitARPSync);
            defineSettings(-1);
        }

        if (settingID == 3) {

            useCmdPing = Boolean.parseBoolean(InputUtils.getUserInput(ANSI ? ColorsUtils.PURPLE + "[-]" + ColorsUtils.RESET + " | Do you want to use CMD's ping command (slower and more resource hungry) or a faster method (can miss hosts) ? (True/False):" : "[-] | Do you want to use CMD's ping command (slower and more resource hungry) or a faster method (can miss hosts) ? (True/False):"));
            System.out.println(ANSI ? ColorsUtils.GREEN + "[v]" + ColorsUtils.RESET + " | You set the wait ARP sync feature to " + useCmdPing : "[v] | You set the use CMD's ping feature to " + useCmdPing);
            defineSettings(-1);
        }
    }
}
