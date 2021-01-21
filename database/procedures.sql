create or replace function calc_rating(r_id number)
    return number
as
    rating_sum number;
    rating_amount number;
begin
    select count(*), sum(SCORE) into rating_amount, rating_sum from OPINION where RECIPE_ID = r_id;
    return rating_sum / rating_amount;
end;
/
create or replace trigger tg_add_to_public
    after insert on "USER"
    for each row
begin
    insert into BELONG values (null, 0, :new.USERNAME);
end;
/
create or replace function convert_unit(first_unit varchar, second_unit varchar, quantity number)
    return number
as
    new_quantity number(9,5);
    first_ratio number(9,5);
    second_ratio number(9,5);
begin
    select liter_per_unit_ratio into first_ratio from unit where name = first_unit;
    select liter_per_unit_ratio into second_ratio from unit where name = second_unit;
    if second_ratio = 0 then
        return (first_ratio*quantity) / (0.000001);
    else
    return (first_ratio*quantity)/second_ratio;
    end if;
end;
/
create or replace procedure add_group(u_id varchar2, g_name varchar2)
as
    new_id number(4);
begin
    insert into "GROUP" values (null, g_name, null)
    returning GROUP_ID into new_id;
    insert into BELONG values (null, new_id, u_id);
end;
/
create or replace procedure delete_account(u_id varchar2)
as
begin
    delete from BELONG where USERNAME = u_id;
    delete from SHOPPING_LIST where USERNAME = u_id and GROUP_ID is null;
    update SHOPPING_LIST set USERNAME = null where USERNAME = u_id;
    delete from FOLLOWED where FOLLOWING_USERNAME = u_id or FOLLOWED_USERNAME = u_id;
    delete from REPORTED where REPORTING_USER = u_id;
    delete from OPINION where USERNAME = u_id;
    delete from FAVORITE where USERNAME = u_id;
    delete from RECIPE where OWNER_NAME = u_id;
    delete from "USER" where USERNAME = u_id;
--  TODO delete_group if no one belongs there
end;
/
create or replace procedure delete_group(g_id number)
as
begin
    delete from BELONG where GROUP_ID = g_id;
    delete from SHOPPING_LIST where GROUP_ID = g_id;
    delete from PUBLICITY where GROUP_ID = g_id;
    delete from "GROUP" where GROUP_ID = g_id;
end;
/
create or replace procedure add_ingredient_to_ingredient_list(p_name IN varchar2, p_unit IN varchar2, r_ing_list_id OUT NUMBER)
as
    v_exist NUMBER;
    v_ing varchar2(40);
begin
    select count(*) into v_exist from INGREDIENT where NAME = p_name;
    if v_exist = 0 then
        insert into INGREDIENT(NAME, STANDARD_UNIT) values(p_name, p_unit) returning NAME into v_ing;
    end if;

    select count(*) into v_exist from INGREDIENT_LIST where RECIPE_ID is null and INGREDIENT_NAME = p_name;
    if v_exist = 0 then
        insert into INGREDIENT_LIST(INGREDIENT_UNIT, INGREDIENT_NAME) values (p_unit,p_name) returning INGREDIENT_LIST_ID into r_ing_list_id;
    else
        select INGREDIENT_LIST_ID into r_ing_list_id from INGREDIENT_LIST where RECIPE_ID is null and INGREDIENT_NAME = p_name;
    end if;
end;
/
create or replace procedure add_new_ingredient_to_shopping_list(p_name varchar2, p_unit varchar2, p_amount number, p_username varchar2)
as
    v_exist NUMBER;
    v_ing_list_id NUMBER;
begin
    begin
        add_ingredient_to_ingredient_list(p_name, p_unit, v_ing_list_id);
    end;
    select count(*) into v_exist from SHOPPING_LIST where USERNAME = p_username and GROUP_ID is null and INGREDIENT_LIST_ID = v_ing_list_id;
    if v_exist = 0 then
        insert into SHOPPING_LIST(AMOUNT, INGREDIENT_LIST_ID, USERNAME) values (p_amount,v_ing_list_id, p_username);
    end if;
end;
/
create or replace procedure share_shopping_list(p_name varchar2, p_group_id number)
as
        v_found_ingredient number;
begin
    for c_info in (select INGREDIENT_LIST_ID from SHOPPING_LIST  where USERNAME like p_name and GROUP_ID is null) LOOP
        select COUNT(*) into v_found_ingredient from SHOPPING_LIST where GROUP_ID = p_group_id and INGREDIENT_LIST_ID = c_info.INGREDIENT_LIST_ID;
        if v_found_ingredient = 0 then
            UPDATE SHOPPING_LIST SET GROUP_ID = p_group_id WHERE INGREDIENT_LIST_ID = c_info.INGREDIENT_LIST_ID and GROUP_ID is null and USERNAME = p_name;
        end if;
    end loop;
end;
/
-- @TODO MARIANKA trigger
/
create or replace procedure add_recipe(p_username IN varchar2, p_name IN varchar2, p_description IN varchar2,  p_cost IN number, p_portion IN number, p_date IN varchar2, p_prepare_time IN number, p_group_id IN number, r_recipe_id out number)
as
begin
    INSERT INTO RECIPE VALUES(null, p_username, p_name, p_description, p_cost, TO_DATE(p_date, 'yyyy-mm-dd hh24:mi:ss'), p_prepare_time, p_portion) returning RECIPE_ID into r_recipe_id;
    INSERT INTO PUBLICITY VALUES(null, p_group_id, r_recipe_id);
end;
/
create or replace procedure add_ingredient_to_recipe(p_name varchar2, p_unit varchar2, p_amount number, p_recipe_id number)
as
    v_ing_list_id NUMBER;
begin
    begin
        add_ingredient_to_ingredient_list(p_name, p_unit, v_ing_list_id);
    end;
    UPDATE INGREDIENT_LIST SET RECIPE_ID = p_recipe_id, AMOUNT = p_amount WHERE INGREDIENT_LIST_ID = v_ing_list_id;
end;