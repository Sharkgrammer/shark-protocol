package util;

import java.net.Socket;

public interface ResultHandler{

    void messageReceived(String message,Socket socket, DataHolder data);

}