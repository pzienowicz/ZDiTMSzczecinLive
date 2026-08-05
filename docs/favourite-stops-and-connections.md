# Ulubione przystanki, polaczenia i mapy

## Cel

Dodac w aplikacji funkcje ulubionych przystankow, ulubionych polaczen i ulubionych map, tak aby uzytkownik mogl szybko sprawdzic najwazniejsze dla siebie odjazdy albo otworzyc wybrana mape bez kazdorazowego wpisywania numeru przystanku albo wybierania linii z mapy.

Funkcja ma byc dobrym kandydatem na rozbudowe premium, ale podstawowy zakres powinien byc uzyteczny takze bez zakupu.

## Zakres MVP

Status: ulubione przystanki, ulubione polaczenia i ulubione mapy sa zaimplementowane jako MVP. Dane sa zapisywane lokalnie w `SharedPreferences` jako JSON przez Gson.

### Ulubione przystanki

- Uzytkownik moze dodac przystanek do ulubionych.
- Przystanek mozna wybrac tak jak obecnie w tablicy/widgetach:
  - recznie po numerze przystanku,
  - przez skan QR.
- Lista ulubionych pokazuje:
  - nazwe przystanku,
  - numer przystanku,
  - najblizsze odjazdy.
- Klikniecie ulubionego przystanku otwiera tablice odjazdow.
- Uzytkownik moze usunac przystanek z ulubionych.

### Ulubione polaczenia

- Polaczenie oznacza zestaw:
  - przystanek,
  - linia,
  - kierunek.
- Lista ulubionych polaczen pokazuje najblizszy pasujacy odjazd.
- Klikniecie polaczenia otwiera szczegoly odjazdow dla tego przystanku przefiltrowane do danej linii/kierunku.
- Uzytkownik moze usunac polaczenie z ulubionych.

Obecnie klikniecie polaczenia otwiera pelna tablice odjazdow dla przystanku. Dedykowany widok przefiltrowany do linii/kierunku zostaje jako mozliwe ulepszenie.

### Ulubione mapy

- Uzytkownik moze dodac aktualna mape do ulubionych przez FAB z sercem.
- Ponowne klikniecie serca usuwa aktualna mape z ulubionych.
- Dodawanie map odbywa sie tylko przez FAB na ekranie mapy.
- Lista ulubionych map pokazuje krotkie kafelki po dwa w wierszu.
- Klikniecie kafelka otwiera konkretna mape.
- Uzytkownik moze usunac mape z ulubionych.
- Po starcie aplikacja otwiera pierwsza ulubiona mape, a jesli lista jest pusta, otwiera domyslna mape ZDiTM.
- Stara preferencja `prefs.favouriteMap` jest migrowana przy starcie aplikacji do rekordu ulubionych map, a nastepnie czyszczona.

## Premium

Proponowany model:

- Free:
  - maksymalnie 3 ulubione przystanki,
  - maksymalnie 1 ulubione polaczenie.
- Premium:
  - bez limitu ulubionych przystankow,
  - bez limitu ulubionych polaczen,
  - mozliwosc tworzenia widgetow z ulubionego przystanku lub polaczenia,
  - przyszlosciowo: alerty odjazdu/opoznien dla ulubionych.

Limity wersji free i dialog z prosba o zakup premium dodajemy na koncu prac nad funkcja. Najpierw doprowadzamy ulubione przystanki i polaczenia do dzialajacego stanu, a dopiero potem ograniczamy liczbe rekordow dla uzytkownikow bez premium.

Po przekroczeniu limitu free aplikacja powinna pokazac dialog premium z jasnym komunikatem:

- co zostalo zablokowane,
- jakie sa limity wersji darmowej,
- co odblokowuje premium,
- ze zakup jest jednorazowy.

Decyzje:

- Tworzymy nowy produkt pod pelne premium.
- Id nowego produktu premium w Google Play: `premium_unlock`.
- Nadal wspieramy istniejacy produkt `widgets_unlock`.
- Zakup `widgets_unlock` ma dawac pelne premium jako legacy entitlement dla obecnych kupujacych.
- Premium bedzie platnoscia jednorazowa.

Do ustalenia:

- Jak nazwiemy wspolny entitlement w kodzie, np. `isPremiumUnlocked`.

## Dane z API ZDiTM

Wykorzystywane endpointy:

- `GET /api/v2/stops` - lista przystankow, id, numer, nazwa, wspolrzedne.
- `GET /api/v2/lines` - lista linii i ich typy.
- `GET /api/v2/departure-boards/{stopNumber}?limit={limit}` - najblizsze odjazdy z przystanku.

