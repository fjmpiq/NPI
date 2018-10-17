export default (request, response) => {
    const pubnub = require('pubnub');
    const kvstore = require('kvstore');
    const xhr = require('xhr');
    
    let headersObject = request.headers;
    let paramsObject = request.params;
    let methodString = request.method;
    let bodyString = request.body;
    let any = JSON.parse(request.body).queryResult.parameters.any;
    let intent = JSON.parse(request.body).queryResult.intent.displayName;
    let error = (err) => {return response.send({"fulfillmentText":"Uh oh, ha habido un error!"});};

    // Dada una entidad (QXXXX) y una propiedad (PXXXX), devuelve el label de la propiedad para la entidad
    // o la lista de labels en el caso de que sea una lista
    function getPropiedad(entidad, propiedad){
	// TODO: Implementar
	return false;
    }

    // Determina si QXXXXX es una obra de arte
    function isArt(title){
        return xhr.fetch("https://www.wikidata.org/wiki/Special:EntityData/"+title+".json").then((x) => {
            let properties = JSON.parse(x.body).entities[title].claims;
            // TODO: deducir a partir de properties que se trata de una obra de arte
            return true;
        });
    }

    // Selecciona resultado de la búsqueda
    function selectResult(results){
	return results.map((r) => {return r.title}).find(isArt); // primero que es obra de arte
    }

    // Busca entidad dado nombre
    function buscaEntidad(nombre){
	    return xhr.fetch("https://www.wikidata.org/w/api.php?action=wbsearchentities&search="+ nombre + "&language=es&limit=5&format=json")
	    .then((x) => {
	        let result = selectResult(JSON.parse(x.body).search);
	        return result;
	    });}



    // Mapa de las funciones que gestionan cada intent
    let intentMap = new Map();
    intentMap.set('ObraRelacionada', obraRelacionada);

    // Función que gestiona el intent ObraRelacionada
    function obraRelacionada(title){
        return title;
    }

    return buscaEntidad(any).then((result) => {
        return response.send({"fulfillmentText":intentMap.get(intent)(result)});
    }).catch(error);
};
