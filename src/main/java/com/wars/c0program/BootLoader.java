package com.wars.c0program;

import com.wars.util.CodeTranslation;
import com.wars.util.Initialize;
import com.wars.compiler.util.Context;

import java.util.List;

import com.wars.compiler.codegen.CodeGenerator;

public class BootLoader extends C0Program {

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
    public String byteCode;
    

    public BootLoader(int K, int p, int PTLE, int PTASIZE, int nup, int SBASE, int SMAX, int HBASE, int HMAX, int HDBASE, int UPBASE, int SMSIZE, int SMUSERPAGE, int SMBASE, int a, int b, String code) {
        this.K = K;
        this.p = p;
        this.PTLE = PTLE;
        this.PTASIZE = PTASIZE;
        this.nup = nup;
        this.SBASE = SBASE;
        this.SMAX = SMAX;
        this.HBASE = HBASE;
        this.HMAX = HMAX;
        this.HDBASE = HDBASE;
        this.UPBASE = UPBASE;
        this.SMSIZE = SMSIZE;
        this.SMUSERPAGE = SMUSERPAGE;
        this.SMBASE = SMBASE;
        this.a = a;
        this.b = b;

        this.code = code;
        this.mipsCode = mipsCode(code);
        this.byteCode = byteCode();
    }

    public BootLoader(){
        String code = generateBootLoader();
        this.code = code;
        this.mipsCode = mipsCode(code);
        this.byteCode = byteCode();
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
        int relativeA = this.a - beforeJumpToKernelSize;
        int beforeJumptToGamma = beforeJumpToKernelSize + 4 * 2;
        int gamainit = this.a - beforeJumptToGamma + 4 * Context.gammaAddress;
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
    
    public String bootLoaderMain(){
        StringBuilder sb = new StringBuilder();
        sb.append("int main(){" + "\n");
        sb.append("\n");

        sb.append("\t" + "uint SPX;" + "\n");
        sb.append("\t" + "uint PPX;" + "\n");
        sb.append("\t" + "int L;" + "\n");
        sb.append("\n");

        sb.append("\t" + "SPX = 0u;" + "\n");
        sb.append("\t" + "PPX = " + this.a + "u;" + "\n");
        sb.append("\t" + "L = " + (this.b - this.a + 4) / (4 * this.K) + ";" + "\n");
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

    public String generateBootLoader(){
        StringBuilder sb = new StringBuilder();
        sb.append(Disk.readms());
        sb.append("\n");
        sb.append(Disk.writems());
        sb.append("\n");
        sb.append(Disk.copyms());
        sb.append("\n");
        sb.append(Disk.readdisk(this.HDBASE, this.K));
        sb.append("\n");
        sb.append(bootLoaderMain());
        sb.append("~");
        sb.append("\n");

        return sb.toString();
    }
}
