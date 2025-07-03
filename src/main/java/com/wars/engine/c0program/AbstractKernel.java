package com.wars.engine.c0program;

import com.wars.engine.util.Initialize;


public class AbstractKernel extends C0Program {

    public int K = Initialize.K;
    public int p = Initialize.p;
    public int PTLE = Initialize.PTLE;
    public int PTASIZE = Initialize.PTASIZE;
    public int nup = Initialize.nup;
    public int SBASE = Initialize.SBASE;
    public int SMAX = Initialize.SMAX;
    public int HBASE = Initialize.HBASE;
    public int HMAX = Initialize.HMAX;
    public int HDBASE = Initialize.HDBASE;
    public int UPBASE = Initialize.UPBASE;
    public int SMSIZE = Initialize.SMSIZE;
    public int SMUSERPAGE = Initialize.SMUSERPAGE;
    public int SMBASE = Initialize.SMBASE;
    public int a = Initialize.a;
    public int b = Initialize.b;

    public String code;
    public String mipsCode;
    public int[] byteCode;
    

    public AbstractKernel(int K, int p, int PTLE, int PTASIZE, int nup, int SBASE, int SMAX, int HBASE, int HMAX, int HDBASE, int UPBASE, int SMSIZE, int SMUSERPAGE, int SMBASE, int a, int b, String code) {
        this.K = K;
        this.p = p;
        this.PTLE = PTLE;
        this.PTASIZE = PTASIZE;
        this.nup = nup;
        this.SBASE = SBASE;
        this.SMAX = SMAX;
        this.HBASE = HBASE;
        this.HMAX = HMAX;
        this.HDBASE = HDBASE;
        this.UPBASE = UPBASE;
        this.SMSIZE = SMSIZE;
        this.SMUSERPAGE = SMUSERPAGE;
        this.SMBASE = SMBASE;
        this.a = a;
        this.b = b;

        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
    }

    public AbstractKernel(String code){
        this.code = code;
        this.mipsCode = mipsCode(this.code);
        this.byteCode = byteCode(this.mipsCode);
    }

    public static AbstractKernel generateAbstractKernel(){
        String code = generateKernel(Initialize.K, Initialize.p, Initialize.PTLE, Initialize.PTASIZE, Initialize.nup, Initialize.SBASE, Initialize.SMAX, Initialize.HBASE, Initialize.HMAX, Initialize.HDBASE, Initialize.UPBASE, Initialize.SMSIZE, Initialize.SMUSERPAGE, Initialize.SMBASE, Initialize.a, Initialize.b);
        return new AbstractKernel(code);
    }

     public static String typedef(int p, int PTASIZE, int nup){
        StringBuilder sb = new StringBuilder();
        sb.append("typedef uint' ptrunsigned; // pointer unsigned integer type\n");
        sb.append("typedef uint[32] u; // spr\n");
        sb.append("typedef uint[8] v; // gpr\n");
        sb.append("typedef struct {u GPR; v SPR} pcb; // gpr + spr\n");
        sb.append("typedef pcb[" + (p + 1) + "] PCBt; // for p user and + abstract kernel (gpr + spr)\n");
        sb.append("\n");
        sb.append("typedef uint[" + PTASIZE + "] PTAt; // page table entries array\n");
        sb.append("typedef uint[" + (p + 1) + "] PTOIt; // page table origin array for p user and abstract kernel\n"); 
        sb.append("\n");
        sb.append("typedef struct {uint usr; uint px} auxrec; // for a physical page it stores user which held this page and respective virtual page\n");
        sb.append("typedef auxrec[" + nup + "] IPTt; // for every physical page, above\n");
        sb.append("\n");

        return sb.toString();
    }
    
