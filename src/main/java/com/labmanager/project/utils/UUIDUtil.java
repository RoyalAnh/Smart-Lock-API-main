package com.labmanager.project.utils;

import java.nio.ByteBuffer;
import java.util.UUID;
import org.apache.commons.codec.binary.Base32;

public class UUIDUtil {

    public static String generateBase32UUID() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        Base32 base32 = new Base32();
        String base32UUID = base32.encodeToString(byteBuffer.array()).replace("=", "");

        // Ensure the result is exactly 16 characters long
        if (base32UUID.length() > 16) {
            return base32UUID.substring(0, 16);
        } else if (base32UUID.length() < 16) {
            return String.format("%-16s", base32UUID).replace(' ', '0');
        }
        return base32UUID;
    }

    public static void main(String[] args) {
        String base32UUID = generateBase32UUID();
        System.out.println(base32UUID);
    }
}