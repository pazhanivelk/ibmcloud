'use strict';

function watsoninputaction(params) {
  const name = params.name || 'World';
  return { payload: `Hello, ${name}!` };
}

exports.watsoninputaction = watsoninputaction;
