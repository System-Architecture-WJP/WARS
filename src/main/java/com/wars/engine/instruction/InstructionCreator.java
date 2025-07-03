package com.wars.engine.instruction;

@FunctionalInterface
interface InstructionCreator {
    Instruction create(int[] operands);
}
