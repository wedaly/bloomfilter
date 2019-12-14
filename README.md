# Bloom filter

Bloom filter implementation in Scala.

To build the project, you can either use [sbt on the command line](https://docs.scala-lang.org/getting-started/sbt-track/getting-started-with-scala-and-sbt-on-the-command-line.html) or [import the project into IntelliJ](https://www.jetbrains.com/help/idea/sbt-support.html).

To run the example program: `sbt "run /usr/share/dict/words"`

The example program loads the word list into a Bloom filter, then provides an interactive command-line prompt to test if a word is in the filter.

To run the unit tests: `sbt test`
