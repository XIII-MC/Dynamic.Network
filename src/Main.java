import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public static void main(String[] args) {

        getNetworkIPs();

    }

    private static void getNetworkIPs() {

        try {

            final byte[] ip = InetAddress.getLocalHost().getAddress();

            for (int i = 1; i <= 254; i++) {

                final int j = i;

                executorService.execute(() -> {
                    try {

                        ip[3] = (byte) j;
                        final InetAddress address = InetAddress.getByAddress(ip);
                        final String convIP = address.toString().substring(1);

                        if (address.isReachable(5000)) System.out.println("Host '" + convIP + "' is reachable.");
                    } catch (final IOException ignored) {}
                });
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}