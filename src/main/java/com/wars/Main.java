package com.wars;
import com.wars.c0program.*;
import com.wars.util.CodeTranslation;
import com.wars.compiler.util.Context;
import com.wars.assembler.Assembler;
import com.wars.c0program.AbstractKernel;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Context.DEBUG = false;
        String code = "int main(){asm( macro: divt(1, 1, 2) ); return 1}~";
        C0Program pr = new C0Program(code);
        // System.out.println(Arrays.asList(pr.mipsCode.split("\n")));
        // // Assembler as = new Assembler(Arrays.asList(pr.mipsCode.split("\n")), 0);
        // // List<String> out = as.assembleToBinaryStringList();
        // System.out.println(pr.mipsCode);
        // System.out.println(pr.byteCode);
        // System.out.println("Dasd");
        // byte[] arr = CodeTranslation.MIPSTranslationByteArray(pr.mipsCode);
        // int size = 0;
        // for (byte el : arr){
        //     size = (size + 1) % 4;
        //     System.out.print(String.format("%8s", Integer.toBinaryString(el & 0xFF))
        //          .replace(' ', '0'));
        //     if (size == 0){
        //         System.out.println();
        //     }
        // }

        AbstractKernel ab = new AbstractKernel();
        System.out.println(ab.code);
        System.out.println(ab.mipsCode);
        // System.out.println(ab.byteCode);
        
        // System.out.println(code);
    }   
}
