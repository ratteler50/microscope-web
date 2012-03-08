INSERT INTO users (user_name, password, email) VALUES
  ('David', '$shiro1$SHA-256$500000$bAxt+ZkSpECAD6I5SwDAFA==$djPjAh3Tzl/H4b1/3afGVS5dhMcmN08uArPsKHueXIM=', 'David');
INSERT INTO users (user_name, password, email)
VALUES ('Ben', '$shiro1$SHA-256$500000$bAxt+ZkSpECAD6I5SwDAFA==$djPjAh3Tzl/H4b1/3afGVS5dhMcmN08uArPsKHueXIM=', 'Ben');
INSERT INTO users (user_name, password, email) VALUES
  ('Garrett', '$shiro1$SHA-256$500000$bAxt+ZkSpECAD6I5SwDAFA==$djPjAh3Tzl/H4b1/3afGVS5dhMcmN08uArPsKHueXIM=',
   'Garrett');
INSERT INTO users (user_name, password, email)
VALUES ('Sam', '$shiro1$SHA-256$500000$bAxt+ZkSpECAD6I5SwDAFA==$djPjAh3Tzl/H4b1/3afGVS5dhMcmN08uArPsKHueXIM=', 'Sam');
INSERT INTO users (user_name, password, email)
VALUES ('Sean', '$shiro1$SHA-256$500000$bAxt+ZkSpECAD6I5SwDAFA==$djPjAh3Tzl/H4b1/3afGVS5dhMcmN08uArPsKHueXIM=', 'Sean');

INSERT INTO games (active, big_picture, round, turn, lens, last_pes, last_card)
VALUES (1, 'Mankind leaves the sick Earth behind and spreads out to the stars', 1, 3, 1, 2, 1);
INSERT INTO games (active, big_picture, round, turn, lens, last_pes, last_card)
VALUES (1, 'The Moons of Khalidor Struggle for Supremacy', 11, 0, 1, 0, 14);

INSERT INTO players (game, userid, "position", palette_num, action_done) VALUES (1, 2, 2, 2, 0);
INSERT INTO players (game, userid, "position", palette_num, action_done) VALUES (1, 1, 1, 2, 0);
INSERT INTO players (game, userid, "position", palette_num, action_done) VALUES (1, 3, 4, 1, 0);
INSERT INTO players (game, userid, "position", palette_num, action_done) VALUES (1, 4, 3, 2, 0);
INSERT INTO players (game, userid, "position", palette_num, action_done) VALUES (2, 1, 1, 3, 0);
INSERT INTO players (game, userid, "position", palette_num, action_done) VALUES (2, 5, 2, 2, 0);
INSERT INTO players (game, userid, "position", palette_num, action_done) VALUES (2, 4, 3, 2, 0);
INSERT INTO players (game, userid, "position", palette_num, action_done) VALUES (2, 2, 4, 3, 0);

