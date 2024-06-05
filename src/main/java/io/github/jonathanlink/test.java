package io.github.jonathanlink;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.*;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

class GetWordLocationAndSize extends PDFTextStripper 
{

    public GetWordLocationAndSize() throws IOException 
    {
        super();
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException 
    {
        String wordSeparator = getWordSeparator();
        List<TextPosition> word = new ArrayList<>();
        for (TextPosition text : textPositions) 
        {
            String thisChar = text.getUnicode();
            if (thisChar != null) 
            {
                if (thisChar.length() >= 1) 
                {
                    if (!thisChar.equals(wordSeparator)) 
                    {
                        word.add(text);
                    } 
                    else if (!word.isEmpty()) 
                    {
                        printWord(word);
                        word.clear();
                    }
                }
            }
        }
        if (!word.isEmpty()) 
        {
            printWord(word);
            word.clear();
        }
    }

    void printWord(List<TextPosition> word) 
    {
        Rectangle2D boundingBox = null;
        StringBuilder builder = new StringBuilder();
        for (TextPosition text : word) 
        {
            Rectangle2D box = new Rectangle2D.Float(text.getXDirAdj(), text.getYDirAdj(),
                    text.getWidthDirAdj(), text.getHeightDir());
            if (boundingBox == null)
                boundingBox = box;
            else
                boundingBox.add(box);
            builder.append(text.getUnicode());
        }
        double xCoordinate = boundingBox.getX();
        double yCoordinate = boundingBox.getY();
        double heightOfbbox = boundingBox.getHeight();
        double widthOfbbox = boundingBox.getWidth();

        int xIntegerValue = (int) xCoordinate;
        int yIntegerValue = (int) yCoordinate;
        int IntegerHeight = (int) heightOfbbox;
        int IntegerWidth  = (int) widthOfbbox;

        System.out.println(builder.toString() + " [(X=" + xIntegerValue + ",Y=" + yIntegerValue
                + ") height=" + IntegerHeight+ " width=" + IntegerWidth + "]");
    }

}

public class test 
{

    public static void main(String[] args) throws IOException 
    {
        // if (args.length < 1) 
        // {
        //     System.out.println("Usage: java Test <pdfFilePath>");
        //     return;
        // }
        // String string = args[0];
        // String path = string.replaceAll("(?i)\\.pdf$", ".pdf");
        // System.out.println(string + "this is the args[0]");
        // String[] parts = string.split("/");
        // String lastpart = parts[parts.length - 1];
        // String filename = lastpart.replaceAll("(?i)\\.pdf$", ".txt");
        // String csvname = lastpart.replaceAll("(?i)\\.pdf$", "_boundingBox.csv");
        // System.out.println(lastpart);
        String string = "D:\\ResearchPaperReader\\PDFLayoutTextStripper\\2310.05030v2.pdf";
        String forBbox = string;

        try {
            PDFParser pdfParser = new PDFParser(new RandomAccessFile(new File(string), "r"));
            pdfParser.parse();
            PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
            PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
            string = pdfTextStripper.getText(pdDocument);
            writeTextToFile(string, "output.txt"); // file name by venkat using replace
            System.out.println("Completed the text genrations");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;

        try {
            String pdfFilePath = forBbox; // Provide your PDF file path here
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            System.out.println("After pddocument");
            PDFTextStripper stripper = new GetWordLocationAndSize();
            System.out.println("after stripper");
            stripper.setSortByPosition(true);
            stripper.setStartPage(0);
            stripper.setEndPage(document.getNumberOfPages());
            System.out.println("after end page");

            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            try 
            {
                FileWriter csvWriter = new FileWriter("output.csv");
                System.setOut(new PrintStream(new OutputStream() 
                {
                    @Override
                    public void write(int b) throws IOException 
                    {
                        csvWriter.write(b);
                    }
                }));

                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------------------------------------");
                stripper.writeText(document, dummy);
                System.out.println("afterwrite text");
                csvWriter.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }

            document.close();
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private static void writeTextToFile(String text, String filePath) 
    {
        try (FileWriter fileWriter = new FileWriter(filePath)) 
        {
            fileWriter.write(text);
        } catch (IOException e) 
        {
            System.err.println("Error writing text to file: " + e.getMessage());
        }
    }

}