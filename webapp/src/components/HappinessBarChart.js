import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

const data = [
    {
        name: 'USA',
        averageSentiment: 4.0
    }
];

export default class HappinessBarChart extends React.Component {

    render() {
        return (
            <BarChart width={600} height={300} data={data}
                margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="averageSentiment" fill="#8884d8" />
            </BarChart>
        );
    }
}