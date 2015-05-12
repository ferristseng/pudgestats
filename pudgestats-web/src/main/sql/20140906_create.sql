-- table declarations :
create table "SteamUser" (
    "name" varchar(128) not null,
    "avatarFull" varchar(128) not null,
    "avatar" varchar(128) not null,
    "timestamp" timestamp not null,
    "steamId" varchar(128) not null,
    "id" varchar(128) primary key not null,
    "profileUrl" varchar(128) not null,
    "avatarMedium" varchar(128) not null
  );

-- table declarations :
create table "SteamUserSession" (
    "timestamp" timestamp not null,
    "id" varchar(128) primary key not null
  );

-- table declarations :
create table "ParserVersion" (
    "versionName" varchar(128) not null,
    "versionNum" varchar(128) not null,
    "id" integer primary key not null
  );
create sequence "s_ParserVersion_id";

-- column group indexes :
create unique index "idxb080e6b" on "ParserVersion" ("versionNum","versionName");

-- table declarations :
create table "HookEvent" (
    "illusion" boolean not null,
    "timestamp" real not null,
    "hit" boolean not null,
    "id" integer primary key not null,
    "target" varchar(128) not null,
    "matchId" integer not null
  );
create sequence "s_HookEvent_id";
create table "PlayerItemEvent" (
    "name" varchar(128) not null,
    "timestamp" real not null,
    "id" integer primary key not null,
    "entityHandle" integer not null,
    "matchId" integer not null
  );
create sequence "s_PlayerItemEvent_id";
create table "KillEvent" (
    "timestamp" real not null,
    "id" integer primary key not null,
    "target" varchar(128) not null,
    "matchId" integer not null
  );
create sequence "s_KillEvent_id";
create table "RuneEvent" (
    "timestamp" real not null,
    "rune" varchar(128) not null,
    "id" integer primary key not null,
    "matchId" integer not null
  );
create sequence "s_RuneEvent_id";
create table "DeathEvent" (
    "illusion" boolean not null,
    "timestamp" real not null,
    "id" integer primary key not null,
    "attacker" varchar(128) not null,
    "matchId" integer not null
  );
create sequence "s_DeathEvent_id";
create table "PlayerAbilityEvent" (
    "name" varchar(128) not null,
    "timestamp" real not null,
    "id" integer primary key not null,
    "entityHandle" integer not null,
    "matchId" integer not null,
    "level" integer not null
  );
create sequence "s_PlayerAbilityEvent_id";
create table "DismemberEvent" (
    "illusion" boolean not null,
    "timestamp" real not null,
    "damage" integer not null,
    "id" integer primary key not null,
    "target" varchar(128) not null,
    "matchId" integer not null
  );
create sequence "s_DismemberEvent_id";

-- table declarations :
create table "Player" (
    "name" varchar(128) not null,
    "steamId" bigint not null,
    "id" integer primary key not null,
    "hero" varchar(128) not null,
    "gameTeam" integer not null,
    "matchId" integer not null
  );
create sequence "s_Player_id";

create table "Leaderboard" (
    "timestamp" timestamp not null,
    "scoreType" integer not null,
    "id" integer primary key not null
  );
create sequence "s_Leaderboard_id";
create table "LeaderboardMember" (
    "score" real not null,
    "leaderboardId" integer not null,
    "steamUserId" varchar(128) not null
  );

-- foreign key constraints :
alter table "LeaderboardMember" add constraint "LeaderboardMemberFK1" foreign key ("leaderboardId") references "Leaderboard"("id");

-- composite key indexes :
alter table "LeaderboardMember" add primary key ("leaderboardId","steamUserId");

-- table declarations :
create table "Match" (
    "numHooksEnemyHeroesHit" integer not null,
    "duration" real not null,
    "winner" integer not null,
    "numHooksNeutralsHit" integer not null,
    "selfRotDamage" integer not null,
    "heroRotDamage" integer not null,
    "id" integer primary key not null,
    "numHooksCreepsHit" integer not null,
    "neutralRotDamage" integer not null,
    "numHooksUsed" integer not null,
    "numHooksAlliedHeroesHit" integer not null,
    "mode" integer not null,
    "numHooksIllusionsHit" integer not null,
    "matchId" integer not null,
    "creepRotDamage" integer not null,
    "parserId" integer not null
  );
create sequence "s_Match_id";

-- foreign key constraints :
alter table "Match" add constraint "MatchFK1" foreign key ("parserId") references "ParserVersion"("id");
alter table "Player" add constraint "PlayerFK2" foreign key ("matchId") references "Match"("id");
alter table "DeathEvent" add constraint "DeathEventFK3" foreign key ("matchId") references "Match"("id");
alter table "DismemberEvent" add constraint "DismemberEventFK4" foreign key ("matchId") references "Match"("id");
alter table "HookEvent" add constraint "HookEventFK5" foreign key ("matchId") references "Match"("id");
alter table "RuneEvent" add constraint "RuneEventFK6" foreign key ("matchId") references "Match"("id");
alter table "KillEvent" add constraint "KillEventFK7" foreign key ("matchId") references "Match"("id");
alter table "PlayerItemEvent" add constraint "PlayerItemEventFK8" foreign key ("matchId") references "Match"("id");
alter table "PlayerAbilityEvent" add constraint "PlayerAbilityEventFK9" foreign key ("matchId") references "Match"("id");
