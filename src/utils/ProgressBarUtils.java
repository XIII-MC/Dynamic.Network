package utils;

import net.NetScan;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class ProgressBarUtils {

    public static void progressPercentage(int remain, int total) {
        if (remain > total) {
            throw new IllegalArgumentException();
        }
        int maxBareSize = 100; // 10unit for 100%
        int remainProcent = ((10000 * remain) / total) / maxBareSize;
        char defaultChar = '.';
        String icon = "|";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("[");
        for (int i = 0; i < remainProcent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring(remainProcent, bare.length());
        System.out.print("\r" + bareDone + bareRemain + " " + remainProcent + "% | Alive hosts: " + NetScan.aliveHost + " | Dead Hosts: " + NetScan.deadHost + " | Unknown Hosts: " + NetScan.unknownHost);
        if (remain == total) {
            System.out.print("\r");
        }
    }

    public static void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 :
                (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string
                .append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies(current == 0 ? (int) (Math.log10(total)) : (int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        if (percent == 100) {
            System.out.print("\r");
        } else System.out.print(string);
    }
}
