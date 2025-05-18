package com.wars.simulator;


public class Configuration {
    private int pc;
    private int[] gpr;
    private byte[] memory;

    public Configuration(int memorySize) {
        this.pc = 0;
        this.gpr = new int[32];
        this.memory = new byte[memorySize];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Configuration{");
        sb.append("pc=").append(pc).append(", gpr={");

        for (int i = 0; i < gpr.length; i++) {
            sb.append("gpr ").append(i).append(" - ").append(gpr[i]);
            sb.append("\n");
        }

        sb.append("}}");
        return sb.toString();
    }

    //TODO helper methods for manipulation to come
}
