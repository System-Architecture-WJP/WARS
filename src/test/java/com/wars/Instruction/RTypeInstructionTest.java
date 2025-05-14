package com.wars.Instruction;

import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RTypeInstructionTest {

    @ParameterizedTest
    @MethodSource("provideValidRTypeInstructions")
    void testRTypeInstructions(String mnemonic, String[] operands, String expectedBinary) {
        Instruction instruction = InstructionRegistry.create(mnemonic, operands);
        assertEquals(expectedBinary.replaceAll("_", ""), instruction.toBinaryString());
    }

    private static Stream<Arguments> provideValidRTypeInstructions() {
        return Stream.of(
                // srl
                Arguments.of("srl", new String[]{"1", "2", "3"}, "000000_00000_00010_00001_00011_000010"),
                Arguments.of("srl", new String[]{"31", "31", "31"}, "000000_00000_11111_11111_11111_000010"),
                Arguments.of("srl", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_000010"),
                Arguments.of("srl", new String[]{"15", "15", "15"}, "000000_00000_01111_01111_01111_000010"),
                // add
                Arguments.of("add", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_100000"),
                Arguments.of("add", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_100000"),
                Arguments.of("add", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_100000"),
                Arguments.of("add", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_100000"),
                // addu
                Arguments.of("addu", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_100001"),
                Arguments.of("addu", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_100001"),
                Arguments.of("addu", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_100001"),
                Arguments.of("addu", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_100001"),
                // sub
                Arguments.of("sub", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_100010"),
                Arguments.of("sub", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_100010"),
                Arguments.of("sub", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_100010"),
                Arguments.of("sub", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_100010"),
                // subu
                Arguments.of("subu", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_100011"),
                Arguments.of("subu", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_100011"),
                Arguments.of("subu", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_100011"),
                Arguments.of("subu", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_100011"),
                // and
                Arguments.of("and", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_100100"),
                Arguments.of("and", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_100100"),
                Arguments.of("and", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_100100"),
                Arguments.of("and", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_100100"),
                // or
                Arguments.of("or", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_100101"),
                Arguments.of("or", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_100101"),
                Arguments.of("or", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_100101"),
                Arguments.of("or", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_100101"),
                // xor
                Arguments.of("xor", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_100110"),
                Arguments.of("xor", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_100110"),
                Arguments.of("xor", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_100110"),
                Arguments.of("xor", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_100110"),
                // nor
                Arguments.of("nor", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_100111"),
                Arguments.of("nor", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_100111"),
                Arguments.of("nor", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_100111"),
                Arguments.of("nor", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_100111"),
                // slt
                Arguments.of("slt", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_101010"),
                Arguments.of("slt", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_101010"),
                Arguments.of("slt", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_101010"),
                Arguments.of("slt", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_101010"),
                // sltu
                Arguments.of("sltu", new String[]{"1", "2", "3"}, "000000_00010_00011_00001_00000_101011"),
                Arguments.of("sltu", new String[]{"31", "31", "31"}, "000000_11111_11111_11111_00000_101011"),
                Arguments.of("sltu", new String[]{"0", "0", "0"}, "000000_00000_00000_00000_00000_101011"),
                Arguments.of("sltu", new String[]{"15", "15", "15"}, "000000_01111_01111_01111_00000_101011"),
                // jr
                Arguments.of("jr", new String[]{"1"}, "000000_00001_00000_00000_00000_001000"),
                Arguments.of("jr", new String[]{"31"}, "000000_11111_00000_00000_00000_001000"),
                Arguments.of("jr", new String[]{"0"}, "000000_00000_00000_00000_00000_001000"),
                Arguments.of("jr", new String[]{"15"}, "000000_01111_00000_00000_00000_001000"),
                // jalr
                Arguments.of("jalr", new String[]{"1", "2"}, "000000_00010_00000_00001_00000_001001"),
                Arguments.of("jalr", new String[]{"31", "31"}, "000000_11111_00000_11111_00000_001001"),
                Arguments.of("jalr", new String[]{"0", "0"}, "000000_00000_00000_00000_00000_001001"),
                Arguments.of("jalr", new String[]{"15", "15"}, "000000_01111_00000_01111_00000_001001"),
                // sysc
                Arguments.of("sysc", new String[]{}, "000000_00000_00000_00000_00000_001100"),
                // eret
                Arguments.of("eret", new String[]{}, "010000_10000_00000_00000_00000_011000"),
                // movg2s
                Arguments.of("movg2s", new String[]{"1", "2"}, "010000_00100_00010_00001_00000_000000"),
                Arguments.of("movg2s", new String[]{"31", "31"}, "010000_00100_11111_11111_00000_000000"),
                Arguments.of("movg2s", new String[]{"0", "0"}, "010000_00100_00000_00000_00000_000000"),
                Arguments.of("movg2s", new String[]{"15", "15"}, "010000_00100_01111_01111_00000_000000"),
                // movs2g
                Arguments.of("movs2g", new String[]{"1", "2"}, "010000_00000_00010_00001_00000_000000"),
                Arguments.of("movs2g", new String[]{"31", "31"}, "010000_00000_11111_11111_00000_000000"),
                Arguments.of("movs2g", new String[]{"0", "0"}, "010000_00000_00000_00000_00000_000000"),
                Arguments.of("movs2g", new String[]{"15", "15"}, "010000_00000_01111_01111_00000_000000")
        );
    }
}
