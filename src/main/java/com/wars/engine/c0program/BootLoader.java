package com.wars.engine.c0program;

import com.wars.engine.util.Initialize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class BootLoader extends C0Program {

    public static String BootLoaderFileName = "src/main/resources/c0 programs/BootLoader.txt";
    public int K = Initialize.K;
    public int a = Initialize.ROMSTART;
    public int b = Initialize.ROMEND;
    public int HDBASE = Initialize.HDBASE;
    public int SBASE = Initialize.SBASE;
    public int SMAX = Initialize.SMAX;
    public int HBASE = Initialize.HBASE;
    public int HMAX = Initialize.HMAX;
    public int KernelStart = Initialize.a;
    public int KernelEnd = Initialize.b;
    public String code;
    public String mipsCode;
    public int[] byteCode;


    public BootLoader(String code) {
        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
    }

    public static BootLoader generateBootLoader() {
        String code = "";
        Path path = Path.of(BootLoaderFileName);
        try {
            code = Files.readString(path);
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
        return new BootLoader(code);
    }

    @Override
    public String toString() {
        return "------------C0 Program------------\n" +
                this.code + "\n" +
                "------------MIPS Code------------\n" +
                this.mipsCode + "\n" +
                "------------Byte Code------------\n" +
                Arrays.toString(this.byteCode) + "\n";
    }

    public String getMipsCode() {
        return this.mipsCode;
    }

    public String getCode() {
        return this.code;
    }

    public int[] getByteCode() {
        return this.byteCode;
    }
}
