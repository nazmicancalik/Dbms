package com.company;

import java.util.Arrays;

public class Page {

    public static final int NUMBER_OF_RECORDS_IN_PAGE = 100;
    public static final int MAX_NUMBER_OF_FIELDS_IN_A_RECORD = 10;
    public static final int FIELD_LENGTH = 4;

    String typeName;
    // Header
    int numberOfRecords;
    int numberOfDeletedRecords;

    int[] isDeleted;
    int[][] fieldValues;

    public Page(String typeName){
        numberOfRecords = 0;
        numberOfDeletedRecords = NUMBER_OF_RECORDS_IN_PAGE;

        isDeleted = new int[NUMBER_OF_RECORDS_IN_PAGE];    // Init isDeleted Array.
        Arrays.fill(isDeleted,1);                       // Init the array all elements are deleted.

        fieldValues = new int[NUMBER_OF_RECORDS_IN_PAGE][MAX_NUMBER_OF_FIELDS_IN_A_RECORD];
        for (int[] row: fieldValues){
            Arrays.fill(row, 0);
        }
    }
}
