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

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import org.mmarini.qucomp.apis.Matrix;
import org.mmarini.swing.GridLayoutHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mmarini.qucomp.swing.Messages.format;

/**
 * Handle the UI for ket editing
 */
public class KetEditor extends JPanel {
    private static final String[] QUBIT_VALUES = {"|0>", "|1>", "|+>", "|->", "|i>", "|-i>"};
    private static final Matrix[] KET_VALUES = {
            Matrix.ketBase(0),
            Matrix.ketBase(1),
            Matrix.plus(),
            Matrix.minus(),
            Matrix.i(),
            Matrix.minus_i()
    };
    private static final Logger logger = LoggerFactory.getLogger(KetEditor.class);
    private final PublishProcessor<Matrix> kets;
    private final List<JComboBox<String>> quBits;
    private int numQuBits;

    /**
     * Creates the editor
     */
    public KetEditor() {
        logger.atDebug().log("Created");
        this.kets = PublishProcessor.create();
        this.quBits = new ArrayList<>();
        createContent();
        createFlow();
        setNumQuBits(0);
    }

    /**
     * Creates the content
     */
    private void createContent() {
        GridLayoutHelper<KetEditor> layout = new GridLayoutHelper<>(this)
                .modify("insets,10 vw,1 hw,0");
        for (int i = 0; i < numQuBits; i++) {
            JComboBox<String> qubit = new JComboBox<>(QUBIT_VALUES);
            quBits.add(qubit);
            layout.at(0, i)
                    .add(new JLabel(format("KetEditor.bit.label", i)));
            layout.at(1, i)
                    .add(qubit);
        }
        invalidate();
    }

    /**
     * Create the flow
     */
    private void createFlow() {
        for (JComboBox<String> quBit : quBits) {
            quBit.addItemListener(ev -> generateKet());
        }
    }

    /**
     * Generate the ket
     */
    private void generateKet() {
        getOptionalKet().ifPresent(kets::onNext);
    }

    /**
     * Returns the optional of ket
     */
    public Optional<Matrix> getOptionalKet() {
        return quBits.stream()
                .map(combo -> KET_VALUES[combo.getSelectedIndex()])
                .reduce((a, b) ->
                        b.cross(a)
                );
    }

    /**
     * Returns the flow of ket
     */
    public Flowable<Matrix> readKet() {
        return kets;
    }

    /**
     * Set the valid number of qubits
     *
     * @param numQuBits the valid number of qubits
     */
    public void setNumQuBits(int numQuBits) {
        this.numQuBits = numQuBits;
        quBits.clear();
        removeAll();
        createContent();
        createFlow();
        invalidate();
        generateKet();
    }
}
