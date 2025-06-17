package com.wars.compiler.exceptions.typedef;

import com.wars.compiler.model.VarType;

public class TypeDefInvalidArraySizeException extends TypeDefException {
    public TypeDefInvalidArraySizeException(VarType.Builder varType) {
        super("Error! Can't define array with size " + varType.getArraySize());
    }
}
