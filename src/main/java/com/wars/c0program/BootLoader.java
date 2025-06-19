package com.wars.c0program;

import com.wars.util.CodeTranslation;
import com.wars.util.Initialize;
import com.wars.compiler.util.Context;

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
    public String byteCode;
    

    public BootLoader(int K, int a, int b,  int HDBASE, int SBASE, int SMAX, int HBASE, int HMAX, int KernelStart, String code) {
        this.K = K;
        this.a = a;
        this.b = b;
        this.HDBASE = HDBASE;

        this.SBASE = SBASE;
        this.SMAX = SMAX;
        this.HBASE = HBASE;
        this.HMAX = HMAX;

        this.KernelStart = KernelStart;

        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
    }

    public BootLoader(String code){
        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
    }

    public static BootLoader generateBootLoader(){
        String code = generateBootLoader(Initialize.K, Initialize.ROMSTART, Initialize.ROMEND, Initialize.HDBASE);
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

        int size = CodeGenerator.getInstance().totalProgramRealSize(true) - Context.removedStatementsForBootLoader;
        int beforeJumpToKernelSize = 4 * (Context.bootLoaderInit + size);
        int relativeA = this.KernelStart - beforeJumpToKernelSize;
        int beforeJumptToGamma = beforeJumpToKernelSize + 4 * 2;
        int gamainit = this.KernelEnd - beforeJumptToGamma + 4 * Context.gammaAddress;
        StringBuilder sb = new StringBuilder();

        sb.append("macro: ssave(1)" + "\n");
        sb.append("movs2g 2 1" + "\n");
        sb.append("andi 1 1 1" + "\n");
        sb.append("blez 1 " + (size + 2) + "\n");

        sb.append("\n");
        sb.append("_bootloader:\n");
        sb.append(initial.toString());
        sb.append("j " + relativeA + "\n");
        sb.append("\n");

        sb.append("_continue:" + "\n");
        sb.append("macro: srestore(1)" + "\n");
        sb.append("j " + gamainit + "\n");

        

        return sb.toString();
    }
    
    public static String bootLoaderMain(int a, int b, int K){
        StringBuilder sb = new StringBuilder();
        sb.append("int main(){" + "\n");
        sb.append("\n");

        sb.append("\t" + "uint SPX;" + "\n");
        sb.append("\t" + "uint PPX;" + "\n");
        sb.append("\t" + "int L;" + "\n");
        sb.append("\n");

        sb.append("\t" + "SPX = 0u;" + "\n");
        sb.append("\t" + "PPX = " + a + "u;" + "\n");
        sb.append("\t" + "L = " + (b - a + 4) / (4 * K) + ";" + "\n");
        sb.append("\n");

        sb.append("\t" + "while L>0 {" + "\n");
        sb.append("\t\t" + "readdisk(PPX, SPX);" + "\n");
        sb.append("\t\t" + "SPX = SPX + 1u;" + "\n");
        sb.append("\t\t" + "PPX = PPX + 1u;" + "\n");
        sb.append("\t\t" + "L = L-1" + "\n");
        sb.append("\t};\n");

        sb.append("\t" + "return 1" + " // returning from function and translation of the return statement is removed through Assembler translation \n");
        sb.append("}");

        return sb.toString();
    }

    public static String generateBootLoader(int K, int a, int b, int HDBASE){
        StringBuilder sb = new StringBuilder();
        sb.append(Disk.readms());
        sb.append("\n");
        sb.append(Disk.writems());
        sb.append("\n");
        sb.append(Disk.copyms());
        sb.append("\n");
        sb.append(Disk.readdisk(HDBASE, K));
        sb.append("\n");
        sb.append(bootLoaderMain(a, b, K));
        sb.append("~");
        sb.append("\n");

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

    public String getByteCode(){
        return this.byteCode;
    }
}
