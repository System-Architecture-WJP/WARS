package com.wars.compiler.exceptions.typedef;

import com.wars.compiler.model.VarType;

public class TypeDefNoStructComponentsException extends TypeDefException {

    public TypeDefNoStructComponentsException(VarType.Builder varType) {
        super("Cannot create struct + '" + varType.getName() + "' with no components");
    }
}
