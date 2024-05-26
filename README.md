# Modern-Europa
Benny Schärer | L-TIN-22-Di-a
## Abstrakt
Für das OOP-Projekt wird ein Multiplayer Real-Time-Strategie-Spiel mit dem Name Modern-Europa entwickelt. In diesem EPIC wird der Umfang dieses Projektes beschrieben. Ziel des Spiels ist es den Gegner Spieler mit Einheiten zu überrumpeln und seine Gebäude zu zerstören. Die vom Lehrer definierten Kriterien müssen dafür abgedeckt sein.
## Features
-	Das Spiel kann per Peer to Peer über das Netzwerk mit mindestens einer anderen Person gespielt werden. Dabei übernimmt ein Spieler die Rolle des Servers.
-	Das Spielfeld besteht aus einem Isometrischen Gitter.
-	Es gibt eine Einheit, die erstellt werden kann und sich mittels Pathfinding selbst über die Karte bewegt und dabei auf Hindernisse achtet.
-	Es gibt ein Gebäude, das auf einen freien Platz gebaut werden kann.
-	Mit einer Ressource kann der Spieler Gebäuden bauen und Einheiten erstellen.
-	Sobald ein Spieler keine Gebäude mehr besitzt, ist das Spiel beendet und es wird ein Spieler als Sieger deklariert.
-	Der Spielfluss wird mit Log4J geloggt und unterstützt das Erkennen und Debuggen von Fehlern.
-	Ressourcen werden zufällig auf der Spielkarte verteilt. (Optional* die ganze Spielkarte wird generiert.)
## Anleitung
- Eine Technische Dokumentation ist unter dem "doc" Ordner zu finden.
- Eine kurze Setup Guide ist ebenfalls unter "doc/setup.md" zu finden.