package com.wars.compiler.table;

import com.wars.compiler.exceptions.memory.MemoryStructAlreadyExistsException;
import com.wars.compiler.exceptions.memory.MemoryStructException;
import com.wars.compiler.exceptions.memory.MemoryStructIncompatibleTypeException;
import com.wars.compiler.exceptions.memory.MemoryStructNotFoundException;
import com.wars.compiler.model.VarType;
import com.wars.compiler.model.Variable;
import com.wars.compiler.tree.DTE;
import com.wars.compiler.util.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wars.compiler.model.VarType.TypeClass.STRUCT;
import static com.wars.compiler.util.Logger.log;
import static com.wars.compiler.util.TypeUtils.checkTokenType;

public class MemoryTable implements Table {
    private final Map<String, Variable> table;
    private static MemoryTable INSTANCE;

    public MemoryTable() {
        table = new HashMap<>();
    }

    public static MemoryTable getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MemoryTable();
        return INSTANCE;
    }

    public static void reset() {
        INSTANCE = null;
    }

    public Variable getMemory(String name) throws MemoryStructException {
        var memory = table.get(name);
        if (memory == null)
            throw new MemoryStructNotFoundException(name);
        return memory;
    }

    public void addMemory(Variable memoryStruct) throws MemoryStructException {
        if (memoryStruct.getType().typeClass != STRUCT)
            throw new MemoryStructIncompatibleTypeException();
        if (table.containsKey(memoryStruct.getName()))
            throw new MemoryStructAlreadyExistsException(memoryStruct.getName());
        table.put(memoryStruct.getName(), memoryStruct);
    }

    @Override
    public void fillTable(DTE vads) {
        checkTokenType(vads, "<VaDS>");

        List<List<String>> componentPairs = vads.extractComponentPairs();
        String name = "$gm";

        VarType.Builder structBuilder = VarType.createStructTypeBuilder(componentPairs, name);
        VarType varType = null; 
        try {
            varType = TypeTable.getInstance().createStructType(structBuilder);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        Variable gm = new Variable(name, Context.BPT, varType, 0);
        try {
            addMemory(gm);    
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }

    public Variable gm() throws MemoryStructException {
        return getMemory("$gm");
    }

    public void printTable() {
        System.out.println("\n\n------ Memory TABLE ------");
        table.forEach((key, value) ->
                System.out.println("[KEY=" + key + ", VALUE=" + value + "]")
        );
        System.out.println("--------------------------");

    }
}
