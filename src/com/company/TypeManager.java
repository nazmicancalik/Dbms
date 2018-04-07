package com.company;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.company.SystemCatalogManager.FIELD_NAME_LENGTH;

public class TypeManager {

    public static final int MAX_NUMBER_OF_FIELDS_IN_A_RECORD = 10;
    public static final int NUMBER_OF_RECORDS_IN_PAGE = 100;
    public static final int RECORD_SIZE = 4 + MAX_NUMBER_OF_FIELDS_IN_A_RECORD * 4;
    public static final int PAGE_SIZE = 4 + 4 + NUMBER_OF_RECORDS_IN_PAGE * RECORD_SIZE;

    FileManager fileManager;
    SystemCatalogManager systemCatalogManager;
    Page currentPage;
    String typeName;
    int pageCount;

    public TypeManager(String aTypeName) throws IOException {
        // TODO If the file exist already, just read the file and load the required pages and config (header)
        File file = new File(aTypeName+".t");
        boolean fileExists = file.exists();

        typeName = aTypeName;
        systemCatalogManager = new SystemCatalogManager();
        fileManager = new FileManager(typeName + ".t", true);
        pageCount = systemCatalogManager.getPageCountOfAType(aTypeName);
        if (!fileExists){
            System.out.println("New type is created. Initializing type file...");
            fillTypeFile();
        }
        init();
    }

    public void fillTypeFile() throws IOException {
        addPageToEnd();
    }

    public Page loadPage(int pageIndex) throws IOException {

        Page page = new Page(typeName);
        FileManager inFm = new FileManager(typeName+".t",false);
        inFm.seekToStart();     // Redundant
        inFm.seek(PAGE_SIZE*pageIndex,true);
        page.numberOfRecords = inFm.readInt();
        page.numberOfDeletedRecords = inFm.readInt();


        // Start to read Records if there is any.
        if(page.numberOfRecords != 0){
            for(int i = 0;i < page.numberOfRecords; i++){
                page.isDeleted[i] = inFm.readInt();
                for(int j = 0; j < MAX_NUMBER_OF_FIELDS_IN_A_RECORD;j++){
                    page.fieldValues[i][j] = inFm.readInt();
                }
            }
        }
        inFm.close();
        return page;
    }

    public void init() throws IOException {
        this.currentPage = loadPage(0);
    }

    public void addPageToEnd() throws IOException {
        Page emptyPage = new Page(typeName);        // Initialize an empty page.
        writePage(emptyPage,systemCatalogManager.getPageCountOfAType(typeName));
        increasePageCount();
    }

    public void writePage(Page page, int index) throws IOException {
        FileManager inFm = new FileManager(typeName+".t",true);
        inFm.seekToStart();          // Redundant
        inFm.seek(index*PAGE_SIZE,true);
        inFm.writeInt(page.numberOfRecords);         // Write number of records.
        inFm.writeInt(page.numberOfDeletedRecords);  // Write number of deleted records.

        for (int i = 0;i < NUMBER_OF_RECORDS_IN_PAGE; i++){
            inFm.writeInt(page.isDeleted[i]);        // Write isDeleted flag.
            for (int j = 0;j < MAX_NUMBER_OF_FIELDS_IN_A_RECORD;j++){
                inFm.writeInt(page.fieldValues[i][j]);
            }
        }
        inFm.close();
    }

