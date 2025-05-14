package org.mmarini.qucomp.apps;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.mmarini.qucomp.apis.*;
import org.mmarini.qucomp.swing.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

/**
 * Computes the quantum state
 */
public class Compute {

    private static final Logger logger = LoggerFactory.getLogger(Compute.class);

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
        parser.addArgument("gates")
                .metavar("GATES")
                .setDefault("qucomp.qg")
                .help("specify gates configuration file");
        parser.addArgument("inputs")
                .metavar("INPUTS")
                .type(java.lang.String.class)
                .nargs(1)
                .help("specify inputs ket expression");
        return parser;
    }

    /**
     * Application entry point
     *
     * @param args command line arguments
     */
    public static void main(java.lang.String[] args) {
        new Compute().start(args);
    }

    /**
     * Starts the computatino app
     *
     * @param args the argument
     */
    private void start(java.lang.String[] args) {
        ArgumentParser parser = createParser();
        try {
            Namespace args1 = parser.parseArgs(args);
            File file = new File(args1.getString("gates"));
            QuParser gateParser = QuParser.create(file);
            List<QuGate> gates = gateParser.parse();
            Matrix m = QuCircuitBuilder.build(gates);
            List<String> inText = args1.get("inputs");
            Ket inputs = Ket.fromText(inText.getFirst());
            if (inputs.values().length != m.numCols()) {
                throw new IllegalArgumentException(format("input ket expression state number %d must match the matrix gates size %d",
                        inputs.values().length, m.numCols()));
            }
            Ket result = inputs.mul(m);
            logger.atInfo().log("output = {}", result.toString());
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (IOException e) {
            logger.atError().setCause(e).log("IO Error");
            System.exit(1);
        }
    }
}
