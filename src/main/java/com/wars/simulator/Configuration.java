package com.wars.simulator;


import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private final int[] gpr;
    private final Map<Integer, Byte> memory;
    private long pc;

    private boolean isRunning;

    public Configuration() {
        this.pc = 0L;
        this.gpr = new int[32];
        this.memory = new HashMap<>();
        this.isRunning = true;
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
        return memory.getOrDefault(address, (byte) 0);
    }

    public void setByte(int address, byte value) {
        memory.put(address, value);
    }

    public void setByteArray(byte[] arr, int startIndex) {
        for (int i = 0; i < arr.length; i++) {
            setByte(startIndex + i, arr[i]);
        }
    }

    public int getWord(int address) {
        return ((getByte(address) & 0xFF) << 24) |
                ((getByte(address + 1) & 0xFF) << 16) |
                ((getByte(address + 2) & 0xFF) << 8) |
                (getByte(address + 3) & 0xFF);
    }

    public void setWord(int address, int value) {
        memory.put(address, (byte) ((value >>> 24) & 0xFF));
        memory.put(address + 1, (byte) ((value >>> 16) & 0xFF));
        memory.put(address + 2, (byte) ((value >>> 8) & 0xFF));
        memory.put(address + 3, (byte) (value & 0xFF));
    }

    public boolean hasWordAt(int address) {
        return memory.containsKey(address);
    }

    public void halt() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
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
