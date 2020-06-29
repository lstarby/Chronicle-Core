package net.openhft.chronicle.core.tcp;

import java.net.SocketAddress;
import java.net.SocketException;

public interface ChronicleServerSocket {
    int getLocalPort();

    void close();

    void setReuseAddress(boolean b) throws SocketException;

    SocketAddress getLocalSocketAddress();
}
