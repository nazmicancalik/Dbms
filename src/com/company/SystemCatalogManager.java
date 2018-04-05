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
        fileManager.seekToStart();
        System.out.println(fileManager.readInt());
    }
}
