package cli;

import core.LectureMaker;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Options options = new Options();

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(false);
        options.addOption(output);

        Option thresh = new Option("t", "threshold", true, "silence threshold");
        thresh.setRequired(false);
        options.addOption(thresh);

        Option cl = new Option("cl", "cutlength", true, "minimum cutlength");
        cl.setRequired(false);
        options.addOption(cl);

        Option acfac = new Option("a", "accelerate", true, "acceleration factor");
        acfac.setRequired(false);
        options.addOption(acfac);

        Option inv = new Option("n", "noise", false, "cut out noise instead of silence");
        inv.setRequired(false);
        options.addOption(inv);

        Option help = new Option("h", "help", false, "print this message");
        help.setRequired(false);
        options.addOption(help);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        if(cmd.hasOption("h") || cmd.getArgs().length == 0) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("lasy <flags> LECTURE_RECORDINGS", options);
            return;
        }

        List<LectureMaker> llm = new ArrayList<>();
        for (String file : cmd.getArgs()) {
            LectureMaker lm = new LectureMaker(file);

            if (cmd.hasOption("o")) {
                lm.setOutputPath(cmd.getOptionValue("o"));
                break; // multiple input files with the same output file would make no sense
            }

            if (cmd.hasOption("t"))
                lm.setThreshold(Double.parseDouble(cmd.getOptionValue("o")));

            if (cmd.hasOption("cl"))
                lm.setMinCutLength(Double.parseDouble(cmd.getOptionValue("cl")));

            if (cmd.hasOption("a"))
                lm.setSpeedUpFactor(Float.parseFloat(cmd.getOptionValue("a")));

            if (cmd.hasOption("n"))
                lm.setInvert(true);

            llm.add(lm);
        }

        for (LectureMaker lm : llm) {
            try {
                lm.genFinal();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
