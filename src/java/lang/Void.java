/*
 * @(#)Void.java	1.7 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * The Void class is an uninstantiable placeholder class to hold a
 * reference to the Class object representing the primitive Java type
 * void.
 *
 * @author  unascribed
 * @version 1.7, 11/29/01
 * @since   JDK1.1
 */
public final
class Void {

    /**
     * The Class object representing the primitive Java type void.
     */
    public static final Class TYPE = Class.getPrimitiveClass("void");

    /*
     * The Void class cannot be instantiated.
     */
    private Void() {}
}
