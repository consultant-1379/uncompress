/*
 * Created on 9.5.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.distocraft.dc5000.uncompress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO intro TODO usage TODO used databases/tables TODO used properties
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class Uncompressor {

  private String fileSuffix;

  private String uncompressor;

  private String inDir;

  private String archiveDir;

  private String failedDir;

  private String baseDir;

  private String afterUnpackCommand;

  private GenericUncompressor unComp;

  private Properties conf;

  private Logger log;

  public Uncompressor(Properties conf, String techPack, String set_type, String set_name) throws Exception {

    this.conf = conf;
    String loggerName = "etl." + techPack + "." + set_type + "." + set_name;
    
    log = Logger.getLogger(loggerName + ".Uncompressor");

    this.fileSuffix = conf.getProperty("fileSuffix", ".*");
    this.uncompressor = conf.getProperty("uncompressor");

    this.baseDir = conf.getProperty("baseDir");
    if (!baseDir.endsWith(File.separator))
      this.baseDir += File.separator;

    this.inDir = conf.getProperty("inDir", this.baseDir + "compressed" + File.separator);
    this.failedDir = conf.getProperty("failedDir", this.baseDir + "failed" + File.separator);
    this.archiveDir = conf.getProperty("archiveDir", this.baseDir + "archive" + File.separator);
    this.afterUnpackCommand = conf.getProperty("afterUncompressCommand", "move");

    unComp = createUncompressor(uncompressor);
    unComp.init(this.conf, loggerName);

  }

  public void execute() throws Exception {

    // create a filter that filters out unwanted files
    FilenameFilter filter = new FilenameFilter() {

      public boolean accept(File dir, String name) {      
      	Pattern patt = Pattern.compile(fileSuffix);
        Matcher m = patt.matcher(name);
      	return m.find();
      }
    };

    // list all needed files
    File[] files = new File(inDir).listFiles(filter);

    // loop all files
    for (int i = 0; i < files.length; i++) {

      try {

        List list = unComp.uncompress(files[i]);
        log.info("Uncompressed " + list.size() + " files from file " + files[i].getName());

        
        if (!archiveDir.endsWith(File.separator))
        	archiveDir += File.separator;
        
        handleFile(files[i], new File(archiveDir+files[i].getName()), afterUnpackCommand);

      } catch (Exception e) {

        log.warning("Error while uncompressing file " + files[i].getName());
        log.fine("Moving to failed");
        move(files[i], new File(this.failedDir + files[i].getName()));

      }

    }
  }

  private GenericUncompressor createUncompressor(String className) throws Exception {

    try {

      /* Create object from the class */
      GenericUncompressor ist = (GenericUncompressor) (Class.forName(className).newInstance());

      return ist;

    } catch (ClassNotFoundException e) {
      throw new Exception("Error while creating an uncompressor", e);
    } catch (InstantiationException e) {
      throw new Exception("Error while creating an uncompressor", e);
    } catch (IllegalAccessException e) {
      throw new Exception("Error while creating an uncompressor", e);
    } catch (NullPointerException e) {
      throw new Exception("Error while creating an uncompressor: uncompressor type not found (" + className + ")", e);
    }

  }

  public static void move(File file, File tgt) throws Exception {

    boolean success = file.renameTo(tgt);

    if (success)
      return;

    copy(file, new File(tgt.getAbsolutePath()));

    file.delete();

  }

  public static void copy(File inFile, File outFile) throws Exception {

    if (!inFile.equals(outFile)) {

      InputStream in = new FileInputStream(inFile);
      OutputStream out = new FileOutputStream(outFile);

      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }

      in.close();
      out.close();

    }

  }

  public static void handleFile(File file, File dest, String command) throws Exception {

    if (command.equalsIgnoreCase("move")) {

      move(file, dest);

    } else if (command.equalsIgnoreCase("delete")) {

      file.delete();

    } else if (command.equalsIgnoreCase("copy")) {

      copy(file, dest);

    } else {

      throw new Exception("Unknown command " + command);

    }

  }

}
