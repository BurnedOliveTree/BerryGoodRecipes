-- truncate table PUBLICITY;
-- truncate table FAVORITE;
-- truncate table INGREDIENT_LIST;
-- truncate table RECIPE;
-- truncate table SHOPPING_LIST;
-- truncate table INGREDIENT;
-- truncate table BELONG;
-- truncate table "GROUP";
-- truncate table FOLLOWED;
-- truncate table "USER";
-- truncate table UNIT;
-- truncate table UNIT_SYSTEM;

insert into UNIT_SYSTEM values('metric');
insert into UNIT_SYSTEM values('kitchen');
insert into UNIT_SYSTEM values('N/A');
insert into UNIT_SYSTEM values('US');
commit;

insert into UNIT values('gram', 'metric', 0.001);
insert into UNIT values('kilogram', 'metric', 1);
insert into UNIT values('mililiter', 'metric', 0.001);
insert into UNIT values('liter', 'metric', 1);
insert into UNIT values('teaspoon', 'kitchen',0.005);
insert into UNIT values('tablespoon', 'kitchen',0.015);
insert into UNIT values('glass', 'kitchen', 0.25);
insert into UNIT values('drop', 'kitchen', 0.00005);
insert into UNIT values('gallon', 'US', 3.79);
insert into UNIT values('quart', 'US', 0.95);
insert into UNIT values('pint', 'US', 0.47);
insert into UNIT values('handful', 'kitchen', 0.04);
insert into UNIT values('piece', 'N/A', 0);
insert into UNIT values('pinch', 'kitchen', 0.00031);
commit;

insert into "GROUP" values (0, 'public', null);
insert into "GROUP" values (null, 'admin', null);
insert into "GROUP" values (2, 'Kółeczko', null);
insert into "GROUP" values (3, 'Kusicille', null);
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
insert into "USER" values ('Madzik', '12345', null);
insert into "USER" values ('KD', '12345', null);
insert into "USER" values ('Oskar', 'kółeczko', null);
insert into "USER" values ('Paweł', 'kółeczko', null);
commit;

insert into FOLLOWED values (null, 'BurnedOliveTree', 'Rokarolka');
insert into FOLLOWED values (null, 'BurnedOliveTree', 'Marianka');
commit;

insert into BELONG values (null, 1, 'BurnedOliveTree');
insert into BELONG values (null, 1, 'Rokarolka');
insert into BELONG values (null, 1, 'Marianka');

insert into BELONG values (null, 2, 'Marianka');
insert into BELONG values (null, 2, 'Rokarolka');
insert into BELONG values (null, 2, 'Oskar');
insert into BELONG values (null, 2, 'Paweł');
insert into BELONG values (null, 2, 'BurnedOliveTree');

insert into BELONG values (null, 3, 'Rokarolka');
insert into BELONG values (null, 3, 'Madzik');
insert into BELONG values (null, 3, 'KD');
commit;

insert into INGREDIENT values ('makaron', null, 'gram');
insert into INGREDIENT values ('makaron lasagne', null, 'gram');
insert into INGREDIENT values ('makaron spaghetti', null, 'gram');

insert into INGREDIENT values ('ser żółty', null, 'gram');
insert into INGREDIENT values ('twaróg', 0.95525, 'gram');
insert into INGREDIENT values ('masło', 0.911, 'gram');
insert into INGREDIENT values ('mleko', 1.03, 'liter');
insert into INGREDIENT values ('serek mascarpone', null, 'gram');
insert into INGREDIENT values ( 'gęsty jogurt naturalny', null, 'gram');
insert into INGREDIENT values ('serek kremowy', null, 'gram');

insert into INGREDIENT values ('woda', 0.997, 'liter');
insert into INGREDIENT values ('olej', 0.9188, 'liter');
insert into INGREDIENT values ('miód', null, 'tablespoon');
insert into INGREDIENT values ('musztarda', null, 'teaspoon');
insert into INGREDIENT values ('jajko', null, 'piece');
insert into INGREDIENT values ('żółtko', null, 'piece');
insert into INGREDIENT values ('białko', null, 'piece');
insert into INGREDIENT values ('drożdże', null, 'gram');
insert into INGREDIENT values ('spirytus', null, 'liter');

insert into INGREDIENT values ('kawa', null, 'teaspoon');
insert into INGREDIENT values ('kakao', null, 'teaspoon');

