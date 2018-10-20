---
title: Sentencias SPARQL
---

En todas las sentencias `${any}` es la variable a buscar

# Creador de `${any}`

```sql
SELECT ?creatorLabel WHERE {
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
SELECT ?worksLabel WHERE {
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
SELECT ?countryLabel ?locLabel WHERE {
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

# Cuéntame algo sobre $obra

# Qué tamaño tiene $obra

# Cuándo se hizo $obra

# Quién hizo $obra

# Historia de $obra

# Género/movimiento de $obra

# Qué representa $obra
