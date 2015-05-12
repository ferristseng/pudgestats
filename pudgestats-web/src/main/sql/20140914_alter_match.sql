-- alter match tables with nullable definitions :
alter table "Match" add "updated" timestamp;
alter table "Match" add "timestamp" timestamp;

-- update matches and set the updated to the current time : 
update "Match" set "updated" = current_timestamp;

-- set updated in match to not null
alter table "Match" alter column "updated" set not null;

-- this is the first part... 
-- update data here, then run the second part!
