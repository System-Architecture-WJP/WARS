package com.wars.util;

public class Initialize {

    public static final int K = (1 << 10);
    public static final int p = 2;
    public static final int PTLE = 2;
    public static final int PTASIZE = p * PTLE;
    public static final int nup = 2;
    public static final int SBASE = 0;
    public static final int SMAX = 0;
    public static final int HBASE = 0;
    public static final int HMAX = 0;
    public static final int HDBASE = (1 << 12);
    public static final int UPBASE = (1 << 14);
    public static final int SMSIZE = (1 << 28);
    public static final int SMUSERPAGE = (1 << 20);
    public static final int SMBASE = (SMSIZE - p * SMUSERPAGE);
    public static final int a = 8 * (1 << 10);
    public static final int b = a + PTLE * K;
    
}
