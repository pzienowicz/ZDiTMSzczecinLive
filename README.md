# Komunikacja Miejska Szczecin

Nieoficjalna aplikacja Android z informacjami o komunikacji miejskiej ZDiTM Szczecin.

Aplikacja pokazuje mapę przystanków i pojazdów, rozkłady jazdy, tablice odjazdów oraz aktualne zmiany w kursowaniu. Jest przeznaczona dla osób, które chcą szybko sprawdzić najbliższe odjazdy, znaleźć przystanek albo podejrzeć położenie tramwajów i autobusów na mapie.

## Ważne

Aplikacja korzysta z danych udostępnianych przez ZDiTM Szczecin. Nie jest oficjalną aplikacją ZDiTM.

Lokalizacja pojazdów jest przybliżona. Nie wszystkie pojazdy przekazują informację o swoim położeniu, dlatego część z nich może nie być widoczna na mapie. Dane o lokalizacji odświeżają się mniej więcej co 30 sekund.

Działanie aplikacji zależy od dostępności i szybkości serwisów ZDiTM. Jeżeli system źródłowy działa wolno albo jest niedostępny, może to wpływać także na aplikację.

## Główne funkcje

- mapa przystanków i pojazdów komunikacji miejskiej w Szczecinie
- filtrowanie mapy według wybranej linii
- rozkłady jazdy i wirtualna tablica odjazdów dla przystanków
- aktualne zmiany w rozkładach jazdy
- ulubione przystanki, połączenia i mapy
- widgety z odjazdami na ekranie telefonu
- skanowanie kodów QR z przystanków
- tryb ciemny

## Pobierz

<a href="https://play.google.com/store/apps/details?id=pl.pzienowicz.zditmszczecinlive">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

## Screenshots

![Mapa pojazdów i przystanków](https://play-lh.googleusercontent.com/lyrquf0t-xTS2XHKOpYkOIq8L1BWyBGQcmYRGWXqgMNs8IbZLUaRuuKnTKLwMoGQaC1WzdOYNW-U0NiQMek3-Gg=w526-h296)
![Lista linii](https://play-lh.googleusercontent.com/1Pv1pM0fYfZ9ojSv8xFYnRO4wm9PYY7YPU5S8josyk89QasJNV0GvZ0tJ26VGFkY4OFjXDdubbUvdsv_cYxo2Q=w526-h296)
![Tablica odjazdów](https://play-lh.googleusercontent.com/-TiiPfdR0xMM_B5cdmpV0WQh4Yt4tq4WzTvMUDicvF_fqyku35yhHk1fnyBVgSl8jz1lvYPVr6vh6VrRcfziGw=w526-h296)
![Szczegóły przystanku](https://play-lh.googleusercontent.com/SVf2-g_5ULR_7mjuxDiJXAMyWACDTu0JQcjXtQEMwisZkwqtkQ1xJCF162ysmltVdkm8AtQ97V4jajiGw8FE=w526-h296)
![Zmiany w rozkładach](https://play-lh.googleusercontent.com/Il32KYPsO5Sg0msxQePNz7wYqolHMqbqxeGLJxLHPNaerCj2bj5AW5M1ssNJJ2IYRlc84BQPff0eMNvrWprqoVw=w526-h296)
![Widget odjazdów](https://play-lh.googleusercontent.com/dLKQtIbp-AkDINGzcD-7BRL7GdIuldwczWwYFoRMI1pOiC7tn7EvKJjI-55EAl2_bQe7u_Rz8FxjjkQEal6J=w526-h296)

## Stack technologiczny

- Kotlin
- Android Views i ViewBinding
- WebView dla mapy ZDiTM
- Retrofit i Gson do komunikacji z API
- Material Components
- ZXing do skanowania kodów QR
- Google Play Billing dla funkcji Premium

## Premium

Aplikacja ma wersję darmową z limitami ulubionych elementów. Jednorazowy zakup Premium odblokowuje większe limity ulubionych przystanków, połączeń i map, widgety z odjazdami oraz tryb ciemny.

## Uprawnienia

Aplikacja może prosić o następujące uprawnienia:

- internet i stan sieci - pobieranie danych z serwisów ZDiTM
- lokalizacja - przekazanie pozycji do mapy, jeżeli użytkownik chce użyć funkcji lokalizacyjnych
- aparat - skanowanie kodów QR z przystanków
- alarmy i przypomnienia - automatyczne odświeżanie widgetów

## Źródła danych

Aplikacja korzysta z danych i stron ZDiTM Szczecin:

- https://www.zditm.szczecin.pl/
- https://www.zditm.szczecin.pl/pl/mapy/przystanki-i-pojazdy
- https://www.zditm.szczecin.pl/api/v2/

## Kontakt

- GitHub
- E-mail: zienowicz.pawel(at)gmail.com
