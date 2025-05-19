package com.wars.macro;

import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import com.wars.operand.OperandParser;

import java.util.List;
import java.util.LinkedList;
import com.wars.exception.AssemblerException;

public class Macro {

    public static List<Instruction> evaluate(String line){

        String[] split = line.split("[\\s(),]+");
        
        if (split.length < 2) {
            throw new AssemblerException("Invalid macro definition: " + line);
        }
        
        String macroType = split[1]; 

        if (macroType.equals("gpr")){

            int reg = OperandParser.parseUnsigned(split[2], 5);
            String s = split[4];
            if (s.equals("enc")){ 
            
                int val = OperandParser.parseSigned(split[5], 5);

                return enc(reg, val);
            }

            return store(reg, s);


            
        }

        else if (macroType.equals("divt")){
            
            if (split.length != 5){
                throw new AssemblerException("Invalid macro definition: " + line); 
            }
            
            int k = OperandParser.parseUnsigned(split[2], 5);
            int i = OperandParser.parseUnsigned(split[3], 5);
            int j = OperandParser.parseUnsigned(split[4], 5);

            return divt(k, i, j);
        }
        else if (macroType.equals("divu")){

            if (split.length != 5){
                throw new AssemblerException("Invalid macro definition: " + line); 
            }

            int k = OperandParser.parseUnsigned(split[2], 5);
            int i = OperandParser.parseUnsigned(split[3], 5);
            int j = OperandParser.parseUnsigned(split[4], 5);

            
            return divu(k, i, j); 
        }
        else if (macroType.equals("mul")){

            if (split.length != 5) {
                throw new AssemblerException("Invalid macro definition: " + line);
            }

            int k = OperandParser.parseUnsigned(split[2], 5);
            int i = OperandParser.parseUnsigned(split[3], 5);
            int j = OperandParser.parseUnsigned(split[4], 5);

            return mul(k, i, j);
         
        }
        else if (macroType.equals("zero")){
            
            if (split.length != 4) {
                throw new AssemblerException("Invalid macro definition: " + line);
            }

            int i = OperandParser.parseUnsigned(split[2], 5);
            int j = OperandParser.parseUnsigned(split[3], 5); 

            return zero(i, j);
        
        }
        else {
            throw new AssemblerException("Invalid macro type: " + macroType);
        }

    }

    private static List<Instruction> enc(int reg, int val){
        
        return store(reg, val);
    }

    private static List<Instruction> zero(int i, int j){
        List<Instruction> instructions = new LinkedList<>();
        
        instructions.add(InstructionRegistry.create("sw",   new int[]{0, i, 0}));
        instructions.add(InstructionRegistry.create("addi", new int[]{i, i, 4}));
        instructions.add(InstructionRegistry.create("addi", new int[]{j, j, -1}));
        instructions.add(InstructionRegistry.create("bne",  new int[]{0, j, -3}));
        
        return instructions;
    }

    private static List<Instruction> mul(int k, int i, int j){

        List<Instruction> instructions = new LinkedList<>();

        instructions.add(InstructionRegistry.create("addi", new int[]{24, 0, 1}));
        instructions.add(InstructionRegistry.create("addi", new int[]{26, i, 0}));
        instructions.add(InstructionRegistry.create("addi", new int[]{27, 0, 0}));
        instructions.add(InstructionRegistry.create("and",  new int[]{25, j, 24}));
        instructions.add(InstructionRegistry.create("beq",  new int[]{25, 0, 2}));
        instructions.add(InstructionRegistry.create("add",  new int[]{27, 27, 26}));
        instructions.add(InstructionRegistry.create("add",  new int[]{24, 24, 24}));
        instructions.add(InstructionRegistry.create("add",  new int[]{26, 26, 26}));
        instructions.add(InstructionRegistry.create("bne",  new int[]{24, 0, -5}));
        instructions.add(InstructionRegistry.create("addi", new int[]{k, 27, 0}));

        return instructions;
        
    }
    