insert into INGREDIENT values ('mąka', 0.593, 'kilogram');
insert into INGREDIENT values ('mąka wrocławska', 0.593, 'kilogram');
insert into INGREDIENT values ('mąka poznańska', 0.593, 'kilogram');
insert into INGREDIENT values ('mąka tortowa', 0.593, 'kilogram');
insert into INGREDIENT values ('mąka pszenna', 0.593, 'kilogram');
insert into INGREDIENT values ('suchary Mamuty', null, 'piece');
insert into INGREDIENT values ('krem czekoladowy', null, 'piece');
insert into INGREDIENT values ('biała czekolada', null, 'piece');
insert into INGREDIENT values ('margaryna', null, 'gram');
insert into INGREDIENT values ('śmietanka 18%', null, 'tablespoon');
insert into INGREDIENT values ('cukier', 1.59, 'gram');
insert into INGREDIENT values ('cukier puder', 0.801, 'gram');
insert into INGREDIENT values ('proszek do pieczenia', 2.2, 'gram');
insert into INGREDIENT values ('cukier waniliowy', 1.056, 'gram');
insert into INGREDIENT values ('brązowy cukier', null, 'gram');
insert into INGREDIENT values ('rodzynki sułtańskie', null, 'gram');
insert into INGREDIENT values ('aromat migdałowy', null, 'teaspoon');
insert into INGREDIENT values ('pieprz', null, 'pinch');
insert into INGREDIENT values ('migdały', null, 'piece');
insert into INGREDIENT values ('curry', null, 'teaspoon');
insert into INGREDIENT values ('przyprawa złocisty kurczak', null, 'teaspoon');
insert into INGREDIENT values ('przyprawa papryka słodka', null, 'teaspoon');
insert into INGREDIENT values ('pietruszka', null, 'piece');
insert into INGREDIENT values ('ciasto francuskie', null, 'piece');
insert into INGREDIENT values ('cynamon', null, 'teaspoon');
insert into INGREDIENT values ('kandyzowana skórka pomarańczowa', null, 'glass');
insert into INGREDIENT values ('gorzka czekolada', null, 'piece');

insert into INGREDIENT values ('mięso mielone', null, 'kilogram');
insert into INGREDIENT values ('kurczak', null, 'piece');

insert into INGREDIENT values ('sól', null, 'pinch');

insert into INGREDIENT values ('podłużny biszkopt', null, 'piece');

insert into INGREDIENT values ('jabłko', null, 'piece');
insert into INGREDIENT values ('gruszka', null, 'piece');
insert into INGREDIENT values ('brzoskwinia', null, 'piece');
insert into INGREDIENT values ('pomarańcza', null, 'piece');
insert into INGREDIENT values ('malina', null, 'piece');
insert into INGREDIENT values ('śliwka', null, 'piece');
insert into INGREDIENT values ('pomidor', null, 'piece');
insert into INGREDIENT values ('passata pomidorowa', null, 'mililiter');
insert into INGREDIENT values ('orzechy włoskie', null, 'gram');
insert into INGREDIENT values ('mak', null, 'gram');
insert into INGREDIENT values ('marmolada', null, 'gram');

commit;

-- shopping list inserts;

