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
            
                String val = split[5];
                String type = split[6];

                return enc(reg, val, type);
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
        else if (macroType.equals("store")){

            if (split.length != 4) {
                throw new AssemblerException("Invalid macro definition: " + line);
            }

            int reg = OperandParser.parseUnsigned(split[2], 5);
            String val = split[3];

            return storeInt(reg, val);

        }
        else if (macroType.equals("ssave")){

            if (split.length != 3) {
                throw new AssemblerException("Invalid macro definition: " + line);
            }

            int r = OperandParser.parseUnsigned(split[2], 5);

            return ssave(r, (1 << 10));
        }
        else if (macroType.equals("srestore")){

            if (split.length != 3) {
                throw new AssemblerException("Invalid macro definition: " + line);
            }
            
            int r = OperandParser.parseUnsigned(split[2], 5);

            return srestore(r, (1 << 10));
        }
        else if (macroType.equals("restore-user")){
            if (split.length != 2){
                throw new AssemblerException("Invalid macro definition: " + line);
            }
            
            return restoreUser((1 << 10));
        }
        else if (macroType.equals("save-user")){
            if (split.length != 2){
                throw new AssemblerException("Invalid macro definition: " + line);
            }
            return saveUser((1 << 10));
        }
        else {
            throw new AssemblerException("Invalid macro type: " + macroType);
        }

    }

    private static List<Instruction> enc(int reg, String val, String type){
        if (type.equals("int")){
            return storeInt(reg, val);
        }
        else if (type.equals("uint")){
            return storeUnsignedInt(reg, val);
        }
        else if (type.equals("bool")){
            return storeBool(reg, val);
        }
        else if (type.equals("char")){
            return storeChar(reg, val);
        }
        else {
            throw new AssemblerException("Invalid macro type: " + type); 
        }
        
    }

    private static List<Instruction> zero(int i, int j){
        List<Instruction> instructions = new LinkedList<>();
        
        instructions.add(InstructionRegistry.create("sw",   new int[]{0, i, 0}));
        instructions.add(InstructionRegistry.create("addi", new int[]{i, i, 4}));
        instructions.add(InstructionRegistry.create("addi", new int[]{j, j, -1}));
        instructions.add(InstructionRegistry.create("bne",  new int[]{0, j, -3})); // j instead i
        
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
        instructions.add(InstructionRegistry.create("bgtz", new int[]{23, 30})); // size increased, as number of instructions increases
        instructions.add(InstructionRegistry.create("add",  new int[]{23, i, 0}));
        instructions.add(InstructionRegistry.create("add",  new int[]{24, j, 0}));
        instructions.add(InstructionRegistry.create("addi", new int[]{25, 0, 1}));
        instructions.add(InstructionRegistry.create("add",  new int[]{26, 0, 0}));

        store(21, "1" + "0".repeat(31)).forEach(instructions::add);

        instructions.add(InstructionRegistry.create("and",  new int[]{22, 24, 21}));
        instructions.add(InstructionRegistry.create("bltz", new int[]{22, 5})); // gpr 22 stores, b[31]0^31, b[31] = 1 <-> gpr(22) < 0
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
        instructions.add(InstructionRegistry.create("sltu", new int[]{22, 23, j})); // 22 instead of 27, otherwise won't finish B = A
        instructions.add(InstructionRegistry.create("blez", new int[]{22, -23})); // size increased, as number of instructions increases
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
        instructions.add(InstructionRegistry.create("beq",  new int[]{0, 0, 52}));
        
        signAndAbs(21, 19, i).forEach(instructions::add); // avoid overwriting in i
        signAndAbs(22, 18, j).forEach(instructions::add); // avoid overwriting in j
        
        instructions.add(InstructionRegistry.create("xor",  new int[]{20, 21, 22}));

        divu(k, 19, 18).forEach(instructions::add);

        instructions.add(InstructionRegistry.create("blez", new int[]{20, 4}));
        instructions.add(InstructionRegistry.create("nor", new int[]{22, 0, 0}));
        instructions.add(InstructionRegistry.create("xor", new int[]{k, k, 22}));
        instructions.add(InstructionRegistry.create("addi", new int[]{k, k, 1}));



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

    private static List<Instruction> ssave(int r, int k){
        
        List<Instruction> instructions = new LinkedList<>(); 

        instructions.add(InstructionRegistry.create("sw", new int[]{r, 0, 4 * k + 8 + 4 * r}));

        return instructions; 
    }

    private static List<Instruction> srestore(int r, int k){

        List<Instruction> instructions = new LinkedList<>();

        if (r == 0){
            throw new AssemblerException("Cannot load into register 0"); 
        }

        instructions.add(InstructionRegistry.create("lw", new int[]{r, 0, 4 * k + 8 + 4 * r }));

        return instructions;
    }

    private static List<Instruction> restoreUser(int k){

        List<Instruction> instructions = new LinkedList<>();
        instructions.add(InstructionRegistry.create("sw", new int[]{1, 0, 4 * k + 4}));
        instructions.add(InstructionRegistry.create("lw", new int[]{1, 0, 4 * k}));
        
        for (int i = 28; i <= 30; i++){
            instructions.add(InstructionRegistry.create("sw", new int[]{i, 1, 4 * i}));
        }

        instructions.add(InstructionRegistry.create("lw", new int[]{1, 0, 4 * k + 4}));
        for(int i = 1; i <= 6; i++){
            if (i != 2 && i != 4){
                instructions.add(InstructionRegistry.create("lw", new int[]{2, 1, 4 * (32 + i)}));
                instructions.add(InstructionRegistry.create("movg2s", new int[]{i, 2}));
            }
        }

        for (int i = 31; i >= 1; i--){
            instructions.add(InstructionRegistry.create("lw", new int[]{i, 1, 4 * i}));
        }

        return instructions; 
    }

    private static List<Instruction> saveUser(int k){

        List<Instruction> instructions = new LinkedList<>(); 

        instructions.add(InstructionRegistry.create("lw", new int[]{1, 0, 4 * k + 4}));
        for(int i = 2; i <= 31; i++){
            instructions.add(InstructionRegistry.create("sw", new int[]{i, 1, 4 * i}));
        }

        instructions.add(InstructionRegistry.create("add", new int[]{2, 0, 1}));
        srestore(1, k).forEach(instructions::add);
        instructions.add(InstructionRegistry.create("sw", new int[]{1, 2, 4}));

        for (int i = 1; i <= 4; i++){
            instructions.add(InstructionRegistry.create("movs2g", new int[]{i, 1}));
            instructions.add(InstructionRegistry.create("sw", new int[]{1, 2, 4 * (32 + i)}));
        }

        instructions.add(InstructionRegistry.create("lw", new int[]{1, 0, 4 * k}));
        
        for (int i = 28; i <= 30; i++){
            instructions.add(InstructionRegistry.create("lw", new int[]{i, 1, 4 * i}));
        }

        return instructions; 
    }

    private static List<Instruction> storeInt(int reg, String val){
        int intVal = Integer.parseInt(val);
        
        List<Instruction> instructions = new LinkedList<>();

        instructions.add(InstructionRegistry.create("lui", new int[]{reg, intVal >> 16}));
        instructions.add(InstructionRegistry.create("ori", new int[]{reg, reg, intVal & 0xFFFF}));

        return instructions;
    }

    private static List<Instruction> storeUnsignedInt(int reg, String val){

        long longVal = Long.parseLong(val);
        if (longVal > (1L << 32) - 1L || longVal < 0){
            throw new AssemblerException("Unsigned Integer out of bounds: " + val);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append((longVal >> (31 - i)) & 1);
        }

        return store(reg, sb.toString());
    }

    private static List<Instruction> storeBool(int reg, String val){

        if (val.equals("true")){
            return store(reg, "1");
        }
        else if (val.equals("false")){
            return store(reg, "0");
        }
        else {
            throw new AssemblerException("Invalid boolean value: " + val);
        }

    }

    private static List<Instruction> storeChar(int reg, String val){
        if (val.length() != 1){
            throw new AssemblerException("Invalid char value: " + val);
        }
        List<Instruction> instructions = new LinkedList<>();
        instructions.add(InstructionRegistry.create("lui", new int[]{reg, (int) val.charAt(0) >> 16}));
        instructions.add(InstructionRegistry.create("ori", new int[]{reg, reg, (int) val.charAt(0) & 0xFFFF}));

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

        instructions.add(InstructionRegistry.create("lui", new int[]{reg, TwosComplement(s.substring(0, 16))}));
        instructions.add(InstructionRegistry.create("ori", new int[]{reg, reg, TwosComplement(s.substring(16, 32))}));
        return instructions;
        
    }

    private static int TwosComplement(String s){
        int val = Integer.parseInt(s, 2);
        if (s.charAt(0) == '1'){
            val -= 2 * (1 << s.length());
        }
        return val; 
    }


    


}
