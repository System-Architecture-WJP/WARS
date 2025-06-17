package com.wars;
import com.wars.c0program.*;
import com.wars.util.CodeTranslation;
import com.wars.compiler.util.Context;
import com.wars.assembler.Assembler;

import static com.wars.compiler.util.Context.bootLoaderInit;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Context.DEBUG = false;
        String code = "int main(){asm( macro: divt(1, 1, 2) ); return 1}~";
        C0Program pr = new C0Program(code);
        BootLoader bt = BootLoader.generateBootLoader();
        AbstractKernel ab = AbstractKernel.generateAbstractKernel();
        System.out.println(bt.getMipsCode());
        System.out.println(pr);
    }   
}
