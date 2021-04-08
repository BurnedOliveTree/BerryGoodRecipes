drop table UNIT_SYSTEM cascade;
drop table UNIT cascade;
drop table "USER" cascade;
drop table FOLLOWED cascade;
drop table "GROUP" cascade;
drop table BELONG cascade;
drop table INGREDIENT cascade;
drop table SHOPPING_LIST cascade;
drop table RECIPE cascade;
drop table INGREDIENT_LIST cascade;
drop table FAVORITE cascade;
drop table PUBLICITY cascade;
drop table OPINION cascade;
drop table REPORTED cascade;

create table UNIT_SYSTEM
(
    NAME varchar(40) not null constraint UNIT_SYSTEM_PK primary key
);

create table UNIT
(
    NAME varchar(40) not null constraint UNIT_PK primary key,
    UNIT_SYSTEM_ID varchar(40) not null constraint Unit_Unit_system_fk references UNIT_SYSTEM(NAME),
    LITER_PER_UNIT_RATIO numeric(8,5) not null
);

create table "USER"
(
    USERNAME varchar(40) not null constraint USER_PK primary key,
    PASSWORD varchar(40) not null,
    UNIT_SYSTEM_ID varchar(40) default 'Default' not null constraint USER_UNIT_SYSTEM_FK references UNIT_SYSTEM (NAME)
);

create table FOLLOWED
(
    FOLLOWED_ID serial constraint FOLLOWED_PK primary key,
    FOLLOWING_USERNAME varchar(40) not null constraint FOLLOWING_USER_FK references "USER" (USERNAME),
    FOLLOWED_USERNAME varchar(40) not null constraint FOLLOWED_USER_FK references "USER" (USERNAME)
);

create table "GROUP"
(
    GROUP_ID serial constraint GROUP_PK primary key,
    NAME varchar(40) not null
);

create table BELONG
(
    BELONG_ID serial constraint BELONG_PK primary key,
    GROUP_ID integer not null constraint BELONG_GROUP_FK references "GROUP" (GROUP_ID),
    USERNAME varchar(40) not null constraint BELONG_USER_FK references "USER" (USERNAME),
    SUPERUSER bool not null default false
);

create table INGREDIENT
(
    NAME varchar(40) constraint INGREDIENT_PK primary key,
    DENSITY numeric(8, 2),
    STANDARD_UNIT varchar(40) constraint STANDARD_UNIT_FK references UNIT(NAME)
);

create table RECIPE
(
    RECIPE_ID serial constraint RECIPE_PK primary key,
    OWNER_NAME varchar(40) not null constraint OWNER_USER_FK references "USER" (USERNAME),
    NAME varchar(1000) not null,
    PREPARATION_METHOD varchar(4000) not null,
    COST numeric(8, 2),
    DATE_ADDED date default current_timestamp not null,
    PREPARATION_TIME numeric(8),
    PORTIONS numeric(8, 2) default 0 not null CHECK (PORTIONS >= 0.0)
);

create table INGREDIENT_LIST
(
    INGREDIENT_LIST_ID serial constraint INGREDIENT_LIST_PK primary key,
    RECIPE_ID integer constraint IL_RECIPE_ID_FK references RECIPE(RECIPE_ID)  on delete cascade,
    AMOUNT numeric(8, 2),
    INGREDIENT_UNIT varchar(40) not null constraint IL_INGREDIENT_UNIT_FK references UNIT(NAME),
    INGREDIENT_NAME varchar(40) not null constraint IL_INGREDIENT_ID_FK references INGREDIENT(NAME)
);

create table SHOPPING_LIST
(
    SHOPPING_LIST_ID serial constraint SHOPPING_LIST_PK primary key,
    AMOUNT numeric(8, 2) not null,
    INGREDIENT_LIST_ID integer not null constraint SHOPPING_INGREDIENT_FK references INGREDIENT_LIST (INGREDIENT_LIST_ID)  on delete cascade,
    USERNAME varchar(40) constraint SHOPPING_USER_FK references "USER" (USERNAME),
    GROUP_ID integer constraint SHOPPING_GROUP_FK references "GROUP" (GROUP_ID)
);

alter table "GROUP" add SHOPPING_LIST_ID integer constraint GROUP_SHOPPING_FK references SHOPPING_LIST (SHOPPING_LIST_ID);

create table FAVORITE
(
    FAVORITE_ID serial constraint FAVORITE_PK primary key,
    USERNAME varchar(40) not null constraint FAV_USER_ID_FK references "USER"(USERNAME),
    RECIPE_ID integer not null constraint FAV_RECIPE_ID_FK references RECIPE(RECIPE_ID)  on delete cascade
);

create table PUBLICITY
(
    PUBLICITY_ID serial constraint PUBLICITY_PK primary key,
    GROUP_ID integer constraint  PUB_GROUP_ID references "GROUP"(GROUP_ID),
    RECIPE_ID integer not null constraint PUB_RECIPE_ID references RECIPE(RECIPE_ID) on delete cascade
);

create table OPINION
(
    OPINION_ID serial constraint OPINION_PK primary key,
    USERNAME VARCHAR(40) not null constraint OPINION_USER_FK references "USER"(USERNAME),
    RECIPE_ID integer not null constraint OPINION_RECIPE_ID_FK references RECIPE(RECIPE_ID)  on delete cascade,
    SCORE integer not null,
    "COMMENT" varchar(40)
);

create table REPORTED
(
    REPORTED_ID serial constraint REPORTED_PK primary key,
    REPORTING_USER varchar(40) not null constraint REPORTED_USER_ID_FK references "USER"(USERNAME),
    OPINION_ID integer not null constraint REPORTED_OPINION_ID references OPINION(OPINION_ID)  on delete cascade
);