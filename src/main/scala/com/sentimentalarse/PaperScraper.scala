package com.sentimentalarse

import slick.driver.SQLiteDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.xml.XML
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.impl.Future
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit._
import scala.xml.Node

/**
 * This scrapes the ids of papers related to ML, CS, OR etc...
 */
class PaperScraper {

  val baseURL = "http://export.arxiv.org/api/query?"
  
  val searchParams = "cat:cs.CV+OR+cat:cs.AI+OR+cat:cs.LG+OR+cat:cs.CL+OR+cat:cs.NE+OR+cat:stat.ML"
  
  def parseIdVersion(url:String): (String, Int) = {
    val idVersion = url.split("\\.").last.split("v")
    (idVersion(0), idVersion(1).toInt)
  }
  
  def attributeEquals(name: String, value: String)(node: Node) = {
    node.attribute(name).filter(_==value).isDefined
  }
    
  def scrapePaperInfo(startIndex:Int=8000, maxIndex:Int=10000) = {
    val query = "search_query=%s&sortBy=lastUpdatedDate&start=%d&max_results=%d"
    val completeURL = (baseURL + query).format(this.searchParams , startIndex, maxIndex)
    val page = Source.fromURL(completeURL).mkString
    val xml = XML.loadString(page)
    val entries = xml \ "entry"
    val ids = entries \ "id"
    val urls = ids.map(_.text)
    val pdfUrl = entries \ "link"
    
    val idsUrls = (pdfUrl.filter(x => x.attribute("title").isDefined 
        && x.attribute("type").isDefined) 
        map(_ \"@href") zip urls.map(this.parseIdVersion(_)))
        
    idsUrls
  }
 
  def addToDb(db: Database, id: String, ver: Int, url: String) = {
    val paperIdVersions = TableQuery[PaperIdVersions]
    val row = paperIdVersions.filter(p => p.id === id)
    val insertOrUpdateAction = (row.exists.result.flatMap { exists => 
      if(!exists) {
        paperIdVersions += (id, ver, url)
      } else {
        row.map(_.version).update(ver)
        row.map(_.url).update(url)
      }
    }).transactionally
    
    db.run(insertOrUpdateAction)
  }
}

object PaperScraper extends App {
  val ps = new PaperScraper()
  val idsUrls = ps.scrapePaperInfo()
  println("Scrapped Paper Info...")
  println("Starting database inserts...")
  val db = Database.forConfig("sqlitedb")
  idsUrls foreach {
    case(url, idVer) => 
      Await.result(ps.addToDb(db, idVer._1, idVer._2, url.text), 20 second)
  }
}
