Osztott rendszerek esti tagozatos beadandó feladat 2015/16/2.
=============================================================
Szólánc
-------
Tudnivalók
----------
Beadás határideje: __2016.05.15. 23:59__ (szigorú)

- Beadás módja: a [BE-AD](https://bead.inf.elte.hu) rendszeren keresztül, a forráskódok (nem az egész project) a csomagszerkezetnek megfelelő mappaszerkezetben tömörítve, egyetlen ```zip``` állományként.

- A feladatra többször is beadható megoldás, mindig a legutolsót veszem figyelembe; a beadott megoldásról látszik, hogy ellenőriztem-e. Az elutasított megoldáshoz kapcsolt megjegyzés is fog tartozni, ami segít azt kijavítani az újra beküldéshez. __Nem célszerű tehát az utolsó pillanatig várni a megoldás (első) beküldésére.__

- Értékelés: elfogadott / nem elfogadott

- Megvalósítás: Java nyelven, socketekkel

A feladat összefoglaló leírása
------------------------------
A klasszikus szólánc játék során a 2 játékos felváltva mond 1-1 szót úgy, hogy a következő szónak mindig az előző szó utolsó betűjével kell kezdődnie. Készíts egy szervert, ami biztosítja a játékos párok közötti kommunikációt és nyomon követi a játékmeneteket!

A játékszerver
--------------
Készítsd el a ```Jatekszerver``` osztályt a ```bead.egyszeru``` csomagba. A játékszervert a ```65456``` porthoz rendeljük. A szerver egy játékmenetet a következőképpen bonyolít le:

- Várakozik két játékos csatlakozására, akik a csatlakozás után megküldik a szervernek nevüket.

- A szerver létrehoz egy fájlt, amibe játékmenet során összeálló szóláncot fogja rögzíteni, a következő névvel: ```<jatekos1>_<jatekos2>_<idobelyeg>.txt```

- Amint a második játékos is csatlakozott, egy speciális ```start``` üzenettel jelzi az először csatlakozottnak, hogy ő a kezdőjátékos, tehát először neki kell egy tetszőleges szót mondania.

- A szerver innentől kezdve mindig fogad egy egy szavas üzenetet az egyik játékostól.

    - Ha ez a szó folytatja a szóláncot (ugyanazzal a betűvel kezdődik, mint a másik játékos előző szava, vagy ez az első szó), akkor továbbítja a másik játékos felé, aki válaszként elküldi a szólánc következő elemét, amit a szerver továbbít a másik irányba stb.
    
    - Ha a szó nem folytatja a szóláncot, akkor a szerver erről üzenetet küld a kliensnek, és vár egy új szót tőle. (Ha az is rossz, újra vár.)

- A szerver rögzíti a játék során összeálló szóláncot a játékmenethez létrehozott fájlba. Egy sorban a beküldő játékos neve, majd attól szóközzel elválasztva az általa beküldött szó szerepeljen.

- Ha valamelyik játékos az ```exit``` üzenetet küldi (ez a szóláncban tiltott szó lesz), vagy váratlanul lecsatlakozik, a játékmenet véget ér, és a másik játékos ```nyert```. A nyertest a szerver a nyert üzenettel értesíti, majd mindkét játékossal bontja a kapcsolatot.

- A klienseket külső telnetes programmal, kézzel indítva lehet vezérelni.

A szervert készítsük fel több játékmenet egy időben történő kezelésére: tehát minden két, egymás után csatlakozott játékoshoz indítson el egy játékmenetet, majd azonnal legyen képes újabb két játékos fogadására. A szerver álljon le, ha 60 másodpercen keresztül nem csatlakozik egy játékos sem (/tipp:/ ```ServerSocket``` osztály ```setSoTimeout``` metódusa), és már nincsen folyamatban lévő játék.

A tiltott szavak szervere
--------------------------
Módosítsd a játékszervert az alábbiak szerint, és az így kapott új program (és minden vonatkozó osztály) kerüljön a ```bead.osszetett``` csomagba.

Készíts egy ```TiltottDeploy``` nevű osztályt, amelynek főprogramja ```tiltott1```, ```tiltott2``` stb. neveken ```TiltottSzerver``` objektumokat jegyez be az RMI névszolgáltatásba (amely a programon kívülről induljon el, a szolgáltatás alapértelmezett portján), összesen annyit, amennyi az első parancssori paraméterének számszerű értéke, amelyről feltesszük, hogy legalább kettő. Az objektumok megkapják a ```tiltott1.txt```, ```tiltott2.txt``` stb. fájlok szöveges tartalmát, amely soronként egy-egy szót tartalmaz; ezeket a szavakat eltárolják.

A játékszerver mindkét kapcsolódott klienshez számontartja, hogy melyik tiltott szavas szerver tartozik hozzá; ez kezdetben az első játékoshoz a ```tiltott1```, a második játékoshoz a ```tiltott2```. Minden alkalommal, amikor szó érkezik be valamelyik klienstől, a játékszerver meghívja a megfelelő tiltott szavas szerver ```tiltottE``` műveletét, amely visszatér azzal, hogy a szó szerepelt-e már nála, és egyúttal el is tárolja a szót. Ha szerepelt, akkor ez tiltott szó volt, és a játékos veszített; különben a játék megy tovább.

Ha valamelyik játékos a ```"tiltott 1"```, ```"tiltott 2"``` stb. sort küldi, az nem tippként kezelendő: ilyenkor a szerver állítsa át az ellenfélhez rendelt szervert. Előfordulhat, hogy érvénytelen a szám, pl. a beküldött sor tartalma ```"tiltott 42"```, de csak három ilyen szerver van, akkor a sort hagyja figyelmen kívül. Megtörténhet, hogy a két játékos ugyanahhoz a tiltott szavas szerverhez rendeli egymást, ez nem probléma. A játékosnak attól függetlenül, hogy tiltó üzenetet küldött, még tippelnie kell (amit megelőzhet még újabb tiltás-átállítási lépés).

