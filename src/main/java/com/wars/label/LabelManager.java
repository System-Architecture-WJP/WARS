package com.wars.label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wars.exception.AssemblerException;
import com.wars.instruction.JTypeInstruction;

public class LabelManager {
    private final Map<String, Label> labels = new HashMap<>();
    private long currAddress;

    public LabelManager(int startAddress) {
        currAddress = startAddress;
    }

    public void define(String label, long line) {
        Label l = labels.get(label);
        if (l == null) {
            l = new Label(label, currAddress, line);
            labels.put(label, l);
            return;
        }
        if (l.isDefined()) {
            throw new AssemblerException("Double definition of " + label
                    + " label. First time seen at line " + l.getLine());
        }
        l.define(currAddress, line);
    }

    public void resolve(String label, JTypeInstruction instruction) {
        if (instruction.isResolved()) {
            throw new AssemblerException("Attempt to resolve resolved instruction");
        }

        Label l = labels.get(label);
        if (l == null) {
            l = new Label(label, instruction);
            labels.put(label, l);
        } else if (l.isDefined()) {
            instruction.resolve(l.getAddress());
        } else {
            l.addInstruction(instruction);
        }
    }
    
    public List<Label> getUndefined() {
        List<Label> result = new ArrayList<>();
        for (Map.Entry<String,Label>  l : labels.entrySet()) {
            if (!l.getValue().isDefined())
                result.add(l.getValue());
        }
        return result;
    }
    
    public void increaseAddress(int num) {
        currAddress += num;
    }
    
    public long getCurrAddress() {
        return currAddress;
    }

}
