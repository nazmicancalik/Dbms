package com.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SystemCatalogManager {

    public static final int NUMBER_OF_RECORDS_IN_A_PAGE = 100;
    public static final int MAX_NUMBER_OF_FIELDS_IN_A_RECORD = 10;
    public static final int NUMBER_OF_EMPTY_PAGES_AT_START = 3;
    public static final int PAGE_SIZE = 10*228;

    FileManager fileManager;
    int typeCount;
    int pageCount;
    int[] isDeletedData;
    String[] typeNames;
    int[] fieldCounts;
    Map<String,String[]> fieldNames;

    public SystemCatalogManager() throws IOException {
        File f = new File("syscat.ctg");
        boolean catalogExists = f.exists();
        fileManager = new FileManager("syscat.ctg",true);
        if(!catalogExists){
            fillSystemCatalogFile();
        }

        typeCount = fileManager.readInt();
        pageCount = fileManager.readInt();
        typeNames = new String[0];
    }

    private void fillSystemCatalogFile() throws IOException {
        fileManager.seekToStart();
        fileManager.writeInt(0);    // Number of types.
        fileManager.writeInt(NUMBER_OF_EMPTY_PAGES_AT_START);    // Number of pages.
        for(int i = 0;i < NUMBER_OF_EMPTY_PAGES_AT_START;i++){
            addEmptyPage();
        }
    }

    private void addEmptyPage() throws IOException {
        fileManager.seekToEnd();
        fileManager.writeInt(0);    // TODO Number of empty records.
        for(int i = 0;i < NUMBER_OF_RECORDS_IN_A_PAGE;i++){
            fileManager.writeInt(0);    // Is deleted : no
            fileManager.writeString("",20);     // Type Name
            fileManager.writeInt(0);    // Number of fields.
            for(int j = 0; j < MAX_NUMBER_OF_FIELDS_IN_A_RECORD;j++){
                fileManager.writeString("",20);
            }
        }
    }

    public void getPage(int pageIndex) throws IOException {
        fileManager.seek(8+pageIndex*PAGE_SIZE,true);
        fieldNames = new HashMap<>();
        for(int i = 0; i < NUMBER_OF_RECORDS_IN_A_PAGE;i++){
            isDeletedData[i] = fileManager.readInt();
            typeNames[i] = fileManager.readString(20).trim();
            fieldCounts[i] = fileManager.readInt();
            String[] fieldNamesTemp = new String[fieldCounts[i]];
            for (int j = 0;j < fieldCounts[i];j++){
                String currentFieldName = fileManager.readString(20).trim();
                if (currentFieldName.length()!=0){
                    fieldNamesTemp[i] = currentFieldName;
                }
            }
            fieldNames.put(typeNames[i],fieldNamesTemp);
        }
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

    public int getFieldNumberOfAType(String typeName) throws IOException {
        fileManager.seek(4,true);
        String currentTypeName = fileManager.readString(20);
        while(currentTypeName != typeName){
            int fieldCount = fileManager.readInt();
            fileManager.seek(fieldCount*20,false);
            currentTypeName = fileManager.readString(20);
        }

        // Return the number of fields for the given type.
        return fileManager.readInt();
    }
}
