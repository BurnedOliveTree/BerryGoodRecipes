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