insert into RECIPE values (null, 'BurnedOliveTree', 'Lasagne', 'Mięso podsmażyć na oleju. Odstawić, doprawić solą i pieprzem.' || chr(13) || 'Passatę pomidorową podgotować, można dodać trochę oregano pod koniec podgrzewania.' || chr(13) || 'Płaty ciasta zalać gorąca woda. Jak zmiękną to układać je na blaszce, którą trzeba wcześniej nasmarować olejem.' || chr(13) || 'Układamy ciasto, na to podsmażone mięso i zalewamy ciepłą passatą. Krok powtarzamy.' || chr(13) || 'Na ostatni płat posypać utarty żółty ser. Wszystko zalać passatą, a jak będzie mało pomidorów, to wodą z moczenia płatów. Musi być woda, bo inaczej płaty się wysuszą i będą twarde.' || chr(13) || 'Piec 30 minut na 180 stopniach na środkowej półce.', null, sysdate, 60, 4);
insert into RECIPE values (null, 'BurnedOliveTree', 'Ciasteczka twarogowe z dżemem', 'Rozmielić twaróg, dodać rozdrobnione masło, dodać mąki, wymieszać.' || chr(13) || 'Ciasto cienko rozwałkować i pociąć na kwadraty, na każdy nałożyć dżem (najlepiej nie rzadki).' || chr(13) || 'Rozbić i wymieszać jajko w osobnej misce.' || chr(13) || 'Posmarować ciasteczka jajkiem przed pieczeniem.' || chr(13) || 'Blachę(y) posmarować masłem i posypać mąką.' || chr(13) || 'Piec w 180-185 stopniach przez 10-15 minut.', null, sysdate, 45, 48);
insert into RECIPE values (null, 'BurnedOliveTree', 'Ciasto olejne' , 'Cukier rozpuścić w gorącej wodzie, wystudzić.' || chr(13) || 'Dodać mąkę, proszek, żółtka i olej.' || chr(13) || 'Z białek ubić pianę i dodać.' || chr(13) || 'Można dodać kakao do części. Wlewając do blachy masę z kakaem trzeba wlać najpierw, resztę na górę.' || chr(13) || 'Piec 30-40 minut w 180-190 stopniach.', null, sysdate, 70, 1);
insert into RECIPE values (null, 'BurnedOliveTree', 'Rohliczki' , 'Orzechy ołupać i zmielić.' || chr(13) || 'Pokrojone w kostkę masło zmieszać z resztą składników.' || chr(13) || 'Odstawić rozrobione ciasto na 30 minut do lodówki.' || chr(13) || 'Blachę posmarować masłem i posypać mąką.' || chr(13) || 'Rozwałkować ciasto na cylindry o średnicy około 1 cm i wysokości około 8 cm.' || chr(13) || 'Piec 7-11 minut w 190-200 stopniach.' || chr(13) || 'Odczekać chwilę, po czym do miski wsypać cukier waniliowy oraz cukier puder i obtoczyć w nich wszystkie rohliczki.' || chr(13) || 'Schować do pudełka i odłożyć do suchego miejsca na co najmniej 2 tygodnie.', null, sysdate, 150, 36);
insert into RECIPE values (null, 'BurnedOliveTree', 'Masłowe ciasteczka sklejane z marmoladą', 'Pokrojone w kostkę masło zmieszać z resztą składników.' || chr(13) || 'Odstawić rozrobione ciasto na 30 minut do lodówki.' || chr(13) || 'Blachę posmarować masłem i posypać mąką.' || chr(13) || 'Ciasto rozwałkować i powycinać kształty (kółko na spód, oraz jakiś wzór na górę).' || chr(13) || 'Piec 7-10 minut w 190 stopniach.' || chr(13) || 'Po upieczeniu należy na spód położyć trochę dżemu i położyć na to wierzch ciasteczka.' || chr(13) || 'Schować do pudełka i odłożyć do suchego miejsca na co najmniej 2 tygodnie.', null, sysdate, 180, 18);
insert into RECIPE values (null, 'BurnedOliveTree', 'Naleśniki', 'Jajko opłukać i rozbić. Dodać szczyptę soli. Dodać łyżkę cukru. Dokładnie wymieszać. Sukcesywnie dodawać trochę maki i trochę mleka / wody.' || chr(13) || 'Odstawić na 15-20 minut.' || chr(13) ||'Usmażyć na rozgrzanej patelni.', null, sysdate, 60, 8);
insert into RECIPE values (null, 'Madzik', 'Ciasto czekoladowe', 'Cukier z mlekiem roztopić' || chr(13) || 'Żółtka utrzeć z cukrem, dodać mąkę i sok z pomarańczy' || chr(13) || 'Dodać czekolade i ubite białka' || chr(13) || 'Piec 20 min w 200°C', 20, sysdate, 30, 1);
insert into RECIPE values (null, 'Madzik', 'Muffinki', 'Mąkę, proszek do pieczenia, sól wsypać do jednej miski. W drugiej misce rozbić jajko, ubić widelcem, dodać mleko i ostudzone masło. Do suchych składników wedle uznania dodać bakalie, rodzynki, wiórki kokosowe, orzechy. Wymieszać łyżką. Wypełnić foremki do 2/3 wysokości. Piec w temperaturze 180°C ok. 25 min.', null, sysdate, 30, 5);
insert into RECIPE values (null, 'Madzik', 'Tiramisu', 'Sposób przygotowania naparu z kawy' || chr(13) || 'Składać na niego będą się 4 łyżeczki kawy (można zastąpić 6/7 łyżeczkami likiery kawowego) w połączeniu z wrzątkiem. Należy przygotować dwie szklanki.' || chr(13) || chr(13) || 'Sposób przygotowania ciasta' || chr(13) || 'Żółtka ubić z cukrem i solą na gęsty, jasny krem' || chr(13) || 'W innym naczyniu rozetrzeć serek mascarpone, po łyżce dodając mase jajeczną i ubijać' || chr(13) || 'Biszkopty namoczyć w naparze kawowym. Połowę ułożyć na dnie naczynia, biszkopty przykryć połową kremu, ułożyć drugą warstwę biszkoptów i nałożyć resztę kremu + kakao' || chr(13) || 'Do masy na końcu dodać ubite białka z cukrem i delikatnie wymieszać ręcznie' || chr(13) || 'Wstawić do lodówki na ok 2h.' , null, sysdate, 180, 1);
insert into RECIPE values (null, 'Madzik', 'Szybkie pączuszki', 'Ciasto na pączki podzielić na 3 części' || chr(13) || 'Do jednej dodać cynamon' || chr(13) || 'Do drugiej dodać np. kokos' || chr(13) || 'Trzecią zachować normalną' || chr(13) || 'Wlać olej do garnka i rozgrzać' || chr(13) || 'Wkładać pączuszki', 5, sysdate, 15, 15);
insert into RECIPE values (null, 'Madzik', 'Kurczak w miodzie', 'Pokroic kurczaka, posolic, dodac miodu i musztardy, odstawic na chwilę, podsmażyć na patelni', 20, sysdate, 15,  2);
insert into RECIPE values (null, 'Madzik', 'Sernikobrownie z malinami', 'Blaszkę 20x30 cm (może być mniejsza) wysmarować masłem i wyłożyć papierem. Czekoladę rozpuścić w kąpieli wodnej i ostudzić.' || chr(13) || 'Masło i 250 gram cukur pudru zmiksować na gładką masę, dodać 3 jajka-ubijając po jednym i miksując przed dodaniem kolejnego. Wlać roztopioną czekoladę i zmiksować. Dodać mąkę, wymieszać. 1/2 mikstury wyłożyć na spód blachy. Pozostała część będzie na górę ciasta' || chr(13) || 'W drugiej misce utrzeć ser (ja dodałam dwa serki półtłuste po 250 gram i przecisnełam je przez sitko) Dodać cukier - 150g, 2 jajka i cukier waniliowy. Masa powinna mieć gładką konsystencję' || chr(13) || 'Wylać masę serową na masę czekoladową. Na wierzch wylać resztę masy czekoladowej o włożyć maliny. Masa czekoladowa jest dość gęsta, dlatego ciężko się ją wlewa i wyrównuje' || chr(13) || 'Piec w temperaturze 170°C przez około 45-60 minut. Studzić w ciepłym, ale otwartym piekarniku.', null, sysdate, 120, 1);
insert into RECIPE values (null, 'Madzik', 'Makowiec', 'Mak zaparzyć we wrzącej wodzie (parzyć około 2h) odstawić do ostygnięcia, odsączyć z nadmiaru wody, trzykrotnie zmielić' || chr(13) || 'Dodać pozostałe składniki, wymieszać. Na koniec delikatnie wymieszać z ubitymi na sztywno białkami' || chr(13) || 'Piec w temp. 180°C ok.30 minut.', null, sysdate, 200, 1);
insert into RECIPE values (null, 'KD', 'Ciasto z białą polewą', 'Rozgniatasz puchary lub krakersy I mieszasz z Nutellą. Wkładasz na wybraną przez siebie blaszkę (Dobra jest tortownica lub do Brownie)' || chr(13) || 'Wkładasz do zamrażarki, nie musi być na długo. Chodzi o to żeby stwardnialo na tyle, żeby kolejna warstwa nie zmieszała się poprzednią.' || chr(13) || 'Kiedy dolna część odpoczywa sobie w zamrażalniku bierzemy się za polewę. Rozpuszczamy delikatnie w rondelku lub na patelni czekoladę. Zdejmujemy z gazu, dodajemy stopniowo mascarpone i mieszamy do uzyskania jednolitej konsystencji. Całość wylewamy na pierwszą część. Chowamy znów do zamrażarki na 1h. Potem może leżeć w lodówce, żeby ciasto nie zamarzło całkowicie.', null, sysdate, null, 1);
insert into RECIPE values (null, 'Oskar', 'Kurczak w cieście francuskim z migdałami', 'Farsz to pół na pół migdały z pietruszką' || chr(13) || 'Doprawiasz pół na pół curry i złocisty kurczak, sól pieprz, papryka słodka według uznania'|| chr(13) ||'W piecu musi być 10 minut w 220 st i 15 minut w 200'|| chr(13) ||'No a reszta to rozinanie kurczaka, zaplatanie w ciasto itd'|| chr(13) ||' Przed wstawieniem do pieca smarujesz całym jajkiem'|| chr(13) ||'i posypujesz migdałami', null, sysdate, null, 2);
insert into RECIPE values (null, 'Paweł', 'Najłatwiejsze kruche ciastka', 'Z podanych składników wyrobić szybko ciasto. Włożyć na 1h do lodówki. Potem cienko rozwałkować i wykrawać foremkami ciastka. Piec na papierze do pieczenia na ostrym ogniu (temp ok 220°C-200°C. Można przed pieczeniem posmarować rozbitym jajkiem itp.', null, sysdate, null, 1);
-- insert into RECIPE values (null, 'Paweł', 'Kołacz na 50 blach', '')
insert into RECIPE values (null, 'Paweł', 'Rogaliki migdałowe', 'Zmielić migdały. Całość wyrobić i formować wałek o średnicy 1cm. Pokroić na paseczki o dł. 5 cm i formować rogaliki. Piec na papierze. Gorące posypać cukrem pudrem i cukrem waniliowym', null, sysdate, null, 35);
commit;

