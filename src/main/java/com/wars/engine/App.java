package com.wars.engine;

import com.wars.engine.assembler.Assembler;
import com.wars.engine.simulator.Configuration;
import com.wars.engine.simulator.Simulator;
import com.wars.engine.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
         InputStream in = new FileInputStream("src/main/resources/mips programs/condition");
//        InputStream in = System.in;
        PrintStream out = System.out;

        Assembler asm = new Assembler(in, out, 0);

        int[] instructions = asm.toIntCodeArray();
        Configuration res = Simulator.simulate(instructions);
        Log.info(res.toString());
    }
}
