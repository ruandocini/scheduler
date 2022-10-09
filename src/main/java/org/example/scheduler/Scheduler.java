package org.example.scheduler;

import lombok.Getter;
import org.example.scheduler.process.PCBComparator;
import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.Status;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler {
    private final Map<String, ProcessControlBlock> processTable;

    private final Queue<ProcessControlBlock> blockedQueue = new PriorityQueue<>(new PCBComparator());
    private final Queue<ProcessControlBlock> readyQueue = new PriorityQueue<>(new PCBComparator());

    @Getter
    private ProcessControlBlock running;

    private final int quantum;

    public Scheduler(Map<String, ProcessControlBlock> processTable, int quantum) {
        this.processTable = processTable;
        this.quantum = quantum;

        readyQueue.addAll(this.processTable.values());

        this.running = readyQueue.poll();
    }

    public void run() {
        while (processTable.size() > 0 && running != null) {
            int instructionsRan = 0;

            for (int i = 0; i < quantum; i++) {
                try {
                    running.getProgram()[running.getPc()].run(running);
                } catch (InterruptedIOException e) {
                    break;
                } finally {
                    instructionsRan++;
                    running.increasePC();
                }
            }

            // TODO: Implementar lógica de desbloqueio de items bloqueados

            running.consumeCredit();

            requeueProcessIfRequired();

            System.out.printf("Interrompendo %s após %s instruções%n", running.getProgramName(), instructionsRan);

            running = readyQueue.poll();

        }
    }

    private void requeueProcessIfRequired() {
        switch (running.getStatus()) {
            case RUNNING:
            case READY:
                running.setStatus(Status.READY);
                readyQueue.add(running);
                break;
            case BLOCKED:
                blockedQueue.add(running);
                break;
            case DONE:
                processTable.remove(running.getProgramName());
                break;
        }
    }
}
