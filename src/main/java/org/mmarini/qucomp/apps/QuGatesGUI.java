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
import net.sourceforge.argparse4j.inf.Namespace;
import org.mmarini.qucomp.apis.*;
import org.mmarini.qucomp.swing.GatesPanel;
import org.mmarini.qucomp.swing.KetEditor;
import org.mmarini.qucomp.swing.KetPanel;
import org.mmarini.qucomp.swing.Messages;
import org.mmarini.swing.GridLayoutHelper;
import org.mmarini.swing.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static io.reactivex.rxjava3.schedulers.Schedulers.computation;
import static io.reactivex.rxjava3.schedulers.Schedulers.io;
import static java.util.Objects.requireNonNull;
import static org.mmarini.swing.SwingUtils.centerOnScreen;
import static org.mmarini.swing.SwingUtils.showDialogMessage;

/**
 * Computation GUI
 */
public class QuGatesGUI {
    private static final Logger logger = LoggerFactory.getLogger(QuGatesGUI.class);

    /**
     * Returns the command line argument parser
     */
    private static ArgumentParser createParser() {
        ArgumentParser parser = ArgumentParsers.newFor(QuGatesGUI.class.getName()).build()
                .defaultHelp(true)
                .version(Messages.getString("Compute.title"))
                .description("Compute the quantum state.");
        parser.addArgument("-v", "--version")
                .action(Arguments.version())
                .help("show current version");
        parser.addArgument("gates")
                .nargs("?")
                .setDefault("qucomp.qg")
                .help("specify gates configuration file");
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
            Namespace parsedArgs = parser.parseArgs(args);
            new QuGatesGUI(parsedArgs).start();
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (Throwable e) {
            logger.atError().setCause(e).log("Error");
            System.exit(1);
        }
    }

    private final JFrame frame;
    private final JMenuItem exitMenu;
    private final JMenuItem openMenu;
    private final JFileChooser fileChooser;
    private final GatesPanel gatesPanel;
    private final KetPanel inputPanel;
    private final KetPanel outputPanel;
    private final KetEditor inputEditor;
    private final JSplitPane inGateOutSplitPanel;
    private final JSplitPane gateOutSplitPanel;
    private final Namespace args;
    private Ket input;
    private Matrix matrix;

    /**
     * Creates the application
     *
     * @param parsedArgs the parsed command line arguments
     */
    public QuGatesGUI(Namespace parsedArgs) {
        this.args = requireNonNull(parsedArgs);
        this.frame = new JFrame();
        this.openMenu = SwingUtils.createMenuItem("ComputeGUI.openMenu");
        this.exitMenu = SwingUtils.createMenuItem("ComputeGUI.exitMenu");
        this.fileChooser = new JFileChooser();
        this.gatesPanel = new GatesPanel();
        this.inputPanel = new KetPanel();
        this.outputPanel = new KetPanel();
        this.inputEditor = new KetEditor();
        this.inGateOutSplitPanel = new JSplitPane();
        this.gateOutSplitPanel = new JSplitPane();
        createContent();
        createFlow();
    }

    /**
     * Computes the result
     */
    private void compute() {
        if (matrix != null && input != null) {
            Ket currentInput = this.input;
            Matrix currentMatrix = this.matrix;
            computation().scheduleDirect(() -> {
                try {
                    outputPanel.setKet(currentInput.mul(currentMatrix));
                    frame.validate();
                    frame.repaint();
                } catch (Exception e) {
                    logger.atError().setCause(e).log("Error computing");
                }
            });
        }
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

        inGateOutSplitPanel.setLeftComponent(inputPanel);
        inGateOutSplitPanel.setRightComponent(gateOutSplitPanel);
        inGateOutSplitPanel.setOneTouchExpandable(true);
        inGateOutSplitPanel.setResizeWeight(0);
        inGateOutSplitPanel.setContinuousLayout(true);

        gateOutSplitPanel.setLeftComponent(gatesPanel);
        gateOutSplitPanel.setRightComponent(outputPanel);
        gateOutSplitPanel.setOneTouchExpandable(true);
        gateOutSplitPanel.setResizeWeight(1);
        gateOutSplitPanel.setContinuousLayout(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Messages.getString("Compute.title"));
        frame.setJMenuBar(createMenu());
        frame.setResizable(true);
        frame.setSize(1024, 800);

        new GridLayoutHelper<>(frame.getContentPane()).modify("insets,2 center fill vw,1")
                .modify("at,0,0").add(inputEditor)
                .modify("at,1,0 hw,1").add(inGateOutSplitPanel);
    }

    /**
     * Creates the action flow
     */
    private void createFlow() {
        openMenu.addActionListener(this::onOpen);
        exitMenu.addActionListener(this::onExit);
        inputEditor.readKet()
                .doOnNext(this::onInputChanged)
                .subscribe();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                onFrameOpen();
            }
        });
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
     * Handle exit menu action
     *
     * @param actionEvent the event
     */
    private void onExit(ActionEvent actionEvent) {
        frame.dispose();
    }

    /**
     * Handle the frame open
     */
    private void onFrameOpen() {
        gateOutSplitPanel.setDividerLocation(6d / 8d);
        inGateOutSplitPanel.setDividerLocation(2d / 10d);
    }

    /**
     * Handle the gates changed event
     *
     * @param gates the gates
     */
    private void onGatesChanged(List<QuGate> gates) {
        this.matrix = QuCircuitBuilder.build(gates);
        this.input = null;
        gatesPanel.setGates(gates);
        inputEditor.setNumQuBits(QuCircuitBuilder.numQuBits(gates));
        compute();
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }

    /**
     * @param ket the ket
     */
    private void onInputChanged(Ket ket) {
        inputPanel.setKet(ket);
        this.input = ket;
        compute();
        frame.validate();
        frame.repaint();

    }

    /**
     * Handles the open action
     *
     * @param actionEvent the event
     */
    private void onOpen(ActionEvent actionEvent) {
        int rc = fileChooser.showOpenDialog(null);
        if (rc == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            JDialog d = showDialogMessage("ComputeGUI.loadingDialog.title", "ComputeGUI.loadingDialog.text");
            io().scheduleDirect(() -> {
                open(file);
                d.dispose();
            });
        }
    }

    /**
     * Open file
     *
     * @param file the file
     */
    private void open(File file) {
        if (!file.canRead()) {
            SwingUtils.showErrorKey("ComputeGUI.errorDialog.title", "ComputeGUI.fileCannotBeRead.text", file);
        } else {
            try {
                QuParser parser = QuParser.create(file);
                try {
                    List<QuGate> gates = parser.parse();
                    onGatesChanged(gates);
                } catch (IllegalArgumentException e) {
                    SwingUtils.showFormattedError("ComputeGUI.errorDialog.title", parser.fullErrorMessages());
                }
            } catch (FileNotFoundException e) {
                SwingUtils.showErrorKey("ComputeGUI.errorDialog.title", e.getMessage());
            }
        }
    }

    /**
     * Starts the application
     */
    private void start() {
        open(new File(args.getString("gates")));
        centerOnScreen(frame);
        frame.setVisible(true);
    }
}
