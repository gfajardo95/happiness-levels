import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { IdentitySerializer, JsonSerializer, RSocketClient } from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';

export default class HappinessBarChart extends React.Component {

    state = {
        countrySentiments: []
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
                keepAlive: 180000,
                lifetime: 180000,
                dataMimeType: 'application/json',
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
                            let foundCountry = false;
                            prevState.countrySentiments.map(data => {
                                if (data.country === payload.data.country) {
                                    foundCountry = true;
                                    const cumulativeAverage = (data.averageSentiment + payload.data.averageSentiment) / 2;
                                    data.averageSentiment = cumulativeAverage;
                                }

                                return data;
                            });

                            if (!foundCountry) {
                                prevState.countrySentiments.push(payload.data);
                            }

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
        return (
            <BarChart width={600} height={300} data={this.state.countrySentiments.slice()}
                margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="country" />
                <YAxis domain={[0, 5]} tickCount={6} />
                <Tooltip />
                <Legend />
                <Bar name="Average Sentiment" dataKey="averageSentiment" fill="#8884d8" maxBarSize={200} />
            </BarChart>
        );
    }
}