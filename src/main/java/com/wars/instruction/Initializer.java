package com.wars.instruction;

import com.wars.exception.SimulatorException;
import com.wars.operand.OperandType;
import com.wars.simulator.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wars.operand.OperandType.*;

class Initializer {

    private static final Map<String, InstructionCreator> instructionCreatorMap = new HashMap<>();
    private static final Map<String, InstructionExecutor> instructionExecutorMap = new HashMap<>();
    private static final Map<String, List<OperandType>> operandTypesMap = new HashMap<>();
    private static final Map<Integer, String> opcodeMap = new HashMap<>();

    static {
        // Registering I-Type instructions
        lw();
        sw();
        addi();
        addiu();
        slti();
        sltiu();
        andi();
        ori();
        xori();
        lui();
        bltz();
        bgez();
        beq();
        bne();
        blez();
        bgtz();

        // Registering R-Type instructions
        srl();
        add();
        addu();
        sub();
        subu();
        and();
        or();
        xor();
        nor();
        slt();
        sltu();
        jr();
        jalr();
        sysc();
        eret();
        movg2s();
        movs2g();

        // Registering J-Type instructions
        j();
        jal();
    }

    static Map<String, InstructionCreator> initializeCreatorMap() {
        return instructionCreatorMap;
    }

    static Map<String, InstructionExecutor> initializeExecutorMap() {
        return instructionExecutorMap;
    }

    static Map<String, List<OperandType>> initializeOperandTypesMap() {
        return operandTypesMap;
    }

    static Map<Integer, String> initializeOpcodeMap() {
        return opcodeMap;
    }

    private static void register(String mnemonic,
                                 int opcode,
                                 List<OperandType> operandTypes,
                                 InstructionCreator creator,
                                 InstructionExecutor executor) {
        instructionCreatorMap.put(mnemonic, creator);
        instructionExecutorMap.put(mnemonic, executor);
        operandTypesMap.put(mnemonic, operandTypes);
        opcodeMap.put(opcode, mnemonic);
    }


    private static void register(String mnemonic, int opcode, List<OperandType> operandTypes, InstructionCreator creator) {
        instructionCreatorMap.put(mnemonic, creator);
        operandTypesMap.put(mnemonic, operandTypes);
        opcodeMap.put(opcode, mnemonic);
    }

