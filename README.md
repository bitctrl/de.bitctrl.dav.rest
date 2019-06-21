# de.bitctrl.dav.rest

REST API für den Datenverteiler
 
 
[![Build Status](https://travis-ci.org/bitctrl/de.bitctrl.dav.rest.svg?branch=master)](https://travis-ci.org/bitctrl/de.bitctrl.dav.rest)


## Entwicklungsdokumentation

Erstellung mit Gradle: ```./gradlew build```

Die RAML Beschreibung der Kommunikationsschnittstelle liegt hier: **[RAML Modell](https://github.com/bitctrl/de.bitctrl.dav.rest/tree/master/de.bitctrl.dav.rest.api/src/main/resources)**

Die Struktur des Repositories gliedert sich in mehrere Unterprojekte.

### [REST API](https://github.com/bitctrl/de.bitctrl.dav.rest/tree/master/de.bitctrl.dav.rest.api)

Bibliothek zur Serialisierung und Deserialisierung der JSON Requests.

Die Generierung der Klassen zu Serialisierung und Deserialisierung geschieht mit Hilfe des **ramltojaxrs** Gradle Plugins und der entsprechenden Gradle Task.


### [Client Implementierung](https://github.com/bitctrl/de.bitctrl.dav.rest/tree/master/de.bitctrl.dav.rest.client)
Implementierung eines Clients für ERZ Datenverteiler.
Für die Kommunikation mit einer Gegenstelle wird die Jersey Web-Client Implementierung verwendet.


### [Server Implementierung](https://github.com/bitctrl/de.bitctrl.dav.rest/tree/master/de.bitctrl.dav.rest.server)
Beispiel Implementierung eines einfachen Servers, der alle empfangen Request in ein Logfile ausgibt.


