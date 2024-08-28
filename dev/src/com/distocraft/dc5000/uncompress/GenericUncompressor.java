/*
 * Created on 9.5.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.distocraft.dc5000.uncompress;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * TODO intro TODO usage TODO used databases/tables TODO used properties
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public interface GenericUncompressor {

  public void init(Properties conf, String loggerName) throws Exception;

  public List uncompress(File file) throws Exception;

}
