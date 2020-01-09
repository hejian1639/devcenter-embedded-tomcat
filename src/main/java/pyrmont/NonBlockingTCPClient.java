package pyrmont;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NonBlockingTCPClient {
    public static void main(String[] args) {
        byte[] data = "hello".getBytes();
        SocketChannel channel = null;
        try {
            // 1. open a socket channel
            channel = SocketChannel.open();
            // adjust to be nonblocking
            channel.configureBlocking(false);
            // 2. init connection to server and repeatedly poll with complete
            // connect() and finishConnect() are nonblocking operation, both return immediately
            if (!channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8899))) {
                while (!channel.finishConnect()) {
                    System.out.print(".");
                }
            }

            System.out.println("Connected to server...");

            ByteBuffer writeBuffer = ByteBuffer.wrap(data);
            ByteBuffer readBuffer = ByteBuffer.allocate(data.length);
            int totalBytesReceived = 0;
            int bytesReceived;
            // 3. read and write bytes
            while (totalBytesReceived < data.length) {
                if (writeBuffer.hasRemaining()) {
                    channel.write(writeBuffer);
                }
                if ((bytesReceived = channel.read(readBuffer)) == -1) {
                    throw new SocketException("Connection closed prematurely");
                }
                totalBytesReceived += bytesReceived;
//                System.out.print(".");
            }
            System.out.println("Server said: " + new String(readBuffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 4 .close socket channel
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}