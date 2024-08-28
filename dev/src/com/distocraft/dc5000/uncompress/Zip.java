/*
 * Created on 9.5.2005 TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.distocraft.dc5000.uncompress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * TODO intro TODO usage TODO used databases/tables TODO used properties
 * 
 * @author savinen Copyright Distocraft 2005 $id$
 */
public class Zip implements GenericUncompressor {

  private Logger log;
  private String loggerName;
  private String separator;
  private String outDir;
  private boolean addDirs = false;

  public void init(Properties conf,String loggerName) {

    this.loggerName = loggerName;
    String baseDir = conf.getProperty("baseDir");
    this.separator = conf.getProperty("directorySeparator","");
    this.outDir = conf.getProperty("outDir",baseDir+"in"+File.separator);
    if (!separator.equalsIgnoreCase("")) addDirs = true;
  }

  public List uncompress(File file) throws Exception{

    log = Logger.getLogger(loggerName + ".Uncompressor.Zip");

    log.fine("unZip starts");
    ArrayList list = new ArrayList(1000);

    InputStream fileIn = null;
    ZipEntry zipEntry = null;
    File tarFile = null;
    FileInputStream tarSource = null;
    FileOutputStream tarDestination = null;
    byte[] buffer;
    int bytes_read;

    StringBuffer tarDir = new StringBuffer();


      fileIn = new BufferedInputStream(new FileInputStream(file));

      ZipInputStream zipStream = new ZipInputStream(fileIn);

      if (zipStream.available()!=0){
      	            		      
	      while ((zipEntry = (ZipEntry) zipStream.getNextEntry())!=null) {
	            	
	        tarDir.delete(0, tarDir.length());
	        String filename = "";
	        String tarName = null;
	      	
	        if (!zipEntry.isDirectory()) {
	
	          // do we write directories to the filename
	          if (addDirs) {
	
	          	
	          	// add dir name to the filename
	          	filename = new File(zipEntry.getName()).getName();

	          	
	            int i = 0;
	            int ii = 0;
	
	            // get filename with path
	            String tmpName = zipEntry.getName();
	
	            // reads all the directorynames
	            while ((ii = tmpName.indexOf("/", i)) != -1) {
	              // create directory string..
	              String dirName = tmpName.substring(i, ii);
	
	              // adds directoryname and separator if not fullstop "." or empty "
	              // "
	              if ((!dirName.equals(".")) && (!dirName.equals(" "))){
	                tarDir.append(dirName + separator);
	              }
	              
	              i = ii + 1;
	            }
	
	          } else {
	          	
	          	// create dirs

	          	File tmpFile = new File(zipEntry.getName());
	          	filename = tmpFile.getPath();
	          	String dir = filename.replaceAll(tmpFile.getName(),"");
	          	
	          	createDir(outDir + dir);
	          	
	          	
	          			          	
	          }
	
	
	          if (tarDir.length() > 0) {
	
	            tarName = outDir + "/" + tarDir.toString() + filename;
	          } else {
	
	            tarName = outDir + "/" + filename;
	
	          }
	
	          try {
	
	            tarDestination = new FileOutputStream(tarName);
	            buffer = new byte[1024];
	
	            while (true) {
	              bytes_read = zipStream.read(buffer);
	
	              if (bytes_read == -1)
	                break;
	
	              tarDestination.write(buffer, 0, bytes_read);
	
	            }
	
	            tarDestination.close();
	
	            list.add(tarName);
	
	          } catch (FileNotFoundException e) {
	            log.warning("Error in unZip while processing file: " + tarName);
	          }
	
	        } else {
	        	
	        	// create dirs if not created yet, used on empty directories
	        	if (!addDirs) createDir(outDir + zipEntry.getName());	        	
	        }
	      }
      }
      zipStream.close();
      fileIn.close();

      log.fine("unZip stops");

      return list;

  }
  
  public void createDir(String name)throws Exception{
  	
	if (!new File(name).exists()){
		
		boolean ok = new File(name).mkdirs();
		
		if (!ok){			
			log.warning("Could not create directory: "+name);
			throw new Exception("Could not create directory: "+name);			
		}
	}

  } 
  
}