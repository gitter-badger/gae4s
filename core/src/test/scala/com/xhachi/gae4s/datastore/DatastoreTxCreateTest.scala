package com.xhachi.gae4s.datastore

import java.util.ConcurrentModificationException

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.xhachi.gae4s.tests.AppEngineTestSuite
import org.scalatest.FunSuite


class DatastoreTxCreateTest extends FunSuite with AppEngineTestSuite {

  override def getConfig = new LocalDatastoreServiceTestConfig :: super.getConfig

  implicit val meta = EntityMeta.createMeta[SampleEntity]

  test("tx1開始→tx2開始→tx1でcreate→tx2でcreate→tx1コミット→tx2コミットでtx2コミット時にエラーになること") {

    val tx1 = Datastore.beginTx
    val tx2 = Datastore.beginTx

    val key = Datastore.createKey[SampleEntity](1)

    val tx1entity = new SampleEntity(key)
    val tx2entity = new SampleEntity(key)

    Datastore.createWithTx(tx1, tx1entity)
    Datastore.createWithTx(tx2, tx2entity)

    tx1.commit()
    intercept[ConcurrentModificationException] {
      tx2.commit()
    }
  }

  test("tx1開始→tx2開始→tx1でcreate→tx1コミット→tx2でcreate→tx2コミットでConcurrentModificationExceptionが発生すること") {

    val tx1 = Datastore.beginTx
    val tx2 = Datastore.beginTx

    val key = Datastore.createKey[SampleEntity](1)

    val tx1entity = new SampleEntity(key)
    val tx2entity = new SampleEntity(key)

    Datastore.createWithTx(tx1, tx1entity)
    tx1.commit()

    intercept[ConcurrentModificationException] {
      Datastore.createWithTx(tx2, tx2entity)
    }

    tx2.commit()
  }

  test("tx1開始→tx1でcreate→tx2開始→tx1コミット→tx2でcreate→tx2コミットでConcurrentModificationExceptionが発生すること") {

    val tx1 = Datastore.beginTx

    val key = Datastore.createKey[SampleEntity](1)

    val tx1entity = new SampleEntity(key)
    val tx2entity = new SampleEntity(key)

    Datastore.createWithTx(tx1, tx1entity)
    val tx2 = Datastore.beginTx
    tx1.commit()


    intercept[ConcurrentModificationException] {
      Datastore.createWithTx(tx2, tx2entity)
    }
    tx2.commit()
  }

  test("tx1開始→tx1でcreate→tx1コミット→tx2開始→tx2でcreate→tx2コミットでエラーになること") {

    val tx1 = Datastore.beginTx

    val key = Datastore.createKey[SampleEntity](1)

    val tx1entity = new SampleEntity(key)
    val tx2entity = new SampleEntity(key)

    Datastore.createWithTx(tx1, tx1entity)
    tx1.commit()

    val tx2 = Datastore.beginTx

    intercept[ConcurrentModificationException] {
      Datastore.createWithTx(tx2, tx2entity)
    }
    tx2.commit()
  }

  class SampleEntity(val key: Key[SampleEntity]) extends Entity[SampleEntity]

}


