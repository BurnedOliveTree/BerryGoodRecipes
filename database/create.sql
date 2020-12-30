drop table UNIT_SYSTEM cascade constraints;
drop table UNIT cascade constraints;
drop table "USER" cascade constraints;
drop table FOLLOWED cascade constraints;
drop table "GROUP" cascade constraints;
drop table BELONG cascade constraints;
drop table INGREDIENT cascade constraints;
drop table SHOPPING_LIST cascade constraints;
drop table RECIPE cascade constraints;
drop table INGREDIENT_LIST cascade constraints;
drop table FAVORITE cascade constraints;
drop table PUBLICITY cascade constraints;
drop table OPINION cascade constraints;
drop table REPORTED cascade constraints;

CREATE TABLE Unit_system
(
    name VARCHAR2(40) NOT NULL  CONSTRAINT Unit_system_pk PRIMARY KEY
);

CREATE TABLE Unit
(
    Name VARCHAR2(40) CONSTRAINT Unit_pk PRIMARY KEY,
    Unit_system_id VARCHAR2(40) NOT NULL CONSTRAINT Unit_Unit_system_fk REFERENCES Unit_system (name),
    Liter_per_unit_ratio NUMBER(7,2) NOT NULL,
    Table_spoon_per_unit_ratio NUMBER(7,2) NOT NULL,
    Tea_spoon_per_unit_ratio NUMBER(7,2) NOT NULL,
    Glass_per_unit_ratio NUMBER(7,2) NOT NULL,
    Gram_per_unit_ratio NUMBER(7,2) NOT NULL
);

create table "USER"
(
    USERNAME varchar2(40 byte) not null constraint USER_PK primary key,
    PASSWORD varchar2(40 byte) not null,
    UNIT_SYSTEM_ID VARCHAR2(40) constraint USER_UNIT_SYSTEM_FK references UNIT_SYSTEM (NAME)
);

create table FOLLOWED
(
    FOLLOWED_ID number(4) generated by default on null as identity constraint FOLLOWED_PK primary key,
    FOLLOWING_USERNAME varchar2(40 byte) not null constraint FOLLOWING_USER_FK references "USER" (USERNAME),
    FOLLOWED_USERNAME varchar2(40 byte) not null constraint FOLLOWED_USER_FK references "USER" (USERNAME)
);

create table "GROUP"
(
    GROUP_ID number(4) generated by default on null as identity constraint GROUP_PK primary key,
    NAME varchar2(40 byte) not null
);

create table BELONG
(
    BELONG_ID number(4) generated by default on null as identity constraint BELONG_PK primary key,
    GROUP_ID number(4) not null constraint BELONG_GROUP_FK references "GROUP" (GROUP_ID),
    USERNAME varchar2(40 byte) not null constraint BELONG_USER_FK references "USER" (USERNAME)
);

create table INGREDIENT
(
    NAME varchar2(40 BYTE) constraint INGREDIENT_PK primary key,
    DENSITY number(4, 2),
    STANDARD_UNIT constraint STANDARD_UNIT_FK references UNIT(NAME)
);

create table SHOPPING_LIST
(
    SHOPPING_LIST_ID number(4) generated by default on null as identity constraint SHOPPING_LIST_PK primary key,
    AMOUNT number(4) not null,
    INGREDIENT_NAME varchar2(40) not null constraint SHOPPING_INGREDIENT_FK references INGREDIENT (NAME),
    USERNAME varchar2(40 byte) not null constraint SHOPPING_USER_FK references "USER" (USERNAME),
    GROUP_ID number(4) not null constraint SHOPPING_GROUP_FK references "GROUP" (GROUP_ID)
);

alter table "GROUP" add SHOPPING_LIST_ID number(4) constraint GROUP_SHOPPING_FK references SHOPPING_LIST (SHOPPING_LIST_ID);

create table RECIPE
(
  RECIPE_ID number(4) generated by default on null as identity constraint RECIPE_PK primary key,
  OWNER_NAME varchar2(40 byte) not null constraint OWNER_USER_FK references "USER" (USERNAME),
  NAME varchar2(1000 BYTE) not null,
  PREPARATION_METHOD varchar2(4000 BYTE) not null,
  COST number(4, 2),
  DATE_ADDED date default sysdate not null,
  PREPARATION_TIME date,
  PORTIONS number(4) default 0 not null CHECK (PORTIONS >= 0)
);

create table INGREDIENT_LIST
(
    INGREDIENT_LIST_ID number(4) generated by default on null as identity constraint INGREDIENT_LIST_PK primary key,
    RECIPE_ID number(4) not null constraint IL_RECIPE_ID_FK references RECIPE(RECIPE_ID),
    AMOUNT number(4),
    INGREDIENT_UNIT varchar2(40) not null constraint INGREDIENT_UNIT_FK references UNIT(NAME),
    INGREDIENT_NAME varchar2(40) not null constraint IL_INGREDIENT_ID_FK references INGREDIENT(NAME)
);

create table FAVORITE
(
    FAVORITE_ID number(4) generated by default on null as identity constraint FAVORITE_PK primary key,
    USERNAME varchar2(40 byte) not null constraint FAV_USER_ID_FK references "USER"(USERNAME),
    RECIPE_ID number(4) not null constraint FAV_RECIPE_ID_FK references RECIPE(RECIPE_ID)
);

create table PUBLICITY
(
    PUBLICITY_ID number(4) generated by default on null as identity constraint PUBLICITY_PK primary key,
    GROUP_ID number(4) constraint  PUB_GROUP_ID references "GROUP"(GROUP_ID),
    RECIPE_ID number(4) constraint PUB_RECIPE_ID references RECIPE(RECIPE_ID)
);

create table OPINION
(
    OPINION_ID number(4) generated by default on null as identity constraint OPINION_PK primary key,
    USERNAME VARCHAR2(40 byte) not null constraint OPINION_USER_FK references "USER"(USERNAME),
    RECIPE_ID number(4) not null constraint OPINION_RECIPE_ID_FK references RECIPE(RECIPE_ID),
    SCORE number(4) not null,
    "COMMENT" varchar2(40 byte)
);

create table REPORTED
(
    REPORTED_ID number(4) generated by default on null as identity constraint REPORTED_PK primary key,
    REPORTING_USER varchar2(40 byte) not null constraint REPORTED_USER_ID_FK references "USER"(USERNAME),
    OPINION_ID number(4) constraint REPORTED_OPINION_ID references OPINION(OPINION_ID)
);