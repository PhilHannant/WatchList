import org.scalatest._

class WatchListTest extends FlatSpec with Matchers{


  val watchList = WatchList()

  "a watchList" should "take a contentID, add it to a list and return a list of ContentIDs" in {
    watchList.addContentIDs("zRE49")
    watchList.addContentIDs("wYqiZ")
    watchList.addContentIDs("15nW5")
    val expected = List("zRE49", "wYqiZ", "15nW5")
    watchList.getContentIDs() should contain theSameElementsAs expected

  }

}
