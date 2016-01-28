package com.mlizhi.base;

import android.support.v4.view.MotionEventCompat;
import java.io.ByteArrayOutputStream;
import java.util.Random;

public class Crypter {
    private static Random random;
    private ByteArrayOutputStream baos;
    private int contextStart;
    private int crypt;
    private boolean header;
    private byte[] key;
    private byte[] out;
    private int padding;
    private byte[] plain;
    private int pos;
    private int preCrypt;
    private byte[] prePlain;

    static {
        random = new Random();
    }

    public Crypter() {
        this.header = true;
        this.baos = new ByteArrayOutputStream(8);
    }

    private static long getUnsignedInt(byte[] in, int offset, int len) {
        int end;
        long ret = 0;
        if (len > 8) {
            end = offset + 8;
        } else {
            end = offset + len;
        }
        for (int i = offset; i < end; i++) {
            ret = (ret << 8) | ((long) (in[i] & MotionEventCompat.ACTION_MASK));
        }
        return (4294967295L & ret) | (ret >>> 32);
    }

    public byte[] decrypt(byte[] in, int offset, int len, byte[] k) {
        if (k == null) {
            return null;
        }
        this.preCrypt = 0;
        this.crypt = 0;
        this.key = k;
        byte[] m = new byte[(offset + 8)];
        if (len % 8 != 0 || len < 16) {
            return null;
        }
        this.prePlain = decipher(in, offset);
        this.pos = this.prePlain[0] & 7;
        int count = (len - this.pos) - 10;
        if (count < 0) {
            return null;
        }
        int i;
        for (i = offset; i < m.length; i++) {
            m[i] = (byte) 0;
        }
        this.out = new byte[count];
        this.preCrypt = 0;
        this.crypt = 8;
        this.contextStart = 8;
        this.pos++;
        this.padding = 1;
        while (this.padding <= 2) {
            if (this.pos < 8) {
                this.pos++;
                this.padding++;
            }
            if (this.pos == 8) {
                m = in;
                if (!decrypt8Bytes(in, offset, len)) {
                    return null;
                }
            }
        }
        i = 0;
        while (count != 0) {
            if (this.pos < 8) {
                this.out[i] = (byte) (m[(this.preCrypt + offset) + this.pos] ^ this.prePlain[this.pos]);
                i++;
                count--;
                this.pos++;
            }
            if (this.pos == 8) {
                m = in;
                this.preCrypt = this.crypt - 8;
                if (!decrypt8Bytes(in, offset, len)) {
                    return null;
                }
            }
        }
        this.padding = 1;
        while (this.padding < 8) {
            if (this.pos < 8) {
                if ((m[(this.preCrypt + offset) + this.pos] ^ this.prePlain[this.pos]) != 0) {
                    return null;
                }
                this.pos++;
            }
            if (this.pos == 8) {
                m = in;
                this.preCrypt = this.crypt;
                if (!decrypt8Bytes(in, offset, len)) {
                    return null;
                }
            }
            this.padding++;
        }
        return this.out;
    }

    public byte[] decrypt(byte[] in, byte[] k) {
        return decrypt(in, 0, in.length, k);
    }

    public byte[] encrypt(byte[] in, int offset, int len, byte[] k) {
        if (k == null) {
            return in;
        }
        int i;
        this.plain = new byte[8];
        this.prePlain = new byte[8];
        this.pos = 1;
        this.padding = 0;
        this.preCrypt = 0;
        this.crypt = 0;
        this.key = k;
        this.header = true;
        this.pos = (len + 10) % 8;
        if (this.pos != 0) {
            this.pos = 8 - this.pos;
        }
        this.out = new byte[((this.pos + len) + 10)];
        this.plain[0] = (byte) ((rand() & 248) | this.pos);
        for (i = 1; i <= this.pos; i++) {
            this.plain[i] = (byte) (rand() & MotionEventCompat.ACTION_MASK);
        }
        this.pos++;
        for (i = 0; i < 8; i++) {
            this.prePlain[i] = (byte) 0;
        }
        this.padding = 1;
        while (this.padding <= 2) {
            if (this.pos < 8) {
                byte[] bArr = this.plain;
                int i2 = this.pos;
                this.pos = i2 + 1;
                bArr[i2] = (byte) (rand() & MotionEventCompat.ACTION_MASK);
                this.padding++;
            }
            if (this.pos == 8) {
                encrypt8Bytes();
            }
        }
        int i3 = offset;
        while (len > 0) {
            if (this.pos < 8) {
                bArr = this.plain;
                i2 = this.pos;
                this.pos = i2 + 1;
                i = i3 + 1;
                bArr[i2] = in[i3];
                len--;
            } else {
                i = i3;
            }
            if (this.pos == 8) {
                encrypt8Bytes();
            }
            i3 = i;
        }
        this.padding = 1;
        while (this.padding <= 7) {
            if (this.pos < 8) {
                bArr = this.plain;
                i2 = this.pos;
                this.pos = i2 + 1;
                bArr[i2] = (byte) 0;
                this.padding++;
            }
            if (this.pos == 8) {
                encrypt8Bytes();
            }
        }
        return this.out;
    }

