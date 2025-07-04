package com.wars.engine.exception.assembler.macro;

import com.wars.engine.exception.assembler.AssemblerException;

public class InvalidMacroDefinitionException extends AssemblerException {
    public InvalidMacroDefinitionException(String message) {
        super("Invalid macro definition: " + message);
    }
}
