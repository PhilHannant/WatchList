import scala.collection.mutable.ListBuffer

case class WatchList() {

  lazy val watchList: ListBuffer[String] = new ListBuffer[String]


  def addContentIDs(contentID: String):Unit = {
    watchList += contentID
  }

  def getContentIDs(): List[String] = {
    watchList.toList
  }

  def deleteContentID(contentID: String) = {
    if(watchList.contains(contentID)){
      watchList -= contentID
    }
  }
}

