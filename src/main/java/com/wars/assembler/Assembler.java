package com.wars.assembler;

import com.wars.exception.AssemblerException;
import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import com.wars.label.Label;
import com.wars.label.LabelManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Assembler {
    private final Scanner inputScanner;
    private final OutputStream outputStream;
    private final LabelManager labelManager;
    private final Queue<Instruction> instructions = new LinkedList<>();
    private long currLine = 0;

    public Assembler(InputStream inputStream, OutputStream outputStream,
            int startAddress) {
        inputScanner = new Scanner(inputStream);
        this.outputStream = outputStream;
        labelManager = new LabelManager(startAddress);
    }

    private void drainInstructionsToBinaryString(PrintStream outputPrintStream) {
        while (!instructions.isEmpty()) {
            Instruction i = instructions.peek();
            if (!i.isResolved()) {
                break;
            }
            instructions.remove();
            outputPrintStream.println(i.toBinaryString());
        }
    }
    
    public void assembleToBinaryString() {
        PrintStream outputPrintStream = new PrintStream(outputStream);
        
        while (advance()) {
            drainInstructionsToBinaryString(outputPrintStream);
        }
        drainInstructionsToBinaryString(outputPrintStream);
    }

    private boolean advance() {
        if (!inputScanner.hasNextLine()) {
            List<Label> unresolvedLabels = labelManager.getUndefined();
            if (!unresolvedLabels.isEmpty()) {
                throw new AssemblerException(
                        "Not all labels are resolved at the end of input stream: "
                                + unresolvedLabels);
            }
            return false;
        }

        String line = inputScanner.nextLine().trim();
        currLine++;

        if (line.isBlank() || line.startsWith("#")) {
            return true;
        }

        String[] split = line.split("\\s+");

        if (split.length == 1 && split[0].endsWith(":")) {
            labelManager.define(split[0].substring(0, split[0].length() - 1),
                    currLine);
            return true;
        }

        if (split[0].equals("macro:")) {
            return handleMacro(line);
        }

        String mnemonic = split[0];
        String[] operands = Arrays.copyOfRange(split, 1, split.length);
        Instruction instruction = InstructionRegistry.create(mnemonic,
                operands, labelManager);

        instructions.add(instruction);
        labelManager.increaseAddress(1);
        
        return true;
    }

    private boolean handleMacro(String line) {
        // TODO: implement this function
        return true;
    }

}
