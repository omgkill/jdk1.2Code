/*
 * @(#)CRC32.java	1.24 03/05/16
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

/**
 * A class that can be used to compute the CRC-32 of a data stream.
 *
 * @see		Checksum
 * @version 	1.24, 05/16/03
 * @author 	David Connelly
 */
public
class CRC32 implements Checksum {
    private int crc;

    /*
     * Loads the ZLIB library.
     */
    static {
	java.security.AccessController.doPrivileged(
		  new sun.security.action.LoadLibraryAction("zip"));
    }

    /**
     * Creates a new CRC32 class.
     */
    public CRC32() {
    }
   

    /**
     * Updates CRC-32 with specified byte.
     */
    public void update(int b) {
	crc = update(crc, b);
    }

    /**
     * Updates CRC-32 with specified array of bytes.
     */
    public void update(byte[] b, int off, int len) {
	if (b == null) {
	    throw new NullPointerException();
	}
	if (off < 0 || len < 0 || off > b.length - len) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	crc = updateBytes(crc, b, off, len);
    }

    /**
     * Updates checksum with specified array of bytes.
     */
    public void update(byte[] b) {
	crc = updateBytes(crc, b, 0, b.length);
    }

    /**
     * Resets CRC-32 to initial value.
     */
    public void reset() {
	crc = 0;
    }

    /**
     * Returns CRC-32 value.
     */
    public long getValue() {
	return (long)crc & 0xffffffffL;
    }

    private native static int update(int crc, int b);
    private native static int updateBytes(int crc, byte[] b, int off, int len);
}
