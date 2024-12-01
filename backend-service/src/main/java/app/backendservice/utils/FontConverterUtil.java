package app.backendservice.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.Value;
import org.apache.commons.io.FilenameUtils;

import app.backendservice.config.DirConfiguration;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FontConverterUtil
{
    public static byte[] ttfToWoff2(byte[] fileTtf, DirConfiguration dirConfiguration) throws Exception, IOException
    {
        File  tempTTF          = null; 
        File  tempWoff2        = null;
        byte[] woff2FormatData = null; 

        try
        {                   
            tempTTF = File.createTempFile("tempTTF", ".ttf", new File(dirConfiguration.getTemp()));
            
            FileOutputStream fileOutputStream = new FileOutputStream(tempTTF);            
            fileOutputStream.write(fileTtf);
            fileOutputStream.close();

            ProcessBuilder builder = new ProcessBuilder();

            builder.command(
                "sh", 
                "-c",
                String.format("woff2_compress %s", tempTTF.getPath())
            );

            Process copressProcess = builder.start();
            int     exitCode       = copressProcess.waitFor();
            
            if (exitCode != 0)
            {
                log.warn("Error in compressing process");
            }

            tempWoff2 = new File(
                String.format("%s/%s.woff2",dirConfiguration.getTemp(),
                FilenameUtils.getBaseName(tempTTF.getName()))
            );

            FileInputStream fileInputStream = new FileInputStream(tempWoff2);
            
            woff2FormatData = fileInputStream.readAllBytes();
            fileInputStream.close();

        }        
        finally 
        {
            if (tempTTF != null && tempTTF.exists()) 
            {
                tempTTF.delete();
            }

            if (tempWoff2 != null && tempWoff2.exists()) 
            {
                tempWoff2.delete();
            } 
        }          
        
        return woff2FormatData;
    }    
}
