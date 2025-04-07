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
import org.mmarini.qucomp.apis.QuGate;
import org.mmarini.qucomp.swing.GatesPanel;
import org.mmarini.qucomp.swing.Messages;
import org.mmarini.swing.SwingUtils;
import org.mmarini.yaml.Locator;
import org.mmarini.yaml.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static org.mmarini.qucomp.apis.QuStateBuilder.loadGates;

/**
 * Computation GUI
 */
public class ComputeGUI {
    private static final Logger logger = LoggerFactory.getLogger(ComputeGUI.class);
    private final Namespace args;
    private final JFrame frame;
    private final JMenuItem exitMenu;
    private final JMenuItem openMenu;
    private final JFileChooser fileChooser;
    private final GatesPanel gatesPanel;
    private QuGate[] gates;

    /**
     * Creates the application
     *
     * @param args the parsed command line arguments
     */
    public ComputeGUI(Namespace args) {
        this.args = args;
        this.frame = new JFrame();
        this.openMenu = SwingUtils.createMenuItem("ComputeGUI.openMenu");
        this.exitMenu = SwingUtils.createMenuItem("ComputeGUI.exitMenu");
        this.fileChooser = new JFileChooser();
        this.gatesPanel = new GatesPanel();
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
    public static void main(String[] args) {
        ArgumentParser parser = createParser();
        try {
            new ComputeGUI(parser.parseArgs(args)).start();
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
    }

    /**
     * Creates the content
     */
    private void createContent() {
        fileChooser.setCurrentDirectory(new File("."));
        gatesPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("ComputeGUI.gatePanel.title")));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Messages.getString("Compute.title"));
        frame.setJMenuBar(createMenu());
        frame.setSize(800, 600);
        Container panel = frame.getContentPane();
        panel.add(gatesPanel);
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
