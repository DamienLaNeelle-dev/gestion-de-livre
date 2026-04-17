import http from 'k6/http';
import { Rate } from 'k6/metrics';

const failureRate = new Rate('failed_requests');

export function test_books() {
    const titles = ['Clean Code', 'Refactoring', 'Design Patterns', 'The Pragmatic Programmer'];
    const authors = ['Robert Martin', 'Martin Fowler', 'Gang of Four', 'Andrew Hunt'];
    const randomIndex = Math.floor(Math.random() * titles.length);

    const createRes = http.post(
        'http://localhost:8080/books',
        JSON.stringify({ title: titles[randomIndex], author: authors[randomIndex] }),
        { headers: { 'Content-Type': 'application/json' } }
    );
    failureRate.add(createRes.status !== 201);

    const getRes = http.get('http://localhost:8080/books');
    failureRate.add(getRes.status !== 200);
}