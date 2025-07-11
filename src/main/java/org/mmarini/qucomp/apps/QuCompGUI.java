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

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.mmarini.qucomp.compiler.*;
import org.mmarini.qucomp.swing.Messages;
import org.mmarini.qucomp.swing.VariablePanel;
import org.mmarini.swing.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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
                .setDefault("qucomp.qu")
                .help("specify qu source file");
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
    private final JMenuItem saveMenu;
    private final JMenuItem saveAsMenu;
    private final JMenuItem runMenu;
    private final JFileChooser fileChooser;
    private final Namespace args;
    private final JEditorPane codeEditor;
    private final JTextArea errorPanel;

    private final Compiler compiler;
    private final SyntaxRule syntax;
    private final Processor processor;
    private final VariablePanel varPanel;
    private final JSplitPane execPanel;
    private File sourceFile;

    /**
     * Creates the application
     *
     * @param parsedArgs the parsed arguments
     */
    public QuCompGUI(Namespace parsedArgs) throws QuException {
        this.args = requireNonNull(parsedArgs);
        this.frame = new JFrame();
        this.openMenu = SwingUtils.createMenuItem("ComputeGUI.openMenu");
        this.saveMenu = SwingUtils.createMenuItem("ComputeGUI.saveMenu");
        this.saveAsMenu = SwingUtils.createMenuItem("ComputeGUI.saveAsMenu");
        this.runMenu = SwingUtils.createMenuItem("QuCompGUI.runMenu");
        this.exitMenu = SwingUtils.createMenuItem("ComputeGUI.exitMenu");
        this.fileChooser = new JFileChooser();
        this.codeEditor = new JEditorPane();
        this.errorPanel = new JTextArea();
        this.syntax = Syntax.rule("<code-unit>");
        this.compiler = Compiler.create();
        this.processor = new Processor();
        this.varPanel = new VariablePanel();
        this.execPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

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
        FileNameExtensionFilter filter = new FileNameExtensionFilter("qu file", "qu");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        codeEditor.setFont(Font.decode(Font.MONOSPACED).deriveFont(Font.BOLD, 14));

        errorPanel.setFont(Font.decode(Font.MONOSPACED).deriveFont(Font.BOLD, 14));
        errorPanel.setEditable(false);
        errorPanel.setRows(2);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Messages.getString("ComputeGUI.title"));
        frame.setJMenuBar(createMenu());
        frame.setResizable(true);
        frame.setSize(1024, 700);

        // Creates the execution panel
        execPanel.setTopComponent(new JScrollPane(codeEditor));
        execPanel.setBottomComponent(new JScrollPane(errorPanel));
        execPanel.setDividerLocation(0.75);
        execPanel.setResizeWeight(1);
        execPanel.setContinuousLayout(true);
        execPanel.setOneTouchExpandable(true);

        // Create the main tab panel
        JTabbedPane tabPanel = new JTabbedPane();
        tabPanel.add(Messages.getString("QuCompGui.execPanel.title"), execPanel);
        tabPanel.add(Messages.getString("QuCompGui.varPanel.title"), varPanel);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(tabPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the action flow
     */
    private void createFlow() {
        openMenu.addActionListener(this::onOpen);
        saveMenu.addActionListener(this::onSave);
        saveAsMenu.addActionListener(this::onSaveAs);
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
        fileMenu.add(saveMenu);
        fileMenu.add(saveAsMenu);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitMenu);

        JMenu execMenu = SwingUtils.createMenu("QuCompGUI.execMenu");
        execMenu.add(runMenu);

        menuBar.add(fileMenu);
        menuBar.add(execMenu);
        return menuBar;
    }

    /**
     * Handles the open action
     *
     * @param actionEvent the event
     */
    private void onOpen(ActionEvent actionEvent) {
        if (sourceFile != null) {
            fileChooser.setSelectedFile(sourceFile);
        }
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
     * Handles save menu event
     *
     * @param actionEvent event
     */
    private void onSave(ActionEvent actionEvent) {
        save(sourceFile);
    }

    /**
     * Handles save as menu event
     *
     * @param actionEvent the event
     */
    private void onSaveAs(ActionEvent actionEvent) {
        int rc = fileChooser.showSaveDialog(null);
        if (rc == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            save(file);
        }
    }

    /**
     * Executes the code
     *
     * @param source the source code
     */
    void execute(String source) {
        try {
            errorPanel.setText("Running code ...");
            // Compile and execute
            Value.ListValue values = (Value.ListValue) compile(source).evaluate(processor);
            StringBuilder text = new StringBuilder();
            for (Value value : values.value()) {
                if (value != null) {
                    Arrays.stream(value.source().fullReportMessage(value.toString()))
                            .forEach(s -> text.append(s).append("\n"));
                }
                text.append("\n");
            }
            errorPanel.setText(text.toString());
            varPanel.setVariables(processor.variables());
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
        } finally {
            runMenu.setEnabled(true);
            codeEditor.setEnabled(true);
        }
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
        execPanel.setDividerLocation(0.75);
    }

    /**
     * Opens the source file
     *
     * @param code the source file
     */
    private void open(File code) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(code));
            codeEditor.setText(reader.lines()
                    .map(line -> line + "\n")
                    .reduce(String::concat)
                    .orElse(""));
            this.sourceFile = code;
            frame.setTitle(Messages.format("ComputeGUI.fileTitle", code.toString()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles run event
     *
     * @param actionEvent the event
     */
    private void onRun(ActionEvent actionEvent) {
        runMenu.setEnabled(false);
        codeEditor.setEnabled(false);
        String text = source();
        Completable.fromAction(() ->
                        execute(text))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /**
     * Saves the code in file
     *
     * @param code the file
     */
    private void save(File code) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(code))) {
            writer.print(codeEditor.getText());
            this.sourceFile = code;
            frame.setTitle(Messages.format("ComputeGUI.fileTitle", code.toString()));
        } catch (IOException e) {
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
