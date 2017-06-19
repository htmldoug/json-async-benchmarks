package com.rallyhealth.json.parsers

import java.nio.charset.StandardCharsets

import de.undercouch.actson.JsonEvent._
import de.undercouch.actson.JsonParser
import org.openjdk.jmh.infra.Blackhole

import scala.annotation.switch

class ActsonParser(
  parser: JsonParser = new JsonParser(StandardCharsets.UTF_8),
  bh: Blackhole
) extends BenchedParser {

  private val feeder = parser.getFeeder

  override def feedAndParse(bytes: Array[Byte], off: Int, len: Int): Unit = {
    var o = off
    var l = len

    while (l > 0) {
      var consumed = feed(bytes, o, l)
      o += consumed
      l -= consumed

      var event: Int = nextToken()
      while (event != NEED_MORE_INPUT) {
        consumeData(event)
        event = nextToken()
      }
    }
  }

  private def feed(bytes: Array[Byte], o: Int, l: Int) = {
    feeder.feed(bytes, o, l)
  }

  private def nextToken(): Int = parser.nextEvent()

  private def consumeData(event: Int) = {
    (event: @switch) match {
      case FIELD_NAME => bh.consume(parser.getCurrentString)
      case VALUE_STRING => bh.consume(parser.getCurrentString)
      case VALUE_INT => bh.consume(parser.getCurrentInt)
      case VALUE_DOUBLE => bh.consume(parser.getCurrentDouble)
      case VALUE_TRUE => bh.consume(parser.getCurrentString.toBoolean)
      case VALUE_FALSE => bh.consume(parser.getCurrentString.toBoolean)
      case ERROR => throw new IllegalStateException("parsing error")
      case _ => // nothing to do
    }
  }

  override def finish(): Unit = {
    parser.getFeeder.done()
  }
}
