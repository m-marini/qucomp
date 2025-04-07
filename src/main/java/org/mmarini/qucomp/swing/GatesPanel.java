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

import org.mmarini.qucomp.apis.QuGate;
import org.mmarini.qucomp.apis.QuStateBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static java.lang.String.format;

/**
 * Displays the quantum gates
 */
public class GatesPanel extends JPanel {
    /**
     * Creates the panel
     */
    public GatesPanel() {
    }

    /**
     * Set the gates to show
     *
     * @param gates the gates
     */
    public void setGates(QuGate[] gates) {
        removeAll();
        int numQubits = QuStateBuilder.numQuBits(gates);
        setLayout(new GridLayout(gates.length + 2, numQubits));
        for (int col = 0; col < numQubits; col++) {
            JLabel label = new JLabel(format(Messages.getString("GatesPanel.port.input"), col));
            label.setBorder(BorderFactory.createEtchedBorder());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            add(label);
        }
        for (QuGate gate : gates) {
            for (int col = 0; col < numQubits; col++) {
                int finalCol = col;
                JLabel label = Arrays.stream(gate.indices())
                        .anyMatch(i -> i == finalCol)
                        ? new JLabel(Messages.getString("GatesPanel.port." + gate.type()))
                        : new JLabel("");
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEtchedBorder());
                add(label);
            }
        }
        for (int col = 0; col < numQubits; col++) {
            JLabel label = new JLabel(format(Messages.getString("GatesPanel.port.output"), col));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEtchedBorder());
            add(label);
        }
        doLayout();
    }
}