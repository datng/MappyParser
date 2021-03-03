object MappyParserTestFixture {

  val smallDatasetName = "small_dataset.tsv"
  val biggerDatasetName = "tornik-map-20171006.10000.tsv"

  val expectedResultSmallDataSet: List[String] = List(
    "standard\t4",
    "traffic\t1",
    "traffic_hd\t2",
    "standard_hd\t1",
    "traffic\t2",
    "standard\t2",
    "public_transport_hd\t1",
    "standard\t4",
    "standard_hd\t1"
  )

  val expectedResultWithZoomLevel: List[String] = List(
    "standard\t4\t19,12,14",
    "traffic\t1\t14",
    "traffic_hd\t2\t12",
    "standard_hd\t1\t14",
    "traffic\t2\t14,17",
    "standard\t2\t19,17",
    "public_transport_hd\t1\t15",
    "standard\t4\t18,19,14",
    "standard_hd\t1\t18"
  )
}
