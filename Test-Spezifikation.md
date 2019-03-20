# ClientDataExchange Testspezifikation
## Methoden der Prüfung
Folgende Methoden werden zur Durchführung der Prüfung eingesetzt:

*  **Statische Analyse (STAT):** Das Grundprinzip der statischen Analyse besteht darin, dass ein Prüfgegenstand, der nach einem vorgegebenen Formalismus aufgebaut ist, gelesen wird. Hierbei werden entweder sofort Fehler bzw. fehlerträchtige Situationen festgestellt bzw. Informationen abgeleitet, die nach Ende des Lesevorgangs Rückschlüsse auf Fehler bzw. fehlerträchtige Situationen zulassen.
*  **Black-Box-Testfallentwurf (BBTE):** Beim Black-Box-Testfallentwurf werden die Testfälle aus den Anforderungen bzw. Spezifikationen abgeleitet. Der Prüfgegenstand wird als schwarzer Kasten angesehen, d. h. der Prüfer ist nicht an der internen Struktur und dem Verhalten des Prüfgegenstandes interessiert. Dabei werden folgende Blackbox-Testfallentwurfsmethoden verwendet:
    *  **Intuitive Testfallermittlung:** Grundlage für diesen methodischen Ansatz ist die intuitive Fähigkeit und Erfahrung von Menschen, Testfälle nach erwarteten Fehlern auszuwählen.
    *  **Funktionsabdeckung:** Bei der Funktionsabdeckung werden Testfälle identifiziert, mit denen nachgewiesen werden kann, dass die jeweilige Funktion vorhanden und auch ausführbar ist. Hierbei wird der Testfall auf das Normalverhalten und das Ausnahmeverhalten des Prüfgegenstandes ausgerichtet.

Die Ergebnisse der Testfälle werden im Prüfprotokoll chronologisch nach Prüffällen gegliedert festgehalten.

## Prüfkriterien

Prüfkriterium für den Black-Box-Test der Software ist die vollständige Übereinstimmung der vom Prüfling im Rahmen des Testablaufs erzeugten Ausgaben mit den Sollvorgaben der Prüffallbeschreibungen.

Die Prüfung gilt als erfolgreich, wenn alle in Kapitel „Prüffälle“ aufgeführten Prüffälle mit den vorgegebenen Ergebnissen durchgeführt wurden und keine formalen Fehler mehr vorliegen. Es dürfen darüber hinaus während der Tests keine den Anforderungen widersprechenden Ergebnisse auftreten.

## Prüfumgebung

Die Prüfumgebung setzt sich aus der ClientDataExchange Client - Implementierung und einer Beispiel - Implementierung des ClientDataExchange Servers zusammen. Außerdem wird als Datenquelle ein ERZ Datenverteiler Testsystem verwendet, in dem Messqerschnitte, Anzeigequerschnitte und Glättemeldeanlagen konfiguriert sind. Die Erzeugung der Daten geschieht mit geeigneten Generatoren oder händisch im ERZ Datenverteiler.

## Prüffälle

### Verkehrsdaten

#### Prüffall 1.1: Übertragen von dynamischen Fahrstreifendaten (`atg.verkehrsDatenKurzZeitFs`)
##### Prüfschritte
  1. Parametrierung eines einzelnen Fahrstreifens und der Attributgruppe `atg.verkehrsDatenKurzZeitFs` zur Übertragung.
  2. Starten des ClientDataExchange Servers
  3. Starten des ClientDataExchange Clients mit entsprechenden Verbindungsparametern

##### Erwartetes Ergebnis
* Im Log des Servers erscheint eine Meldung mit den statischen Daten des Fahrstreifens (ID, Name, Lage).
* Im Log des Servers erscheinen zyklisch Meldungen mit den Verkehrsdaten (dynamischen Daten) des Fahrstreifens.

#### Prüffall 1.2: Übertragen von dynamischen Fahrstreifendaten (`atg.verkehrsDatenKurzZeitIntervall`)

##### Prüfschritte
  1. Parametrierung eines einzelnen Fahrstreifens und der Attributgruppe `atg.verkehrsDatenKurzZeitIntervall` zur Übertragung.
  2. Starten des ClientDataExchange Servers
  3. Starten des ClientDataExchange Clients mit entsprechenden Verbindungsparametern