    public static String gm(){
        StringBuilder sb = new StringBuilder();

        sb.append("uint CP; // Current Process 0 - kernel, > 0 - user\n");
        sb.append("bool ipf; // invalid page fault\n");
        sb.append("PCBt PCB; // Process Control Block\n");
        sb.append("PTAt PTA; // Page Table Address\n");
        sb.append("PTOIt PTOI; // Page Table Origin\n");
        sb.append("\n");
        sb.append("uint i; // interrupt mask\n");
        sb.append("uint IL; // interrupt level\n");
        sb.append("uint found; // interrupt found\n");
        sb.append("uint EVA; // exception virtual address\n");
        sb.append("uint EVPX; // exception virutal address page index\n");
        sb.append("bool ptle; // page table length exception\n");
        sb.append("bool psfull; // all physical pages are full\n");
        sb.append("uint nextup; // next page index to swap in\n");
        sb.append("IPTt ipt; //\n");
        sb.append("int WOV; // write only variable for void functions\n");
        sb.append("\n");

        return sb.toString();
    }

    public static String computeIpf(){
        StringBuilder sb = new StringBuilder();

        sb.append("\tIL = 0u;\n");
        sb.append("\ti = 1u; \n");
        sb.append("\tfound = 0u; \n");
        sb.append("\twhile found==0u {\n");
        sb.append("\t\tIL = IL+1u; \n");
        sb.append("\t\ti = 2u*i; \n");
        sb.append("\t\tgpr(1) = i; \n");
        sb.append("\t\tgpr(2) = PCB[CP].SPR[2] {1}; \n");
        sb.append("\t\t" + asm("and 3 1 2") + "; \n");
        sb.append("\t\tfound = gpr(3)\n");
        sb.append("\t}; \n");
        
        sb.append("\n");
        sb.append("\tEVA = 0u; \n");
        sb.append("\tif IL==17u {EVA = PCB[CP].SPR[3]}; \n");
        sb.append("\tif IL==20u {EVA = PCB[CP].SPR[4]}; \n");

        sb.append("\n");
        sb.append("\tgpr(1) = EVA; \n");
        sb.append("\t" + asm("srl 1 1 12") + "; \n");
        sb.append("\tEVPX = gpr(1); \n");
        sb.append("\n");

        sb.append("\tptle = (EVPX>=PCB[CP].SPR[6]);" + "\n");
        sb.append("\tipf = (IL==17u||IL==20u)&&!(bool)ptle; \n");
        sb.append("\n");

        return sb.toString();
    }

    public static String scheduler(){
        StringBuilder sb = new StringBuilder();
        sb.append("void scheduler(){\n");
        sb.append("\n");
        sb.append("\t" + "CP = 1u" + " // set current user to 1 \n");
        sb.append("}; \n");
        sb.append("\n");

        return sb.toString();
    }

    public static String swapIn(int UPBASE, int K, int SMBASE, int SMUSERPAGE){
        StringBuilder sb = new StringBuilder();
        sb.append("void swapIn(){\n");
        sb.append("\n");
        sb.append("\t" + "uint PPXIN;" + " // pysical page index to swap in \n");
        sb.append("\t" + "uint SPAIN;" + " // swap page index to swap in \n");
        sb.append("\t" + "uint PTEIIN;" + " // pate table entry index to swap in \n");
        sb.append("\n");
        sb.append("\t" + "PPXIN = " + UPBASE + "u / " + (K * 4) + "u + nextup;" + "\n");
        sb.append("\t" + "SPAIN = " + SMBASE + "u + CP*" + (SMUSERPAGE) + "u + EVPX;" + "\n");
        sb.append("\t" + "PTEIIN = PTOI[CP] + EVPX;" + "\n");
        sb.append("\t" + "readdisk(PPXIN, SPAIN);" + " // read page from disk \n");
        sb.append("\t" + "PTA[PTEIIN] = PPXIN*" + (4 * K) + "u" + " + " + (2 * K) + "u;" + "\n");
        sb.append("\n");
        sb.append("\t" + "ipt[nextup].usr = CP;" + " // set user \n");
        sb.append("\t" + "ipt[nextup].px = EVPX" + " // set page index \n");
        sb.append("\n");
        sb.append("}; \n");

        return sb.toString();
    }


