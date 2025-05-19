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
}
