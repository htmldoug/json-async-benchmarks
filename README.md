# json-async-benchmarks

Async json parser implementation performance tests.

## Results

Implementations:
* [jackson-core-2.9.0.pr4](https://github.com/FasterXML/jackson-core/releases/tag/jackson-core-2.9.0.pr4)
* [actson 1.2.0](https://github.com/michel-kraemer/actson/releases/tag/v1.2.0)

Tests:
* `bookstoreAllBytesAtOnce` - Feeds [bookstore.json](bench/src/main/resources/examples/bookstore.json) to the parser in a single byte[] array.
* `oneHundredBookStoresStreamed` - Feeds an json array of 100 [bookstore.json](bench/src/main/resources/examples/bookstore.json) instances to the parser, one bookstore at a time.


```
> bench/jmh:run -wi 15 -i 30 -f3 -t1 .*
[info] Benchmark                                    (implementation)   Mode  Cnt       Score      Error  Units
[info] JsonBenchmarks.bookstoreAllBytesAtOnce                 actson  thrpt   90   91527.435 ± 1423.217  ops/s
[info] JsonBenchmarks.bookstoreAllBytesAtOnce                jackson  thrpt   90  228358.584 ± 3672.275  ops/s
[info] JsonBenchmarks.oneHundredBookStoresStreamed            actson  thrpt   90    1018.986 ±   22.096  ops/s
[info] JsonBenchmarks.oneHundredBookStoresStreamed           jackson  thrpt   90    3335.238 ±   60.136  ops/s
```
Last tested: June, 2017.
