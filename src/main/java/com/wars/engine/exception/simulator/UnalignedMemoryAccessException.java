package com.wars.engine.exception.simulator;

public class UnalignedMemoryAccessException extends SimulatorException {
    public UnalignedMemoryAccessException(int address) {
        super("Fetch address is not word aligned at address: " + address);
    }
}
