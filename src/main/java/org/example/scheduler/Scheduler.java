package org.example.scheduler;

import lombok.Getter;
import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.ProcessTable;
import org.example.scheduler.process.Status;
import org.example.scheduler.structures.BlockedQueue;
import org.example.scheduler.structures.ReadyQueue;

import java.io.InterruptedIOException;
import java.util.*;

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
        float averageInstructionsRan = 0;
        float quantumsRan = 0;

        Map<String, Integer> chagesPerProcess = new HashMap<String,Integer>();

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

            if (chagesPerProcess.containsKey(running.getProgramName())){
                int currentValue = chagesPerProcess.get(running.getProgramName());
                currentValue++;
                chagesPerProcess.put(running.getProgramName(),currentValue);
            } else {
                chagesPerProcess.put(running.getProgramName(),1);
            }

            running.consumeCredit();

            if (!areThereCreditsLeft()) {
                resetCredits();
            }

            unblockProcessIfDoneWithIO();
            requeueProcessIfRequired();

            if (!Status.DONE.equals(running.getStatus()))
                System.out.printf("Interrompendo %s após %s instruções%n", running.getProgramName(), instructionsRan);
                averageInstructionsRan = averageInstructionsRan + instructionsRan;

            running = readyQueue.poll();
            quantumsRan++;
        }

        float totalInterruptions = 0;
        int processes = 0;
        for (Map.Entry<String, Integer> entry : chagesPerProcess.entrySet()) {
//            System.out.printf("Processo %s: %s interrupções\n", entry.getKey(), entry.getValue());
            processes++;
            totalInterruptions = totalInterruptions + entry.getValue();
        }

        System.out.printf("MEDIA DE TROCAS: %s\n", totalInterruptions/processes);
        System.out.printf("MEDIA DE INSTRUÇÕES: %s\n", averageInstructionsRan/quantumsRan);
        System.out.printf("QUANTUM: %s\n", quantum);
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
