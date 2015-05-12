package info.pudgestats.fetcher

import org.scalatest.FunSuite

class SchedulerSpec 
  extends FunSuite
{
  def generateTestAccounts(n: Int): List[SteamAccount] = {
    val testAcc1 = new SteamAccount("test1", "test1", "test1")
    val testAcc2 = new SteamAccount("test2", "test2", "test2")
    val testAcc3 = new SteamAccount("test3", "test3", "test3")
    List(testAcc1, testAcc2, testAcc3).take(n)
  }
  
  def hoursInMillis(hrs: Int): Long = hrs * 3600 * 1000

  test("A BasicAccountScheduler with no accounts should always return None") {
    val scheduler = new BasicAccountScheduler(List())
    assert(scheduler.nextAccount.isEmpty)
    assert(scheduler.nextAccount.isEmpty)
    assert(scheduler.nextAccount.isEmpty)
  }

  test("A BasicAccountScheduler with an account, and the default batch size " + 
       "should be scheduled for 24 hours later") {
    val scheduler = new BasicAccountScheduler(generateTestAccounts(1))
    val time = System.currentTimeMillis
    val acct = scheduler.nextAccount match {
      case Some(a) => a
      case None => fail("A valid account wasn't returned")
    }
    assert(acct.nextActive >= time + hoursInMillis(24)) 
  }

  test("A BasicAccountScheduler with an account, and the default batch size " + 
       "should not return an account after one is checked out") {
    val scheduler = new BasicAccountScheduler(generateTestAccounts(1))
    val acct = scheduler.nextAccount
    assert(scheduler.nextAccount.isEmpty)
  }

  test("A BasicAccountScheduler with 2 accounts, and the default batch size " + 
       "should return an account after one is checked out") {
    val scheduler = new BasicAccountScheduler(generateTestAccounts(2))
    val acct = scheduler.nextAccount
    assert(!scheduler.nextAccount.isEmpty)
  }

  test("A BasicAccountScheduler with 1 account, and a batch size of 0 " + 
       "should return an account always")
  {
    val scheduler = new BasicAccountScheduler(generateTestAccounts(1), 0)
    assert(!scheduler.nextAccount.isEmpty)
    assert(!scheduler.nextAccount.isEmpty)
  }

  test("A BasicAccountScheduler with 1 account, and a batch size of 25 " + 
       "should return an account where the next active time is 6 hours " + 
       "later") {
    val scheduler = new BasicAccountScheduler(generateTestAccounts(1), 25)
    val time = System.currentTimeMillis
    val acct = scheduler.nextAccount match {
      case Some(a) => assert(a.nextActive >= time + hoursInMillis(6))
      case None => fail("A valid account wasn't returned")
    }
  }

  test("A BasicAccountScheduler with 2 accounts, and a batch size of 25 " + 
       "should return an account after one is checked out") {
    val scheduler = new BasicAccountScheduler(generateTestAccounts(2), 25)
    assert(!scheduler.nextAccount.isEmpty)
    assert(!scheduler.nextAccount.isEmpty)
  }

  test("A BasicAccountScheduler with 3 accounts, and a batch sizse of 25 " + 
       "should return no account after all 3 are checked out") {
    val scheduler = new BasicAccountScheduler(generateTestAccounts(3), 25)
    assert(!scheduler.nextAccount.isEmpty)
    assert(!scheduler.nextAccount.isEmpty)
    assert(!scheduler.nextAccount.isEmpty)
    assert(scheduler.nextAccount.isEmpty)
  }
}