##### Erwartetes Ergebnis
* Im Log des Servers erscheint eine Meldung mit den statischen Daten des Fahrstreifens (ID, Name, Lage).
* Im Log des Servers erscheinen zyklisch Meldungen mit den Verkehrsdaten (dynamischen Daten) des Fahrstreifens.

#### Prüffall 2.1: Übertragen von dynamischen Messquerschnittdaten (`atg.verkehrsDatenKurzZeitMq`)

##### Prüfschritte
  1. Parametrierung eines einzelnen Messquerschnitts und der Attributgruppe `atg.verkehrsDatenKurzZeitMq` zur Übertragung.
  2. Starten des ClientDataExchange Servers
  3. Starten des ClientDataExchange Clients mit entsprechenden Verbindungsparametern

##### Erwartetes Ergebnis
* Im Log des Servers erscheint eine Meldung mit den statischen Daten des Messquerschnitts (ID, Name, Fahrstreifen-ID's).
* Im Log des Servers erscheinen zyklisch Meldungen mit den Verkehrsdaten (dynamischen Daten) des Messquerschnitts.

### Anzeigedaten

#### Prüffall 3.1: Übertragen von dynamischen Anzeigedaten

##### Prüfschritte
  1. Parametrierung einer einzelnen Anzeige und der Attributgruppe `atg.anzeigeEigenschaftIst` zur Übertragung.
  2. Starten des ClientDataExchange Servers
  3. Starten des ClientDataExchange Clients mit entsprechenden Verbindungsparametern

##### Erwartetes Ergebnis
* Im Log des Servers erscheint eine Meldung mit den statischen Daten der Anzeige (ID, Name, Lage).
* Im Log des Servers erscheint eine Meldungen mit den Anzeigedaten (dynamischen Daten) der Anzeige. Wenn das Attribut "grafik" nicht leer oder `null` ist, dann wird eine Bilddatei (.bmp) auf dem Server abgelegt.

#### Prüffall 4.1: Übertragen von dynamischen Anzeigequerschnittdaten

##### Prüfschritte
  1. Parametrierung eines einzelnen Anzeigequerschnitts und der Attributgruppe `atg.anzeigeQuerschnittEigenschaftIst` zur Übertragung.
  2. Starten des ClientDataExchange Servers
  3. Starten des ClientDataExchange Clients mit entsprechenden Verbindungsparametern

##### Erwartetes Ergebnis
* Im Log des Servers erscheint eine Meldung mit den statischen Daten des Anzeigequerschnitts (ID, Name, Anzeige-ID's).
* Im Log des Servers erscheint eine Meldungen mit dem aktuellen Zustand des Anzeigequerschnitts (dynamischen Daten).

#### Prüffall 4.1: Übertragen von Helligkeitsdaten von Anzeigequerschnitten

##### Prüfschritte
  1. Parametrierung einer DE zur Helligkeitssteuerung eines Anzeigequerschnitts und der Attributgruppe `atg.tlsUfdErgebnisMeldungHelligkeitHK` zur Übertragung.
  2. Starten des ClientDataExchange Servers
  3. Starten des ClientDataExchange Clients mit entsprechenden Verbindungsparametern

##### Erwartetes Ergebnis
* Im Log des Servers erscheint eine Meldungen mit der aktuellen Helligkeits des Anzeigequerschnitts (dynamischen Daten).

### Umfelddaten

#### Prüffall 5.1: Übertragen von dynamischen Umfelddaten (`atg.gmaUmfelddaten`)

##### Prüfschritte
  1. Parametrierung einer einzelnen Glättemeldeanlage und der Attributgruppe `atg.gmaUmfelddaten` zur Übertragung.
  2. Starten des ClientDataExchange Servers
  3. Starten des ClientDataExchange Clients mit entsprechenden Verbindungsparametern

##### Erwartetes Ergebnis
* Im Log des Servers erscheint eine Meldung mit den statischen Daten der Glättemeldeanlage (ID, Name, DWD Kennung, Position ).
* Im Log des Servers erscheint eine Meldungen mit den aktuellen Umfelddaten der Glättemeldeanlage (dynamischen Daten).
