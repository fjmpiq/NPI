---
title: Sentencias SPARQL
---

En todas las sentencias `${any}` es la variable a buscar

# Creador de `${any}`

```sql
SELECT ?itemLabel ?creatorLabel WHERE {
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
  ?item (wdt:P279|wdt:P31) ?type.
  VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
  ?item wdt:P170 ?creator.
} LIMIT 10
```


# Obras de `${any}`

```sql
SELECT ?itemLabel ?worksLabel WHERE {
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
  ?item (wdt:P279|wdt:P31) wd:Q5.
  ?item wdt:P800 ?works.
} LIMIT 10
```

# Dónde está $obra

```sql
SELECT ?itemLabel ?countryLabel ?locLabel WHERE {
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
  ?item (wdt:P279|wdt:P31) ?type.
  VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
  ?item wdt:P17 ?country.
  ?item wdt:P276 ?loc.
} LIMIT 10
```

# Qué tamaño tiene $obra

```sql
SELECT ?itemLabel ?height ?width WHERE {
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
  ?item (wdt:P279|wdt:P31) ?type.
  VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
  ?item wdt:P2048 ?height.
  ?item wdt:P2049 ?width.
} LIMIT 10
```

# Cuándo se hizo $obra

```sql
SELECT ?itemLabel ?inception WHERE {
  SERVICE wikibase:mwapi {
      bd:serviceParam wikibase:api "EntitySearch" .
      bd:serviceParam wikibase:endpoint "www.wikidata.org" .
      bd:serviceParam mwapi:search "Mona Lisa" .
      bd:serviceParam mwapi:language "es" .
      ?item wikibase:apiOutputItem mwapi:item .
  }
  SERVICE wikibase:label {
    bd:serviceParam wikibase:language "es" .
   }
  ?item (wdt:P279|wdt:P31) ?type.
  VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
  ?item wdt:P571 ?inception.
} LIMIT 10
```



# Género/movimiento de $obra

```sql
SELECT ?itemLabel ?movementLabel ?genreLabel WHERE {
  SERVICE wikibase:mwapi {
      bd:serviceParam wikibase:api "EntitySearch" .
      bd:serviceParam wikibase:endpoint "www.wikidata.org" .
      bd:serviceParam mwapi:search "La balsa de medusa" .
      bd:serviceParam mwapi:language "es" .
      ?item wikibase:apiOutputItem mwapi:item .
  }
  SERVICE wikibase:label {
    bd:serviceParam wikibase:language "es" .
   }
  ?item (wdt:P279|wdt:P31) ?type.
  VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
  OPTIONAL {
    ?item wdt:P135 ?movement;
          wdt:P136 ?genre.}
} ORDER BY DESC(?movementLabel) LIMIT 10
```

# Historia de $obra/Hechos sobre obra

Esto devuelve una lista de los eventos significativos pero no tengo muy claro cómo mostrar también propiedades de esos eventos como la fecha en la que ocurrieron.
Esto debería poder hacerse con [qualifiers](https://www.wikidata.org/wiki/Wikidata:SPARQL_tutorial#Qualifiers) pero no sé muy bien cómo funcionan

```sql
SELECT ?itemLabel ?eventLabel WHERE {
  SERVICE wikibase:mwapi {
      bd:serviceParam wikibase:api "EntitySearch" .
      bd:serviceParam wikibase:endpoint "www.wikidata.org" .
      bd:serviceParam mwapi:search "Mona Lisa" .
      bd:serviceParam mwapi:language "es" .
      ?item wikibase:apiOutputItem mwapi:item .
  }
  SERVICE wikibase:label {
    bd:serviceParam wikibase:language "es" .
   }
  ?item (wdt:P279|wdt:P31) ?type.
  VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
  ?item wdt:P793 ?event.
  # ?event pq:P585 ?date.
} LIMIT 10
```

# Obras relacionadas con

Posible idea: obras del mismo autor/género/movimiento que la obra mencionada.
