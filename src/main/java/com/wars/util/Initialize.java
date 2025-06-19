package com.wars.util;

public class Initialize {

    public static final int K = (1 << 10);
    public static final int p = 2;
    public static final int PTLE = 2;
    public static final int PTASIZE = p * PTLE;
    public static final int nup = 2;
    public static final int SBASE = 10 * K;
    public static final int SMAX = 14 * K;
    public static final int HBASE = 15 * K;
    public static final int HMAX = 19 * K;
    public static final int UPBASE = 20 * K;
    public static final int HDBASE = UPBASE + 4 * K * nup + K;
    public static final int SMSIZE = (1 << 28);
    public static final int SMUSERPAGE = (1 << 20);
    public static final int SMBASE = (SMSIZE - p * SMUSERPAGE);
    public static final int a = 9 * K;
    public static final int b = a + 4 * PTLE * K;
    public static final int ROMSTART = 0;
    public static final int ROMEND = 4 * K;
    
}
