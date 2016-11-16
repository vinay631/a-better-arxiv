package com.sentimentalarse

import slick.driver.SQLiteDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.xml.XML
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.impl.Future
import scala.concurrent.duration._

class DownloadPapers {
  
  def getUndownloadedList(db: Database) = {
    
    val paperIdVersions = TableQuery[PaperIdVersions]
    val toBeDownloadedRows = paperIdVersions.filter(p => p.downloaded === false)
    val query = toBeDownloadedRows.take(1000).map(row => (row.id, row.url))
    db.run(query.result)
  }
  def download(url: String) = {
    val downloadFile = new DownloadFile()
    try {
      val pdfUrl = url.replace("http", "https") + ".pdf"
        println("Downloading ... " + pdfUrl)
        downloadFile.fileDownloader(pdfUrl, "pdfs/" + url.split("/").last + ".pdf")
        true
    } catch {
      case e: Exception => false
    }
  }
  
  def downloadAndUpdate(db: Database, toBeDownloaded: List[(String, String)]) = {
    val downloadedIds = toBeDownloaded.filter(x => download(x._2)).map(x => x._1)
    println(downloadedIds)
    val paperIdVersions = TableQuery[PaperIdVersions]
    val query = for {
      f <- paperIdVersions if (f.id inSet downloadedIds) && !f.downloaded
    } yield f
    val action = (query.exists.result.flatMap { exists =>
      query.map(_.downloaded).update(true)
    }).transactionally
    
    db.run(action)
  }
}

object DownloadPapers extends App {
  val downloadPapers = new DownloadPapers()
  val db = Database.forConfig("sqlitedb")
  val toBeDownloaded = Await.result(downloadPapers.getUndownloadedList(db), 15 second)
  Await.result(downloadPapers.downloadAndUpdate(db, toBeDownloaded.toList), 5 second)
}