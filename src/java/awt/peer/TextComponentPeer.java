/*
 * @(#)TextComponentPeer.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface TextComponentPeer extends ComponentPeer {
    void setEditable(boolean editable);
    String getText();
    void setText(String l);
    int getSelectionStart();
    int getSelectionEnd();
    void select(int selStart, int selEnd);
    void setCaretPosition(int pos);
    int getCaretPosition();
}
