package com.wars.compiler.codegen;

import com.wars.compiler.config.Configuration;
import com.wars.compiler.model.Fun;
import com.wars.compiler.model.VarReg;
import com.wars.compiler.model.VarType;
import com.wars.compiler.model.Variable;
import com.wars.compiler.table.MemoryTable;
import com.wars.compiler.table.TypeTable;
import com.wars.compiler.tree.DTE;

import static com.wars.compiler.codegen.ExpressionEvaluator.evaluateExpression;
import static com.wars.compiler.util.Context.BPT;
import static com.wars.compiler.util.Context.SPT;
import static com.wars.compiler.util.Logger.log;
import static com.wars.compiler.util.TypeUtils.checkTokenType;

public class IdEvaluator {
    private static CodeGenerator cg() {
        return CodeGenerator.getInstance();
    }


    public static VarReg evaluateId(DTE id, boolean lv) throws Exception {
        checkTokenType(id, "<id>");

        log("Evaluating id: " + id.getBorderWord());

        // id -> Na
        if (id.getFirstSon().isType("<Na>")) {
            log("Found <Na>:\n");
            DTE na = id.getFirstSon();
            VarReg result = bindVariableName(na);

            if (!lv) {
                String instr = Instruction.deref(result.register);
                cg().addInstruction(instr);
            }

            return result;
        }

        // id -> id.Na
        if (id.getNthSon(2).isType(".")) {
            DTE nestedId = id.getFirstSon();
            DTE nestedNa = id.getNthSon(3);
//            int struct = evaluateId(flattened.get(0));
            VarReg structReg = evaluateId(nestedId, true);
            String compName = nestedNa.getBorderWord();

            Variable boundComp = structReg.variable.getStructComponent(compName);

            if (boundComp != null) { // variable in struct
                // let j store base address of struct
                // generated instruction will be
                // addi j j displ(comp, struct)
                int j = structReg.register;
                int displ = boundComp.getDisplacement();

                // create instruction
                String instr = Instruction.addi(j, j, displ);
                // add instruction to the list
                cg().addInstruction(instr);

                VarReg result = new VarReg(boundComp, j);

                if (!lv) {
                    cg().addInstruction(Instruction.deref(result.register));
                }

                return result;
            }
        }

        // id -> id[E]
        if (id.getNthSon(2).isType("[")) {
            DTE nestedId = id.getFirstSon();
            DTE nestedIndex = id.getNthSon(3);

            VarReg array = evaluateId(nestedId, true);
            VarReg index = evaluateExpression(nestedIndex);

            // gpr(23) = enc(size(t))
            int arrSize = array.type.getArrayCompTargetType().size;
            // storing encoded size in $23
            cg().addInstruction("macro: gpr(23) = enc(" + arrSize + ", uint)");

            // mul(j', j', 23)
            cg().addInstruction("macro: mul(" + index.register + ", " + index.register + ", 23)");

            // add j j j'
            String instr = Instruction.add(array.register, array.register, index.register);
            cg().addInstruction(instr);

            Configuration.getInstance().freeRegister(index.register);

            assert array.variable != null;
            Variable arrayVariable = new Variable(array.variable.getName() + "[" + nestedIndex.getBorderWord() + "]", array.variable.getBaseAddress(), array.variable.getType().getArrayCompTargetType(),  array.variable.getDisplacement() + arrSize);
            VarReg result = new VarReg(arrayVariable, array.register);
            log("ARRAY " + arrayVariable);
          
            if (!lv) {
                cg().addInstruction(Instruction.deref(result.register));
            }

            return result;
        }

        // id -> id*
        if (id.getNthSon(2).isType("'")) {
            VarReg pointer = evaluateId(id.getFirstSon(), true);
            log("POINTER " + id.getBorderWord() + ", of form: " + pointer.variable + ", " + pointer.type);

            // create instruction lw j j 0 ~ deref
            String instr = Instruction.deref(pointer.register);
            cg().addInstruction(instr);

            if (!lv) {
                cg().addInstruction(Instruction.deref(pointer.register));
            }

            VarType targetType = TypeTable.getInstance().getType(pointer.type.pointerTypeTargetName);
            return new VarReg(new Variable(null, 0, targetType, 0), pointer.register);
        }

        if (id.getNthSon(2).isType("&")) {

            VarReg reg = evaluateId(id.getFirstSon(), true);
            VarType pType = TypeTable.getInstance().getTypesPointer(reg.type);
            return new VarReg(reg.register, pType);
        }

        throw new IllegalArgumentException("Grammar error on \"" + id.getBorderWord() + "\"");
    }

    public static VarReg bindVariableName(DTE na) {
        checkTokenType(na, "<Na>");

        String name = na.getBorderWord();
        Variable bindedVariable = null;


        // try to bind from current function
        Fun cf = Configuration.getInstance().currentFunction();
        Variable cfMemory = cf.getMemoryStruct();
        if (cfMemory != null) {
            bindedVariable = cfMemory.getStructComponent(name);
        }

        if (bindedVariable != null) { // variable contained in function struct
            // base address is loaded into register j
            // with cf=f and bindedVariable=x, we get
            // addi j spt displ(x, $f) - size($f)
            int imm = bindedVariable.getDisplacement() - cf.getSize();
            int j = Configuration.getInstance().getFirstFreeRegister();

            // create instruction
            String instr = Instruction.addi(j, SPT, imm);
            // add instruction to the list
            cg().addInstruction(instr);

            return new VarReg(bindedVariable, j);
        }

        // try to bind from gm
        try {
            Variable gm = MemoryTable.getInstance().gm();
            bindedVariable = gm.getStructComponent(name);
        } catch (Exception ignored) {
        }

        if (bindedVariable != null) { // variable is in global memory
            // generated command is
            // addi j bpt displ(x, $gm)
            int displ = bindedVariable.getDisplacement();
            int j = Configuration.getInstance().getFirstFreeRegister();

            // create instruction
            String instr = Instruction.addi(j, BPT, displ);
            // add instruction to the list
            cg().addInstruction(instr);

            return new VarReg(bindedVariable, j);
        }

        throw new IllegalArgumentException("No variable with name " + name + " found.");
    }

}
