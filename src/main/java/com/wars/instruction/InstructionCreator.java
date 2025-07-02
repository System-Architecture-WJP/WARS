package com.wars.instruction;

@FunctionalInterface
interface InstructionCreator {
    Instruction create(int[] operands);
}
