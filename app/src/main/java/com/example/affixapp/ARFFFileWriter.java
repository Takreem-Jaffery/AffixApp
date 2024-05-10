package com.example.affixapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ARFFFileWriter {

    String filePath;
    String relation;
    String attributes;
    String data;

    public ARFFFileWriter() {
        this.filePath = "assets/users.arff";
        this.relation = "@relation users\n\n";
        this.attributes = "@attribute age numeric\n" +
                "@attribute zodiacSign {aries,taurus,gemini,cancer,leo,virgo,libra,scorpio,aquarius,sagittarius,capricorn,pisces}\n" +
                "@attribute profession {computer,engineering,arts,architecture,math,science,psychology,languages,business,other}\n" +
                "@attribute hobby {sports,travel,books,gym,games,movies,music,other}\n" +
                "@attribute fears {insects,heights,darkness,dentists,crowds,thunder,other}\n" +
                "@attribute gender {male,female,other}\n\n";
        //need to make it so that this data is read from firstore
        this.data = "@data\n" +
                "18,taurus,arts,books,insects,female\n" +
                "20,virgo,arts,travel,darkness,female\n" +
                "36,libra,engineering,books,insects,male\n" +
                "34,leo,science,gym,dentists,male\n";
    }
    private void writeARFFFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(relation+attributes+data);
            System.out.println("ARFF file created successfully at: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to write ARFF file: " + e.getMessage());
        }
    }
}