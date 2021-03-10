create or replace function calc_rating(r_id integer)
    returns integer
    language plpgsql
as
$$
declare
    rating_sum integer;
    rating_amount integer;
begin
    select count(*), sum(SCORE) into rating_amount, rating_sum from OPINION where RECIPE_ID = r_id;
    return rating_sum / rating_amount;
end;
$$;
create or replace function add_to_public()
    returns trigger
    language plpgsql
as
$$
begin
    insert into BELONG values (default, 0, new.username);
    return new;
end;
$$;
create trigger tg_add_to_public
    after insert on "USER"
    for each row
    execute function add_to_public();
create or replace function convert_unit(first_unit varchar, second_unit varchar, quantity numeric)
    returns numeric
    language plpgsql
as
$$
declare
    first_ratio numeric(9,5);
    second_ratio numeric(9,5);
begin
    select liter_per_unit_ratio into first_ratio from unit where name = first_unit;
    select liter_per_unit_ratio into second_ratio from unit where name = second_unit;
    if second_ratio = 0 then
        return (first_ratio*quantity) / (0.000001);
    else
        return (first_ratio*quantity)/second_ratio;
    end if;
end;
$$;
create or replace procedure add_group(u_id varchar, g_name varchar)
    language plpgsql
as
$$
declare
    new_id integer;
begin
    insert into "GROUP" values (default, g_name, null)
    returning GROUP_ID into new_id;
    insert into BELONG values (default, new_id, u_id);
end;
$$;
create or replace procedure delete_account(u_id varchar)
    language plpgsql
as
$$
declare
    member_count integer;
    cr cursor is (select GROUP_ID from BELONG where USERNAME = u_id);
    grp_id integer;
begin
    open cr;
    delete from BELONG where USERNAME = u_id;
    delete from SHOPPING_LIST where USERNAME = u_id and GROUP_ID is null;
    update SHOPPING_LIST set USERNAME = null where USERNAME = u_id;
    delete from FOLLOWED where FOLLOWING_USERNAME = u_id or FOLLOWED_USERNAME = u_id;
    delete from REPORTED where REPORTING_USER = u_id;
    delete from OPINION where USERNAME = u_id;
    delete from FAVORITE where USERNAME = u_id;
    delete from RECIPE where OWNER_NAME = u_id;
    delete from "USER" where USERNAME = u_id;
    loop
        fetch cr into grp_id;
        exit when not found;
        select count(*) into member_count from BELONG where GROUP_ID = grp_id;
        if member_count = 0 then
            execute delete_group(grp_id);
        end if;
    end loop;
    close cr;
end;
$$;
create or replace procedure delete_group(g_id integer)
    language plpgsql
as
$$
begin
    delete from BELONG where GROUP_ID = g_id;
    delete from SHOPPING_LIST where GROUP_ID = g_id;
    delete from PUBLICITY where GROUP_ID = g_id;
    delete from "GROUP" where GROUP_ID = g_id;
end;
$$;
create or replace function add_ingredient_to_ingredient_list(p_name in varchar, p_unit in varchar)
    returns integer
    language plpgsql
as
$$
declare
    v_exist integer;
    v_ing varchar(40);
    r_ing_list_id integer;
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
    return r_ing_list_id;
end;
$$;
create or replace procedure add_new_ingredient_to_shopping_list(p_name varchar, p_unit varchar, p_amount numeric, p_username varchar)
    language plpgsql
as
$$
declare
    v_exist integer;
    v_ing_list_id integer;
begin
    select add_ingredient_to_ingredient_list(p_name, p_unit) into v_ing_list_id;
    select count(*) into v_exist from SHOPPING_LIST where USERNAME = p_username and GROUP_ID is null and INGREDIENT_LIST_ID = v_ing_list_id;
    if v_exist = 0 then
        insert into SHOPPING_LIST(AMOUNT, INGREDIENT_LIST_ID, USERNAME) values (p_amount, v_ing_list_id, p_username);
    end if;
end;
$$;
create or replace procedure share_shopping_list(p_name varchar, p_group_id integer)
    language plpgsql
as
$$
declare
    v_found_ingredient integer;
    c_info integer;
begin
    for c_info in (select INGREDIENT_LIST_ID from SHOPPING_LIST where USERNAME like p_name and GROUP_ID is null) LOOP
        select COUNT(*) into v_found_ingredient from SHOPPING_LIST where GROUP_ID = p_group_id and INGREDIENT_LIST_ID = c_info;
        if v_found_ingredient = 0 then
            update SHOPPING_LIST set GROUP_ID = p_group_id where INGREDIENT_LIST_ID = c_info and GROUP_ID is null and USERNAME = p_name;
        else
            delete from SHOPPING_LIST where INGREDIENT_LIST_ID = c_info and GROUP_ID is null and USERNAME = p_name;
        end if;
    end loop;
end;
$$;
create or replace function add_favourites()
    returns trigger
    language plpgsql
as
$$
declare
    rec_id integer;
begin
    for rec_id in (select RECIPE_ID from RECIPE where OWNER_NAME = new.followed_username) loop
        insert into FAVORITE values(default, new.following_username, rec_id);
    end loop;
    return new;
end;
$$;
create trigger add_favourites_tg
    after insert on FOLLOWED
    for each row
    execute procedure add_favourites();
create or replace function add_recipe(p_username varchar, p_name varchar, p_description varchar,  p_cost double precision, p_portion double precision, p_date varchar, p_prepare_time integer, p_group_id integer)
    returns numeric
    language plpgsql
as
$$
declare
    r_recipe_id numeric;
begin
    insert into RECIPE values(default, p_username, p_name, p_description, p_cost, TO_DATE(p_date, 'yyyy-mm-dd hh24:mi:ss'), p_prepare_time, p_portion) returning RECIPE_ID into r_recipe_id;
    insert into PUBLICITY values(default, p_group_id, r_recipe_id);
    return r_recipe_id;
end;
$$;
create or replace function add_ingredient_to_recipe(p_name varchar, p_unit varchar, p_amount double precision, p_recipe_id integer)
    returns numeric
    language plpgsql
as
$$
declare
    r_ing_list_id numeric;
begin
    select add_ingredient_to_ingredient_list(p_name, p_unit) into r_ing_list_id;
    update INGREDIENT_LIST set RECIPE_ID = p_recipe_id, AMOUNT = p_amount where INGREDIENT_LIST_ID = r_ing_list_id;
    return r_ing_list_id;
end;
$$;