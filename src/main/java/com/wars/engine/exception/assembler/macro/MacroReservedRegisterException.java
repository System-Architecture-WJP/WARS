package com.wars.engine.exception.assembler.macro;

import com.wars.engine.exception.assembler.AssemblerException;

public class MacroReservedRegisterException extends AssemblerException {
    public MacroReservedRegisterException(String regName) {
        super("Cannot write to reserved register: " + regName);
    }
}
