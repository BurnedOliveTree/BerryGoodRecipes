drop table USER;
drop table FOLLOWED;
drop table INGREDIENT;
drop table RECIPE;
drop table INGREDIENT_LIST;
drop table FAVORITE;
drop table PUBLICITY;

CREATE TABLE Unit_system
(
Unit_system_id NUMBER(4) GENERATED BY DEFAULT ON NULL AS IDENTITY CONSTRAINT Unit_system_pk PRIMARY KEY,
name VARCHAR2(40) NOT NULL
);

CREATE TABLE Unit
(
Unit_id NUMBER(4) GENERATED BY DEFAULT ON NULL AS IDENTITY CONSTRAINT Unit_pk PRIMARY KEY,
Unit_system_id NUMBER(4) NOT NULL CONSTRAINT Unit_Unit_system_fk REFERENCES Unit_system (Unit_system_id),
Name VARCHAR2(40) NOT NULL,
Liter_per_unit_ratio NUMBER(7,2) NOT NULL,
Table_spoon_per_unit_ratio NUMBER(7,2) NOT NULL,
Tea_spoon_per_unit_ratio NUMBER(7,2) NOT NULL,
Glass_per_unit_ratio NUMBER(7,2) NOT NULL,
Gram_per_unit_ratio NUMBER(7,2) NOT NULL
);

create table "USER"
(
    USER_ID number(4) generated by default on null as identity constraint USER_PK primary key,
    USERNAME varchar2(40) not null,
    PASSWORD varchar2(40) not null,
    UNIT_SYSTEM_ID number(4) constraint USER_UNIT_SYSTEM_FK references UNIT_SYSTEM (UNIT_SYSTEM_ID)
);

create table FOLLOWED
(
    FOLLOWED_ID number(4) generated by default on null as identity constraint FOLLOWED_PK primary key,
    FOLLOWING_USER_ID number(4) not null constraint FOLLOWING_USER_FK references "USER" (USER_ID),
    FOLLOWED_USER_ID number(4) not null constraint FOLLOWED_USER_FK references "USER" (USER_ID)
);

create table INGREDIENT
(
    INGREDIENT_ID number(4) generated by default on null as identity constraint INGREDIENT_PK primary key,
    NAME varchar2(40 BYTE) not null,
    DENSITY number(4, 2),
    STANDARD_UNIT_ID constraint STANDARD_UNIT_FK references UNIT(UNIT_ID)
);

create table RECIPE
(
  RECIPE_ID number(4) generated by default on null as identity constraint RECIPE_PK primary key,
  OWNER_ID number(4) not null constraint OWNER_USER_FK references "USER" (USER_ID),
  NAME varchar2(40 BYTE) not null,
  PREPARATION_METHOD varchar2(4000 BYTE) not null,
  COST number(4),
  DATE_ADDED date,
  PREPARATION_TIME date,
  PORTIONS number(4) CHECK (PORTIONS >= 0)
);

create table INGREDIENT_LIST
(
    INGREDIENT_LIST_ID number(4) generated by default on null as identity constraint INGREDIENT_LIST_PK primary key,
    AMOUNT number(4),
    INGREDIENT_UNIT number(4) not null constraint INGREDIENT_UNIT_FK references UNIT(UNIT_ID),
    RECIPE_ID number(4) not null constraint IL_RECIPE_ID_FK references RECIPE(RECIPE_ID),
    INGREDIENT_ID number(4) not null constraint IL_INGREDIENT_ID_FK references INGREDIENT(INGREDIENT_ID)
);

create table FAVORITE
(
    FAVORITE_ID number(4) generated by default on null as identity constraint FAVORITE_PK primary key,
    USER_ID number(4) not null constraint FAV_USER_ID_FK references "USER"(USER_ID),
    RECIPE_ID number(4) not null constraint FAV_RECIPE_ID_FK references RECIPE(RECIPE_ID)
);

create table PUBLICITY
(
    PUBLICITY_ID number(4) generated by default on null as identity constraint PUBLICITY_PK primary key,
    GROUP_ID number(4) constraint  PUB_GROUP_ID references GROUP(GROUP_ID),
    RECIPE_ID number(4) constraint PUB_RECIPE_ID references RECIPE(RECIPE_ID)
);