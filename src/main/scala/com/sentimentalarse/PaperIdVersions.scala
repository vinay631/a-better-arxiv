package com.sentimentalarse

import slick.driver.SQLiteDriver.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

class PaperIdVersions(tag: Tag) extends
 Table[(String, Int, String, Boolean)](tag, "paper_id_versions") {
  def id = column[String]("id")
  def version = column[Int]("version")
  def url = column[String]("url")
  def downloaded = column[Boolean]("downloaded")
  
  def *  =
    (id, version, url, downloaded)
}

