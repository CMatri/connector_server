package com.cmeet.remoter.packet;

public class VolumePacket extends Packet {
    byte dir; // 0 down 1 up

    public VolumePacket(byte dir) {
        this.dir = dir;
    }

    @Override
    public byte[] getData() {
        return new byte[]{ 0x03, dir };
    }
}
