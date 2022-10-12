package org.example.scheduler;

import lombok.Getter;
import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.Status;
import org.example.scheduler.structures.BlockedQueue;

import java.io.InterruptedIOException;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler {
    private final Map<String, ProcessControlBlock> processTable;

    private final BlockedQueue blockedQueue = new BlockedQueue();
    private final Queue<ProcessControlBlock> readyQueue = new PriorityQueue<>();

    @Getter
    private ProcessControlBlock running;

    private final int quantum;

    public Scheduler(Map<String, ProcessControlBlock> processTable, int quantum) {
        this.processTable = processTable;
        this.quantum = quantum;

        readyQueue.addAll(this.processTable.values());

        this.running = readyQueue.poll();

        System.out.println("Processos carregados em ordem de créditos iniciais:");
        System.out.println();
        System.out.println(running);
        //FIXME: This does not print nodes in order of priority
        this.readyQueue.forEach(System.out::println);
        System.out.println();
    }

    public void run() {
        while (processTable.size() > 0) {
            if (running == null) {
                unblockProcessIfDoneWithIO();
                running = readyQueue.poll();
                continue;
            }

            int instructionsRan = 0;
            System.out.printf("Executando %s%n", running.getProgramName());

            for (int i = 0; i < quantum; i++) {
                try {
                    if (running.getStatus() == Status.DONE) break;

                    running.getProgram()[running.getPc()].run(running);
                } catch (InterruptedIOException e) {
                    break;
                } finally {
                    instructionsRan++;
                    running.increasePC();
                }
            }

            //TODO: Implementar lógica de reset dos créditos

            running.consumeCredit();

            unblockProcessIfDoneWithIO();
            requeueProcessIfRequired();

            if (!Status.DONE.equals(running.getStatus()))
                System.out.printf("Interrompendo %s após %s instruções%n", running.getProgramName(), instructionsRan);

            running = readyQueue.poll();

        }
    }

    private void requeueProcessIfRequired() {
        switch (running.getStatus()) {
            case RUNNING:
            case READY:
                readyQueue.add(running);
                running.setStatus(Status.READY);
                break;
            case BLOCKED:
                blockedQueue.add(running);
                break;
            case DONE:
                processTable.remove(running.getProgramName());
                break;
        }
    }

    public void unblockProcessIfDoneWithIO() {
        Optional<ProcessControlBlock> unblockedProcess = blockedQueue.poll();

        unblockedProcess.ifPresent(readyQueue::add);
    }
}
