package app.backendservice.utils;

import org.apache.fontbox.ttf.TrueTypeFont;

import app.backendservice.config.DirConfiguration;
import app.backendservice.exception.ResourceNotValidatedException;

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.NamingTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.Data;



@Data
public class TTFParserApacheUtils 
{
    
    private String           fontFamily;
    private String           title;
    private byte[]           fileBytes;
    private DirConfiguration dirConfiguration; 

    private static TTFParser parser = new TTFParser();


    public TTFParserApacheUtils(byte[] file, DirConfiguration dirConfiguration)
    {   
        this.dirConfiguration = dirConfiguration;
        File tempFile = null;

        try
        {
            this.fileBytes = file;  
            
            tempFile = File.createTempFile(
                "temp", 
                ".ttf", 
                new File(this.dirConfiguration.getTemp())
            );

            FileOutputStream outputStream = new FileOutputStream(tempFile, false);
            outputStream.write(file);
            
            FileInputStream inputStream = new FileInputStream(tempFile);
            
            TrueTypeFont font        = parser.parse(inputStream);
            NamingTable  namingTable = font.getNaming();

            title      = font.getName();
            fontFamily = namingTable.getFontFamily();

            inputStream.close();
            outputStream.close();
        }
        catch (IOException ex)
        {
            throw new ResourceNotValidatedException(String.format("Can't parse file from array %s", ex.getMessage()));
        }
        finally
        {               
            if (tempFile != null && tempFile.exists())
            {
                tempFile.delete();
            }               
        }
    }
}
