package com.company;

import java.io.IOException;

public class TypeManager {

    public static final int MAX_NUMBER_OF_FIELDS_IN_A_RECORD = 10;
    public static final int NUMBER_OF_RECORDS_IN_PAGE = 100;
    public static final int RECORD_SIZE = 4 + MAX_NUMBER_OF_FIELDS_IN_A_RECORD * 4;
    public static final int PAGE_SIZE = 4 + 4 + NUMBER_OF_RECORDS_IN_PAGE * RECORD_SIZE;

    FileManager fileManager;
    SystemCatalogManager systemCatalogManager;
    Page page;
    String typeName;
    int pageCount;

    public TypeManager(String aTypeName) throws IOException {
        // TODO If the file exist already, just read the file and load the required pages and config (header).
        systemCatalogManager = new SystemCatalogManager();
        typeName = aTypeName;
        fileManager = new FileManager(typeName + ".t", true);
        pageCount = 1;  // Default one page.
    }

    public void init() throws IOException {
        addPage();
    }

    public void addPage() throws IOException {
        Page emptyPage = new Page(typeName);        // Initialize an empty page.
        writePage(emptyPage,systemCatalogManager.getPageCountOfAType(typeName)-1);
    }

    public void writePage(Page page, int index) throws IOException {
        fileManager.seekToStart();          // Redundant
        fileManager.seek(index*PAGE_SIZE,true);
        fileManager.writeInt(page.numberOfRecords);         // Write number of records.
        fileManager.writeInt(page.numberOfDeletedRecords);  // Write number of deleted records.

        for (int i = 0;i < NUMBER_OF_RECORDS_IN_PAGE; i++){
            fileManager.writeInt(page.isDeleted[i]);        // Write isDeleted flag.
            for (int j = 0;j < MAX_NUMBER_OF_FIELDS_IN_A_RECORD;j++){
                fileManager.writeInt(page.fieldValues[i][j]);
            }
        }
    }
}
