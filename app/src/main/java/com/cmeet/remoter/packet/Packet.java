package com.cmeet.remoter.packet;

/* Standard:
   10 bytes
   first byte identifier:
      0x01 = Mouse
      0x02 = Click
      0x03 = Volume
      0x04 = Key
   data bytes:
      Mouse:   0x01 |x| (00.000) |y| (00.000) (0|1) (0|1) // 0x01 identifier, xy magnitudes, xneg|xpos, yneg|ypos
      Click:   1 byte (0 left, 1 right)
      Volume:  1 byte (0 down, 1 up)
      Key:     1 byte, ASCII
 */
// who likes oop woop woop
public abstract class Packet {
    public static enum Type {
        MOUSE, CLICK, VOLUME, KEY
    }

    public Type type;
    public abstract byte[] getData();
}


