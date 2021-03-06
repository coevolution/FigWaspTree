/*
 * WholeNumberField.java
 *
 * Copyright (C) 2012 Andrew Rambaut
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package figtree.ui.components;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class WholeNumberField extends JTextField
        implements FocusListener, DocumentListener {

    protected static char MINUS_CHAR = '-';
    protected EventListenerList changeListeners = new EventListenerList();
    protected int min;
    protected int max;
    protected boolean range_check = false;
    protected boolean range_checked = false;

    public WholeNumberField() {
        super();
    }

    public WholeNumberField(int min, int max) {
        this();
        this.min = min;
        this.max = max;
        range_check = true;
        this.addFocusListener(this);
    }

    public void focusGained(FocusEvent evt) {
    }

    public void focusLost(FocusEvent evt) {
        if (range_check && !range_checked) {
            range_checked = true;
            try {
                int value = (Integer.valueOf(getText())).intValue();
                if (value < min || value > max) {
                    errorMsg();
                    return;
                }
            } catch (NumberFormatException e) {
                errorMsg();
                return;
            }
        }
    }

    public void setText(Integer obj) {
        setText(obj.toString());
    }

    protected void errorMsg() {
        JOptionPane.showMessageDialog(this,
                "Illegal entry\nValue must be between " + min + " and " +
                max + " inclusive", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setValue(int value) {
        if (range_check) {
            if (value < min || value > max) {
                errorMsg();
                return;
            }
        }
        setText(Integer.toString(value));
    }

    public Integer getValue() {
        try {
            return new Integer(getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Integer getValue(int default_value) {
        Integer value = getValue();
        if (value == null)
            return new Integer(default_value);
        else
            return value;
    }

    protected Document createDefaultModel() {
        Document doc = new WholeNumberFieldDocument();
        doc.addDocumentListener(this);
        return doc;
    }

    public void insertUpdate(DocumentEvent e) {
        range_checked = false;
        fireChanged();
    }

    public void removeUpdate(DocumentEvent e) {
        range_checked = false;
        fireChanged();
    }

    public void changedUpdate(DocumentEvent e) {
        range_checked = false;
        fireChanged();
    }

    static char[] numberSet = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    class WholeNumberFieldDocument extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {

            if (str == null) return;
            str = str.trim();

            String buf = getText(0, offs) + str;
            char[] array = buf.toCharArray();

            if (array.length > 0) {
                if (array[0] != MINUS_CHAR && !member(array[0], numberSet)) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }

            for (int i = 1; i < array.length; i++) {
                if (!member(array[i], numberSet)) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            super.insertString(offs, str, a);
        }
    }

    static boolean member(char item, char[] array) {
        for (int i = 0; i < array.length; i++)
            if (array[i] == item) return true;
        return false;
    }
    //------------------------------------------------------------------------
    // Event Methods
    //------------------------------------------------------------------------

    public void addChangeListener(ChangeListener x) {
        changeListeners.add(ChangeListener.class, x);
    }

    public void removeChangeListener(ChangeListener x) {
        changeListeners.remove(ChangeListener.class, x);
    }

    protected void fireChanged() {
        ChangeEvent c = new ChangeEvent(this);
        Object[] listeners = changeListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ChangeListener cl = (ChangeListener) listeners[i + 1];
                cl.stateChanged(c);
            }
        }
    }
}
