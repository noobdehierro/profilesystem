package com.example.profilesystem.qr.util;

import java.util.UUID;

public class IdGenerator {
    public static String generateShortId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }
}
