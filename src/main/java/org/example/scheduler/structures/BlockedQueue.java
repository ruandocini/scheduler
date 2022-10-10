package org.example.scheduler.structures;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.scheduler.process.ProcessControlBlock;
import org.example.scheduler.process.Status;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class BlockedQueue {

    private Queue<BlockedProcess> blockedQueue = new LinkedList<>();

    public Optional<ProcessControlBlock> poll() {
        if (blockedQueue.peek() == null) {
            return Optional.empty();
        }

        if (blockedQueue.peek().blockedPeriod == 0) {
            ProcessControlBlock processControlBlock = blockedQueue.poll().getProcessControlBlock();
            processControlBlock.setStatus(Status.READY);
            return Optional.of(processControlBlock);
        }

        blockedQueue.peek().decreaseBlockedPeriod();
        return Optional.empty();
    }

    public void add(ProcessControlBlock processControlBlock) {
        blockedQueue.add(new BlockedProcess(2, processControlBlock));
    }

    @Data
    @AllArgsConstructor
    private static class BlockedProcess {
        private int blockedPeriod;

        private ProcessControlBlock processControlBlock;

        public void decreaseBlockedPeriod() {
            if (blockedPeriod != 0) blockedPeriod--;
        }
    }
}
