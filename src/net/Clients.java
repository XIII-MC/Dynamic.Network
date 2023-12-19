package net;

import java.util.*;

public final class Clients extends Ports {

    public static List<String> getClientTypeByPorts(final String ipv4, final String hostname) {

        int pts_printer = 0, pts_nas = 0, pts_tv = 0, pts_server = 0, pts_wsServer = 0, pts_web = 0, pts_msClient = 0;

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

        for(Map.Entry<Integer, Integer> entry : ports_msClient.entrySet()) {

            if (checkPort(ipv4, entry.getKey())) pts_msClient += entry.getValue();
        }

        final List<String> tempPtsResults = Arrays.asList(pts_printer + "    | Printer score: " + pts_printer, pts_nas + "    | NAS score: " + pts_nas, pts_tv + "    | TV score: " + pts_tv, pts_server + "    | Server score: " + pts_server, pts_wsServer + "    | MS Server score: " + pts_wsServer, pts_web + "    | Web score: " + pts_web, pts_msClient + "    | MS Client score: " + pts_msClient);
        final List<String> ptsResults = new ArrayList<>();

        Collections.sort(ptsResults);

        for (final String result : tempPtsResults) {

            ptsResults.add(result.replaceAll("^.*(?=    )", ""));
        }

        return ptsResults;
    }
}
