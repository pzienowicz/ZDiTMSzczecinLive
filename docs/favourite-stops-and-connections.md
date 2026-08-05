# Ulubione przystanki i polaczenia

## Cel

Dodac w aplikacji funkcje ulubionych przystankow i ulubionych polaczen, tak aby uzytkownik mogl szybko sprawdzic najwazniejsze dla siebie odjazdy bez kazdorazowego wpisywania numeru przystanku albo wybierania linii z mapy.

Funkcja ma byc dobrym kandydatem na rozbudowe premium, ale podstawowy zakres powinien byc uzyteczny takze bez zakupu.

## Zakres MVP

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

Sheet z zakladkami:

- `Przystanki`
- `Polaczenia`

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

## Model danych

Docelowo warto przejsc na Room, jezeli funkcja bedzie rosla. MVP mozna zrobic w SharedPreferences jako JSON, ale bedzie to mniej wygodne przy migracjach.

Rekomendacja: Room.

Proponowane encje:

### FavouriteStop

- `id`: lokalny identyfikator
- `stopId`: id z API, opcjonalnie
- `stopNumber`: numer przystanku
- `stopName`: nazwa przystanku
- `createdAt`
- `sortOrder`

### FavouriteConnection

- `id`: lokalny identyfikator
- `stopNumber`
- `stopName`
- `lineId`
- `lineNumber`
- `direction`
- `createdAt`
- `sortOrder`

## Otwarte decyzje

- Czy darmowy limit ma byc 3 przystanki i 1 polaczenie.
- Czy ulubione maja miec osobny ekran, czy sheet.
- Czy dodajemy widget dla ulubionego polaczenia juz w pierwszym etapie.
- Czy migrujemy od razu na Room.
- Czy zmieniamy obecny przycisk serca, czy zostawiamy go tylko dla domyslnej mapy.

## Etapy implementacji

1. Dodac model i storage ulubionych.
2. Dodac sheet/listy ulubionych.
3. Dodac dodawanie/usuwanie ulubionych przystankow.
4. Dodac pobieranie odjazdow dla ulubionych przystankow.
5. Dodac dodawanie/usuwanie ulubionych polaczen.
6. Podpiac billing/odblokowanie premium.
7. Na koncu dodac limity free/premium.
8. Na koncu dodac dialog z prosba o zakup premium po przekroczeniu limitu.
9. Dodac testy storage i logiki limitow.
