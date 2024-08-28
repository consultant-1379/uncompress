/*
 * Created on 9.5.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.distocraft.dc5000.uncompress;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * TODO intro TODO usage TODO used databases/tables TODO used properties
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class UncompressorAction extends TransferActionBase {
  private Meta_collections collection;

  private Meta_transfer_actions actions;

  private String collectionSetName;

  /**
   * Empty protected constructor
   */
  protected UncompressorAction() {
  }

  /**
   * 
   * @param versionNumber
   *          metadata version
   * @param collectionSetId
   *          primary key for collection set
   * @param collectionId
   *          primary key for collection
   * @param transferActionId
   *          primary key for transfer action
   * @param transferBatchId
   *          primary key for transfer batch
   * @param connectId
   *          primary key for database connections
   * @param rockFact
   *          metadata repository connection object
   * @param connectionPool
   *          a pool for database connections in this collection
   * @param trActions
   *          object that holds transfer action information (db contents)
   */
  public UncompressorAction(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact,
      Meta_transfer_actions trActions, SetContext sctx) throws EngineMetaDataException {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, trActions);

    try {

      // Get collection set name
      Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);
      Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);

      this.collectionSetName = collSet.getCollection_set_name();
      this.collection = collection;
      this.actions = trActions;

    } catch (Exception e) {
      throw new EngineMetaDataException("Exception in Uncompressor", new String[] { "" }, e, this, this.getClass()
          .getName());
    }

  }

  public void execute() throws EngineException {

    try {

      Properties properties = new Properties();

      String act_cont = this.actions.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          properties.load(bais);
          bais.close();
        } catch (Exception e) {
          System.out.println("Error loading action contents");
          e.printStackTrace();
        }
      }

      Uncompressor uncomp = new Uncompressor(properties, collectionSetName, collection.getSettype(), collection
          .getCollection_name());
      uncomp.execute();

    } catch (Exception e) {
      throw new EngineException("Exception in Uncompressor", new String[] { "" }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_SYSTEM);

    }

  }

}
