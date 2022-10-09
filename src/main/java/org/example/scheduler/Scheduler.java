package org.example.scheduler;

import lombok.Getter;
import org.example.scheduler.process.PCBComparator;
import org.example.scheduler.process.ProcessControlBlock;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler {
    private final Map<String, ProcessControlBlock> processTable;

    private final Queue<ProcessControlBlock> blockedQueue = new PriorityQueue<>(new PCBComparator());
    private final Queue<ProcessControlBlock> readyQueue = new PriorityQueue<>(new PCBComparator());

    @Getter
    private ProcessControlBlock running;

    public Scheduler(Map<String, ProcessControlBlock> processTable) {
        this.processTable = processTable;

        readyQueue.addAll(this.processTable.values());

        this.running = readyQueue.poll();
    }
}
