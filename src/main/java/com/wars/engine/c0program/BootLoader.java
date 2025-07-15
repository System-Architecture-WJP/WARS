package com.wars.engine.c0program;

import com.wars.engine.util.CodeTranslation;
import com.wars.engine.util.Initialize;
import com.wars.compiler.util.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static String BootLoaderFileName = "src/main/resources/c0 programs/FetchKernel.c0";
    public static String ROM = "src/main/resources/mips programs/ROM";

    public BootLoader(String code){
        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
        writeCodeToFile(this.mipsCode, ROM);
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

        String mipsInstructions = CodeTranslation.C0Translation(adjustedCode);
        List<String> instructions = new ArrayList<>(Arrays.asList(mipsInstructions.split("\n")));
        int beforeJumpToGamma = 0;
        boolean main = false;
        int index = 0;

        for(int i = 0; i < instructions.size(); i++){
            String instruction = instructions.get(i);
            if (main && instructions.get(i + 2).isEmpty()){
                index = i;
                break; 
            }
            if (instruction.startsWith("_main:")){
                main = true;
            }
            
            beforeJumpToGamma += CodeGenerator.getInstance().instructionRealSize(instruction);
        }

        // remove return translation
        instructions.remove(index); 
        instructions.remove(index);

        int kernelFetchingSize = beforeJumpToGamma; // without instruction - jump to kernel 

        for (int i = index; i < instructions.size(); i++) {
            kernelFetchingSize += CodeGenerator.getInstance().instructionRealSize(instructions.get(i));
        }

        // Initialize gammaAddress
        AbstractKernel ab = AbstractKernel.generateAbstractKernel();
        ab.findGammaAddress();
        System.out.println();
        
        int beforeJumpToKernelPC = 4 * Initialize.bootLoaderInit + 4 * beforeJumpToGamma;
        int offsetA = this.KernelStart - beforeJumpToKernelPC;
        
        instructions.add(index, "j " + offsetA);
        instructions.add(0, "macro: ssave(1)");
        instructions.add(1, "movs2g 2 1");
        instructions.add(2, "andi 1 1 1");
        instructions.add(3, "blez 1 " + (kernelFetchingSize + 2)); // additional instruction for jump to kernel;

        instructions.add(4, "\n_bootloader:");
        instructions.add("\n_continue:");
        instructions.add("macro: srestore(1)");

        int beforeJumpToGammaPC = 4 * Initialize.bootLoaderInit + 4 * kernelFetchingSize + 8; // for srestore, and previous jump
        int offsetGamma = this.KernelStart + 4 * Initialize.gammaAddress - 4 * beforeJumpToGammaPC;

        instructions.add("j " + offsetGamma); 

        String mipsCode = String.join("\n", instructions);
        return mipsCode;
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
