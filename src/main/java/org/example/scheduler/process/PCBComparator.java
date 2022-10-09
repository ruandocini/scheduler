package org.example.scheduler.process;

import java.util.Comparator;

public class PCBComparator implements Comparator<ProcessControlBlock> {
    @Override
    public int compare(ProcessControlBlock o1, ProcessControlBlock o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority()) * -1;
    }
}
