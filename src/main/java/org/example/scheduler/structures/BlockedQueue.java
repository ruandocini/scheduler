package org.example.scheduler.structures;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.scheduler.process.ProcessControlBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockedQueue {

    private final Queue<BlockedProcess> blockedQueue = new LinkedList<>();

    public List<ProcessControlBlock> poll() {
        if (blockedQueue.size() == 0) {
            return Collections.emptyList();
        }

        List<ProcessControlBlock> unblockedProcesses = new ArrayList<>();
        Iterator<BlockedProcess> blockedProcessIterator = blockedQueue.iterator();
        while (blockedProcessIterator.hasNext()) {
            BlockedProcess blockedProcess = blockedProcessIterator.next();
            blockedProcess.decreaseBlockedPeriod();

            if (blockedProcess.blockedPeriod == 0) {
                blockedProcessIterator.remove();
                unblockedProcesses.add(blockedProcess.getProcessControlBlock());
            }
        }

        return unblockedProcesses;
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
