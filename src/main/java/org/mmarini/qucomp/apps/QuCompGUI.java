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
import org.mmarini.qucomp.compiler.*;
import org.mmarini.qucomp.swing.Messages;
import org.mmarini.swing.GridLayoutHelper;
import org.mmarini.swing.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Arrays;

import static io.reactivex.rxjava3.schedulers.Schedulers.io;
import static java.util.Objects.requireNonNull;
import static org.mmarini.swing.SwingUtils.centerOnScreen;
import static org.mmarini.swing.SwingUtils.showDialogMessage;

/**
 * User interface to process quantum code
 */
public class QuCompGUI {
    private static final Logger logger = LoggerFactory.getLogger(QuCompGUI.class);

    /**
     * Returns the command line argument parser
     */
    private static ArgumentParser createParser() {
        ArgumentParser parser = ArgumentParsers.newFor(QuGatesGUI.class.getName()).build()
                .defaultHelp(true)
                .version(Messages.getString("QuCompute.title"))
                .description("Process quantum code.");
        parser.addArgument("-f", "--file")
                .setDefault("qucomp.qu");
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
            Namespace parsedArgs = parser.parseArgs(args);
            new QuCompGUI(parsedArgs).start();
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
    private final JMenuItem runMenu;
    private final JFileChooser fileChooser;
    private final Namespace args;
    private final JEditorPane codeEditor;
    private final JTextArea errorPanel;

    private final Compiler compiler;
    private final SyntaxRule syntax;
    private final Processor processor;

    /**
     * Creates the application
     *
     * @param parsedArgs the parsed arguments
     */
    public QuCompGUI(Namespace parsedArgs) throws QuException {
        this.args = requireNonNull(parsedArgs);
        this.frame = new JFrame();
        this.openMenu = SwingUtils.createMenuItem("ComputeGUI.openMenu");
        this.runMenu = SwingUtils.createMenuItem("QuCompGUI.runMenu");
        this.exitMenu = SwingUtils.createMenuItem("ComputeGUI.exitMenu");
        this.fileChooser = new JFileChooser();
        this.codeEditor = new JEditorPane();
        this.errorPanel = new JTextArea();
        this.syntax = Syntax.rule("<code-unit>");
        this.compiler = Compiler.create();
        this.processor = new Processor(obj -> {
        });

        createContent();
        createFlow();
    }

    private CommandNode compile(String source) throws IOException {
        Tokenizer tokenizer = Tokenizer.create(source).open();
        syntax.parse(compiler.createParseContext(tokenizer));
        return compiler.pop();
    }

    /**
     * Creates the content
     */
    private void createContent() {
        fileChooser.setCurrentDirectory(new File("."));

        codeEditor.setFont(Font.decode(Font.MONOSPACED).deriveFont(Font.BOLD, 14));

        errorPanel.setFont(Font.decode(Font.MONOSPACED).deriveFont(Font.BOLD, 14));
        errorPanel.setEditable(false);
        errorPanel.setRows(2);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Messages.getString("Compute.title"));
        frame.setJMenuBar(createMenu());
        frame.setResizable(true);
        frame.setSize(1024, 700);

        new GridLayoutHelper<>(frame.getContentPane()).modify("insets,2 center fill vw,1 hw,1")
                .modify("at,0,0").add(new JScrollPane(codeEditor))
                .modify("at,0,1 vw,0").add(errorPanel);
    }

    /**
     * Creates the action flow
     */
    private void createFlow() {
        openMenu.addActionListener(this::onOpen);
        exitMenu.addActionListener(this::onExit);
        runMenu.addActionListener(this::onRun);
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

        JMenu execMenu = SwingUtils.createMenu("QuCompGUI.execMenu");
        execMenu.add(runMenu);

        menuBar.add(fileMenu);
        menuBar.add(execMenu);
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

    private void onRun(ActionEvent actionEvent) {
        try {
            // Compile
            Object value = processor.evaluate(compile(source()));
            // Process
            errorPanel.setText(value.toString());
        } catch (QuSourceException e) {
            SourceContext ctx = e.context();
            String[] msg = ctx.fullReportMessage(e.getMessage());
            errorPanel.setText(Arrays.stream(msg)
                    .map(a -> a + "\n")
                    .reduce(String::concat).orElse(""));
            Element root = codeEditor.getDocument().getDefaultRootElement();
            int startOfLineOffset = root.getElement(ctx.lineNumber() - 1).getStartOffset();
            codeEditor.setCaretPosition(startOfLineOffset + ctx.position());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    private void open(File code) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(code));
            codeEditor.setText(reader.lines()
                    .map(line -> line + "\n")
                    .reduce(String::concat)
                    .orElse(""));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the source code
     */
    private String source() {
        return codeEditor.getText();
    }

    /**
     * Starts the application
     */
    private void start() {
        open(new File(args.getString("file")));
        centerOnScreen(frame);
        frame.setVisible(true);
    }
}
