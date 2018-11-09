---
title: Memoria técnica de prácticas 1 y 2
date: Nuevos Paradigmas de Interacción
author:
- Pablo Baeyens Fernández
- Francisco Javier Morales Piqueras
- Jesús Sánchez de Lechina Tejada
documentclass: scrartcl
secnumdepth: 3
lang: es
numbersections: true
linkReferences: true
colorlinks: true
toc: true
---

\newpage

# Introducción e idea de museo

***Musemium***

La Mona Lisa está en el Louvre, las meninas en el Museo del Prado, los
girasoles de Vincent van Gogh en la National Gallery de Londres, El
Grito en Oslo. Pero no todo el arte del mundo está en Europa, La
persistencia del tiempo está en el Museo de Arte Moderno de Nueva
York, si queremos ver la mayor parte de la obra de Frida Khalo
tendríamos que desplazarnos hasta México. Y en Asia, África y Oceanía
podemos perder la cuenta con las obras que reflejan sus culturas.

Si quisiéramos contemplar una mínima parte todas estas obras
necesitaríamos años y un presupuesto desmesurado para viajar y ver
todo lo que nos hayamos propuesto.

¿Y si pudieramos reunir todas esas obras, y la información sobre ellas
en un único lugar?

Nuestra propuesta es justo esa. Desprendernos de la idea arquetípica
de museo. Tan solo necesitamos: Una pantalla, un teléfono y acceso a
internet. 

