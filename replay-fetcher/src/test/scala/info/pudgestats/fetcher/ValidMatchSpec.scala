package info.pudgestats.fetcher

import org.scalatest.FunSuite

class ValidMatchSpec 
  extends FunSuite
{
  test("ValidMatch with Completed should be complete") {
    assert(new ValidMatch(0, 0.toLong, 0, None, Status.Completed).isComplete)
  }

  test("ValidMatch with ReplayNotFound should be complete") {
    assert(new ValidMatch(0, 0.toLong, 0, None, Status.ReplayNotFound).isComplete)
  }

  test("Older matches should be prioritized over newer matches") {
    assert(new ValidMatch(0, 10.toLong, 0, None, Status.Unknown) > 
      new ValidMatch(0, 11.toLong, 0, None, Status.Unknown))
  }

  test("Newer matches should be prioritized below older matches") {
    assert(new ValidMatch(0, 11.toLong, 0, None, Status.Unknown) <
      new ValidMatch(0, 10.toLong, 0, None, Status.Unknown))
  }

  test("Serializing match should yield valid CSV") {
    assert(new ValidMatch(1, 2.toLong, 3, Some(4), Status.Unknown).serialize ==
      "1,2,3,4,3,")
  }

  test("Serializing a match without a replay salt " +  
       "should yield expected CSV") {
    assert(new ValidMatch(1, 2.toLong, 3, None, Status.Unknown).serialize == 
      "1,2,3,-1,3,")
  }

  test("Deserializing match should yield the same object") {
    assert(new ValidMatch().deserialize("1,2,3,4,3") ==
      new ValidMatch(1, 2.toLong, 3, Some(4), Status.Unknown))
  }

  test("Matches with different statuses are the same") {
    assert(new ValidMatch(674645840, 0.toLong, 0, None, Status.Unknown) ==
           new ValidMatch(674645840, 0.toLong, 0, None, Status.ReplayPending))
  }

  test("Matches with different IDs are considered different") {
    assert(new ValidMatch(496, 240.toLong, 0, None, Status.Unknown) !=
           new ValidMatch(497, 241.toLong, 0, None, Status.Unknown))
  }

  test("Matches with different SeqNums are considered different") {
    assert(new ValidMatch(496, 240.toLong, 0, None, Status.Unknown) !=
           new ValidMatch(498, 242.toLong, 0, None, Status.Unknown))
  }

  test("Matches with the same SeqNums and IDs are considered equal") {
    assert(new ValidMatch(498, 242.toLong, 0, None, Status.Unknown) ==
           new ValidMatch(498, 242.toLong, 0, None, Status.Unknown))
  }

  test("Difference of ValidMatch sets with same elements should be empty") {
    val set1 = Set(
      new ValidMatch(839611599, 242.toLong, 123, None, Status.Unknown),
      new ValidMatch(839611587, 243.toLong, 121, None, Status.Unknown),
      new ValidMatch(839619254, 244.toLong, 123, None, Status.Unknown),
      new ValidMatch(839596018, 245.toLong, 122, None, Status.Unknown),
      new ValidMatch(839612684, 246.toLong, 123, None, Status.Unknown))
    val set2 = Set(
      new ValidMatch(839611599, 242.toLong, 123, None, Status.Unknown),
      new ValidMatch(839611587, 243.toLong, 121, None, Status.Unknown),
      new ValidMatch(839619254, 244.toLong, 123, None, Status.Unknown),
      new ValidMatch(839596018, 245.toLong, 122, None, Status.Unknown),
      new ValidMatch(839612684, 246.toLong, 123, None, Status.Unknown))

    assert((set1 &~ set2) == Set())
  }

  test("Difference of ValidMatch sets with different but equivalent " + 
       "elements should be empty") {
    val set1 = Set(
      new ValidMatch(839611599, 242.toLong, 123, None, Status.Unknown),
      new ValidMatch(839611587, 243.toLong, 121, None, Status.Unknown),
      new ValidMatch(839619254, 244.toLong, 123, None, Status.Unknown),
      new ValidMatch(839596018, 245.toLong, 122, None, Status.Unknown),
      new ValidMatch(839612684, 246.toLong, 123, None, Status.Unknown))
    val set2 = Set(
      new ValidMatch(839611599, 242.toLong, 123, Some(1), Status.ReadyToDownload),
      new ValidMatch(839611587, 243.toLong, 121, Some(2), Status.ReadyToDownload),
      new ValidMatch(839619254, 244.toLong, 123, Some(3), Status.ReadyToDownload),
      new ValidMatch(839596018, 245.toLong, 122, Some(4), Status.ReadyToDownload),
      new ValidMatch(839612684, 246.toLong, 123, Some(5), Status.ReadyToDownload))

    assert((set1 &~ set2) == Set())
  }
}
