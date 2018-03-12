package observatory

import Utils._
import org.apache.log4j.{Level, Logger}

object Main extends App {

  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  val dir = "target/temperatures"

  val temps = Extraction.locateTemperatures(2015, "/stations.csv", "/1975.csv")
  val tempsAvg = Extraction.locationYearlyAverageRecords(temps)



  val data = List[(Year, Iterable[(Location, Temperature)])]((2015, tempsAvg))
  Interaction.generateTiles[Iterable[(Location, Temperature)]](data, generateAndSaveTile)
}
