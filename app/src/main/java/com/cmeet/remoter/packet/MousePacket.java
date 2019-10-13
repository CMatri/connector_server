package com.cmeet.remoter.packet;

import java.text.DecimalFormat;

public class MousePacket extends Packet {
    float x, y;

    public MousePacket(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public byte[] getData() {
        String formatted = '\1' + new DecimalFormat("00.000").format(Math.abs(x)) + new DecimalFormat("00.000").format(Math.abs(y)) + (x < 0 ? '\0' : '\1') + (y < 0 ? '\0' : '\1');
        while(formatted.length() < 20) formatted += '\0';
        return formatted.getBytes();
    }
}
