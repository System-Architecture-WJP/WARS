package com.wars.util;

import com.wars.compiler.codegen.CodeGenerator;
import com.wars.compiler.config.Configuration;
import com.wars.compiler.dk.DK1;
import com.wars.compiler.dk.GenerateAutomaton;
import com.wars.compiler.grammar.Grammar;
import com.wars.compiler.table.FunctionTable;
import com.wars.compiler.table.MemoryTable;
import com.wars.compiler.table.TypeTable;
import com.wars.compiler.tree.DTE;
import com.wars.compiler.util.TypeUtils;

import java.util.List;
import java.util.LinkedList;

import static com.wars.compiler.util.Context.DEBUG;

public class CodeTranslation {

    public static String grammarFilePath = "compiler/src/main/java/grammar/Grammar.txt";
    public static String terminalsFilePath = "compiler/src/main/java/grammar/Terminals.txt";
    public static boolean generated = false; 
    public static Grammar g;
    public static DK1 dk1; 

    public static void fillTables(DTE program) {
        TypeUtils.checkTokenType(program, "<prog>");

        DTE current = program.getFirstSon();
        if (current.isType("<TyDS>")) {
            TypeTable.getInstance().fillTable(current);
            current = current.getNthBrother(2);
        }

        if (current.isType("<VaDS>")) {
            MemoryTable.getInstance().fillTable(current);
            current = current.getNthBrother(2);
        }

        if (current.isType("<FuDS>")) {
            FunctionTable.getInstance().fillTable(current);
        }

        if (DEBUG) {
            TypeTable.getInstance().printTable();
            MemoryTable.getInstance().printTable();
            FunctionTable.getInstance().printTable();
        }
    }

    public static void initialize() throws Exception {
        if (!generated){
            g = new Grammar(grammarFilePath, terminalsFilePath);
            dk1 = GenerateAutomaton.generateAutomaton(grammarFilePath, terminalsFilePath, g);
            generated = true; 
        }
    }

    public static List<String> C0TranslationList(String code, boolean program) {
        DTE parsedT = dk1.parseString(code);
        fillTables(parsedT);
        
        CodeGenerator.getInstance().setGrammar(g);
        CodeGenerator.getInstance().generateCode();

        List<String> translation = new LinkedList<>();
        try {
            translation = CodeGenerator.getInstance().getInstructionsList(program);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

        TypeTable.reset();
        MemoryTable.reset();
        FunctionTable.reset();
        Configuration.reset();
        CodeGenerator.reset();

        return translation;

    } 

    
    public static String C0Translation(String code, boolean program) {
        DTE parsedT = dk1.parseString(code);
        fillTables(parsedT);
        
        CodeGenerator.getInstance().setGrammar(g);
        CodeGenerator.getInstance().generateCode();

        String translation = "" ;
        try {
            translation = CodeGenerator.getInstance().getInstructions(true);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }


        TypeTable.reset();
        MemoryTable.reset();
        FunctionTable.reset();
        Configuration.reset();
        CodeGenerator.reset();

        return translation;

    } 
    
}
