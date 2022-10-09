package org.example.scheduler.execution;

import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.Status;

import java.io.InterruptedIOException;

public abstract class Instruction {

    public void run(ProcessControlBlock processControlBlock) throws InterruptedIOException {
        processControlBlock.setStatus(Status.RUNNING);
        executeInstruction(processControlBlock);
        log(processControlBlock);
    }

    public abstract void executeInstruction(ProcessControlBlock processControlBlock) throws InterruptedIOException;

    public abstract void log(ProcessControlBlock processControlBlock);
}
