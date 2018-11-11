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

El visor 3D utilizado es además compatible con el uso de las Google Cardboard para mejorar la experiencia de usuario si se dispone de unas.

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

El sensor de proximidad detiene la narración del bot en el caso de que se detecte un objeto próximo a la pantalla, de forma similar al funcionamiento de aplicaciones como WhatsApp o Telegram. De esta forma podemos detener al bot dándole la vuelta al móvil de tal manera que la pantalla repose sobre una superficie.

La aplicación muestra un *Toast* informativo que explica este funcionamiento para que el usuario comprenda el funcionamiento.

### Uso de códigos QR

La aplicación incluye el uso de sensores QR que permiten acceder a información de una obra mediante el uso de la cámara. De esta forma es sencillo interactuar con posibles recomendaciones de obras de arte del museo, que sólo tiene que añadir un código QR de una obra de arte en una pared.

Para acceder a esta funcionalidad es necesario pulsar en el botón de QR. Este nos lleva a una actividad que detecta códigos QR y nos da información sobre la obra asociada. La interfaz también permite activar o desactivar la linterna para entornos de visibilidad limitada.

### Uso de acelerómetro

Si el usuario no quiere preguntar sobre una obra concreta puede interactuar con la aplicación utilizando el sensor de acelerómetro para obtener información sobre una obra aleatoria como su nombre o localización. Simplemente necesita agitar su dispositivo y recibirá información sobre una obra aleatoria.

Además, utilizando el botón de opciones puede ajustar el umbral de este sensor de tal manera que no tenga que agitar el dispositivo con mucha fuerza, ajustándose así a los distintos dispositivos y ofreciendo una opción accesible para interactuar con la aplicación si el usuario tiene movilidad reducida.

\newpage

# Documentación técnica

A continuación describimos de forma breve la implementación de la funcionalidad de la aplicación.
Cada atributo y método que hemos implementado tiene además su documentación en forma de Javadoc dentro del propio código.

## Interfaz y recursos comunes

`MainActivity` es la actividad principal de la aplicación.
Es una subclase de `VoiceActivity` (clase que gestiona la interacción oral) e implementa la interfaz `SensorEventListener` para poder gestionar los sensores. Está construida a partir del recurso externo `zoraidacallejas/Chatbot`.

Atributos generales en `MainActivity`:

- `LOGTAG`: Etiqueta de la actividad para mensajes de depuración
- `queryResultTextView`: Vista principal de texto.

Métodos en `MainActivity`:

- `onCreate`: Inicializa la interfaz visual, de voz y los sensores
- `setSpeakButton`: Configura el botón de habla
- `setExtraButtons`: Configura los botones extra (botón QR, botón 3D y botón de opciones)
- `setTextView`: Establece el texto inicial de la vista principal de texto
- `onResume`: Registra los sensores
- `onPause`: Elimina los sensores
- `onActivityResult`: Gestiona la información devuelta por las actividades de opciones y escaneo QR

## Interfaz de voz
### Texto y voz

Atributos en `MainActivity`:

- `ID_PROMPT_QUERY` e `ID_PROMPT_INFO`: Indican si el mensaje es una respuesta o información (por ejemplo sobre un error)
- `startListeningTime`: Indica el tiempo en el que se empezó a escuchar por última vez para procesado de errores.
- `initialPromptDone`: Indica si el mensaje inicial debe ser reproducido (`false`) o no (`true`).

Métodos en `MainActivity`:

- `showRecordPermissionExplanation`: indica que necesita el micrófono
- `onRecordAudioPermissionDenied`: Indica que no puede funcionar sin micrófono
- `startListening`: comienza a escuchar al usuario
- `processAsrReadyForSpeech` y `changeButtonAppearanceToListening`: Cambia la apariencia del botón para indicar que está escuchando
- `changeButtonAppearanceToDefault`: Cambia apariencia al botón por defecto
- `processAsrError`: Procesa error al escuchar
- `processAsrResults`: Toma el mejor resultado escuchado y lo manda al chatbot
- `deviceConnectedToInternet`: Comprueba si el dispositivo está conectado a Internet
- `onDestroy`: Desactiva el motor de voz
- `onTTSDone`: Registra el sensor de aceleración
- `onTTSError`: Indica por log que ha habido un error.
- `onTTSStart`: Indica por log que empieza a hablar
- `onClick`: Gestiona el comportamiento del botón de habla (mensaje inicial o escucha)

Clases relacionadas:

- `VoiceActivity`: Clase abstracta que extiende `MainActivity` para implementar la interacción oral

### Integración con DialogFlow

Atributos en `MainActivity`:

