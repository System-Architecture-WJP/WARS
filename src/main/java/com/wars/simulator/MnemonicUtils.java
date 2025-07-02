package com.wars.simulator;

import com.wars.exception.SimulatorException;

class MnemonicUtils {
    public static String getMnemonicForIType(int opcode, int rt) {
        if (opcode == 0b000001 && rt == 0b00000) {
            return "bltz";
        }

        if (opcode == 0b000001 && rt == 0b00001) {
            return "bgez";
        }

        return switch (opcode) {
            case 0b100011 -> "lw";
            case 0b101011 -> "sw";

            case 0b001000 -> "addi";
            case 0b001001 -> "addiu";
            case 0b001010 -> "slti";
            case 0b001011 -> "sltiu";
            case 0b001100 -> "andi";
            case 0b001101 -> "ori";
            case 0b001110 -> "xori";
            case 0b001111 -> "lui";

            case 0b000100 -> "beq";
            case 0b000101 -> "bne";
            case 0b000110 -> "blez";
            case 0b000111 -> "bgtz";

            default -> throw new SimulatorException("Unknown I-type opcode: " + opcode);
        };
    }

    public static String getMnemonicForRType(int opcode, int fun, int rs) {
        if (opcode == 0b000000) {
            return switch (fun) {
                case 0b000010 -> "srl";

                case 0b100000 -> "add";
                case 0b100001 -> "addu";
                case 0b100010 -> "sub";
                case 0b100011 -> "subu";

                case 0b100100 -> "and";
                case 0b100101 -> "or";
                case 0b100110 -> "xor";
                case 0b100111 -> "nor";

                case 0b101010 -> "slt";
                case 0b101011 -> "sltu";

                case 0b001000 -> "jr";
                case 0b001001 -> "jalr";
                case 0b001100 -> "sysc";
                default -> throw new SimulatorException("Unknown R-type function code: " + fun);
            };
        }

        if (opcode == 0b010000) {
            return switch (rs) {
                case 0b10000 -> "eret";
                case 0b00100 -> "movg2s";
                case 0b00000 -> "mov2gs";
                default -> throw new SimulatorException("Unknown R-type function code for opcode 010000: " + fun);
            };
        }

        throw new SimulatorException("Unknown R-type opcode: " + opcode);
    }

    public static String getMnemonicForJType(int opcode) {
        if (opcode == 0b000010) {
            return "j";
        }

        if (opcode == 0b000011) {
            return "jal";
        }

        throw new SimulatorException("Unknown J-type opcode: " + opcode);
    }
}
