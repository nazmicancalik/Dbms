package com.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SystemCatalogManager {

    public static final int FIELD_NAME_LENGTH = 20;
    public static final String SYSTEM_CATALOG_NAME = "syscat.ctg";
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
        fileManager = new FileManager(SYSTEM_CATALOG_NAME,true);
        if(!catalogExists){
            System.out.println("New Database is created. Initializing System Catalog...");
            fillSystemCatalogFile();
        }

        init();
    }


    public void init() throws IOException {

        // Read Header
        FileManager inFm = new FileManager(SYSTEM_CATALOG_NAME,false);
        inFm.seekToStart();
        typeCount = inFm.readInt();
        numberOfDeletedTypes = inFm.readInt();
        fieldNames = new HashMap<>();

        // Start to read Records if there is any.
        if(typeCount != 0){
            for(int i = 0;i < typeCount;i++){
                isDeletedData.add(inFm.readInt());                      // Read isdeleted data.
                pageCounts.add(inFm.readInt());
                typeNames.add(inFm.readString(FIELD_NAME_LENGTH).trim());   // Read type name.
                fieldCounts.add(inFm.readInt());                     // Read field count of that type.
                String[] tempFieldNames = new String[fieldCounts.get(i)];
                for(int j = 0; j < fieldCounts.get(i); j++){
                    tempFieldNames[j] = inFm.readString(FIELD_NAME_LENGTH).trim();
                }
                fieldNames.put(typeNames.get(i),tempFieldNames);
            }
        }
        inFm.close();
    }

    private void fillSystemCatalogFile() throws IOException {
        FileManager inFm = new FileManager(SYSTEM_CATALOG_NAME,true);
        inFm.seekToStart();
        inFm.writeInt(0);    // Number of types.
        inFm.writeInt(0);    // Number of deleted types.
        inFm.close();
    }

    public void setTypeCount(int aTypeCount) throws IOException {
        FileManager inFm = new FileManager(SYSTEM_CATALOG_NAME,true);
        inFm.seekToStart();
        inFm.writeInt(aTypeCount);
        inFm.close();
    }

    public int getTypeCount(){
        return this.typeCount;
    }

    public void setNumberOfDeletedTypes(int aNumberOfDeletedTypes) throws IOException {
        FileManager inFm = new FileManager(SYSTEM_CATALOG_NAME,true);
        inFm.seek(4,true);   // Go to the position of number of deleted types.
        inFm.writeInt(aNumberOfDeletedTypes);
        inFm.close();
    }

    public int getNumberOfDeletedTypes(){
        return numberOfDeletedTypes;
    }

    public void addType(String name,int fieldCount,String[] aFieldNames) throws IOException {
        int indexToInsert = getDeletedSpaceIndex();
        if (indexToInsert == -1){
            this.typeCount++;
            // Add to the last place of records.
            isDeletedData.add(0);                   // Not deleted.
            pageCounts.add(1);                      // Page Count = 0
            typeNames.add(name);                    // Type name
            fieldCounts.add(fieldCount);            // Field Count
            this.fieldNames.put(name,aFieldNames);   // Add field names.
        }else{
            //Add to the removed ones place.
            String typeToReplace = typeNames.get(indexToInsert);
            isDeletedData.set(indexToInsert,0);                     // Not deleted.
            pageCounts.set(indexToInsert,1);                     // Page Count = 0
            typeNames.set(indexToInsert,name);                      // Type name
            fieldCounts.set(indexToInsert,fieldCount);              // Field Count
            this.fieldNames.put(name,aFieldNames);                   // Add field names.

            // Delete the old record from the map if their names different of course.
            // Otherwise it deletes the new added type which has different fields however same name with the deleted one.
            if(!typeToReplace.equals(name)){
                this.fieldNames.remove(typeToReplace);
            }
        }
        this.update();
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

    public void setPageCountOfAType(String typeName,int pageCount) throws IOException {
        int index = getTypeIndex(typeName);
        pageCounts.set(index,pageCount);
        this.update();
    }

    public int getPageCountOfAType(String typeName){
        int index = getTypeIndex(typeName);
        return pageCounts.get(index);
    }

    public int getDeletedSpaceIndex(){
        for(int i = 0;i<isDeletedData.size();i++){
            if (isDeletedData.get(i) == 1){
                return i;
            }
        }
        return -1;
    }
    public int getTypeIndex(String typeName){
        int i;
        for(i=0;i < typeNames.size();i++){
            if (typeNames.get(i).equals(typeName))
                return i;
        }
        return -1;
    }

    public void update() throws IOException {
        FileManager inFm = new FileManager(SYSTEM_CATALOG_NAME,true);
        inFm.seekToStart();
        inFm.writeInt(typeCount);
        inFm.writeInt(numberOfDeletedTypes);
        for(int i = 0;i < typeNames.size();i++){
            inFm.writeInt(isDeletedData.get(i));               // isDeleted flag
            inFm.writeInt(pageCounts.get(i));                  // page Count
            inFm.writeString(typeNames.get(i),20);      //  Type name
            inFm.writeInt(fieldCounts.get(i));                 // Field Count

            String[] fields = fieldNames.get(typeNames.get(i));         // Get the field names of the current type.
            for (int j = 0; j<fields.length;j++){
                if(fields[j].equals("")){                              // For skipping the empty field names.
                    break;
                }
                inFm.writeString(fields[j],20);           // Write the field name.
            }
        }
        inFm.close();
    }
}
