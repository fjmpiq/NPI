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

La interacción con el sistema se hace principalmente mediante el uso del lenguaje natural por voz pulsando el Botón de habla.
El primer mensaje que el usuario escucha de la aplicación es un mensaje de bienvenida que presenta al agente conversacional.
Las sucesivas veces que el usuario presione el botón de habla el bot intentará dar una respuesta apropiada a las peticiones de información del usuario sobre las obras de arte.

La sintetización y transcripción de voz se hacen utilizando las librerías de Google disponibles en los dispositivos móviles (ver sección [Recursos externos utilizados] para más detalles).
En el caso de que estas librerías no estén disponibles o de que el usuario no de permisos para escuchar la aplicación indicará mediante el uso de la región de texto y mensajes *Toast* la funcionalidad faltante, y desactivará las partes de la aplicación que no estén disponibles.

### Integración con DialogFlow y consultas posibles

Una vez la aplicación ha conseguido la transcripción de la consulta del usuario esta se envía al sistema de DialogFlow que gestiona y devuelve una respuesta adecuada a la pregunta del usuario o una respuesta genérica en otro caso.

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
| `AutorDeObra` | Cuál es el autor de una obra | *¿Quién es el autor de El Grito?* |
| `FechaObra` | En qué fecha (aproximada) se hizo una obra | *¿Cuándo se hizo El Guernica?* |
| `GeneroObra` | A qué género o movimiento artístico pertenece una obra | *¿A qué movimiento pertenece La Última Cena?* |
| `HechosSobreObra` | Qué eventos notables han sucedido en relación a la obra | *Háblame sobre La Mona Lisa* |
| `LocalizacionObra` | Dónde está la obra | *¿Dónde está El Pensador?* |
| `MedidasObra` | Cuánto mide la obra | *¿Cuánto mide Saturno devorando a sus hijos?* |
| `ObraRelacionada` | Obras del mismo autor | *Dime obras relacionadas con La Joven de la Perla* |
| `ObrasDeAutor` | Obras de un autor | *Dime obras de Jeff Koons* |

Para Wikidata hacemos uso Pubnub, con el código que puede consultarse en el fichero adjunto `pubnub.js`.
El sistema intenta reconocer sobre qué obra se pregunta y busca la información asociada en Wikidata, dando una respuesta descriptiva en relación a lo que pregunta el usuario en caso de obtener la información o una respuesta genérica cuando esta información no se encuentra disponible.

El funcionamiento se describe en la sección [Uso de Wikidata].

## Interfaz sensorial

Para la práctica de la interfaz sensorial hemos implementado el uso de 4 sensores con los que se permite al usuario la interacción con funcionalidades extra del museo.
Las siguientes 4 secciones resumen la utilidad que tiene en la aplicación cada uno de los sensores.

### Uso de vector de rotación

El sensor de rotación (*rotation vector sensor*) permite al usuario interactuar con el visor 3D moviendo su dispositivo móvil para rotar una figura que representa una obra de arte.
Puede accederse a esta actividad de la aplicación de dos formas

1. Mediante el uso del botón de 3D, que proporciona un menú que da acceso a las figuras disponibles.
2. Mediante el uso de comandos de voz del tipo *Enséñame el David de Miguel Ángel* que muestran la figura asociada si está disponible.

Las figuras disponibles son:

1. El Pensador de Rodin,
2. La Venus de Willendorf,
3. El David de Miguel Ángel,
4. La Piedad del Vaticano y
5. La Fuente de Duchamp

Hemos escogido estas figuras como ejemplo de diversos tipos de arte y periodos históricos, aunque la aplicación sería fácilmente extensible con nuevas obras de arte.
Para detalles sobre la obtención de las figuras puede consultarse la sección [Recursos externos utilizados].

El sensor proporciona al usuario una forma sencilla y accesible de interactuar con los objetos y explorar las obras de arte, mejorando así su experiencia en el museo.

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

## Uso de Wikidata

El funcionamiento básico es el siguiente: cada intent de esta lista tiene una función asociada que realiza una consulta en Wikidata utilizando el lenguaje SPARQL. Si la consulta:

- Tiene éxito, se devuelve una respuesta que contiene la información solicitada, ajustando en la medida de lo posible los artículos femeninos y masculinos al género de las personas mencionadas.
- Falla una vez, se prueba de nuevo eliminando los artículos iniciales.
- Falla dos veces, se devuelve un mensaje de error genérico asociado al intent.

A continuación vemos un ejemplo de función que resuelve una consulta.

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


# Recursos externos utilizados

