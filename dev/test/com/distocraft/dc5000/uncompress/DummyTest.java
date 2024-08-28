/*
 * Created on 9.5.2005 TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.distocraft.dc5000.uncompress;

import static org.junit.Assert.assertEquals;

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

import com.distocraft.dc5000.*;
import com.distocraft.dc5000.etl.engine.common.EngineException;

import org.junit.Test;


/**
 * TODO intro TODO usage TODO used databases/tables TODO used properties
 * 
 * @author savinen Copyright Distocraft 2005 $id$
 */
public class DummyTest {
	
	
	Uncompress unc = new Uncompress();
	
	UncompressorAction uca = new UncompressorAction();
	
	@Test
	public void test1()  {
		String result = Uncompressor.class.getName();
		unc.init(null, null);
	    assertEquals(result,result);
	}
	
	@Test
	public void test2() throws EngineException {
		String result = Uncompressor.class.getName();
		uca.execute();
	    assertEquals(result,result);
	}
	
}