insert into INGREDIENT_LIST values (null, 1, 500, 'gram', 'mięso mielone');
insert into INGREDIENT_LIST values (null, 1, 700, 'gram', 'passata pomidorowa');;
insert into INGREDIENT_L
IST values (null, 1, 75, 'gram', 'ser żółty');
insert into INGREDIENT_LIST values (null, 1, 15, 'piece', 'makaron lasagne');

insert into INGREDIENT_LIST values (null, 2,  250, 'gram', 'twaróg');
insert into INGREDIENT_LIST values (null, 2,  250, 'gram', 'mąka wrocławska');
insert into INGREDIENT_LIST values (null, 2,  250, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 2,  1, 'piece', 'marmolada');

insert into INGREDIENT_LIST values (null, 3,  256, 'gram', 'cukier puder');
insert into INGREDIENT_LIST values (null, 3,  256, 'gram', 'mąka');
insert into INGREDIENT_LIST values (null, 3,  10, 'tablespoon', 'woda');
insert into INGREDIENT_LIST values (null, 3,  10, 'tablespoon', 'olej');
insert into INGREDIENT_LIST values (null, 3,  4, 'piece', 'jajko');
insert into INGREDIENT_LIST values (null, 3,  2, 'tablespoon', 'proszek do pieczenia');

