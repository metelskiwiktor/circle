1) Stwórz projekt, pobierz credentials.json oraz podmień/wklej je w istniejący plik w tym folderze 'credentials.json'
Krok 1. Enable Google Drive APIs oraz 2. Download Google drive project credentials:
https://technicalsand.com/file-operations-in-google-drive-api-with-spring-boot/
2) Skonfiguruj właściwości projektu w application.yaml według własnych preferencji:
----baza danych----
project.env.db.backup-scheduler: true/false - czy ma się co zdefiniowany czas uruchamiać usługa tworząca backup bazy danych
project.env.db.delete-after-backup: true/false - czy backup danych po wykonaniu wszystkich operacji (w tym wysłanie do google) ma się usunąć z plików lokalnych
project.env.db.backup-cron-scheduler: cron-expression - zdefiniowanie interwału czasu po którym scheduler powinien się uruchomić, domyślnie o pełnej godzinie co godzinę. Więcej o cronie i generowaniu tutaj: https://www.baeldung.com/cron-expressions, spring scheduler cron różni się od np. linuxowego, więcej tutaj: https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron

Backup bazy danych może zostać stworzony dopiero po upływie 4h od poprzedniego, niezależnie od tego czy scheduler się uruchomił wcześniej. Ma to zapobiec sytuacji gdzie stworzą się 2 kopie po wyłączeniu i włączeniu aplikacji, które tereotycznie nie wnoszą żadnej wartości backupu. Dane o czasach backupów są przechowywanie w bazie danych

----logi----
project.env.logs.backup-scheduler: true/false - patrz: baza danych
project.env.logs.delete-after-backup: true/false - patrz: baza danych
project.env.logs.backup-cron-scheduler: cron-expression - patrz: baza danych

Backup logów sprawdza istniejące pliki w katalogu env/logs/*.gz, następnie pobiera z nich daty i zapisuje je do Google Drive tworząc odpowiednie foldery. Po zapisaniu domyślnie je usuwa, co można zmienić konfiguracją delete-after-backup

----google----
project.env.enabled: true/false - przy 'true' łączy się z google drive api korzystając z credentials.json by się zautentykować (przy pierwszym uruchomieniu, przy kolejnych nie trzeba, dane są zapisane w zakładce "tokens" głównego folderu projektu). Jest to potrzebne do zapisywania backupów bazy danych/logów schedulera. W przypadku 'false' scheduler wykonuje swoją robotę bez zapisywania plików w google, więc nic się nie popsuje (będzie w logach warning że nie ma providera do zapisywania plików)

----web----
project.env.web.start-browser: true/false - uruchomienie przeglądarki przy starcie aplikacji z przekierowaniem na panel backendu
project.env.web.frontend-url: string - url frontendu, potrzebne by skonfigurować cors, więcej informacji tutaj: 
https://en.wikipedia.org/wiki/Cross-origin_resource_sharing (kwestie bezpieczeństwa)

----datasource----
spring.datasource.username - nazwa użytkownika bazy danych, na panelu można wejść w konsolę bazę danych i tam wpisać zdefiniowaną nazwę użytkownika
spring.datasource.password - hasło użytkownika bazy danych, na panelu można wejść w konsolę bazę danych i tam wpisać zdefiniowane hasło

----server----
server.port: int - port, na którym ma być uruchamiana aplikacja. Konsola przy przekierowaniu uwzględnia zmianę portu, więc zmiana portu nie powinna nosić za sobą żadnych większych konsekwencji
3) Uruchom run-project.cmd