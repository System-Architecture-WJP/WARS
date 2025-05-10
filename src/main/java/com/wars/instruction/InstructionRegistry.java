package com.wars.instruction;

import java.util.HashMap;
import java.util.Map;

public class InstructionRegistry {
    private static final Map<String, InstructionCreator> registry = new HashMap<>();

    static {
        // Registering I-Type instructions
        register("lw", InstructionRegistry::lw);
        register("sw", InstructionRegistry::sw);
        register("addi", InstructionRegistry::addi);
        register("addiu", InstructionRegistry::addiu);
        register("slti", InstructionRegistry::slti);
        register("sltiu", InstructionRegistry::sltiu);
        register("andi", InstructionRegistry::andi);
        register("ori", InstructionRegistry::ori);
        register("xori", InstructionRegistry::xori);
        register("lui", InstructionRegistry::lui);
        register("bltz", InstructionRegistry::bltz);
        register("bgez", InstructionRegistry::bgez);
        register("beq", InstructionRegistry::beq);
        register("bne", InstructionRegistry::bne);
        register("blez", InstructionRegistry::blez);
        register("bgtz", InstructionRegistry::bgtz);

        // Registering R-Type instructions
        // Registering J-Type instructions
    }

    private static void register(String mnemonic, InstructionCreator creator) {
        registry.put(mnemonic, creator);
    }

    public static Instruction create(String mnemonic, int[] operands) {
        InstructionCreator creator = registry.get(mnemonic);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown instruction: " + mnemonic);
        }
        return creator.create(operands);
    }

    private static Instruction lw(int[] operands) {
        // lw rt rs imm
        return new ITypeInstruction(
                0b100011,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction sw(int[] operands) {
        // sw rt rs imm
        return new ITypeInstruction(
                0b101011,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction addi(int[] operands) {
        // addi rt rs imm
        return new ITypeInstruction(
                0b001000,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction addiu(int[] operands) {
        // addiu rt rs imm
        return new ITypeInstruction(
                0b001001,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction slti(int[] operands) {
        // slti rt rs imm
        return new ITypeInstruction(
                0b001010,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction sltiu(int[] operands) {
        // sltiu rt rs imm
        return new ITypeInstruction(
                0b001011,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction andi(int[] operands) {
        // andi rt rs imm
        return new ITypeInstruction(
                0b001100,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction ori(int[] operands) {
        // ori rt rs imm
        return new ITypeInstruction(
                0b001101,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction xori(int[] operands) {
        // xori rt rs imm
        return new ITypeInstruction(
                0b001110,
                operands[1],
                operands[0],
                operands[2]);
    }

    private static Instruction lui(int[] operands) {
        // lui rt imm
        return new ITypeInstruction(
                0b001111,
                0b000000,
                operands[0],
                operands[2]);
    }

    private static Instruction bltz(int[] operands) {
        // bltz rs imm
        return new ITypeInstruction(
                0b000001,
                operands[0],
                0b00000,
                operands[1]);
    }

    private static Instruction bgez(int[] operands) {
        // bgez rs imm
        return new ITypeInstruction(
                0b000001,
                operands[0],
                0b00001,
                operands[1]);
    }

    private static Instruction beq(int[] operands) {
        // beq rs rt imm
        return new ITypeInstruction(
                0b000000,
                operands[0],
                operands[1],
                operands[2]);
    }

    private static Instruction bne(int[] operands) {
        // bne rs rt imm
        return new ITypeInstruction(
                0b000101,
                operands[0],
                operands[1],
                operands[2]);
    }

    private static Instruction blez(int[] operands) {
        // blez rs imm
        return new ITypeInstruction(
                0b000110,
                operands[0],
                0b00000,
                operands[1]);
    }

    private static Instruction bgtz(int[] operands) {
        // bgtz rs imm
        return new ITypeInstruction(
                0b000111,
                operands[0],
                0b00000,
                operands[1]);
    }

    private static int parseReg(String val, int bits) {
        int number = Integer.parseUnsignedInt(val);
        int max = (1 << bits) - 1;

        if (number > max) {
            throw new IllegalArgumentException("Register number " + val + " out of bounds for " + bits + "-bit unsigned field (0 to " + max + ")");
        }

        return number;
    }


    private static int parseImm(String val) {
        int number = Integer.parseInt(val);

        int min = -(1 << (16 - 1));
        int max = (1 << (16 - 1)) - 1;

        if (number < min || number > max) {
            throw new IllegalArgumentException("Value " + val + " out of bounds for " + 16 + "-bit signed field (" + min + " to " + max + ")");
        }

        return number;
    }

}
