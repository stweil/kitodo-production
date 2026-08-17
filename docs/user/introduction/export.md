# Export

Durch den Export wird ein Vorgang (Images und die Meta- und Strukturdaten) in die jeweilige Präsentation bzw. das DMS exportiert. Es gibt unterschiedliche Möglichkeiten, wie der Export ausgelöst werden kann:

* Automatischer Export
* Manueller Export
* Export über ein [KitodoScript](../../developer/api/kitodoscript.md)

## Automatischer Export

Der automatische Export wird ausgelöst, wenn die vorherige Aufgabe abgeschlossen wurde; der Vorgang wird im Hintergrund ausgeführt. Dafür müssen zwei Voraussetzungen erfüllt sein:

1. **Projekteinstellungen**: In den technischen Daten des Projekts sind u. a. *Automatischer DMS-Export*, *Erzeuge Vorgangsordner* sowie die Pfade *DMS-Export-Ordner für XML-Datei* und *DMS-Export-Images-Ordner* zu setzen.
2. **Aufgabeneinstellungen**: In den Details der Export-Aufgabe müssen die Eigenschaften *Export DMS* und *Automatische Aufgabe* gesetzt sein. Am besten wird dies bereits beim Erstellen der Produktionsvorlage berücksichtigt, damit die Einstellungen beim Anlegen der Vorgänge automatisch übernommen werden.

## Manueller Export

Der manuelle Export benötigt die entsprechenden Berechtigungen. Er kann für einen einzelnen Vorgang (z. B. aus der Aufgabenansicht über *Aktionen*) oder für eine Liste von Vorgängen in der Verwaltung ausgelöst werden.

Manuelle Exporte werden **nicht** im Hintergrund ausgeführt, während des Exports kann also in Kitodo.Production nichts anderes bearbeitet werden. Je nach Umfang des Vorgangs kann dies längere Zeit dauern. Nach dem Export muss die Aufgabe abgeschlossen werden, damit die darauf folgende Aufgabe bearbeitet werden kann.

Hinweis: Ist der Benutzer nicht für den Export in das DMS eingerichtet, werden die Metadaten nur in das [Homeverzeichnis](homedirectory.md) des Nutzers exportiert. Dieser Weg sollte nur in Ausnahmefällen gewählt werden!

Bei einem Export auf eine Trefferliste ist unbedingt darauf zu achten, dass die Liste genau die Vorgänge enthält, die exportiert werden sollen.

## Export über KitodoScript

Für Trefferlisten in der Verwaltung kann der [KitodoScript](../../developer/api/kitodoscript.md)-Befehl `action:exportDms` verwendet werden:

```
action:exportDms exportImages:false
```

Wird der Wert `false` für `exportImages` verwendet, werden nur die Metadaten exportiert; mit `true` werden zusätzlich die Images exportiert.
