package com.wars.engine.exception.assembler.label;

import com.wars.engine.exception.assembler.AssemblerException;

public class LabelConflictException extends AssemblerException {
    public LabelConflictException(String message) {
        super(message);
    }
}
