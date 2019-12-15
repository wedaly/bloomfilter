package bloomfilter

import org.scalatest._


class BloomFilterSpec extends FlatSpec {

    it should "return false for any word if the filter is empty" in {
      val filter = new BloomFilter(128, 3)
      assert(!filter.checkMaybeExists("foobar"))
    }

    it should "return true if the word is added to the filter" in {
      val filter = new BloomFilter(128, 3)
      filter.add("foobar")
      assert(filter.checkMaybeExists("foobar"))
    }

    it should "have a low false positive rate" in {
      val wordsInFilter = List("crow", "ducks", "interest", "noise", "ubiquitous", "wool")
      val otherWords = (0 until 1000).map(_.toString)
      val filter = new BloomFilter((wordsInFilter.length + otherWords.length) * 16, 3)
      wordsInFilter.foreach(word => filter.add(word))

      // The theoretical false positive rate is 0.005, so the expected number of false positives in 1000 items is 5.
      // Arbitrarily set the cutoff to 10, which should be sufficient to detect a regression
      // with a reasonably low probability of flaky test failures.
      val falsePositiveCount = otherWords.map(word => if (filter.checkMaybeExists(word)) 1 else 0).sum
      assert(falsePositiveCount <= 10)
    }

    it should "add and check words for any positive hash length" in {
      val words = (0 until 10000).map(_.toString)
      (1 until 16).foreach(numBitsPerHash => {
        val filter = new BloomFilter(numBitsPerHash, 1)
        words.foreach(word => filter.add(word))
        val allWordsInFilter: Boolean = words.map(word => filter.checkMaybeExists(word)).forall(exists => exists)
        assert(allWordsInFilter)
      })
    }
}
