package com.cmeet.remoter.packet;

public class ClickPacket extends Packet {
    byte side; // 0 left 1 right

    public ClickPacket(byte side) {
        this.side = side;
    }

    public byte[] getData() {
        return new byte[]{ 0x02, side };
    }
}
