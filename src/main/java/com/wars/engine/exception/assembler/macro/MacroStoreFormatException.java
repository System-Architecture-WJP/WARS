package com.wars.engine.exception.assembler.macro;

import com.wars.engine.exception.assembler.AssemblerException;

public class MacroStoreFormatException extends AssemblerException {
    public MacroStoreFormatException(String message) {
        super("Invalid macro store format: " + message);
    }
}