    public byte[] encrypt(byte[] in, byte[] k) {
        return encrypt(in, 0, in.length, k);
    }

    private byte[] encipher(byte[] in) {
        long y = getUnsignedInt(in, 0, 4);
        long z = getUnsignedInt(in, 4, 4);
        long a = getUnsignedInt(this.key, 0, 4);
        long b = getUnsignedInt(this.key, 4, 4);
        long c = getUnsignedInt(this.key, 8, 4);
        long d = getUnsignedInt(this.key, 12, 4);
        long sum = 0;
        long delta = -1640531527 & 4294967295L;
        int loop = 16;
        while (true) {
            int loop2 = loop - 1;
            if (loop <= 0) {
                this.baos.reset();
                writeInt((int) y);
                writeInt((int) z);
                return this.baos.toByteArray();
            }
            sum = (sum + delta) & 4294967295L;
            y = (y + ((((z << 4) + a) ^ (z + sum)) ^ ((z >>> 5) + b))) & 4294967295L;
            z = (z + ((((y << 4) + c) ^ (y + sum)) ^ ((y >>> 5) + d))) & 4294967295L;
            loop = loop2;
        }
    }

    private byte[] decipher(byte[] in, int offset) {
        long y = getUnsignedInt(in, offset, 4);
        long z = getUnsignedInt(in, offset + 4, 4);
        long a = getUnsignedInt(this.key, 0, 4);
        long b = getUnsignedInt(this.key, 4, 4);
        long c = getUnsignedInt(this.key, 8, 4);
        long d = getUnsignedInt(this.key, 12, 4);
        long sum = -478700656 & 4294967295L;
        long delta = -1640531527 & 4294967295L;
        int loop = 16;
        while (true) {
            int loop2 = loop - 1;
            if (loop <= 0) {
                this.baos.reset();
                writeInt((int) y);
                writeInt((int) z);
                return this.baos.toByteArray();
            }
            z = (z - ((((y << 4) + c) ^ (y + sum)) ^ ((y >>> 5) + d))) & 4294967295L;
            y = (y - ((((z << 4) + a) ^ (z + sum)) ^ ((z >>> 5) + b))) & 4294967295L;
            sum = (sum - delta) & 4294967295L;
            loop = loop2;
        }
    }

    private void writeInt(int t) {
        this.baos.write(t >>> 24);
        this.baos.write(t >>> 16);
        this.baos.write(t >>> 8);
        this.baos.write(t);
    }

    private byte[] decipher(byte[] in) {
        return decipher(in, 0);
    }

    private void encrypt8Bytes() {
        byte[] bArr;
        int i;
        this.pos = 0;
        while (this.pos < 8) {
            if (this.header) {
                bArr = this.plain;
                i = this.pos;
                bArr[i] = (byte) (bArr[i] ^ this.prePlain[this.pos]);
            } else {
                bArr = this.plain;
                i = this.pos;
                bArr[i] = (byte) (bArr[i] ^ this.out[this.preCrypt + this.pos]);
            }
            this.pos++;
        }
        System.arraycopy(encipher(this.plain), 0, this.out, this.crypt, 8);
        this.pos = 0;
        while (this.pos < 8) {
            bArr = this.out;
            i = this.crypt + this.pos;
            bArr[i] = (byte) (bArr[i] ^ this.prePlain[this.pos]);
            this.pos++;
        }
        System.arraycopy(this.plain, 0, this.prePlain, 0, 8);
        this.preCrypt = this.crypt;
        this.crypt += 8;
        this.pos = 0;
        this.header = false;
    }

    private boolean decrypt8Bytes(byte[] in, int offset, int len) {
        this.pos = 0;
        while (this.pos < 8) {
            if (this.contextStart + this.pos >= len) {
                return true;
            }
            byte[] bArr = this.prePlain;
            int i = this.pos;
            bArr[i] = (byte) (bArr[i] ^ in[(this.crypt + offset) + this.pos]);
            this.pos++;
        }
        this.prePlain = decipher(this.prePlain);
        if (this.prePlain == null) {
            return false;
        }
        this.contextStart += 8;
        this.crypt += 8;
        this.pos = 0;
        return true;
    }

    private int rand() {
        return random.nextInt();
    }
}
