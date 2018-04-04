package com.company;

import java.io.IOException;

public class SystemCatalogManager {

    FileManager fileManager;
    int typeCount;
    public SystemCatalogManager() throws IOException {
        fileManager = new FileManager("syscat.ctg",true);
        fileManager.seekToStart();
    }

    public void increaseTypeCount() throws IOException {
        fileManager.seekToStart();
        typeCount = fileManager.readShort();
        typeCount++;
        fileManager.writeShort(this.typeCount);
        fileManager.seekToStart();
        System.out.println(fileManager.readShort());
    }
}
