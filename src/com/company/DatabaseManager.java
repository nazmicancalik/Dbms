package com.company;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class DatabaseManager {

    public static final String separator = "==========================================================";
    public static final long SIZE_LIMIT = 10000000;
    public static final int MAX_NUMBER_OF_FIELDS = 10;
    private static final String SYSTEM_CATALOG_NAME = "syscat.ctg";
    SystemCatalogManager sysCatManager;
    FileManager fileManager;
    String[] operations = {
            "\t1.Create a Type",
            "\t2.Delete a Type",
            "\t3.List All Types",
            "\t4.Create a Record",
            "\t5.Delete a Record",
            "\t6.Search for a Record",
            "\t7.List All Records of a Type"
    };

    public static void main(String[] args) throws IOException {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.chooseOperation();
    }

    public DatabaseManager() throws IOException {
        sysCatManager = new SystemCatalogManager();
    }

    // Operation choose interface.
    // Invokes the corresponding Operation Method.
    public void chooseOperation() throws IOException {

        Scanner scanner = new Scanner(System.in);
        boolean operate = true;
        while(operate){
            System.out.println("Please enter the number of the operation you want to take:");
            System.out.println("Please enter -1 to terminate:\n");
            //Print the operations selection interface.
            for (String operation : operations){
                System.out.println(operation);
            }

            int selection = scanner.nextInt();

            switch(selection){
                case 1:
                    if(isFull()){
                        System.out.println("You are out of space. You can't add type.");
                        break;
                    }
                    createType();
                    System.out.println(separator);
                    break;
                case 2:
                    deleteType();
                    System.out.println(separator);
                    break;
                case 3: listTypes();
                    System.out.println(separator);
                    break;
                case 4:
                    if(isFull()){
                        System.out.println("You are out of space. You can't create record.");
                        break;
                    }
                    createRecord();
                    System.out.println(separator);
                    break;
                case 5: deleteRecord();
                    System.out.println(separator);
                    break;
                case 6: searchRecord();
                    System.out.println(separator);
                    break;
                case 7: listALlRecordsOfAType();
                    System.out.println(separator);
                    break;
                case -1: operate = false;
                    break;
            }
        }
    }

    public void createType() throws IOException {
        System.out.println("Creating a type...");
        Scanner scanner = new Scanner(System.in);

        String typeName;
        int fieldNumber;

        System.out.println("Please enter the NAME of the type you want to create: ");
        typeName = scanner.next();

        System.out.println("Please enter the NUMBER OF FIELDS of the type you want to create: ");
        fieldNumber = scanner.nextInt();

        String[] fieldNames = new String[fieldNumber];
        Arrays.fill(fieldNames,"");   //Fill it with empty string.
        for(int i = 0; i < fieldNumber; i++){
           System.out.println("Please enter the name of the field number " + (i + 1));
           fieldNames[i] = scanner.next();
        }

        this.sysCatManager.addType(typeName,fieldNumber,fieldNames);

        TypeManager typeManager = new TypeManager(typeName);
        typeManager.init();
    }

    public void deleteType() throws IOException {
        System.out.println("Delete operation is initializing...");
        System.out.println("Please enter the type you want to delete");
        Scanner scanner = new Scanner(System.in);

        String typeToDelete = scanner.next();
        int typeIndex = sysCatManager.getTypeIndex(typeToDelete);
        if (typeIndex == -1) {
            System.out.println("The type you want to delete doesn't exist.");
            return;
        }
        sysCatManager.isDeletedData.set(typeIndex,1);   // Delete operation.
        sysCatManager.setNumberOfDeletedTypes(sysCatManager.getNumberOfDeletedTypes()+1);   // Increase the number of deleted types.
        // Update System Catalog (Print to the file.)
        sysCatManager.update();
        System.out.println("Updating the System Catalog...");
        File f = new File(typeToDelete+".t");
        if (f.exists()){
            System.out.println("Deleting the corresponding type file...");
            f.delete();         //  Delete the file.
        }
        System.out.println("Type deleted succesfully.");
    }

    public void listTypes(){
        System.out.println("Listing all types...\n");
        for (int i = 0; i < sysCatManager.typeNames.size();i++){
            if (sysCatManager.isDeletedData.get(i) == 0) {
                System.out.println("\t"+sysCatManager.typeNames.get(i));
            }
        }
    }

    public void createRecord() throws IOException {
        System.out.println("Creating a record...");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the TYPE NAME of the record you want to create.");
        String typeOfTheRecord = scanner.next();

        // Stop if the type doesn't exist.
        if(!sysCatManager.typeNames.contains(typeOfTheRecord)){
            System.out.println("This type doesn't exist. Please make sure your type is created beforehand.");
            return;
        }

        // Open the type file.
        int fieldCount = sysCatManager.fieldCounts.get(sysCatManager.getTypeIndex(typeOfTheRecord));
        int[] fields = new int[MAX_NUMBER_OF_FIELDS];
        Arrays.fill(fields,0);
        for (int i = 0; i < fieldCount; i++){
            System.out.println("Please enter the field value #"+i);
            fields[i] = scanner.nextInt();
        }
        TypeManager typeManager = new TypeManager(typeOfTheRecord);
        typeManager.addRecord(fields);
    }

    public void deleteRecord() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the TYPE NAME of the record you want to delete");
        String typeName = scanner.next();
        System.out.println("Please enter the PRIMARY KEY of the record you want to delete");
        int primaryKeyToDelete = scanner.nextInt();

        TypeManager typeManager = new TypeManager(typeName);
        typeManager.deleteRecord(primaryKeyToDelete);
    }

    public void searchRecord() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Searching operation is started...");
        System.out.println("Please enter the type name of the record you want to search.");
        String typeName = scanner.next();
        // If the type doesnt exist.
        if(!this.sysCatManager.typeNames.contains(typeName)){
            System.out.println("The type you are searching for doesn't exist.");
            return;
        }
        // If the element is deleted.
        if(this.sysCatManager.isDeletedData.get(this.sysCatManager.getTypeIndex(typeName)) == 1){
            System.out.println("The type you are searching for doesn't exist.");
            return;
        }
        System.out.println("Please enter the primary key of the record you want to search.");
        int primaryKey = scanner.nextInt();

        TypeManager typeManager = new TypeManager(typeName);
        typeManager.search(primaryKey);

    }

    public void listALlRecordsOfAType() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the TYPE NAME to list its records.");
        String typeName = scanner.next();
        // If the element is deleted.
        if(this.sysCatManager.getTypeIndex(typeName) == -1 || this.sysCatManager.isDeletedData.get(this.sysCatManager.getTypeIndex(typeName)) == 1 ){
            System.out.println("The type you are trying to list doesn't exist.");
            return;
        }
        TypeManager typeManager = new TypeManager(typeName);
        typeManager.listAllRecords();
    }

    public boolean isFull(){
        File dir = new File(".");
        File [] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".t");
            }
        });
        long size = 0;
        for (File tFile : files){
            size += tFile.length();
        }
        File file = new File(SYSTEM_CATALOG_NAME);
        size += file.length();
        return size > SIZE_LIMIT;
    }
}
