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

package org.mmarini.qucomp.apps;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.mmarini.qucomp.apis.Ket;
import org.mmarini.qucomp.apis.Matrix;
import org.mmarini.qucomp.apis.QuCircuitBuilder;
import org.mmarini.qucomp.apis.QuGate;
import org.mmarini.qucomp.swing.GatesPanel;
import org.mmarini.qucomp.swing.KetEditor;
import org.mmarini.qucomp.swing.KetPanel;
import org.mmarini.qucomp.swing.Messages;
import org.mmarini.swing.GridLayoutHelper;
import org.mmarini.swing.SwingUtils;
import org.mmarini.yaml.Locator;
import org.mmarini.yaml.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static org.mmarini.qucomp.apis.QuCircuitBuilder.loadGates;

/**
 * Computation GUI
 */
public class ComputeGUI {
    private static final Logger logger = LoggerFactory.getLogger(ComputeGUI.class);
    private final JFrame frame;
    private final JMenuItem exitMenu;
    private final JMenuItem openMenu;
    private final JFileChooser fileChooser;
    private final GatesPanel gatesPanel;
    private final KetPanel inputPanel;
    private final KetPanel outputPanel;
    private final KetEditor inputEditor;
    private QuGate[] gates;
    private Ket input;

    /**
     * Creates the application
     *
     */
    public ComputeGUI() {
        this.frame = new JFrame();
        this.openMenu = SwingUtils.createMenuItem("ComputeGUI.openMenu");
        this.exitMenu = SwingUtils.createMenuItem("ComputeGUI.exitMenu");
        this.fileChooser = new JFileChooser();
        this.gatesPanel = new GatesPanel();
        this.inputPanel = new KetPanel();
        this.outputPanel = new KetPanel();
        this.inputEditor = new KetEditor();
        createContent();
        createFlow();
    }

    /**
     * Returns the command line argument parser
     */
    private static ArgumentParser createParser() {
        ArgumentParser parser = ArgumentParsers.newFor(Compute.class.getName()).build()
                .defaultHelp(true)
                .version(Messages.getString("Compute.title"))
                .description("Compute the quantum state.");
        parser.addArgument("-v", "--version")
                .action(Arguments.version())
                .help("show current version");
        return parser;
    }

    /**
     * Application entry point
     *
     * @param args command line arguments
     */
    public static void main(java.lang.String[] args) {
        ArgumentParser parser = createParser();
        try {
            parser.parseArgs(args);
            new ComputeGUI().start();
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (Throwable e) {
            logger.atError().setCause(e).log("Error");
            System.exit(1);
        }
    }

    /**
     * Creates the action flow
     */
    private void createFlow() {
        openMenu.addActionListener(this::handleOpen);
        exitMenu.addActionListener(this::handleExit);
        inputEditor.readKet()
                .doOnNext(this::handleInputChanged)
                .subscribe();
    }

    /**
     * Creates the content
     */
    private void createContent() {
        fileChooser.setCurrentDirectory(new File("."));
        gatesPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("ComputeGUI.gatePanel.title")));
        inputPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("ComputeGUI.inputPanel.title")));
        outputPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("ComputeGUI.outputPanel.title")));
        inputEditor.setBorder(BorderFactory.createTitledBorder(Messages.getString("ComputeGUI.inputEditor.title")));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Messages.getString("Compute.title"));
        frame.setJMenuBar(createMenu());
        frame.setResizable(true);
        frame.setSize(1024, 800);

        new GridLayoutHelper<>(frame.getContentPane()).modify("insets,2 center fill vw,1")
                .modify("at,0,0").add(inputEditor)
                .modify("at,1,0").add(inputPanel)
                .modify("at,2,0 hw,1").add(gatesPanel)
                .modify("at,3,0 hw,0").add(outputPanel);
    }

    /**
     * Handle exit menu action
     *
     * @param actionEvent the event
     */
    private void handleExit(ActionEvent actionEvent) {
        frame.dispose();
    }

    /**
     * Handles the open action
     *
     * @param actionEvent the event
     */
    private void handleOpen(ActionEvent actionEvent) {
        int rc = fileChooser.showOpenDialog(null);
        if (rc == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.canRead()) {
                SwingUtils.showErrorKey("ComputGUI.errorDialog.title", "ComputGUI.fileCannotBeRead.text", file);
            } else {
                try {
                    handleGatesChanged(loadGates(Utils.fromFile(file), Locator.root()));
                } catch (Throwable e) {
                    SwingUtils.showErrorKey("ComputGUI.errorDialog.title", e);
                }
            }
        }
    }

    /**
     * Handle the gates changed event
     *
     * @param gates the gates
     */
    private void handleGatesChanged(QuGate[] gates) {
        this.gates = gates;
        gatesPanel.setGates(gates);
        compute();
        inputEditor.setNumQuBits(QuCircuitBuilder.numQuBits(gates));
        frame.validate();
    }

    /**
     * Computes the result
     */
    private void compute() {
        if (gates != null && input != null) {
            try {
                Matrix m = QuCircuitBuilder.build(gates);
                Ket result = input.mul(m);
                outputPanel.setKet(result);
            } catch (Exception e) {
                logger.atError().setCause(e).log("Error computing");
            }
        }
    }

    /**
     * @param ket the ket
     */
    private void handleInputChanged(Ket ket) {
        inputPanel.setKet(ket);
        this.input = ket;
        compute();
        frame.validate();
    }

    /**
     * Create the menu
     */
    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = SwingUtils.createMenu("ComputeGUI.fileMenu");
        fileMenu.add(openMenu);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitMenu);

        menuBar.add(fileMenu);
        return menuBar;
    }

    /**
     * Starts the application
     */
    private void start() {
        frame.setVisible(true);
    }
}
