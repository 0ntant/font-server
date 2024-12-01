package app.backendservice.utils;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Slf4j
public class FileUtils 
{
    public static byte[] getAllFileToBytes(String path)
    {
        byte[] buff = null;    

        try 
        {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);

            buff = new byte[(int) file.length()];
            
            inputStream.read(buff);
            inputStream.close();

            return buff;
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage(), ex);
        }

        return buff;
    }


    public static void saveFile(byte[] bytes, String path)
    {
        try
        {
            File file = new File(path);         
            FileOutputStream outputStream = new FileOutputStream(file);

            file.createNewFile();
            outputStream.write(bytes);
            outputStream.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage(), ex);
        }
    }


    public static boolean isExists(String path)
    {
        File file = new File(path);
        return file.exists();
    }

    
    public static boolean deleteFile(String path)
    {
        File file = new File(path);
        return file.delete();
    }
}