Potencjalnie pozniej:

- GTFS statyczny - offline i pelne rozklady.
- GTFS-RT trips - odwolane kursy i punktualnosc.
- GTFS-RT alerts - alerty o zakloceniach.
- `GET /api/v2/vehicles` - opoznienia, pojazdy, kolejny przystanek, model, niska podloga.

## Ograniczenia API

- API ZDiTM ma limit 100 zadan na minute na adres IP.
- Trzeba honorowac cache headers tam, gdzie ma to sens.
- Dla list ulubionych nie odpytywac kazdego elementu osobno zbyt agresywnie.
- Preferowane podejscie:
  - odswiezanie po wejsciu w sheet/widok,
  - reczne odswiezenie,
  - cache krotkoterminowy dla tablic odjazdow,
  - limit wynikow w `departure-boards`.

## Proponowany UX

### Wejscie

Opcje do rozwazenia:

- nowa pozycja w menu "Ulubione",
- dodanie do obecnego menu "Wiecej",
- rozbudowanie przycisku serca, ktory obecnie zapisuje domyslna mape.

Preferowane MVP: nowa pozycja w "Wiecej", zeby nie przebudowywac glownej nawigacji.

### Widok ulubionych

Sheet z sekcjami:

- `Przystanki`
- `Polaczenia`
- `Mapy`

Dla kazdego wpisu:

- nazwa/numer,
- najblizszy odjazd lub kilka odjazdow,
- akcja edycji/usuniecia,
- ewentualnie oznaczenie premium przy funkcjach zablokowanych.

### Dodawanie przystanku

- FAB lub przycisk "Dodaj przystanek".
- Otwiera istniejacy `BusStopDialog`.
- Po wyborze zapisuje przystanek do ulubionych.

### Dodawanie polaczenia

- Najpierw wybor przystanku.
- Potem pobranie tablicy odjazdow i wybor jednej z linii/kierunkow.
- Zapisujemy stabilne dane:
  - stop number,
  - stop id, jesli dostepny,
  - line id,
  - line number,
  - headsign/direction.

### Dodawanie mapy

- FAB z sercem dziala jako toggle aktualnej mapy:
  - jesli aktualna mapa nie jest w ulubionych, dodaje ja,
  - jesli aktualna mapa jest juz w ulubionych, usuwa ja.
- Sheet ulubionych ma sekcje `Mapy`, bez przycisku dodawania.
- Kafelki map sa wyswietlane po dwa w wierszu.

## Model danych

Decyzja na MVP: nie dodajemy Room. Uzywamy `SharedPreferences` + JSON przez Gson, bo danych jest malo, struktura jest prosta i aplikacja juz korzysta z `SharedPreferences`.

Room zostaje jako opcja pozniejsza, jezeli funkcja urosnie o offline GTFS, historie, alerty albo zlozone filtrowanie.

Proponowane encje:

### FavouriteStop

- `stopId`: id z API
- `stopNumber`: numer przystanku
- `stopName`: nazwa przystanku

### FavouriteConnection

- `stopId`: id z API
- `stopNumber`
- `stopName`
- `lineNumber`
- `direction`

### FavouriteLine

- `title`: nazwa widoczna w UI, np. `Linia 75`, `Wszystkie linie` albo `Mapa`
- `url`: konkretna mapa do otwarcia

## Otwarte decyzje

- Czy darmowy limit ma byc 3 przystanki i 1 polaczenie.
- Czy dodajemy widget dla ulubionego polaczenia.
- Czy dodajemy dedykowany widok odjazdow przefiltrowany do ulubionego polaczenia.
- Czy limity free obejmuja takze ulubione mapy.

## Etapy implementacji

1. [x] Dodac model i storage ulubionych.
2. [x] Dodac sheet/listy ulubionych.
3. [x] Dodac dodawanie/usuwanie ulubionych przystankow.
4. [x] Dodac pobieranie odjazdow dla ulubionych przystankow.
5. [x] Dodac dodawanie/usuwanie ulubionych polaczen.
6. [x] Dodac pobieranie najblizszego pasujacego odjazdu dla ulubionych polaczen.
7. [x] Dodac testy storage i formatowania odjazdow.
8. [x] Dodac sekcje ulubionych map.
9. [ ] Podpiac billing/odblokowanie premium.
10. [ ] Na koncu dodac limity free/premium.
11. [ ] Na koncu dodac dialog z prosba o zakup premium po przekroczeniu limitu.
12. [ ] Dodac testy logiki limitow.
