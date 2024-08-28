/*
 * Created on 9.5.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.distocraft.dc5000.uncompress;

import com.ericsson.eniq.common.lwp.LwProcess;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * TODO intro TODO usage TODO used databases/tables TODO used properties
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class Uncompress implements GenericUncompressor {

  private Logger log;

  private String outDir;
  private String archiveDir;
  private String baseDir;
  private String afterUnpackCommand;
  
  private String unpackCommand;

  private String fileSuffix;

  public void init(Properties conf, String loggerName) {

    log = Logger.getLogger(loggerName + ".Uncompressor.uncompress");

    this.baseDir = conf.getProperty("baseDir");
    this.outDir = conf.getProperty("outDir", baseDir + "in" + File.separator);
    this.archiveDir = conf.getProperty("archiveDir", baseDir + "archive" + File.separator);
    this.unpackCommand = conf.getProperty("unCompressCommand");
    this.fileSuffix = conf.getProperty("fileSuffix", ".*");
    this.afterUnpackCommand = conf.getProperty("afterUncompressCommand", "move");

  }

  public List uncompress(File file) throws Exception {

    if (!this.outDir.endsWith(File.separator))
      this.outDir += File.separator;

    // tmpfile
    String newFilename = this.outDir +"TMP"+ (new Date()).getTime()+".Z";

    // copy file to out dir..
    Uncompressor.copy(file, new File(newFilename));

    try {

      LwProcess.execute(unpackCommand + " " + file.getAbsolutePath());

      ArrayList a = new ArrayList();
      a.add(file.getAbsolutePath());
      return a;
      
    } finally {

      if (!file.exists()){
      	new File(newFilename).renameTo(file);
      } else {
      	new File(newFilename).delete();
      }

    }
  }
}
