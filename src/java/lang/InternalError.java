/*
 * @(#)InternalError.java	1.17 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown to indicate some unexpected internal error has occurred in 
 * the Java Virtual Machine. 
 *
 * @author  unascribed
 * @version 1.17, 11/29/01
 * @since   JDK1.0
 */
public
class InternalError extends VirtualMachineError {
    /**
     * Constructs an <code>InternalError</code> with no detail message. 
     */
    public InternalError() {
	super();
    }

    /**
     * Constructs an <code>InternalError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public InternalError(String s) {
	super(s);
    }
}
