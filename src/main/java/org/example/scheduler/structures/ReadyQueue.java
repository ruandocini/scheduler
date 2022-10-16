package org.example.scheduler.structures;

import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.Status;

import java.util.PriorityQueue;

public class ReadyQueue extends PriorityQueue<ProcessControlBlock> {

    @Override
    public boolean add(ProcessControlBlock processControlBlock) {
        processControlBlock.setStatus(Status.READY);

        return super.add(processControlBlock);
    }
}
