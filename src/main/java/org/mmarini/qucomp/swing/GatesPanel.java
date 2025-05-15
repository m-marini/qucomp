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

import org.mmarini.qucomp.apis.QuCircuitBuilder;
import org.mmarini.qucomp.apis.QuGate;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Displays the quantum gates
 */
public class GatesPanel extends JPanel {
    private static final Map<String, BiFunction<QuGate, Integer, JComponent>> BUILDERS = Map.of(
            "s", (g, n) -> new BitGate("S", g.indices()[0], n),
            "t", (g, n) -> new BitGate("T", g.indices()[0], n),
            "h", (g, n) -> new BitGate("H", g.indices()[0], n),
            "x", (g, n) -> new BitGate("X", g.indices()[0], n),
            "y", (g, n) -> new BitGate("Y", g.indices()[0], n),
            "z", (g, n) -> new BitGate("Z", g.indices()[0], n),
            "swap", (g, n) -> new SwapGate(g.indices()[0], g.indices()[1], n),
            "cnot", (g, n) -> new CNotGate(g.indices()[0], g.indices()[1], n),
            "ccnot", (g, n) -> new CCNotGate(g.indices()[0], g.indices()[1], g.indices()[2], n),
            "map", (g, n) -> new StateMapGate("f%d(s)", g.indices(), n)
    );

    /**
     * Returns the bit gate for the given quantum gate
     *
     * @param gate      the quntum gate
     * @param numQubits the number of qubits
     */
    private static JComponent createGate(QuGate gate, int numQubits) {
        BiFunction<QuGate, Integer, JComponent> builder = BUILDERS.get(gate.type());
        return builder != null
                ? builder.apply(gate, numQubits)
                : new BitGate("", -1, numQubits);
    }

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
    public void setGates(java.util.List<QuGate> gates) {
        removeAll();
        int numQubits = QuCircuitBuilder.numQuBits(gates);
        setLayout(new GridLayout(1, gates.size() + 1));
        add(new TerminalGate("I%d", numQubits, BitGate.RIGHT_CONNECTED));
        for (QuGate gate : gates) {
            add(createGate(gate, numQubits));
        }
        add(new TerminalGate("O%d", numQubits, BitGate.LEFT_CONNECTED));
        invalidate();
    }
}