insert into INGREDIENT_LIST values (null, 4,  200, 'gram', 'mąka tortowa');
insert into INGREDIENT_LIST values (null, 4,  140, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 4,  70, 'gram', 'cukier puder');
insert into INGREDIENT_LIST values (null, 4,  70,'gram', 'orzechy włoskie');

insert into INGREDIENT_LIST values (null, 5,  210, 'gram', 'mąka poznańska');
insert into INGREDIENT_LIST values (null, 5,  140, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 5,  70, 'gram', 'cukier puder');
insert into INGREDIENT_LIST values (null, 5,  2, 'piece', 'żółtko');
insert into INGREDIENT_LIST values (null, 5,  1, 'piece', 'cukier waniliowy');

insert into INGREDIENT_LIST values (null, 6,  200, 'gram', 'mąka wrocławska');
insert into INGREDIENT_LIST values (null, 6,  250, 'mililiter', 'mleko');
insert into INGREDIENT_LIST values (null, 6,  250, 'mililiter', 'woda');
insert into INGREDIENT_LIST values (null, 6,  1, 'tablespoon', 'cukier');
insert into INGREDIENT_LIST values (null, 6,  1, 'piece', 'jajko');

insert into INGREDIENT_LIST values (null, 7,  6, 'piece', 'jajko');
insert into INGREDIENT_LIST values (null, 7,  1, 'glass', 'cukier');
insert into INGREDIENT_LIST values (null, 7,  1, 'glass', 'mąka');
insert into INGREDIENT_LIST values (null, 7,  2, 'piece', 'gorzka czekolada');
insert into INGREDIENT_LIST values (null, 7,  0.5, 'piece', 'masło');
insert into INGREDIENT_LIST values (null, 7,  1, 'piece', 'pomarańcza');

