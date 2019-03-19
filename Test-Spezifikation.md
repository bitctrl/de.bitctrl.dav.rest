# DAV REST API Testspezifikation
## Methoden der Prüfung
Folgende Methoden werden zur Durchführung der Prüfung eingesetzt:

*  **Statische Analyse (STAT):** Das Grundprinzip der statischen Analyse besteht darin, dass ein Prüfgegenstand, der nach einem vorgegebenen Formalismus aufgebaut ist, gelesen wird. Hierbei werden entweder sofort Fehler bzw. fehlerträchtige Situationen festgestellt bzw. Informationen abgeleitet, die nach Ende des Lesevorgangs Rückschlüsse auf Fehler bzw. fehlerträchtige Situationen zulassen.
*  **Black-Box-Testfallentwurf (BBTE):** Beim Black-Box-Testfallentwurf werden die Testfälle aus den Anforderungen bzw. Spezifikationen abgeleitet. Der Prüfgegenstand wird als schwarzer Kasten angesehen, d. h. der Prüfer ist nicht an der internen Struktur und dem Verhalten des Prüfgegenstandes interessiert. Dabei werden folgende Blackbox-Testfallentwurfsmethoden verwendet:
    *  **Intuitive Testfallermittlung:** Grundlage für diesen methodischen Ansatz ist die intuitive Fähigkeit und Erfahrung von Menschen, Testfälle nach erwarteten Fehlern auszuwählen.
    *  **Funktionsabdeckung:** Bei der Funktionsabdeckung werden Testfälle identifiziert, mit denen nachgewiesen werden kann, dass die jeweilige Funktion vorhanden und auch ausführbar ist. Hierbei wird der Testfall auf das Normalverhalten und das Ausnahmeverhalten des Prüfgegenstandes ausgerichtet.

Die Ergebnisse der Testfälle werden im Prüfprotokoll chronologisch nach Prüffällen gegliedert festgehalten.

## Prüfkriterien

Prüfkriterium für den Black-Box-Test der Software ist die vollständige Übereinstimmung der vom Prüfling im Rahmen des Testablaufs erzeugten Ausgaben mit den Sollvorgaben der Prüffallbeschreibungen.

Die Prüfung gilt als erfolgreich, wenn alle in [Kapitel „Prüffälle“](#prueffaelle) aufgeführten Prüffälle mit den vorgegebenen Ergebnissen durchgeführt wurden und keine formalen Fehler mehr vorliegen. Es dürfen darüber hinaus während der Tests keine den Anforderungen widersprechenden Ergebnisse auftreten.

## Prüffälle <a name="prueffaelle"></a>

### Verkehrsdaten
#### Prüffall 1.1: Übertragen von statischen Fahrstreifendaten
##### 
#### Prüffall 1.2: Übertragen von dynamischen Fahrstreifendaten (```atg.verkehrsDatenKurzZeitFs```)
#### Prüffall 1.3: Übertragen von dynamischen Fahrstreifendaten (```atg.verkehrsDatenKurzZeitIntervall```)
#### Prüffall 2.1: Übertragen von statischen Messquerschnittdaten
#### Prüffall 2.1: Übertragen von dynamischen Messquerschnittdaten (```atg.verkehrsDatenKurzZeitMq```)

### Anzeigedaten
#### Prüffall 3.1: Übertragen von statischen Anzeigedaten
#### Prüffall 3.2: Übertragen von dynamischen Anzeigedaten
#### Prüffall 4.1: Übertragen von statischen Anzeigequerschnittdaten
#### Prüffall 4.2: Übertragen von dynamischen Anzeigequerschnittdaten

### Umfelddaten
#### Prüffall 5.1: Übertragen von statischen Umfelddaten
#### Prüffall 5.2: Übertragen von dynamischen Umfelddaten (```atg.gmaUmfelddaten```)
