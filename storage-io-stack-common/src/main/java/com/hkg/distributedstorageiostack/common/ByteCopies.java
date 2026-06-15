package com.hkg.distributedstorageiostack.common;

import java.util.Arrays;

/**
 * Tiny defensive-copy utility for byte arrays that cross module boundaries.
 */
public final class ByteCopies {
    private ByteCopies() {
    }

    public static byte[] copyOf(byte[] bytes) {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
