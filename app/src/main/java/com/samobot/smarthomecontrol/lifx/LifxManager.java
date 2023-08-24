package com.samobot.smarthomecontrol.lifx;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LifxManager {

    static int port = 56700;
    DatagramSocket sock;

    public LifxManager() throws SocketException {
        sock = new DatagramSocket(port);
    }

    public void close() { sock.close(); }

    public void setLightState(InetAddress address, boolean state) {
        SendPacketThread packetThread = new SendPacketThread(LifxPackets.setStatePacket(address, port, state));
        packetThread.start();
    }

    public void setLightHSBK(InetAddress address, int hue, int saturation, int brightness, int kelvin) {
        SendPacketThread packetThread = new SendPacketThread(LifxPackets.changeColorPacket(address, port, hue, saturation, brightness, kelvin));
        packetThread.start();
    }

    public void discoverLights(Activity activity, DiscoverLightRunnable runWhenDone) {
        DiscoverLightsThread discoverLightsThread = new DiscoverLightsThread(activity, runWhenDone);
        discoverLightsThread.start();
    }

    public void getLightName(Activity activity, InetAddress address, boolean joinThread, final ReceivedStringRunnable runWhenDone) {
        SendPacketThread sendPacketThread;
        try {
            sendPacketThread = new SendPacketThread(LifxPackets.getLabelPacket(address, port, getLocalIPAddress(activity)), new ReceivedPacketRunnable() {
                @Override
                public void run(DatagramPacket returnPacket) {
                    runWhenDone.run(new String(Arrays.copyOfRange(returnPacket.getData(), 36, returnPacket.getLength())).trim());
                }
            }, new DatagramPacket(new byte[68], 68));
            sendPacketThread.start();
            if(joinThread) {
                sendPacketThread.join();
            }
        } catch (UnknownHostException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class DiscoverLightsThread extends Thread {

        Activity activity;
        DiscoverLightRunnable runWhenDone;
        public List<DatagramPacket> recievedResponses = new ArrayList<>();

        DiscoverLightsThread(Activity activity, DiscoverLightRunnable runWhenDone) {this.activity = activity; this.runWhenDone = runWhenDone;}

        @Override
        public void run() {
            try {
                byte[] localIP = getLocalIPAddress(activity);
                if(Build.FINGERPRINT.contains("generic")) {
                    localIP = new byte[]{(byte) 192, (byte) 168, 1, 15};
                }
                System.out.println(InetAddress.getByAddress(localIP).getHostAddress());
                InetAddress broadcastAddr = InetAddress.getByAddress(new byte[]{localIP[0], localIP[1], localIP[2], (byte) 0xFF});
                DatagramPacket packet = LifxPackets.discoveryPacket(broadcastAddr, port, localIP);
                sock.send(packet);
                System.out.println("Zero Seconds");
                long time = System.nanoTime();
                while(System.nanoTime() - time < 30000000000L) {
                    DatagramPacket sampleReplyPacket = new DatagramPacket(new byte[41], 41);
                    sock.setSoTimeout(10000);
                    try {
                        sock.receive(sampleReplyPacket);
                        if(sampleReplyPacket.getAddress() != null && !sampleReplyPacket.getAddress().getHostAddress().equals(InetAddress.getByAddress(localIP).getHostAddress())) {
                            recievedResponses.add(sampleReplyPacket);
                        }
                    } catch(SocketTimeoutException e) {
                        e.printStackTrace();
                    }
                }
                runWhenDone.run(activity, recievedResponses);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public interface DiscoverLightRunnable {
        void run(Activity activity, List<DatagramPacket> recievedResponses);
    }

    private class SendPacketThread extends Thread {

        DatagramPacket packet;
        ReceivedPacketRunnable receivedPacketRunnable;
        public DatagramPacket replyPacket;
        boolean responseRequired = false;

        SendPacketThread(DatagramPacket packet) {
            this.packet = packet;
        }

        SendPacketThread(DatagramPacket packet, ReceivedPacketRunnable runWhenDone, DatagramPacket sampleReplyPacket) {
            this.packet = packet;
            this.receivedPacketRunnable = runWhenDone;
            this.replyPacket = sampleReplyPacket;
            responseRequired = true;
        }

        @Override
        public void run() {
            try {
                sock.send(packet);
                if(responseRequired) {
                    sock.receive(replyPacket);
                    if(packet.equals(replyPacket)) {
                        sock.receive(replyPacket);
                    }
                    receivedPacketRunnable.run(replyPacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ReceivedPacketRunnable {
        void run(DatagramPacket returnPacket);
    }

    public interface ReceivedStringRunnable {
        void run(String returnString);
    }

    private static byte[] getLocalIPAddress(Activity activity) throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Activity.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return InetAddress.getByName(ip).getAddress();
    }

}
