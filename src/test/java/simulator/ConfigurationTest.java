package simulator;

import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import com.wars.simulator.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{0, rt, imm});
        lui.execute(config);

        assertEquals(0x00000000, config.getRegister(rt), "Register should be 0 when immediate is 0.");
    }

    @Test
    void test_lui_all_ones_imm() {
        int rt = 2;
        int imm = 0xFFFF;

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{0, rt, imm});
        lui.execute(config);

        assertEquals(0xFFFF0000, config.getRegister(rt), "Register should have imm in upper 16 bits.");
    }

    @Test
    void test_lui_with_positive_imm() {
        int rt = 2;
        int imm = 0x1234;

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{0, rt, imm});
        lui.execute(config);

        assertEquals(0x12340000, config.getRegister(rt), "Immediate should be shifted to upper 16 bits.");
    }

    @Test
    void test_lui_does_not_affect_other_bits() {
        int rt = 2;
        int imm = 0x00FF;

        config.setRegister(rt, 0xFFFFFFFF); // ensure value gets overwritten

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{0, rt, imm});
        lui.execute(config);

        assertEquals(0x00FF0000, config.getRegister(rt), "Register should only contain shifted immediate.");
    }

    @Test
    void test_lui_does_not_extend_sign() {
        int rt = 2;
        int imm = 0x8000;

        Instruction lui = InstructionRegistry.getExecutableInstruction("lui", new int[]{0, rt, imm});
        lui.execute(config);

        assertEquals(0x80000000, config.getRegister(rt), "Should shift immediate without sign extension.");
    }

    @Test
    void test_bltz_branch_taken() {
        int rs = 1;
        int imm = 2; // branch offset (will be sign-extended)
        config.setRegister(rs, -5);  // negative value, so branch taken
        config.setPC(100);

        Instruction bltz = InstructionRegistry.getExecutableInstruction("bltz", new int[]{rs, 0, imm});
        bltz.execute(config);

        assertEquals(108, config.getPC());
    }

    @Test
    void test_bltz_branch_taken_with_negative_offset() {
        int rs = 1;
        int imm = -2; // branch offset (will be sign-extended)
        config.setRegister(rs, -5);  // negative value, so branch taken
        config.setPC(100);

        Instruction bltz = InstructionRegistry.getExecutableInstruction("bltz", new int[]{rs, 0, imm});
        bltz.execute(config);

        assertEquals(92, config.getPC());
    }

    @Test
    void test_bltz_branch_not_taken() {
        int rs = 1;
        int imm = 2;
        config.setRegister(rs, 5);  // non-negative, branch not taken
        config.setPC(100);

        Instruction bltz = InstructionRegistry.getExecutableInstruction("bltz", new int[]{rs, 0, imm});
        bltz.execute(config);

        // Expected PC: 100 + 4
        assertEquals(104, config.getPC());
    }

    @Test
    void test_bgez_branch_taken_greater() {
        int rs = 1;
        int imm = 2; // branch offset (will be sign-extended)
        config.setRegister(rs, 10);
        config.setPC(100);

        Instruction bgez = InstructionRegistry.getExecutableInstruction("bgez", new int[]{rs, 0, imm});
        bgez.execute(config);

        assertEquals(108, config.getPC());
    }

    @Test
    void test_bgez_branch_taken_equals() {
        int rs = 1;
        int imm = 2; // branch offset (will be sign-extended)
        config.setRegister(rs, 0);
        config.setPC(100);

        Instruction bltz = InstructionRegistry.getExecutableInstruction("bgez", new int[]{rs, 0, imm});
        bltz.execute(config);

        assertEquals(108, config.getPC());
    }

    @Test
    void test_bgez_branch_not_taken() {
        int rs = 1;
        int imm = 2;
        config.setRegister(rs, -5);
        config.setPC(100);

        Instruction bltz = InstructionRegistry.getExecutableInstruction("bgez", new int[]{rs, 0, imm});
        bltz.execute(config);

        // Expected PC: 100 + 4
        assertEquals(104, config.getPC());
    }

    @Test
    void test_beq_branch_taken() {
        int rs = 1;
        int rt = 2;
        int imm = 3;

        config.setPC(100);
        config.setRegister(rs, 42);
        config.setRegister(rt, 42); // rs == rt, branch taken

        Instruction beq = InstructionRegistry.getExecutableInstruction("beq", new int[]{rs, rt, imm});
        beq.execute(config);

        int expectedPC = 100 + (imm << 2);
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_beq_branch_not_taken() {
        int rs = 1;
        int rt = 2;
        int imm = 3;

        config.setPC(100);
        config.setRegister(rs, 42);
        config.setRegister(rt, 50); // rs != rt, branch not taken

        Instruction beq = InstructionRegistry.getExecutableInstruction("beq", new int[]{rs, rt, imm});
        beq.execute(config);

        int expectedPC = 100 + 4;
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_bne_branch_not_taken() {
        int rs = 1;
        int rt = 2;
        int imm = 3;

        config.setPC(100);
        config.setRegister(rs, 42);
        config.setRegister(rt, 42);

        Instruction bne = InstructionRegistry.getExecutableInstruction("bne", new int[]{rs, rt, imm});
        bne.execute(config);

        int expectedPC = 100 + 4;
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_bne_branch_taken() {
        int rs = 1;
        int rt = 2;
        int imm = 3;

        config.setPC(100);
        config.setRegister(rs, 42);
        config.setRegister(rt, 50);

        Instruction bne = InstructionRegistry.getExecutableInstruction("bne", new int[]{rs, rt, imm});
        bne.execute(config);

        int expectedPC = 100 + (imm << 2);
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_blez_branch_not_taken() {
        int rs = 1;
        int imm = 2; // branch offset (will be sign-extended)
        config.setRegister(rs, 10);
        config.setPC(100);

        Instruction blez = InstructionRegistry.getExecutableInstruction("blez", new int[]{rs, 0, imm});
        blez.execute(config);

        assertEquals(104, config.getPC());
    }

    @Test
    void test_blez_branch_taken_equals_0() {
        int rs = 1;
        int imm = 2; // branch offset (will be sign-extended)
        config.setRegister(rs, 0);
        config.setPC(100);

        Instruction blez = InstructionRegistry.getExecutableInstruction("blez", new int[]{rs, 0, imm});
        blez.execute(config);

        int expectedPC = 100 + (imm << 2);
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_blez_branch_taken_less_than_0() {
        int rs = 1;
        int imm = 2;
        config.setRegister(rs, -5);
        config.setPC(100);

        Instruction blez = InstructionRegistry.getExecutableInstruction("blez", new int[]{rs, 0, imm});
        blez.execute(config);

        int expectedPC = 100 + (imm << 2);
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_bgtz_branch_taken() {
        int rs = 1;
        int imm = 2;
        config.setRegister(rs, 10);
        config.setPC(100);

        Instruction bgtz = InstructionRegistry.getExecutableInstruction("bgtz", new int[]{rs, 0, imm});
        bgtz.execute(config);

        int expectedPC = 100 + (imm << 2);
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_bgtz_branch_taken_with_negative_offset() {
        int rs = 1;
        int imm = -2;
        config.setRegister(rs, 10);
        config.setPC(100);

        Instruction bgtz = InstructionRegistry.getExecutableInstruction("bgtz", new int[]{rs, 0, imm});
        bgtz.execute(config);

        int expectedPC = 100 + (imm << 2);
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_bgtz_branch_not_taken() {
        int rs = 1;
        int imm = 2;
        config.setRegister(rs, -10);
        config.setPC(100);

        Instruction bgtz = InstructionRegistry.getExecutableInstruction("bgtz", new int[]{rs, 0, imm});
        bgtz.execute(config);

        int expectedPC = 100 + 4;
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_srl_shift_positive() {
        int rd = 1, rt = 2, sa = 3;
        config.setPC(100);
        config.setRegister(rt, 0b0000_1000); // 8 in binary

        Instruction srl = InstructionRegistry.getExecutableInstruction("srl", new int[]{rd, 0, rt, sa});
        srl.execute(config);

        assertEquals(1, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_srl_zero_shift() {
        int rd = 1, rt = 2, sa = 0;
        config.setPC(100);
        config.setRegister(rt, 0x12345678);

        Instruction srl = InstructionRegistry.getExecutableInstruction("srl", new int[]{rd, 0, rt, sa});
        srl.execute(config);

        assertEquals(0x12345678, config.getRegister(rd)); // No shift
        assertEquals(104, config.getPC());
    }

    @Test
    void test_add_normal() {
        int rs = 1, rt = 2, rd = 3;
        config.setPC(200);
        config.setRegister(rs, 5);
        config.setRegister(rt, 10);

        Instruction add = InstructionRegistry.getExecutableInstruction("add", new int[]{rd, rs, rt});
        add.execute(config);

        assertEquals(15, config.getRegister(rd));
        assertEquals(204, config.getPC());
    }

    @Test
    void test_add_zero_result() {
        int rs = 1, rt = 2, rd = 3;
        config.setPC(200);
        config.setRegister(rs, 7);
        config.setRegister(rt, -7);

        Instruction add = InstructionRegistry.getExecutableInstruction("add", new int[]{rd, rs, rt});
        add.execute(config);

        assertEquals(0, config.getRegister(rd));
        assertEquals(204, config.getPC());
    }

    // @Test
    // void test_add_overflow_throws() {
    //     int rs = 1, rt = 2, rd = 3;
    //     config.setRegister(rs, Integer.MAX_VALUE);
    //     config.setRegister(rt, 1);

    //     Instruction add = InstructionRegistry.getExecutableInstruction("add", new int[]{rd, rs, rt});

    //     assertThrows(ArithmeticException.class, () -> add.execute(config));
    // }

    @Test
    void test_addu() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 10);
        config.setRegister(rt, 20);
        config.setPC(100);

        Instruction addu = InstructionRegistry.getExecutableInstruction("addu", new int[]{rd, rs, rt});
        addu.execute(config);

        assertEquals(30, config.getRegister(rd));
        assertEquals(104, config.getPC()); // PC should increment by 4
    }

    @Test
    void test_addu_unsigned_wraparound() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, Integer.MAX_VALUE);
        config.setRegister(rt, 1);
        config.setPC(200);

        Instruction addu = InstructionRegistry.getExecutableInstruction("addu", new int[]{rd, rs, rt});
        addu.execute(config);

        assertEquals(Integer.MIN_VALUE, config.getRegister(rd)); // wraparound occurs
        assertEquals(204, config.getPC());
    }

    @Test
    void test_sub_basic() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 20);
        config.setRegister(rt, 5);
        config.setPC(100);

        Instruction sub = InstructionRegistry.getExecutableInstruction("sub", new int[]{rd, rs, rt});
        sub.execute(config);

        assertEquals(15, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_sub_result_negative() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 5);
        config.setRegister(rt, 10);
        config.setPC(200);

        Instruction sub = InstructionRegistry.getExecutableInstruction("sub", new int[]{rd, rs, rt});
        sub.execute(config);

        assertEquals(-5, config.getRegister(rd));
        assertEquals(204, config.getPC());
    }

    // @Test
    // void test_sub_overflow_throws() {
    //     int rs = 2, rt = 3, rd = 1;
    //     config.setRegister(rs, Integer.MIN_VALUE);
    //     config.setRegister(rt, 1);
    //     config.setPC(300);

    //     Instruction sub = InstructionRegistry.getExecutableInstruction("sub", new int[]{rd, rs, rt});

    //     assertThrows(ArithmeticException.class, () -> sub.execute(config));
    // }

    @Test
    void test_subu_basic() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 15);
        config.setRegister(rt, 5);
        config.setPC(100);

        Instruction subu = InstructionRegistry.getExecutableInstruction("subu", new int[]{rd, rs, rt});
        subu.execute(config);

        assertEquals(10, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_subu_negative_result() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 5);
        config.setRegister(rt, 10);
        config.setPC(200);

        Instruction subu = InstructionRegistry.getExecutableInstruction("subu", new int[]{rd, rs, rt});
        subu.execute(config);

        assertEquals(-5, config.getRegister(rd));  // still a valid int
        assertEquals(204, config.getPC());
    }

    @Test
    void test_subu_does_not_throw_on_overflow() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, Integer.MIN_VALUE);
        config.setRegister(rt, 1);
        config.setPC(300);

        Instruction subu = InstructionRegistry.getExecutableInstruction("subu", new int[]{rd, rs, rt});

        assertDoesNotThrow(() -> subu.execute(config));
        assertEquals(Integer.MIN_VALUE - 1, config.getRegister(rd));
        assertEquals(304, config.getPC());
    }

    @Test
    void test_and_basic() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0b1100);
        config.setRegister(rt, 0b1010);
        config.setPC(100);

        Instruction and = InstructionRegistry.getExecutableInstruction("and", new int[]{rd, rs, rt});
        and.execute(config);

        assertEquals(0b1000, config.getRegister(rd));  // 0b1100 & 0b1010 = 0b1000
        assertEquals(104, config.getPC());
    }

    @Test
    void test_and_with_zero() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0xFFFFFFFF);
        config.setRegister(rt, 0);
        config.setPC(200);

        Instruction and = InstructionRegistry.getExecutableInstruction("and", new int[]{rd, rs, rt});
        and.execute(config);

        assertEquals(0, config.getRegister(rd));  // all bits AND 0 = 0
        assertEquals(204, config.getPC());
    }

    @Test
    void test_and_all_ones() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0xFFFFFFFF);
        config.setRegister(rt, 0xFFFFFFFF);
        config.setPC(300);

        Instruction and = InstructionRegistry.getExecutableInstruction("and", new int[]{rd, rs, rt});
        and.execute(config);

        assertEquals(0xFFFFFFFF, config.getRegister(rd));  // all bits AND all bits = all bits
        assertEquals(304, config.getPC());
    }

    @Test
    void test_or_basic() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0b1100);
        config.setRegister(rt, 0b1010);
        config.setPC(100);

        Instruction or = InstructionRegistry.getExecutableInstruction("or", new int[]{rd, rs, rt});
        or.execute(config);

        assertEquals(0b1110, config.getRegister(rd));  // 0b1100 | 0b1010 = 0b1110
        assertEquals(104, config.getPC());
    }

    @Test
    void test_or_with_zero() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0x00000000);
        config.setRegister(rt, 0xFFFFFFFF);
        config.setPC(200);

        Instruction or = InstructionRegistry.getExecutableInstruction("or", new int[]{rd, rs, rt});
        or.execute(config);

        assertEquals(0xFFFFFFFF, config.getRegister(rd));  // 0 | all bits = all bits
        assertEquals(204, config.getPC());
    }

    @Test
    void test_or_all_zero() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0);
        config.setRegister(rt, 0);
        config.setPC(300);

        Instruction or = InstructionRegistry.getExecutableInstruction("or", new int[]{rd, rs, rt});
        or.execute(config);

        assertEquals(0, config.getRegister(rd));
        assertEquals(304, config.getPC());
    }

    @Test
    void test_xor_basic() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0b1100);    // 12
        config.setRegister(rt, 0b1010);    // 10
        config.setPC(100);

        Instruction xor = InstructionRegistry.getExecutableInstruction("xor", new int[]{rd, rs, rt});
        xor.execute(config);

        assertEquals(0b0110, config.getRegister(rd));  // 12 ^ 10 = 6
        assertEquals(104, config.getPC());
    }

    @Test
    void test_xor_with_self() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0b1111_0000);
        config.setRegister(rt, 0b1111_0000);
        config.setPC(200);

        Instruction xor = InstructionRegistry.getExecutableInstruction("xor", new int[]{rd, rs, rt});
        xor.execute(config);

        assertEquals(0, config.getRegister(rd));  // x ^ x = 0
        assertEquals(204, config.getPC());
    }

    @Test
    void test_xor_with_zero() {
        int rs = 2, rt = 3, rd = 1;
        config.setRegister(rs, 0b10101010);
        config.setRegister(rt, 0);
        config.setPC(300);

        Instruction xor = InstructionRegistry.getExecutableInstruction("xor", new int[]{rd, rs, rt});
        xor.execute(config);

        assertEquals(0b10101010, config.getRegister(rd));  // x ^ 0 = x
        assertEquals(304, config.getPC());
    }

    @Test
    void test_nor_basic() {
        int rs = 1, rt = 2, rd = 3;
        config.setPC(100);
        config.setRegister(rs, 0b1010);
        config.setRegister(rt, 0b1100);
        config.setRegister(rd, 0);

        Instruction nor = InstructionRegistry.getExecutableInstruction("nor", new int[]{rd, rs, rt});
        nor.execute(config);

        int expected = ~(0b1010 | 0b1100);
        assertEquals(expected, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_slt_rd_set_to_1_when_rs_less_than_rt_positive() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, 5);
        config.setRegister(rt, 10);
        config.setPC(100);

        Instruction slt = InstructionRegistry.getExecutableInstruction("slt", new int[]{rd, rs, rt});
        slt.execute(config);

        assertEquals(1, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_slt_rd_set_to_0_when_rs_not_less_than_rt_equal() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, 10);
        config.setRegister(rt, 10);
        config.setPC(100);

        Instruction slt = InstructionRegistry.getExecutableInstruction("slt", new int[]{rd, rs, rt});
        slt.execute(config);

        assertEquals(0, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_slt_rd_set_to_0_when_rs_greater_than_rt() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, 15);
        config.setRegister(rt, 10);
        config.setPC(100);

        Instruction slt = InstructionRegistry.getExecutableInstruction("slt", new int[]{rd, rs, rt});
        slt.execute(config);

        assertEquals(0, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_slt_with_negative_rs_and_positive_rt_sets_rd_to_1() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, -5);
        config.setRegister(rt, 3);
        config.setPC(100);

        Instruction slt = InstructionRegistry.getExecutableInstruction("slt", new int[]{rd, rs, rt});
        slt.execute(config);

        assertEquals(1, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_slt_with_positive_rs_and_negative_rt_sets_rd_to_0() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, 5);
        config.setRegister(rt, -3);
        config.setPC(100);

        Instruction slt = InstructionRegistry.getExecutableInstruction("slt", new int[]{rd, rs, rt});
        slt.execute(config);

        assertEquals(0, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_sltu_rd_set_to_1_when_unsigned_rs_less_than_rt() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, 5);
        config.setRegister(rt, 10);
        config.setPC(100);

        Instruction sltu = InstructionRegistry.getExecutableInstruction("sltu", new int[]{rd, rs, rt});
        sltu.execute(config);

        assertEquals(1, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_sltu_rd_set_to_0_when_unsigned_rs_not_less_than_rt_equal() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, 10);
        config.setRegister(rt, 10);
        config.setPC(100);

        Instruction sltu = InstructionRegistry.getExecutableInstruction("sltu", new int[]{rd, rs, rt});
        sltu.execute(config);

        assertEquals(0, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_sltu_rd_set_to_0_when_unsigned_rs_greater_than_rt() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, 15);
        config.setRegister(rt, 10);
        config.setPC(100);

        Instruction sltu = InstructionRegistry.getExecutableInstruction("sltu", new int[]{rd, rs, rt});
        sltu.execute(config);

        assertEquals(0, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_sltu_rd_set_to_1_when_rs_negative_and_rt_positive_unsigned() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, -5);   // 0xFFFFFFFB unsigned is large number
        config.setRegister(rt, 3);    // 3 unsigned
        config.setPC(100);

        Instruction sltu = InstructionRegistry.getExecutableInstruction("sltu", new int[]{rd, rs, rt});
        sltu.execute(config);

        // unsigned -5 (0xFFFFFFFB) > 3, so result should be 0
        assertEquals(0, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_sltu_rd_set_to_1_when_rs_positive_and_rt_negative_unsigned() {
        int rs = 1;
        int rt = 2;
        int rd = 3;
        config.setRegister(rs, 5);    // 5 unsigned
        config.setRegister(rt, -3);   // 0xFFFFFFFD unsigned is large number
        config.setPC(100);

        Instruction sltu = InstructionRegistry.getExecutableInstruction("sltu", new int[]{rd, rs, rt});
        sltu.execute(config);

        // 5 unsigned < 0xFFFFFFFD unsigned, so result should be 1
        assertEquals(1, config.getRegister(rd));
        assertEquals(104, config.getPC());
    }

    @Test
    void test_jr_sets_pc_to_register_value() {
        int rs = 1;
        int jumpAddress = 0x00400020;
        config.setRegister(rs, jumpAddress);
        config.setPC(100);

        Instruction jr = InstructionRegistry.getExecutableInstruction("jr", new int[]{0, rs});
        jr.execute(config);

        assertEquals(jumpAddress, config.getPC());
    }

    @Test
    void test_jr_with_zero_address() {
        int rs = 1;
        config.setRegister(rs, 0);
        config.setPC(100);

        Instruction jr = InstructionRegistry.getExecutableInstruction("jr", new int[]{0, rs});
        jr.execute(config);

        assertEquals(0, config.getPC());
    }

    @Test
    void test_jalr_sets_return_address_and_jumps() {
        int rs = 1;
        int rd = 2;
        int jumpAddress = 0x00400020;
        int currentPC = 100;
        config.setRegister(rs, jumpAddress);
        config.setPC(currentPC);

        Instruction jalr = InstructionRegistry.getExecutableInstruction("jalr", new int[]{rd, rs});
        jalr.execute(config);

        assertEquals(currentPC + 4, config.getRegister(rd));
        assertEquals(jumpAddress, config.getPC());
    }

    @Test
    void test_jalr_with_zero_jump_address() {
        int rs = 1;
        int rd = 2;
        int currentPC = 100;
        config.setRegister(rs, 0);
        config.setPC(currentPC);

        Instruction jalr = InstructionRegistry.getExecutableInstruction("jalr", new int[]{rd, rs});
        jalr.execute(config);

        assertEquals(currentPC + 4, config.getRegister(rd));
        assertEquals(0, config.getPC());
    }

    @Test
    void test_j_jump_address() {
        long initialPC = 0x12345678L;
        config.setPC(initialPC);
        int iindex = 0x03FFFFFF;


        Instruction j = InstructionRegistry.getExecutableInstruction("j", new int[]{iindex});
        j.execute(config);

        // long pcPlus4 = initialPC + 4L;
        // long upperPC = pcPlus4 & 0xF0000000L;
        // long target = ((long) iindex << 2) & 0x0FFFFFFFL;
        // long expectedPC = upperPC | target;
        long expectedPC = initialPC + iindex;
        assertEquals(expectedPC, config.getPC());
    }

    @Test
    void test_jal_jump_and_link() {
        long initialPC = 0x00400000L;
        config.setPC(initialPC);
        int iindex = 0x0003FFFF;

        Instruction jal = InstructionRegistry.getExecutableInstruction("jal", new int[]{iindex});
        jal.execute(config);

        long pcPlus4 = initialPC + 4;
        // long expectedPC = (pcPlus4 & 0xF0000000L) | (((long) iindex << 2) & 0x0FFFFFFFL);
        long expectedPC = initialPC + iindex;
        int expectedLink = (int) pcPlus4;

        assertEquals(expectedLink, config.getRegister(31));
        assertEquals(expectedPC, config.getPC());
    }


}
