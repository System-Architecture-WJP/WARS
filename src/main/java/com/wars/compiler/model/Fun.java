package com.wars.compiler.model;

import com.wars.compiler.exceptions.memory.MemoryStructException;
import com.wars.compiler.exceptions.typedef.TypeDefException;
import com.wars.compiler.exceptions.typedef.TypeNotDefinedException;
import com.wars.compiler.table.MemoryTable;
import com.wars.compiler.table.TypeTable;
import com.wars.compiler.tree.DTE;
import com.wars.compiler.util.TypeUtils;

import java.util.LinkedList;
import java.util.List;

import static com.wars.compiler.util.Logger.log;

public class Fun {
    @Override
    public String toString() {
        return "Function(name=" + name +
                ", returnType= " + (returnType == null ? "void" : returnType.name) +
                ", memoryStruct= " + memoryStruct +
                ", numParameters= " + numParameters +
                ", numLocalVariables= " + numLocalVariables + 
                ", body = " + body.toString();
    }

    private final String name;
    private final VarType returnType; //
    // no rda
    private final Variable memoryStruct; // $f = {int a, int b, char c} merge <PaDS> + <VaDS>
    /*
    int f(int a, int b) {
        char c;
        return 1
    }

     */
    private final int numParameters;
    private final int numLocalVariables; 
    private final DTE body;


    public Fun(String name,
               VarType returnType,
               Variable memoryStruct,
               int numParameters,
               int numLocalVariables, 
               DTE body) {
        this.name = name;
        this.returnType = returnType;
        this.memoryStruct = memoryStruct;
        this.numParameters = numParameters;
        this.numLocalVariables = numLocalVariables;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public VarType getReturnType() {
        return returnType;
    }

    public Variable getMemoryStruct() {
        return memoryStruct;
    }

    public int getNumParameters() {
        return numParameters;
    }

    public int getNumLocalVariables(){
        return numLocalVariables; 
    }

    public DTE getBody() {
        return body;
    }

    public int getSize() {
        if (memoryStruct == null) return 0;
        return memoryStruct.getType().size;
    }

    public static class Builder {
        private String name;
        private VarType returnType;

        private DTE pads;
        private DTE vads;
        private DTE body;

        public Fun build() throws TypeDefException, MemoryStructException {

            int numParameters;
            int numLocalVariables;
            if (pads == null) {
                numParameters = 0;
            } else {
                numParameters = pads.getFlattenedSequence().size();
//                numParameters = pads.getChildrenSize();
            }

            if (vads == null){
                numLocalVariables = 0;
            }
            else {
                numLocalVariables = vads.getFlattenedSequence().size();
            }
            List<List<String>> componentPairs = extractComponentPairs();

            VarType.Builder structBuilder;
            VarType structType = null;
            Variable memoryStruct = null;

            if (!componentPairs.isEmpty()) {
                structBuilder = VarType.createStructTypeBuilder(componentPairs, name);
                structType = TypeTable.getInstance().createStructType(structBuilder);
                memoryStruct = new Variable("$" + name, 0, structType, 0);
                MemoryTable.getInstance().addMemory(memoryStruct);
            }


            return new Fun(name, returnType, memoryStruct, numParameters, numLocalVariables, body);
        }

        public List<List<String>> extractComponentPairs() {
            List<List<String>> result = new LinkedList<>();

            if (pads != null) {
                result.addAll(pads.extractComponentPairs());
            }
            if (vads != null) {
                result.addAll(vads.extractComponentPairs());
            }
            return result;
        }

        public String getName() {
            return name;
        }

        public VarType getReturnType() {
            return returnType;
        }

        public DTE getBody() {
            return body;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setReturnType(String returnType) throws TypeNotDefinedException {
            if (returnType.equals("void")){
                this.returnType = null; 
            }
            else {
            this.returnType = TypeTable.getInstance().getType(returnType);
            }
            return this;
        }

        public Builder setPads(DTE pads) {
            this.pads = pads;
            return this;
        }

        public Builder setVads(DTE vads) {
            this.vads = vads;
            return this;
        }

        public Builder setBody(DTE body) {
            this.body = body;
            return this;
        }
    }

    public static Fun fromDTE(DTE fud) throws Exception {
        TypeUtils.checkTokenType(fud, "<FuD>");

        String returnType = fud.getFirstSon().getBorderWord();
        String name = fud.getNthSon(2).getBorderWord();
        DTE pads = null;
        DTE vads = null;
        DTE body;

        // getting to function parenthesis. If there are no parameters, current tree element is expected to be R_PAREN
        // fud.fson = ty
        // fud.fson.bro = na
        // fud.fson.bro.bro = (

        // Ty Na (PaDS) { VaDS ; body }
        DTE nextElement = fud.getNthSon(4);
        if (nextElement.isType("<PaDS>")) {
            pads = nextElement;
            log("Pads: " + pads.getBorderWord());
            nextElement = nextElement.getBrother();
        }

        // getting to function closure. If there are no variable declarations, current tree element is expected to be <body>
        nextElement = nextElement.getNthBrother(2);
        if (nextElement.isType("<VaDS>")) {
            vads = nextElement;
            log("Vads: " + vads.getBorderWord());
            nextElement = nextElement.getNthBrother(2);
        }

        body = nextElement;

        return new Builder()
                .setName(name)
                .setReturnType(returnType)
                .setPads(pads)
                .setVads(vads)
                .setBody(body)
                .build();

    }
}
