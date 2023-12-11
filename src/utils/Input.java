package utils;

import java.util.Scanner;

public final class Input {

    public static String getUserInput(final String question) {
        final Scanner scannerIntID = new Scanner(System.in);
        System.out.println(question);
        return (scannerIntID.nextLine());
    }
}
