package com.company;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class DatabaseManager {

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
    }

    public DatabaseManager() throws IOException {
        sysCatManager = new SystemCatalogManager();
        chooseOperation();
    }

    // Operation choose interface.
    // Invokes the corresponding Operation Method.
    public void chooseOperation() throws IOException {
        System.out.println("Please enter the number of the operation you want to take:");

        //Print the operations selection interface.
        for (String operation : operations){
            System.out.println(operation);
        }
        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt();

        switch(selection){
            case 1: createType();
                break;
            case 2: deleteType();
                break;
            case 3: listTypes();
                break;
            case 4: createRecord();
                break;
            case 5: deleteRecord();
                break;
            case 6: searchRecord();
                break;
            case 7: listALlRecordsOfAType();
                break;
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
        for(int i = 0; i < fieldNumber; i++){
           System.out.println("Please enter the name of the field number " + (i + 1));
           fieldNames[i] = scanner.next();
        }


        // Increase type count and add the necessary info for the new type.
        this.sysCatManager.setTypeCount(this.sysCatManager.getTypeCount()+1);
        this.sysCatManager.addTypeInfo(typeName,fieldNumber,fieldNames);
        FileOutputStream out = new FileOutputStream(typeName+".dat");
        // Fill the table.
        // TODO Type manager can work here.
        out.close();
    }

    public void deleteType(){
        System.out.println("Deleted a type.");
    }

    public void listTypes(){
        System.out.println("Listed all types");
    }

    public void createRecord() throws IOException {
        System.out.println("Creating a record.");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the TYPE NAME of the record you want to create.");
        String typeOfTheRecord = scanner.next();

        // Open the type file.
        TypeManager typeManager = new TypeManager(typeOfTheRecord + ".dat");
        //if(this.sysCatManager.checkTypeExists(typeOfTheRecord)){
            for(int i = 0;i <  sysCatManager.getFieldNumberOfAType(typeOfTheRecord);i++){
                System.out.println("yaeaadad");
            }
        //}else{
        //    System.out.println("Type you want to create doesn't exist");
        //    return;
        //}
    }

    public void deleteRecord(){
        System.out.println("Deleted a record.");
    }

    public void searchRecord(){
        System.out.println("Searched for a record");
    }

    public void listALlRecordsOfAType(){
        System.out.println("All records of a type.");
    }
}
