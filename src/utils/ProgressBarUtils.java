package utils;

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
        System.out.print("\r" + bareDone + bareRemain + " " + remainProcent + "%");
        if (remain == total) {
            System.out.print("\r");
        }
    }
}
