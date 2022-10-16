package org.example.scheduler;

import lombok.Getter;
import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.ProcessTable;
import org.example.scheduler.process.Status;
import org.example.scheduler.structures.BlockedQueue;
import org.example.scheduler.structures.ReadyQueue;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Scheduler {
    private final ProcessTable processTable;

    private final BlockedQueue blockedQueue = new BlockedQueue();
    private final ReadyQueue readyQueue = new ReadyQueue();

    @Getter
    private ProcessControlBlock running;

    private final int quantum;

    public Scheduler(ProcessTable processTable, int quantum) {
        this.processTable = processTable;
        this.quantum = quantum;

        readyQueue.addAll(this.processTable.values());

        this.running = readyQueue.poll();

        System.out.println("Processos carregados em ordem de créditos iniciais:");
        System.out.println();

        ProcessControlBlock[] pcbArray = processTable.values().toArray(ProcessControlBlock[]::new);
        Arrays.sort(pcbArray);
        Arrays.stream(pcbArray).forEach(System.out::println);

        System.out.println();
    }

    public void run() {
        while (processTable.size() > 0) {
            if (running == null) {
                unblockProcessIfDoneWithIO();
                running = readyQueue.poll();
                continue;
            }

            System.out.println("------------------------");

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

            running.consumeCredit();

            if (!areThereCreditsLeft()) {
                resetCredits();
            }

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
        List<ProcessControlBlock> unblockedProcess = blockedQueue.poll();

        readyQueue.addAll(unblockedProcess);
    }

    private boolean areThereCreditsLeft() {
        return processTable
                .values()
                .stream()
                .anyMatch(pcb -> pcb.getCredits() > 0);
    }

    private void resetCredits() {
        processTable
                .values()
                .forEach(ProcessControlBlock::resetCredits);
    }
}
