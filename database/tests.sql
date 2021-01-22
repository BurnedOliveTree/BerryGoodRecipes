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
-- adding ingredient to shopping list which isn't in ingredient list 
begin
    add_new_ingredient_to_shopping_list('ketchup', 'tablespoon', 5.0, 'Rokarolka');
end;
commit;
-- add recipe to database
declare
    recipe_id number;
begin
    add_recipe('Rokarolka', 'Placki', 'Wymieszaj składniki. Smaż na wolnym ogniu.', 5, 1, sysdate,10, 0, recipe_id);
    add_ingredient_to_recipe('mąka', 'gram', 100, recipe_id);
    add_ingredient_to_recipe('sól', 'teaspoon', 1, recipe_id);
end;
commit;
-- add ingredient which doesn't exist in past
begin
    add_new_ingredient_to_shopping_list('mąka orkiszowa', 'gram', 100, 'Rokarolka');
end;