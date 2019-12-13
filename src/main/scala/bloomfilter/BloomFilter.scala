package bloomfilter

import scala.util.Random
import scala.util.hashing.MurmurHash3

// A Bloom filter is a probabilistic data structure used to test set membership.
// Its accuracy is controlled by two parameters:
//  * numBitsPerHash is the number of bits allocated for each hash function
//  * numHashes is the number of distinct hash functions
// Setting these values higher reduces the probability of false positives
// at the cost of additional memory.
class BloomFilter(numBitsPerHash: Int, numHashes: Int) {

  // Store all bits in a single flat byte array
  private val bitMap: Array[Byte] = new Array(numHashes * numBitsPerHash / 8)

  // Generate a random seed for each hash function
  private val hashSeeds: Array[Int] = (0 until numHashes).map(_ => Random.nextInt()).toArray

  // Add a string to the Bloom filter
  def add(s: String): Unit = {
    (0 until numHashes)
      .foreach(hashIdx => setBitForHash(hashIdx, hashString(hashIdx, s)))
  }

  // Check that a string exists in the Bloom filter.
  // This may return false positives but never false negatives.
  def checkMaybeExists(s: String): Boolean = {
    (0 until numHashes)
      .forall(hashIdx => checkBitForHash(hashIdx, hashString(hashIdx, s)))
  }

  private def hashString(hashIdx: Int, s: String): Int = {
    // Use murmur3 as the hash function because it is fast to calculate
    // and included in the Scala standard library.
    // I don't think murmur3 guarantees that hash functions with different seeds
    // are independent, but in practice it's probably close enough.
    MurmurHash3.stringHash(s, hashSeeds(hashIdx))
  }

  private def setBitForHash(hashIdx: Int, hash: Int): Unit = {
    val i = bitOffset(hashIdx, hash)
    bitMap(i / 8) = (bitMap(i / 8) | (0x01 << (i % 8))).toByte
  }

  private def checkBitForHash(hashIdx: Int, hash: Int): Boolean = {
    val i = bitOffset(hashIdx, hash)
    val bitValue = (bitMap(i / 8) >> (i % 8)) & 0x01
    bitValue > 0
  }

  private def bitOffset(hashIdx: Int, hash: Int): Int = {
    // Use Math.floorMod instead of the % operator to ensure that the offset is always positive.
    // Modding the hash value like this could make some values more likely than others,
    // but I expect numBitsPerHash will usually be large enough that this probably
    // won't hurt the accuracy significantly in practice.
    hashIdx * numBitsPerHash + Math.floorMod(hash, numBitsPerHash)
  }
}
