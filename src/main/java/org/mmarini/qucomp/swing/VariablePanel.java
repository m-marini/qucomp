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

import org.mmarini.qucomp.compiler.Value;
import org.mmarini.swing.GridLayoutHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Map;

/**
 * Allows showing the variables and their detail value
 */
public class VariablePanel extends JPanel {
    private final JList<String> varList;
    private final JTextArea details;
    private final JSplitPane splitPanel;
    private Map<String, Value> variables;
    private boolean init;

    /**
     * Creates the panel
     */
    public VariablePanel() {
        this.varList = new JList<>();
        this.details = new JTextArea(10, 80);
        this.splitPanel = new JSplitPane();
        createContent();
        createFlow();
    }

    /**
     * Creates the content
     */
    private void createContent() {
        // Creates the list panel
        details.setEditable(false);
        details.setFont(Font.decode(Font.MONOSPACED).deriveFont(Font.BOLD, 14));

        JScrollPane scrollVarList = new JScrollPane(varList);
        JPanel listPanel = new GridLayoutHelper<>(new JPanel()).modify("insets,5 n fill vw,1 hw,1")
                .modify("at,0,0").add(scrollVarList)
                .getContainer();
        listPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("VariablePanel.list.title")));

        JScrollPane detailsPanel = new JScrollPane(details);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("VariablePanel.details.title")));

        // Creates the split panel
        splitPanel.setLeftComponent(listPanel);
        splitPanel.setRightComponent(detailsPanel);
        splitPanel.setOneTouchExpandable(true);
        splitPanel.setResizeWeight(0);
        splitPanel.setContinuousLayout(true);

        setLayout(new BorderLayout());
        add(splitPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the events flow
     */
    private void createFlow() {
        varList.addListSelectionListener(this::onSelection);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!init) {
                    init = true;
                    splitPanel.setDividerLocation(0.25);
                }
            }
        });
    }

    /**
     * Handles the selection events
     *
     * @param listSelectionEvent the event
     */
    private void onSelection(ListSelectionEvent listSelectionEvent) {
        if (!listSelectionEvent.getValueIsAdjusting()) {
            String id = varList.getSelectedValue();
            Value value = variables.get(id);
            String text = value != null
                    ? Arrays.stream(value.source().fullReportMessage("")).reduce((a, b) -> a + "\n" + b).orElse("")
                    + "\n" + value
                    : "";
            details.setText(text);
        }
    }

    /**
     * Sets the variables
     *
     * @param variables the map of variables
     */
    public void setVariables(Map<String, Value> variables) {
        this.variables = variables;
        String selected = varList.getSelectedValue();
        String[] values = variables.keySet().stream().sorted().toArray(String[]::new);
        varList.setListData(values);
        varList.setSelectedValue(selected, true);
    }
}
