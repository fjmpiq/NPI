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

*Musemium*

# Funcionamiento de la aplicación

## Interfaz básica y recursos comunes

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

### Texto y voz

TODO: No sé qué va aquí

### Integración con DialogFlow

### Funcionamiento de DialogFlow

Distinguimos dos tipos básicos de consultas que podemos hacer en DialogFlow: las consultas que hacen uso de Wikidata y las que no. Para cada consulta damos un ejemplo de posible frase a probar entre paréntesis y en cursiva para ver qué responde el bot.

Las consultas que no hacen uso de Wikidata son:

- Pedir ayuda sobre el funcionamiento de la aplicación (*Ayúdame*). La aplicación sugiere posibles preguntas y ayuda.
  Implementado en el intent `AyudaMuseo`.
- Responder a saludos (*Hola*). Implementado en `Default Welcome Intent`.
- Responder a despedidas (*Adiós*). Implementado en `DespedidaBot`.
- Responder a preguntas sobre el horario del museo 

## Interfaz sensorial

### Uso de vector de rotación
### Uso de sensor de proximidad
### Uso de códigos QR
### Uso de acelerómetro

# Módulos

*A continuación se han agrupado los atributos métodos y clases relacionadas con cada módulo. Habría que desarrollar cada uno.*

## Interfaz y recursos comunes:

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
