create or replace function calc_rating(r_id number)
    return number
as
    rating_sum number;
    rating_amount number;
begin
    select count(*), sum(SCORE) into rating_amount, rating_sum from OPINION where RECIPE_ID = r_id;
    return rating_sum / rating_amount;
end;

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
    return (first_ratio*quantity)/second_ratio;
end;
/

