import React from 'react';

export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.client = new WebSocket('ws://localhost:8989');
  }

  componentDidMount() {
    this.client.onopen = () => {
      this.client.send(JSON.stringify({
        name: 'gabriel',
        greeting: 'hello server',
      }));
    };
    this.client.onmessage = (message) => {
      console.log(message.data);
    };
    this.client.onclose = () => {
      this.client.close();
    };
  }

  render() {
    return (
      <div>
        <h1>Live Dashboard!</h1>
      </div>
    );
  }
}
