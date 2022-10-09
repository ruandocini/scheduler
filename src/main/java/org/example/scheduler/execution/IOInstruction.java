package org.example.scheduler.execution;

import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.Status;

import java.io.InterruptedIOException;

public class IOInstruction extends Instruction {
    @Override
    public void executeInstruction(ProcessControlBlock processControlBlock) throws InterruptedIOException {
        processControlBlock.setStatus(Status.BLOCKED);
        throw new InterruptedIOException();
    }

    @Override
    public void log(ProcessControlBlock processControlBlock) {
        System.out.println("E/S iniciada em " + processControlBlock.getProgramName());
    }
}
