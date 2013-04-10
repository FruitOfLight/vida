Pre spravne vytvorenie postera pouzite pdflatex a
nasledovnu postupnost prikazov:

1. pdflatex main
2. bibtex main
3. pdflatex main 

(Krok 2 vytvori zoznam literatury. Krok 3 je potrebny aby sa spravne
zakomponovali odkazy na literaturu.)

Velkost posteru je zadefinovana v subore papermyposter.cfg.

----

V pripade, ze pouzijete obycajny latex namiesto pdflatexu, nastanu
nasledujuce problemy:

- Obrazky treba prekonvertovat do formatu eps. U loga fakulty to
  pravdepodobne znamena, ze nepojde zachovat transparenciu pozadia.

- Vysledny dvi subor treba prekonvertovat do postskriptu a potom
  do pdf. Kedze ide o nestandardne velkosti, je potrebne pouzit
  nasledujucu postupnost prikazov:

   dvips -Ppdf -G0 main -o main.ps
   ps2pdf -g25512x25512 main.ps
  
  (velkost sa pocit ako inches * 720)


