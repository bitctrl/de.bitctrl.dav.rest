# ClientDataExchange Client

Client-Implementierung der REST API für Datenverteiler (Dav3)

## Kurzbeschreibung der Funktionalität

Die Software realisiert den Client einer REST API
 
 
## Betriebsinformationen

### Installation der Software

#### Voraussetzungen

Es muss eine Java Runtime Umgebung (JRE) ab Version 8.0 oder höher installiert und über den
Suchpfad auffindbar sein [jre].
Eine Java Runtime Umgebung (JRE) ist für den Betrieb ausreichend, jedoch bietet das Java Development
Kit (JDK) zusätzlich nützliche Tools für die Diagnose [jdk].
Die korrekte Installation von Java lässt sich auf der Kommandozeile mit folgendem Befehl überprüfen:

```
java -version
```

Erfolgt die Ausgabe der installierten Javaversion ist der Pfad korrekt eingerichtet. Erfolgt eine Meldung,
dass der Befehl nicht gefunden wurde, muss die Pfadvariable angepasst werden.
Unter Linux-Systemen (unter anderem Linux, Mac OS X) kann dies mit folgendem Kommando erfolgen:

```
export PATH=$PATH:/pfad_zu_java/bin
```

Unter Windows muss der Pfad im Dialog "Systemsteuerung/System/Erweitert/Umgebungsvariablen"
angepasst werden. Der Wert der Variablen **PFAD** muss um den Text `;/pfad_zu_java/bin` ergänzt
werden.

#### Erstinstalltion

Der Inhalt der ZIP-Archive der SWE muss in das Verzeichnis `$PROJEKT_HOME/lib` kopiert werden.
Unter Linux-Systemen werden die ZIP-Archive mit

```
unzip de.bitctrl.dav.rest.client-0.0.1-SNAPSHOT.zip
```

entpackt und mit

```
cp -r de.bitctrl.dav.rest.client $PROJEKT_HOME/lib
```

in den Ordner mit den Bibliotheken des Projekts kopiert.
Unter Windows kann ab Windows XP der Windows-Explorer sowohl für das Entpacken, als auch für
das Kopieren verwendet werden. Für ältere Windows-Systeme muss ein zusätzliches Tool zum Entpacken
des ZIP-Archivs verwendet werden (z.B. das kostenlose  [7-Zip](http://7-zip.org)).

#### Deinstallation der Software

Für die Deinstallation sollte die SWE gestoppt werden.

Zur Deinstallation der Software werden die Dateien und Verzeichnisse, die in Kapitel "Erstinstallation
der Software" installiert bzw. kopiert wurden, gelöscht.

Gleiches gilt für Dateien und Verzeichnisse, die bei der Installation angelegt bzw. kopiert wurden.

#### Aktualisierung der Software

Für die Aktualisierung muss die SWE gestoppt werden.

Die Aktualisierung entspricht der Deinstallation und anschließender Erstinstallation der SWE.

### Einrichtung der Software

#### Konfiguration
Folgende Konfigurationsbereiche müssen im Datenverteiler, mit dem sich die SWE verbindet vorhanden sein:
* ''kb.systemModellGlobal'' aus dem KV ''kv.kappich'', mind. in Version 38

#### Parametrierung
Die SWE benötigt zum Betrieb folgende Parameter:
* ''atg.archiv'' an einem Objekt vom Typ ''typ.archiv''

Über die Parametrierung der ''atg.archiv'' wird festgelegt, auf welche Datenidentifikationen sich die SWE anmeldet. Die Empfangenen Daten werden dann transformiert und via REST API an einen REST Server versandt (vgl. [Archivparameterierung](https://gitlab.nerz-ev.de/ERZ/SPEZ_de.bsvrz.ars/blob/master/06-BetrInf/BetrInf_ArS_FREI_V15.0_D2018-02-14.pdf)).  

### Aufnahme des Betriebs

#### Startparameter
Die SWE verwendet die Datenverteiler - Applikationsfunktionen zur Kommunikation mit dem Datenverteiler und unterstützt bzw. benötigt daher auch die entsprechenden Aufrugparameter (siehe [Betriebsinformationen DaV](https://gitlab.nerz-ev.de/ERZ/SPEZ_de.bsvrz.kernsoftware/blob/master/06-BetrInf/BetrInf_DaV-DAF_FREI_V6.0_D2018-10-30.pdf)).

* `-objekt=<pid>` : Die PID des Applikationsobjektes (vom Typ `typ.archiv`)
* `-url=http://localhost` : Die URL des REST Servers, der Standardwert ist `http://localhost`.
* `-port=80` : Der Port des REST Servers, der Standardwert ist `9998`.

