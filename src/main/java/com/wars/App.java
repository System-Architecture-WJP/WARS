package com.wars;

import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import com.wars.assembler.Assembler;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        Assembler asm = new Assembler(System.in, System.out);
        asm.assembleToBinaryString();
    }
}
