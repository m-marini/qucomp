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

/**
 * Draws the gate component
 */
public class SwapGate extends AbstractGate {
    public static final int CROSS_SIZE = 11;
    private final int port0;
    private final int port1;

    /**
     * Create the gate
     *
     * @param port0   the swap bit
     * @param port1   the swap bit
     * @param numBits the number of bits
     */
    public SwapGate(int port0, int port1, int numBits) {
        super(numBits);
        this.port0 = port0;
        this.port1 = port1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        int width = size.width;
        int height = size.height;
        int x0 = width / 2;
        g.setColor(getForeground());
        g.setFont(getFont());
        for (int i = 0; i < numBits; i++) {
            int y0 = (i * 2 + 1) * height / numBits / 2;
            g.drawLine(0, y0, width, y0);
            if (i == port0 || i == port1) {
                g.drawLine(x0 - CROSS_SIZE / 2, y0 - CROSS_SIZE / 2, x0 + CROSS_SIZE / 2, y0 + CROSS_SIZE / 2);
                g.drawLine(x0 - CROSS_SIZE / 2, y0 + CROSS_SIZE / 2, x0 + CROSS_SIZE / 2, y0 - CROSS_SIZE / 2);
            }
        }
        int y0 = (port0 * 2 + 1) * height / numBits / 2;
        int y1 = (port1 * 2 + 1) * height / numBits / 2;
        g.drawLine(x0, y0, x0, y1);
    }
}
