package com.wars.engine.assembler;

import com.wars.engine.label.Label;
import com.wars.engine.label.LabelManager;
import com.wars.engine.operand.OperandParser;
import com.wars.engine.exception.AssemblerException;
import com.wars.engine.instruction.Instruction;
import com.wars.engine.instruction.InstructionRegistry;
import com.wars.engine.instruction.JTypeInstruction;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.function.Consumer;

import com.wars.engine.macro.Macro;

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

    public Assembler(String codeLines, long currLine) {
        this.inputScanner = new Scanner(codeLines);
        this.outputStream = OutputStream.nullOutputStream();
        this.labelManager = new LabelManager(0);
        this.instructionsQueue = new LinkedList<>();
        this.currLine = currLine;
    }

    public void assembleToBinaryString() {
        try (PrintStream outputPrintStream = new PrintStream(outputStream)) {
            var instructionConsumer = (Consumer<Instruction>) instruction -> {
                String binaryString = instruction.toBinaryString();
                outputPrintStream.println(binaryString);
            };

            while (advance()) {
                drainInstructions(instructionConsumer);
            }
            drainInstructions(instructionConsumer);
        }
    }

    public int[] toByteCodeArray() {
        List<Integer> out = new ArrayList<>();
        var instructionConsumer = (Consumer<Instruction>) instruction -> {
            out.add(instruction.encode());
        };

        while (advance()) {
            drainInstructions(instructionConsumer);
        }
        drainInstructions(instructionConsumer);
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    private void drainInstructions(Consumer<Instruction> consumer) {
        while (!instructionsQueue.isEmpty()) {
            Instruction i = instructionsQueue.peek();
            if (!i.isResolved()) {
                break;
            }
            instructionsQueue.remove();
            consumer.accept(i);
        }
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
        instructionsQueue.addAll(instructions);
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
