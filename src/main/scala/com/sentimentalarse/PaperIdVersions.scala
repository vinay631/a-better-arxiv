package com.sentimentalarse

import slick.driver.SQLiteDriver.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

class PaperIdVersions(tag: Tag) extends
 Table[(String, Int, String)](tag, "paper_id_versions") {
  def id = column[String]("id")
  def version = column[Int]("version")
  def url = column[String]("url")
  
  def *  =
    (id, version, url)
}

