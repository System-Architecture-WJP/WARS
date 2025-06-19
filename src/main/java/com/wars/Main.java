package com.wars;
import com.wars.c0program.*;
import com.wars.util.CodeTranslation;
import com.wars.compiler.util.Context;
import com.wars.instruction.InstructionRegistry;
import com.wars.assembler.Assembler;
import com.wars.util.Initialize;
import com.wars.simulator.Simulator;
import com.wars.simulator.Configuration;
import com.wars.instruction.Initializer;

import static com.wars.compiler.util.Context.bootLoaderInit;


import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        Context.DEBUG = false;

        BootLoader bt = BootLoader.generateBootLoader();
        AbstractKernel ab = AbstractKernel.generateAbstractKernel();
        

        int a = 2;
        int b = 10;
        String code = "int a; int b; void swap(){gpr(1) = a; gpr(2) = b {1}; b = gpr(1) {2}; a = gpr(2)}; int main(){a = " + a + "; b = " + b + "; swap(); return 1}~";
        C0Program pr = new C0Program(code);
        // System.out.println(pr);
        Configuration config = new Configuration(pr.HMAX);
        byte [] byteCode = CodeTranslation.MIPSTranslationByteArray(pr.mipsCode);
        config.setByteArray(byteCode, 0);
        Simulator.simulate(config);
        System.out.println("Syscall signal - " + config.getRegister(1));
        System.out.println("a: " + config.getWord(pr.SBASE));
        System.out.println("a: " + config.getWord(pr.SBASE + 4));       
        
        
    }   
}
