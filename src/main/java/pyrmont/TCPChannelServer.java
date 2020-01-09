package pyrmont;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPChannelServer {
    public static void main(String[] args) {
        Selector selector = null;
        try {
            // 1. open a selector
            selector = Selector.open();
            // 2. listen for server socket channel
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // must to be nonblocking mode before register
            ssc.configureBlocking(false);
            // bind server socket channel to port 8899
            ssc.bind(new InetSocketAddress(8899));
            // 3. register it with selector
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (true) { // run forever
                // 4. select ready SelectionKey for I/O operation
                selector.select();
                // 5. get selected keys
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                // 6. handle selected key's interest operations
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        // get socket channel from server socket channel
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        // must to be nonblocking before register with selector
                        clientChannel.configureBlocking(false);
                        // register socket channel to selector with OP_READ
                        clientChannel.register(key.selector(), SelectionKey.OP_READ);
                    }

                    if (key.isValid() && key.isReadable()) {
                        // read bytes from socket channel to byte buffer
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer readBuffer = ByteBuffer.allocate(20);
                        int readBytes = clientChannel.read(readBuffer);
                        if (readBytes == -1) {
                            System.out.println("closed.......");
                            clientChannel.close();
                        } else if (readBytes > 0) {
                            String s = new String(readBuffer.array(), readBuffer.arrayOffset());
                            System.out.println("Client said: " + s);
                            // attachment is content used to write
                            key.interestOps(SelectionKey.OP_WRITE);
                            key.attach("Welcome!!!");
                        }

                    }

                    if (key.isValid() && key.isWritable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        // get content from attachment
                        String content = (String) key.attachment();
                        // write content to socket channel
                        clientChannel.write(ByteBuffer.wrap(content.getBytes()));
                        key.interestOps(SelectionKey.OP_READ);
                    }

                    // remove handled key from selected keys
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close selector
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}