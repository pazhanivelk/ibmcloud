'use strict';

var AssistantV1 = require('watson-developer-cloud/assistant/v1');

/**
 * Instantiate the Watson Assistant Service
 */


/**
 * Calls the assistant message api.
 * returns a promise
 */
var message = function(text, context) {
    console.log("inside message **************");
    var assistant = new AssistantV1({
        username: process.env.ASSISTANT_USERNAME || 'apikey',
        password: process.env.ASSISTANT_PASSWORD || 'sm2Trx14ybklxGiW-xxH86-EnPEUhGWJxqvjTRUpSjB4',
        version: '2018-02-16'
    });
    console.log("assistant name " + assistant.name);
  var payload = {
    workspace_id: process.env.WORKSPACE_ID || '1234',
    input: {
      text: text
    },
    context: context
  };
  return new Promise((resolve, reject) =>
  
    assistant.message(payload, function(err, data) {

        console.log("assistant response ...." +data);
      if (err) {
        reject(err);
      } else {
        resolve(data);
      }
    })
  );
};

async function asyncmessage(text, context) {
    let response = await message(text, context);
    return response;
  }

// This example makes two successive calls to assistant service.
// Note how the context is passed:
// In the first message the context is undefined. The service starts a new assistant.
// The context returned from the first call is passed in the second request - to continue the assistant.

var testMessage = function (){
message('first message', undefined)
  .then(response1 => {
    // APPLICATION-SPECIFIC CODE TO PROCESS THE DATA
    // FROM ASSISTANT SERVICE
    console.log(JSON.stringify(response1, null, 2), '\n--------');

    // invoke a second call to assistant
    return message('second message', response1.context);
  })
  .then(response2 => {
    console.log(JSON.stringify(response2, null, 2), '\n--------');
    console.log(
      'Note that the two reponses should have the same context.conversation_id'
    );
  })
  .catch(err => {
    // APPLICATION-SPECIFIC CODE TO PROCESS THE ERROR
    // FROM ASSISTANT SERVICE
    console.error(JSON.stringify(err, null, 2));
  });
}
  exports.message = message;
  exports.testMessage = testMessage;
  exports.asyncmessage = asyncmessage;
