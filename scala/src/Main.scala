import scala.io.Source

/**
  * Created by duccao on 12/02/16.
  */
object Main extends App {

  val filename = "input/sample.in"
  for (line <- Source.fromFile(filename).getLines) {
    println(line)
  }

}
