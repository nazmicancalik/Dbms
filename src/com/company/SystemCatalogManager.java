package com.company;

import java.io.IOException;

public class SystemCatalogManager {

    FileManager fileManager;
    int typeCount;

    public SystemCatalogManager() throws IOException {
        fileManager = new FileManager("syscat.ctg",true);
        typeCount = fileManager.readInt();
    }

    public void increaseTypeCount() throws IOException {
        typeCount++;
        fileManager.seekToStart();
        fileManager.writeInt(typeCount);
    }

    public void addTypeInfo(String name,int fieldNumber,String[] fieldNames) throws IOException {
        fileManager.seekToEnd();
        fileManager.writeString(name,20); // 20 is the fixed length
        fileManager.writeInt(fieldNumber); // Write the field number
        for (String fieldName : fieldNames){
            fileManager.writeString(fieldName,20);
        }
    }
}
