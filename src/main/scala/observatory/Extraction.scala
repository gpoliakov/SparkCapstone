package observatory

import Utils._
import java.time.LocalDate

import org.apache.spark.sql.SparkSession

/**
  * 1st milestone: data extraction
  */
object Extraction {

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("ScalaCapstone")
      .config("spark.master", "local")
      .getOrCreate()


  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    val file = fsPath(stationsFile)
    val stationsData = spark.sparkContext.textFile(file)
    val temperaturesData = spark.sparkContext.textFile(file)

    val stations = stationsData
      .map(_.split(','))
      .filter(_.length == 4)
      .map(a => (Station(a(0), a(1)), Location(a(2).toDouble, a(3).toDouble)))

    val temperatures = temperaturesData
      .map(_.split(','))
      .filter(_.length == 5)
      .map(a => (Station(a(0), a(1)), (LocalDate.of(year, a(2).toInt, a(3).toInt), fahrenheitToCelsius(a(4).toDouble))))

    stations.join(temperatures)
      .mapValues{case (location, (localDate, temperature)) => (localDate, location, temperature)}
      .values.collect().toSeq
  }


  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    spark.sparkContext.parallelize(records.toSeq)
      .groupBy{case (localDate, location, temperature) => location}
      .mapValues(entries => entries.map{case (localDate, location, temperature) => (temperature, 1)})
      .mapValues(_.reduce((ent1, ent2) => (ent1._1 + ent2._1, ent1._2 + ent2._2)))
      .mapValues({case (sumTemperatures, cnt) => sumTemperatures / cnt}).collect().toSeq
  }

}
