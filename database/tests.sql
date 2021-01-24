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
declare
    before_share NUMERIC;
    after_share_group NUMERIC;
    after_share_user NUMERIC;
begin
    select count(*) into before_share from shopping_list where username = 'Rokarolka' and GROUP_ID is null;
    share_shopping_list('Rokarolka', 1);
    commit;
    select count(*) into after_share_user from shopping_list where username = 'Rokarolka' and GROUP_ID=1;
    select count(*) into after_share_group from shopping_list where username = 'Rokarolka' and GROUP_ID=null;
    DBMS_OUTPUT.PUT_LINE('If some ingredient already in group shopping list, then dont add them to group shopping list');
    DBMS_OUTPUT.PUT_LINE('Before: ' || before_share || ', after in user shopping list ' || after_share_user || ', after in group shopping list ' || after_share_group);
end;
/
-- TEST PROCEDURE add_new_ingredient_to_shopping_list
-- adding ingredient to shopping list which isn't in ingredient list
delete INGREDIENT_LIST where INGREDIENT_NAME = 'ketchup' and INGREDIENT_UNIT = 'tablespoon';
delete INGREDIENT where NAME = 'ketchup';
commit;
declare
    ing_list_id NUMERIC;
    before_adding NUMERIC;
    after_adding NUMERIC;
begin
    select count(*) into before_adding from ingredient_list;
    add_ingredient_to_ingredient_list('ketchup', 'tablespoon', ing_list_id);
    select count(*) into after_adding from ingredient_list;
    commit;
    DBMS_OUTPUT.PUT_LINE('Before: ' || before_adding || ', after: ' || after_adding || ', new id in ingredient_list: ' || ing_list_id);
end;
/
-- TEST PROCEDURE dd_new_ingredient_to_shopping_list
-- which doesn't exist in past
delete SHOPPING_LIST sl where sl.INGREDIENT_LIST_ID = (SELECT il.INGREDIENT_LIST_ID FROM INGREDIENT_LIST il WHERE il.INGREDIENT_NAME = 'mąka orkiszowa');
delete INGREDIENT_LIST where INGREDIENT_NAME = 'mąka orkiszowa';
delete INGREDIENT where NAME = 'mąka orkiszowa';
commit;
declare
    before_adding_in_il NUMERIC;
    after_adding_in_il NUMERIC;
    before_adding_in_ing NUMERIC;
    after_adding_in_ing NUMERIC;
     new_id NUMERIC;
begin
    select count(*) into before_adding_in_il from ingredient_list;
    select count(*) into before_adding_in_ing from ingredient;
    add_new_ingredient_to_shopping_list('mąka orkiszowa', 'gram', 100, 'Rokarolka');
    commit;
    select count(*) into after_adding_in_il from ingredient_list;
    select count(*) into after_adding_in_ing from ingredient;
    select sl.INGREDIENT_LIST_ID into new_id from SHOPPING_LIST sl where sl.INGREDIENT_LIST_ID = (select il.INGREDIENT_LIST_ID from INGREDIENT_LIST il where il.INGREDIENT_NAME = 'mąka orkiszowa');
    DBMS_OUTPUT.PUT_LINE('Before in ingredient list: ' || before_adding_in_il || ', after in ingredient list: ' || after_adding_in_il);
    DBMS_OUTPUT.PUT_LINE('Before in ingredient: ' || before_adding_in_ing || ', after in ingredient: ' || after_adding_in_ing);
    DBMS_OUTPUT.PUT_LINE('New ingredient in shopping list: ' || new_id);
end;
/
-- TEST PROCEDURE add_recipe, PROCEDURE add_ingredient_to_recipe
-- add recipe to database
delete RECIPE where NAME = 'Placki' and OWNER_NAME = 'Rokarolka';
declare
    new_recipe_id number;
begin
    add_recipe('Rokarolka', 'Placki', 'Wymieszaj składniki. Smaż na wolnym ogniu.', 5, 1, sysdate,10, 0, new_recipe_id);
    add_ingredient_to_recipe('mąka', 'gram', 100, new_recipe_id);
    add_ingredient_to_recipe('sól', 'teaspoon', 1, new_recipe_id);
    commit;
    DBMS_OUTPUT.PUT_LINE('New recipe id: ' || new_recipe_id);
    for cr in (select * from INGREDIENT_LIST where RECIPE_ID = new_recipe_id) loop
        DBMS_OUTPUT.PUT_LINE('Recipe id: ' ||  cr.RECIPE_ID  || ' -- > Ingredient name: ' || cr.INGREDIENT_NAME || ' Unit: ' || cr.INGREDIENT_UNIT || ' Amount: ' || cr.AMOUNT);
    end loop;
end;
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
    delete_account('testing1');
    delete_account('testing2');
    delete_account('testing3');
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
    insert into "USER" values ('testing4', '1111', null);
    commit;
    select count(*) into user_before_count from "USER" where USERNAME = 'testing4';
    select count(*) into belong_before_count from BELONG where USERNAME = 'testing4';
    delete_account('testing4');
    commit;
    select count(*) into user_after_count from "USER" where USERNAME = 'testing4';
    select count(*) into belong_after_count from BELONG where USERNAME = 'testing4';
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
-- trigger add_favourites_tg