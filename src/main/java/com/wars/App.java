package com.wars;

import com.wars.assembler.Assembler;

public class App {
    public static void main(String[] args) {
        Assembler asm = new Assembler(System.in, System.out, 0);
        asm.assembleToBinaryString();
    }
}
