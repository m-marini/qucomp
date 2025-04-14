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

import java.awt.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Draw the input gate
 */
public class StateMapGate extends AbstractGate {
    private final int[] connections;
    private final String format;

    /**
     * Creates the terminal gate
     *
     * @param format    the format text
     * @param numQubits the number of bits
     * @param ports     the ports
     */
    public StateMapGate(String format, int[] ports, int numQubits) {
        super(numQubits);
        this.format = requireNonNull(format);
        this.connections = ports;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        int width = size.width;
        int height = size.height;
        int x0 = width / 2;
        g.setColor(getForeground());
        g.setFont(getFont().deriveFont(Font.BOLD));
        for (int i = 0; i < numBits; i++) {
            // Check if bit is connected
            int bit = -1;
            for (int j = 0; j < connections.length; j++) {
                if (connections[j] == i) {
                    bit = j;
                    break;
                }
            }
            int y0 = (i * 2 + 1) * height / numBits / 2;
            if (bit >= 0) {
                // Bit is mapped
                drawBox(g, format(format, bit), x0, y0, width, LEFT_CONNECTED | RIGHT_CONNECTED);
            } else {
                // Bit is not mapped
                g.drawLine(0, y0, width, y0);
            }
        }
    }
}
