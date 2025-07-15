package com.wars.compiler.exceptions.memory;

public class MemoryStructAlreadyExistsException extends MemoryStructException {
    public MemoryStructAlreadyExistsException(String name) {
        super("Memory struct with name " + name + " already exists!");
    }
}
