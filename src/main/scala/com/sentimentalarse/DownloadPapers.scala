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
    val downloadUrls = (toBeDownloadedRows.exists.result.flatMap { exists =>
      val downloadFile = new DownloadFile()
      toBeDownloadedRows.result.map(_.foreach {
        case(id, ver, url, down) => 
          val pdfUrl = url.replace("http", "https") + ".pdf"
          downloadFile.fileDownloader(pdfUrl, url.split("/").last + ".pdf")
      })
                             
      toBeDownloadedRows.map(_.downloaded).update(true)
    }).transactionally
    
    db.run(downloadUrls)
    
  }
}

object DownloadPapers extends App {
  val downloadPapers = new DownloadPapers()
  val db = Database.forConfig("sqlitedb")
  Await.result(downloadPapers.getUndownloadedList(db), 60 second)
}