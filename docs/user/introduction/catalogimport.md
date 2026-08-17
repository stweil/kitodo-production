# Importkonfigurationen und Katalogsuche

Um Metadaten aus externen Katalogen oder aus Dateien zu übernehmen, verwendet Kitodo.Production **Importkonfigurationen**. Sie entsprechen den Katalog- bzw. OPAC-Konfigurationen, die in früheren Versionen über die Datei `kitodo_opac.xml` definiert wurden, können aber direkt über die Oberfläche als eigene Objekte erzeugt und verwaltet werden.

Für jede Importkonfiguration kann einer der folgenden Typen ausgewählt werden:

* **Katalogsuche**: Metadaten werden von einer Suchschnittstelle abgefragt (entspricht `<catalog>`-Einträgen in der `kitodo_opac.xml` *ohne* `<fileUpload>true</fileUpload>`).
* **Dateiupload**: Metadaten werden nicht durch Abfragen einer Schnittstelle, sondern über vom lokalen Rechner hochgeladene XML-Dateien importiert (entspricht `<catalogue>`-Einträgen *mit* `<fileUpload>true</fileUpload>`).
* **Vorlagenvorgang**: Der Benutzer legt einen bestehenden Vorgang als Vorlage fest, aus dessen Metadaten neue Vorgänge erzeugt werden.

## XML-Validierung

Ab Kitodo.Production 4.0 werden die bei einer Katalogabfrage zurückgelieferten XML-Daten gegen ihre Schema-Definitionen validiert. Tritt ein Validierungsfehler auf, wird der Metadatenimport abgebrochen und eine Fehlermeldung angezeigt. Da die Nutzenden in der Regel keine Kontrolle über die Daten des abgefragten Katalogs haben, kann die XML-Validierung in der Bearbeitungsmaske der Importkonfiguration für Einzelfälle deaktiviert werden.

## Suchfelder

Importkonfigurationen vom Typ *Katalogsuche* benötigen Suchfelder, die bei der Anfrage an die Schnittstelle verwendet werden. Sie werden über die Liste *Suchfelder* hinzugefügt.

* Mindestens ein Suchfeld muss als **ID-Feld** festgelegt sein, damit Kitodo einzelne Datensätze eindeutig referenzieren kann (z. B. für den [Massenimport](massimport.md)).
* Über das **Default-Suchfeld** kann festgelegt werden, welches Suchfeld beim Anlegen eines neuen Vorgangs in der Importmaske vorausgewählt ist.

Die restlichen Eingabefelder der Bearbeitungsmaske (Suchschnittstellenart, Metadatenformat, Default-Importtiefe usw.) entsprechen den XML-Kindelementen der früheren `kitodo_opac.xml`-Einträge.

## Abbildungsdateien

Alle Importkonfigurationen vom Typ *Katalogsuche* oder *Dateiupload* benötigen Regeln, mit denen die importierten Metadaten aus dem Originalformat in das interne Kitodo-Metadatenformat umgewandelt werden. Diese Regeln werden in XSL-Transformationsdateien definiert und in der Oberfläche als **Abbildungsdateien** angelegt: Für jede Abbildungsdatei werden neben Namen und Beschreibung ein Eingabe- und ein Ausgabemetadatenformat festgelegt und die Datei wird der Importkonfiguration zugeordnet (entspricht den `<mappingFiles>`-Elementen der `kitodo_opac.xml`).

Durch das explizite Festlegen der Formate wird gewährleistet, dass bei mehreren aufeinanderfolgenden Transformationen das Ausgabeformat einer Abbildungsdatei mit dem Eingabeformat der nächsten übereinstimmt. Über die Option *Vorstrukturierter Import* wird festgelegt, ob die Abbildungsdateien auch die METS-Struktur eines Vorgangs aus bereits existierenden Strukturen des importierten Datensatzes erzeugen, oder ob sie nur der Metadatenabbildung dienen.

Alle Importkonfigurationen stehen dem Benutzer über die Schaltfläche *Datenübernahme* in der Importmaske zur Verfügung, gruppiert nach den oben beschriebenen Typen.

## Migration vorhandener Katalogkonfigurationen

Zur Umwandlung bestehender Katalogkonfigurationen aus einer `kitodo_opac.xml` bietet Kitodo einen Konverter an: Über den Button *Katalogkonfigurationen importieren* auf dem Tab *Importkonfigurationen* der Projektdetails werden alle in der Datei gefundenen Konfigurationen aufgelistet. Nach der Auswahl und mit dem Klick auf *Start* wird die Umwandlung teilautomatisch durchgeführt; die in den Konfigurationen hinterlegten XSL-Abbildungsdateien werden dabei ebenfalls umgewandelt. Da Abbildungsdateien explizite Ein- und Ausgabemetadatenformate enthalten, muss für jede noch nicht umgewandelte Abbildungsdatei angegeben werden, in welches Format transformiert wird.

## Katalogsuche und Metadata-Import

In der Importmaske öffnet sich das Fenster *Katalogsuche* mit folgenden Inhalten:

1. **Katalog** – Auswahl des Katalogs (bzw. der Importkonfiguration), in dem gesucht wird. Für jedes Projekt kann ein Default-Katalog konfiguriert werden.
2. **Feld** – Auswahl des Suchfelds, in dem der Wert gesucht wird. Für jede Importkonfiguration kann ein Default-Suchfeld konfiguriert werden.
3. **Wert** – Das Suchkriterium, z. B. ein Identifier.
4. **Importtiefe** – Anzahl der Hierarchieebenen, die importiert wird (hauptsächlich für mehrbändige Werke relevant).
5. **Zusätzlicher Import** – Hinzufügen von Metadaten: Wenn diese Option aktiviert ist, werden die Metadaten des vorangegangenen Imports beibehalten und mit den Metadaten des aktuellen Imports ergänzt; bestehende Metadaten werden dabei nicht überschrieben.

## Dateien uploaden

Importkonfigurationen vom Typ *Dateiupload* stellen einen Upload einzelner XML-Dateien bereit, aus dem die Metadaten anhand der zugehörigen Abbildungsdateien übernommen werden. Der Ablauf entspricht der Katalogsuche; anstelle der Abfrage der Schnittstelle wird die lokal ausgewählte Datei verarbeitet.
