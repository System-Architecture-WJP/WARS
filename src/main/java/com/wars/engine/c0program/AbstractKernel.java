package com.wars.engine.c0program;

import com.wars.engine.util.Initialize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.naming.Context;
import com.wars.compiler.codegen.CodeGenerator;

public class AbstractKernel extends C0Program {

    public static String AbstractKernelFileName = "src/main/resources/c0 programs/AbstractKernel.c0";
    public static String KernelMips = "src/main/resources/mips programs/KernelMips";
    public int K = Initialize.K;
    public int p = Initialize.p;
    public int PTLE = Initialize.PTLE;
    public int PTASIZE = Initialize.PTASIZE;
    public int nup = Initialize.nup;
    public int SBASE = Initialize.SBASE;
    public int SMAX = Initialize.SMAX;
    public int HBASE = Initialize.HBASE;
    public int HMAX = Initialize.HMAX;
    public int HDBASE = Initialize.HDBASE;
    public int UPBASE = Initialize.UPBASE;
    public int SMSIZE = Initialize.SMSIZE;
    public int SMUSERPAGE = Initialize.SMUSERPAGE;
    public int SMBASE = Initialize.SMBASE;
    public int a = Initialize.a;
    public int b = Initialize.b;
    public String code;
    public String mipsCode;
    public int[] byteCode;


    public AbstractKernel(String code) {
        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
        writeCodeToFile(this.mipsCode, KernelMips);
    }

    public static AbstractKernel generateAbstractKernel() {
        String code = "";
        Path path = Path.of(AbstractKernelFileName);
        try {
            code = Files.readString(path);
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }

        return new AbstractKernel(code);
    }

    public int findGammaAddress(){
        String[] instructions = this.mipsCode.split("\n");
        int gammaAddress = 0;
        for (String instruction : instructions){
            if (instruction.equals("macro: save-user")) {
                Initialize.gammaAddress = gammaAddress;
                return gammaAddress;
            }
            gammaAddress += CodeGenerator.getInstance().instructionRealSize(instruction);
            
        }

        return 0; 
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
