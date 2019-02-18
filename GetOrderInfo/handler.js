'use strict';

var Cloudant = require('@cloudant/cloudant');


function hello(orderId) {
console.log("*********************************cloud functions start here ********************")
var me = '87effa49-9c84-464e-a76c-5f639eba523b-bluemix'; 
var password = "939611dc7dcb91b9e4270bd4c3c1315375a1dfbc";
  var cloudant = Cloudant({ account:me, key:"omysendegiefullestortedr", password:password });
  cloudant.set_cors({ enable_cors: true, allow_credentials: true, origins: [ '*' ]}).then((data) => {
    // success - response is in 'data'.
  }).catch((err) => {
    // failure - error information is in 'err'.
  });
  var db = cloudant.db.use('inventorydb')
  console.log("db instance "+ db);
  
var cities = [
  { "_id":"Boston",
    "type":"Feature",
    "geometry": {
      "type":"Point","coordinates": [-71.063611, 42.358056]
    }
  },
  { "_id":"Houston",
    "type":"Feature",
    "geometry": {
      "type":"Point","coordinates": [-95.383056, 29.762778]
    }
  },
  { "_id":"Ruston",
    "type":"Feature",
    "geometry": {
      "type":"Point","coordinates": [-92.640556, 32.529722]
    }
  }
];

db.bulk({ docs: cities }, function(err) {
  if (err) {
    throw err;
  }

  console.log('Inserted all cities');
});
  
 
  return { payload: "order_status"  };
}

exports.hello = hello;
