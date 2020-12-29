truncate table PUBLICITY;
truncate table FAVORITE;
truncate table INGREDIENT_LIST;
truncate table RECIPE;
truncate table SHOPPING_LIST;
truncate table INGREDIENT;
truncate table BELONG;
truncate table "GROUP";
truncate table FOLLOWED;
truncate table "USER";
truncate table UNIT;
truncate table UNIT_SYSTEM;

insert into UNIT_SYSTEM values('metryczny');
insert into UNIT_SYSTEM values('imperialny');
insert into UNIT_SYSTEM values('kuchenny');
insert into UNIT_SYSTEM values('N/A');
commit;

insert into UNIT values('gram', 'metryczny', 1,1,1,1,1);
insert into UNIT values('kilogram', 'metryczny', 1,1,1,1,1);
insert into UNIT values('mililitr', 'metryczny', 1,1,1,1,1);
insert into UNIT values('litr', 'metryczny', 1,1,1,1,1);
insert into UNIT values('sztuka', 'N/A', 1,1,1,1,1);
insert into UNIT values('łyżeczka', 'kuchenny', 1,1,1,1,1);
insert into UNIT values('łyżka', 'kuchenny', 1,1,1,1,1);
insert into UNIT values('szklanka', 'kuchenny', 1,1,1,1,1);
commit;

insert into "USER" values ('BerryRootUser', 'BerryHardPassword', null);
insert into "USER" values ('David', '470646', null);
insert into "USER" values ('Alex', '451546', null);
insert into "USER" values ('Maria', '438485', null);
insert into "USER" values ('Anna', '387660', null);
insert into "USER" values ('Marco', '352629', null);
insert into "USER" values ('Antonio', '325085', null);
insert into "USER" values ('Daniel', '310096', null);
insert into "USER" values ('Andrea', '305442', null);
insert into "USER" values ('Laura', '296627', null);
insert into "USER" values ('Ali', '290285', null);
insert into "USER" values ('BurnedOliveTree', 'Karo1104', null);
insert into "USER" values ('Rokarolka', '12345', null);
insert into "USER" values ('Marianka', '12345', null);
commit;

insert into FOLLOWED values (null, 'BurnedOliveTree', 'Rokarolka');
commit;

insert into "GROUP" values (0, 'public', null);
insert into "GROUP" values (null, 'admin', null);
commit;

insert into BELONG values (null, 1, 'BurnedOliveTree');
insert into BELONG values (null, 1, 'Rokarolka');
insert into BELONG values (null, 1, 'Marianka');
commit;

insert into INGREDIENT values ('makaron', null, 'gram');
insert into INGREDIENT values ('makaron lasagne', null, 'gram');
insert into INGREDIENT values ('makaron spaghetti', null, 'gram');

insert into INGREDIENT values ('ser żółty', null, 'gram');
insert into INGREDIENT values ('twaróg', 0.95525, 'gram');
insert into INGREDIENT values ('masło', 0.911, 'gram');
insert into INGREDIENT values ('mleko', 1.03, 'litr');

insert into INGREDIENT values ('woda', 0.997, 'litr');
insert into INGREDIENT values ('olej', 0.9188, 'litr');
insert into INGREDIENT values ('jajko', null, 'sztuka');
insert into INGREDIENT values ('żółtko', null, 'sztuka');

insert into INGREDIENT values ('mąka', 0.593, 'kilogram');
insert into INGREDIENT values ('mąka wrocławska', 0.593, 'kilogram');
insert into INGREDIENT values ('mąka poznańska', 0.593, 'kilogram');
insert into INGREDIENT values ('mąka tortowa', 0.593, 'kilogram');

insert into INGREDIENT values ('cukier', 1.59, 'gram');
insert into INGREDIENT values ('cukier puder', 0.801, 'gram');
insert into INGREDIENT values ('proszek do pieczenia', 2.2, 'gram');
insert into INGREDIENT values ('cukier waniliowy', 1.056, 'gram');

insert into INGREDIENT values ('mięso mielone', null, 'kilogram');

insert into INGREDIENT values ('jabłko', null, 'sztuka');
insert into INGREDIENT values ('gruszka', null, 'sztuka');
insert into INGREDIENT values ('brzoskwinia', null, 'sztuka');
insert into INGREDIENT values ('pomarańcza', null, 'sztuka');
insert into INGREDIENT values ('śliwka', null, 'sztuka');
insert into INGREDIENT values ('pomidor', null, 'sztuka');
insert into INGREDIENT values ('passata pomidorowa', null, 'mililitr');
insert into INGREDIENT values ('orzechy włoskie', null, 'gram');
insert into INGREDIENT values ('marmolada', null, 'gram');
commit;

-- shopping list inserts;

