#!/usr/bin/python

import Image
import glob
import os

show = 0

for imgfile in glob.glob('gui-buttons/*.png'):
    img = Image.open(imgfile).convert('RGBA')
    data = img.getdata()
    hoverData = []
    activeData = []
    activeHoverData = []
    inactiveData = []
    inactiveHoverData = []
    pressedData = []
    for pixel in data:
        r,g,b,a = pixel
        hoverData.append((127-3*r//2, 127-3*g//2, 127-3*b//2, a))
        activeData.append((0, r+g+b, 0, a))
        activeHoverData.append((0, 255-r-g-b, 0, a))
        inactiveData.append((r+g+b, 0, 0, a))
        inactiveHoverData.append((255-r-g-b, 0, 0, a))
        pressedData.append((0, 0, 255-r-g-b, a))

    noneFile = imgfile.replace("gui-buttons/","");
    hoverFile = imgfile.replace("buttons","buttons-hover");
    activeFile = imgfile.replace("buttons","buttons-active");
    activeHoverFile = imgfile.replace("buttons","buttons-active-hover");
    inactiveFile = imgfile.replace("buttons","buttons-inactive");
    inactiveHoverFile = imgfile.replace("buttons","buttons-inactive-hover");
    pressedFile = imgfile.replace("buttons","buttons-pressed");
    print noneFile
        
    img.putdata(hoverData);
    img.save(hoverFile);
    img.putdata(activeData);
    img.save(activeFile);
    img.putdata(activeHoverData);
    img.save(activeHoverFile);
    img.putdata(inactiveData);
    img.save(inactiveFile);
    img.putdata(inactiveHoverData);
    img.save(inactiveHoverFile);
    img.putdata(pressedData);
    img.save(pressedFile);
print
