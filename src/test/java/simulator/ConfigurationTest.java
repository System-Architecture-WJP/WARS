package simulator;

import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import com.wars.simulator.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationTest {
    private Configuration config;

    @BeforeEach
    void setUp() {
        config = new Configuration(1024); // Initialize with 1KB of memory
    }

    @Test
    void test_lw_with_0_imm() {
        // Setup: Store a word at memory address 100
        int valueToStore = 14;
        int address = 100;
        int rs = 2; // we'll use $2 as base
        int rt = 3; // we'll load into $3
        int imm = 0;

        config.setRegister(rs, address);    // $2 = 100
        config.setWord(address, valueToStore);  // Mem[100] = 14

        // Create and execute the lw instruction: lw $3, 0($2)
        Instruction lw = InstructionRegistry.getExecutableInstruction("lw", new int[]{rs, rt, imm});
        lw.execute(config);

        // Assert
        assertEquals(valueToStore, config.getRegister(rt),
                "Register $3 should hold the value loaded from memory.");
    }

    @Test
    void test_lw_with_positive_imm() {
        // Setup: Store a word at memory address 100
        int baseAddress = 200;
        int imm = 8; // positive immediate
        int effectiveAddress = baseAddress + imm;
        int valueToStore = 42;
        int rs = 2; // base register
        int rt = 3; // destination register

        config.setRegister(rs, baseAddress); // $2 = 200
        config.setWord(effectiveAddress, valueToStore); // Mem[208] = 42

        // Create and execute the lw instruction: lw $3, 0($2)
        Instruction lw = InstructionRegistry.getExecutableInstruction("lw", new int[]{rs, rt, imm});
        lw.execute(config);

        // Assert
        assertEquals(valueToStore, config.getRegister(rt),
                "Register $3 should contain the value loaded from address $2 + 8.");
    }

    @Test
    void test_sw_with_0_imm() {
        // Setup
        int valueToStore = 42;
        int address = 200;
        int rs = 1; // base register
        int rt = 2; // value register
        int imm = 0;

        config.setRegister(rs, address); // $1 = 200
        config.setRegister(rt, valueToStore); // $2 = 42

        // Create and execute: sw $2, 0($1)
        Instruction sw = InstructionRegistry.getExecutableInstruction("sw", new int[]{rs, rt, imm});
        sw.execute(config);

        // Assert that memory at address 200 contains 42
        assertEquals(valueToStore, config.getWord(address),
                "Memory at address 200 should contain the value from register $2.");
    }

    @Test
    void test_sw_with_positive_imm() {
        // Setup
        int valueToStore = 55;
        int baseAddress = 300;
        int imm = 16;
        int rs = 4; // base register
        int rt = 5; // value register

        config.setRegister(rs, baseAddress); // $4 = 300
        config.setRegister(rt, valueToStore); // $5 = 55

        // Create and execute: sw $5, 16($4)
        Instruction sw = InstructionRegistry.getExecutableInstruction("sw", new int[]{rs, rt, imm});
        sw.execute(config);

        // Assert memory at 316 (300 + 16) holds 55
        int effectiveAddress = baseAddress + imm;
        assertEquals(valueToStore, config.getWord(effectiveAddress),
                "Memory at address 316 should contain the value from register $5.");
    }

    @Test
    void test_addi_with_positive_imm() {
        int rs = 1;
        int rt = 2;
        int base = 10;
        int imm = 5;

        config.setRegister(rs, base);

        Instruction addi = InstructionRegistry.getExecutableInstruction("addi", new int[]{rs, rt, imm});
        addi.execute(config);

        assertEquals(base + imm, config.getRegister(rt),
                "Register $2 should be $1 + imm (10 + 5 = 15)");
    }

    @Test
    void test_addi_with_negative_imm() {
        int rs = 1;
        int rt = 2;
        int base = 20;
        int imm = 0xFFFF; // -1 in 16-bit two's complement

        config.setRegister(rs, base);

        Instruction addi = InstructionRegistry.getExecutableInstruction("addi", new int[]{rs, rt, imm});
        addi.execute(config);

        int expected = base + ((short) imm); // convert to signed short
        assertEquals(expected, config.getRegister(rt),
                "Register $2 should be $1 + sign-extended imm (20 + -1 = 19)");
    }

    @Test
    void test_addiu_with_positive_imm() {
        int rs = 4;
        int rt = 5;
        int base = 20;
        int imm = 12;

        config.setRegister(rs, base);

        Instruction addiu = InstructionRegistry.getExecutableInstruction("addiu", new int[]{rs, rt, imm});
        addiu.execute(config);

        assertEquals(base + imm, config.getRegister(rt),
                "Register $5 should equal $4 + imm (20 + 12 = 32)");
    }

    @Test
    void test_addiu_with_negative_imm() {
        int rs = 4;
        int rt = 5;
        int base = 100;
        int imm = 0xFFFC; // -4 as 16-bit immediate

        config.setRegister(rs, base);

        Instruction addiu = InstructionRegistry.getExecutableInstruction("addiu", new int[]{rs, rt, imm});
        addiu.execute(config);

        int expected = base + (short) imm; // sign-extend properly
        assertEquals(expected, config.getRegister(rt),
                "Register $5 should equal $4 + sign-extended imm (100 + -4 = 96)");
    }

    @Test
    void test_slti_rs_less_than_imm() {
        int rs = 2;
        int rt = 3;
        config.setRegister(rs, 5);
        int imm = 10;

        Instruction slti = InstructionRegistry.getExecutableInstruction("slti", new int[]{rs, rt, imm});
        slti.execute(config);

        assertEquals(1, config.getRegister(rt), "Expected $3 to be 1 because 5 < 10");
    }

    @Test
    void test_slti_rs_equal_to_imm() {
        int rs = 2;
        int rt = 3;
        config.setRegister(rs, 10);
        int imm = 10;

        Instruction slti = InstructionRegistry.getExecutableInstruction("slti", new int[]{rs, rt, imm});
        slti.execute(config);

        assertEquals(0, config.getRegister(rt), "Expected $3 to be 0 because 10 is not less than 10");
    }

    @Test
    void test_slti_rs_greater_than_negative_imm() {
        int rs = 2;
        int rt = 3;
        config.setRegister(rs, 0);
        int imm = -1; // 0xFFFF

        Instruction slti = InstructionRegistry.getExecutableInstruction("slti", new int[]{rs, rt, imm});
        slti.execute(config);

        assertEquals(0, config.getRegister(rt), "Expected $3 to be 0 because 0 > -1");
    }

    @Test
    void test_slti_with_negative_rs_and_imm() {
        int rs = 2;
        int rt = 3;
        config.setRegister(rs, -10);
        int imm = -5;

        Instruction slti = InstructionRegistry.getExecutableInstruction("slti", new int[]{rs, rt, imm});
        slti.execute(config);

        assertEquals(1, config.getRegister(rt), "Expected $3 to be 1 because -10 < -5");
    }

    @Test
    void test_sltiu_rs_less_than_imm() {
        int rs = 1;
        int rt = 2;
        config.setRegister(rs, 5);
        int imm = 10;

        Instruction sltiu = InstructionRegistry.getExecutableInstruction("sltiu", new int[]{rs, rt, imm});
        sltiu.execute(config);

        assertEquals(1, config.getRegister(rt));
    }

    @Test
    void test_sltiu_rs_equal_to_imm() {
        int rs = 1;
        int rt = 2;
        config.setRegister(rs, 10);
        int imm = 10;

        Instruction sltiu = InstructionRegistry.getExecutableInstruction("sltiu", new int[]{rs, rt, imm});
        sltiu.execute(config);

        assertEquals(0, config.getRegister(rt));
    }

    @Test
    void test_sltiu_rs_greater_than_imm() {
        int rs = 1;
        int rt = 2;
        config.setRegister(rs, 20);
        int imm = 10;

        Instruction sltiu = InstructionRegistry.getExecutableInstruction("sltiu", new int[]{rs, rt, imm});
        sltiu.execute(config);

        assertEquals(0, config.getRegister(rt));
    }

    @Test
    void test_sltiu_negative_rs_and_small_imm() {
        int rs = 1;
        int rt = 2;
        config.setRegister(rs, -1); // 0xFFFFFFFF (unsigned: 4294967295)
        int imm = 10;

        Instruction sltiu = InstructionRegistry.getExecutableInstruction("sltiu", new int[]{rs, rt, imm});
        sltiu.execute(config);

        assertEquals(0, config.getRegister(rt));
    }

    @Test
    void test_sltiu_zero_rs_large_imm() {
        int rs = 1;
        int rt = 2;
        config.setRegister(rs, 0);
        int imm = 0xFFFF;

        Instruction sltiu = InstructionRegistry.getExecutableInstruction("sltiu", new int[]{rs, rt, imm});
        sltiu.execute(config);

        assertEquals(1, config.getRegister(rt));
    }

    @Test
    void test_andi_all_bits_set_and_0xFFFF() {
        int rs = 1;
        int rt = 2;
        int imm = 0xFFFF;

        config.setRegister(rs, 0xFFFFFFFF); // All bits set

        Instruction andi = InstructionRegistry.getExecutableInstruction("andi", new int[]{rs, rt, imm});
        andi.execute(config);

        assertEquals(0xFFFF, config.getRegister(rt));
    }

    @Test
    void test_andi_partial_bits() {
        int rs = 1;
        int rt = 2;
        int imm = 0x0F0F;

        config.setRegister(rs, 0xAAAA); // 0b1010101010101010

        Instruction andi = InstructionRegistry.getExecutableInstruction("andi", new int[]{rs, rt, imm});
        andi.execute(config);

        assertEquals(0x0A0A, config.getRegister(rt)); // 0b0000101000001010
    }

    @Test
    void test_andi_with_zero() {
        int rs = 1;
        int rt = 2;
        int imm = 0x1234;

        config.setRegister(rs, 0x0);

        Instruction andi = InstructionRegistry.getExecutableInstruction("andi", new int[]{rs, rt, imm});
        andi.execute(config);

        assertEquals(0x0, config.getRegister(rt));
    }

    @Test
    void test_andi_ignores_upper_16_bits() {
        int rs = 1;
        int rt = 2;
        int imm = 0xFFFF;

        config.setRegister(rs, 0x12345678);

        Instruction andi = InstructionRegistry.getExecutableInstruction("andi", new int[]{rs, rt, imm});
        andi.execute(config);

        assertEquals(0x5678, config.getRegister(rt)); // Only lower 16 bits are ANDed
    }

    @Test
    void test_andi_with_0x0000() {
        int rs = 1;
        int rt = 2;
        int imm = 0x0000;

        config.setRegister(rs, 0x12345678);

        Instruction andi = InstructionRegistry.getExecutableInstruction("andi", new int[]{rs, rt, imm});
        andi.execute(config);

        assertEquals(0x0, config.getRegister(rt));
    }

    @Test
    void test_ori_all_zeros_with_imm() {
        int rs = 1;
        int rt = 2;
        int imm = 0x1234;

        config.setRegister(rs, 0x00000000);

        Instruction ori = InstructionRegistry.getExecutableInstruction("ori", new int[]{rs, rt, imm});
        ori.execute(config);

        assertEquals(0x1234, config.getRegister(rt));
    }

    @Test
    void test_ori_all_ones_with_imm() {
        int rs = 1;
        int rt = 2;
        int imm = 0x1234;

        config.setRegister(rs, 0xFFFFFFFF);

        Instruction ori = InstructionRegistry.getExecutableInstruction("ori", new int[]{rs, rt, imm});
        ori.execute(config);

        assertEquals(0xFFFFFFFF, config.getRegister(rt));
    }

    @Test
    void test_ori_partial_bits() {
        int rs = 1;
        int rt = 2;
        int imm = 0x00F0;

        config.setRegister(rs, 0x0F00); // 0000111100000000

        Instruction ori = InstructionRegistry.getExecutableInstruction("ori", new int[]{rs, rt, imm});
        ori.execute(config);

        assertEquals(0x0FF0, config.getRegister(rt)); // 0000111111110000
    }

    @Test
    void test_ori_with_zero_imm() {
        int rs = 1;
        int rt = 2;
        int imm = 0x0000;

        config.setRegister(rs, 0xABCD1234);

        Instruction ori = InstructionRegistry.getExecutableInstruction("ori", new int[]{rs, rt, imm});
        ori.execute(config);

        assertEquals(0xABCD1234, config.getRegister(rt));
    }

    @Test
    void test_ori_ignores_upper_16_bits() {
        int rs = 1;
        int rt = 2;
        int imm = 0x00FF;

        config.setRegister(rs, 0x12340000);

        Instruction ori = InstructionRegistry.getExecutableInstruction("ori", new int[]{rs, rt, imm});
        ori.execute(config);

        assertEquals(0x123400FF, config.getRegister(rt));
    }

    @Test
    void test_xori_all_zeros_with_imm() {
        int rs = 1;
        int rt = 2;
        int imm = 0xFFFF;

        config.setRegister(rs, 0x00000000);

        Instruction xori = InstructionRegistry.getExecutableInstruction("xori", new int[]{rs, rt, imm});
        xori.execute(config);

        assertEquals(0xFFFF, config.getRegister(rt));
    }

    @Test
    void test_xori_same_value_results_zero() {
        int rs = 1;
        int rt = 2;
        int imm = 0x00FF;

        config.setRegister(rs, 0x00FF);

        Instruction xori = InstructionRegistry.getExecutableInstruction("xori", new int[]{rs, rt, imm});
        xori.execute(config);

        assertEquals(0x0000, config.getRegister(rt));
    }

    @Test
    void test_xori_partial_flip() {
        int rs = 1;
        int rt = 2;
        int imm = 0x0F0F;

        config.setRegister(rs, 0x00FF);

        Instruction xori = InstructionRegistry.getExecutableInstruction("xori", new int[]{rs, rt, imm});
        xori.execute(config);

        assertEquals(0xFF0, config.getRegister(rt));  // 00FF ^ 0F0F = 00F0
    }

    @Test
    void test_xori_with_zero_imm() {
        int rs = 1;
        int rt = 2;
        int imm = 0x0000;

        config.setRegister(rs, 0x12345678);

        Instruction xori = InstructionRegistry.getExecutableInstruction("xori", new int[]{rs, rt, imm});
        xori.execute(config);

        assertEquals(0x12345678, config.getRegister(rt));
    }

    @Test
    void test_xori_upper_bits_preserved() {
        int rs = 1;
        int rt = 2;
        int imm = 0xFFFF;

        config.setRegister(rs, 0x12340000);

        Instruction xori = InstructionRegistry.getExecutableInstruction("xori", new int[]{rs, rt, imm});
        xori.execute(config);

        assertEquals(0x1234FFFF, config.getRegister(rt));
    }

    @Test
    void test_lui_zero_imm() {
        int rt = 2;
        int imm = 0x0000;

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{rt, imm});
        lui.execute(config);

        assertEquals(0x00000000, config.getRegister(rt), "Register should be 0 when immediate is 0.");
    }

    @Test
    void test_lui_all_ones_imm() {
        int rt = 2;
        int imm = 0xFFFF;

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{rt, imm});
        lui.execute(config);

        assertEquals(0xFFFF0000, config.getRegister(rt), "Register should have imm in upper 16 bits.");
    }

    @Test
    void test_lui_with_positive_imm() {
        int rt = 2;
        int imm = 0x1234;

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{rt, imm});
        lui.execute(config);

        assertEquals(0x12340000, config.getRegister(rt), "Immediate should be shifted to upper 16 bits.");
    }

    @Test
    void test_lui_does_not_affect_other_bits() {
        int rt = 2;
        int imm = 0x00FF;

        config.setRegister(rt, 0xFFFFFFFF); // ensure value gets overwritten

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{rt, imm});
        lui.execute(config);

        assertEquals(0x00FF0000, config.getRegister(rt), "Register should only contain shifted immediate.");
    }

    @Test
    void test_lui_does_not_extend_sign() {
        int rt = 2;
        int imm = 0x8000;

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{rt, imm});
        lui.execute(config);

        assertEquals(0x80000000, config.getRegister(rt), "Should shift immediate without sign extension.");
    }
}
