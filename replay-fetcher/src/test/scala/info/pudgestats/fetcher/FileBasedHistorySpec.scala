package info.pudgestats.fetcher

import java.nio.file.{Paths, Files}

import org.scalatest.FunSuite

class FileBasedHistorySpec 
  extends FunSuite
{
  val hpath = "/tmp/pudge_stats_history_tmp.log"

  override def withFixture(test: NoArgTest) = {
    val result = super.withFixture(test) 
    Files.deleteIfExists(Paths.get(hpath))
    result
  }

  test("FileBasedHistory should create a new file if one does not exist.") {
    val history = new FileBasedHistory(hpath)
    assert(Files.exists(Paths.get(hpath)))
  }

  test("FileBasedHistory should save a match properly without exception.") {
    val history = new FileBasedHistory(hpath)
    history.push(new ValidMatch(0, 0, 0, None, Status.Unknown)) 
    assert(true)
  }

  test("FileBasedHistory should save a match to an empty history, and " +
       "the match should deserialize properly") {
    val history = new FileBasedHistory(hpath)
    history.push(new ValidMatch(1, 2, 3, Some(4), Status.Unknown))
    history.populate
    assert(history.history.head == new ValidMatch(1, 2, 3, Some(4), Status.Unknown))
  }

  test("FileBasedHistory should not include duplicates in the history " +
       "after populating.") {
    val history = new FileBasedHistory(hpath)
    history.push(new ValidMatch(809611878, 726909264, 0, None, Status.Unknown))
    history.push(new ValidMatch(809611878, 726909264, 0, None, Status.Unknown))
    history.push(new ValidMatch(809611878, 726909264, 0, None, Status.Unknown))
    history.push(new ValidMatch(809611878, 726909264, 0, None, Status.Unknown))
    history.populate
    assert(history.history.size == 1)
  }

  test("Underlying FixedLengthQueue should maintain its limit when given " + 
       "an excess of matches.") {
    val history = new FileBasedHistory(hpath, 2)
    history.push(new ValidMatch(809611878, 726909264, 0, None, Status.Unknown))
    history.push(new ValidMatch(880961057, 726909351, 0, None, Status.Unknown))
    history.push(new ValidMatch(880961059, 726907722, 0, None, Status.Unknown))
    history.populate
    assert(history.history.size == 2)
  }

  test("Underlying FixedLength queue should prefer matches pushed later " + 
       "when duplciates in the history file exist.") {
    val history = new FileBasedHistory(hpath)
    history.push(new ValidMatch(880961057, 726909351, 0, None, Status.Unknown))
    history.push(new ValidMatch(880961057, 726909351, 0, None, Status.ReplayPending))
    history.populate
    assert(history.history.head.status == Status.ReplayPending)
  }

  test("Populating history should put older items first") { 
    val history = new FileBasedHistory(hpath)
    history.push(new ValidMatch(809611878, 726909264, 0, None, Status.Unknown))
    history.push(new ValidMatch(880961057, 726909351, 0, None, Status.Unknown))
    history.push(new ValidMatch(880961059, 726907722, 0, None, Status.Unknown))
    history.populate
    val historyArray = history.history.toArray
    assert(historyArray(0).id == 809611878)
    assert(historyArray(1).id == 880961057)
    assert(historyArray(2).id == 880961059)
  }

  test("Populating history from a file should have a nonempty history") {
    val history = new FileBasedHistory(getClass.getResource("/history.log"))
    history.populate
    assert(history.history.size != 0)
  }
}
