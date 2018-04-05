package com.company;

public class TypeManager {

    FileManager fileManager;

    public TypeManager(String typeName){
        fileManager = new FileManager(typeName,true);
    }
}
