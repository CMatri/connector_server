package com.cmeet.remoter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.cmeet.remoter.packet.Packet;
import com.cmeet.remoter.util.LIFOList;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server {
    private MainActivity activity;
    private ServerSocket serverSocket;
    private SocketServerThread socketServerThread;
    private boolean running = false;
    private boolean foundClient = false;
    static final int socketServerPORT = 5515;
    static final int socketServerDiscoveryPORT = 5514;

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        kill();
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!foundClient) {
                    try {
                        DatagramSocket clientSocket = new DatagramSocket();

                        clientSocket.setBroadcast(true);
                        InetAddress address = getBroadcastAddress();

                        byte[] sendData;

                        sendData = ("\01\02\03" + socketServerPORT).getBytes();

                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, socketServerDiscoveryPORT);
                        clientSocket.send(sendPacket);
                        clientSocket.close();
                        sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        thread.start();
        running = true;
        socketServerThread = new SocketServerThread();
        Thread serverThread = new Thread(socketServerThread);
        serverThread.start();
    }

    public void packet(Packet p) {
        if (socketServerThread != null && socketServerThread.socketServerReplyThread != null)
            socketServerThread.socketServerReplyThread.packet(p);
    }

    public void kill() {
        running = false;
        foundClient = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    private class SocketServerThread extends Thread {
        SocketServerReplyThread socketServerReplyThread;

        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.msg.setText("Server starting.");
                }
            });

            try {
                serverSocket = new ServerSocket(socketServerPORT);
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.msg.setText("Server started.");
                    }
                });

                while (running) {
                    Socket socket = serverSocket.accept();
                    socketServerReplyThread = new SocketServerReplyThread(socket);
                    socketServerReplyThread.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.msg.setText("Server stopped.");
                }
            });
        }
    }

    public static class MousePacketHandler {
        public static LIFOList<Packet> stack = new LIFOList<>();
    }

    private class SocketServerReplyThread extends Thread {
        private Socket hostThreadSocket;

        SocketServerReplyThread(Socket socket) {
            hostThreadSocket = socket;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "pong";
            foundClient = true;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.msg.setText("Established connection");
                    }
                });

                String line = "";
                while (running && !line.equals("exit")) {
                    while (!MousePacketHandler.stack.isEmpty()) {
                        Packet pkt = MousePacketHandler.stack.pop();
                        if (pkt != null) {
                            printStream.write(pkt.getData());
                        }
                    }
                }

                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void packet(Packet p) {
            MousePacketHandler.stack.push(p);
        }
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server will run at : " + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}