package observatory

case class Station(Stn: String, Wban: String)

case class Location(lat: Double, lon: Double)

case class Tile(x: Int, y: Int, zoom: Int)

case class GridLocation(lat: Int, lon: Int)

case class CellPoint(x: Double, y: Double)

case class Color(red: Int, green: Int, blue: Int)
