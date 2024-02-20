# Dateipfad zur Textdatei
dateipfad = 'notflush.txt'

# Liste zum Speichern der Zeilen aus der Datei
zeilen = []

# Datei öffnen und Zeilen in die Liste einlesen
with open(dateipfad, 'r') as datei:
    zeilen = datei.readlines()

# Sortiere die Zeilen nach der Zahl hinter dem letzten Komma
sortierte_zeilen = sorted(zeilen, key=lambda x: int(x.split(',')[-1]))

# Datei mit den sortierten Zeilen überschreiben
with open(dateipfad, 'w') as datei:
    datei.writelines(sortierte_zeilen)