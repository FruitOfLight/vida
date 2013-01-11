# Specifikacie a standardy

## Graph 
 * ma zoznam vrcholov aj hran
 * ma funkciu draw(Canvas)
   + preco nemat Canvas ako premennu? lebo je to zbytocne, nikde inde ako v draw sa nepouzije
     - ani sa nema pouzit, graf nema byt od canvasu nijako zavisly, dokonca sa ani nema
       starat, ci sa do canvasu zmesti ( ved nech sa kludne nezmesti, bude sa dat posuvat a
       zoomovat ) 
   + draw kompletne vykresli sameho seba
   + niekde mozu byt aj nejake zobrazovacie veci ako napr zoom a shift -- to by som asi dal do 
     canvasu, ale nie nutne -- treba zvazit

## Edge
 * zvazit ci directed alebo undirected
 * ma Vertexy from a to nie inty ( a pokial je undirected, asi by sa to nemalo volat from a to)

 * zvazit, ci bude mat zoznam sprav, alebo sa o to bude starat ina entita

## Vertex
 * pozicia, farba, velkost -- veci ohladom vykreslovaia
   ked sa bude dat zoomovat, tak to mozno chcu byt realne cisla, ktovie
 * susedne hrany -- veci ohladom grafu

## Program
 

## Model


# Trable a na co treba mysliet

## Chceme vizualizovat

treba vediet pekne zobrazovat nejake premenne procesov - napriklad f ktorom su fragmente, na
ktorom su leveli, aku maju naladu, ci je niektory sef

bude treba spravit nejake temne listenery, napriklad listener, ktory nastavuje velkost vrchola podla
levelu programu farbu vrchola podla nalady

## Efektivita

sem tam je v kode pouzite provizorne riesenie indexOf, ktore asi funguje v case O(n),
casom sa mozme zamysliet, ci to nechceme zrychlit
