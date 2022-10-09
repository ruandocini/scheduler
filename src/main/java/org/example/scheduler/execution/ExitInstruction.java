package org.example.scheduler.execution;

import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.Status;

public class ExitInstruction extends Instruction {
    @Override
    public void executeInstruction(ProcessControlBlock processControlBlock) {

    }

    @Override
    public void log(ProcessControlBlock processControlBlock) {
        System.out.printf(
                "%s terminado. X=%s. Y=%s%n",
                processControlBlock.getProgramName(),
                processControlBlock.getX(),
                processControlBlock.getY()
        );
        processControlBlock.setStatus(Status.DONE);
    }
}
