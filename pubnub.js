export default (request, response) => {
    const pubnub = require('pubnub');
    const kvstore = require('kvstore');
    const xhr = require('xhr');
    
    let headersObject = request.headers;
    let paramsObject = request.params;
    let methodString = request.method;
    let bodyString = request.body;
    let any = JSON.parse(request.body).queryResult.parameters.any.replace("?", "");
    let intent = JSON.parse(request.body).queryResult.intent.displayName;
    let error = (err) => {return response.send({"fulfillmentText":"Uh oh, ha habido un error!"});};
    
    // Mapa de las funciones que gestionan cada intent
    let intentMap = new Map();
    intentMap.set('ObraRelacionada', obraRelacionada);
    intentMap.set('AutorDeObra', autorDeObra);
    intentMap.set('FechaObra', fechaObra);  
    intentMap.set('ObrasDeAutor', obrasDeAutor);
    intentMap.set('LocalizacionObra', localizacionObra);
    intentMap.set('MedidasObra', medidasObra);
    intentMap.set('GeneroObra', generoObra);
    intentMap.set('ObraRelacionada', obraRelacionada);
    intentMap.set('HechosSobreObra', hechosSobreObra);    
    
    // Función que gestiona el intent AutorDeObra
    function autorDeObra(){
        const endpointUrl = 'https://query.wikidata.org/sparql',
        sparqlQuery = `
        SELECT ?itemLabel ?creatorLabel ?creatorDescription WHERE {
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
        } LIMIT 10`,
      fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
      headers = { 'Accept': 'application/sparql-results+json' };

        return xhr.fetch( fullUrl, { headers } ).then( body => body.json() ).then( json => {
            const results = json.results.bindings;
            if (results.length === 0)
                return "Vaya, no conozco esa obra. :-("
            else
                return "El autor de " + results[0].itemLabel.value + " es " + results[0].creatorLabel.value + ", " + results[0].creatorDescription.value + ".";
        });
    }
    
    
    // Función que gestiona el intent FechaObra
    function fechaObra(){
        const endpointUrl = 'https://query.wikidata.org/sparql',
        sparqlQuery = `
        SELECT ?itemLabel ?inception WHERE {
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
            ?item wdt:P571 ?inception.
        } LIMIT 10`,
        fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
        headers = { 'Accept': 'application/sparql-results+json' };

        return xhr.fetch( fullUrl, { headers } ).then( body => body.json() ).then( json => {
            const results = json.results.bindings;
            if (results.length === 0)
                return "Vaya, no conozco esa obra. :-(";
            else
                return results[0].itemLabel.value + " data de " + results[0].inception.value.substring(0,4) + ".";
        });
    }
    
    // Función que gestiona el intent ObrasDeAutor
    function obrasDeAutor(){
        const endpointUrl = 'https://query.wikidata.org/sparql',
        sparqlQuery = `
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
        } LIMIT 10`,
        fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
        headers = { 'Accept': 'application/sparql-results+json' };

        return xhr.fetch( fullUrl, { headers } ).then( body => body.json() ).then( json => {
            const results = json.results.bindings;
            if (results.length === 0)
                return "Vaya, no conozco a ese artista. :-(";
            else if (results.length === 1)
                return "La obra más conocida de " + results[0].itemLabel.value + " es " + results[0].worksLabel.value;
            else if (results.length === 2)
                return "Algunas obras de " + results[0].itemLabel.value + " son " + results[0].worksLabel.value + " o " + results[1].worksLabel.value + ".";
            else
                return "Algunas obras de " + results[0].itemLabel.value + " son " + results[0].worksLabel.value + ", " + results[1].worksLabel.value + " o " + results[2].worksLabel.value + ".";
        });
    }
    
    // Función que gestiona el intent ObrasDeAutor
    function localizacionObra(){
        const endpointUrl = 'https://query.wikidata.org/sparql',
        sparqlQuery = `
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
        } LIMIT 10`,
        fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
        headers = { 'Accept': 'application/sparql-results+json' };

        return xhr.fetch( fullUrl, { headers } ).then( body => body.json() ).then( json => {
            const results = json.results.bindings;
            if (results.length === 0)
                return "Vaya, no esa obra. :-(";
            else
                return results[0].itemLabel.value + " se encuentra en " + results[0].locLabel.value + " (" + results[0].countryLabel.value + ").";
        });
    }
    
    // Función que gestiona el intent MedidasObra
    function medidasObra(){
        const endpointUrl = 'https://query.wikidata.org/sparql',
        sparqlQuery = `
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
        } LIMIT 10`,
        fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
        headers = { 'Accept': 'application/sparql-results+json' };

        return xhr.fetch( fullUrl, { headers } ).then( body => body.json() ).then( json => {
            const results = json.results.bindings;
            if (results.length === 0)
                return "Vaya, no conozco esa obra. :-(";
            else
                return results[0].itemLabel.value + " mide " + results[0].height.value + " centímetros de alto y " + results[0].width.value + " centímetros de ancho.";
        });
    }
    
    // Función que gestiona el intent GeneroObra
    function generoObra(){
        const endpointUrl = 'https://query.wikidata.org/sparql',
        sparqlQuery = `
        SELECT ?item ?itemLabel ?movementLabel ?genreLabel WHERE {
            SERVICE wikibase:mwapi {
                bd:serviceParam wikibase:api "EntitySearch" .
                bd:serviceParam wikibase:endpoint "www.wikidata.org" .
                bd:serviceParam mwapi:search "${any}" .
                bd:serviceParam mwapi:language "es" .
                ?item wikibase:apiOutputItem mwapi:item .
                ?num wikibase:apiOrdinal true .
            }
            SERVICE wikibase:label {
                bd:serviceParam wikibase:language "es" .
            }
            ?item (wdt:P279|wdt:P31) ?type.
            VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
            OPTIONAL {
                ?item wdt:P135 ?movement;
                wdt:P136 ?genre.}
        } ORDER BY ASC(?num) LIMIT 10`,
        fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
        headers = { 'Accept': 'application/sparql-results+json' };

        return xhr.fetch( fullUrl, { headers } ).then( body => body.json() ).then( json => {
            const results = json.results.bindings;
            if (results.length === 0)
                return "Vaya, no conozco esa obra. :-(";
            else
                if (results[0].hasOwnProperty("movementLabel"))
                    if (results[0].hasOwnProperty("genreLabel"))
                        return results[0].itemLabel.value + " pertenece al movimiento " + results[0].movementLabel.value + " y al género " + results[0].genreLabel.value + ".";
                    else
                        return results[0].itemLabel.value + " pertenece al movimiento " + results[0].movementLabel.value + ".";
                else
                    if (results[0].hasOwnProperty("genreLabel"))
                        return results[0].itemLabel.value + " pertenece al género " + results[0].genre.value + ".";
                    else
                        return "No conozco el género ni el movimiento de " + results[0].itemLabel.value + ".";
        });
    }
    
    // Función que gestiona el intent ObraRelacionada
    function obraRelacionada(id){
        const endpointUrl = 'https://query.wikidata.org/sparql',
        sparqlQuery = `
        SELECT ?itemLabel ?creatorLabel ?workLabel WHERE {
            SERVICE wikibase:mwapi {
                bd:serviceParam wikibase:api "EntitySearch" .
                bd:serviceParam wikibase:endpoint "www.wikidata.org" .
                bd:serviceParam mwapi:search "${any}" .
                bd:serviceParam mwapi:language "es" .
                bd:serviceParam wikibase:limit 10 .
                ?item wikibase:apiOutputItem mwapi:item .
                ?num wikibase:apiOrdinal true .
            }
            SERVICE wikibase:label {
                bd:serviceParam wikibase:language "es" .
            }
            ?item (wdt:P279|wdt:P31) ?type.
            VALUES ?type {wd:Q3305213 wd:Q18573970 wd:Q219423 wd:Q179700}
            ?item wdt:P170 ?creator.
            ?work wdt:P170 ?creator.
        } ORDER BY ASC(?num) LIMIT 10`,
        fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
        headers = { 'Accept': 'application/sparql-results+json' };

        return xhr.fetch( fullUrl, { headers } ).then( body => body.json() ).then( json => {
            const results = json.results.bindings.filter(r => (r.itemLabel.value !== r.workLabel.value) && (!/Q[0-9]+/.test(r.workLabel.value)));
            if (results.length === 0)
                return "Vaya, no conozco a ese artista. :-(";
            else if (results.length === 1)
                return "Una obra relacionada con " + results[0].itemLabel.value + " es " + results[0].workLabel.value + ".";
            else if (results.length === 2)
                return "Algunas obras relacionadas con " + results[0].itemLabel.value + " son " + results[0].workLabel.value + " o " + results[1].workLabel.value;
            else
                return "Algunas obras relacionadas con " + results[0].itemLabel.value + " son " + results[0].workLabel.value + ", " + results[1].workLabel.value + " o " + results[2].workLabel.value + ".";
        });
    }
    
    // Función que gestiona el intent HechosSobreObra
    function hechosSobreObra(id){
        const endpointUrl = 'https://query.wikidata.org/sparql',
        sparqlQuery = `
        SELECT ?itemLabel ?eventLabel ?date WHERE {
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
            ?item p:P793 ?eventOfItem .
            ?eventOfItem ps:P793 ?event .
            ?eventOfItem pq:P585 ?date .
        } LIMIT 10`,
        fullUrl = endpointUrl + '?query=' + encodeURIComponent( sparqlQuery ),
        headers = { 'Accept': 'application/sparql-results+json' };
        return xhr.fetch( fullUrl, { headers } ).then( body => body.json() ).then( json => {
            const results = json.results.bindings;
            if (results.length === 0)
                return "Vaya, no conozco a ese artista. :-(";
            else {
                let events = results.map(e => "En " + e.date.value.substring(0,4) + " se produjo " + e.eventLabel.value + ".");
                return "Conozco los siguientes eventos sobre " + results[0].itemLabel.value + ":\n" + events.join(" ");
            }
        });
    }
