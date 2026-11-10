# Dedsafio 4 – mod unificado (Fabric 1.21.1)

## Cómo convertirlo en .jar con GitHub
1. Crea un repositorio nuevo en GitHub y sube TODO el contenido de esta carpeta (incluida `.github`).
2. Entra a la pestaña **Actions** → "Compilar Dedsafio 4" (se ejecuta solo al subir; también puedes darle *Run workflow*).
3. Cuando termine en verde, abre la ejecución y baja el archivo de **Artifacts** → `dedsafio4`.
4. Si falla en rojo, copia el mensaje de error y me lo pegas.

## Novedades (última)
- **Nave de Ghostsafio con su propio ítem**: ahora usa el icono del cohete de Ruleta (no el cohete de fuegos artificiales). La `nave_espacial` de Ruleta queda oculta (era la repetida).
- **Se arma con 5 piezas** (receta de 3x3: cabina arriba, ala izquierda + motor + ala derecha en medio, fuselaje abajo): Cabina, Ala Izquierda, Ala Derecha, Motor y Fuselaje de la Nave. Las recetas de las piezas están en `src/main/resources/data/dedsafio4/recipe/`. Nueva pestaña *Dedsafio - Espacio*.
- **Espada de Gleba**: 10 de daño.
- El icono de la nave también va dentro de este mod (`assets/ghostsafio/...`), así que se ve aunque no uses el `COMPLETO.jar`.

## Novedades (anteriores)
- **Pestañas por categoría** (en vez de una sola): *Dedsafio - Gleba*, *Dedsafio - Prehistórico*, *Dedsafio - Limbo*, *Dedsafio - Bolsas* y *Dedsafio - General*. Dentro de cada una van primero los ítems y al final los bloques. Qué va en cada una se cambia en `src/main/resources/dedsafio4_categorias.json`.
- **Tótems devueltos**: ya no se esconde nada; solo se muestra una vez cada ítem repetido entre mods (por ejemplo el Tótem Montaña).
- **Nautilus Óseo Pequeño**: aparece poco a poco en Gleba, como el Nautilus y el Cerebro (hasta 6 cerca de cada jugador). Vuela, aplica Levitación y no se puede empujar. Huevo incluido.
- **Aparición más rápida** del Nautilus y el Cerebro: el compilador de `tools/unir_jars.py` cambia el intervalo de 50 a 20 ticks al armar el `.jar` completo.
- **Pelos de Gleba** y **Garra de Gleba** son bloques y salen en vetas raras en Gleba.

## Qué hace
- Una sola pestaña creativa **Dedsafio** con los ítems de Ruleta, Ghostsafio, Ghostitem, Sacos y Silla; **los bloques van al final**.
- Las pestañas viejas se esconden. Los **tótems** y la `nave_espacial` de Ruleta no salen en el catálogo (siguen existiendo con `/give`).
- Ítems repetidos entre mods (corazones, ámbar, grasa…): se muestra uno solo (prioridad Ruleta > Ghostitem > Ghostsafio).
- 11 ítems nuevos con tus texturas: Antídoto, Fruta del Espacio, Fruto de Quiu, Gigantofruta, Pastilla Vacía, Sangre de Reptisaurio, Carne Glebanoide, Cerebro de Gleba, Costilla, Cuero y Espada de Gleba.
- Gleba: se conectan al bioma `grasa_ore`, `raices_colgantes` y `cuerdas_colgantes` (ya existían en los datos de Ghostsafio pero no estaban activos).
- Descripciones: en `src/main/resources/assets/dedsafio4/lang/es_es.json` agrega líneas `"item.<mod>.<item>.desc": "texto"` (por ejemplo `"item.ruleta.ambar.desc"`). Salen en gris en el tooltip. Para varias líneas usa `\n`.

## Los otros mods
Este mod NO trae adentro los 5 mods originales; instálalos junto a él en la carpeta `mods`.
Para tener **un solo archivo**: sube los 5 `.jar` originales a la carpeta `originales/` del repositorio
(los grandes, 39 MB y 69 MB, necesitan `git` o GitHub Desktop; la web solo deja 25 MB por archivo).
El workflow genera entonces `dedsafio4-COMPLETO.jar` con todo adentro.

## Pendiente (necesita más trabajo)
- Aspecto de Gleba (suelo blanco, árboles rojos enormes, cielo): vive en el código compilado de Ghostsafio.
- Unificar el cohete "Nave": solo está oculto el de Ruleta.
- Textos de descripción oficiales de Dedsafio 4.
