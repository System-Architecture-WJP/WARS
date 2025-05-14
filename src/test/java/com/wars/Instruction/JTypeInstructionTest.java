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

class JTypeInstructionTest {

    @ParameterizedTest
    @MethodSource("provideValidJTypeInstructions")
    void testJTypeInstructions(String mnemonic, String[] operands, String expectedBinary) {
        Instruction instruction = InstructionRegistry.create(mnemonic, operands);
        assertEquals(expectedBinary.replaceAll("_", ""), instruction.toBinaryString());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidJTypeInstructions")
    void testInvalidJTypeInstructions(String mnemonic, String[] operands) {
        assertThrows(AssemblerException.class, () -> InstructionRegistry.create(mnemonic, operands));
    }

    private static Stream<Arguments> provideValidJTypeInstructions() {
        return Stream.of(
                // j
                Arguments.of("j", new String[]{"1048575"}, "000010_00000011111111111111111111"),
                Arguments.of("j", new String[]{"0"}, "000010_00000000000000000000000000"),
                Arguments.of("j", new String[]{"524287"}, "000010_00000001111111111111111111"),
                Arguments.of("j", new String[]{"67108863"}, "000010_11111111111111111111111111"),
                // jal
                Arguments.of("jal", new String[]{"1048575"}, "000011_00000011111111111111111111"),
                Arguments.of("jal", new String[]{"0"}, "000011_00000000000000000000000000"),
                Arguments.of("jal", new String[]{"524287"}, "000011_00000001111111111111111111"),
                Arguments.of("jal", new String[]{"67108863"}, "000011_11111111111111111111111111")
                );
    }

    private static Stream<Arguments> provideInvalidJTypeInstructions() {
        return Stream.of(
                // Invalid iindex (greater than 67,108,863)
                Arguments.of("j", new String[]{"67108865"}),
                Arguments.of("j", new String[]{"67108866"}),
                Arguments.of("j", new String[]{"671088660"})
        );
    }
}
