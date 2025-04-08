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

import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;
import org.mmarini.swing.GridLayoutHelper;

import javax.swing.*;
import java.awt.*;

import static java.lang.String.format;

/**
 * Shows the ket information: states, probabilities, bit probabilities
 * - states id
 * - states values
 * - probability values
 * - bit id
 * - bit probabilities
 */
public class KetPanel extends JPanel {
    /**
     * Returns the complex label view
     *
     * @param value the value
     */
    private static String complexLabel(Complex value) {
        float real = value.real();
        float im = value.im();
        if (real == 0) {
            if (im == 0) {
                return "0";
            } else if (im == 1) {
                return "-i";
            } else {
                return format("%.4fi", im);
            }
        } else if (real == 1) {
            if (im == 0) {
                return "1";
            } else if (im == 1) {
                return "1+i";
            } else if (im == -1) {
                return "1-i";
            } else if (im > 0) {
                return format("1+%.4fi", im);
            } else {
                return format("1-%.4fi", -im);
            }
        } else if (real == -1) {
            if (im == 0) {
                return "-1";
            } else if (im == 1) {
                return "-1+i";
            } else if (im == -1) {
                return "-1-i";
            } else if (im > 0) {
                return format("-1+%.4fi", im);
            } else {
                return format("-1-%.4fi", -im);
            }
        } else if (im == 0) {
            return format("%4f", real);
        } else if (im == 1) {
            return format("%4f+i", real);
        } else if (im == -1) {
            return format("%4f-i", real);
        } else if (im > 0) {
            return format("%4f+%.4fi", real, im);
        } else {
            return format("%4f-%.4fi", real, -im);
        }
    }

    /**
     * Returns the centered label
     *
     * @param text the text
     */
    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return label;
    }

    /**
     * Returns the text of probability
     *
     * @param value the value
     */
    private static String probLabel(double value) {
        return value == 0
                ? "0"
                : value == 1
                ? "1"
                : format("%.4f", value);
    }

    private final JPanel statePanel;
    private final JPanel bitPanel;

    /**
     * Creates the panel
     */
    public KetPanel() {
        this.statePanel = new JPanel();
        this.bitPanel = new JPanel();
        createContent();
    }

    /**
     * Creates the content
     */
    private void createContent() {
        statePanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("KetPanel.statesPanel.title")));
        bitPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("KetPanel.bitsPanel.title")));
        new GridLayoutHelper<>(this)
                .modify("insets,2 center fill noweight")
                .modify("at,0,0").add(statePanel)
                .modify("at,0,1").add(bitPanel);
    }

    /**
     * Sets the bit info
     *
     * @param ket the ket
     */
    private void setBitsInfo(Ket ket) {
        bitPanel.removeAll();
        double[] bitProbs = ket.bitProbs();
        bitPanel.setLayout(new GridLayout(bitProbs.length, 2));
        for (int i = 0; i < bitProbs.length; i++) {
            bitPanel.add(createLabel(Messages.format("KetPanel.bitProbability.title", i)));
            bitPanel.add(createLabel(probLabel(bitProbs[i])));
        }
        bitPanel.invalidate();
    }

    /**
     * Sets the ket to show
     *
     * @param ket the ket
     */
    public void setKet(Ket ket) {
        setStateInfo(ket);
        setBitsInfo(ket);
        invalidate();
    }

    /**
     * Sets the state info
     *
     * @param ket the ket
     */
    private void setStateInfo(Ket ket) {
        statePanel.removeAll();
        int numStates = ket.values().length;
        statePanel.setLayout(new GridLayout(numStates + 1, 3));
        statePanel.add(createLabel(""));
        statePanel.add(createLabel(Messages.getString("KetPanel.statesValue.title")));
        statePanel.add(createLabel(Messages.getString("KetPanel.statesProbability.title")));
        for (int i = 0; i < numStates; i++) {
            Complex complex = ket.values()[i];
            statePanel.add(createLabel(Messages.format("KetPanel.stateId.title", i)));
            statePanel.add(createLabel(complexLabel(complex)));
            statePanel.add(createLabel(probLabel(complex.moduleSquare())));
        }
        statePanel.invalidate();
    }
}
