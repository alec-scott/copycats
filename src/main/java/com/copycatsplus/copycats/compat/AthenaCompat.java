package com.copycatsplus.copycats.compat;

public class AthenaCompat {
    // PORT-NOTE(26.1): Athena has no 26.1 Fabric build; nothing to unwrap
    public static <T> T unwrapAthenaGetter(T getter) {
        return getter;
    }
}
