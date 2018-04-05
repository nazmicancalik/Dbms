package com.company;

import java.io.IOException;
import java.util.ArrayList;

public class SystemCatalogManager {

    FileManager fileManager;
    int typeCount;
    ArrayList<String> typeNames;

    public SystemCatalogManager() throws IOException {
        fileManager = new FileManager("syscat.ctg",true);
        typeCount = fileManager.readInt();
        typeNames = new ArrayList<>(typeCount);
    }

    public void increaseTypeCount() throws IOException {
        typeCount++;
        fileManager.seekToStart();
        fileManager.writeInt(typeCount);
    }

    public void initTypeNames() throws IOException {
        for (int i = 0;i<typeCount;i++){
            this.typeNames.add(fileManager.readString(20));
            System.out.println(typeNames.get(0));
        }
    }
    public void addTypeInfo(String name,int fieldNumber,String[] fieldNames) throws IOException {
        fileManager.seekToEnd();
        fileManager.writeString(name,20); // 20 is the fixed length
        fileManager.writeInt(fieldNumber); // Write the field number
        for (String fieldName : fieldNames){
            fileManager.writeString(fieldName,20);
        }
    }

    public boolean checkTypeExists(String typeToCheck){
        for(String typeName: typeNames){
            if (typeName.equals(typeToCheck)){
                return true;
            }
        }
        return false;
    }
}
