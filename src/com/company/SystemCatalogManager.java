package com.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SystemCatalogManager {

    public static final int FIELD_NAME_LENGTH = 20;
    FileManager fileManager;

    // Header
    int typeCount;
    int numberOfDeletedTypes;

    //Record Fields
    ArrayList<Integer> isDeletedData = new ArrayList<>();
    ArrayList<Integer> pageCounts = new ArrayList<>();
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
                pageCounts.add(fileManager.readInt());
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
    }

    public int getTypeCount(){
        return this.typeCount;
    }

    public void setNumberOfDeletedTypes(int aNumberOfDeletedTypes) throws IOException {
        fileManager.seek(4,true);   // Go to the position of number of deleted types.
        fileManager.writeInt(aNumberOfDeletedTypes);
    }

    public int getNumberOfDeletedTypes(){
        return numberOfDeletedTypes;
    }

    public void addTypeInfo(String name,int fieldNumber,String[] fieldNames) throws IOException {
        if (numberOfDeletedTypes != 0){
            // Find the location to insert the type field.

        }
        fileManager.seekToEnd();
        fileManager.writeInt(0);                    // Not deleted.
        fileManager.writeInt(1);                    // Page Count = 1
        fileManager.writeString(name,20);       // 20 is the fixed length
        fileManager.writeInt(fieldNumber);             // Write the field number
        for (String fieldName : fieldNames){
            fileManager.writeString(fieldName,20);
        }
    }

    public int getPageCountOfAType(String typeName){
        return pageCounts.get(getTypeIndex(typeName));
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

    public int getTypeIndex(String typeName){
        if (typeNames.isEmpty()){
            return -1;
        }
        int i;
        for(i=0;i < typeNames.size();i++){
            if (typeNames.get(i).equals(typeName))
                return i;
        }
        return -1;
    }
}
