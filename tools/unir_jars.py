#!/usr/bin/env python3
"""Mete los .jar de la carpeta 'originales' DENTRO del jar de Dedsafio 4 (jar-in-jar de Fabric).
Ademas acelera la aparicion de los mobs de Gleba (Nautilus y Cerebro) en Ghostsafio: de cada 50 ticks a cada 20.
Uso: unir_jars.py <dedsafio4.jar> <carpeta_originales> <salida.jar>"""
import sys, os, io, json, glob, zipfile

AQUI = os.path.dirname(os.path.abspath(__file__))
INTERVALO_NUEVO = 20  # ticks entre intentos de aparicion (antes 50)
PATRON = bytes([0x10, 50, 0x70, 0x99])  # bipush 50; irem; ifeq  (GlebaSpawner.tick)

def parchear_ghostsafio(ruta):
    """Devuelve los bytes del jar con GlebaSpawner acelerado."""
    salida = io.BytesIO()
    with zipfile.ZipFile(ruta) as zi, zipfile.ZipFile(salida, 'w', zipfile.ZIP_DEFLATED) as zo:
        for it in zi.infolist():
            if it.is_dir():
                continue
            data = zi.read(it.filename)
            if it.filename == 'com/ghostsafio/mitomod/GlebaSpawner.class':
                if data.count(PATRON) == 1:
                    data = data.replace(PATRON, bytes([0x10, INTERVALO_NUEVO, 0x70, 0x99]))
                    print('  GlebaSpawner acelerado: 50 -> %d ticks' % INTERVALO_NUEVO)
                else:
                    print('  AVISO: no se encontro el patron en GlebaSpawner (se deja igual)')
            if it.filename == 'assets/ghostsafio/models/item/nave.json':
                data = json.dumps({"parent": "minecraft:item/generated",
                                   "textures": {"layer0": "ghostsafio:item/nave"}}).encode('utf-8')
                print('  Nave de Ghostsafio: modelo con su propio icono')
            zo.writestr(it.filename, data)
        icono = os.path.join(AQUI, 'nave.png')
        if os.path.exists(icono):
            zo.write(icono, 'assets/ghostsafio/textures/item/nave.png')
    return salida.getvalue()

src, carpeta, salida = sys.argv[1:4]
jars = sorted(glob.glob(os.path.join(carpeta, '*.jar')))
with zipfile.ZipFile(src) as zi, zipfile.ZipFile(salida, 'w', zipfile.ZIP_DEFLATED) as zo:
    for it in zi.infolist():
        if it.is_dir():
            continue
        data = zi.read(it.filename)
        if it.filename == 'fabric.mod.json':
            meta = json.loads(data.decode('utf-8'))
            meta['jars'] = [{'file': 'META-INF/jars/' + os.path.basename(j)} for j in jars]
            data = json.dumps(meta, indent=2, ensure_ascii=False).encode('utf-8')
        zo.writestr(it.filename, data)
    for j in jars:
        nombre = 'META-INF/jars/' + os.path.basename(j)
        if os.path.basename(j).startswith('ghostsafio'):
            zo.writestr(nombre, parchear_ghostsafio(j))
        else:
            zo.write(j, nombre)
print('Listo:', salida, '->', len(jars), 'mods dentro')
