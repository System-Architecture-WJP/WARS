package com.wars.engine.util;

public class Initialize {

    public static final int K = (1 << 10);
    public static final int p = 2;
    public static final int PTLE = 2;
    public static final int PTASIZE = p * PTLE;
    public static final int nup = 2;

    public static final int ROMSTART = 0;
    public static final int ROMEND = 4 * K;

    public static final int a = 12 * K;
    public static final int b = 20 * K;
    public static final int SBASE = 24 * K;
    public static final int SMAX = 32 * K;
    public static final int HBASE = 36 * K;
    public static final int HMAX = 40 * K; 
    public static final int UPBASE = 44 * K; 
    public static final int HDBASE = 56 * K;
    public static final int SMSIZE = (1 << 28);
    public static final int SMUSERPAGE = (1 << 20);
    public static final int SMBASE = (SMSIZE - p * SMUSERPAGE);
    
    
}
