package com.company;

import java.util.Scanner;

public class DatabaseManager {

    SystemCatalogManager sysCatManager;
    String[] operations = {
            "\t1.Create a Type",
            "\t2.Delete a Type",
            "\t3.List All Types",
            "\t4.Create a Record",
            "\t5.Delete a Record",
            "\t6.Search for a Record",
            "\t7.List All Records of a Type"
    };

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
    }

    public DatabaseManager(){
        sysCatManager = new SystemCatalogManager();
        this.chooseOperation();
    }

    // Operation choose interface.
    // Invokes the corresponding Operation Method.
    public void chooseOperation(){
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

    public void createType(){
        System.out.println("Created a type.");
    }

    public void deleteType(){
        System.out.println("Deleted a type.");
    }

    public void listTypes(){
        System.out.println("Listed all types");
    }

    public void createRecord(){
        System.out.println("Created a Record.");
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
