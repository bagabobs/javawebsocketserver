package testwebsocketserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(8080);
        try {
            System.out.println("Server has started on 127.0.0.1. Waiting for Connection...");
            try(Socket client = server.accept()) {
                System.out.println("A Client Connected");
                try (Scanner scanner = new Scanner(client.getInputStream(), "UTF-8");
                     BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream())) {
                    String data = scanner.useDelimiter("\\r\\n\\r\\n").next();
                    Matcher get = Pattern.compile("^GET").matcher(data);
                    if (get.find()) {
                        Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                        match.find();
                        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n" +
                                "Connection: Upgrade\r\n" +
                                "Sec-WebSocket-Accept: " +
                                Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8"))) +
                                "\r\n\r\n").getBytes("UTF-8");
                        bos.write(response);
                        bos.flush();

                        data = scanner.useDelimiter("\\r\\n\\r\\n").next();
                        System.out.println(data);
                    }

                } catch (IOException ex) {
                    throw ex;
                }
            } catch(IOException ez) {
                throw ez;
            }
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
