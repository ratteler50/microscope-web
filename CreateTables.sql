CREATE TABLE USERS (
  id        INTEGER PRIMARY KEY,
  user_name TEXT NOT NULL,
  password  TEXT NOT NULL,
  email     TEXT NOT NULL,
  UNIQUE (user_name),
  UNIQUE (email)
);


CREATE TABLE GAMES (
  id          INTEGER PRIMARY KEY,
  active      BOOLEAN NOT NULL,
  big_picture TEXT,
  round       INTEGER NOT NULL,
  turn        INTEGER NOT NULL,
  lens        INTEGER NOT NULL REFERENCES USERS (id),
  last_pes    INTEGER CHECK (last_pes >= 0 AND last_pes < 3),
  last_card   INTEGER
);


CREATE TABLE PLAYERS (
  game        INTEGER NOT NULL REFERENCES GAMES (id),
  userid      INTEGER NOT NULL REFERENCES USERS (id),
  position    INTEGER NOT NULL,
  palette_num INTEGER NOT NULL,
  action_done BOOLEAN NOT NULL,
  PRIMARY KEY (game, userid)
);

CREATE TABLE PALETTES (
  id          INTEGER PRIMARY KEY,
  player      INTEGER NOT NULL REFERENCES USERS (id),
  description TEXT    NOT NULL,
  in_game     BOOLEAN NOT NULL,
  game        INTEGER NOT NULL REFERENCES GAMES (id)
);

CREATE TABLE FOCUSES (
  id     INTEGER PRIMARY KEY,
  focus  TEXT    NOT NULL,
  round  INTEGER NOT NULL,
  game   INTEGER NOT NULL REFERENCES GAMES (id),
  player INTEGER NOT NULL REFERENCES USERS (id)


);

CREATE TABLE LEGACIES (
  id     INTEGER PRIMARY KEY,
  legacy TEXT    NOT NULL,
  round  INTEGER NOT NULL,
  game   INTEGER NOT NULL REFERENCES GAMES (id),
  player INTEGER REFERENCES USERS (id)

);


CREATE TABLE PERIODS (
  id          INTEGER PRIMARY KEY,
  period      TEXT    NOT NULL,
  description TEXT    NOT NULL,
  tone        BOOLEAN NOT NULL,
  position    INTEGER NOT NULL,
  turn        INTEGER NOT NULL,
  round       INTEGER NOT NULL,
  game        INTEGER NOT NULL REFERENCES GAMES (id),
  player      INTEGER NOT NULL REFERENCES USERS (id)
);

CREATE TABLE EVENTS (
  id          INTEGER PRIMARY KEY,
  event       TEXT    NOT NULL,
  description TEXT    NOT NULL,
  tone        BOOLEAN NOT NULL,
  position    INTEGER NOT NULL,
  turn        INTEGER NOT NULL,
  round       INTEGER NOT NULL,
  period      INTEGER NOT NULL REFERENCES PERIODS (id),
  player      INTEGER NOT NULL REFERENCES USERS (id)
);

CREATE TABLE CHARACTERS (
  id          INTEGER PRIMARY KEY,
  name        TEXT NOT NULL,
  description TEXT NOT NULL
);

-- The blank character #0 to make things cleaner
INSERT INTO CHARACTERS (id, name, description) VALUES (0, 'None', '');

CREATE TABLE SCENES (
  id          INTEGER PRIMARY KEY,
  tone        BOOLEAN NOT NULL,
  dictated    BOOLEAN NOT NULL,
  position    INTEGER NOT NULL,
  turn        INTEGER NOT NULL,
  round       INTEGER NOT NULL,
  steps_left  INTEGER NOT NULL,
  event       INTEGER NOT NULL REFERENCES EVENTS (id),
  player      INTEGER NOT NULL REFERENCES USERS (id),
  question    TEXT    NOT NULL,
  setting     TEXT    NOT NULL,
  answer      TEXT    NOT NULL,
  description TEXT    NOT NULL,
  banned1     INTEGER REFERENCES CHARACTERS (id),
  banned2     INTEGER REFERENCES CHARACTERS (id),
  required1   INTEGER REFERENCES CHARACTERS (id),
  required2   INTEGER REFERENCES CHARACTERS (id)
);

CREATE TABLE INSCENE (
  id     INTEGER PRIMARY KEY,
  role   INTEGER NOT NULL REFERENCES CHARACTERS (id),
  scene  INTEGER NOT NULL REFERENCES SCENES (id),
  player INTEGER REFERENCES USERS (id),
  banned BOOLEAN NOT NULL
);