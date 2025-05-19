package com.wars.assembler;

import com.wars.exception.AssemblerException;
import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import com.wars.instruction.JTypeInstruction;
import com.wars.label.Label;
import com.wars.label.LabelManager;
import com.wars.operand.OperandParser;
import com.wars.macro.Macro;

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
    private final Queue<Instruction> instructionsQueue;
    private long currLine;

    public Assembler(InputStream inputStream, OutputStream outputStream, long currLine) {
        this.inputScanner = new Scanner(inputStream);
        this.outputStream = outputStream;
        this.labelManager = new LabelManager(0);
        this.instructionsQueue = new LinkedList<>();
        this.currLine = currLine;
    }

    private void drainInstructionsToBinaryString(PrintStream outputPrintStream) {
        while (!instructionsQueue.isEmpty()) {
            Instruction i = instructionsQueue.peek();
            if (!i.isResolved()) {
                break;
            }
            instructionsQueue.remove();
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
        Instruction instruction;

        if ((mnemonic.equals("j") || mnemonic.equals("jal"))
                && isJumpLabeled(operands)) {
            instruction = handleJType(mnemonic, operands);
        } else {
            var expectedOperandTypes = InstructionRegistry.getOperandTypes(mnemonic);
            int[] parsedOperands = OperandParser.parseAll(operands, expectedOperandTypes);
            instruction = InstructionRegistry.create(mnemonic, parsedOperands);
        }

        instructionsQueue.add(instruction);
        labelManager.increaseAddress(4);
        return true;
    }

    private boolean handleMacro(String line) {
    
        List<Instruction> instructions = Macro.evaluate(line); 

        instructions.forEach(instructionsQueue::add);
        labelManager.increaseAddress(4 * instructions.size());
        return true;
    }

    private JTypeInstruction handleJType(String mnemonic, String[] operands) {
        JTypeInstruction result = new JTypeInstruction(mnemonic.equals("j") ? 0b000010 : 0b000011, labelManager.getCurrAddress());
        labelManager.resolve(operands[0], result);
        return result;
    }

    private boolean isJumpLabeled(String[] operands) {
        return operands.length == 1 && Label.isValidName(operands[0]);
    }

}
