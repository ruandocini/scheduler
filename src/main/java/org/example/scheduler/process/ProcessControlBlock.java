package org.example.scheduler.process;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.scheduler.execution.Instruction;

@Getter
@Builder
public class ProcessControlBlock implements Comparable<ProcessControlBlock> {

    private String programName;

    private int pc;

    private int priority;

    private int credits;

    @Setter
    private Status status;

    @Setter
    private int x;

    @Setter
    private int y;

    private Instruction[] program;

    public void increasePC() {
        if (pc == program.length - 1) {
            return;
        }

        pc++;
    }

    public void consumeCredit() {
        if (credits == 0) {
            return;
        }

        credits--;
    }

    public void resetCredits() {
        this.credits = this.priority;
    }

    @Override
    public String toString() {
        return programName;
    }

    @Override
    public int compareTo(ProcessControlBlock o) {
        int comparisonResult = Integer.compare(credits, o.getCredits()) * -1;

        if (comparisonResult == 0) {
            if (this.status == Status.RUNNING)
                return -1;
            else if (o.status == Status.RUNNING)
                return 1;
        }

        return comparisonResult;
    }
}
