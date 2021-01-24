-- TEST PROCEDURE share_shopping_list
-- check adding ingredient to group shopping list
delete from SHOPPING_LIST where USERNAME='Rokarolka' or USERNAME='BurnedOliveTree';
commit;
insert into SHOPPING_LIST values (null, 5, 10, 'BurnedOliveTree', 1);
insert into SHOPPING_LIST values (null, 34, 30, 'BurnedOliveTree', 1);
insert into SHOPPING_LIST values (null, 5, 10, 'Rokarolka', null);
insert into SHOPPING_LIST values (null, 5, 29, 'Rokarolka', null);
insert into SHOPPING_LIST values (null, 34, 30, 'BurnedOliveTree', null);
commit;
begin
    share_shopping_list('Rokarolka', 1);
end;
commit;
begin
    share_shopping_list('BurnedOliveTree', 1);
end;
commit;
/
-- TEST PROCEDURE add_new_ingredient_to_shopping_list
-- adding ingredient to shopping list which isn't in ingredient list 
begin
    add_new_ingredient_to_shopping_list('ketchup', 'tablespoon', 5.0, 'Rokarolka');
end;
commit;
-- add ingredient which doesn't exist in past
begin
    add_new_ingredient_to_shopping_list('mąka orkiszowa', 'gram', 100, 'Rokarolka');
end;
/
-- TEST PROCEDURE add_recipe, PROCEDURE add_ingredient_to_recipe
-- add recipe to database
declare
    recipe_id number;
begin
    add_recipe('Rokarolka', 'Placki', 'Wymieszaj składniki. Smaż na wolnym ogniu.', 5, 1, sysdate,10, 0, recipe_id);
    add_ingredient_to_recipe('mąka', 'gram', 100, recipe_id);
    add_ingredient_to_recipe('sól', 'teaspoon', 1, recipe_id);
end;
commit;
/
-- TEST FUNCTION calc_rating
-- insert few opinions and return the average rating
declare
    avg_result number(6, 2);
    r_id number(4);
begin
    select RECIPE_ID into r_id from RECIPE where NAME = 'Placki' and OWNER_NAME = 'Rokarolka';
    insert into OPINION values (null, 'BurnedOliveTree', r_id, 9, 'Pyszne');
    insert into OPINION values (null, 'Marianka', r_id, 6, 'Może być');
    insert into OPINION values (null, 'BerryRootUser', r_id, 1, 'Nie może być');
    commit;
    select calc_rating(r_id) into avg_result from DUAL;
    DBMS_OUTPUT.PUT_LINE(avg_result);
end;
/
-- TEST PROCEDURE add_group
-- add group to database
declare
    g_id number(4);
    u_id varchar2(40);
begin
    add_group('BurnedOliveTree', 'NewTestGroup');
    commit;
    select GROUP_ID into g_id from "GROUP" where NAME = 'NewTestGroup';
    select USERNAME into u_id from BELONG where GROUP_ID = g_id;
    DBMS_OUTPUT.PUT_LINE(u_id);
end;
/
-- TEST TRIGGER tg_add_to_public
-- add few users and check if there are more people in public
declare
    before_count number(4);
    after_count number(4);
begin
    select count(*) into before_count from BELONG where GROUP_ID = 0; -- PUBLIC is always 0
    insert into "USER" values ('testing1', '1111', null);
    insert into "USER" values ('testing2', '1111', null);
    insert into "USER" values ('testing3', '1111', null);
    commit;
    select count(*) into after_count from BELONG where GROUP_ID = 0;
    DBMS_OUTPUT.PUT_LINE('Before: ' || before_count || ', after: ' || after_count);
end;
/
-- TEST PROCEDURE delete_account
declare
    user_before_count number(4);
    belong_before_count number(4);
    user_after_count number(4);
    belong_after_count number(4);
begin
    select count(*) into user_before_count from "USER" where USERNAME = 'testing1';
    select count(*) into belong_before_count from BELONG where USERNAME = 'testing1';
    delete_account('testing1');
    commit;
    select count(*) into user_after_count from "USER" where USERNAME = 'testing1';
    select count(*) into belong_after_count from BELONG where USERNAME = 'testing1';
    DBMS_OUTPUT.PUT_LINE('Before: ' || user_before_count || ', after: ' || user_after_count);
    DBMS_OUTPUT.PUT_LINE('Before: ' || belong_before_count || ', after: ' || belong_after_count);
end;
/
-- TEST PROCEDURE delete_group
declare
    g_id number(4);
    group_before_count number(4);
    belong_before_count number(4);
    group_after_count number(4);
    belong_after_count number(4);
begin
    select GROUP_ID into g_id from "GROUP" where NAME = 'NewTestGroup';
    select count(*) into group_before_count from "GROUP" where GROUP_ID = g_id;
    select count(*) into belong_before_count from BELONG where GROUP_ID = g_id;
    delete_group(g_id);
    commit;
    select count(*) into group_after_count from "GROUP" where GROUP_ID = g_id;
    select count(*) into belong_after_count from BELONG where GROUP_ID = g_id;
    DBMS_OUTPUT.PUT_LINE('Before: ' || group_before_count || ', after: ' || group_after_count);
    DBMS_OUTPUT.PUT_LINE('Before: ' || belong_before_count || ', after: ' || belong_after_count);
end;
/

-- function convert_unit
-- procedure add_ingredient_to_ingredient_list
-- trigger add_favourites_tg