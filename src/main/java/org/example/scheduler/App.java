package org.example.scheduler;

import org.example.scheduler.util.ProgramParser;

import java.io.IOException;

public class App {
    public static void main( String[] args ) throws IOException {
        Scheduler scheduler = new Scheduler(ProgramParser.assembleProcessTable());

        System.out.print("TEST");
    }
}
