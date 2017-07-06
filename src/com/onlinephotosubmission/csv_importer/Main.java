package com.onlinephotosubmission.csv_importer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Main {

    public static String[] headerTypes = {
            "Email",
            "ID",
            "Campus",
            "Notes"
    };
    private static String delimiter = ",";

    public static void main(String[] args) throws Exception {

        String filePath = args[0];
        String completedFile = args[1];
        File[] files = getCSVFilesFromDirectory(filePath);

        for (File csvfile : files) {
            System.out.println(csvfile);
        }
        for (File csvfile : files) {
            String fileName = csvfile.getName().replaceFirst("[.][^.]+$", "");
            List<String> lines = convertTextFileToListOfLines(csvfile.getAbsoluteFile().toString());
            List<CardHolder> cardHolders = convertLinesIntoCardHolders(lines);
            saveCardHolders(cardHolders, fileName, args[2]);
            Path inFile = Paths.get(csvfile.getAbsoluteFile().toString());
            Path outputFile = Paths.get(completedFile);
            transferFileToCompleted(inFile, outputFile);
        }

    }

    private static String fileName(String fileName) {
        LocalDate timeStamp = LocalDate.now();
        return fileName + "-" + timeStamp + "-Report.csv";
    }

    private static void transferFileToCompleted(Path inputFile, Path outputFile) {
        try {
            Files.move(inputFile, outputFile.resolve(inputFile.getFileName()), REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveCardHolders(List<CardHolder> cardHolders, String fileName, String arg) throws Exception{
        String content = "";
        String header = "";
        int openingHeaderCounter = 0;
        for(CardHolder cardHolder : cardHolders) {
            if(openingHeaderCounter == 0) {
                header = "Status," + cardHolder + "\n";
                openingHeaderCounter++;
            }
            else
                content = content + "failure notice, " + cardHolder + "\n";
        }
        String reportOutputPath = arg + "/" + fileName(fileName);
        File file = new File(reportOutputPath);

        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(header);
        bufferedWriter.write(content);

        bufferedWriter.close();
    }

    public static void calculateIndexForOutput(String header) {
        String[] Header = header.split(delimiter);
        for (int i = 0; i < Header.length; i++) {
            for (int j = 0; j < headerTypes.length; j++) {
                if(headerTypes[j].equals(Header[i])) {
                    if(i == 0) { CardHolder.setEmailIndex(j); }
                    if(i == 1) { CardHolder.setIdIndex(j); }
                    if(i == 2) { CardHolder.setCampusIndex(j); }
                    if(i == 3) { CardHolder.setNotesIndex(j); }
                }
            }
        }
    }

    private static List<CardHolder> convertLinesIntoCardHolders(List<String> lines) throws Exception {
        List<CardHolder> cardHolders = new ArrayList<CardHolder>();
        for (String line : lines) {
            cardHolders.add(convertLineIntoCardholder(line));
        }
        return cardHolders;
    }

    private static CardHolder convertLineIntoCardholder(String line) {
        CardHolder cardHolder = new CardHolder(delimiter, line);
        return cardHolder;
    }

    private static List<String> convertTextFileToListOfLines(String csvPath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(csvPath));
        List<String> lines = new ArrayList<String>();
        String line = null;
        int initialHeaderRead = 0;
        while((line = bufferedReader.readLine()) != null) {
            if(initialHeaderRead == 0) {
                calculateIndexForOutput(line);
                initialHeaderRead++;
            }
            lines.add(line);
        }
        bufferedReader.close();
        return lines;
    }
}
