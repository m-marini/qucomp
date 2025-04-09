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

import static java.util.Objects.requireNonNull;

/**
 * Draws the gate component
 */
public class BitGate extends AbstractGate {
    private final String gate;
    private final int port;

    /**
     * Create the gate
     *
     * @param gate    the gate definition
     * @param port    the port bit
     * @param numBits the number of bits
     */
    public BitGate(String gate, int port, int numBits) {
        super(numBits);
        this.port = port;
        this.gate = requireNonNull(gate);
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
            int y0 = (i * 2 + 1) * height / numBits / 2;
            if (i == port && !gate.equals("i")) {
                drawBox(g, gate, x0, y0, width, LEFT_CONNECTED | RIGHT_CONNECTED);
            } else {
                g.drawLine(0, y0, width, y0);
            }
        }
    }
}
