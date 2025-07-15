package com.wars.engine.simulator;


import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private static final int PAGE_SHIFT = 12; // 2^12 = 4096 bytes per page
    private static final int PAGE_SIZE = 1 << PAGE_SHIFT;
    private static final int PAGE_MASK = PAGE_SIZE - 1;
    private final int[] gpr;
    private final Map<Integer, byte[]> memoryPaged;
    private long pc;
    private boolean isRunning;

    public Configuration() {
        this.pc = 0L;
        this.gpr = new int[32];
        this.memoryPaged = new HashMap<>();
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
        int pageNumber = address >>> PAGE_SHIFT;
        int offset = address & PAGE_MASK;

        if (!memoryPaged.containsKey(pageNumber)) {
            return 0;
        }

        byte[] page = memoryPaged.get(pageNumber);
        return page[offset];
    }

    public void setByte(int address, byte value) {
        int pageNumber = address >>> PAGE_SHIFT;
        int offset = address & PAGE_MASK;

        byte[] page = getPage(pageNumber);
        page[offset] = value;
    }

    private byte[] getPage(int pageNumber) {
        return memoryPaged.computeIfAbsent(pageNumber, k -> new byte[PAGE_SIZE]);
    }

    public void setWordArray(int[] arr, int startIndex) {
        for (int i = 0; i < arr.length; i++) {
            setWord(startIndex + i * 4, arr[i]);
        }
    }

    public int getWord(int address) {
        return ((getByte(address) & 0xFF) << 24) |
                ((getByte(address + 1) & 0xFF) << 16) |
                ((getByte(address + 2) & 0xFF) << 8) |
                (getByte(address + 3) & 0xFF);
    }

    public void setWord(int address, int value) {
        setByte(address, (byte) ((value >>> 24) & 0xFF));
        setByte(address + 1, (byte) ((value >>> 16) & 0xFF));
        setByte(address + 2, (byte) ((value >>> 8) & 0xFF));
        setByte(address + 3, (byte) (value & 0xFF));
    }

    public boolean hasWordAt(int address) {
        int pageNumber = address >>> PAGE_SHIFT;
        return memoryPaged.containsKey(pageNumber);
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
        sb.append("\n \t pc = ").append(pc).append(",\n").append("\t gpr = {" + "\n");

        for (int i = 0; i < gpr.length; i++) {
            sb.append("\t \t gpr ").append(i).append(" - ").append(gpr[i]);
            sb.append("\n");
        }

        sb.append("}}");
        return sb.toString();
    }

}