insert into RECIPE values (null, 'BurnedOliveTree', 'Lasagne', 'Mięso podsmażyć na oleju. Odstawić, doprawić solą i pieprzem. Passatę pomidorową podgotować, można dodać trochę oregano pod koniec podgrzewania. Płaty ciasta zalać gorąca woda. Jak zmiękną to układać je na blaszce, którą trzeba wcześniej nasmarować olejem. Układamy ciasto, na to podsmażone mięso i zalewamy ciepłą passatą. Krok powtarzamy. Na ostatni płat posypać utarty żółty ser. Wszystko zalać passatą, a jak będzie mało pomidorów, to wodą z moczenia płatów. Musi być woda, bo inaczej płaty się wysuszą i będą twarde. Piec 30 minut na 180 stopniach na środkowej półce.', null, sysdate, null, 4);
insert into RECIPE values(null, 'BurnedOliveTree', 'Ciasteczka twarogowe z dżemem', 'Rozmielić twaróg, dodać rozdrobnione masło, dodać mąki, wymieszać. Ciasto cienko rozwałkować i pociąć na kwadraty, na każdy nałożyć dżem (najlepiej nie rzadki). Rozbić i wymieszać jajko w osobnej misce. Posmarować ciasteczka jajkiem przed pieczeniem. Blachę(y) posmarować masłem i posypać mąką. Piec w 180-185 stopniach przez 10-15 minut.',  null, sysdate, null, 48);
insert into RECIPE values(null, 'BurnedOliveTree', 'Ciasto olejne' , 'Cukier rozpuścić w gorącej wodzie, wystudzić. Dodać mąkę, proszek, żółtka i olej. Z białek ubić pianę i dodać. Można dodać kakao do części. Wlewając do blachy masę z kakaem trzeba wlać najpierw, resztę na górę. Piec 30-40 minut w 180-190 stopniach.',  null, sysdate, null, 1);
insert into RECIPE values(null, 'BurnedOliveTree', 'Rohliczki' , 'Orzechy ołupać i zmielić. Pokrojone w kostkę masło zmieszać z resztą składników. Odstawić rozrobione ciasto na 30 minut do lodówki. Blachę posmarować masłem i posypać mąką. Rozwałkować ciasto na cylindry o średnicy około 1 cm i wysokości około 8 cm. Piec 7-11 minut w 190-200 stopniach. Odczekać chwilę, po czym do miski wsypać cukier waniliowy oraz cukier puder i obtoczyć w nich wszystkie rohliczki. Schować do pudełka i odłożyć do suchego miejsca na co najmniej 2 tygodnie.',  null, sysdate, null, 36);
insert into RECIPE values(null, 'BurnedOliveTree', 'Masłowe ciasteczka sklejane z marmoladą', 'Pokrojone w kostkę masło zmieszać z resztą składników. Odstawić rozrobione ciasto na 30 minut do lodówki. Blachę posmarować masłem i posypać mąką. Ciasto rozwałkować i powycinać kształty (kółko na spód, oraz jakiś wzór na górę). Piec 7-10 minut w 190 stopniach. Po upieczeniu należy na spód położyć trochę dżemu i położyć na to wierzch ciasteczka. Schować do pudełka i odłożyć do suchego miejsca na co najmniej 2 tygodnie.',  null, sysdate, null, 18);
commit;

insert into INGREDIENT_LIST values (null, 1, 500, 'gram', 'mięso mielone');
insert into INGREDIENT_LIST values (null, 1, 700, 'gram', 'passata pomidorowa');;
insert into INGREDIENT_LIST values (null, 1,75, 'gram', 'ser żółty');
insert into INGREDIENT_LIST values (null, 1, 15, 'sztuka', 'makaron lasagne');

insert into INGREDIENT_LIST values (null, 2,  250, 'gram','twaróg');
insert into INGREDIENT_LIST values (null, 2,  250, 'gram', 'mąka wrocławska');
insert into INGREDIENT_LIST values (null, 2,  250, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 2,  1, 'sztuka', 'marmolada');

insert into INGREDIENT_LIST values (null, 3,  256, 'gram', 'cukier puder');
insert into INGREDIENT_LIST values (null, 3,  256, 'gram', 'mąka');
insert into INGREDIENT_LIST values (null, 3,  10, 'łyżka', 'woda');
insert into INGREDIENT_LIST values (null, 3,  10, 'łyżka', 'olej');
insert into INGREDIENT_LIST values (null, 3,  4, 'sztuka', 'jajko');
insert into INGREDIENT_LIST values (null, 3,  2, 'łyżka', 'proszek do pieczenia');

insert into INGREDIENT_LIST values (null, 4,  200, 'gram', 'mąka tortowa');
insert into INGREDIENT_LIST values (null, 4,  140, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 4,  70, 'gram', 'cukier puder');
insert into INGREDIENT_LIST values (null, 4,  70,'gram', 'orzechy włoskie');

insert into INGREDIENT_LIST values (null, 5,  210, 'gram', 'mąka poznańska');
insert into INGREDIENT_LIST values (null, 5,  140, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 5,  70, 'gram', 'cukier puder');
insert into INGREDIENT_LIST values (null, 5,  2, 'sztuka', 'żółtko');
insert into INGREDIENT_LIST values (null, 5,  1, 'sztuka', 'cukier waniliowy');
commit;

insert into FAVORITE values (null, 'BurnedOliveTree', 1);
insert into FAVORITE values (null, 'BurnedOliveTree', 3);
commit;

insert into PUBLICITY values(null, 0, 1);
insert into PUBLICITY values(null, 0, 2);
insert into PUBLICITY values(null, 0, 3);
insert into PUBLICITY values(null, 1, 4);
insert into PUBLICITY values(null, 1, 5);
commit;