Con esto podemos preguntar sobre cualquier obra o autor de la que se
disponga información en nuestro museo. Y nuestra colección tiene
truco, es el fruto del poder de la comunidad y la
colaboración. Sólamente está limitada por la información en español
que podemos encontrar en [wikidata](https://www.wikidata.org/), por lo
cual la colección del museo seguirá creciendo sin que tengamos que
hacer nada. Además, tú puedes formar parte del museo colaborando con
esta base de datos.

# Funcionamiento de la aplicación

## Interfaz básica

La interfaz principal de la aplicación consta de 4 botones y una región con texto.
Los botones permiten acceder a distintas funcionalidades de la aplicación que también son accesibles mediante la interfaz oral. Los botones son:

Botón de habla
: Es la principal vía de interacción de la aplicación y permite interactuar con la interfaz oral, que da acceso a la interfaz oral. Su estado se modifica en función de si el bot está escuchando o no. Descrito en [Interfaz oral].

Botón de QR
: Permite acceder al lector de códigos QR. Descrito en [Uso de códigos QR].

Botón de 3D
: Permite acceder al visor 3D seleccionando el modelo a visualizar. Descrito en [Uso de vector de rotación].

Botón de opciones
: Permite acceder a las opciones y créditos de la aplicación. Descrito en [Uso de acelerómetro].

La región de texto contiene un texto inicial que describe el funcionamiento básico de la aplicación.
Además indica posibles problemas cuando la interfaz oral no está disponible (debido a la falta de algún requisito necesario en el dispositivo).

Además se hace uso de *Toasts* para mostrar información opcional.

## Interfaz oral

La interfaz oral se ha implementado con un bot de DialogFlow, que combina respuestas fijas con consultas a la base de datos [Wikidata](https://www.wikidata.org/) para la obtención de información relativa a las obras de arte.
También permite interactuar con otras partes de la aplicación como el visor 3D de forma oral, haciendo así más accesible la aplicación a personas con movilidad reducida.

### Sintetización y transcripción de voz

TODO: No sé qué va aquí

### Consultas posibles

Distinguimos dos tipos básicos de consultas que podemos hacer en DialogFlow: las consultas que hacen uso de Wikidata y las que no. Para cada consulta damos un ejemplo de posible frase a probar entre paréntesis y en cursiva para ver qué responde el bot.

Las consultas que no hacen uso de Wikidata son:

|Nombre | Descripción | Ejemplo|
|------------------|-------------|--------------------|
| `AyudaMuseo` | Proveer ayuda | *Ayúdame* |
| `Default Welcome Intent` | Responder a saludos | *Hola* |
| `DespedidaBot` | Responder a despedidas | *Adiós* |
| `HorarioMuseo` | Responder a preguntas sobre el horario del museo | *¿Cuándo cerráis?* |
| `MuestraModelo` | Mostrar un modelo 3D en el visor 3D | *Enséñame el David* |
| `NombreAgente` | Indicar cuál es su nombre | *¿Cómo te llamas?* |
| `NombreMuseo` | Indicar cuál es el nombre del museo | *¿Cómo se llama el museo?* |
| `PrecioEntradaMuseo` | Indicar el precio de la entrada del museo | *¿Qué precio tienen las entradas?* |
| `Preguntas personales` | Responder a preguntas personales | *Te quiero* |
| `SmallTalk` | Responder a preguntas sobre su estado de ánimo | *¿Cómo estás?* |

Estas consultas se implementan con un intent que proporciona una respuesta aleatoria a la pregunta o frase del usuario, exceptuando la consulta `MuestraModelo`, que utiliza una entidad que guarda los posibles nombres de las obras de arte.

Las consultas que hacen uso de Wikidata son:

|Nombre | Descripción | Ejemplo|
|------------------|-------------|--------------------|
| | | |
| | | |
| | | |
| | | |
| | | |
| | | |
| | | |
| | | |

Para Wikidata hacemos uso Pubnub, con el código que puede consultarse en el fichero adjunto `pubnub.js`.

El funcionamiento básico es el siguiente: cada intent de esta lista tiene una función asociada que realiza una consulta en Wikidata utilizando el lenguaje SPARQL. Si la consulta:

- Tiene éxito, se devuelve una respuesta que contiene la información solicitada, ajustando en la medida de lo posible los artículos femeninos y masculinos al género de las personas mencionadas.
- Falla una vez, se prueba de nuevo eliminando los artículos iniciales.
- Falla dos veces, se devuelve un mensaje de error genérico asociado al intent.

A continuación vemos un ejemplo de función que resuelve una consulta.




### Funcionamiento de DialogFlow y Wikidata
### Integración con DialogFlow

TODO: 

## Interfaz sensorial

### Uso de vector de rotación
### Uso de sensor de proximidad
### Uso de códigos QR
### Uso de acelerómetro

# Documentación técnica

*A continuación se han agrupado los atributos métodos y clases relacionadas con cada módulo. Habría que desarrollar cada uno.*

## Interfaz y recursos comunes

Atributos en MainActivity:

- LOGTAG
- ID_PROMPT_QUERY
- ID_PROMPT_INFO
- queryResultTextView

Métodos en MainActivity:

- onCreate
- setSpeakButton
- set3DButton
- setQRbutton
- setTextView
- onResume
- onPause

## Modelos 3D

Atributos en MainActivity:

- loadModelParameters

Métodos en MainActivity:

- loadModelFromAssets
- launchModelRendererActivity

Clases relacionadas:

- ModelActivity
- ModelSurfaceView
- ModelRenderer

## Texto y voz

Atributos en MainActivity:

- startListeningTime
- initialPromptDone


Métodos en MainActivity:

- showRecordPermissionExplanation
- onRecordAudioPermissionDenied
- startListening
- processAsrReadyForSpeech
- changeButtonAppearanceToListening
- changeButtonAppearanceToDefault
- processAsrError
- processAsrResults
- deviceConnectedToInternet
- onDestroy
- onTTSDone
- onTTSError
- onTTSStart
- onClick

Clases relacionadas:

- VoiceActivity

## Integración con DialogFlow

Atributos en MainActivity:

- aiDataService

Métodos en MainActivity:

- sendMsgToChatBot

Clases en MainActivity:

- MyAsyncTaskClass

Hablar de la clase MyAsyncTaskClass.

## Sensores de proximidad y agitación

Atributos en MainActivity:

- sManager
- proximitySensor
- accelSensor
- mAccel
- mAccelCurrent
- mAccelLast

Métodos en MainActivity:

- setUpSensors
- onAccuracyChanged
- onSensorChanged
- onShake

Clases relacionadas:

- SensorEventListener

## Obras aleatorias

Atributos en MainActivity:

- artworks

Métodos en MainActivity:

- randArtwork


# Código externo utilizado