    private static List<Instruction> divu(int k, int i, int j){

        List<Instruction> instructions = new LinkedList<>();

        instructions.add(InstructionRegistry.create("add",  new int[]{k, 0, 0}));
        instructions.add(InstructionRegistry.create("sltu", new int[]{23, i, j}));
        instructions.add(InstructionRegistry.create("bgtz", new int[]{23, 29}));
        instructions.add(InstructionRegistry.create("add",  new int[]{23, i, 0}));
        instructions.add(InstructionRegistry.create("add",  new int[]{24, j, 0}));
        instructions.add(InstructionRegistry.create("addi", new int[]{25, 0, 1}));
        instructions.add(InstructionRegistry.create("add",  new int[]{26, 0, 0}));

        store(21, "1" + "0".repeat(31)).forEach(instructions::add);

        instructions.add(InstructionRegistry.create("and",  new int[]{22, 24, 21}));
        instructions.add(InstructionRegistry.create("bgtz", new int[]{22, 5}));
        instructions.add(InstructionRegistry.create("add",  new int[]{25, 25, 25}));
        instructions.add(InstructionRegistry.create("add",  new int[]{24, 24, 24}));
        instructions.add(InstructionRegistry.create("sltu", new int[]{27, 23, 24}));
        instructions.add(InstructionRegistry.create("blez", new int[]{27, -5}));

        instructions.add(InstructionRegistry.create("addi", new int[]{24, 24, -1}));
        instructions.add(InstructionRegistry.create("sltu", new int[]{22, 24, 23}));
        instructions.add(InstructionRegistry.create("addi", new int[]{24, 24, 1}));
        instructions.add(InstructionRegistry.create("blez", new int[]{22, 3}));
        instructions.add(InstructionRegistry.create("or",   new int[]{26, 26, 25}));
        instructions.add(InstructionRegistry.create("sub",  new int[]{23, 23, 24}));
        instructions.add(InstructionRegistry.create("sltu", new int[]{27, 23, 24}));
        instructions.add(InstructionRegistry.create("blez", new int[]{27, 9}));

        instructions.add(InstructionRegistry.create("srl",  new int[]{25, 25, 1}));
        instructions.add(InstructionRegistry.create("srl",  new int[]{24, 24, 1}));
        instructions.add(InstructionRegistry.create("sltu", new int[]{27, 23, 24}));
        instructions.add(InstructionRegistry.create("bgtz", new int[]{27, -3}));
        instructions.add(InstructionRegistry.create("or",   new int[]{26, 26, 25}));
        instructions.add(InstructionRegistry.create("sub",  new int[]{23, 23, 24}));
        instructions.add(InstructionRegistry.create("sltu", new int[]{27, 23, 24}));
        instructions.add(InstructionRegistry.create("blez", new int[]{27, -23}));
        instructions.add(InstructionRegistry.create("add",  new int[]{k, 26, 0}));
        
        return instructions;
        
    }

    private static List<Instruction> divt(int k, int i, int j){

        List<Instruction> instructions = new LinkedList<>(); 

        store(21, "1" + "0".repeat(31)).forEach(instructions::add);

        instructions.add(InstructionRegistry.create("bne",  new int[]{21, i, 5}));
        instructions.add(InstructionRegistry.create("nor",  new int[]{21, 0, 0}));
        instructions.add(InstructionRegistry.create("bne",  new int[]{21, j, 3}));
        instructions.add(InstructionRegistry.create("add",  new int[]{k, i, 0}));
        instructions.add(InstructionRegistry.create("beq",  new int[]{0, 0, 51}));
        
        signAndAbs(21, i, i).forEach(instructions::add);
        signAndAbs(22, j, j).forEach(instructions::add);
        
        instructions.add(InstructionRegistry.create("xor",  new int[]{20, 21, 22}));

        divu(k, i, j).forEach(instructions::add);

        instructions.add(InstructionRegistry.create("blez", new int[]{20, 4}));
        instructions.add(InstructionRegistry.create("nor", new int[]{22, 0, 0}));
        instructions.add(InstructionRegistry.create("xor", new int[]{k, k, 22}));
        instructions.add(InstructionRegistry.create("addi", new int[]{k, k, 1}));



        return instructions; 
    }

    private static List<Instruction> store(int reg, String s){
        
        if (s.length() > 32) {
            throw new AssemblerException("String must be 32 bits long");
        }

        if (s.length() < 32){
            StringBuilder sb = new StringBuilder(s);
            while (sb.length() < 32){
                sb.insert(0, '0');
            }
            s = sb.toString(); 
        }
        
        for (char c : s.toCharArray()){
            if (c != '1' && c != '0'){
                throw new AssemblerException("String must be binary " + s);
            }
        }

        List<Instruction> instructions = new LinkedList<>();

        instructions.add(InstructionRegistry.create("lui", new int[]{reg, Integer.parseInt(s.substring(0, 16), 2) - 32768 * (s.charAt(0) == '1' ? 2 : 0)}));
        instructions.add(InstructionRegistry.create("ori", new int[]{reg, reg, Integer.parseInt(s.substring(16, 32), 2) - 32768 * (s.charAt(16) == '1' ? 2 : 0)}));
        return instructions;
        
    }

    private static List<Instruction> store(int reg, int imm){

        List<Instruction> instructions = new LinkedList<>();
        
        instructions.add(InstructionRegistry.create("lui", new int[]{reg, imm >> 16}));
        instructions.add(InstructionRegistry.create("ori", new int[]{reg, reg, imm & 0xFFFF}));
        
        return instructions; 
    }

    private static List<Instruction> signAndAbs(int j, int k, int i){

        List<Instruction> instructions = new LinkedList<>();

        instructions.add(InstructionRegistry.create("slti", new int[]{j, i, 0}));
        instructions.add(InstructionRegistry.create("blez", new int[]{j, 5}));
        instructions.add(InstructionRegistry.create("nor",  new int[]{k, 0, 0}));
        instructions.add(InstructionRegistry.create("xor",  new int[]{k, k, i}));
        instructions.add(InstructionRegistry.create("addi", new int[]{k, k, 1}));
        instructions.add(InstructionRegistry.create("blez", new int[]{0, 2}));
        instructions.add(InstructionRegistry.create("addi", new int[]{k, i, 0}));

        return instructions;
    }
    


}
