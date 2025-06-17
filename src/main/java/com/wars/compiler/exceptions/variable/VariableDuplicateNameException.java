package com.wars.compiler.exceptions.variable;

import com.wars.compiler.exceptions.typedef.TypeDefException;

public class VariableDuplicateNameException extends TypeDefException {

    public VariableDuplicateNameException(String structName, String compName) {
        super("Can not create struct '" + structName + "'! component with name '" + compName + "' already exists!");
    }
}
