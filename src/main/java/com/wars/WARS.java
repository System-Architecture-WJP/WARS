package com.wars;

import com.wars.assembler.Assembler;
import com.wars.simulator.Configuration;
import com.wars.simulator.Simulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

public class WARS {
    public static void main(String[] args) throws FileNotFoundException {
        InputStream in = new FileInputStream("src/main/resources/mips programs/condition");
//        InputStream in = System.in;
        PrintStream out = System.out;

        Assembler asm = new Assembler(in, out, 0);
        asm.assembleToBinaryString();

        int[] instructions = asm.getInstructionIntBytecode();
        Configuration res = Simulator.simulate(instructions);
        System.out.println(res);
    }
}
