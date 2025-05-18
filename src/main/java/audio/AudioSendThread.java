package audio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioSendThread extends Thread {

    private final DatagramSocket socket;
    private final AudioSetup audioSetup;

    public AudioSendThread(DatagramSocket socket, AudioSetup audioSetup) {
        this.socket = socket;
        this.audioSetup = audioSetup;
    }

    @Override
    public void run() {
        try {

            DatagramPacket pingPacket = new DatagramPacket(new byte[1], 1);
            socket.receive(pingPacket);

            InetAddress addr = pingPacket.getAddress();
            int port = pingPacket.getPort();

            byte[] audioBuf = new byte[4096];
            while (true) {
                int count = audioSetup.getMicrophone().read(audioBuf, 0, audioBuf.length);
                if (count > 0) {
                    ByteBuffer bb = ByteBuffer.allocate(Long.BYTES + Integer.BYTES + count)
                            .order(ByteOrder.BIG_ENDIAN);
                    bb.putLong(System.nanoTime());
                    bb.putInt(count);
                    bb.put(audioBuf, 0, count);
                    byte[] packetData = bb.array();

                    DatagramPacket packet = new DatagramPacket(
                            packetData, packetData.length, addr, port);
                    socket.send(packet);
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur dans l'envoi des packets audio: " + e.getMessage());
        }
    }
}
