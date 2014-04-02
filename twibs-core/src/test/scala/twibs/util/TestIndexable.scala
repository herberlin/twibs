/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.{TextField, StringField, Document, Field}
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.Term

case class TestIndexable(key: String, firstName: String, lastName: String, emailAddress: String) {
  def UNIQUE_INDEXABLE_ID_NAME = "uniqueIndexableId"

  def indexableId = key

  def addFields(document: Document): Unit = {
    document.add(new TextField("firstName", firstName, Store.NO))
    document.add(new TextField("lastName", lastName, Store.NO))
    document.add(new StringField("emailAddress", emailAddress, Store.NO))
  }

  def insert(indexWriter: IndexWriter): Unit = {
    val document = new Document
    document.add(new StringField(UNIQUE_INDEXABLE_ID_NAME, indexableId, Field.Store.NO))
    addFields(document)
    indexWriter.addDocument(document)
  }

  def update(indexWriter: IndexWriter): Unit = {
    delete(indexWriter)
    insert(indexWriter)
  }

  def delete(indexWriter: IndexWriter): Unit =
    indexWriter.deleteDocuments(new Term(UNIQUE_INDEXABLE_ID_NAME, indexableId))
}