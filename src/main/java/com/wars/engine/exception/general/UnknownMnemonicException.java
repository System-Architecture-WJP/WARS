package com.wars.engine.exception.general;

public class UnknownMnemonicException extends RuntimeException {
    public UnknownMnemonicException(String unknownMnemonic, String possibleMnemonics) {
        super("Unknown instruction: " + unknownMnemonic + ", Possible mnemonics: " + possibleMnemonics);
    }
}
