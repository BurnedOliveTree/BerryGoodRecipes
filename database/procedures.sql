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