    public static String swapOut(int UPBASE, int SMBASE, int K, int SMUSERPAGE){
        StringBuilder sb = new StringBuilder();
        sb.append("void swapOut(){\n");
        sb.append("\n");
        sb.append("\t" + "uint PPXOUT;" + " // physical page index to swap out \n");
        sb.append("\t" + "uint SPAOUT;" + " // swap page index to swap out \n");
        sb.append("\t" + "uint PTEIOUT;" + " // page table entry index to swap out \n");
        sb.append("\n");
        sb.append("\t" + "PPXOUT = " + UPBASE + "u / " + (K * 4) + "u + nextup;" + "\n");
        sb.append("\t" + "SPAOUT = " + SMBASE + "u + ipt[nextup].usr*" + SMUSERPAGE + "u + ipt[nextup].px;" + "\n");
        sb.append("\t" + "PTEIOUT = PTOI[ipt[nextup].usr] + ipt[nextup].px;" + "\n");
        sb.append("\n");
        sb.append("\t" + "writedisk(SPAOUT, PPXOUT);"  + " // write page to disk \n");
        sb.append("\t" + "PTA[PTEIOUT] = PTA[PTEIOUT]-" + (2 * K) + "u" + "\n");
        sb.append("\n");
        sb.append("}; \n");

        return sb.toString();
    }

