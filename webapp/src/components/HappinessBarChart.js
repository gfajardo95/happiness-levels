import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { IdentitySerializer, JsonSerializer, RSocketClient } from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';

export default class HappinessBarChart extends React.Component {

    state = {
        countrySentiments: [
            { country: 'USA', averageSentiment: 0.0 }
        ]
    }

    componentDidMount() {

        this.getCountrySentiments();
    }

    getCountrySentiments() {
        let client = new RSocketClient({
            serializers: {
                data: JsonSerializer,
                metadata: IdentitySerializer
            },
            setup: {
                // ms btw sending keepalive to server
                keepAlive: 90000,
                // ms timeout if no keepalive response
                lifetime: 180000,
                // format of `data`
                dataMimeType: 'application/json',
                // format of `metadata`
                metadataMimeType: 'message/x.rsocket.routing.v0',
            },
            transport: new RSocketWebSocketClient({
                url: 'ws://localhost:7000/'
            }),
        });

        // Open the connection
        client.connect().subscribe({
            onComplete: socket => {
                // socket provides the rsocket with methods to close the socket.
                socket.requestStream({
                    data: {
                        'projectId': 'happiness-level',
                        'subscriptionId': 'pull-country-sentiments'
                    },
                    metadata: String.fromCharCode('country-sentiments'.length) + 'country-sentiments',
                }).subscribe({
                    onComplete: () => console.log('complete'),
                    onError: error => {
                        console.log(error);
                    },
                    onNext: payload => {
                        console.log(payload.data);
                        this.setState(function (prevState) {
                            prevState.countrySentiments.map(data => {
                                if (data.country === payload.data.country) {
                                    if (data.averageSentiment === 0.0) {
                                        data.averageSentiment = payload.data.averageSentiment;
                                    } else {
                                        const cumulativeAverage = (data.averageSentiment + payload.data.averageSentiment) / 2;
                                        data.averageSentiment = cumulativeAverage;
                                    }
                                }

                                return data;
                            });

                            return {
                                countrySentiments: prevState.countrySentiments
                            };
                        });
                    },
                    onSubscribe: subscription => {
                        subscription.request(2147483647);
                    },
                });
            },
            onError: error => {
                console.log(error);
            },
            onSubscribe: cancel => {
                /* call cancel() to abort */
            }
        });
    }

    render() {
        return (  // TODO: set a max height and only re-render the bar, not the entire chart
            <BarChart width={600} height={300} data={this.state.countrySentiments.slice()}
                margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="country" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="averageSentiment" fill="#8884d8" />
            </BarChart>
        );
    }
}