    private static void lw() {
        // lw rt rs imm
        int opcode = 0b100011;
        register("lw", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = m
                            int address = c.getRegister(rs) + imm;

                            if (address % 4 != 0) {
                                throw new SimulatorException("Unaligned memory access at address: " + address);
                            }

                            int value = c.getWord(address);
                            c.setRegister(rt, value);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void sw() {
        // sw rt rs imm
        int opcode = 0b101011;
        register("sw", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // m = rt
                            int address = c.getRegister(rs) + imm;

                            if (address % 4 != 0) {
                                throw new SimulatorException("Unaligned memory access at address: " + address);
                            }

                            int value = c.getRegister(rt);
                            c.setWord(address, value);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void addi() {
        // addi rt rs imm
        int opcode = 0b001000;
        register("addi", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = rs + sxt(imm)
                            int extendedImm = (short) imm;
                            int result = c.getRegister(rs) + extendedImm;
                            // TODO overflow check + test
                            c.setRegister(rt, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void addiu() {
        // addiu rt rs imm
        int opcode = 0b001001;
        register("addiu", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = rs + sxt(imm)
                            int signedImm = (short) imm;
                            int result = c.getRegister(rs) + signedImm;
                            c.setRegister(rt, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void slti() {
        // slti rt rs imm
        int opcode = 0b001010;
        register("slti", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = (rs < sxt(imm)) ? 1 : 0
                            int signedImm = (short) imm;
                            int rsValue = c.getRegister(rs);
                            int result = (rsValue < signedImm) ? 1 : 0;
                            c.setRegister(rt, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void sltiu() {
        // sltiu rt rs imm
        int opcode = 0b001011;
        register("sltiu", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = (rs < imm) ? 1 : 0
                            int rsVal = c.getRegister(rs);
                            int immUnsigned = imm & 0xFFFF; // Zero-extend to 32 bits
                            long rsUnsigned = Integer.toUnsignedLong(rsVal);

                            int result = (rsUnsigned < (long) immUnsigned) ? 1 : 0;
                            c.setRegister(rt, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void andi() {
        // andi rt rs imm
        int opcode = 0b001100;
        register("andi", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = rs & imm
                            int result = c.getRegister(rs) & imm;
                            c.setRegister(rt, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void ori() {
        // ori rt rs imm
        int opcode = 0b001101;
        register("ori", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = rs | imm
                            int result = c.getRegister(rs) | imm;
                            c.setRegister(rt, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void xori() {
        // xori rt rs imm
        int opcode = 0b001110;
        register("xori", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[1], operands[0], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = rs ^ imm
                            int result = c.getRegister(rs) ^ imm;
                            c.setRegister(rt, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void lui() {
        // lui rt imm
        int opcode = 0b001111;
        register("lui", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, 0b000000, operands[0], operands[1]),
                operands -> {
                    int rt = operands[0], imm = operands[1];
                    return new ITypeInstruction(opcode, 0b000000, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // rt = imm0^16
                            int result = (imm & 0xFFFF) << 16;
                            c.setRegister(rt, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void bltz() {
        // bltz rs imm
        int opcode = 0b000001;
        register("bltz", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], 0b00000, operands[1]),
                operands -> {
                    int rs = operands[0], imm = operands[1];
                    return new ITypeInstruction(opcode, rs, 0b00000, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // pc = pc + (rs < 0 ? imm00 : 4)
                            int rsVal = c.getRegister(rs);
                            int signedImm = (short) imm;
                            int offSet = rsVal < 0 ? (signedImm << 2) : 4;
                            c.setPC(c.getPC() + offSet);
                        }
                    };
                });
    }

    private static void bgez() {
        // bgez rs imm
        int opcode = 0b000001;
        register("bgez", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], 0b00001, operands[1]),
                operands -> {
                    int rs = operands[0], imm = operands[1];
                    return new ITypeInstruction(opcode, rs, 0b00000, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // pc = pc + (rs >= 0 ? imm00 : 4)
                            int rsVal = c.getRegister(rs);
                            int signedImm = (short) imm;
                            int offSet = rsVal >= 0 ? (signedImm << 2) : 4;
                            c.setPC(c.getPC() + offSet);
                        }
                    };
                });
    }

    private static void beq() {
        // beq rs rt imm
        int opcode = 0b000100;
        register("beq", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], operands[1], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // pc = pc + (rs = rt ? imm00 : 4)
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int signedImm = (short) imm;
                            int offSet = rsVal == rtVal ? (signedImm << 2) : 4;
                            c.setPC(c.getPC() + offSet);
                        }
                    };
                });
    }

    private static void bne() {
        // bne rs rt imm
        int opcode = 0b000101;
        register("bne", opcode, List.of(REG5, REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], operands[1], operands[2]),
                operands -> {
                    int rs = operands[0], rt = operands[1], imm = operands[2];
                    return new ITypeInstruction(opcode, rs, rt, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // pc = pc + (rs != rt ? imm00 : 4)
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int signedImm = (short) imm;
                            int offSet = rsVal != rtVal ? (signedImm << 2) : 4;
                            c.setPC(c.getPC() + offSet);
                        }
                    };
                });
    }

    private static void blez() {
        // blez rs imm
        int opcode = 0b000110;
        register("blez", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], 0b00000, operands[1]),
                operands -> {
                    int rs = operands[0], imm = operands[1];
                    return new ITypeInstruction(opcode, rs, 0b00000, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // pc = pc + (rs <= 0 ? imm00 : 4)
                            int rsVal = c.getRegister(rs);
                            int signedImm = (short) imm;
                            int offSet = rsVal <= 0 ? (signedImm << 2) : 4;
                            c.setPC(c.getPC() + offSet);
                        }
                    };
                });
    }

    private static void bgtz() {
        // bgtz rs imm
        int opcode = 0b000111;
        register("bgtz", opcode, List.of(REG5, IMM16),
                operands -> new ITypeInstruction(opcode, operands[0], 0b00000, operands[1]),
                operands -> {
                    int rs = operands[0], imm = operands[1];
                    return new ITypeInstruction(opcode, rs, 0b00000, imm) {
                        @Override
                        public void execute(Configuration c) {
                            // pc = pc + (rs > 0 ? imm00 : 4)
                            int rsVal = c.getRegister(rs);
                            int signedImm = (short) imm;
                            int offSet = rsVal > 0 ? (signedImm << 2) : 4;
                            c.setPC(c.getPC() + offSet);
                        }
                    };
                });
    }

    private static void srl() {
        // srl rd rt sa
        int opcode = 0b000000;
        int fun = 0b000010;
        register("srl", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, 0b00000, operands[1], operands[0], operands[2], fun),
                operands -> {
                    int rt = operands[1], rd = operands[0], sa = operands[2];
                    return new RTypeInstruction(opcode, 0b00000, rt, rd, sa, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = srl(rt, sa)
                            int rtVal = c.getRegister(rt);
                            int result = rtVal >>> sa;
                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void add() {
        // add rd rs rt
        int opcode = 0b000000;
        int fun = 0b100000;
        register("add", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = rs + rt
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = rsVal + rtVal;

                            if (((rsVal ^ result) & (rtVal ^ result)) < 0) {
                                throw new ArithmeticException("Integer overflow in add");
                            }

                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void addu() {
        // addu rd rs rt
        int opcode = 0b000000;
        int fun = 0b100001;
        register("addu", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = rs + rt
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = rsVal + rtVal;
                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void sub() {
        // sub rd rs rt
        int opcode = 0b000000;
        int fun = 0b100010;
        register("sub", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = rs - rt
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = rsVal - rtVal;

                            if (((rsVal ^ rtVal) & (rsVal ^ result)) < 0) {
                                throw new ArithmeticException("Integer overflow on sub");
                            }

                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void subu() {
        // subu rd rs rt
        int opcode = 0b000000;
        int fun = 0b100011;
        register("subu", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = rs - rt
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = rsVal - rtVal;

                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void and() {
        // and rd rs rt
        int opcode = 0b000000;
        int fun = 0b100100;
        register("and", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = rs & rt
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = rsVal & rtVal;
                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void or() {
        // or rd rs rt
        int opcode = 0b000000;
        int fun = 0b100101;
        register("or", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = rs | rt
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = rsVal | rtVal;
                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void xor() {
        // xor rd rs rt
        int opcode = 0b000000;
        int fun = 0b100110;
        register("xor", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = rs ^ rt
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = rsVal ^ rtVal;
                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void nor() {
        // nor rd rs rt
        int opcode = 0b000000;
        int fun = 0b100111;
        register("nor", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = ! (rs | rt)
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = ~(rsVal | rtVal);
                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void slt() {
        // slt rd rs rt
        int opcode = 0b000000;
        int fun = 0b101010;
        register("slt", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = (rs < rt ? 1 : 0)
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            int result = rsVal < rtVal ? 1 : 0;
                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void sltu() {
        // sltu rd rs rt
        int opcode = 0b000000;
        int fun = 0b101011;
        register("sltu", opcode, List.of(REG5, REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], operands[2], operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rt = operands[2], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, rt, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = (rs < rt ? 1 : 0)
                            int rsVal = c.getRegister(rs);
                            int rtVal = c.getRegister(rt);
                            boolean less = Integer.compareUnsigned(rsVal, rtVal) < 0;
                            int result = less ? 1 : 0;
                            c.setRegister(rd, result);
                            c.setPC(c.getPC() + 4);
                        }
                    };
                });
    }

    private static void jr() {
        // jr rs
        int opcode = 0b000000;
        int fun = 0b001000;
        register("jr", opcode, List.of(REG5),
                operands -> new RTypeInstruction(opcode, operands[0], 0b00000, 0b00000, 0b00000, fun),
                operands -> {
                    int rs = operands[0];
                    return new RTypeInstruction(opcode, rs, 0b00000, 0b00000, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // pc = rs
                            int rsVal = c.getRegister(rs);
                            c.setPC(rsVal);
                        }
                    };
                });
    }

    private static void jalr() {
        // jalr rd rs
        int opcode = 0b000000;
        int fun = 0b001001;
        register("jalr", opcode, List.of(REG5, REG5),
                operands -> new RTypeInstruction(opcode, operands[1], 0b00000, operands[0], 0b00000, fun),
                operands -> {
                    int rs = operands[1], rd = operands[0];
                    return new RTypeInstruction(opcode, rs, 0b00000, rd, 0b00000, fun) {
                        @Override
                        public void execute(Configuration c) {
                            // rd = pc + 3, pc = rs
                            int nextPC = (int) (c.getPC() + 4);
                            c.setRegister(rd, nextPC);
                            int target = c.getRegister(rs);
                            c.setPC(target);
                        }
                    };
                });
    }

    private static void sysc() {
        // sysc
        int opcode = 0b000000;
        int fun = 0b001100;
        register("sysc", opcode, List.of(),
                operands -> new RTypeInstruction(opcode, 0b00000, 0b00000, 0b00000, 0b00000, fun),
                operands -> new RTypeInstruction(opcode, 0b00000, 0b00000, 0b00000, 0b00000, fun) {
                    @Override
                    public void execute(Configuration c) {
                        // TODO what happens on syscall
                        // c.handleSyscall();
                        // c.setPC(c.getPC() + 4);
                    }
                });
    }

    private static void eret() {
        // eret
        int opcode = 0b010000;
        register("eret", opcode, List.of(),
                operands -> new RTypeInstruction(opcode, 0b10000, 0b00000, 0b00000, 0b00000, 0b011000),
                operands -> new RTypeInstruction(opcode, 0b10000, 0b00000, 0b00000, 0b00000, 0b011000) {
                    @Override
                    public void execute(Configuration c) {
                        // PC = EPC (Exception Program Counter)
                        // TODO
                    }
                });
    }

    private static void movg2s() {
        // movg2s rd rt
        int opcode = 0b010000;
        register("movg2s", opcode, List.of(REG5, REG5),
                operands -> new RTypeInstruction(opcode, 0b00100, operands[1], operands[0], 0b00000, 0b000000),
                operands -> {
                    int rd = operands[0], rt = operands[1];
                    return new RTypeInstruction(opcode, 0b00100, rt, rd, 0b00000, 0b000000) {
                        @Override
                        public void execute(Configuration c) {
                            // spr[rd] = gpr[rt]
                            // TODO
                        }
                    };
                });
    }

    private static void movs2g() {
        // movs2g rd rt
        int opcode = 0b010000;
        register("movs2g", opcode, List.of(REG5, REG5),
                operands -> new RTypeInstruction(opcode, 0b00000, operands[1], operands[0], 0b00000, 0b000000),
                operands -> {
                    int rd = operands[0], rt = operands[1];
                    return new RTypeInstruction(opcode, 0b00000, rt, rd, 0b00000, 0b000000) {
                        @Override
                        public void execute(Configuration c) {
                            // gpr[rt] = spr[rd]
                            // TODO
                        }
                    };
                });
    }

    private static void j() {
        // j iindex
        int opcode = 0b000010;
        register("j", opcode, List.of(IINDEX26),
                operands -> new JTypeInstruction(opcode, operands[0]),
                operands -> {
                    int iindex = operands[0];
                    return new JTypeInstruction(opcode, iindex) {
                        @Override
                        public void execute(Configuration c) {
                            // pc = bin_32(pc + 4)[31:28]iindex00
                            long pcPlus4 = c.getPC() + 4L;
                            long upperPC = pcPlus4 & 0xF0000000L;
                            long target = ((long) iindex << 2) & 0x0FFFFFFFL;

                            long newPC = upperPC | target;
                            c.setPC(newPC);
                        }
                    };
                });
    }

    private static void jal() {
        // jal iindex
        int opcode = 0b000011;
        register("jal", opcode, List.of(IINDEX26),
                operands -> new JTypeInstruction(opcode, operands[0]),
                operands -> {
                    int iindex = operands[0];
                    return new JTypeInstruction(opcode, iindex) {
                        @Override
                        public void execute(Configuration c) {
                            // R31 = pc + 4
                            // pc = bin_32(pc + 4)[31:28]iindex00
                            long pcPlus4 = c.getPC() + 4L;
                            c.setRegister(31, (int) pcPlus4);

                            long upperPC = pcPlus4 & 0xF0000000L;
                            long target = ((long) iindex << 2) & 0x0FFFFFFFL;

                            long newPC = upperPC | target;
                            c.setPC(newPC);
                        }
                    };
                });
    }
}