    public static String localVariables(){
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + "uint i;" + "\n");
        return sb.toString();
    } 

    public static String initVariables(int K){
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + "psfull = false;" + "\n");
        sb.append("\t" + "nextup = 0u;" + "\n");
        sb.append("\n");
        sb.append("\t" + "gpr(1) = PCB[0].GPR[0]&;" + " // init referernce on scratch memory \n");
        sb.append("\t" + asm("sw 1 0 " + (4 * K)) + ";\n");

        return sb.toString();
    }


    public static String initPTA(int PTASIZE){
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + "i = 0u;" + "\n");
        sb.append("\t" + "while i<" + (PTASIZE + 1) + "u {" + "\n");
        sb.append("\t\t" + "PTA[i] = 0u;" + "\n");
        sb.append("\t\t" + "i = i + 1u" + "\n");
        sb.append("\t};\n");
        sb.append("\n");
        return sb.toString();
    }

    public static String initPTOI(int p, int PTLE){
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + "i = 1u;" + "\n");
        sb.append("\t" + "PTOI[0] = 0u; // ignore for kernel" + "\n");
        sb.append("\t" + "while i<" + (p + 1) + "u {" + "\n");
        sb.append("\t\t" + "PTOI[i] = (i-1u)*" + PTLE + "u;" + "\n");
        sb.append("\t\t" + "i = i + 1u" + "\n");
        sb.append("\t};\n");
        sb.append("\n");
        return sb.toString();

    }
    
    public static String initSPR6(int PTLE, int p){
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + "i = 1u;" + "\n");
        sb.append("\t" + "PCB[0].SPR[6] = " + PTLE + "u;" + " // ignore for kernel" + "\n");
        sb.append("\t" + "while i<" + (p + 1)+ "u {" + "\n");
        sb.append("\t\t" + "PCB[i].SPR[6] = " + PTLE + "u;" + "\n");
        sb.append("\t\t" + "i = i + 1u" + "\n");
        sb.append("\t};\n");
        sb.append("\n");
        return sb.toString();
    }

    public static String initSPR5(int p){
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + "i = 1u;" + "\n");
        sb.append("\t" + "PCB[0].SPR[5] = 0u;" + " // ignore for kernel" + "\n");
        sb.append("\t" + "while i<" + (p + 1) + "u {" + "\n");
        sb.append("\t\t" + "gpr(1) = PTA[PTOI[i]]&;" + "\n");
        sb.append("\t\t" + "PCB[i].SPR[5] = gpr(1);" + "\n");
        sb.append("\t\t" + "i = i + 1u" + "\n");
        sb.append("\t};\n");
        sb.append("\n");
        return sb.toString();
    }

    public static String ipfHandler(int nup){
        StringBuilder sb = new StringBuilder();
        sb.append("void ipfHandler(){\n");
        sb.append("\n");
        sb.append("\t" + "if (!(bool)psfull) {swapIn()} " +  "\n");
        sb.append("\t" + "else {swapOut(); swapIn()}; " + "\n");
        sb.append("\n");
        sb.append("\t" + "nextup = nextup + 1u; " + "\n");
        // sb.append("\n");
        sb.append("\t" + "if nextup==" + nup + "u {nextup = 0u; psfull = true}\n");
        // sb.append("\t" + "CP = 1u\n");
        sb.append("}; \n");

        return sb.toString();
    }

    public static String runvm(){
        StringBuilder sb = new StringBuilder();
        sb.append("int runvm(){\n");
        sb.append("\n");
        sb.append("\t" + "gpr(1) = PCB[CP].GPR[0]&; // save current user on scratch memory\n");
        sb.append("\t" + asm("macro: restore-user") + "; // save spr's for kernel, restore user_cp configuration\n");
        sb.append("\n");
        sb.append("\t" + asm("macro: save-user") + "; // save user_cp configuration, restore kernel\n");

        sb.append("\n");

        sb.append(computeIpf());
        sb.append("\n");
        sb.append("\t" + "if (bool)ipf {ipfHandler(); gpr(1) = PCB[CP].GPR[0]&; " + asm("macro: restore-user") + "}; // if there is ipf handle it, go back to user_cp \n");
        sb.append("\t" + "return 1\n");
        sb.append("}; \n");

        return sb.toString();
    }

    public static String kernelMain(int K, int p, int PTLE, int PTASIZE, int nup, int SBASE, int SMAX, int HBASE, int HMAX, int HDBASE, int UPBASE, int SMSIZE, int SMUSERPAGE, int SMBASE, int a, int b){
        StringBuilder sb = new StringBuilder();
        sb.append("int main(){" + "\n");
        sb.append("\n");
        sb.append(localVariables());
        sb.append(initPTA(PTASIZE));
        sb.append(initPTOI(p, PTLE));
        sb.append(initSPR6(PTLE, p));
        sb.append(initSPR5(p));
        sb.append(initVariables(K));
        sb.append("\twhile true {" + "\n"); 
        sb.append("\t\tscheduler(); " + "\n");
        sb.append("\t\tWOV = runvm()" + "\n");
        sb.append("\t}; " + "\n");
        sb.append("\treturn 1" + "\n");
        sb.append("}~");
        return sb.toString();
    }

    public static String generateKernel(int K, int p, int PTLE, int PTASIZE, int nup, int SBASE, int SMAX, int HBASE, int HMAX, int HDBASE, int UPBASE, int SMSIZE, int SMUSERPAGE, int SMBASE, int a, int b){
        StringBuilder sb = new StringBuilder();
        sb.append(typedef(p, PTASIZE, nup));
        sb.append("\n");
        sb.append(gm());
        sb.append("\n");
        sb.append(Disk.readms());
        sb.append("\n");
        sb.append(Disk.writems());
        sb.append("\n");
        sb.append(Disk.copyms());
        sb.append("\n");
        sb.append(Disk.readdisk(HDBASE, K));
        sb.append("\n");
        sb.append(Disk.writedisk(HDBASE, K));
        sb.append("\n");
        sb.append(swapIn(UPBASE, K, SMBASE, SMUSERPAGE));
        sb.append("\n");
        sb.append(swapOut(UPBASE, SMBASE, K, SMUSERPAGE));
        sb.append("\n");
        sb.append(ipfHandler(nup));
        sb.append("\n");
        sb.append(scheduler());
        sb.append("\n");
        sb.append(runvm());
        sb.append("\n");
        sb.append(kernelMain(K, p, PTLE, PTASIZE, nup, SBASE, SMAX, HBASE, HMAX, HDBASE, UPBASE, SMSIZE, SMUSERPAGE, SMBASE, a, b));

        return sb.toString();

    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("------------C0 Program------------\n");
        sb.append(this.code + "\n");
        sb.append("------------MIPS Code------------\n");
        sb.append(this.mipsCode + "\n");
        sb.append("------------Byte Code------------\n");
        sb.append(this.byteCode + "\n");

        return sb.toString();
    }

    public String getMipsCode(){
        return this.mipsCode;
    }

    public String getCode(){
        return this.code;
    }

    public int[] getByteCode(){
        return this.byteCode;
    }
}
