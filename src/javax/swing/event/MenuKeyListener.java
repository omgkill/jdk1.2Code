/*
 * @(#)MenuKeyListener.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;


import java.util.EventListener;


/**
 * MenuKeyListener
 *
 * @version 1.5 11/29/01
 * @author Georges Saab
 */
public interface MenuKeyListener extends EventListener {
    /**
     * Invoked when a key has been typed.
     * This event occurs when a key press is followed by a key release.
     */
    void menuKeyTyped(MenuKeyEvent e);

    /**
     * Invoked when a key has been pressed.
     */
    void menuKeyPressed(MenuKeyEvent e);

    /**
     * Invoked when a key has been released.
     */
    void menuKeyReleased(MenuKeyEvent e);
}

