const express = require('express');
const os = require('os');
const PubSub = require('@google-cloud/pubsub');
const WebSocket = require('ws');

const app = express();

const wss = new WebSocket.Server({ port: 8989 });

wss.on('connection', (ws) => {
  ws.isAlive = true; /* detect broken connection */
  ws.on('pong', () => {
    ws.isAlive = true;
  });
  ws.on('message', (message) => {
    console.log('received %s', message);
    ws.send('something');
  });
});

setInterval(() => {
  wss.clients.forEach((ws) => {
    if (ws.isAlive === false) ws.terminate();
    ws.isAlive = false;
    ws.ping(null, false);
  });
}, 10000);

const pubsub = new PubSub();
const subscriptionName = 'country-sentiments-sub';
const timeout = 60;
const subscription = pubsub.subscription(subscriptionName);

let messageCount = 0;
const messageHandler = (message) => {
  console.log(`received message ${message.id}`);
  console.log(`\tData: ${message.data}`);
  console.log(`\tAttributes: ${message.attributes}`);
  messageCount += 1;

  message.ack();
};

subscription.on('message', messageHandler);
setTimeout(() => {
  subscription.removeListener('message', messageHandler);
  console.log(`${messageCount} message(s) received.`);
}, timeout * 100000);

app.use(express.static('dist'));
app.get('/api/getUsername', (req, res) => res.send({ username: os.userInfo().username }));
app.listen(8080, () => console.log('Listening on port 8080!'));
