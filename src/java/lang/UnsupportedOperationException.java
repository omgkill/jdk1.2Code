/*
 * @(#)UnsupportedOperationException.java	1.8 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown to indicate that the requested operation is not supported.
 *
 * @author  Josh Bloch
 * @version 1.8 11/29/01
 * @since   JDK1.2
 */
public class UnsupportedOperationException extends RuntimeException {
    /**
     * Constructs an UnsupportedOperationException with no detail message.
     */
    public UnsupportedOperationException() {
    }

    /**
     * Constructs an UnsupportedOperationException with the specified
     * detail message.
     */
    public UnsupportedOperationException(String message) {
	super(message);
    }
}
