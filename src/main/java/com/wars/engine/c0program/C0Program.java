package com.wars.engine.c0program;

import com.wars.compiler.util.Context;
import com.wars.engine.util.CodeTranslation;
import com.wars.engine.util.Log;

import java.util.Arrays;
import java.io.FileWriter.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class C0Program {

    public int K = (1 << 10);
    public int a = 0;
    public int b = 4 * K;

    public int SBASE = 5 * K;
    public int SMAX = 9 * K;
    public int HBASE = 10 * K;
    public int HMAX = 14 * K;

    public String code;
    public String mipsCode;
    public int[] byteCode;

    public C0Program(int a, int b, int SBASE, int SMAX, int HBASE, int HMAX, String code) {
        this.a = a;
        this.b = b;
        this.SBASE = SBASE;
        this.SMAX = SMAX;
        this.HBASE = HBASE;
        this.HMAX = HMAX;
        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
    }

    public C0Program(String code) {
        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
    }

    public C0Program(int SBASE, int SMAX, int HBASE, int HMAX, String code) {
        this.SBASE = SBASE;
        this.SMAX = SMAX;
        this.HBASE = HBASE;
        this.HMAX = HMAX;

        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);

    }

    public C0Program() {
    }

    public static String toC0Grammar(String code) {
        StringBuilder adjustedCode = new StringBuilder();
        boolean comment = false;
        boolean extraSpace = false;
        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == ' ' && i < code.length() - 2 && code.charAt(i + 1) == '/' && code.charAt(i + 2) == '/') {
                comment = true;
            }
            if (i < code.length() - 1 && code.charAt(i) == '/' && code.charAt(i + 1) == '/') {
                comment = true;
            }
            if (code.charAt(i) == '\n') {
                comment = false;
            }

            extraSpace = code.charAt(i) == ' ' && (extraSpace || i == 0 || code.charAt(i - 1) == '\n');
            if (!comment && code.charAt(i) != '\n' && code.charAt(i) != '\t' && !extraSpace && code.charAt(i) != '\r') {
                adjustedCode.append(code.charAt(i));
            }
        }
        return adjustedCode.toString();
    }

    public static String asm(String s) {
        return "asm( " + s + " )";
    }

    public String mipsCode(String code) {
        String adjustedCode = toC0Grammar(code);
        Context.SBASE = this.SBASE;
        Context.HBASE = this.HBASE;
        Context.SMAX = this.SMAX;
        Context.HMAX = this.HMAX;
        return CodeTranslation.C0Translation(adjustedCode);
    }

    public int[] byteCode(String mipsCode) {
        return CodeTranslation.MIPSTranslation(mipsCode);
    }

    public static void writeCodeToFile(String code, String fileName) {
        try {
            Files.writeString(Paths.get(fileName), code);
        } catch (IOException e) {
            Log.error("Error writing file: " + e.getMessage());
        }
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
