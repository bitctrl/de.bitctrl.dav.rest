# Dav REST Client

Client-Implementierung der REST API für Datenverteiler (Dav3)

# Kurzbeschreibung der Funktionalität

Die Software realisiert den Client einer REST API
 
 
# Betriebsinformationen

## Installtation der Software

### Voraussetzungen

### Erstinstalltion

## Einrichtung der Software

### Konfiguration
Folgende Konfigurationsbereiche müssen im Datenverteiler, mit dem sich die SWE verbindet vorhanden sein:
* ''kb.systemModellGlobal'' aus dem KV ''kv.kappich'', mind. in Version 38

### Parametrierung
Die SWE benötigt zum Betrieb folgende Parameter:
* ''atg.archiv'' an einem Objekt vom Typ ''typ.archiv''

Über die Parametrierung der ''atg.archiv'' wird festgelegt, auf welche Datenidentifikationen sich die SWE anmeldet. Die Empfangenen Daten werden dann transformiert und via REST API an einen REST Server versandt (vgl. [Archivparameterierung](https://gitlab.nerz-ev.de/ERZ/SPEZ_de.bsvrz.ars/blob/master/06-BetrInf/BetrInf_ArS_FREI_V15.0_D2018-02-14.pdf)).  

## Aufnahme des Betriebs

### Startparameter
Die SWE verwendet die Datenverteiler - Applikationsfunktionen zur Kommunikation mit dem Datenverteiler und unterstützt bzw. benötigt daher auch die entsprechenden Aufrugparameter (siehe [Betriebsinformationen DaV](https://gitlab.nerz-ev.de/ERZ/SPEZ_de.bsvrz.kernsoftware/blob/master/06-BetrInf/BetrInf_DaV-DAF_FREI_V6.0_D2018-10-30.pdf)).


## Unterbrechen oder Beendigung des Betriebs


