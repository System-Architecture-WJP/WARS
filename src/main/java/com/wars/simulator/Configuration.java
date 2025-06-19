package com.wars.simulator;


public class Configuration {
    private long pc;
    private final int[] gpr;
    private final byte[] memory;

    public Configuration(int memorySize) {
        this.pc = 0L;
        this.gpr = new int[32];
        this.memory = new byte[memorySize];
    }

    public long getPC() {
        return pc;
    }

    public void setPC(long pc) {
        this.pc = pc;
    }

    public int getRegister(int index) {
        return gpr[index];
    }

    public void setRegister(int index, int value) {
        if (index != 0) {
            gpr[index] = value;
        }
    }

    public byte getByte(int address) {
        return memory[address];
    }

    public void setByte(int address, byte value) {
        memory[address] = value;
    }

    public void setByteArray(byte[] arr, int startIndex) {
        for(int i = 0; i < arr.length; i++){
            setByte(startIndex + i, arr[i]);
        }
    }

    public int getWord(int address) {
        return ((memory[address] & 0xFF) << 24) |
                ((memory[address + 1] & 0xFF) << 16) |
                ((memory[address + 2] & 0xFF) << 8) |
                (memory[address + 3] & 0xFF);
    }

    public void setWord(int address, int value) {
        memory[address] = (byte) ((value >>> 24) & 0xFF);
        memory[address + 1] = (byte) ((value >>> 16) & 0xFF);
        memory[address + 2] = (byte) ((value >>> 8) & 0xFF);
        memory[address + 3] = (byte) (value & 0xFF);
    }

    public byte[] getMemory(){
        return this.memory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Configuration{");
        sb.append("pc=").append(pc).append(", gpr={" + "\n");

        for (int i = 0; i < gpr.length; i++) {
            sb.append("gpr ").append(i).append(" - ").append(gpr[i]);
            sb.append("\n");
        }

        sb.append("}}");
        return sb.toString();
    }
}
