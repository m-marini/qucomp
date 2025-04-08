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
import java.awt.geom.Rectangle2D;

import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.util.Objects.requireNonNull;

/**
 * Draws the gate component
 */
public class BitGate extends JComponent {
    public static final int INSETS = 3;
    public static final int LEFT_CONNECTED = 1;
    public static final int RIGHT_CONNECTED = 2;
    public static final int BOX_SIZE = 15;
    private final int numBits;
    private final String gate;
    private final int port;

    /**
     * Draws a boxed text connection
     *
     * @param g     the context
     * @param text  the text
     * @param x0    the center abscissa
     * @param y0    the center ordinate
     * @param width the width
     */
    public static void drawBox(Graphics g, java.lang.String text, int x0, int y0, int width) {
        drawBox(g, text, x0, y0, width, LEFT_CONNECTED & RIGHT_CONNECTED);
    }

    /**
     * Draws a boxed text connection
     *
     * @param g           the context
     * @param text        the text
     * @param x0          the center abscissa
     * @param y0          the center ordinate
     * @param width       the width
     * @param connections connection mask
     */
    public static void drawBox(Graphics g, java.lang.String text, int x0, int y0, int width, int connections) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D textBound = fm.getStringBounds(text, g);
        int w = (int) round(textBound.getWidth());
        int h = (int) round(textBound.getHeight());
        int boxSize = max(max(w, h), BOX_SIZE) + INSETS * 2;
        int xBox = x0 - boxSize / 2 - INSETS;
        int yBox = y0 - boxSize / 2 - INSETS;
        int xText = x0 - (int) round(textBound.getCenterX());
        int yText = y0 - (int) round(textBound.getCenterY());
        g.drawRect(xBox, yBox, boxSize, boxSize);
        g.drawString(text, xText, yText);
        if ((connections & LEFT_CONNECTED) != 0) {
            g.drawLine(0, y0, xBox - 1, y0);
        }
        if ((connections & RIGHT_CONNECTED) != 0) {
            g.drawLine(xBox + boxSize, y0, width - 1, y0);
        }
    }

    /**
     * Create the gate
     *
     * @param gate    the gate definition
     * @param port    the port bit
     * @param numBits the number of bits
     */
    public BitGate(String gate, int port, int numBits) {
        this.numBits = numBits;
        this.port = port;
        this.gate = requireNonNull(gate);
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
            if (i == port && !gate.equals("i")) {
                drawBox(g, gate, x0, y0, width, LEFT_CONNECTED | RIGHT_CONNECTED);
            } else {
                g.drawLine(0, y0, width, y0);
            }
        }
    }
}
