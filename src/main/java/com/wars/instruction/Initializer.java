package com.wars.instruction;

import com.wars.operand.OperandType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wars.operand.OperandType.*;

class Initializer {

    private static final Map<String, InstructionCreator> instructionCreatorMap = new HashMap<>();
    private static final Map<String, List<OperandType>> operandTypesMap = new HashMap<>();
    private static final Map<Integer, String> opcodeMap = new HashMap<>();

    static {
        // Registering I-Type instructions
        lw();
        sw();
        addi();
        addiu();
        slti();
        sltiu();
        andi();
        ori();
        xori();
        lui();
        bltz();
        bgez();
        beq();
        bne();
        blez();
        bgtz();

        // Registering R-Type instructions
        srl();
        add();
        addu();
        sub();
        subu();
        and();
        or();
        xor();
        nor();
        slt();
        sltu();
        jr();
        jalr();
        sysc();
        eret();
        movg2s();
        movs2g();

        // Registering J-Type instructions
        j();
        jal();
    }

    static Map<String, InstructionCreator> initializeCreatorMap() {
        return instructionCreatorMap;
    }

    static Map<String, List<OperandType>> initializeOperandTypesMap() {
        return operandTypesMap;
    }

    static Map<Integer, String> initializeOpcodeMap() {
        return opcodeMap;
    }

    private static void register(String mnemonic, int opcode, List<OperandType> operandTypes, InstructionCreator creator) {
        instructionCreatorMap.put(mnemonic, creator);
        operandTypesMap.put(mnemonic, operandTypes);
        opcodeMap.put(opcode, mnemonic);
    }

    private static void lw() {
        // lw rt rs imm
        int opcode = 0b100011;
        register("lw", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void sw() {
        // sw rt rs imm
        int opcode = 0b101011;
        register("sw", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void addi() {
        // addi rt rs imm
        int opcode = 0b001000;
        register("addi", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void addiu() {
        // addiu rt rs imm
        int opcode = 0b001001;
        register("addiu", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void slti() {
        // slti rt rs imm
        int opcode = 0b001010;
        register("slti", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void sltiu() {
        // sltiu rt rs imm
        int opcode = 0b001011;
        register("sltiu", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void andi() {
        // andi rt rs imm
        int opcode = 0b001100;
        register("andi", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void ori() {
        // ori rt rs imm
        int opcode = 0b001101;
        register("ori", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void xori() {
        // xori rt rs imm
        int opcode = 0b001110;
        register("xori", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]));
    }

    private static void lui() {
        // lui rt imm
        int opcode = 0b001111;
        register("lui", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, 0b000000, operands[0], operands[1]));
    }

    private static void bltz() {
        // bltz rs imm
        int opcode = 0b000001;
        register("bltz", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], 0b00000, operands[1]));
    }

    private static void bgez() {
        // bgez rs imm
        int opcode = 0b000001;
        register("bgez", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], 0b00001, operands[1]));
    }

    private static void beq() {
        // beq rs rt imm
        int opcode = 0b000000;
        register("beq", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], operands[1], operands[2]));
    }

    private static void bne() {
        // bne rs rt imm
        int opcode = 0b000101;
        register("bne", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], operands[1], operands[2]));
    }

    private static void blez() {
        // blez rs imm
        int opcode = 0b000110;
        register("blez", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], 0b00000, operands[1]));
    }

    private static void bgtz() {
        // bgtz rs imm
        int opcode = 0b000111;
        register("bgtz", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], 0b00000, operands[1]));
    }

    private static void srl() {
        // srl rd rt sa
        int opcode = 0b000000;
        int fun = 0b000010;
        register("srl", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, 0b00000, operands[1], operands[0], operands[2], fun));
    }

    private static void add() {
        // add rd rs rt
        int opcode = 0b000000;
        register("add", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b100000));
    }

    private static void addu() {
        // addu rd rs rt
        int opcode = 0b000000;
        register("addu", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b100001));
    }

    private static void sub() {
        // sub rd rs rt
        int opcode = 0b000000;
        register("sub", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b100010));
    }

    private static void subu() {
        // subu rd rs rt
        int opcode = 0b000000;
        register("subu", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b100011));
    }

    private static void and() {
        // and rd rs rt
        int opcode = 0b000000;
        register("and", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b100100));
    }

    private static void or() {
        // or rd rs rt
        int opcode = 0b000000;
        register("or", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b100101));
    }

    private static void xor() {
        // xor rd rs rt
        int opcode = 0b000000;
        register("xor", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b100110));
    }

    private static void nor() {
        // nor rd rs rt
        int opcode = 0b000000;
        register("nor", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b100111));
    }

    private static void slt() {
        // slt rd rs rt
        int opcode = 0b000000;
        register("slt", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b101010));
    }

    private static void sltu() {
        // sltu rd rs rt
        int opcode = 0b000000;
        register("sltu", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, 0b101011));
    }

    private static void jr() {
        // jr rs
        int opcode = 0b000000;
        register("jr", opcode, List.of(REG5),
                operands -> new RTypeInstruction(opcode, operands[0], 0b00000, 0b00000, 0b00000, 0b001000));
    }

    private static void jalr() {
        // jalr rd rs
        int opcode = 0b000000;
        register("jalr", opcode, List.of(REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], 0b00000, operands[0], 0b00000, 0b001001));
    }

    private static void sysc() {
        // sysc
        int opcode = 0b000000;
        register("sysc", opcode, List.of(),
                operands -> new RTypeInstruction(opcode, 0b00000, 0b00000, 0b00000, 0b00000, 0b001100));
    }

    private static void eret() {
        // eret
        int opcode = 0b010000;
        register("eret", opcode, List.of(),
                operands -> new RTypeInstruction(opcode, 0b10000, 0b00000, 0b00000, 0b00000, 0b011000));
    }

    private static void movg2s() {
        // movg2s rd rt
        int opcode = 0b010000;
        register("movg2s", opcode, List.of(REG5, REG5),
                operands -> new RTypeInstruction(opcode, 0b00100, operands[1], operands[0], 0b00000, 0b000000));
    }

    private static void movs2g() {
        // movs2g rd rt
        int opcode = 0b010000;
        register("movs2g", opcode, List.of(REG5, REG5),
                operands -> new RTypeInstruction(opcode, 0b00000, operands[1], operands[0], 0b00000, 0b000000));
    }

    private static void j() {
        // j iindex
        int opcode = 0b000010;
        register("j", opcode, List.of(IINDEX26),
                operands -> new JTypeInstruction(opcode, operands[0]));
    }

    private static void jal() {
        // jal iindex
        int opcode = 0b000011;
        register("jal", opcode, List.of(IINDEX26),
                operands -> new JTypeInstruction(opcode, operands[0]));
    }
}