insert into INGREDIENT_LIST values (null, 8, 2, 'glass', 'mąka');
insert into INGREDIENT_LIST values (null, 8, 1, 'tablespoon', 'proszek do pieczenia');
insert into INGREDIENT_LIST values (null, 8, 0.66, 'glass', 'cukier');
insert into INGREDIENT_LIST values (null, 8, 1, 'pinch', 'sól');
insert into INGREDIENT_LIST values (null, 8, 1, 'piece', 'jajko');
insert into INGREDIENT_LIST values (null, 8, 1, 'glass', 'mleko');
insert into INGREDIENT_LIST values (null, 8, 0.5, 'glass', 'masło');

insert into INGREDIENT_LIST values (null, 9, 6, 'piece', 'żółtko');
insert into INGREDIENT_LIST values (null, 9, 3, 'piece', 'białko');
insert into INGREDIENT_LIST values (null, 9, 6, 'teaspoon', 'cukier');
insert into INGREDIENT_LIST values (null, 9, 1, 'pinch', 'sól');
insert into INGREDIENT_LIST values (null, 9, 40, 'piece', 'podłużny biszkopt');
insert into INGREDIENT_LIST values (null, 9, 500, 'gram', 'serek mascarpone');
insert into INGREDIENT_LIST values (null, 9, 4, 'teaspoon', 'kawa');
insert into INGREDIENT_LIST values (null, 9, 2, 'tablespoon', 'kakao');

insert into INGREDIENT_LIST values (null, 10, 200, 'gram', 'gęsty jogurt naturalny');
insert into INGREDIENT_LIST values (null, 10, 1, 'piece', 'jajko');
insert into INGREDIENT_LIST values (null, 10, 1, 'pinch', 'sól');
insert into INGREDIENT_LIST values (null, 10, 1, 'glass', 'mąka pszenna');
insert into INGREDIENT_LIST values (null, 10, 1, 'teaspoon', 'proszek do pieczenia');

insert into INGREDIENT_LIST values (null, 11, 2, 'piece', 'kurczak');
insert into INGREDIENT_LIST values (null, 11, 1, 'tablespoon', 'miód');
insert into INGREDIENT_LIST values (null, 11, 1, 'teaspoon', 'musztarda');

insert into INGREDIENT_LIST values (null, 12, 200, 'gram', 'gorzka czekolada');
insert into INGREDIENT_LIST values (null, 12, 200, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 12, 400, 'gram', 'cukier puder');
insert into INGREDIENT_LIST values (null, 12, 5, 'piece', 'jajko');
insert into INGREDIENT_LIST values (null, 12, 110, 'gram', 'mąka pszenna');
insert into INGREDIENT_LIST values (null, 12, 400, 'gram', 'serek kremowy');
insert into INGREDIENT_LIST values (null, 12, 1, 'piece', 'cukier waniliowy');
insert into INGREDIENT_LIST values (null, 12, 15, 'piece', 'malina');

