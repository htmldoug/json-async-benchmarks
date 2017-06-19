package com.rallyhealth.json.parsers

import com.fasterxml.jackson.core.JsonTokenId._
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser
import com.fasterxml.jackson.core.{JsonFactory, JsonTokenId}
import org.openjdk.jmh.infra.Blackhole

import scala.annotation.switch

class JacksonParser(
  parser: NonBlockingJsonParser = new JsonFactory().createNonBlockingByteArrayParser().asInstanceOf[NonBlockingJsonParser],
  bh: Blackhole
) extends BenchedParser {

  override def feedAndParse(bytes: Array[Byte], off: Int, len: Int): Unit = {
    feed(bytes, off, len)

    var event: Int = nextToken()
    while (event != JsonTokenId.ID_NOT_AVAILABLE) {
      consumeData(event)
      event = nextToken()
    }
  }

  private def feed(bytes: Array[Byte], off: Int, len: Int) = {
    parser.feedInput(bytes, off, len)
  }

  private def nextToken(): Int = parser.nextToken().id()

  private def consumeData(event: Int) = {
    (event: @switch) match {
      case ID_FIELD_NAME => bh.consume(parser.getValueAsString)
      case ID_STRING => bh.consume(parser.getValueAsString)
      case ID_NUMBER_INT => bh.consume(parser.getIntValue)
      case ID_NUMBER_FLOAT => bh.consume(parser.getFloatValue)
      case ID_TRUE => bh.consume(parser.getBooleanValue)
      case ID_FALSE => bh.consume(parser.getBooleanValue)
      case _ => // nothing to do
    }
  }

  override def finish(): Unit = parser.close()
}