- `aiDataService`: Conexión al servicio de DialogFlow.

Métodos en `MainActivity`:

- `sendMsgToChatBot`: envía mensaje escuchado al bot.

Clases en `MainActivity`:

- `MyAsyncTaskClass`: Clase que se encarga de enviar la petición a DialogFlow de forma asíncrona.

TODO: Hablar de la clase MyAsyncTaskClass.

### Uso de Wikidata (Pubnub)

Para la resolución de algunas de las consultas hacemos uso de Wikidata, una base de datos apoyada en Wikipedia que nos permite obtener información sobre las obras de arte del museo.
El código está alojado en Pubnub y lo adjuntamos en el fichero `pubnub.js`.

Wikidata es una base de datos basada en tripletas Propiedad - Relación - Objeto.
Para obtener información sobre las obras de arte hacemos uso de las propiedades adecuadas.
A continuación hay una lista de propiedades de ejemplo que utilizamos en las consultas:

| Nombre             | Descripción                     | Id    | Ejemplos                                     |
|--------------------+---------------------------------+-------+----------------------------------------------|
| instance of        | Qué tipo de cosa es             | P31   | painting, sculpture, human                   |
| image              | Imagen de la obra               | P18   | Archivo .jpg                                 |
| inception          | Fecha de creación               | P571  | 1893, 1500s. Puede ser un periodo            |
| movement           | Movimiento artístico            | P135  | Alto Renacimiento, Cubismo                   |
| creator            | Creador                         | P175  | Leonardo Da Vinci                            |
| described at URL   | descrita en la web              | P973  | URL donde se describe                        |
| country of origin  | País de origen                  | P495  | Italia                                       |
| significant event  | Lista de eventos significativos | P793  | Descubrimiento, robo (con fecha)             |
| notable work       | Obras notables                  | P800  | Mona Lisa                                    |

