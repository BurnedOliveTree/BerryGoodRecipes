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