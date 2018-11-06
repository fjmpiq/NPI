---
title: Memoria técnica de la Práctica 1 de Nuevos Paradigmas de Interacción
author: |
    Pablo Baeyens Fernández \
    Francisco Javier Morales Piqueras \
    Jesús Sánchez de Lechina Tejada
---

# Funcionamiento de la aplicación

*Aquí iría un resumen de cada una de las funciones que tiene nuestra aplicación y cómo usarlas. Las agruparíamos en 6 módulos (o como queráis llamarlos; se admite otra organización y se agradecen propuestas de mejora para los nombres de cada una): interfaz y recursos comunes, modelos 3D, texto y voz, integración con DialogFlow, sensores de proximidad y agitación y obras aleatorias.*

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
