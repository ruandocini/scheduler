package org.example.scheduler.execution;

import org.example.scheduler.process.ProcessControlBlock;

public class AssignInstruction extends Instruction {
    private String register;

    private int value;

    public AssignInstruction(String command) {
        String[] parsedCommand = command.split("=");

        this.register = parsedCommand[0];
        this.value = Integer.parseInt(parsedCommand[1]);
    }

    @Override
    public void executeInstruction(ProcessControlBlock processControlBlock) {
        if ("X".equals(register)) {
            processControlBlock.setX(value);
        } else {
            processControlBlock.setY(value);
        }
    }

    @Override
    public void log(ProcessControlBlock processControlBlock) {
    }
}
