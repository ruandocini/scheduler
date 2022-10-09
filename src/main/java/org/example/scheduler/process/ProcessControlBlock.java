package org.example.scheduler.process;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ProcessControlBlock {

    private String programName;

    @Setter
    private int pc;

    @Setter
    private Status status;

    @Setter
    private int priority;

    @Setter
    private int x;

    @Setter
    private int y;

    private String[] program;
}
