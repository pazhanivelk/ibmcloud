'use strict';

var Cloudant = require('@cloudant/cloudant');


function getOrderData(orderId) {

  
  const name = params.name || 'World';
  return { payload: `Hello, ${name}!` };
}

exports.getOrderData = getOrderData;
