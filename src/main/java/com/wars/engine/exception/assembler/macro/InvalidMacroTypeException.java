package com.wars.engine.exception.assembler.macro;

import com.wars.engine.exception.assembler.AssemblerException;

public class InvalidMacroTypeException extends AssemblerException {
    public InvalidMacroTypeException(String type) {
        super("Invalid macro type: " + type);
    }
}
