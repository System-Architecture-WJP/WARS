package com.wars.engine.Instruction;

import com.wars.engine.instruction.Instruction;
import com.wars.engine.instruction.InstructionRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RTypeInstructionTest {

    @ParameterizedTest
    @MethodSource("provideValidRTypeInstructions")
    void testRTypeInstructions(String mnemonic, int[] operands, String expectedBinary) {
        Instruction instruction = InstructionRegistry.create(mnemonic, operands);
        assertEquals(expectedBinary.replaceAll("_", ""), instruction.toBinaryString());
    }

    private static Stream<Arguments> provideValidRTypeInstructions() {
        return Stream.of(
                // srl
                Arguments.of("srl", new int[]{1, 2, 3}, "000000_00000_00010_00001_00011_000010"),
                Arguments.of("srl", new int[]{31, 31, 31}, "000000_00000_11111_11111_11111_000010"),
                Arguments.of("srl", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_000010"),
                Arguments.of("srl", new int[]{15, 15, 15}, "000000_00000_01111_01111_01111_000010"),
                // add
                Arguments.of("add", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_100000"),
                Arguments.of("add", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_100000"),
                Arguments.of("add", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_100000"),
                Arguments.of("add", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_100000"),
                // addu
                Arguments.of("addu", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_100001"),
                Arguments.of("addu", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_100001"),
                Arguments.of("addu", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_100001"),
                Arguments.of("addu", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_100001"),
                // sub
                Arguments.of("sub", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_100010"),
                Arguments.of("sub", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_100010"),
                Arguments.of("sub", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_100010"),
                Arguments.of("sub", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_100010"),
                // subu
                Arguments.of("subu", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_100011"),
                Arguments.of("subu", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_100011"),
                Arguments.of("subu", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_100011"),
                Arguments.of("subu", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_100011"),
                // and
                Arguments.of("and", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_100100"),
                Arguments.of("and", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_100100"),
                Arguments.of("and", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_100100"),
                Arguments.of("and", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_100100"),
                // or
                Arguments.of("or", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_100101"),
                Arguments.of("or", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_100101"),
                Arguments.of("or", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_100101"),
                Arguments.of("or", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_100101"),
                // xor
                Arguments.of("xor", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_100110"),
                Arguments.of("xor", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_100110"),
                Arguments.of("xor", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_100110"),
                Arguments.of("xor", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_100110"),
                // nor
                Arguments.of("nor", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_100111"),
                Arguments.of("nor", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_100111"),
                Arguments.of("nor", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_100111"),
                Arguments.of("nor", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_100111"),
                // slt
                Arguments.of("slt", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_101010"),
                Arguments.of("slt", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_101010"),
                Arguments.of("slt", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_101010"),
                Arguments.of("slt", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_101010"),
                // sltu
                Arguments.of("sltu", new int[]{1, 2, 3}, "000000_00010_00011_00001_00000_101011"),
                Arguments.of("sltu", new int[]{31, 31, 31}, "000000_11111_11111_11111_00000_101011"),
                Arguments.of("sltu", new int[]{0, 0, 0}, "000000_00000_00000_00000_00000_101011"),
                Arguments.of("sltu", new int[]{15, 15, 15}, "000000_01111_01111_01111_00000_101011"),
                // jr
                Arguments.of("jr", new int[]{1}, "000000_00001_00000_00000_00000_001000"),
                Arguments.of("jr", new int[]{31}, "000000_11111_00000_00000_00000_001000"),
                Arguments.of("jr", new int[]{0}, "000000_00000_00000_00000_00000_001000"),
                Arguments.of("jr", new int[]{15}, "000000_01111_00000_00000_00000_001000"),
                // jalr
                Arguments.of("jalr", new int[]{1, 2}, "000000_00010_00000_00001_00000_001001"),
                Arguments.of("jalr", new int[]{31, 31}, "000000_11111_00000_11111_00000_001001"),
                Arguments.of("jalr", new int[]{0, 0}, "000000_00000_00000_00000_00000_001001"),
                Arguments.of("jalr", new int[]{15, 15}, "000000_01111_00000_01111_00000_001001"),
                // sysc
                Arguments.of("sysc", new int[]{}, "000000_00000_00000_00000_00000_001100"),
                // eret
                Arguments.of("eret", new int[]{}, "010000_10000_00000_00000_00000_011000"),
                // movg2s
                Arguments.of("movg2s", new int[]{1, 2}, "010000_00100_00010_00001_00000_000000"),
                Arguments.of("movg2s", new int[]{31, 31}, "010000_00100_11111_11111_00000_000000"),
                Arguments.of("movg2s", new int[]{0, 0}, "010000_00100_00000_00000_00000_000000"),
                Arguments.of("movg2s", new int[]{15, 15}, "010000_00100_01111_01111_00000_000000"),
                // movs2g
                Arguments.of("movs2g", new int[]{1, 2}, "010000_00000_00010_00001_00000_000000"),
                Arguments.of("movs2g", new int[]{31, 31}, "010000_00000_11111_11111_00000_000000"),
                Arguments.of("movs2g", new int[]{0, 0}, "010000_00000_00000_00000_00000_000000"),
                Arguments.of("movs2g", new int[]{15, 15}, "010000_00000_01111_01111_00000_000000")
        );
    }
}
