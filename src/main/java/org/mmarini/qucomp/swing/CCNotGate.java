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

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Draws the gate component
 */
public class CCNotGate extends AbstractGate {
    public static final int DATA_SIZE = 11;
    public static final int CONTROL_SIZE = 7;
    private final int data;
    private final int control0;
    private final int control1;

    /**
     * Create the gate
     *
     * @param data     the data bit
     * @param control0 the control bit
     * @param control1 the control bit
     * @param numBits  the number of bits
     */
    public CCNotGate(int data, int control0, int control1, int numBits) {
        super(numBits);
        this.control0 = control0;
        this.control1 = control1;
        this.data = data;
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
            if (i == control0 || i == control1) {
                g.fillOval(x0 - CONTROL_SIZE / 2, y0 - CONTROL_SIZE / 2, CONTROL_SIZE, CONTROL_SIZE);
            } else if (i == data) {
                g.drawOval(x0 - DATA_SIZE / 2, y0 - DATA_SIZE / 2, DATA_SIZE, DATA_SIZE);
                g.drawLine(x0, y0 - DATA_SIZE / 2, x0, y0 + DATA_SIZE / 2);
            }
        }
        int lowestBit = min(min(control0, control1), data);
        int highestBit = max(max(control0, control1), data);
        int y0 = (lowestBit * 2 + 1) * height / numBits / 2;
        int y1 = (highestBit * 2 + 1) * height / numBits / 2;
        g.drawLine(x0, y0, x0, y1);
    }
}
