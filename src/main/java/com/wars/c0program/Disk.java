package com.wars.c0program;

public class Disk {

    public static String readms(){
        StringBuilder sb = new StringBuilder();
        sb.append("int readms(uint a){ //software read from hardwware memory\n");
        sb.append("\n");
        sb.append("\t" + "int tmp;" + "\n");
        sb.append("\t" + "gpr(1) = a;" + "\n");
        sb.append("\t" + C0Program.asm("lw 2 1 0") + ";" + "\n");
        sb.append("\t" + asm("sw 2 29 -4")+ ";" + "\n");
        sb.append("\t" + "return tmp" + "\n");
        sb.append("};" + "\n");
        sb.append("\n");

        return sb.toString(); 
    }

    public static String writems(){
        StringBuilder sb = new StringBuilder();
        sb.append("void writems(uint x, uint a){ //software write to hardware memory\n");
        sb.append("\n");
        sb.append("\t" + "gpr(1) = x;" + "\n");
        sb.append("\t" + "gpr(2) = a {1};" + "\n");
        sb.append("\t" + asm("sw 1 2 0") + "\n");
        sb.append("};" + "\n");
        sb.append("\n");

        return sb.toString();

    }

    public static String copyms(){
        StringBuilder sb = new StringBuilder();
        sb.append("void copyms(uint a, uint b, uint L){ //copies L words starting from a in MS to words from b\n");
        sb.append("\n");
        sb.append("\t" + "gpr(1) = a;" + "\n");
        sb.append("\t" + "gpr(2) = b {1};" + "\n");
        sb.append("\t" + "gpr(3) = L {1, 2};" + "\n");
        sb.append("\n");
        sb.append("\t" + asm("blez 3 7") + ";" + " // until L <= 0 {\n");
        sb.append("\t" + asm("lw 4 1 0") + ";" + "\n");
        sb.append("\t" + asm("sw 4 2 0") + ";" + " // m(b) = m(a)\n");
        sb.append("\t" + asm("addi 1 1 4") + ";" + " // a += 4\n");
        sb.append("\t" + asm("addi 2 2 4") + ";" + " // b += 4\n");
        sb.append("\t" + asm("addi 3 3 -1") + ";" + " // L -= 1\n");
        sb.append("\t" + asm("blez 0 -6") + " // }\n");
        sb.append("};" + "\n");
        sb.append("\n");
        return sb.toString();
    }

    public static String readdisk(int HDBASE, int K){
        StringBuilder sb = new StringBuilder();
        sb.append("void readdisk(uint ppx, uint spx){ // ppx - MS page index where page is stored, spx - swap memory page index which is read\n");
        sb.append("\n");
        sb.append("\t" + "int y;" + " \n");
        sb.append("\t" + "writems(spx, " + (HDBASE + 4 * K) + "u);" + " // spa(d) = spx \n");
        sb.append("\t" + "writems(1u, " + (HDBASE + 4 * K + 4) + "u);" + " // issue read access \n");
        sb.append("\n");
        sb.append("\t" + "y = 1;" + " \n");
        sb.append("\t" + "while y!=0 {" + "\n");
        sb.append("\t\t" + "y = readms(" + (HDBASE + 4 * K + 4) + "u)" + " // polling \n");
        sb.append("\t}; \n");
        sb.append("\n");
        sb.append("\tcopyms(" + HDBASE + "u, ppx*" + (4 * K) + "u, " + K + "u)" + "\n");
        sb.append("};" + "\n");
        sb.append("\n");

        return sb.toString();
    }

    public static String writedisk(int HDBASE, int K){
        StringBuilder sb = new StringBuilder();
        sb.append("void writedisk(uint spx, uint ppx){ // spx - swap memory page index which is written, ppx - page index which is stored \n");
        sb.append("\n");
        sb.append("\t" + "int y;" + " \n");
        sb.append("\tcopyms(ppx*" + (4 * K) + "u, " + HDBASE + "u, " + K + "u);" + " \n");
        sb.append("\t" + "writems(spx, " + (HDBASE + 4 * K) + "u);" + " // spa(d) = spx \n");
        sb.append("\t" + "writems(2u, " + (HDBASE + 4 * K + 4) + "u);" + " // issue write access \n");
        sb.append("\n");
        sb.append("\t" + "y = 2;" + " \n");
        sb.append("\t" + "while y!=0 {" + "\n");
        sb.append("\t\t" + "y = readms(" + (HDBASE + 4 * K + 4) + "u)" + " // polling \n");
        sb.append("\t}\n");
        sb.append("\n");
        sb.append("};" + "\n");
        sb.append("\n");

        return sb.toString();
    }

    public static String asm(String s){
        return "asm( " + s + " )";
    }


    
}
