package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileManager {

    private static RandomAccessFile raf;

    public FileManager(String filename,boolean write){
        if (write){
            try {
                raf = new RandomAccessFile(filename,"rw");
            } catch (FileNotFoundException e) {
                System.err.println("Unable to open the random access file handler for file: " + filename);
                e.printStackTrace();
            }
        }
        else{
            try {
                raf = new RandomAccessFile(filename,"r");
            } catch (FileNotFoundException e) {
                System.err.println("Unable to open the random access file handler for file: " + filename);
                e.printStackTrace();
            }
        }
    }

    public String readString(int length) throws IOException {
        String res = "";
        for(int i = 0; i < length; i++){
            res += (char) raf.readByte();
        }
        return res;
    }

    public void writeString(String str, int length) throws IOException{

        int i;
        if (str.length() > length){
            str = str.substring(0,length);
        }

        for (i = 0;i<str.length();i++){
            raf.write(str.charAt(i));
        }

        i = length - str.length();
        while(i-- > 0){
            raf.writeByte(' ');
        }
    }

    public void seek(int offset,boolean fromStart) throws IOException {
        if (fromStart){
            raf.seek(offset);
        }else {
            raf.seek(raf.getFilePointer() + offset);
        }
    }

    public short readByte() throws java.io.IOException
    {
        return (short) raf.readUnsignedByte();
    }

    public void writeByte(short b) throws java.io.IOException
    {
        raf.write(b & 0xff);
    }

    public void writeInt(int s) throws java.io.IOException
    {
        raf.writeInt(s);
    }

    public int readInt() throws java.io.IOException
    {
        return raf.readInt();
    }

    public void seekToStart() throws IOException {
        raf.seek(0);
    }
}
