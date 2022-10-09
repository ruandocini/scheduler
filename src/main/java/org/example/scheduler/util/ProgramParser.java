package org.example.scheduler.util;

import com.google.common.base.Strings;
import org.example.scheduler.execution.AssignInstruction;
import org.example.scheduler.execution.CommandInstruction;
import org.example.scheduler.execution.ExitInstruction;
import org.example.scheduler.execution.IOInstruction;
import org.example.scheduler.execution.Instruction;
import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.Status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgramParser {

    private static final String FILE_TEMPLATE = "programas/%s.txt";

    public static int getQuantum() throws IOException {
        String[] lines = readFileLineByLine(new File(String.format(FILE_TEMPLATE, "quantum")));

        return Integer.parseInt(lines[0]);
    }

    public static Map<String, ProcessControlBlock> assembleProcessTable() throws IOException {
        Map<String, ProcessControlBlock> processTable = new HashMap<>();
        String[] priorities = readFileLineByLine(new File(String.format(FILE_TEMPLATE, "prioridades")));

        for (int i = 1; i <= 10; i++) {
            ProcessControlBlock process = fetchProcessFromFile(
                    new File(
                            String.format(
                                    FILE_TEMPLATE,
                                    Strings.padStart(String.valueOf(i), 2, '0')
                            )
                    ),
                    Integer.parseInt(priorities[i - 1])
            );

            processTable.put(process.getProgramName(), process);
        }

        return processTable;
    }

    private static ProcessControlBlock fetchProcessFromFile(File programFile, int priority) throws IOException {
        String[] lines = readFileLineByLine(programFile);
        String programName = lines[0];
        String[] commands = Arrays.copyOfRange(lines, 1, lines.length);
        Instruction[] program = getInstructions(commands);

        return ProcessControlBlock
                .builder()
                .pc(0)
                .program(program)
                .programName(programName)
                .priority(priority)
                .credits(priority)
                .x(0)
                .y(0)
                .status(Status.READY)
                .build();
    }

    private static String[] readFileLineByLine(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        List<String> commands = new ArrayList<>();
        String line = bufferedReader.readLine();

        while (line != null) {
            commands.add(line);
            line = bufferedReader.readLine();
        }

        return commands.toArray(String[]::new);
    }

    private static Instruction[] getInstructions(String[] commands) {
        return Arrays
                .stream(commands)
                .map(ProgramParser::identifyInstruction)
                .collect(Collectors.toList())
                .toArray(Instruction[]::new);
    }

    private static Instruction identifyInstruction(String command) {
        switch (command) {
            case "COM":
                return new CommandInstruction();
            case "E/S":
                return new IOInstruction();
            case "SAIDA":
                return new ExitInstruction();
            default:
                return new AssignInstruction(command);
        }
    }
}
