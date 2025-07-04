package com.wars.engine.Instruction;

import com.wars.engine.instruction.Instruction;
import com.wars.engine.instruction.InstructionRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ITypeInstructionTest {

    @ParameterizedTest
    @MethodSource("provideValidITypeInstructions")
    void testITypeInstructions(String mnemonic, int[] operands, String expectedBinary) {
        Instruction instruction = InstructionRegistry.createForEncoder(mnemonic, operands);
        assertEquals(expectedBinary.replaceAll("_", ""), instruction.toBinaryString());
    }

    private static Stream<Arguments> provideValidITypeInstructions() {
        return Stream.of(
                // lw
                Arguments.of("lw", new int[]{0, 1, 1}, "100011_00001_00000_0000000000000001"),
                Arguments.of("lw", new int[]{0, 5, 1}, "100011_00101_00000_0000000000000001"),
                Arguments.of("lw", new int[]{0, 31, 1}, "100011_11111_00000_0000000000000001"),
                Arguments.of("lw", new int[]{1, 5, 1}, "100011_00101_00001_0000000000000001"),
                Arguments.of("lw", new int[]{31, 5, 1}, "100011_00101_11111_0000000000000001"),
                Arguments.of("lw", new int[]{31, 5, 45}, "100011_00101_11111_0000000000101101"),
                Arguments.of("lw", new int[]{31, 5, -1}, "100011_00101_11111_1111111111111111"),
                Arguments.of("lw", new int[]{31, 5, -32768}, "100011_00101_11111_1000000000000000"),
                // sw
                Arguments.of("sw", new int[]{0, 1, 1}, "101011_00001_00000_0000000000000001"),
                Arguments.of("sw", new int[]{0, 5, 1}, "101011_00101_00000_0000000000000001"),
                Arguments.of("sw", new int[]{31, 5, 45}, "101011_00101_11111_0000000000101101"),
                // addi
                Arguments.of("addi", new int[]{0, 1, 1}, "001000_00001_00000_0000000000000001"),
                Arguments.of("addi", new int[]{0, 5, 1}, "001000_00101_00000_0000000000000001"),
                Arguments.of("addi", new int[]{31, 5, -1}, "001000_00101_11111_1111111111111111"),
                // addiu
                Arguments.of("addiu", new int[]{0, 1, 1}, "001001_00001_00000_0000000000000001"),
                Arguments.of("addiu", new int[]{0, 5, 1}, "001001_00101_00000_0000000000000001"),
                Arguments.of("addiu", new int[]{31, 5, -1}, "001001_00101_11111_1111111111111111"),
                // slti
                Arguments.of("slti", new int[]{0, 1, 1}, "001010_00001_00000_0000000000000001"),
                Arguments.of("slti", new int[]{0, 5, 1}, "001010_00101_00000_0000000000000001"),
                Arguments.of("slti", new int[]{31, 5, -1}, "001010_00101_11111_1111111111111111"),
                // sltiu
                Arguments.of("sltiu", new int[]{0, 1, 1}, "001011_00001_00000_0000000000000001"),
                Arguments.of("sltiu", new int[]{0, 5, 1}, "001011_00101_00000_0000000000000001"),
                Arguments.of("sltiu", new int[]{31, 5, -1}, "001011_00101_11111_1111111111111111"),
                // andi
                Arguments.of("andi", new int[]{0, 1, 1}, "001100_00001_00000_0000000000000001"),
                Arguments.of("andi", new int[]{0, 5, 1}, "001100_00101_00000_0000000000000001"),
                Arguments.of("andi", new int[]{31, 5, -32768}, "001100_00101_11111_1000000000000000"),
                // ori
                Arguments.of("ori", new int[]{0, 1, 1}, "001101_00001_00000_0000000000000001"),
                Arguments.of("ori", new int[]{0, 5, 1}, "001101_00101_00000_0000000000000001"),
                Arguments.of("ori", new int[]{31, 5, -1}, "001101_00101_11111_1111111111111111"),
                // xori
                Arguments.of("xori", new int[]{0, 1, 1}, "001110_00001_00000_0000000000000001"),
                Arguments.of("xori", new int[]{0, 5, 1}, "001110_00101_00000_0000000000000001"),
                Arguments.of("xori", new int[]{31, 5, -1}, "001110_00101_11111_1111111111111111"),
                // lui
                Arguments.of("lui", new int[]{1, 1}, "001111_00000_00001_0000000000000001"),
                Arguments.of("lui", new int[]{31, 32767}, "001111_00000_11111_0111111111111111"),
                Arguments.of("lui", new int[]{31, -32768}, "001111_00000_11111_1000000000000000"),
                Arguments.of("lui", new int[]{0, 0}, "001111_00000_00000_0000000000000000"),
                Arguments.of("lui", new int[]{15, -1}, "001111_00000_01111_1111111111111111"),
                // bltz
                Arguments.of("bltz", new int[]{1, 1}, "000001_00001_00000_0000000000000001"),
                Arguments.of("bltz", new int[]{31, 32767}, "000001_11111_00000_0111111111111111"),
                Arguments.of("bltz", new int[]{31, -32768}, "000001_11111_00000_1000000000000000"),
                Arguments.of("bltz", new int[]{0, 0}, "000001_00000_00000_0000000000000000"),
                Arguments.of("bltz", new int[]{15, -1}, "000001_01111_00000_1111111111111111"),
                // bgez
                Arguments.of("bgez", new int[]{1, 1}, "000001_00001_00001_0000000000000001"),
                Arguments.of("bgez", new int[]{31, 32767}, "000001_11111_00001_0111111111111111"),
                Arguments.of("bgez", new int[]{31, -32768}, "000001_11111_00001_1000000000000000"),
                Arguments.of("bgez", new int[]{0, 0}, "000001_00000_00001_0000000000000000"),
                Arguments.of("bgez", new int[]{15, -1}, "000001_01111_00001_1111111111111111"),
                // beq
                Arguments.of("beq", new int[]{1, 1, 1}, "000100_00001_00001_0000000000000001"),
                Arguments.of("beq", new int[]{31, 31, 32767}, "000100_11111_11111_0111111111111111"),
                Arguments.of("beq", new int[]{31, 0, -32768}, "000100_11111_00000_1000000000000000"),
                Arguments.of("beq", new int[]{0, 0, 0}, "000100_00000_00000_0000000000000000"),
                Arguments.of("beq", new int[]{15, 15, -1}, "000100_01111_01111_1111111111111111"),
                // bne
                Arguments.of("bne", new int[]{1, 1, 1}, "000101_00001_00001_0000000000000001"),
                Arguments.of("bne", new int[]{31, 31, 32767}, "000101_11111_11111_0111111111111111"),
                Arguments.of("bne", new int[]{31, 0, -32768}, "000101_11111_00000_1000000000000000"),
                Arguments.of("bne", new int[]{0, 0, 0}, "000101_00000_00000_0000000000000000"),
                Arguments.of("bne", new int[]{15, 15, -1}, "000101_01111_01111_1111111111111111"),
                // blez
                Arguments.of("blez", new int[]{1, 1}, "000110_00001_00000_0000000000000001"),
                Arguments.of("blez", new int[]{31, 32767}, "000110_11111_00000_0111111111111111"),
                Arguments.of("blez", new int[]{31, -32768}, "000110_11111_00000_1000000000000000"),
                Arguments.of("blez", new int[]{0, 0}, "000110_00000_00000_0000000000000000"),
                Arguments.of("blez", new int[]{15, -1}, "000110_01111_00000_1111111111111111"),
                // bgtz
                Arguments.of("bgtz", new int[]{1, 1}, "000111_00001_00000_0000000000000001"),
                Arguments.of("bgtz", new int[]{31, 32767}, "000111_11111_00000_0111111111111111"),
                Arguments.of("bgtz", new int[]{31, -32768}, "000111_11111_00000_1000000000000000"),
                Arguments.of("bgtz", new int[]{0, 0}, "000111_00000_00000_0000000000000000"),
                Arguments.of("bgtz", new int[]{15, -1}, "000111_01111_00000_1111111111111111")
        );
    }
}