    public void addRecord(int[] fields) throws IOException {
        int pageCount = systemCatalogManager.getPageCountOfAType(typeName);
        Page page;
        boolean inserted = false;
        for (int i = 0;i < pageCount && !inserted ;i++){
            page = loadPage(i);
            // If the page is not full totally.
            if (page.numberOfDeletedRecords != 0){
                for (int j = 0; j < NUMBER_OF_RECORDS_IN_PAGE;j++){
                    // If the record is empty or deleted.
                    if (page.isDeleted[j] == 1){
                        System.out.println("Empty space for the new record is found. Inserting the new record to page #" + i +" record #"+j);
                        page.numberOfRecords++;
                        page.numberOfDeletedRecords--;
                        page.isDeleted[j] = 0;
                        page.fieldValues[j] = fields;       // Make its values the new ones.
                        this.writePage(page,i);
                        inserted = true;                    // Insertion is complete. Stop the process.
                        System.out.println("Record creation is succesful.");
                        break;
                    }
                }
            }
        }

        // If all the pages are full and need to insert the record. Create another page.
        if(!inserted){
            System.out.println("There is no empty page left for the new record. Creating a new page...");
            Page newPage = new Page(typeName);
            newPage.isDeleted[0] = 0;               // Make the first element of the new page, not deleted.
            newPage.fieldValues[0] = fields;        // Make the first record's fields the given fields.
            this.writePage(newPage,pageCount);      // Write the new page to the last index.
            System.out.println("New page is created and written to the file " + typeName + ".t");
            increasePageCount();
        }
    }

    public void increasePageCount() throws IOException {
        System.out.println("Increasing the page count in the system catalog for the type " + typeName+ "...");
        int oldPageCount = systemCatalogManager.getPageCountOfAType(typeName);
        systemCatalogManager.setPageCountOfAType(typeName,oldPageCount+1);
    }

    public void search(int primaryKey) throws IOException {
        int pageCount = systemCatalogManager.getPageCountOfAType(typeName);
        Page page;
        boolean found = false;
        for (int i = 0;i < pageCount && !found;i++){
            page = loadPage(i);
            // If the page is not full totally.
            System.out.println("Searching Page #"+i+" ...");
            if (page.numberOfRecords != 0){
                for (int j = 0; j < NUMBER_OF_RECORDS_IN_PAGE;j++){
                    // If the record is not empty or deleted.
                    if (page.isDeleted[j] == 0){
                        // Check for the given records first field (primary key)
                        if(page.fieldValues[j][0] == primaryKey) {
                            System.out.println("Successfull");
                            System.out.println("Record is found on page #" + i + " record #"+ j);
                            String[] fieldNames = systemCatalogManager.fieldNames.get(typeName);

                            // ****** PRINTING PART ******
                            // Print field names.
                            System.out.print("\n"+typeName+"\t\t");
                            for (int k = 0; k < fieldNames.length; k++){
                                System.out.print(fieldNames[k]);
                                System.out.print("\t");
                            }
                            System.out.println("\n           \t-------------------");
                            // Print field values.
                            System.out.print("Record #"+j+"\t");
                            for (int k = 0; k < fieldNames.length; k++){
                                System.out.print(page.fieldValues[j][k]);
                                System.out.print("\t");
                            }
                            System.out.println();
                            found = true;
                            break;
                        }
                    }
                }
            }
        }

        if(!found){
            System.out.println("The record you are looking for doesn't exist.");
        }
    }

    public void listAllRecords() throws IOException {
        int pageCount = systemCatalogManager.getPageCountOfAType(typeName);
        Page page;
        for (int i = 0;i < pageCount;i++){
            page = loadPage(i);
            // If the page is not full totally.
            System.out.println("Reading Page #"+i+" ...");
            if (page.numberOfRecords != 0){

                String[] fieldNames = systemCatalogManager.fieldNames.get(typeName);
                // Print field names.
                System.out.print(typeName.toUpperCase()+"\t\t");
                for (int k = 0; k < fieldNames.length; k++){
                    System.out.print(fieldNames[k]);
                    System.out.print("\t");
                }
                System.out.println("\n           \t-------------------");
                for (int j = 0; j < NUMBER_OF_RECORDS_IN_PAGE;j++){
                    // If the record is not empty or deleted.
                    if (page.isDeleted[j] == 0){
                        // Print field values.
                        System.out.print("Record #"+j+"\t");
                        for (int k = 0; k < fieldNames.length; k++){
                            System.out.print(page.fieldValues[j][k]);
                            System.out.print("\t");
                        }
                        System.out.println();
                    }
                }
            }
        }
    }
}
