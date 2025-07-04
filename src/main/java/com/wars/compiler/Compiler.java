package com.wars.compiler;

import com.wars.engine.c0program.AbstractKernel;
import com.wars.engine.c0program.BootLoader;
import com.wars.engine.c0program.C0Program;
import com.wars.compiler.util.Context;
import com.wars.engine.simulator.Configuration;
import com.wars.engine.simulator.Simulator;
import com.wars.engine.util.CodeTranslation;

public class Compiler {
    public static void main(String[] args) {
        Context.DEBUG = true;

        BootLoader bt = BootLoader.generateBootLoader();
        AbstractKernel ab = AbstractKernel.generateAbstractKernel();

        int a = 2;
        int b = 10;
        String code = "int a; int b; void swap(int a){gpr(1) = a; gpr(2) = b {1}; b = gpr(1) {2}; a = gpr(2)}; int main(){bool c; c = a+2>=3; a = " + a + "; b = " + b + "; swap(2*3*2); return 1}~";
        C0Program pr = new C0Program(code);

        // System.out.println(pr);
        Configuration config = new Configuration();
        byte[] byteCode = CodeTranslation.MIPSTranslationByteArray(pr.mipsCode);
        config.setByteArray(byteCode, 0);
        Configuration res = Simulator.simulate(config);

        System.out.println("Syscall signal - " + config.getRegister(1));
        System.out.println("a: " + config.getWord(pr.SBASE));
        System.out.println("a: " + config.getWord(pr.SBASE + 4));
        System.out.println(res);
        System.out.println(bt.getCode());
    }
}
