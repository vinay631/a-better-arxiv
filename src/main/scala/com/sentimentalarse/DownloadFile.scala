package com.sentimentalarse

import slick.driver.SQLiteDriver.api._
import java.net.URL
import java.io.File
import sys.process._

class DownloadFile {
  
  def fileDownloader(url: String, fileName: String) = {
    new URL(url) #> new File(fileName) !!
  }
 
}
 
object DownloadFile extends App {
  
  val dPdf = new DownloadFile()
  val url = "http://arxiv.org/pdf/1604.02135v2"
  val fileName = "1604.02135v2.pdf"
  dPdf.fileDownloader(url, fileName)
}