package com.rallyhealth.json

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

import com.rallyhealth.json.JsonBenchmarks.BenchmarkState
import com.rallyhealth.json.parsers.{ActsonParser, BenchedParser, JacksonParser}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import scala.io.Source

/**
  * ==Quick Run from sbt==
  *
  * > bench/jmh:run -wi 5 -i 10 -f1 -t1 .*
  *
  * Which means "10 iterations" "5 warmup iterations" "1 fork" "1 thread".
  * Benchmarks should be usually executed at least in 10 iterations (as a rule of thumb), but more is better.
  *
  *
  * ==Using Oracle Flight Recorder==
  *
  * Flight Recorder / Java Mission Control is an excellent tool shipped by default in the Oracle JDK distribution.
  * It is a profiler that uses internal APIs (commercial) and thus is way more precise and detailed than your every-day profiler.
  *
  * To record a Flight Recorder file from a JMH run, run it using the jmh.extras.JFR profiler:
  * > bench/jmh:run -prof jmh.extras.JFR -t1 -f 1 -wi 10 -i 20 .*
  *
  * This will result in flight recording file which you can open and analyze offline using JMC.
  * Start with "jmc" from a terminal.
  *
  * @see https://github.com/ktoso/sbt-jmh
  */
class JsonBenchmarks {

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput))
  def bookstoreAllBytesAtOnce(state: BenchmarkState, bh: Blackhole): Unit = {
    val parser = state.parser(bh)
    parser.feedAndParse(state.storeBytes)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput))
  def oneHundredBookStoresStreamed(state: BenchmarkState, bh: Blackhole): Unit = {
    val parser = state.parser(bh)
    parser.feedAndParse(state.startArray)

    var i = 1
    val total = 100
    while (i <= total) {
      parser.feedAndParse(state.storeBytes)
      if (i != total) {
        parser.feedAndParse(state.comma)
      }
      i += 1
    }

    parser.feedAndParse(state.endArray)
  }
}

object JsonBenchmarks {

  @State(Scope.Benchmark)
  class BenchmarkState {

    @Param(Array("actson", "jackson"))
    var implementation: String = _

    def parser(bh: Blackhole): BenchedParser = implementation match {
      case "actson" => new ActsonParser(bh = bh)
      case "jackson" => new JacksonParser(bh = bh)
    }

    val storeBytes: Array[Byte] = {
      Source.fromInputStream(getClass.getResourceAsStream("/examples/bookstore.json"))
        .mkString
        .getBytes(StandardCharsets.UTF_8)
    }

    val startArray: Array[Byte] = "[".getBytes("UTF-8")
    val endArray: Array[Byte] = "]".getBytes("UTF-8")
    val comma: Array[Byte] = ",".getBytes("UTF-8")
  }

}
