package com.samobot.smarthomecontrol.lifx;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class LifxPackets {

    static DatagramPacket changeColorPacket(InetAddress address, int port, int color, int saturation, int brightness, int kelvin) {

        byte[] packetData = {49, 0, 0, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 0, 0, 0, 0, 85, 85, -1, -1, 85, 85, -84, 13, 0, 4, 0, 0};

        packetData[37] = (byte) (color & 0xFF); //color byte 1
        packetData[38] = (byte) ((color >> 8) & 0xFF); //color byte 2
        packetData[39] = (byte) (saturation & 0xFF); //saturation byte 1
        packetData[40] = (byte) ((saturation >> 8) & 0xFF); //saturation byte 2
        packetData[41] = (byte) (brightness & 0xFF); //brightness byte 1
        packetData[42] = (byte) ((brightness >> 8) & 0xFF); //brightness byte 2
        packetData[43] = (byte) (kelvin & 0xFF); //kelvin byte 1
        packetData[44] = (byte) ((kelvin >> 8) & 0xFF); //kelvin byte 2
        return new DatagramPacket(packetData, packetData.length, address, port);
    }

    static DatagramPacket setStatePacket(InetAddress address, int port, boolean state) {
        byte[] packetData = {0, 0, 0, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 117, 0, 0, 0, 0, -1, -1, 0, 0, 0, 4};
        packetData[0] = (byte) packetData.length;
        System.out.println(packetData.length + ":" + packetData[0]);
        if (state) {
            packetData[37] = -1;
            packetData[38] = -1;
            System.out.println("Turning light on");
        } else {
            packetData[37] = (byte) 0x00;
            packetData[38] = (byte) 0x00;
            System.out.println("Turning light off");
        }
        return new DatagramPacket(packetData, packetData.length, address, port);
    }

    static DatagramPacket discoveryPacket(InetAddress address, int port, byte[] localIP) {
        byte[] packetData = {0, 0, 0, 52,
                (byte) 192, (byte) 168, (byte) 0, (byte) 0, //source
                0, 0, 0, 0, 0, 0, 0, 0, //target
                0, 0, 0, 0, 0, 0, //reserved
                0, //require response or acknowledgement
                0, //sequence
                0, 0, 0, 0, 0, 0, 0, 0, //reserved, start of protocol header
                2, 0, //GetService command
                0, 0}; //reserved
        packetData[0] = (byte) packetData.length;
        packetData[4] = localIP[0];
        packetData[5] = localIP[1];
        packetData[6] = localIP[2];
        packetData[7] = localIP[3];
        return new DatagramPacket(packetData, packetData.length, address, port);
    }

    static DatagramPacket getLabelPacket(InetAddress address, int port, byte[] localIP) {
        byte[] packetData = {0, 0, 0, 52,
                (byte) 192, (byte) 168, (byte) 0, (byte) 0, //source
                0, 0, 0, 0, 0, 0, 0, 0, //target
                0, 0, 0, 0, 0, 0, //reserved
                0, //require response or acknowledgement
                0, //sequence
                0, 0, 0, 0, 0, 0, 0, 0, //reserved, start of protocol header
                23, 0, //GetLabel command
                0, 0}; //reserved
        packetData[0] = (byte) packetData.length;
        packetData[4] = localIP[0];
        packetData[5] = localIP[1];
        packetData[6] = localIP[2];
        packetData[7] = localIP[3];
        return new DatagramPacket(packetData, packetData.length, address, port);
    }

}
