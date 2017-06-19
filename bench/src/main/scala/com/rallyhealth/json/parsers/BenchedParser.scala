package com.rallyhealth.json.parsers

trait BenchedParser {

  def feedAndParse(bytes: Array[Byte]): Unit = feedAndParse(bytes, 0, bytes.length)

  def feedAndParse(bytes: Array[Byte], off: Int, len: Int): Unit

  def finish(): Unit
}

