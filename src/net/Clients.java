package net;

import java.util.Map;

public final class Clients extends Ports {

    public static void getClientTypeByPorts(final String ipv4, final String hostname) {

        if (hostname.contains("DESKTOP")) {
            System.out.println("[PTS] Machine identified as a Windows desktop.");
            return;
        }

        int pts_printer = 0, pts_nas = 0, pts_tv = 0, pts_server = 0, pts_wsServer = 0, pts_web = 0;

        for(Map.Entry<Integer, Integer> entry : ports_printer.entrySet()) {

            if (checkPort(ipv4, entry.getKey())) pts_printer += entry.getValue();
        }

        for(Map.Entry<Integer, Integer> entry : ports_nas.entrySet()) {

            if (checkPort(ipv4, entry.getKey())) pts_nas += entry.getValue();
        }

        for(Map.Entry<Integer, Integer> entry : ports_tv.entrySet()) {

            if (checkPort(ipv4, entry.getKey())) pts_tv += entry.getValue();
        }

        for(Map.Entry<Integer, Integer> entry : ports_server.entrySet()) {

            if (checkPort(ipv4, entry.getKey())) pts_server += entry.getValue();
        }

        for(Map.Entry<Integer, Integer> entry : ports_wsServ.entrySet()) {

            if (checkPort(ipv4, entry.getKey())) pts_wsServer += entry.getValue();
        }

        for(Map.Entry<Integer, Integer> entry : ports_web.entrySet()) {

            if (checkPort(ipv4, entry.getKey())) pts_web += entry.getValue();
        }

        System.out.println("[PTS] Printer Score: " + pts_printer);
        System.out.println("[PTS] NAS Score: " + pts_nas);
        System.out.println("[PTS] TV Score: " + pts_tv);
        System.out.println("[PTS] Server Score: " + pts_server);
        System.out.println("[PTS] WSServ Score: " + pts_wsServer);
        System.out.println("[PTS] Web Score: " + pts_web);
    }
}
