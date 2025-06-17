package com.wars.c0program;
import com.wars.util.CodeTranslation;
import com.wars.compiler.util.Context;

public class C0Program {

    public int K = (1 << 10);
    public int a = 0;
    public int b = 4 * K;

    public int SBASE = 5 * K;
    public int SMAX = 9 * K;
    public int HBASE = 10 * K;
    public int HMAX = 14 * K; 

    public String code;
    public String mipsCode;
    public String byteCode;

    public C0Program(int a, int b, int SBASE, int SMAX, int HBASE, int HMAX, String code){
        this.a = a;
        this.b = b;
        this.SBASE = SBASE;
        this.SMAX = SMAX;
        this.HBASE = HBASE;
        this.HMAX = HMAX;
        this.code = code;
        this.mipsCode = mipsCode(code);
        this.byteCode = byteCode();
    }

    public C0Program(String code){
        this.code = code;
        this.mipsCode = mipsCode(code);
        this.byteCode = byteCode();
    }

    public C0Program(){}

    
    public String mipsCode(String code) {
        String adjustedCode = toC0Grammar(code);
        Context.SBASE = this.SBASE;
        Context.HBASE = this.HBASE;
        Context.SMAX = this.SMAX;
        Context.HMAX = this.HMAX;
        return CodeTranslation.C0Translation(adjustedCode, true);
    }

    public String byteCode(){
        String byteCode = "";

        return byteCode;
    }

    public static String toC0Grammar(String code){
        String adjustedCode = "";
        boolean comment = false;
        for (int i = 0; i < code.length(); i++){
            if (code.charAt(i) == ' ' && i < code.length() - 2 && code.charAt(i + 1) == '/' && code.charAt(i + 2) == '/'){
                comment = true; 
            }
            if (i < code.length() - 1 && code.charAt(i) == '/' && code.charAt(i + 1) == '/'){
                comment = true;
            }
            if (code.charAt(i) == '\n'){
                comment = false; 
            }
            if (!comment && code.charAt(i) != '\n' && code.charAt(i) != '\t'){
                adjustedCode += code.charAt(i);
            }
            
        }
        return adjustedCode;
    }

    public static String asm(String s){
        return "asm( " + s + " )";
    }

    



}
