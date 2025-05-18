package audio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AudioReceiveThread extends Thread {

    private final DatagramSocket socket;
    private final AudioSetup audioSetup;
    private final String ip;
    private final int port;

    public AudioReceiveThread(DatagramSocket socket, AudioSetup audioSetup, String ip, int port) {
        this.socket = socket;
        this.audioSetup = audioSetup;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {

        try {
            InetAddress peerAddr = InetAddress.getByName(ip);

            ScheduledExecutorService ka = Executors.newSingleThreadScheduledExecutor();
            ka.scheduleAtFixedRate(() -> {
                try {
                    socket.send(new DatagramPacket(new byte[1], 1, peerAddr, port));
                } catch (IOException ignored) {
                }
            }, 0, 5, TimeUnit.SECONDS);
            byte[] buf = new byte[4096 + Long.BYTES + Integer.BYTES];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (true) {
                socket.receive(packet);
                ByteBuffer bb = ByteBuffer.wrap(packet.getData(), 0, packet.getLength())
                        .order(ByteOrder.BIG_ENDIAN);

                if (bb.get() == 0x00)
                    continue;

                bb.getLong();
                int len = bb.getInt();
                
                byte[] pcm = new byte[len];
                bb.get(pcm);

                audioSetup.getSpeakers().write(pcm, 0, len);
            }
        } catch (IOException e) {
            System.out.println("Erreur dans la réception des packets audio: " + e.getMessage());
        }
    }
}