INSERT INTO palettes (player, description, in_game, game) VALUES (1, 'habitable worlds', 0, 1);
INSERT INTO palettes (player, description, in_game, game) VALUES (2, 'aliens', 1, 1);
INSERT INTO palettes (player, description, in_game, game) VALUES (3, 'MM/DD/YYYY format', 0, 1);
INSERT INTO palettes (player, description, in_game, game) VALUES (4, 'communication with aliens', 0, 1);
INSERT INTO palettes (player, description, in_game, game) VALUES (4, 'crossovers', 0, 1);
INSERT INTO palettes (player, description, in_game, game) VALUES (1, 'zombies', 0, 1);
INSERT INTO palettes (player, description, in_game, game) VALUES (2, 'talking animals', 1, 1);
INSERT INTO palettes (player, description, in_game, game) VALUES (1, 'MULTIPLE RACES', 1, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (2, 'MULTIPLE DIFFERENT MAGIC SYSTEMS', 1, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (5, 'TABOOED MAGIC', 1, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (4, 'DRAGONS', 1, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (1, 'TELEPORTATION', 0, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (2, 'BORING RELIGIONS', 0, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (5, 'RESURRECTION', 0, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (4, 'SACRIFICE MAGIC', 0, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (1, 'OMNIPOTENT GODS', 0, 2);
INSERT INTO palettes (player, description, in_game, game) VALUES (2, 'OMNIPRESENCE', 0, 2);

INSERT INTO focuses (focus, round, game, player) VALUES ('The sinking of the Gabriel Dora', 1, 1, 1);

INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('Humanity stagnates isolated and alone', '', 1, 4, 2, 0, 1, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('Mankind makes new life among stars', '', 0, 1, 2, 0, 1, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('"Unifiers" conquer multiple star systems', '', 1, 3, 4, 0, 1, 2);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('Mass settlement of alien Dyson-sphere', '', 0, 2, 4, 0, 1, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('CHARTING OF THE SKY', '', 0, 1, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('FIRST MEETINGS: WHEN THE DIFFERENT RACES BEGIN 1 COMMUNICATION AND MEETINGS', '', 0, 2, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('THE RISE AND FALL OF THE GOD KING', '', 0, 3, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('THE THICKENING: KHALIDOR STARTS EMITTING AN ATMOSPHERE THAT SHROUDS THE NEAREST MOONS', '', 0, 4, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('THE WAR OF WEALTH: COLD WAR BETWEEN THE CMA AND THE COM', '', 1, 5, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player) VALUES
  ('THE ERA OF SUCCESSFUL EXPERIMENTS: SEVERAL ENTIRELY NEW TECHNIQUES OF MAGIC ARE DEVELOPED', '', 0, 6, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('THE GODLING WAR', '', 1, 7, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('CULT WARS: CULTS VIE FOR POWER', '', 1, 8, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('THE HEGEMONY OF THE LUNAR ALLIANCE', '', 1, 9, 0, 11, 2, 1);
INSERT INTO periods (period, description, tone, "position", turn, round, game, player)
VALUES ('CONVERGENCE OF THE MOONS', '', 0, 10, 0, 11, 2, 1);

INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('Solar flares destory human settlements', '', 0, 3, 4, 0, 4, 3);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('Survey ship "Meadowlark" discovers sphere', '', 0, 2, 4, 0, 4, 4);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('Gabriel Dora sinks in the North Atlantic.  No known survivers', '', 0, 1, 1, 1, 4, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('VESUVIUS CRACKS CODE AND CAN ACCESS OTHER MOONS COMMUNICATION', '', 0, 1, 0, 11, 5, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('A TRIBE OF THE COMMUNITY IS EXPELLED FROM THE CONSENSUS', '', 1, 2, 0, 11, 5, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'A VESUVIAN SCIENTIST''S CHILD PLAYS A BOARD GAME WITH A TITANIAN CHILD. THIS IS THE VERY FIRST CONTACT BETWEEN THESE WORLDS',
  '', 0, 1, 0, 11, 6, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('VESUVIUS BEGINS CROSS MOON SHIPPING', '', 0, 2, 0, 11, 6, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('A COMMUNITY TRIBE BREAKS CONTACT WITH OTHER TRIBES AND SETTLE A NEARBY MOON, DIMINISHING THEIR MAGICAL POWER', '',
   1, 3, 0, 11, 6, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'GRAXIAN EXPEDITION GOES CLOSER TO KHALIDOR THAN EVER BEFORE - MOST DIE - ONLY SURVIVOR GOES ON TO BECOME THE GOD KING',
  '', 1, 1, 0, 11, 7, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('THE FOUNDING OF THE ORDER OF THE GODS, A SECRET ORGINIZATION BASED ON VESUVIUS.  GODS ARE PEOPLE', '', 0, 2, 0, 11,
   7, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('ROGUE GODS OF VARIOUS LEVELS GO PUBLIC, GAINING CULT FOLLOWINGS', '', 1, 3, 0, 11, 7, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('DETORR UNIFIES FOR FIRST TIME TO BETTER COMBAT FRACKISH THREAT', '', 0, 4, 0, 11, 7, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('THE GOD KING BEGINS HIS SLUMBER IN THE CORE OF HEPHAESTUS', '', 0, 5, 0, 11, 7, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('THE BLADES OF THE 1 FAITH FORMED TO ELIMINATE "GODS" AND BREAK UP THEIR CULTS', '', 0, 6, 0, 11, 7, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'A SMALL CULT OF GODLINGS LOYAL TO THE GOD KING MOVE HIS SLEEPING BODY TO THE CENTER OF KHALIDOR AS PART OF A RITUAL AT THE BEHEST OF THE GOD KING',
  '', 0, 7, 0, 11, 7, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('THE VESUVIAN GOD ORLACH ATTEMPTS TO EXTEND THE THICKENING TO VESUVIUS BUT IS FOILED BY UNKNOWN FORCES', '', 1, 1, 0,
   11, 8, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('OKARIAN MECHANICS INVENT NEW METHODS OF TRAVEL IN THE THICKENING - OPEN AIR SHIPS', '', 0, 2, 0, 11, 8, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('HEPHAESTUS DUE TO EXTREME HEAT BECOMES THE MOST PROSPEROUS MOON FROM TRADE, ASSISTED BY THE THICKENING', '', 0, 3,
   0, 11, 8, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('THE CORE MOON AFFILIATION IS FOUNDED BASED ON TRADE', '', 0, 4, 0, 11, 8, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('GRANADA LEAD FORMATION OF CONGLOMERATE OF OUTER MOONS WITH MOONS NOT IN THE CMA', '', 0, 5, 0, 11, 8, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('THE THICKENING EXPANDS, ENCOMPASSING TWICE AS MANY MOONS AS BEFORE', '', 0, 6, 0, 11, 8, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('TITANIA, PART OF COM IS NOW IN THE THICKENING.  THEY ARE KICKED FROM COM AND DISALLOWED IN CMA', '', 1, 7, 0, 11, 8,
   1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('BOTH THE CMA AND COM BYLAWS MUST BE REWRITTEN WHEN THE THICKENING SUDDENLY DISAPPEARS', '', 1, 8, 0, 11, 8, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('UNPRECEDENTED DISEASE AND FAMINE SPREAD THROUGH THE INNER PLANETS', '', 1, 9, 0, 11, 8, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('AGLAR STARTS LENDING THEIR EXPERTISE AS ASSASINS', '', 1, 1, 0, 11, 9, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('AGLAR ADOPT INDIVIDUAL NAMES TO AID IN DEALING WITH OUTSIDERS', '', 0, 2, 0, 11, 9, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('REEK THE FIRST''S HOME MOON COLLIDES INTO KHALIDOR', '', 1, 1, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('DIVINE VESSELS FORMED AS CULT OF HORSEMEN-WORSHIPING GODLINGS IN SECRET, TAKING ON ASPECTS THEREOF', '', 1, 2, 0,
   11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('REEK, A GOD OF DEATH, INTRODUCES BLOOD MAGIC WHICH IS QUICKLY TABOOED', '', 1, 3, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('AN AGLAR BLADE ASSASSINATES REEK THE FIRST USING REEK''S OWN BLOOD MAGIC', '', 0, 4, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'MALLORAN TRIBESMEN INVENT MAGIC THAT TAKES MEMORIES AND STORES THEM IN DAGGERS.  DAGGERS = MULTIPLE USE; MOST TARGETS DIE',
  '', 1, 5, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('THE SOURCE OF ALL KNOWN FORMS OF MAGIC IS DISCOVERED TO FLOW FROM KHALIDOR ITSELF', '', 0, 6, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('OKARIAN MECHANICS CREATE CRYSTAL FORTRESS THAT INCREASES CORE WORLD DEFENSES IN THE THICKENING', '', 0, 7, 0, 11,
   10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('THE DIVINE VESSELS SELECT REEK II AS THE NEW GOD OF DEATH', '', 0, 8, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('OKAR DEVELOPS A WAY TO BOOST POWER TO GODLING LEVELS THROUGH USE OF DIAMONDS', '', 1, 9, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'CONSESUS OF COMMUNITY TRIBES RENAME HOME PLANET FROM HOMELAND TO INDULGENCE IN PENANCE FOR YEARS OF MAGICAL EXCESSES',
  '', 0, 10, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('REEK II WINS REVOLUTIONARY WAR AND FOUNDS CITY OF BELFONG', '', 1, 11, 0, 11, 10, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('THE GODS DECIDE TO ADD MORE GODS FROM THE GODLINGS, AND THE GODLINGS DISPUTE ESCALATES TO WAR', '', 1, 1, 0, 11, 11,
   1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('BELFONGIAN DRACOMORPHS HIGHLY SOUGHT AFTER AS MERCENARIES BY ALL SIDES IN GODLING WAR', '', 0, 2, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'JARL, THE THIRD SON OF THE KING OF GRANADA, IS POPULARLY ELECTED BY A LANDSLIDE AS THE LEADER OF THE BELFONG COUNCIL',
  '', 0, 3, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('KYLAR, GOD OF PESTILENCE, TAMPERS WITH TITANIAN RITUAL TO CAUSE DEVESTATING DISEASE', '', 1, 4, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'TITANIA MAGES ACCIDENTALLY DESTROY THE ENTIRE MOON VESUVIUS, KILLING EVERYONE ON IT.  EVERYONE ELSE TURNS ON TITANIA',
  '', 1, 5, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('GRANADAN MAGE TEAM ASSESSES VESUVIUS, PUBLICLY DECLARES IT FIT FOR INHABITANCE, DESPITE KNOWING THE CONTRARY', '',
   1, 6, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('INJURED VESUVIUS GODLING HORATIO CAPTURED BY CMA', '', 1, 7, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('BELFONG PEACE ACCORD SIGNED, ESTABLISHING VESUVIUS AS A REFUGEE MOON, AND THE HEADQUARTERS OF THE LUNAR ALLIANCE',
   '', 0, 8, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'LINGERING EFFECTS ON VESUVIUS LEAD TO ANYONEY''S DEATH AFTER EXACTLY 2 KHALIDORAN YEARS ACCUMULATED ON THE MOON.  HEADQUARTERS MOVEED TO INDULGENCE SINCE 2 YEARS IS TOO SHORT A TIME TO DEAL WITH LEGISLATION.',
  '', 1, 9, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('VESUVIUS USED FOR PRISON', '', 1, 10, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('DEVREEN BECOMES A GODLING WITH ONE BODY AT A TIME BEING A GODLING', '', 1, 11, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES (
  'CMA FRAMED FOR MURDER OF JARL, LEADING TO DISSOLUTION AND ALL ASSETS SEIZED BY COM.  CMA LEADERS SENT TO PRISON PLANET.',
  '', 0, 12, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('BLADES OF THE 1 FAITH SUCCESSFULLY ASSASSINATE THE GODLING OF DEVREEN', '', 0, 13, 0, 11, 11, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player) VALUES
  ('PRISONERS ON VESUVIUS MAKE A CULT TO THE FOUR HORSEMEN AND SOME BECOME PATHOMANCERS PROLONGING THEIR LIFE', '', 1,
   1, 0, 11, 12, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('THE DIVINE VESSELS AND THEIR FOLLOWERS BRING ABOUT THE DEATH OF REEK VII', '', 0, 2, 0, 11, 12, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('LUNAR ALLIANCE MANDATES DECIMATION OF COMMUNITY TRIBES IN ORDER TO CURB THEIR POWER', '', 1, 1, 0, 11, 13, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('THE AGLAR AND MALLORA ARE SECRETLY REINDUCTED INTO THE CONSENSUS', '', 0, 2, 0, 11, 13, 1);
INSERT INTO events (event, description, tone, "position", turn, round, period, player)
VALUES ('SELF PROCLAIMED JARL II LEADS POPULAR REVOLUTION AND SENDS ASSEMBLY TO VESUVIUS', '', 0, 3, 0, 11, 13, 1);

INSERT INTO characters (name, description) VALUES ('None', '');
INSERT INTO characters (name, description) VALUES ('An assasian', '');
INSERT INTO characters (name, description) VALUES ('The captain''s son', '');
INSERT INTO characters (name, description) VALUES ('The captain', '');
INSERT INTO characters (name, description) VALUES ('The captain''s wife', '');
INSERT INTO characters (name, description) VALUES ('The duke of whales', '');

INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (0, 0, 1, 2, 1, 1, 3, 1, 'What caused the Gabriel Dora to sink?', 'The grand dining room on the Gabriel',
           'The assassian destroyed the ship to kill the duke', '', 2, NULL, 1, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (1, 1, 3, 0, 11, 0, 46, 1, 'HOW DID JARL''S POSITION CHANGE WHILE IN SECLUSION ON VESUVIUS?',
           '- THE STALKER KILLED GRANADAN ROYAL FAMILY WITH DAGGERS', 'JARL LEFT AS SOLE HEIR TO GRANADAN THRONE', '',
        NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (0, 1, 1, 0, 11, 0, 5, 1, 'WHAT PERCIPITATED THE EXILE?',
           '- FARMING MAGIC - FRACK COME FROM HOMELAND - 15 TRIBES - MALLORA BECOME ARTISTIC EXILES',
           'MALLORA OPENED MIND FRIGHTENED THE CONSENSUS', '', NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (1, 1, 1, 0, 11, 0, 14, 1, 'WHY WAS THE BLADES OF THE 1 FAITH ESTABLISHED?',
           '- SCHISM WITHIN THE ORDER OF THE GODS - FOUR HORSEMEN ARE GODS FROM A DIFFERENT PLANET - AGLAR AND FRACKS ON DIFFERENT PLANET',
           'FRACK VILLAGERS BLAME GODLING FOR PLAGUE, AND FEAR UPCOMING GOD WAR', '', NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (0, 1, 1, 0, 11, 0, 15, 1, 'WHY DID THE GOD KING CREATE A RITUAL TO CREATE THE THICKENING?',
           '- THICKENING MEANT TO ENCOMPASS ALL MOONS', 'TO PROTECT THE KHALIDOR SYSTEM FROM THE FOUR HORSEMEN', '',
        NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (1, 1, 1, 0, 11, 0, 49, 1, 'HOW WAS THE CMA DISBANDED?', '',
           'DEVREEN AND CULT FOLLOWING OVERTHROW THE CMA, DEVREEN ASSUMES FULL CONTROL OF THE MOONS', '', NULL, NULL,
        NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (0, 1, 1, 0, 11, 0, 19, 1, 'WAS HOMELAND ACCEPTED INTO THE CMA?',
           '-  MEMBER OF DEVREEN TRIBE LOCATED ON ALL CORE PLANETS TO INCREASE COMMUNICATION SPEED',
           'YES, BY A NARROW MARGIN AS A PROBATIONARY MEMBER', '', NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (1, 1, 1, 0, 11, 0, 38, 1, 'HOW DID REEK IV THE GOD OF DEATH INCITE THE GODLING WAR?',
           '- INTENSITY OF MAGIC INFLUENCES WAKING GOD KING - VESUVIAN GODLING IS NAMED HORATIO',
           'GODLINGS INCITED BY THIRST FOR POWER DEMONSTRATED BY THE GOD OF DEATH', '', NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (1, 1, 1, 0, 11, 0, 50, 1, 'HOW DID THE AGLAR KILL DEVREEN?',
           '- CRYSTAL FORTRESSES STILL AROUND, THOUGH ABANDONED AND MOVABLE - CMA LOYALISTS EXIST',
           'PUSH CASTLE TOWARDS PLANET, ASSASSINATE HIM ON A SPACESHIP', '', NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (1, 1, 1, 0, 11, 0, 42, 1, 'WHAT WERE THE REPERCUSSIONS FOR TITANIA AFTER THE INCIDENT?',
           '- GOD KING SLEEPS A LOT - GODLING CRITICALLY WOUNDED BY CORE CONSPIRACY - GODLING POWERED THE SPELL - A COMMUNITY TRIBE KNOWS "EVERYTHING"',
           'NEUTRAL CORE WORLD AFFILIATION GETS INVOLVED', '', NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (1, 1, 1, 0, 11, 0, 46, 1, 'WHY DID THEY CHOOSE INDULGENCE TO MOVE THE HQ?',
           '- CMA INVOLVED WITH SPELL GOING WRONG - INDULGENCE NEAR CMA / TITANIA - TITANIA IN GOOD TERMS - GRANADA ASSASSINATED BELFONG REPRESENTATIVE',
           'SEVERAL COUNCILORS LEFT THE MEETING, ALLOWING MINORTY VOTE TO PASS', '', NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (0, 1, 2, 0, 11, 0, 46, 1,
           'WHY DID THE GRANADAN HIGH COUNCIL SEND AN ASSASIN TO KILL JARL, A MAN THEY KNEW WAS GOING TO DIE?',
           '- FRAMING CMA PLANNED BY PLACING KNIVES - MOVEMENT OF DIAMONDS, AND MORE AS PAYMENT - THE STALKER',
           'INFORMATION IN KNIVES (LIKELY RELATED TO PLAGUE OR JARL)', '', NULL, NULL, NULL, NULL);
INSERT INTO scenes (tone, dictated, "position", turn, round, steps_left, event, player, question, setting, answer, description, banned1, banned2, required1, required2)
VALUES (0, 1, 1, 0, 11, 0, 54, 1, 'HOW DOES THE COMMUNITY PLAN TO STRIKE BACK AGAINST THE LUNAR ALLIANCE?',
           '- JARL II HAS THE DAGGERS - MALLORA ARE INDIVIDUALS - AGLAR ASSASSINATED GRANADAN ROYALTY',
           'RECOMBINE SOME MALLORANS TO GETHER INTELLIGENCE FOR AN EVENTUAL ATTACK', '', NULL, NULL, NULL, NULL);


INSERT INTO inscene (role, scene, player, banned) VALUES (2, 1, NULL, 1);
INSERT INTO inscene (role, scene, player, banned) VALUES (3, 1, 4, 0);
INSERT INTO inscene (role, scene, player, banned) VALUES (4, 1, 3, 0);
INSERT INTO inscene (role, scene, player, banned) VALUES (1, 1, 2, 0);
INSERT INTO inscene (role, scene, player, banned) VALUES (5, 1, 1, 0);


INSERT INTO legacies (legacy, round, game, player) VALUES ('THE BLADES OF THE 1 FAITH', 11, 2, 1);
INSERT INTO legacies (legacy, round, game, player) VALUES ('THE COMMUNITY', 11, 2, 5);
INSERT INTO legacies (legacy, round, game, player) VALUES ('THE FOUR HORSEMEN', 11, 2, 4);
INSERT INTO legacies (legacy, round, game, player) VALUES ('THE GOD KING', 11, 2, 2);