insert into INGREDIENT_LIST values (null, 13, 5, 'glass', 'mąka pszenna');
insert into INGREDIENT_LIST values (null, 13, 180, 'mililiter', 'mleko');
insert into INGREDIENT_LIST values (null, 13, 200, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 13, 6, 'piece', 'jajko');
insert into INGREDIENT_LIST values (null, 13, 45, 'gram', 'drożdże');
insert into INGREDIENT_LIST values (null, 13, 6, 'tablespoon', 'cukier');
insert into INGREDIENT_LIST values (null, 13, 0.5, 'teaspoon', 'sól');
insert into INGREDIENT_LIST values (null, 13, 1.5, 'tablespoon', 'spirytus');
insert into INGREDIENT_LIST values (null, 13, 1, 'piece', 'cukier waniliowy');
insert into INGREDIENT_LIST values (null, 13, 500, 'gram', 'mak');
insert into INGREDIENT_LIST values (null, 13, 170, 'gram', 'brązowy cukier');
insert into INGREDIENT_LIST values (null, 13, 100, 'gram', 'rodzynki sułtańskie');
insert into INGREDIENT_LIST values (null, 13, 50, 'gram', 'orzechy włoskie');
insert into INGREDIENT_LIST values (null, 13, 3, 'tablespoon', 'miód');
insert into INGREDIENT_LIST values (null, 13, 1, 'teaspoon', 'aromat migdałowy');
insert into INGREDIENT_LIST values (null, 13, 1, 'teaspoon', 'cynamon');
insert into INGREDIENT_LIST values (null, 13, 0.5, 'glass', 'kandyzowana skórka pomarańczowa');

insert into INGREDIENT_LIST values (null, 14, 1, 'piece', 'suchary Mamuty');
insert into INGREDIENT_LIST values (null, 14, 1, 'piece', 'krem czekoladowy');
insert into INGREDIENT_LIST values (null, 14, 500, 'gram', 'serek mascarpone');
insert into INGREDIENT_LIST values (null, 14, 2, 'piece', 'biała czekolada');

insert into INGREDIENT_LIST values (null, 15, 200, 'gram', 'migdały');
insert into INGREDIENT_LIST values (null, 15, 2, 'piece', 'kurczak');
insert into INGREDIENT_LIST values (null, 15, 1, 'teaspoon', 'curry');
insert into INGREDIENT_LIST values (null, 15, 1, 'teaspoon', 'przyprawa złocisty kurczak');
insert into INGREDIENT_LIST values (null, 15, 1, 'teaspoon', 'przyprawa papryka słodka');
insert into INGREDIENT_LIST values (null, 15, 1, 'piece', 'ciasto francuskie');
insert into INGREDIENT_LIST values (null, 15, 1, 'pinch', 'sól');
insert into INGREDIENT_LIST values (null, 15, 1, 'pinch', 'pieprz');

insert into INGREDIENT_LIST values (null, 16, 200, 'gram', 'mąka');
insert into INGREDIENT_LIST values (null, 16, 100, 'gram', 'margaryna');
insert into INGREDIENT_LIST values (null, 16, 50, 'gram', 'cukier');
insert into INGREDIENT_LIST values (null, 16, 1, 'piece', 'cukier waniliowy');
insert into INGREDIENT_LIST values (null, 16, 1, 'tablespoon', 'śmietanka 18%');

insert into INGREDIENT_LIST values (null, 17, 100, 'gram', 'migdały');
insert into INGREDIENT_LIST values (null, 17, 280, 'gram', 'mąka');
insert into INGREDIENT_LIST values (null, 17, 3, 'piece', 'cukier waniliowy');
insert into INGREDIENT_LIST values (null, 17, 100, 'gram', 'cukier puder');
insert into INGREDIENT_LIST values (null, 17, 200, 'gram', 'masło');
insert into INGREDIENT_LIST values (null, 17, 0.5, 'piece', 'proszek do pieczenia');
insert into INGREDIENT_LIST values (null, 17, 2, 'piece', 'jajko');
commit;

insert into FAVORITE values (null, 'BurnedOliveTree', 1);
insert into FAVORITE values (null, 'BurnedOliveTree', 3);
commit;

insert into PUBLICITY values(null, 0, 1);
insert into PUBLICITY values(null, 0, 2);
insert into PUBLICITY values(null, 0, 3);
insert into PUBLICITY values(null, 1, 4);
insert into PUBLICITY values(null, 1, 5);
insert into PUBLICITY values(null, 0, 6);
insert into PUBLICITY values(null, 0, 7);
insert into PUBLICITY values(null, 0, 8);
insert into PUBLICITY values(null, 0, 9);
insert into PUBLICITY values(null, 0, 10);
insert into PUBLICITY values(null, 0, 11);
insert into PUBLICITY values(null, 0, 12);
insert into PUBLICITY values(null, 0, 13);
insert into PUBLICITY values(null, 0, 14);
insert into PUBLICITY values(null, 0, 15);
insert into PUBLICITY values(null, 0, 16);
insert into PUBLICITY values(null, 0, 17);
commit;