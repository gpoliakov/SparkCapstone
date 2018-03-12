package observatory

import java.io.File
import java.nio.file.{Files, Paths}

import scala.math._


object Utils {
  def fsPath(resource: String): String = Paths.get(getClass.getResource(resource).toURI).toString

  def fahrenheitToCelsius(fahrenheit: Temperature): Temperature = (fahrenheit - 32) / 1.8

  // great-circle distance
  def computeDist(a: Location, b: Location): Double = {
    if(a == b){
      0
    }else if((a.lat == -b.lat) && (abs(a.lon - b.lon) == 180)){
      earthRadius * math.Pi
    } else {
      val delta_lon = toRadians(abs(a.lon - b.lon))
      val alat = toRadians(a.lat)
      val blat = toRadians(b.lat)
      val delta_lat = abs(alat - blat)
      val delta_sigma =   2 * asin(sqrt(pow(sin(delta_lat/2), 2) + cos(alat) * cos(blat) * pow(sin(delta_lon/2), 2) ))
      earthRadius * delta_sigma
    }
  }

  def generateAndSaveTile(year: Year, tile: Tile, data: Iterable[(Location, Temperature)]): Unit = {
    val zoom = tile.zoom
    val x = tile.x
    val y = tile.y
    val outdir = "target/temperatures"
    val dir = f"$outdir%s/$year%d/$zoom%d"
    val file = f"$dir%s/$x%d-$y%d.png"
    Files.createDirectories(Paths.get(dir))

    val tempToCol = List[(Temperature, Color)]((60, Color(255, 255, 255)),
                                               (32, Color(255, 0, 0)),
                                               (12, Color(255, 255, 0)),
                                                (0, Color(0, 255, 255)),
                                               (-15, Color(0, 0, 255)),
                                               (-27, Color(255, 0, 255)),
                                               (-50, Color(33, 0, 107)),
                                                (-60, Color(0, 0, 0)))

    val img = Interaction.tile(data, tempToCol, tile)
    img.output(new File(file))
  }
}
