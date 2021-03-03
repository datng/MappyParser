import MappyParserApp._
import MappyParserTestFixture._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class MappyParserAppTest extends AnyFunSuite with Matchers {

  test("test getTile method") {
    val line1 = s"2017-10-05 00:01:09,676	/map/1.0/slab/traffic/256/9/260/177"
    val line2 = s"2017-10-05 00:01:10,528"

    getTile(line1) shouldEqual Some(s"/map/1.0/slab/traffic/256/9/260/177")
    getTile(line2) shouldEqual None
  }

  test("test extractViewMode method") {
    val tile = s"/map/1.0/slab/traffic/256/9/260/177"
    extractViewMode(tile) shouldEqual s"traffic"
  }

  test("test extractZoomLevel method") {
    val tile = s"/map/1.0/slab/traffic/256/9/260/177"
    extractZoomLevel(tile) shouldEqual s"9"
  }

  test("test isTile method") {
    val tile = s"/map/1.0/slab/standard/256/19/263920/186677"
    val notTile = s"/map/1.0/multi-descr/standard_hd/256/13/7773,6273;7773,6274;7774,6273;7774,6274;7775,6273"

    isTile(tile) shouldBe true
    isTile(notTile) shouldBe false
  }

  test("test getViewModeList method") {
    val dataset =
      List("2017-10-05 00:01:09,745\t/map/1.0/slab/standard/256/19/263920/186677",
        "2017-10-05 00:01:09,752\t/map/1.0/slab/standard/256/12/2056/1387",
        "2017-10-05 00:01:09,772\t/map/1.0/slab/standard/256/14/8338/5848")

    val expectedResult = List("standard", "standard", "standard")

    getViewModeList(dataset) shouldEqual expectedResult
  }

  test("test getViewModeAndZoomLevelList method") {
    val dataset =
      List("2017-10-05 00:01:09,745\t/map/1.0/slab/standard/256/19/263920/186677",
        "2017-10-05 00:01:09,752\t/map/1.0/slab/standard/256/12/2056/1387",
        "2017-10-05 00:01:09,772\t/map/1.0/slab/standard/256/14/8338/5848")

    val expectedResult = List("standard,19", "standard,12", "standard,14")

    getViewModeAndZoomLevelList(dataset) shouldEqual expectedResult
  }

  test("test small dataset") {
    val file = Source.fromResource(smallDatasetName)
    val datasetList = file.getLines().toList
    val viewModeList = getViewModeList(datasetList)
    val viewModeListCompressed = compressViewModeListTailRec(viewModeList)
    viewModeListCompressed shouldEqual expectedResultSmallDataSet
    file.close()
  }

  test("test small dataset with zoom level") {
    val file = Source.fromResource(smallDatasetName)
    val datasetList = file.getLines().toList
    val viewModeList = getViewModeAndZoomLevelList(datasetList)
    compressViewModeWithZoomLevelListTailRec(viewModeList) shouldEqual expectedResultWithZoomLevel
    file.close()
  }

  test("test big dataset") {
    val file = Source.fromResource(biggerDatasetName)
    val datasetList = file.getLines().toList
    val viewModeList = getViewModeAndZoomLevelList(datasetList)
    compressViewModeWithZoomLevelListTailRec(viewModeList).foreach(println)
    file.close()
  }

}
