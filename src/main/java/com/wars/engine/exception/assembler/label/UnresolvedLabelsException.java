package com.wars.engine.exception.assembler.label;

import com.wars.engine.exception.assembler.AssemblerException;

public class UnresolvedLabelsException extends AssemblerException {
    public UnresolvedLabelsException(String message) {
        super("Not all labels are resolved at the end of input stream: " + message);
    }
}
