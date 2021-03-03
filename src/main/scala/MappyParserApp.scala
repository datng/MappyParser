import java.io.File
import scala.annotation.tailrec
import scala.io.Source

object MappyParserApp {

  type ViewMode = String
  type ZoomLevel = String

  def getTile(line: String): Option[String] = {
    val secondElemOfSplit = line.split("\t")
    if (secondElemOfSplit.length == 2) {
      Some(secondElemOfSplit(1)).filter(isTile)
    } else None
  }

  def extractViewMode(tile: String): String = {
    tile.split("/")(4)
  }

  def extractZoomLevel(tile: String): String = {
    tile.split("/")(6)
  }

  def isTile(line: String): Boolean = {
    line.startsWith("/map/1.0/slab/") && line.split("/").length > 7
  }

  def getViewModeList(dataset: List[String]): List[String] = {
    dataset
      .flatMap(getTile)
      .map(extractViewMode)
  }

  def getViewModeAndZoomLevelList(dataset: List[String]): List[String] = {
    dataset
      .flatMap(getTile)
      .map(tile => s"${extractViewMode(tile)},${extractZoomLevel(tile)}")
  }

  def compressViewModeListRec(viewModeList: List[String]): List[String] = {
    if (viewModeList.isEmpty) List.empty
    else {
      val head = viewModeList.head
      val (repeatingElements, otherElements) = viewModeList.span(_ == head)
      s"$head\t${repeatingElements.length}" :: compressViewModeListRec(otherElements)
    }
  }

  @tailrec
  def compressViewModeListTailRec(viewModeList: List[String], acc: List[String] = List.empty): List[String] = {
    viewModeList match {
      case head :: _ =>
        val (repeatingElements, otherElements) = viewModeList.span(_ == head)
        compressViewModeListTailRec(otherElements, acc :+ s"$head\t${repeatingElements.length}")
      case Nil => acc
    }
  }

  def compressViewModeWithZoomLevelListRec(viewModeAndZoomLevelList: List[String]): List[String] = {
    if (viewModeAndZoomLevelList.isEmpty) List.empty
    else {
      val head = getViewModeAndZoomLevelTuple(viewModeAndZoomLevelList.head)
      val (repeatingElements, otherElements) =
        viewModeAndZoomLevelList.span(elem => getViewModeAndZoomLevelTuple(elem)._1 == head._1)
      val zoomLevels = repeatingElements.map(getViewModeAndZoomLevelTuple(_)._2).toSet.mkString(",")
      s"${head._1}\t${repeatingElements.length}\t$zoomLevels" :: compressViewModeWithZoomLevelListRec(otherElements)
    }
  }

  @tailrec
  def compressViewModeWithZoomLevelListTailRec(viewModeAndZoomLevelList: List[String], acc: List[String] = List.empty): List[String] = {
    viewModeAndZoomLevelList match {
      case head :: _ =>
        val first = getViewModeAndZoomLevelTuple(head)
        val (repeatingElements, otherElements) =
          viewModeAndZoomLevelList.span(elem => getViewModeAndZoomLevelTuple(elem)._1 == first._1)
        val zoomLevels = repeatingElements.map(getViewModeAndZoomLevelTuple(_)._2).toSet.mkString(",")
        compressViewModeWithZoomLevelListTailRec(otherElements, acc :+ s"${first._1}\t${repeatingElements.length}\t$zoomLevels")
      case Nil => acc
    }
  }

  def getViewModeAndZoomLevelTuple(elem: String): (ViewMode, ZoomLevel) = {
    val split = elem.split(",")
    (split(0), split(1))
  }

  def main(args: Array[String]): Unit = {

    if (args.length == 0) {
      Console.println(s"No argument provided, default dataset will be used !")
      val smallDatasetSrc = Source.fromResource("small_dataset.tsv")
      val datasetList = smallDatasetSrc.getLines().toList
      val viewModeAndZoomLevelList = getViewModeAndZoomLevelList(datasetList)
      compressViewModeWithZoomLevelListTailRec(viewModeAndZoomLevelList).foreach(println)
      smallDatasetSrc.close()
    } else {
      val logFile = Source.fromFile(new File(args(0)))
      val datasetList = logFile.getLines().toList
      val viewModeAndZoomLevelList = getViewModeAndZoomLevelList(datasetList)
      compressViewModeWithZoomLevelListTailRec(viewModeAndZoomLevelList).foreach(println)
      logFile.close()
    }
  }
}
