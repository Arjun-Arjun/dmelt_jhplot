// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.


package jhplot;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;



/**
* Simple file downloader similar to Unix WGET. Use get(url,true) if you want to replace the existiong file.
* Use get(url) for downloading the file only if it does not exist on the local disk. 
* @author S.Chekanov
*/

public class Web {

enum WgetStatus {
  DownloadedSuccessfully, MalformedUrl, IoException, UnableToCloseOutputStream, AlreadyExists;
}


  /**
  * Download a file to the local disk. It assumes the current directory.  If file exists locally, the file will not be overwritten. 
  * @param url input URL for download. 
  **/
  public static WgetStatus get(String url) {
          return get(url,false);
  }

  /**
  * Download a file to the local disk. It assumes the current directory. 
  * @param url input URL for download. 
  * @param isOverwrite set to true if the local file need to be replaced from the web.  
  **/
  public static WgetStatus get(String url, boolean isOverwrite) {
         String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
         Path currentRelativePath = Paths.get("");
         String s = currentRelativePath.toAbsolutePath().toString();
         return get(url,s+File.separator+fileName,isOverwrite);

  }



  /**
  * Download a file to the local disk. 
  * @param url input URL for download. 
  * @param saveAsFile file name with the path where the file will be saved
  * @param isOverwrite set to true if the local file need to be replaced from the web. 
  **/
  public static WgetStatus get(String url, String saveAsFile, boolean isOverwrite) {


   if (isOverwrite==false) {
            File f = new File(saveAsFile);
            if(f.exists() && !f.isDirectory()) return WgetStatus.AlreadyExists;
    }


    InputStream httpIn = null;
    OutputStream fileOutput = null;
    OutputStream bufferedOut = null;
    try {
      // check the http connection before we do anything to the fs
      httpIn = new BufferedInputStream(new URL(url).openStream());
      // prep saving the file
      fileOutput = new FileOutputStream(saveAsFile);
      bufferedOut = new BufferedOutputStream(fileOutput, 1024);
      byte data[] = new byte[1024];
      boolean fileComplete = false;
      int count = 0;
      while (!fileComplete) {
        count = httpIn.read(data, 0, 1024);
        if (count <= 0) {
          fileComplete = true;
        } else {
          bufferedOut.write(data, 0, count);
        }
      }
    } catch (MalformedURLException e) {
      return WgetStatus.MalformedUrl;
    } catch (IOException e) {
      return WgetStatus.IoException;
    } finally {
      try {
        bufferedOut.close();
        fileOutput.close();
        httpIn.close();
      } catch (IOException e) {
        return WgetStatus.UnableToCloseOutputStream;
      }
    }
    return WgetStatus.DownloadedSuccessfully;
  }

}
