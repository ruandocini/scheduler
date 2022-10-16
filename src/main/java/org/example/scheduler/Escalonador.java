package org.example.scheduler;

import com.google.common.base.Strings;
import org.example.scheduler.util.ProgramParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Escalonador {
    public static void main( String[] args ) throws IOException {
        int quantum = ProgramParser.getQuantum();

        Files.createDirectories(Paths.get("resultados"));
        File logFile = new File(
                String.format(
                        "resultados/log%s.txt",
                        Strings.padStart(String.valueOf(quantum), 2, '0')
                )
        );
        logFile.createNewFile();

        try (PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile)))) {
            System.setOut(ps);

            Scheduler scheduler = new Scheduler(ProgramParser.assembleProcessTable(), quantum);
            scheduler.run();
        }
    }
}
