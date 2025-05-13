package com.wars.Instruction;

import com.wars.exception.AssemblerException;
import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ITypeInstructionTest {

    @ParameterizedTest
    @MethodSource("provideValidITypeInstructions")
    void testITypeInstructions(String mnemonic, String[] operands, String expectedBinary) {
        Instruction instruction = InstructionRegistry.create(mnemonic, operands);
        assertEquals(expectedBinary.replaceAll("_", ""), instruction.toBinaryString());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidITypeInstructions")
    void testInvalidITypeInstructions(String mnemonic, String[] operands) {
        assertThrows(AssemblerException.class, () -> InstructionRegistry.create(mnemonic, operands));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidNumberFormatInstructions")
    void testInvalidNumberFormatInstructions(String mnemonic, String[] operands) {
        assertThrows(NumberFormatException.class, () -> InstructionRegistry.create(mnemonic, operands));
    }

    private static Stream<Arguments> provideValidITypeInstructions() {
        return Stream.of(
                // lw
                Arguments.of("lw", new String[]{"0", "1", "1"}, "100011_00001_00000_0000000000000001"),
                Arguments.of("lw", new String[]{"0", "5", "1"}, "100011_00101_00000_0000000000000001"),
                Arguments.of("lw", new String[]{"0", "31", "1"}, "100011_11111_00000_0000000000000001"),
                Arguments.of("lw", new String[]{"1", "5", "1"}, "100011_00101_00001_0000000000000001"),
                Arguments.of("lw", new String[]{"31", "5", "1"}, "100011_00101_11111_0000000000000001"),
                Arguments.of("lw", new String[]{"31", "5", "45"}, "100011_00101_11111_0000000000101101"),
                Arguments.of("lw", new String[]{"31", "5", "-1"}, "100011_00101_11111_1111111111111111"),
                Arguments.of("lw", new String[]{"31", "5", "-32768"}, "100011_00101_11111_1000000000000000"),
                // sw
                Arguments.of("sw", new String[]{"0", "1", "1"}, "101011_00001_00000_0000000000000001"),
                Arguments.of("sw", new String[]{"0", "5", "1"}, "101011_00101_00000_0000000000000001"),
                Arguments.of("sw", new String[]{"31", "5", "45"}, "101011_00101_11111_0000000000101101"),
                // addi
                Arguments.of("addi", new String[]{"0", "1", "1"}, "001000_00001_00000_0000000000000001"),
                Arguments.of("addi", new String[]{"0", "5", "1"}, "001000_00101_00000_0000000000000001"),
                Arguments.of("addi", new String[]{"31", "5", "-1"}, "001000_00101_11111_1111111111111111"),
                // addiu
                Arguments.of("addiu", new String[]{"0", "1", "1"}, "001001_00001_00000_0000000000000001"),
                Arguments.of("addiu", new String[]{"0", "5", "1"}, "001001_00101_00000_0000000000000001"),
                Arguments.of("addiu", new String[]{"31", "5", "-1"}, "001001_00101_11111_1111111111111111"),
                // slti
                Arguments.of("slti", new String[]{"0", "1", "1"}, "001010_00001_00000_0000000000000001"),
                Arguments.of("slti", new String[]{"0", "5", "1"}, "001010_00101_00000_0000000000000001"),
                Arguments.of("slti", new String[]{"31", "5", "-1"}, "001010_00101_11111_1111111111111111"),
                // sltiu
                Arguments.of("sltiu", new String[]{"0", "1", "1"}, "001011_00001_00000_0000000000000001"),
                Arguments.of("sltiu", new String[]{"0", "5", "1"}, "001011_00101_00000_0000000000000001"),
                Arguments.of("sltiu", new String[]{"31", "5", "-1"}, "001011_00101_11111_1111111111111111"),
                // andi
                Arguments.of("andi", new String[]{"0", "1", "1"}, "001100_00001_00000_0000000000000001"),
                Arguments.of("andi", new String[]{"0", "5", "1"}, "001100_00101_00000_0000000000000001"),
                Arguments.of("andi", new String[]{"31", "5", "-32768"}, "001100_00101_11111_1000000000000000"),
                // ori
                Arguments.of("ori", new String[]{"0", "1", "1"}, "001101_00001_00000_0000000000000001"),
                Arguments.of("ori", new String[]{"0", "5", "1"}, "001101_00101_00000_0000000000000001"),
                Arguments.of("ori", new String[]{"31", "5", "-1"}, "001101_00101_11111_1111111111111111"),
                // xori
                Arguments.of("xori", new String[]{"0", "1", "1"}, "001110_00001_00000_0000000000000001"),
                Arguments.of("xori", new String[]{"0", "5", "1"}, "001110_00101_00000_0000000000000001"),
                Arguments.of("xori", new String[]{"31", "5", "-1"}, "001110_00101_11111_1111111111111111"),
                // lui
                Arguments.of("lui", new String[]{"1", "1"}, "001111_00000_00001_0000000000000001"),
                Arguments.of("lui", new String[]{"31", "32767"}, "001111_00000_11111_0111111111111111"),
                Arguments.of("lui", new String[]{"31", "-32768"}, "001111_00000_11111_1000000000000000"),
                Arguments.of("lui", new String[]{"0", "0"}, "001111_00000_00000_0000000000000000"),
                Arguments.of("lui", new String[]{"15", "-1"}, "001111_00000_01111_1111111111111111"),
                // bltz
                Arguments.of("bltz", new String[]{"1", "1"}, "000001_00001_00000_0000000000000001"),
                Arguments.of("bltz", new String[]{"31", "32767"}, "000001_11111_00000_0111111111111111"),
                Arguments.of("bltz", new String[]{"31", "-32768"}, "000001_11111_00000_1000000000000000"),
                Arguments.of("bltz", new String[]{"0", "0"}, "000001_00000_00000_0000000000000000"),
                Arguments.of("bltz", new String[]{"15", "-1"}, "000001_01111_00000_1111111111111111"),
                // bgez
                Arguments.of("bgez", new String[]{"1", "1"}, "000001_00001_00001_0000000000000001"),
                Arguments.of("bgez", new String[]{"31", "32767"}, "000001_11111_00001_0111111111111111"),
                Arguments.of("bgez", new String[]{"31", "-32768"}, "000001_11111_00001_1000000000000000"),
                Arguments.of("bgez", new String[]{"0", "0"}, "000001_00000_00001_0000000000000000"),
                Arguments.of("bgez", new String[]{"15", "-1"}, "000001_01111_00001_1111111111111111"),
                // beq
                Arguments.of("beq", new String[]{"1", "1", "1"}, "000000_00001_00001_0000000000000001"),
                Arguments.of("beq", new String[]{"31", "31", "32767"}, "000000_11111_11111_0111111111111111"),
                Arguments.of("beq", new String[]{"31", "0", "-32768"}, "000000_11111_00000_1000000000000000"),
                Arguments.of("beq", new String[]{"0", "0", "0"}, "000000_00000_00000_0000000000000000"),
                Arguments.of("beq", new String[]{"15", "15", "-1"}, "000000_01111_01111_1111111111111111"),
                // bne
                Arguments.of("bne", new String[]{"1", "1", "1"}, "000101_00001_00001_0000000000000001"),
                Arguments.of("bne", new String[]{"31", "31", "32767"}, "000101_11111_11111_0111111111111111"),
                Arguments.of("bne", new String[]{"31", "0", "-32768"}, "000101_11111_00000_1000000000000000"),
                Arguments.of("bne", new String[]{"0", "0", "0"}, "000101_00000_00000_0000000000000000"),
                Arguments.of("bne", new String[]{"15", "15", "-1"}, "000101_01111_01111_1111111111111111"),
                // blez
                Arguments.of("blez", new String[]{"1", "1"}, "000110_00001_00000_0000000000000001"),
                Arguments.of("blez", new String[]{"31", "32767"}, "000110_11111_00000_0111111111111111"),
                Arguments.of("blez", new String[]{"31", "-32768"}, "000110_11111_00000_1000000000000000"),
                Arguments.of("blez", new String[]{"0", "0"}, "000110_00000_00000_0000000000000000"),
                Arguments.of("blez", new String[]{"15", "-1"}, "000110_01111_00000_1111111111111111"),
                // bgtz
                Arguments.of("bgtz", new String[]{"1", "1"}, "000111_00001_00000_0000000000000001"),
                Arguments.of("bgtz", new String[]{"31", "32767"}, "000111_11111_00000_0111111111111111"),
                Arguments.of("bgtz", new String[]{"31", "-32768"}, "000111_11111_00000_1000000000000000"),
                Arguments.of("bgtz", new String[]{"0", "0"}, "000111_00000_00000_0000000000000000"),
                Arguments.of("bgtz", new String[]{"15", "-1"}, "000111_01111_00000_1111111111111111")
        );
    }

    private static Stream<Arguments> provideInvalidNumberFormatInstructions() {
        return Stream.of(
                // Invalid number formats for rs, rt, and imm
                Arguments.of("addi", new String[]{"abc", "1", "1"}), // Invalid rs
                Arguments.of("addi", new String[]{"1", "xyz", "1"}), // Invalid rt
                Arguments.of("addi", new String[]{"1", "1", "12.34"}), // Invalid imm
                Arguments.of("addi", new String[]{"1", "1", "1e10"}), // Invalid imm (scientific notation)
                Arguments.of("addi", new String[]{"-1", "1", "1"}), // rs < 0
                Arguments.of("addi", new String[]{"1", "-1", "1"}) // rt < 0
        );
    }

    private static Stream<Arguments> provideInvalidITypeInstructions() {
        return Stream.of(
                // Invalid rs (greater than 31)
                Arguments.of("addi", new String[]{"32", "1", "1"}), // rs > 31

                // Invalid rt (greater than 31)
                Arguments.of("addi", new String[]{"1", "32", "1"}), // rt > 31

                // Invalid imm (greater than 32767 or less than -32768)
                Arguments.of("addi", new String[]{"1", "1", "32768"}), // imm > 32767
                Arguments.of("addi", new String[]{"1", "1", "-32769"}) // imm < -32768
        );
    }
}
