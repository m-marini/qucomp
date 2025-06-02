package org.mmarini.qucomp.apps;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.mmarini.MapStream;
import org.mmarini.Tuple2;
import org.mmarini.qucomp.compiler.Compiler;
import org.mmarini.qucomp.compiler.Processor;
import org.mmarini.qucomp.compiler.Syntax;
import org.mmarini.qucomp.compiler.Tokenizer;
import org.mmarini.qucomp.swing.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

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
        parser.addArgument("-d", "--dump")
                .action(Arguments.storeTrue())
                .help("specify variable dump");
        parser.addArgument("-f", "--file")
                .setDefault("qucomp.qu")
                .help("specify qu source file");
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
            File file = new File(args1.getString("file"));
            Tokenizer tokenizer = Tokenizer.create(file).open();

            Compiler compiler = Compiler.create();
            Processor processor = new Processor();

            Syntax.rule("<code-unit>")
                    .parse(compiler.createParseContext(tokenizer));
            Object results = compiler.pop().evaluate(processor);
            if (results instanceof Object[] outs) {
                for (Object out : outs) {
                    if (out != null) {
                        System.out.println(out);
                        logger.atDebug().log("{}", out);
                    }
                }
            }
            if (args1.getBoolean("dump")) {
                MapStream.of(processor.variables())
                        .tuples()
                        .sorted(Comparator.comparing(Tuple2::getV1))
                        .forEach(t -> {
                            System.out.print(t._1);
                            System.out.print(" = ");
                            System.out.println(t._2);
                        });
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (IOException e) {
            logger.atError().setCause(e).log("IO Error");
            System.exit(1);
        }
    }
}
