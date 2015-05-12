package info.pudgestats.web.model

import java.sql.Timestamp

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.internals.OutMapper
import org.squeryl.annotations.Transient
import org.squeryl.dsl.ast.TokenExpressionNode
import org.squeryl.dsl.{CompositeKey2, NumericalExpression}

import api.steam.data.{SteamUser => SteamApiUser}

import info.pudgestats.core.Const
import info.pudgestats.web.util.{ExtendedTable, HasDuplicate, SqlTimestampSupport}

/** Type of score that associated members have */
object ScoreType extends Enumeration {
  type ScoreType        = Value
  val NoScoreType       = Value(0, "No Score Type")
  val HookEnemyAccuracy = Value(1, "Hook Accuracy vs. Enemy Heroes (Avg.)")
}

class Leaderboard(
    val id: Int,
    val scoreType: ScoreType.ScoreType,
    val timestamp: Timestamp = new Timestamp(System.currentTimeMillis))
  extends KeyedEntity[Int]
  with SqlTimestampSupport
{
  def this() = this(0, ScoreType.NoScoreType)

  def this(scoreType: ScoreType.ScoreType) = this(0, scoreType)

  lazy val userAssociations = 
    LeaderboardsStore.leaderboardMembersUsers.left(this)

  lazy val members = 
    this.userAssociations.toList.sortBy(_.score).reverse

  override def toString = s"<Leaderboard timestamp=$timestamp>"
}

/** Extra data about a leaderboard member */ 
class LeaderboardMember(
    val leaderboardId: Int,
    val steamUserId: String,
    val score: Float)
  extends KeyedEntity[CompositeKey2[Int, String]]
{
  def this(steamUserId: String, score: Float) = this(0, steamUserId, score)

  def id = compositeKey(leaderboardId, steamUserId)

  @Transient
  lazy val steamUser = SteamUsersStore.users.lookup(this.steamUserId).get
}

object LeaderboardsStore extends Schema {
  val leaderboards = table[Leaderboard]
  val leaderboardMembers = table[LeaderboardMember]

  val leaderboardMembersUsers = 
    oneToManyRelation(leaderboards, leaderboardMembers).
      via((l, m) => l.id === m.leaderboardId)

  /** Special class to get around some of the limitations of squeryl.
    * Combines a Type and raw Token expression.
    */
  private class TypedTokenExpressionNode[T](
      val s: String)(
      implicit val mapper: OutMapper[T])
    extends TokenExpressionNode(s)
    with NumericalExpression[T]

  /** Expressions */
  private val hooksEnemyHeroesHitExpr = 
    new TypedTokenExpressionNode[Float](
      "CAST(\"numHooksEnemyHeroesHit\" AS Decimal)")

  /** Gets the leaderboard by hook accuracy (on enemies). Uses some 
    * squeryl tricks and hardcoding to get around some innate limitations
    */
  def leaderboardByHookAccuracy(size: Int, thresh: Int = 2) = { 
    join(
      MatchesStore.matches, 
      from(PlayersStore.players)(p => 
        where(p.hero === Const.PudgeNPCString)
        select(p)))((m, p) => 
      groupBy(p.steamId)
      having(count(m.matchId) > thresh)
      compute(avg(hooksEnemyHeroesHitExpr div m.numHooksUsed))
      orderBy(new TokenExpressionNode("c0 DESC"))
      on(m.id === p.matchId)).page(0, size).toList
  }
}
