export default (request, response) => {
    const pubnub = require('pubnub');
    const kvstore = require('kvstore');
    const xhr = require('xhr');
    
    let headersObject = request.headers;
    let paramsObject = request.params;
    let methodString = request.method;
    let bodyString = request.body;

    console.log('request',request); // Log the request envelope passed
    // Query parameters passed are parsed into the request.params object for you
    // console.log(paramsObject.a) // This would print "5" for query string "a=5

    // Set the status code - by default it would return 200
    response.status = 200;
    // Set the headers the way you like
    response.headers['X-Custom-Header'] = 'CustomHeaderValue';
    
    // Mapa de las funciones que gestionan cada intent
    let intentMap = new Map();
    intentMap.set('ObraRelacionada', obraRelacionada);
    
    // Función que gestiona el intent ObraRelacionada
    function obraRelacionada(title){
        return title
    }
    
    // Función que determina si la página de Wikidata con un cierto título (QXXXXX) es una obra de arte
    function isArt(title){
        return xhr.fetch("https://www.wikidata.org/wiki/Special:EntityData/"+title+".json").then((x) => {
            // handle server response
            let properties = JSON.parse(x.body).entities[title].claims;
            // TODO: deducir a partir de properties que se trata de una obra de arte
            return true;
        }).catch((err) => {
            // handle request failure
            return response.send("Malformed JSON body.");
        });
    }
    
    // Esta función es la que debe seleccionar uno de los resultados de la búsqueda
    function selectResult(results){
        // Devuelve el primer título que corresponde a una obra de arte
        return results.map((r) => {return r.title}).find(isArt)
    }
    
    let any = JSON.parse(request.body).queryResult.parameters.any;
    let intent = JSON.parse(request.body).queryResult.intent.displayName;
    
    return xhr.fetch("https://www.wikidata.org/w/api.php?action=query&list=search&format=json&srsearch=" + any.split(" ").join("+")).then((x) => {
        // handle server response
        let result = selectResult(JSON.parse(x.body).query.search);
        return response.send({"fulfillmentText":intentMap.get(intent)(result)});
    }).catch((err) => {
        // handle request failure
        return response.send("Malformed JSON body.");
    });
};
