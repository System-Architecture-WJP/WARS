package com.wars.engine.Instruction;

import com.wars.engine.instruction.Instruction;
import com.wars.engine.instruction.InstructionRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JTypeInstructionTest {

    @ParameterizedTest
    @MethodSource("provideValidJTypeInstructions")
    void testJTypeInstructions(String mnemonic, int[] operands, String expectedBinary) {
        Instruction instruction = InstructionRegistry.create(mnemonic, operands);
        assertEquals(expectedBinary.replaceAll("_", ""), instruction.toBinaryString());
    }

    private static Stream<Arguments> provideValidJTypeInstructions() {
        return Stream.of(
                // j
                Arguments.of("j", new int[]{1048575}, "000010_00000011111111111111111111"),
                Arguments.of("j", new int[]{0}, "000010_00000000000000000000000000"),
                Arguments.of("j", new int[]{524287}, "000010_00000001111111111111111111"),
                Arguments.of("j", new int[]{67108863}, "000010_11111111111111111111111111"),
                // jal
                Arguments.of("jal", new int[]{1048575}, "000011_00000011111111111111111111"),
                Arguments.of("jal", new int[]{0}, "000011_00000000000000000000000000"),
                Arguments.of("jal", new int[]{524287}, "000011_00000001111111111111111111"),
                Arguments.of("jal", new int[]{67108863}, "000011_11111111111111111111111111")
                );
    }
}
