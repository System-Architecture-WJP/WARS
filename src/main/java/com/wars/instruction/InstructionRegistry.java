package com.wars.instruction;

import com.wars.constant.OperandType;
import com.wars.exception.AssemblerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wars.constant.OperandType.IMM16;
import static com.wars.constant.OperandType.REG5;

public class InstructionRegistry {
    private static final Map<String, InstructionDescriptor> registry = new HashMap<>();

    static {
        // Registering I-Type instructions
        register("lw", InstructionRegistry::lw, List.of(REG5, REG5, IMM16));
        register("sw", InstructionRegistry::sw, List.of(REG5, REG5, IMM16));
        register("addi", InstructionRegistry::addi, List.of(REG5, REG5, IMM16));
        register("addiu", InstructionRegistry::addiu, List.of(REG5, REG5, IMM16));
        register("slti", InstructionRegistry::slti, List.of(REG5, REG5, IMM16));
        register("sltiu", InstructionRegistry::sltiu, List.of(REG5, REG5, IMM16));
        register("andi", InstructionRegistry::andi, List.of(REG5, REG5, IMM16));
        register("ori", InstructionRegistry::ori, List.of(REG5, REG5, IMM16));
        register("xori", InstructionRegistry::xori, List.of(REG5, REG5, IMM16));
        register("lui", InstructionRegistry::lui, List.of(REG5, IMM16));
        register("bltz", InstructionRegistry::bltz, List.of(REG5, IMM16));
        register("bgez", InstructionRegistry::bgez, List.of(REG5, IMM16));
        register("beq", InstructionRegistry::beq, List.of(REG5, REG5, IMM16));
        register("bne", InstructionRegistry::bne, List.of(REG5, REG5, IMM16));
        register("blez", InstructionRegistry::blez, List.of(REG5, IMM16));
        register("bgtz", InstructionRegistry::bgtz, List.of(REG5, IMM16));

        // Registering R-Type instructions
        register("srl", InstructionRegistry::srl, List.of(REG5, REG5, REG5));
        register("add", InstructionRegistry::add, List.of(REG5, REG5, REG5));
        register("addu", InstructionRegistry::addu, List.of(REG5, REG5, REG5));
        register("sub", InstructionRegistry::sub, List.of(REG5, REG5, REG5));
        register("subu", InstructionRegistry::subu, List.of(REG5, REG5, REG5));
        register("and", InstructionRegistry::and, List.of(REG5, REG5, REG5));
        register("or", InstructionRegistry::or, List.of(REG5, REG5, REG5));
        register("xor", InstructionRegistry::xor, List.of(REG5, REG5, REG5));
        register("nor", InstructionRegistry::nor, List.of(REG5, REG5, REG5));
        register("slt", InstructionRegistry::slt, List.of(REG5, REG5, REG5));
        register("sltu", InstructionRegistry::sltu, List.of(REG5, REG5, REG5));
        register("jr", InstructionRegistry::jr, List.of(REG5));
        register("jalr", InstructionRegistry::jalr, List.of(REG5, REG5));
        register("sysc", InstructionRegistry::sysc, List.of());
        register("eret", InstructionRegistry::eret, List.of());
        register("movg2s", InstructionRegistry::movg2s, List.of(REG5, REG5));
        register("movs2g", InstructionRegistry::movs2g, List.of(REG5, REG5));

        // Registering J-Type instructions
    }

    private static void register(String mnemonic, InstructionCreator creator, List<OperandType> operandTypes) {
        registry.put(mnemonic, new InstructionDescriptor(operandTypes, creator));
    }

    public static InstructionDescriptor get(String mnemonic) {
        InstructionDescriptor descriptor = registry.get(mnemonic);
        if (descriptor == null) {
            throw new AssemblerException("Unknown instruction: " + mnemonic + "Possible instructions: " + registry.keySet());
        }
        return descriptor;
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

    private static Instruction srl(int[] operands) {
        // srl rd rt sa
        return new RTypeInstruction(
                0b000000,
                0b00000,
                operands[1],
                operands[0],
                operands[2],
                0b000010);
    }

    private static Instruction add(int[] operands) {
        // add rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b100000);
    }

    private static Instruction addu(int[] operands) {
        // addu rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b100001);
    }

    private static Instruction sub(int[] operands) {
        // sub rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b100010);
    }

    private static Instruction subu(int[] operands) {
        // subu rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b100011);
    }

    private static Instruction and(int[] operands) {
        // and rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b100100);
    }


    private static Instruction or(int[] operands) {
        // or rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b100101);
    }


    private static Instruction xor(int[] operands) {
        // xor rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b100110);
    }


    private static Instruction nor(int[] operands) {
        // nor rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b100111);
    }

    private static Instruction slt(int[] operands) {
        // slt rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b101010);
    }

    private static Instruction sltu(int[] operands) {
        // sltu rd rs rt
        return new RTypeInstruction(
                0b000000,
                operands[1],
                operands[2],
                operands[0],
                0b00000,
                0b101011);
    }

    private static Instruction jr(int[] operands) {
        // jr rs
        return new RTypeInstruction(
                0b000000,
                operands[0],
                0b00000,
                0b00000,
                0b00000,
                0b001000);
    }

    private static Instruction jalr(int[] operands) {
        // jalr rd rs
        return new RTypeInstruction(
                0b000000,
                operands[1],
                0b00000,
                operands[0],
                0b00000,
                0b001001);
    }

    private static Instruction sysc(int[] operands) {
        // sysc
        return new RTypeInstruction(
                0b000000,
                0b00000,
                0b00000,
                0b00000,
                0b00000,
                0b001100);
    }

    private static Instruction eret(int[] operands) {
        // eret
        return new RTypeInstruction(
                0b010000,
                0b10000,
                0b00000,
                0b00000,
                0b00000,
                0b011000);
    }

    private static Instruction movg2s(int[] operands) {
        // movg2s rd rt
        return new RTypeInstruction(
                0b010000,
                0b00100,
                operands[1],
                operands[0],
                0b00000,
                0b000000);
    }

    private static Instruction movs2g(int[] operands) {
        // movs2g rd rt
        return new RTypeInstruction(
                0b010000,
                0b00000,
                operands[1],
                operands[0],
                0b00000,
                0b000000);
    }
}
