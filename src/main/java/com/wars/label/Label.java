package com.wars.label;

import java.util.ArrayList;
import java.util.List;

import com.wars.exception.AssemblerException;
import com.wars.instruction.JTypeInstruction;

public class Label {
    private final String name;
    private long line;
    private long address;
    private List<JTypeInstruction> instructions;
    
    private Label(String name) {
        this.name = name;
        assert isValidName(name);
    }
    
    public Label(String name, JTypeInstruction instruction) {
        this(name);
        this.address = -1;
        this.line = -1;
        this.instructions = new ArrayList<>();
        addInstruction(instruction);
    }

    public Label(String name, long address, long line) {
        this(name);
        this.address = address;
        this.line = line;
        this.instructions = null;
    }
    
    public boolean isDefined() {
        return instructions == null;
    }
    
    public void define(long currAddress, long line) {
        address = currAddress;
        this.line = line;
        
        instructions.forEach(i -> i.resolve(address));
        
        instructions = null;
    }

    public static boolean isValidName(String labelName) {
        return labelName.matches("^_[a-z]+$");
    }
    
    public void addInstruction(JTypeInstruction instruction) {
        if (isDefined()) {
            throw new AssemblerException("Attempt to add an instuction to defined label");
        }
        instructions.add(instruction);
    }
    
    public String getName() {
        return name;
    }
    public long getAddress() {
        return address;
    }
    public long getLine() {
        return line;
    }
    
    @Override
    public String toString() {
        if (isDefined()) {
            return name + ": address " + address + "; Defined at line: " + line;
        }
        
        return name;
    }
}
