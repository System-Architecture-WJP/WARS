package com.wars.engine.c0program;

import com.wars.engine.util.CodeTranslation;
import com.wars.engine.util.Initialize;
import com.wars.compiler.util.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.wars.compiler.codegen.CodeGenerator;

public class BootLoader extends C0Program {

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

    public static String BootLoaderFileName = "src/main/resources/c0 programs/BootLoader.txt";


    public BootLoader(String code){
        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
    }

    public static BootLoader generateBootLoader(){
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
    public String mipsCode(String code){

        String adjustedCode = toC0Grammar(code);
        Context.SBASE = this.SBASE;
        Context.HBASE = this.HBASE;
        Context.SMAX = this.SMAX;
        Context.HMAX = this.HMAX;


        List<String> instructions = CodeTranslation.C0TranslationList(adjustedCode, true);

        StringBuilder initial = new StringBuilder();
        for(int i = 0; i < instructions.size() - 2; i++){
            initial.append(instructions.get(i));
        }

        // Initialize gammaAddress
        AbstractKernel.generateAbstractKernel();
        
        int kernelFetchingSize = CodeGenerator.getInstance().totalProgramRealSize(true) - Context.removedStatementsForBootLoader;
        int beforeJumpToKernelSize = 4 * (Context.bootLoaderInit + kernelFetchingSize);
        int relativeA = this.KernelStart - beforeJumpToKernelSize;
        int beforeJumpToGamma = beforeJumpToKernelSize + 4 * 2;
        int gamma = 4 * Context.gammaAddress - beforeJumpToGamma;
        StringBuilder sb = new StringBuilder();

        sb.append("macro: ssave(1)" + "\n");
        sb.append("movs2g 2 1" + "\n");
        sb.append("andi 1 1 1" + "\n");
        sb.append("blez 1 " + (kernelFetchingSize + 2) + "\n");

        sb.append("\n");
        sb.append("_bootloader:\n");
        sb.append(initial.toString());
        sb.append("j " + relativeA + "\n");
        sb.append("\n");

        sb.append("_continue:" + "\n");
        sb.append("macro: srestore(1)" + "\n");
        sb.append("j " + gamma + "\n");



        return sb.toString();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("------------C0 Program------------\n");
        sb.append(this.code + "\n");
        sb.append("------------MIPS Code------------\n");
        sb.append(this.mipsCode + "\n");
        sb.append("------------Byte Code------------\n");
        sb.append(this.byteCode + "\n");

        return sb.toString();
    }

    public String getMipsCode(){
        return this.mipsCode;
    }

    public String getCode(){
        return this.code;
    }

    public int[] getByteCode(){
        return this.byteCode;
    }
}
