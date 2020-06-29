package net.openhft.chronicle.core.tcp.factory;

import net.openhft.chronicle.core.tcp.ChronicleSocket;
import net.openhft.chronicle.core.tcp.ChronicleSocketChannel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public enum ChronicleSocketChannelFactory {
    ;

    public static ChronicleSocketChannel open() throws IOException {
        return newInstance(SocketChannel.open());
    }

    public static ChronicleSocketChannel open(InetSocketAddress socketAddress) throws IOException {
        return newInstance(SocketChannel.open(socketAddress));
    }

    static ChronicleSocketChannel open(final SocketChannel sc) {
        return newInstance(sc);
    }

    @NotNull
    private static ChronicleSocketChannel newInstance(final SocketChannel sc) {
        return new ChronicleSocketChannel() {
            @Override
            public boolean isClosed() {
                throw new UnsupportedOperationException("todo");
            }

            @Override
            public void close() {
                throw new UnsupportedOperationException("todo");
            }

            @Override
            public int read(final ByteBuffer byteBuffer) throws IOException {
                return sc.read(byteBuffer);
            }

            @Override
            public int write(final ByteBuffer byteBuffer) throws IOException {
                return sc.write(byteBuffer);
            }

            @Override
            public long write(final ByteBuffer[] byteBuffers) throws IOException {
                return sc.write(byteBuffers);
            }

            @Override
            public void configureBlocking(final boolean blocking) throws IOException {
                sc.configureBlocking(blocking);
            }

            @Override
            public InetSocketAddress getLocalAddress() throws IOException {
                return (InetSocketAddress) sc.getLocalAddress();
            }

            @Override
            public InetSocketAddress getRemoteAddress() throws IOException {
                return (InetSocketAddress) sc.getRemoteAddress();
            }

            @Override
            public boolean isOpen() {
                return sc.isOpen();
            }

            @Override
            public boolean isBlocking() {
                return sc.isBlocking();
            }

            @Override
            public ChronicleSocket socket() {
                return ChronicleSocketFactory.open(sc.socket());
            }

            @Override
            public void connect(final InetSocketAddress socketAddress) throws IOException {
                sc.connect(socketAddress);
            }

            @Override
            public void register(final Selector selector, final int opConnect) throws ClosedChannelException {
                sc.register(selector, opConnect);
            }

            @Override
            public boolean finishConnect() throws IOException {
                return sc.finishConnect();
            }

            @Override
            public void setOption(final SocketOption<Boolean> soReuseaddr, final boolean b) throws IOException {
                sc.setOption(soReuseaddr, b);
            }

        };
    }

}
