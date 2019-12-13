package bloomfilter

import scala.io.{Source, StdIn}

object Main {

  private val NUM_BITS_PER_HASH: Int = 1 << 21
  private val NUM_HASHES: Int = 3

  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      println(s"Usage: DICTIONARY_FILE")
      System.exit(1)
    }

    val dictionaryFilePath = args(0)
    val bloomFilter = new BloomFilter(NUM_BITS_PER_HASH, NUM_HASHES)

    println(s"Loading dictionary from '${dictionaryFilePath}'...")
    val dictionarySource = Source.fromFile(dictionaryFilePath)
    for (word <- dictionarySource.getLines) {
      bloomFilter.add(word)
    }

    println("Type a word to check if it is in the bloom filter:")
    var word = ""
    while ({word = StdIn.readLine; word != null}) {
      if (bloomFilter.checkMaybeExists(word)) {
        println(s"Word '$word' is probably in the dictionary")
      } else {
        println(s"Word '$word' is definitely not in the dictionary")
      }
    }
  }
}
