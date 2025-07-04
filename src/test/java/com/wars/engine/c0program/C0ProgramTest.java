package com.wars.engine.c0program;

import com.wars.engine.simulator.Configuration;
import com.wars.engine.simulator.Simulator;
import com.wars.engine.util.CodeTranslation;
import com.wars.engine.util.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class C0ProgramTest {

    private Configuration config;

    @BeforeEach
    void setUp() {
        config = new Configuration();
    }

    @Test
    void test_trivial_program() {
        // arrange
        String code = "int main(){asm( addi 2 0 3 ); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        assertEquals(1, config.getRegister(1));
        assertEquals(3, config.getRegister(2));
    }

    @Test
    void test_single_int_max_constant_program() {
        // arrange
        String code = "int a; int main(){a = 2147483647; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 2147483647;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_int_min_constant_program() {
        // arrange
        String code = "int a; int main(){a = -2147483647; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        // Comment: -2147483648 cannot generate
        int expectedValue = -2147483647;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_uint_max_constant_program() {
        // arrange
        String code = "uint a; int main(){a = 4294967295u; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long expectedValue = 4294967295L;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_uint_min_constant_program() {
        // arrange
        String code = "uint a; int main(){a = 0u; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long expectedValue = 0L;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_uint_constant_program() {
        // arrange
        String code = "uint a; int main(){a = 123123u; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long expectedValue = 123123L;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_char_constant_program() {
        // arrange
        String code = "uint a; char b; int main(){a = 1u; b = !; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = '!';
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE + 4));
    }

    @Test
    void test_single_boolean_true_constant_program() {
        // arrange
        String code = "char a; int b; bool c; int main(){a = *; c = true; b = -12; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE + 8));
        assertEquals(-12, config.getWord(pr.SBASE + 4));
        assertEquals('*', config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_false_constant_program() {
        // arrange
        String code = "char a; int b; bool c; int main(){a = *; c = false; b = -197; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE + 8));
        assertEquals(-197, config.getWord(pr.SBASE + 4));
        assertEquals('*', config.getWord(pr.SBASE));
    }

    @Test
    void test_single_int_arithmetic_add() {
        // arrange
        String code = "int a; int b; int c; int main(){a = 2147483647; b = -2147483647; c = a+b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE + 8));
        assertEquals(-(1 << 31) + 1, config.getWord(pr.SBASE + 4));
        assertEquals((1 << 31) - 1, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_int_arithmetic_sub() {
        // arrange
        String code = "int a; int b; int c; int main(){a = 2147483647; b = -2147483647; c = a-b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 2 * ((1 << 31) - 1);
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE + 8));
        assertEquals(-(1 << 31) + 1, config.getWord(pr.SBASE + 4));
        assertEquals((1 << 31) - 1, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_uint_arithmetic_add() {
        // arrange
        String code = "uint c; int main(){uint a; uint b; a = 4294967294u; b = 10u; c = a+b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long expectedValue = 8L;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_uint_arithmetic_sub() {
        // arrange
        String code = "uint c; int main(){uint a; uint b; a = 8u; b = 10u; c = a-b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long expectedValue = 4294967294L;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_uint_arithmetic_mul() {
        // arrange
        String code = "uint c; int main(){uint a; uint b; a = 8u; b = 10u; c = a*b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long expectedValue = 8L * 10L;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_uint_arithmetic_mul_overflow() {
        // arrange
        String code = "uint c; int main(){uint a; uint b; a = 2147483647u; b = 12312312u; c = a*b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long unsignedMax = 4294967296L;
        long expectedValue = (2147483647L * 12312312L) % unsignedMax;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_uint_arithmetic_div() {
        // arrange
        String code = "uint c; int main(){uint a; uint b; a = 12312312u; b = 1000u; c = a/b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long unsignedMax = 4294967296L;
        long expectedValue = (12312312L / 1000L) % unsignedMax;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_uint_arithmetic_div_max() {
        // arrange
        String code = "uint c; int main(){uint a; uint b; a = 4294967295u; b = 1u; c = a/b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long unsignedMax = 4294967296L;
        long expectedValue = (4294967295L / 1L) % unsignedMax;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_uint_arithmetic_div_zero() {
        // arrange
        String code = "uint c; int main(){uint a; uint b; a = 4294967294u; b = 4294967295u; c = a/b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long unsignedMax = 4294967296L;
        long expectedValue = (4294967294L / 4294967295L) % unsignedMax;
        int realValue = config.getWord(pr.SBASE);
        long realLongValue = realValue & 0xFFFFFFFFL;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, realLongValue);
    }

    @Test
    void test_single_int_arithmetic_div_max_negative() {
        // arrange
        String code = "int c; int main(){int a; int b; a = -2147483647; b = 1; c = a/b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = (-2147483647 / 1);
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_int_arithmetic_div_max_positive() {
        // arrange
        String code = "int c; int main(){int a; int b; a = 2147483647; b = 1; c = a/b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = (2147483647 / 1);
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_int_arithmetic_div_max() {
        // arrange
        String code = "int c; int main(){int a; int b; a = -2147483647; b = -1; c = a/b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = (-2147483647 / -1);
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_int_arithmetic_div_min() {
        // arrange
        String code = "int c; int main(){int a; int b; a = 2147483647; b = -1; c = a/b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = (2147483647 / -1);
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_int_arithmetic_div_random() {
        // arrange
        String code = "int c; int main(){int a; int b; a = 2147483646; b = -123; c = a/b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = (2147483646 / -123);
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_int_arithmetic_random() {
        // arrange
        String code = "int c; int a; int main(){int b; b = 14; a = 2*b+3; c = ((a+b)-2)/5; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int b = 14;
        int a = 2 * b + 3;
        int expectedValue = ((a + b) - 2) / 5;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
        assertEquals(a, config.getWord(pr.SBASE + 4));
    }

    @Test
    void test_single_int_arithmetic_random_all() {
        // arrange
        String code = "int a; int b; int c; int main(){a = 123123; b = a*a; c = (b+2*a+1)/(a+1); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int a = 123123;
        int b = a * a;
        int expectedValue = (b + 2 * a + 1) / (a + 1);
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE + 8));
        assertEquals(a, config.getWord(pr.SBASE));
        assertEquals(b, config.getWord(pr.SBASE + 4));
    }

    @Test
    void test_single_uint_arithmetic_random_all() {
        // arrange
        String code = "uint a; uint b; uint c; int main(){a = 123123122u; b = a*a; c = (b+2u*a+1u)/(a+1u); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long unsignedMax = 4294967296L;
        long a = 123123122;
        long b = a * a % unsignedMax;
        long expectedValue = (b + 2 * a + 1) / (a + 1) % unsignedMax;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE + 8));
        assertEquals(a, config.getWord(pr.SBASE));
        assertEquals(b, config.getWord(pr.SBASE + 4));
    }

    @Test
    void test_single_boolean_arithmetic_geq() {
        // arrange
        String code = "bool c; int main(){uint a; uint b; a = 4294967295u; b = 1u; c = a>=b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long a = 4294967295L;
        long b = 1;
        int expectedValue = (a >= b) ? 1 : 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_ge() {
        // arrange
        String code = "bool c; int main(){uint a; uint b; a = 2147483647u; b = 4294967295u; c = a>b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        long a = 2147483647L;
        long b = 4294967295L;
        int expectedValue = (a > b) ? 1 : 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_eq() {
        // arrange
        String code = "bool c; int main(){int a; int b; a = 2147483647; b = -2147483647; a = a+2; c = a==b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int a = 2147483647;
        int b = -2147483647;
        int expectedValue = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_leq() {
        // arrange
        String code = "bool c; int main(){int a; int b; a = -2147483647; b = 2147483647; c=a<=b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int a = -2147483647;
        int b = 2147483647;
        int expectedValue = (a <= b) ? 1 : 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_le() {
        // arrange
        String code = "bool c; int main(){int a; int b; a = 2147483647; b = 2147483647; c=a<b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int a = 2147483647;
        int b = 2147483647;
        int expectedValue = (a < b) ? 1 : 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_neg() {
        // arrange
        String code = "bool c; bool neg(bool a){bool c; c = !(bool)a; return c}; int main(){c = neg(false); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_or() {
        // arrange
        String code = "bool c; bool or(bool a, bool b){bool c; c = (bool)a||(bool)b; return c}; int main(){c = or(false, true); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_and() {
        // arrange
        String code = "bool c; bool and(bool a, bool b){bool c; c = (bool)a&&(bool)b; return c}; int main(){c = and(true, false); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_and_with_neg() {
        // arrange
        String code = "bool c; bool and(bool a, bool b){bool c; bool d; a = neg(a); b = neg(b); c = or(a, b); c = neg(c); return c}; bool or(bool a, bool b){bool c; c = (bool)a||(bool)b; return c}; bool neg(bool a){bool c; c =!(bool)a; return c}; int main(){c = and(true, true); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_or_with_neg() {
        // arrange
        String code = "bool c; bool neg(bool a){bool c; c =!(bool)a; return c}; bool or(bool a, bool b){bool c; bool d; a = neg(a); b = neg(b); c = and(a, b); c = neg(c); return c}; bool and(bool a, bool b){bool c; c = (bool)a&&(bool)b; return c}; int main(){c = or(false, false); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_ptr_arithmetic_eq() {
        // arrange
        String code = "typedef struct {int val} signed; typedef signed' ptr; bool c; ptr a; ptr b; bool eq(ptr a, ptr b){bool c; c = a==b; return c}; int main(){a=new signed'; b=a'&; a'.val=3; c = eq(a, b); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_ptr_arithmetic_neq() {
        // arrange
        String code = "typedef struct {int val} signed; typedef signed' ptr; bool c; void neq(ptr a, ptr b){c = a!=b}; int main(){ptr a; ptr b; a=new signed'; b=a; neq(a, b); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_boolean_arithmetic_random() {
        // arrange
        String code = "bool ipf; int main(){bool ptle; uint IL; IL = 20u; ptle=false; ipf = (IL==17u||IL==20u)&&!(bool)ptle; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_array_initialize() {
        // arrange
        String code = "typedef int[3] arr; arr a; int main(){int b; b =-42; a[2]=(b + 1); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedValue = -42 + 1;
        int index = 2;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedValue, config.getWord(pr.SBASE + index * 4));
    }

    @Test
    void test_single_while_array_initialize() {
        // arrange
        String code = "typedef int[4] arr; arr a; int main(){int i; i = 0; while i<4 {a[i]=i; i=i+1}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int size = 4;
        assertEquals(1, config.getRegister(1));
        for (int i = 0; i < 4; i++) {
            assertEquals(i, config.getWord(pr.SBASE + 4 * i));
        }
    }

    @Test
    void test_single_while_statement_macro_mul() {
        // arrange
        String code = "int a; int b; int main(){a = 1; b = 5; while b!=0 {a = a*3; b = b-1}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = 243;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_while_statement_macro_div() {
        // arrange
        String code = "int a; int b; int main(){a = 243*3; b = 5; while b!=0 {a = a/3; b = b-1}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = 3;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_while_statement_gcd() {
        // arrange
        String code = "int c; int main(){int a; int b; a = 12; b = 16; if a<b {c=b; b=a; a=c}; while b!=0 {c = a/b; a = a-c*b; c = b; b = a; a = c}; c = a; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = 4;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_if_statement_slt() {
        // arrange
        String code = "bool res; int a; int b; int main(){res = true; a = 12; b = 13; if a>=b {res=false}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = (12 < 13) ? 1 : 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_if_statement_macros() {
        // arrange
        String code = "bool res; int a; int b; int main(){res = true; a = 13; b = 12; if a/b>=1 {res=false}; a = a / b; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = (13 / 12 > 1) ? 1 : 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
        assertEquals(1, config.getWord(pr.SBASE + 4));
    }

    @Test
    void test_single_if_else_statement_max() {
        // arrange
        String code = "int mx; int main(){int a; int b; a = -2147483647; b = 1; if a>b {mx=a} else {mx=b}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_if_else_statement_min() {
        // arrange
        String code = "int mn; int main(){int a; int b; a = -2147483647; b = 1; if a<b {mn=a} else {mn=b}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = -2147483647;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_ptr() {
        // arrange
        String code = "typedef int' psigned; psigned ptr; int a; int b; int main(){b = -2147483647; ptr = b&; a = ptr'; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = -2147483647;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE + 4));
    }

    @Test
    void test_single_struct() {
        // arrange
        String code = "typedef struct {int exp; int val} powst; typedef powst' ptr; int x; int main(){x = pow(2, 10); return 1}; int pow(int base, int n){ptr p; p = new powst'; p'.exp = 0; p'.val = 1; while p'.exp<n {p'.exp = p'.exp+1; p'.val=p'.val*base}; return p'.val}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = (1 << 10);
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_single_digit_sum() {
        // arrange
        String code = "int res; int a; int main(){a = 15; res = 0; while a>0 {res = res+(a-(a/10*10)); a = a/10}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = 6;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_double_simple() {
        // arrange
        String code = "int a; int foo(int a){int res; while a>0 {res = res+(a-a/10*10); a = a/10}; return res}; int main(){int b; b = 15; a = foo(b); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = 6;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_rec_fun_pow() {
        // arrange
        String code = "int res; int pow(int a, int n){int res; if n==0 {res = 1} else {n = n-1; res = pow(a, n); res = res*a}; return res}; int main(){int a; int n; a = 2; n = 3; res = pow(a, n); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int expectedResult = 8;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_store_in_gpr_store_plain() {
        // arrange
        String code = "void store(int a){gpr(24) = a}; int main(){int a; a = 2; store(a); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int a = 2;
        assertEquals(1, config.getRegister(1));
        assertEquals(a, config.getRegister(24));
    }

    @Test
    void test_store_in_gpr_read_plain() {
        // arrange
        String code = "int b; void read(int a){gpr(24) = a; b = gpr(24)}; int main(){int a; a = 2; read(a); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int b = 2;
        assertEquals(1, config.getRegister(1));
        assertEquals(b, config.getWord(pr.SBASE));
    }

    @Test
    void test_store_in_gpr_store_read() {
        // arrange
        String code = "int b; void store(int a){gpr(1) = a; b = gpr(1)}; int main(){int a; a = 2147483647; store(a); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int b = 2147483647;
        assertEquals(1, config.getRegister(1));
        assertEquals(b, config.getWord(pr.SBASE));
    }

    @Test
    void test_store_in_gpr_store() {
        // arrange
        String code = "int b; void store(int a){gpr(1) = a; gpr(2) = b {1}; b = gpr(1)}; int main(){int a; a = 2147483647; store(a); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int b = 2147483647;
        assertEquals(1, config.getRegister(1));
        assertEquals(b, config.getWord(pr.SBASE));
    }

    @Test
    void test_store_in_gpr_swap() {
        // arrange
        String code = "int a; int b; void swap(){gpr(1) = a; gpr(2) = b {1}; b = gpr(1) {2}; a = gpr(2)}; int main(){a = 1; b = -1; swap(); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int a = -1;
        int b = 1;
        assertEquals(1, config.getRegister(1));
        assertEquals(a, config.getWord(pr.SBASE));
        assertEquals(b, config.getWord(pr.SBASE + 4));
    }

    @Test
    void test_store_in_gpr_swap_uint_int() {
        // arrange
        String code = "uint a; int b; void set(uint a){gpr(2) = a {1}; b = gpr(2)}; int main(){a = 4294967295u; set(a); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int b = -1;
        assertEquals(1, config.getRegister(1));
        assertEquals(b, config.getWord(pr.SBASE + 4));
    }

    @Test
    void test_inline_asm_add() {
        // arrange
        String code = "int a; int inc(int val){gpr(1) = val; asm( addi 1 1 1 ); val = gpr(1); return val}; int main(){int b; b = 2147483647; a = inc(b); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int a = -2147483648;
        assertEquals(1, config.getRegister(1));
        assertEquals(a, config.getWord(pr.SBASE));
    }

    @Test
    void test_inline_asm_div() {
        // arrange
        String code = "int res; int div(int a, int b){int val; gpr(1) = a; gpr(2) = b {1}; asm( macro: divt(3, 1, 2) ); val = gpr(3); return val}; int main(){int a; int b; a = 2147483647; b = 1; res = div(a, b); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int res = 2147483647;
        assertEquals(1, config.getRegister(1));
        assertEquals(res, config.getWord(pr.SBASE));
    }

    @Test
    void test_inline_asm_uint_div() {
        // arrange
        String code = "int res; int div(uint a, uint b){int val; if b!=0u {gpr(1) = a; gpr(2) = b {1}; asm( macro: divu(3, 1, 2) ); val = gpr(3)} else {res = 0}; return val}; int main(){uint a; uint b; a = 2147483648u; b = 1u; res = div(a, b); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int res = -2147483648;
        assertEquals(1, config.getRegister(1));
        assertEquals(res, config.getWord(pr.SBASE));
    }

    @Test
    void test_inline_asm_uint_div_zero() {
        // arrange
        String code = "int res; int div(uint a, uint b){int val; if b!=0u {gpr(1) = a; gpr(2) = b {1}; asm( macro: divu(3, 1, 2) ); val = gpr(3)} else {res = 0}; return val}; int main(){uint a; uint b; a = 2147483648u; b = 0u; res = div(a, b); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int res = 0;
        assertEquals(1, config.getRegister(1));
        assertEquals(res, config.getWord(pr.SBASE));
    }

    @Test
    void test_disk_readms() {
        // arrange
        String code = "int val; int readms(uint a){int tmp; gpr(1) = a; asm( lw 2 1 0 ); asm( sw 2 29 -4 ); return tmp}; int main(){uint a; a = 4100u; val = readms(a); return 1}~";
        C0Program pr = new C0Program(code);
        int[] byteCode = CodeTranslation.MIPSTranslation(pr.mipsCode);
        config.setWordArray(byteCode, 0);
        int address = 4100;
        int expectedResult = -2147483647;
        config.setWord(address, expectedResult);

        // act
        Simulator.simulate(config);

        // assert
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_disk_writems() {
        // arrange
        String code = "uint val; void writems(uint x, uint a){gpr(1) = x; gpr(2) = a {1}; asm( sw 1 2 0 )}; int main(){uint a; a = 4100u; val = 22u; writems(val, a); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int address = 4100;
        int expectedResult = 22;
        assertEquals(1, config.getRegister(1));
        assertEquals(expectedResult, config.getWord(address));
    }

    @Test
    void test_disk_writems_array() {
        // arrange
        String writems = "void writems(uint x, uint a){gpr(1) = x; gpr(2) = a {1}; asm( sw 1 2 0 )};";
        String code = "typedef int[2] arr; typedef int' ptr; arr ar; ptr addr;" + " " + writems + " int main(){uint a; uint x; int n; x = 10u; n = 2; addr = ar[0]&; gpr(1) = addr; a = gpr(1); while n>0 {writems(x, a); x = x+1u; a = a+4u; n = n-1}; gpr(10) = ar[1]; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        int address = pr.SBASE;
        int expectedResult = 10;
        int n = 2;
        assertEquals(1, config.getRegister(1));
        while (n > 0) {
            assertEquals(expectedResult, config.getWord(address));
            address += 4;
            expectedResult += 1;
            n--;
        }
    }

    @Test
    void test_disk_copyms() {
        // arrange
        String code = "uint val; void writems(uint x, uint a){gpr(1) = x; gpr(2) = a {1}; asm( sw 1 2 0 )}; void copyms(uint a, uint b, uint L){gpr(1) = a; gpr(2) = b {1}; gpr(3) = L {1, 2}; asm( blez 3 7 ); asm( lw 4 1 0 ); asm( sw 4 2 0 ); asm( addi 1 1 4 ); asm( addi 2 2 4 ); asm( addi 3 3 -1 ); asm( blez 0 -6 )}; " +
                "int main(){uint a; int n; n = 2; a = 4100u; val = 22u; while n>0 {writems(val, a); val=val+1u; a=a+4u; n=n-1}; copyms(4100u, 4200u, 5u); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        assertEquals(1, config.getRegister(1));
        int address = 4200;
        int expectedResult = 22;
        int n = 2;
        while (n > 0) {
            assertEquals(expectedResult, config.getWord(address));
            address += 4;
            expectedResult += 1;
            n--;
        }
    }

    @Test
    void test_array_twos_power() {
        // arrange
        String code = "typedef int[32] arr; arr ar; int main(){int b; int x; b = 0; x = 1; while b<32 {ar[b] = x; x = x*2; b=b+1}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        assertEquals(1, config.getRegister(1));
        int address = pr.SBASE;
        int expectedResult = 1;
        int n = 32;
        while (n > 0) {
            assertEquals(expectedResult, config.getWord(address));
            address += 4;
            expectedResult *= 2;
            n--;
        }
    }

    @Test
    void test_ptr_with_gprs() {
        // arrange
        String code = "typedef uint' ptrunsigned; typedef uint[32] u; typedef uint[8] v; typedef struct {u GPR; v SPR} pcb; typedef pcb[3] PCBt; int a; PCBt PCB; int main(){PCB[0].GPR[2] = 2u; gpr(10) = PCB[0].GPR[2]&; asm( lw 10 10 0 ); a = gpr(10); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        assertEquals(1, config.getRegister(1));
        int expectedResult = 2;
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_gcd() {
        // arrange
        String code = "int res; int gcd(int a, int b){int res; if a<b {b=a+b; b=b-a; a=b-a}; if b==0 {res=a} else {res = gcd(b, a-a/b*b)}; return res}; int main(){int a; int b; a = 30; b = 10; res = gcd(a, b); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        assertEquals(1, config.getRegister(1));
        int expectedResult = 10;
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_struct_init() {
        // arrange
        String code = "typedef struct {int f; int s} p; typedef struct {p pp; int s} ppp; ppp q; int main(){int a; a = 2147483647; gpr(1) = a; q.pp.f = gpr(1); return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        assertEquals(1, config.getRegister(1));
        int expectedResult = 2147483647;
        assertEquals(expectedResult, config.getWord(pr.SBASE));
    }

    @Test
    void test_struct_dte() {
        // arrange
        String code = "typedef LEL' u; typedef struct {int content; u next} LEL; u first; u last; int n; int main(){n = 200; first = new LEL'; last = first; n = n-1; while n>0 {last'.content = n; last'.next = new LEL'; last = last'.next; n = n-1}; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        assertEquals(1, config.getRegister(1));
        int address = pr.HBASE;
        int expectedResult = 199;
        int n = 199;
        while (n > 0) {
            assertEquals(expectedResult, config.getWord(address));
            expectedResult -= 2;
            n -= 2;
            address += 8;
        }
    }

    @Test
    void test_ptr_dte() {
        // arrange
        String code = "typedef struct {int f; int s} str; typedef str' ptr; ptr a; int main(){a = new str'; a'.s = 10; return 1}~";
        C0Program pr = new C0Program(code);

        // act
        simulateProgram(pr);

        // assert
        assertEquals(1, config.getRegister(1));
        int address = pr.HBASE + 4;
        int expectedResult = 10;
        assertEquals(expectedResult, config.getWord(address));
    }

    @Test
    void test_abstract_kernel() {
        C0Program kernel = AbstractKernel.generateAbstractKernel();

        Log.info("Generated mips code of lines " + kernel.getMipsCode().split("\n").length);

        int[] byteCode = CodeTranslation.MIPSTranslation(kernel.getMipsCode());
        System.out.println("Number of instruction " + byteCode.length);
        config.setWordArray(byteCode, 0);
    }

    private void simulateProgram(C0Program pr) {
        int[] byteCode = CodeTranslation.MIPSTranslation(pr.getMipsCode());
        config.setWordArray(byteCode, 0);
        Simulator.simulate(config);
    }

}
