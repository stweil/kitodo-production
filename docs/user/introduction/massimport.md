# Massenimport

Kitodo.Production bietet einen Massenimport für Metadaten an: Er ruft eine [Importkonfiguration](catalogimport.md) vom Typ *Katalogsuche* automatisch mit einer Liste von Identifiern auf. Für jeden Identifier wird die Katalogschnittstelle einmal abgefragt und bei erfolgreicher Abfrage ein Digitalisierungsvorgang in Kitodo angelegt.

Die Eingabemaske wird über das Symbol in der Spalte *Aktionen* der Projektdetails aufgerufen.

## Eingabemaske

Die Maske enthält folgende Elemente:

1. **Katalog-Auswahl**: enthält alle Importkonfigurationen vom Typ *Katalogsuche*, auf die der aktuelle Benutzer über die ihm zugewiesenen Mandanten Zugriff hat. Alle Identifier der Tabelle werden beim Auslösen an die ausgewählte Katalogsuche geschickt; die erhaltenen Metadaten werden anhand der zugehörigen [Abbildungsdateien](catalogimport.md#abbildungsdateien) in einen Kitodo-Vorgang umgewandelt.
2. **Tabelle für Datensatz-IDs und zusätzliche Metadaten**: Jede Zeile entspricht einem zu importierenden Datensatz und muss mindestens eine Zelle mit der ID des Datensatzes enthalten. Die IDs stehen immer in der ersten Spalte; über das `+`-Symbol unter der Tabelle können Zeilen hinzugefügt werden. Über das `+`-Symbol in der Titelleiste der Tabelle können weitere Spalten für zusätzliche Metadaten ergänzt werden; zur Auswahl stehen alle Metadaten des verwendeten Regelsatzes, die mit allen Dokumenttypen kompatibel sind. Die Werte in den Zellen können per Mausklick direkt in der Tabelle bearbeitet werden; einzelne Zeilen oder ganze Metadatenspalten können vor dem Auslösen gelöscht werden.
3. **CSV-Datei-Upload**: Die Identifier können alternativ aus einer CSV-Datei übernommen werden (Buttons *Auswählen* und *Hochladen*, erst nach der Katalogauswahl sichtbar). In der ersten Zeile der Datei stehen die Namen der Metadaten-Spalten (die „keys“ des Regelsatzes); die erste Spalte enthält immer die Katalog-IDs. Beispiel:

   ```
   ID, title, place
   123, Band 1, Hamburg
   456, Band 2, Dresden
   789, Band 3, Berlin
   ```
4. **CSV-Spaltentrennzeichen**: `,` oder `;` – kann auch nach dem Upload umgeschaltet werden, wenn sich herausstellt, dass ein falsches Trennzeichen gewählt wurde (erkennbar daran, dass alle Spalten ungetrennt in der ersten Spalte landen).
5. **Knöpfe *Massenimport* und *Abbrechen***: *Massenimport* löst den Import für die ausgewählte Katalogsuche und die aktuelle Tabelle aus; *Abbrechen* führt zurück zur Projektliste.

## Ergebnis

Nach dem Auslösen zeigt ein Fortschrittsbalken den Fortschritt des Massenimports an. Anschließend informiert ein Ergebnis-Dialog, welche Datensätze erfolgreich importiert wurden und bei denen es zu Problemen kam, die den Import verhinderten.

## Weitere Möglichkeiten des Massenimports

* Der [KitodoScript](../../developer/api/kitodoscript.md)-Befehl `action:importProcesses` importiert Vorgänge aus einem Verzeichnis von Metadaten-Dateien (wird im Taskmanager ausgeführt).
* EAD-Sammlungen können ebenfalls importiert werden (EAD-Import).