Cada propiedad tiene un identificador que en la tabla aparece en la columa "Id".
Para obtener información de la base de datos hacemos consultas en el lenguaje [SPARQL](https://www.w3.org/TR/rdf-sparql-query/), un lenguaje para bases de datos de tripletas.

El funcionamiento básico de la interacción con Wikidata por parte de DialogFlow es el siguiente: cada intent que usa Wikidata tiene una función asociada que realiza una consulta utilizando el lenguaje SPARQL. Si la consulta:

- tiene éxito, se devuelve una respuesta que contiene la información solicitada, ajustando en la medida de lo posible los artículos femeninos y masculinos al género de las personas mencionadas.
- falla una vez, se prueba de nuevo eliminando los artículos iniciales.
- falla dos veces, se devuelve un mensaje de error genérico asociado al intent.

A continuación vemos un ejemplo de función que resuelve una consulta de `AutorDeObra`
(*¿Quién es el autor de La Mona Lisa?*).

En primer lugar discutimos la consulta de SPARQL que utilizamos para este intent (ligeramente simplificada para facilitar la explicación).
SPARQL tiene una sintaxis declarativa similar a la de SQL y permite el uso de servicios.
Las consultas pueden probarse en `query.wikidata.org`.
En concreto a partir de [este enlace](https://query.wikidata.org/#SELECT%20%3FitemLabel%20%3FcreatorLabel%20%3FcreatorDescription%20%3FgenderLabel%20WHERE%20%7B%0A%20%20SERVICE%20wikibase%3Amwapi%20%7B%0A%20%20%20%20bd%3AserviceParam%20wikibase%3Aapi%20%22EntitySearch%22%20.%0A%20%20%20%20bd%3AserviceParam%20wikibase%3Aendpoint%20%22www.wikidata.org%22%20.%0A%20%20%20%20bd%3AserviceParam%20mwapi%3Asearch%20%22Mona%20Lisa%22%20.%0A%20%20%20%20bd%3AserviceParam%20mwapi%3Alanguage%20%22es%22%20.%0A%20%20%20%20%3Fitem%20wikibase%3AapiOutputItem%20mwapi%3Aitem%20.%0A%20%20%7D%0A%20%20%0A%20%20SERVICE%20wikibase%3Alabel%20%7B%0A%20%20%20%20bd%3AserviceParam%20wikibase%3Alanguage%20%22es%2Cen%22%20.%0A%20%20%7D%0A%20%20%0A%20%20%3Fitem%20%28wdt%3AP279%7Cwdt%3AP31%29%20%3Ftype.%0A%20%20VALUES%20%3Ftype%20%7Bwd%3AQ3305213%20wd%3AQ18573970%20wd%3AQ219423%20wd%3AQ179700%7D%0A%20%20%3Fitem%20wdt%3AP170%20%3Fcreator.%0A%20%20%3Fcreator%20wdt%3AP21%20%3Fgender.%0A%7D%20LIMIT%2010) puede comprobarse la respuesta que da Wikidata a nuestra consulta.

El código completo de la consulta es:
```sql
SELECT ?itemLabel ?creatorLabel ?creatorDescription ?genderLabel WHERE {
  SERVICE wikibase:mwapi {
    bd:serviceParam wikibase:api "EntitySearch" .
    bd:serviceParam wikibase:endpoint "www.wikidata.org" .
    bd:serviceParam mwapi:search "${any}" .
    bd:serviceParam mwapi:language "es" .
    ?item wikibase:apiOutputItem mwapi:item .
  }

  SERVICE wikibase:label {
    bd:serviceParam wikibase:language "es" .
  }

  ?item wdt:P31 ?type.
  VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
  ?item wdt:P170 ?creator.
  ?creator wdt:P21 ?gender.
} LIMIT 10
```

Explicamos a continuación las distintas partes de la consulta.
La primera línea, `SELECT` nos indica con qué variables queremos quedarnos. En este caso son:

`?itemLabel`
: Nombre de la obra. Utilizamos este nombre para corregir posibles erratas en la entrada de DialogFlow

`?creatorLabel`
: Nombre de la persona que creó la obra

`?creatorDescription`
: Descripción de la persona que creó la obra

`?genderLabel`
: Género de la persona que creo la obra

A continuación, los bloques `SERVICE` indican el uso de servicios externos.
Para todas las consultas hacemos uso de dos servicios:

1. El servicio de búsqueda de MediaWiki (`SERVICE wikibase:mwapi`), que nos permite buscar a partir de la entrada del usuario entre las entidades de Wikidata. Lo hace a partir del valor `${any}` obtenido por DialogFlow como una entidad `any` en la consulta y
2. El servicio de etiquetas de Wikidata (`SERVICE wikibase:label`), que nos permite darle nombre y descripción a las entidades encontradas.
   Usando este servicio, si tenemos una entidad `?A`, las variables `?ALabel` y `?ADescription` contienen su
   nombre y descripción respectivamente.

Por último tenemos el cuerpo de la función.
Utilizamos un bloque de tipo `VALUES` y la sintaxis básica de SPARQL, que nos permite razonar sobre las tripletas.
Una sentencia básica es de la forma `A prop B.` que se lee *"`A` tiene valor `B` en la propiedad `prop`"*.

Las sentencias del cuerpo de la request tienen el siguiente significado:

1. `?item wdt:P31 ?type.` `?item` (el resultado de la búsqueda con el nombre que ha dado el usuario) es una instancia de (`wdt:P31`) `?type`.
2. `VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}`. `?type` es un valor de la siguiente lista: *pintura* (`Q3305213`), *grupo de pinturas* (`Q18573970`), *mural* (`Q219423`) o *estatua* (`Q179700`). De esta forma restringimos la búsqueda a obras de arte.
3. `?item wdt:P170 ?creator.`. `?item` tiene como creador (`wdt:P170`) a `?creator`.
4. `?creator wdt:P21 ?gender.` `?creator` tiene como género (`wdt:P21`) a `?gender`.

De esta forma la consulta nos devuelve obras de arte (esto es, pinturas, grupos de pinturas, murales o estatuas) que coincidan con la consulta del usuario y nos proporciona además su creador y una descripción del mismo.
La última línea limita el número de resultados a 10.

A continuación explicamos brevemente el código en Javascript implementado en Pubnub.
La variable `sparqlQuery` contiene la consulta a la base de datos de Wikidata que acabamos de mostrar.

```javascript
function autorDeObra(){
  const endpointUrl = 'https://query.wikidata.org/sparql',
  sparqlQuery = `...`,
  fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
  headers = { 'Accept': 'application/sparql-results+json' };

)  return xhr.fetch(fullUrl, { headers } )
  .then( body => body.json() )
  .then( json => {
    const results = json.results.bindings;
    if (results.length === 0)
      return tryAgain();
    else
      return autorAutora(results[0].genderLabel.value) +
      " de " + results[0].itemLabel.value +
      " es " + results[0].creatorLabel.value +
      ", " + results[0].creatorDescription.value + ".";
  });
}
```

La función hace una petición a partir del objeto `xhr` de Pubnub (con una API idéntica a [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API)) con la consulta adecuada. A continuación procesa la respuesta como un objeto JSON. Si no consigue resultados, prueba de nuevo (`tryAgain`) con nombres similares. Si no los consiguiera de nuevo devolvería una respuesta genérica a ese intent.

Si los consigue toma el nombre del item `itemLabel`, el nombre del autor `creatorLabel`, la descripción del autor `creatorDescription` y el género del autor `genderLabel` para utilizar los pronombres adecuados.
Esta respuesta se envía a DialogFlow que la envía de vuelta a la aplicación.

La implementación de funciones del resto de intents es muy similar, cambiando sólo la información a la que accedemos.

## Interfaz de sensores
### Modelos 3D

Métodos en `MainActivity`:

- `loadModelFromAssets`: Crea un menú de selección para elegir el modelo que queremos visualizar.
- `launchModelRendererActivity`: dado un archivo de un modelo 3D llama a la activity que se encarga de visualizarlo (`ModelActivity`).

`ModelActivity` es un recurso externo al que se ha modificado la interfaz y limitado algunos aspectos de su funcionamiento para adaptarlo al uso de nuestra aplicación. Esta clase a su vez hace uso de otras como `ArrayModel`, `Floor`, `IndexedModel`, `Light`, `Model`, `ModelRenderer,` `ModelSurfaceView`, `ModelViewerApplication`, `ModelGvrActivity`, `ObjModel` o `Util`.

### Sensor QR

Métodos en `MainActivity`:

- `qrScan`: Inicializa actividad de escaneo.
- `checkScanPermissions`: Comprueba si hay permisos para la cámara.
- `onRequestPermissionResult` y `onCameraPermissionDenied`: Explica al usuario la necesidad de dar permiso para la cámara
- ver también [Interfaz y recursos comunes] para funciones que comparte con otras partes de la interfaz

También se hace uso de `DecoderActivity`, que es un recurso externo.
En esta actividad hemos traducido la interfaz y eliminado algunas características innecesarias para nuestra aplicación.

### Sensores de proximidad y agitación

Atributos en `MainActivity`:

- `sManager`: Gestor de sensores
- `proximitySensor`: Sensor de proximidad
- `accelSensor`: Sensor de aceleración
- `mAccel`: Aceleración actual ajustada por la gravedad
- `mAccelCurrent`: Aceleración actual sin ajustar
- `mAccelLast`: Última aceleración sin ajustar
- `artworks`: objeto JSON que guarda las obras para sugerir obras aleatorias

Métodos en `MainActivity`:

- `setUpSensors`: Configura sensores
- `onAccuracyChanged`: Método vacío necesario para implementar la interfaz de escucha de sensores
- `onSensorChanged`: Gestiona cambios de sensores de aceleración y proximidad.
- `onShake`: Reproduce mensaje de obra aleatoria al agitar.
- `randArtwork`: Genera información sobre obra aleatoria.


# Recursos externos utilizados

Esta sección describe los recursos externos utilizados.
Cuando el código utilizado para la implementación de una actividad es parcial o totalmente externo se indica así en el código fuente.

## Interfaz de voz e interfaz básica

El principal recurso externo utilizado ha sido el proyecto de Zoraida, que hemos usado como inicio:

[`zoraidacallejas/Chatbot`](https://github.com/zoraidacallejas/Chatbot)
: Para la implementación inicial de la aplicación hemos partido del proyecto de Zoraida.
  Hemos modificado la interfaz para adaptarla a nuestra aplicación y añadido el código necesario para las nuevas
  funcionalidades.

Además como punto inicial para la construcción de las consultas a Wikidata hemos utilizado los ejemplos disponibles en la [página de ejemplos de Wikidata](https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries/examples).

## Sensores

Para la lectura de códigos QR hemos utilizado el código siguiente proyecto:

[`dlazaro66/QRCodeReaderView`](https://github.com/dlazaro66/QRCodeReaderView)
: Para la implementación del visor QR hemos partido del visor de códigos QR de este proyecto de Github.
  Hemos modificado la interfaz para adaptarla a nuestra aplicación. El resto de la interacción se realiza desde
  `MainActivity` en los métodos `qrScan` y `onActivityResult`.

Para el visionado de modelos 3D hemos utilizado parcialmente el código del siguiente proyecto:

[`dbrant/ModelViewer3D`](https://github.com/dbrant/ModelViewer3D)
: Para la implementación del visor 3D hemos partido de esta aplicación, que hemos modificado para incluir la
  interacción mediante el sensor de vector de rotación.

Además, las figuras 3D de obras de arte se han obtenido de [Scan The World](https://www.myminifactory.com/es/scantheworld/). Las hemos tratado utilizando Blender para reducir el número de vértices.

## Otros recursos

Hemos hecho uso de la documentación de Android y de la plataforma StackOverflow para aprender cómo utilizar las funcionalidades básicas en relación a Android, SPARQL y Pubnub.
