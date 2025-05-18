package com.wars.assembler;

import com.wars.exception.AssemblerException;
import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionRegistry;
import com.wars.instruction.JTypeInstruction;
import com.wars.label.Label;
import com.wars.label.LabelManager;
import com.wars.operand.OperandParser;
import com.wars.operand.OperandType;

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
    
        String[] split = line.split("[\\s(),]+");
        if (split.length < 2) {
            throw new AssemblerException("Invalid macro definition: " + line);
        }
        String macroType = split[1];
        String [] mipsInstructions;

        if (macroType.equals("gpr")){

            if (split.length != 7) {
                throw new AssemblerException("Invalid macro definition: " + line);
            }

            String reg = split[2];
            String val = split[5];

            mipsInstructions = new String[]{
                "addi " + reg + " 0 " + val,
            };

        }
        else if (macroType.equals("divt")){
            
            mipsInstructions = new String[]{
                ""
            };

        }
        else if (macroType.equals("divu")){
            
            mipsInstructions = new String[]{
                ""
            };


        }
        else if (macroType.equals("mul")){

            if (split.length != 5) {
                throw new AssemblerException("Invalid macro definition: " + line);
            }

            String k = split[2];
            String i = split[3];
            String j = split[4]; 

            mipsInstructions = new String[]{
                "addi 24 0 1",
                "addi 26 " + i + " 0",
                "addi 27 0 0",
                "and 25 " + j + " 24",
                "beq 25 0 2",
                "add 27 27 26",
                "add 24 24 24",
                "add 26 26 26",
                "bne 24 0 -5",
                "addi " + k + " 27 0"
            };
         
        }
        else if (macroType.equals("zero")){
            
            if (split.length != 4) {
                throw new AssemblerException("Invalid macro definition: " + line);
            }

            String i = split[2];
            String j = split[3]; 

            
            mipsInstructions = new String[]{
                "sw 0 " + i + " 0",
                "addi " + i + " " + i + " 4",
                "sw 0 " + j + " 0",
                "bne 0 " + j + " -3"
            };
        
        }
        else {
            throw new AssemblerException("Invalid macro type: " + macroType);
        }

        for (int i = 0; i < mipsInstructions.length; i++){
                
            String[] tokens = mipsInstructions[i].split("\\s+");
            String mnemonic = tokens[0];

            String[] operands = Arrays.copyOfRange(tokens, 1, tokens.length);

            Instruction instruction;
            
            var expectedOperandTypes = InstructionRegistry.getOperandTypes(mnemonic);
            int[] parsedOperands = OperandParser.parseAll(operands, expectedOperandTypes);
            instruction = InstructionRegistry.create(mnemonic, parsedOperands);
            
            instructionsQueue.add(instruction);
            labelManager.increaseAddress(4);
        }

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
