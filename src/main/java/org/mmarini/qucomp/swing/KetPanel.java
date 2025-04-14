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

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
    private static String complexString(Complex value) {
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
     * Returns the text of probability
     *
     * @param value the value
     */
    private static String probString(double value) {
        return value == 0
                ? "0"
                : value == 1
                ? "1"
                : format("%.4f", value);
    }

    private final JTable stateTable;
    private final JTable bitTable;
    private final AbstractTableModel stateModel;
    private final AbstractTableModel bitModel;
    private Ket ket;

    /**
     * Creates the panel
     */
    public KetPanel() {
        this.stateTable = new JTable();
        this.bitTable = new JTable();
        this.bitModel = new AbstractTableModel() {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public String getColumnName(int column) {
                return switch (column) {
                    case 0 -> Messages.getString("KetPanel.bit.title");
                    case 1 -> Messages.getString("KetPanel.bitProbability.title");
                    default -> "?";
                };
            }

            @Override
            public int getRowCount() {
                return ket == null ? 0 : ket.numBits();
            }

            @Override
            public String getValueAt(int rowIndex, int columnIndex) {
                double[] bitProb = ket.bitProbs();
                return switch (columnIndex) {
                    case 0 -> String.valueOf(rowIndex);
                    case 1 -> probString(bitProb[rowIndex]);
                    default -> "?";
                };
            }
        };
        this.stateModel = new AbstractTableModel() {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public String getColumnName(int column) {
                return switch (column) {
                    case 0 -> Messages.getString("KetPanel.state.title");
                    case 1 -> Messages.getString("KetPanel.statesValue.title");
                    case 2 -> Messages.getString("KetPanel.statesProbability.title");
                    default -> "?";
                };
            }

            @Override
            public int getRowCount() {
                return ket != null ? ket.values().length : 0;
            }

            @Override
            public String getValueAt(int rowIndex, int columnIndex) {
                Complex complex = ket.values()[rowIndex];
                return switch (columnIndex) {
                    case 0 -> Messages.format("KetPanel.stateId.title", rowIndex);
                    case 1 -> complexString(complex);
                    case 2 -> probString(complex.moduleSquare());
                    default -> "?";
                };
            }
        };
        createContent();
    }

    /**
     * Creates the content
     */
    private void createContent() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        stateTable.setModel(stateModel);
        stateTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        stateTable.setDefaultRenderer(String.class, centerRenderer);

        bitTable.setModel(bitModel);
        bitTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        bitTable.setDefaultRenderer(String.class, centerRenderer);

        JPanel statePanel = new JPanel();
        statePanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("KetPanel.statesPanel.title")));
        statePanel.setLayout(new BorderLayout());
        statePanel.add(new JScrollPane(stateTable), BorderLayout.CENTER);

        JPanel bitPanel = new JPanel();
        bitPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("KetPanel.bitsPanel.title")));
        bitPanel.setLayout(new BorderLayout());
        bitPanel.add(new JScrollPane(bitTable), BorderLayout.CENTER);

        JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPanel.setTopComponent(bitPanel);
        splitPanel.setBottomComponent(statePanel);
        splitPanel.setOneTouchExpandable(true);
        splitPanel.setContinuousLayout(true);
        splitPanel.setResizeWeight(0);
        splitPanel.setDividerLocation(250);
        setLayout(new BorderLayout());
        add(splitPanel, BorderLayout.CENTER);
    }

    /**
     * Sets the ket to show
     *
     * @param ket the ket
     */
    public void setKet(Ket ket) {
        this.ket = ket;
        stateModel.fireTableChanged(new TableModelEvent(stateModel));
        bitModel.fireTableChanged(new TableModelEvent(stateModel));
        invalidate();
    }
}
