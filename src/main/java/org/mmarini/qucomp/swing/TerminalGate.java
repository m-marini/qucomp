/*
 * Copyright (c) 2025 Marco Marini, marco.marini@mmarini.org
 *
 *  Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 *    END OF TERMS AND CONDITIONS
 *
 */

package org.mmarini.qucomp.swing;

import javax.swing.*;
import java.awt.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.mmarini.qucomp.swing.BitGate.drawBox;

/**
 * Draw the input gate
 */
public class TerminalGate extends JComponent {
    private final int numBits;
    private final int connections;
    private final String format;

    /**
     * Creates the terminal gate
     *
     * @param format      the format text
     * @param numQubits   the number of bits
     * @param connections the connections
     */
    public TerminalGate(String format, int numQubits, int connections) {
        this.format = requireNonNull(format);
        this.numBits = numQubits;
        this.connections = connections;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        Dimension size = getSize();
        int width = size.width;
        int height = size.height;
        g.fillRect(0, 0, width, height);
        int x0 = width / 2;
        g.setColor(getForeground());
        g.setFont(getFont().deriveFont(Font.BOLD));
        for (int i = 0; i < numBits; i++) {
            int y0 = (i * 2 + 1) * height / numBits / 2;
            drawBox(g, format(format, i), x0, y0, width, connections);
        }
    }
}
