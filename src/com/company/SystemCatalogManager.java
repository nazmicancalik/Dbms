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
    public static final int FIELD_NAME_LENGTH = 20;

    FileManager fileManager;

    // Header
    int typeCount;
    int numberOfDeletedTypes;
/*
    //Record Fields
    int[] isDeletedData;
    String[] typeNames;
    int[] fieldCounts;
    Map<String,String[]> fieldNames;
*/
    //Record Fields
    ArrayList<Integer> isDeletedData = new ArrayList<>();
    ArrayList<String> typeNames = new ArrayList<>();
    ArrayList<Integer> fieldCounts = new ArrayList<>();
    Map<String,String[]> fieldNames;

    public SystemCatalogManager() throws IOException {
        File f = new File("syscat.ctg");
        boolean catalogExists = f.exists();
        fileManager = new FileManager("syscat.ctg",true);
        if(!catalogExists){
            System.out.println("New Database is created. Initializing System Catalog...");
            fillSystemCatalogFile();
        }

        init();
    }


    public void init() throws IOException {

        // Read Header
        fileManager.seekToStart();
        typeCount = fileManager.readInt();
        numberOfDeletedTypes = fileManager.readInt();
        fieldNames = new HashMap<>();

        // Start to read Records if there is any.
        if(typeCount != 0){
            for(int i = 0;i < typeCount;i++){
                isDeletedData.add(fileManager.readInt());                      // Read isdeleted data.
                typeNames.add(fileManager.readString(FIELD_NAME_LENGTH).trim());   // Read type name.
                fieldCounts.add(fileManager.readInt());                     // Read field count of that type.
                String[] tempFieldNames = new String[fieldCounts.get(i)];
                for(int j = 0; j < fieldCounts.get(i); j++){
                    tempFieldNames[j] = fileManager.readString(FIELD_NAME_LENGTH).trim();
                }
                fieldNames.put(typeNames.get(i),tempFieldNames);
            }
        }
    }

    private void fillSystemCatalogFile() throws IOException {
        fileManager.seekToStart();
        fileManager.writeInt(0);    // Number of types.
        fileManager.writeInt(0);    // Number of deleted types.
    }

    public void setTypeCount(int aTypeCount) throws IOException {
        fileManager.seekToStart();
        fileManager.writeInt(aTypeCount);
        System.out.println("TYPE COUNT IS : " + typeCount);
    }

    public int getTypeCount(){
        return this.typeCount;
    }

    public void setNumberOfDeletedTypes(int aNumberOfDeletedTypes) throws IOException {
        fileManager.seek(4,true);   // Go to the position of number of deleted types.
        fileManager.writeInt(aNumberOfDeletedTypes);
        System.out.println("NUMBER OF DELETED TYPES IS : " + numberOfDeletedTypes);
    }

    public int getNumberOfDeletedTypes(){
        return numberOfDeletedTypes;
    }

    public void addTypeInfo(String name,int fieldNumber,String[] fieldNames) throws IOException {
        if (numberOfDeletedTypes != 0){
            // Find the location to insert the type field.

        }
        fileManager.seekToEnd();
        fileManager.writeInt(0);                // Not deleted.
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
            currentTypeName = fileManager.readString(20).trim();
        }

        // Return the number of fields for the given type.
        return fileManager.readInt();
    }
}
